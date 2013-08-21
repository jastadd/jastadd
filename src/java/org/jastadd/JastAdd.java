/* Copyright (c) 2005-2013, The JastAdd Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Lund University nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jastadd;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.jastadd.jrag.AST.ASTCompilationUnit;
import org.jastadd.jrag.AST.JragParser;

import org.jastadd.ast.AST.ASTDecl;
import org.jastadd.ast.AST.Ast;
import org.jastadd.ast.AST.Components;
import org.jastadd.ast.AST.Grammar;
import org.jastadd.ast.AST.TokenComponent;

/**
 * JastAdd main class.
 *
 * Parses command-line arguments and builds ASTs.
 */
public class JastAdd {

  private static final String version;
  private static final String timestamp;
  static {
    try {
      ResourceBundle resources = ResourceBundle.getBundle("Version");
      version = resources.getString("version");
      timestamp = resources.getString("timestamp");
    } catch (MissingResourceException e) {
      throw new Error("Could not open Version resource bundle");
    }
  }

  /**
   * @return Short version string
   */
  public static String getVersionString() {
    return "JastAdd2 " + version;
  }

  /**
   * @return The build timestamp
   */
  public static String getBuildTimestamp() {
    return "built at " + timestamp;
  }

  /**
   * @return Version string including link to JastAdd homepage
   */
  public static String getLongVersionString() {
    return "JastAdd2 (http://jastadd.org) version " + version;
  }

  private final JastAddConfiguration config;

  /**
   * Constructor
   * @param configuration
   */
  public JastAdd(JastAddConfiguration configuration) {
    this.config = configuration;
  }

  /**
   * Entry point. Does System.exit when finished.
   * @param args
   */
  public static void main(String[] args) {
    int exitVal = compile(args, System.out, System.err);
    if (exitVal != 0) {
      System.exit(exitVal);
    }
  }

  /**
   * Static entry point. Does not System.exit when finished.
   * @param args command-line arguments
   * @param out standard output stream
   * @param err error message output stream
   * @return exit value - 0 indicates no errors
   */
  public static int compile(String[] args, PrintStream out, PrintStream err) {
    JastAdd jastadd = new JastAdd(new JastAddConfiguration(args, err));
    return jastadd.compile(out, err);
  }

  /**
   * Non-static entry point
   * @param out standard output stream
   * @param err error message output stream
   * @return exit value - 0 indicates no errors
   */
  public int compile(PrintStream out, PrintStream err) {
    if (config.checkProblems()) {
      return 1;
    } else if (config.shouldPrintVersion()) {
      out.println(getVersionString());
      out.println(getBuildTimestamp());
      out.println("Copyright (c) 2005-2013, The JastAdd Team. All rights reserved.");
      out.println("This software is covered by the modified BSD license.");
      return 0;
    } else if (config.shouldPrintHelp()) {
      out.println(getVersionString());
      out.println(getBuildTimestamp());
      out.println();
      config.printHelp(out);
      return 0;
    } else if (config.shouldPrintNonStandardOptions()) {
      config.printNonStandardOptions(out);
      return 0;
    }

    try {
      long time = System.currentTimeMillis();

      Grammar root = config.buildRoot();
      root.genDefaultNodeTypes();

      Collection<Problem> problems = parseAstGrammars(root);
      if (checkErrors(problems, err)) {
        return 1;
      }

      long astParseTime = System.currentTimeMillis() - time;

      problems = root.problems();

      long astErrorTime = System.currentTimeMillis() - time - astParseTime;

      if (checkErrors(problems, err)) {
        return 1;
      }

      // reset weaving errors
      // TODO do we really need to do this?!
      root.problems.clear();

      genASTNode$State(root);
      
      genTracer(root);

      genIncrementalDDGNode(root);

      problems = parseJragFiles(root);
      if (checkErrors(problems, err)) {
        return 1;
      }

      long jragParseTime = System.currentTimeMillis() - time - astErrorTime;

      root.processInterfaceRefinements();
      root.weaveInterfaceIntroductions();

      weaveAspects(root);

      root.processRefinements();

      problems = readCacheFiles(root);
      if (checkErrors(problems, err)) {
        return 1;
      }

      root.weaveCollectionAttributes();

      problems = root.attributeProblems();
      problems.addAll(root.weavingProblems());

      if (checkErrors(problems, err)) {
        return 1;
      }

      root.removeDuplicateInhDecls();

      long jragErrorTime = System.currentTimeMillis() - time - jragParseTime;

      root.jastAddGen(config.getOutputDir(), root.parserName,
          config.getTargetPackage(), config.getPublicModifier());

      @SuppressWarnings("unused")
      long codegenTime = System.currentTimeMillis() - time - jragErrorTime;

      //out.println("AST parse time: " + astParseTime + ", AST error check: " + astErrorTime + ", JRAG parse time: " +
      //    jragParseTime + ", JRAG error time: " + jragErrorTime + ", Code generation: " + codegenTime);
    } catch(NullPointerException e) {
      e.printStackTrace();
      throw e;
    } catch(ArrayIndexOutOfBoundsException e) {
      e.printStackTrace();
      throw e;
    } catch(Exception e) {
      e.printStackTrace();
      return 1;
    }
    return 0;
  }

