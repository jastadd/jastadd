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

import java.io.File;
import java.io.FileNotFoundException;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.jastadd.ast.AST.ASTDecl;
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
      throw new Error("Could load version info: " + e.getMessage());
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

  private final Configuration config;

  /**
   * Constructor
   * @param configuration
   */
  public JastAdd(Configuration configuration) {
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
    JastAdd jastadd = new JastAdd(new Configuration(args, err));
    return jastadd.compile(out, err);
  }

  /**
   * Non-static entry point
   * @param out standard output stream
   * @param err error message output stream
   * @return exit value - 0 indicates no errors
   */
  public int compile(PrintStream out, PrintStream err) {
    if (config.checkProblems(err)) {
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

      Grammar grammar = config.buildRoot();
      grammar.genDefaultNodeTypes();

      Collection<Problem> problems = readASTFiles(grammar, config.getFiles());
      if (checkErrors(problems, err)) {
        return 1;
      }

      long astParseTime = System.currentTimeMillis() - time;

      problems = grammar.problems();

      long astErrorTime = System.currentTimeMillis() - time - astParseTime;

      if (checkErrors(problems, err)) {
        return 1;
      }

      // reset weaving errors
      // TODO do we really need to do this?!
      grammar.problems.clear();

      genASTNode$State(grammar);

      if (config.tracingEnabled()) {
        genTracer(grammar);
      }

      genIncrementalDDGNode(grammar);

      problems = readJRAGFiles(grammar, config.getFiles());
      if (checkErrors(problems, err)) {
        return 1;
      }

      long jragParseTime = System.currentTimeMillis() - time - astErrorTime;

      problems = readCacheFiles(grammar);
      if (checkErrors(problems, err)) {
        return 1;
      }

      if (config.cacheAnalyzeEnabled()) {
        genCacheAnalyzer(grammar);
      }

      grammar.processInterfaceRefinements();
      grammar.weaveInterfaceIntroductions();

      weaveAspects(grammar);

      grammar.processRefinements();


      grammar.weaveCollectionAttributes();

      problems = grammar.attributeProblems();
      problems.addAll(grammar.weavingProblems());

      if (checkErrors(problems, err)) {
        return 1;
      }

      grammar.removeDuplicateInhDecls();

      long jragErrorTime = System.currentTimeMillis() - time - jragParseTime;

      grammar.jastAddGen(config.getPublicModifier());

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

  private static Collection<Problem> readASTFiles(Grammar grammar, Collection<String> fileNames) {
    Collection<Problem> problems = new LinkedList<Problem>();
    // out.println("parsing grammars");
    for (Iterator<String> iter = fileNames.iterator(); iter.hasNext();) {
      String fileName = iter.next();
      if (fileName.endsWith(".ast")) {
        File source = new File(fileName);
        JastAddUtil.parseASTSpec(source, grammar, problems);
      }
    }
    return problems;
  }

  private static Collection<Problem> readJRAGFiles(Grammar grammar, Collection<String> fileNames) {
    Collection<Problem> problems = new LinkedList<Problem>();
    for (Iterator<String> iter = fileNames.iterator(); iter.hasNext();) {
      String fileName = iter.next();
      if (fileName.endsWith(".jrag") || fileName.endsWith(".jadd")) {
        File source = new File(fileName);
        JastAddUtil.parseJRAGSpec(source, grammar, problems);
      }
    }
    return problems;
  }

  private Collection<Problem> readCacheFiles(Grammar grammar) {
    Collection<Problem> problems = new LinkedList<Problem>();
    if (!(grammar.config().cacheConfig || grammar.config().cacheImplicit)) {
      return problems;
    }
    for (Iterator<String> iter = config.getCacheFiles().iterator(); iter.hasNext();) {
      String fileName = iter.next();
      File cacheFile = new File(fileName);
      JastAddUtil.parseCacheDeclarations(cacheFile, grammar, problems);
    }
    return problems;
  }

  private void weaveAspects(Grammar grammar) {
    //out.println("weaving aspect and attribute definitions");
    for (int i = 0; i < grammar.getNumTypeDecl(); i++) {
      if (grammar.getTypeDecl(i) instanceof ASTDecl) {
        ASTDecl decl = (ASTDecl)grammar.getTypeDecl(i);
        java.io.StringWriter writer = new java.io.StringWriter();
        decl.emitImplicitDeclarations(new PrintWriter(writer));

        JastAddUtil.parseAspectBodyDeclarations(new java.io.StringReader(writer.toString()),
            grammar.config().astNodeType, grammar);

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

  private void genTracer(Grammar grammar)
    throws FileNotFoundException {

    grammar.createPackageOutputDirectory();

    PrintWriter writer = new PrintWriter(grammar.targetJavaFile("Tracer"));
    grammar.emitTracer(writer);
    writer.close();
  }

  private void genCacheAnalyzer(Grammar grammar)
    throws FileNotFoundException {

    grammar.createPackageOutputDirectory();

    PrintWriter writer = new PrintWriter(grammar.targetJavaFile("CacheAnalyzer"));
    grammar.emitCacheAnalyzer(writer);
    writer.close();
  }

  private void genIncrementalDDGNode(Grammar grammar) {
    if (grammar.config().incremental) {
      java.io.StringWriter writer = new java.io.StringWriter();
      grammar.genIncrementalDDGNode(new PrintWriter(writer));
      JastAddUtil.parseAspectBodyDeclarations(writer.toString(),
          grammar.config().astNodeType, grammar);
    }
  }

  private void genASTNode$State(Grammar grammar) {
    java.io.StringWriter writer = new java.io.StringWriter();
    grammar.emitASTNode$State(new PrintWriter(writer));
    JastAddUtil.parseAspectBodyDeclarations(writer.toString(),
        grammar.config().astNodeType, grammar);
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
