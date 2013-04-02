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
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import jrag.AST.ASTCompilationUnit;
import jrag.AST.JragParser;

import ast.AST.ASTDecl;
import ast.AST.ASTNode;
import ast.AST.Ast;
import ast.AST.Components;
import ast.AST.Grammar;
import ast.AST.TokenComponent;

/**
 * JastAdd main class.
 *
 * Parses command-line arguments and builds ASTs.
 */
public class JastAdd {

  private static ResourceBundle resources = null;
  private static String RESOURCE_NAME = "JastAdd";
  private static String getString(String key) {
    if (resources == null) {
      try {
        resources = ResourceBundle.getBundle(RESOURCE_NAME);
      } catch (MissingResourceException e) {
        throw new Error("Could not open the resource " + RESOURCE_NAME);
      }
    }
    return resources.getString(key);
  }

  /**
   * @return Short version string
   */
  public static String getVersionString() {
    return "JastAdd2 " + getString("jastadd.version");
  }

  /**
   * @return Version string including link to JastAdd homepage
   */
  public static String getLongVersionString() {
    return "JastAdd2 (http://jastadd.org) version " +
      getString("jastadd.version");
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
   * Entry point
   * @param args
   */
  public static void main(String[] args) {
    JastAdd jastadd = new JastAdd(new JastAddConfiguration(args));
    int exitVal = jastadd.compile();
    if (exitVal != 0) {
      System.exit(exitVal);
    }
  }

  /**
   * Non-static entry point
   * @return Exit value - 0 indicates no errors
   */
  public int compile() {
    if (config.shouldPrintVersion()) {
      System.out.println(getVersionString());
      System.out.println("Copyright (c) 2005-2013, The JastAdd Team. All rights reserved.");
      System.out.println("This software is covered by the modified BSD license.");
      return 0;
    } else if (config.shouldPrintHelp()) {
      System.out.println(getVersionString());
      System.out.println();
      config.printHelp();
      return 0;
    } else if (config.shouldPrintNonStandardOptions()) {
      config.printNonStandardOptions();
      return 0;
    } else if (config.checkProblems()) {
      return 1;
    }

    try {
      long time = System.currentTimeMillis();

      Grammar root = config.buildRoot();
      root.genDefaultNodeTypes();

      Collection<Problem> problems = parseAstGrammars(root);
      boolean hasError = false;
      for (Problem problem: problems) {
        problem.print(System.err);
        if (problem.isError()) {
          hasError = true;
        }
      }
      if (hasError) {
        return 1;
      }

      long astParseTime = System.currentTimeMillis() - time;

      String astErrors = root.astErrors();

      long astErrorTime = System.currentTimeMillis() - time - astParseTime;

      if(!astErrors.equals("")) {
        System.err.println("Semantic error:");
        System.err.println(astErrors);
        return 1;
      }

      ASTNode.resetGlobalErrors();

      genASTNode$State(root);

      genIncrementalDDGNode(root);

      int retVal = parseJragFiles(root);
      if (retVal != 0) {
        return retVal;
      }

      long jragParseTime = System.currentTimeMillis() - time - astErrorTime;

      root.processInterfaceRefinements();
      root.weaveInterfaceIntroductions();

      weaveAspects(root);

      root.processRefinements();

      retVal = readCacheFiles(root);
      if (retVal != 0) {
        return retVal;
      }

      root.weaveCollectionAttributes();

      String err = root.errors();
      if(!err.equals("") || !ASTNode.globalErrors.equals("")) {
        System.err.println("Semantic errors: \n" + err + ASTNode.globalErrors);
        return 1;
      }

      long jragErrorTime = System.currentTimeMillis() - time - jragParseTime;

      root.jastAddGen(config.getOutputDir(), root.parserName,
          config.getTargetPackage(), config.getPublicModifier());

      @SuppressWarnings("unused")
      long codegenTime = System.currentTimeMillis() - time - jragErrorTime;

      //System.out.println("AST parse time: " + astParseTime + ", AST error check: " + astErrorTime + ", JRAG parse time: " +
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

  private int readCacheFiles(Grammar root) {
    for (Iterator<String> iter = config.getCacheFiles().iterator(); iter.hasNext();) {
      String fileName = iter.next();
      //System.out.println("Processing cache file: " + fileName);
      try {
        FileInputStream inputStream = new FileInputStream(fileName);
        JragParser jp = new JragParser(inputStream);
        jp.inputStream = inputStream; // Hack to make input stream visible for ast-parser
        jp.root = root;
        jp.setFileName(fileName);
        jp.CacheDeclarations();
      } catch (jrag.AST.ParseException e) {
        StringBuffer msg = new StringBuffer();
        msg.append("Syntax error in " + fileName + " at line " + e.currentToken.next.beginLine + ", column " +
            e.currentToken.next.beginColumn);
        System.err.println(msg.toString());
        return 1;
      } catch (FileNotFoundException e) {
        System.err.println("File error: Aspect file " + fileName + " not found");
        return 1;
      }
    }
    return 0;
  }

  private void weaveAspects(Grammar root) {
    //System.out.println("weaving aspect and attribute definitions");
    for(int i = 0; i < root.getNumTypeDecl(); i++) {
      if(root.getTypeDecl(i) instanceof ASTDecl) {
        ASTDecl decl = (ASTDecl)root.getTypeDecl(i);
        java.io.StringWriter writer = new java.io.StringWriter();
        decl.emitImplicitDeclarations(new PrintWriter(writer));

        jrag.AST.JragParser jp = new jrag.AST.JragParser(new java.io.StringReader(writer.toString()));
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

  private int parseJragFiles(Grammar root) {
    for (Iterator<String> iter = config.getFiles().iterator(); iter.hasNext();) {
      String fileName = iter.next();
      if(fileName.endsWith(".jrag") || fileName.endsWith(".jadd")) {
        try {
          FileInputStream inputStream = new FileInputStream(fileName);
          JragParser jp = new JragParser(inputStream);
          jp.inputStream = inputStream; // Hack to make input stream visible for ast-parser
          jp.root = root;
          jp.setFileName(fileName);
          ASTCompilationUnit au = jp.CompilationUnit();
          root.addCompUnit(au);
        } catch (jrag.AST.ParseException e) {
          StringBuffer msg = new StringBuffer();
          msg.append("Syntax error in " + fileName + " at line " + e.currentToken.next.beginLine + ", column " +
              e.currentToken.next.beginColumn);
          System.err.println(msg.toString());
          return 1;
        } catch (FileNotFoundException e) {
          System.err.println("File error: Aspect file " + fileName + " not found");
          return 1;
        } catch (Throwable e) {
          System.err.println("Exception occurred while parsing " + fileName);
          e.printStackTrace();
        }
      }
    }
    return 0;
  }

  private void genIncrementalDDGNode(Grammar root) {
    if (root.incremental) {
      java.io.StringWriter writer = new java.io.StringWriter();
      root.genIncrementalDDGNode(new PrintWriter(writer));
      jrag.AST.JragParser jp = new jrag.AST.JragParser(
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

    jrag.AST.JragParser jp = new jrag.AST.JragParser(new java.io.StringReader(writer.toString()));
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
    //System.out.println("parsing grammars");
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

        } catch (ast.AST.TokenMgrError e) {
          problems.add(new Problem.Error(e.getMessage(), fileName));
        } catch (ast.AST.ParseException e) {
          // Exceptions actually caught by error recovery in parser
        } catch (FileNotFoundException e) {
          problems.add(new Problem.Error("could not find the abstract syntax file '" + fileName + "'"));
        }
      }
    }
    return problems;
  }

  @SuppressWarnings("unused")
  private void checkMem() {
    Runtime runtime = Runtime.getRuntime();
    long total = runtime.totalMemory();
    long free = runtime.freeMemory();
    long use = total-free;
    System.out.println("Before GC: Total " + total + ", use " + use);
    runtime.gc();
    total = runtime.totalMemory();
    free = runtime.freeMemory();
    use = total-free;
    System.out.println("After GC: Total " + total + ", use " + use);
  }
}