  /**
   * Print problems and check for errors
   * @param problems
   * @param err error output stream
   * @return <code>true</code> if any of the problems was an error
   */
  private boolean checkErrors(Collection<Problem> problems, PrintStream err) {
    boolean hasError = false;
    for (Problem problem: problems) {
      problem.print(err);
      hasError |= problem.isError();
    }
    return hasError;
  }

  private Collection<Problem> readCacheFiles(Grammar root) {
    Collection<Problem> problems = new LinkedList<Problem>();
    for (Iterator<String> iter = config.getCacheFiles().iterator(); iter.hasNext();) {
      String fileName = iter.next();
      //out.println("Processing cache file: " + fileName);
      try {
        FileInputStream inputStream = new FileInputStream(fileName);
        JragParser jp = new JragParser(inputStream);
        jp.inputStream = inputStream; // Hack to make input stream visible for ast-parser
        jp.root = root;
        jp.setFileName(fileName);
        jp.CacheDeclarations();
      } catch (org.jastadd.jrag.AST.ParseException e) {
        problems.add(new Problem.Error("syntax error", fileName,
            e.currentToken.next.beginLine, e.currentToken.next.beginColumn));
      } catch (FileNotFoundException e) {
        problems.add(new Problem.Error("could not find cache file '" + fileName + "'"));
      }
    }
    return problems;
  }

  private void weaveAspects(Grammar root) {
    //out.println("weaving aspect and attribute definitions");
    for (int i = 0; i < root.getNumTypeDecl(); i++) {
      if (root.getTypeDecl(i) instanceof ASTDecl) {
        ASTDecl decl = (ASTDecl)root.getTypeDecl(i);
        java.io.StringWriter writer = new java.io.StringWriter();
        decl.emitImplicitDeclarations(new PrintWriter(writer));

        org.jastadd.jrag.AST.JragParser jp = new org.jastadd.jrag.AST.JragParser(new java.io.StringReader(writer.toString()));
        jp.root = root;
        jp.setFileName("");
        jp.className = "ASTNode";
        jp.pushTopLevelOrAspect(true);
        try {
          while(true)
            jp.AspectBodyDeclaration();
        } catch (Exception e) {
          // TODO: handle error?
          // String s = e.getMessage();
        }
        jp.popTopLevelOrAspect();

        int j = 0;
        for (Iterator<?> iter = decl.getComponents(); iter.hasNext();) {
          Components c = (Components) iter.next();
          if (c instanceof TokenComponent) {
            c.jaddGen(j, config.getPublicModifier(), decl);
          } else {
            c.jaddGen(j, config.getPublicModifier(), decl);
            j++;
          }
        }
      }
    }
  }

  private Collection<Problem> parseJragFiles(Grammar root) {
    Collection<Problem> problems = new LinkedList<Problem>();
    for (Iterator<String> iter = config.getFiles().iterator(); iter.hasNext();) {
      String fileName = iter.next();
      if (fileName.endsWith(".jrag") || fileName.endsWith(".jadd")) {
        try {
          FileInputStream inputStream = new FileInputStream(fileName);
          JragParser jp = new JragParser(inputStream);
          jp.inputStream = inputStream; // Hack to make input stream visible for ast-parser
          jp.root = root;
          jp.setFileName(fileName);
          ASTCompilationUnit au = jp.CompilationUnit();
          root.addCompUnit(au);
        } catch (org.jastadd.jrag.AST.ParseException e) {
          problems.add(new Problem.Error("syntax error", fileName,
              e.currentToken.next.beginLine, e.currentToken.next.beginColumn));
        } catch (FileNotFoundException e) {
          problems.add(new Problem.Error("could not find aspect file '" + fileName + "'"));
        } catch (Throwable e) {
          problems.add(new Problem.Error("exception occurred while parsing", fileName));
        }
      }
    }
    return problems;
  }

  private void genTracer(Grammar root) {
    java.io.StringWriter writer = new java.io.StringWriter();
    root.emitTracer(new PrintWriter(writer));
    org.jastadd.jrag.AST.JragParser jp = new org.jastadd.jrag.AST.JragParser(
        new java.io.StringReader(writer.toString()));
    jp.root = root;
    jp.setFileName("ASTNode");
    jp.className = "ASTNode";
    jp.pushTopLevelOrAspect(true);
    try {
      while(true)
        jp.AspectBodyDeclaration();
    } catch (Exception e) {
      // TODO: handle error?
      // String s = e.getMessage();
    }
    jp.popTopLevelOrAspect();
  }
  
  private void genIncrementalDDGNode(Grammar root) {
    if (root.incremental) {
      java.io.StringWriter writer = new java.io.StringWriter();
      root.genIncrementalDDGNode(new PrintWriter(writer));
      org.jastadd.jrag.AST.JragParser jp = new org.jastadd.jrag.AST.JragParser(
          new java.io.StringReader(writer.toString()));
      jp.root = root;
      jp.setFileName("ASTNode");
      jp.className = "ASTNode";
      jp.pushTopLevelOrAspect(true);
      try {
        while(true)
          jp.AspectBodyDeclaration();
      } catch (Exception e) {
        // TODO: handle error?
        // String s = e.getMessage();
      }
      jp.popTopLevelOrAspect();
    }
  }

  private void genASTNode$State(Grammar root) {
    java.io.StringWriter writer = new java.io.StringWriter();
    root.emitASTNode$State(new PrintWriter(writer));

    org.jastadd.jrag.AST.JragParser jp = new org.jastadd.jrag.AST.JragParser(new java.io.StringReader(writer.toString()));
    jp.root = root;
    jp.setFileName("ASTNode");
    jp.className = "ASTNode";
    jp.pushTopLevelOrAspect(true);
    try {
      while(true)
        jp.AspectBodyDeclaration();
    } catch (Exception e) {
      // TODO: handle error?
      // String s = e.getMessage();
    }
    jp.popTopLevelOrAspect();
  }

  private Collection<Problem> parseAstGrammars(Grammar root) {
    Collection<Problem> problems = new LinkedList<Problem>();
    //out.println("parsing grammars");
    for (Iterator<String> iter = config.getFiles().iterator(); iter.hasNext();) {
      String fileName = iter.next();
      if(fileName.endsWith(".ast")) {
        try {
          Ast parser = new Ast(new FileInputStream(fileName));
          parser.fileName = fileName;
          Grammar g = parser.Grammar();
          for(int i = 0; i < g.getNumTypeDecl(); i++) {
            root.addTypeDecl(g.getTypeDecl(i));
          }

          // EMMA_2011-12-12: Adding region declarations for incremental evaluation
          for (int i = 0; i < g.getNumRegionDecl(); i++) {
            root.addRegionDecl(g.getRegionDecl(i));
          }
          //

          problems.addAll(parser.parseProblems());

        } catch (org.jastadd.ast.AST.TokenMgrError e) {
          problems.add(new Problem.Error(e.getMessage(), fileName));
        } catch (org.jastadd.ast.AST.ParseException e) {
          // Exceptions actually caught by error recovery in parser
        } catch (FileNotFoundException e) {
          problems.add(new Problem.Error("could not find abstract syntax file '" + fileName + "'"));
        }
      }
    }
    return problems;
  }

  @SuppressWarnings("unused")
  private void checkMem(PrintStream out) {
    Runtime runtime = Runtime.getRuntime();
    long total = runtime.totalMemory();
    long free = runtime.freeMemory();
    long use = total-free;
    out.println("Before GC: Total " + total + ", use " + use);
    runtime.gc();
    total = runtime.totalMemory();
    free = runtime.freeMemory();
    use = total-free;
    out.println("After GC: Total " + total + ", use " + use);
  }
}
