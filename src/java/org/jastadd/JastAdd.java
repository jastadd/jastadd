/* Copyright (c) 2005-2015, The JastAdd Team
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
import org.jastadd.ast.AST.Component;
import org.jastadd.ast.AST.Grammar;
import org.jastadd.ast.AST.InterTypeObject;
import org.jastadd.ast.AST.SynDecl;
import org.jastadd.ast.AST.SynEq;
import org.jastadd.ast.AST.SynthesizedNta;
import org.jastadd.ast.AST.TokenComponent;
import org.jastadd.ast.AST.TypeDecl;

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
   * Entry point. Calls System.exit when finished if something went wrong.
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
    if (config.shouldPrintVersion()) {
      out.println(getVersionString());
      out.println(getBuildTimestamp());
      out.println("Copyright (c) 2005-2015, The JastAdd Team. All rights reserved.");
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
    } else if (config.checkProblems(err)) {
      out.println("Run JastAdd2 again with the --help option to view help.");
      return 1;
    }

    try {
      Grammar grammar = config.buildRoot();

      if (config.generateImplicits()) {
        grammar.addImplicitNodeTypes();
      }

      if (checkErrors(readAstFiles(grammar, config.getFiles()), err)) {
        return 1;
      }

      if (config.shouldGenerateDotGraph()) {
        // Generate Dot graph for the grammar, then exit.
        grammar.genDotGraph(out);
        return 0;
      }

      if (checkErrors("State class generation", genStateClass(grammar), err)) {
        return 1;
      }
      if (checkErrors("incremental DDG node generation", genIncrementalDDGNode(grammar), err)) {
        return 1;
      }

      if (checkErrors("JRAG parsing", readJragFiles(grammar, config.getFiles()), err)) {
        return 1;
      }

      if (checkErrors("attribute weaving", weaveAttributes(grammar), err)) {
        return 1;
      }

      if (checkErrors("reading cache files", readCacheFiles(grammar), err)) {
        return 1;
      }

      if (config.cacheAnalyzeEnabled()) {
        genCacheAnalyzer(grammar);
      }

      grammar.processInterfaceRefinements();
      grammar.weaveInterfaceIntroductions();

      if (checkErrors("weaveAspects", weaveAspects(grammar), err)) {
        return 1;
      }

      if (checkErrors("inter-type object weaving", weaveInterTypeObjects(grammar), err)) {
        return 1;
      }

      if (checkErrors("attribute weaving", weaveAttributes(grammar), err)) {
        return 1;
      }

      if (checkErrors("attribute weaving", addNtaComponents(grammar), err)) {
        return 1;
      }

      if (checkErrors(grammar.problems(), err)) {
        return 1;
      }

      if (checkErrors(grammar.attributeProblems(), err)) {
        return 1;
      }

      grammar.processRefinements();

      grammar.weaveCollectionAttributes();

      if (checkErrors(grammar.weavingProblems(), err)) {
        return 1;
      }

      grammar.removeDuplicateInhDecls();

      grammar.jastAddGen(config.getPublicModifier());

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
    return checkErrors("",  problems, err);
  }

  /**
   * Print problems with a description and check for errors
   * @param description
   * @param problems
   * @param err error output stream
   * @return <code>true</code> if any of the problems was an error
   */
  private boolean checkErrors(String description, Collection<Problem> problems, PrintStream err) {
    boolean hasError = false;
    boolean first = true;
    for (Problem problem: problems) {
      if (first && !description.isEmpty()) {
        err.println("Problems during " + description + ":");
      }
      first = false;
      problem.print(err);
      hasError |= problem.isError();
    }
    return hasError;
  }

  private static Collection<Problem> readAstFiles(Grammar grammar, Collection<String> fileNames) {
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

  private static Collection<Problem> readJragFiles(Grammar grammar, Collection<String> fileNames) {
    Collection<Problem> problems = new LinkedList<Problem>();
    for (Iterator<String> iter = fileNames.iterator(); iter.hasNext();) {
      String fileName = iter.next();
      if (fileName.endsWith(".jrag") || fileName.endsWith(".jadd")) {
        File source = new File(fileName);
        JastAddUtil.parseJRAGSpec(source, grammar, problems);
      }
    }
    problems.addAll(weaveInterTypeObjects(grammar));
    return problems;
  }

  @SuppressWarnings("unchecked")
  protected static Collection<Problem> weaveInterTypeObjects(Grammar grammar) {
    Collection<Problem> problems = new LinkedList<Problem>();
    for (InterTypeObject object: grammar.interTypeObjects) {
      TypeDecl clazz = grammar.lookup(object.className);
      if (clazz != null) {
        clazz.classBodyDecls.add(object.classBodyObject);
      } else {
        problems.add(new Problem.Error("can not add member to unknown class " + object.className,
            object.classBodyObject.fileName, object.classBodyObject.line));
      }
    }
    grammar.interTypeObjects.clear();// reset to avoid double weaving (TODO remove this)
    return problems;
  }

  protected static Collection<Problem> weaveAttributes(Grammar grammar) {
    Collection<Problem> problems = new LinkedList<Problem>();
    for (SynDecl decl: grammar.synDecls) {
      String className = decl.hostName;
      TypeDecl clazz = grammar.lookup(className);
      if (clazz != null) {
        clazz.addSynDecl(decl);
      } else {
        problems.add(decl.errorf("can not add synthesized attribute %s %s to unknown class %s",
            decl.getType(), decl.getName(), className));
      }
    }
    grammar.synDecls.clear();

    for (SynEq equ: grammar.synEqs) {
      String className = equ.hostName;
      TypeDecl clazz = grammar.lookup(className);
      if (clazz != null) {
        clazz.addSynEq(equ);
      } else {
        problems.add(
            equ.errorf("can not add equation for synthesized attribute %s to unknown class %s",
                equ.getName(), className));
      }
    }
    grammar.synEqs.clear();
    return problems;
  }

  /** Add synthesized NTAs as components in their host types. */
  private Collection<Problem> addNtaComponents(Grammar grammar) {
    Collection<Problem> problems = new LinkedList<Problem>();
    for (TypeDecl typeDecl : grammar.getTypeDecls()) {
      for (SynDecl synDecl : typeDecl.getSynDecls()) {
        if (synDecl.getNTA()) {
          ((ASTDecl) grammar.lookup(synDecl.hostName))
              .addSynthesizedNta(new SynthesizedNta(synDecl.getName(), synDecl.getType()));
        }
      }
    }
    return problems;
  }

  private Collection<Problem> readCacheFiles(Grammar grammar) {
    Collection<Problem> problems = new LinkedList<Problem>();
    if (!(config.cacheConfig() || config.cacheImplicit())) {
      return problems;
    }
    for (Iterator<String> iter = config.getCacheFiles().iterator(); iter.hasNext();) {
      String fileName = iter.next();
      File cacheFile = new File(fileName);
      JastAddUtil.parseCacheDeclarations(cacheFile, grammar, problems);
    }
    return problems;
  }

  /**
   * Inject aspect and attribute definitions.
   */
  private Collection<Problem> weaveAspects(Grammar grammar) {
    Collection<Problem> allProblems = new LinkedList<Problem>();
    for (int i = 0; i < grammar.getNumTypeDecl(); i++) {
      if (grammar.getTypeDecl(i) instanceof ASTDecl) {
        ASTDecl decl = (ASTDecl) grammar.getTypeDecl(i);
        java.io.StringWriter writer = new java.io.StringWriter();
        decl.emitImplicitDeclarations(new PrintWriter(writer));

        Collection<Problem> problems = JastAddUtil.parseAspectBodyDeclarations(
            new java.io.StringReader(writer.toString()), config.astNodeType(), grammar);
        allProblems.addAll(problems);

        int j = 0;
        for (Component c : decl.components()) {
          if (c instanceof TokenComponent) {
            c.jaddGen(j, config.getPublicModifier(), decl);
          } else {
            c.jaddGen(j, config.getPublicModifier(), decl);
            j++;
          }
        }
      }
    }
    return allProblems;
  }

  private void genCacheAnalyzer(Grammar grammar)
      throws FileNotFoundException {
    grammar.createPackageOutputDirectory();
    PrintWriter writer = new PrintWriter(grammar.targetJavaFile("CacheAnalyzer"));
    grammar.emitCacheAnalyzer(writer);
    writer.close();
  }

  private Collection<Problem> genIncrementalDDGNode(Grammar grammar) {
    if (config.incremental()) {
      java.io.StringWriter writer = new java.io.StringWriter();
      grammar.genIncrementalDDGNode(new PrintWriter(writer));
      return JastAddUtil.parseAspectBodyDeclarations(writer.toString(),
          config.astNodeType(), grammar);
    }
    return new LinkedList<Problem>();
  }

  private Collection<Problem> genStateClass(Grammar grammar) {
    java.io.StringWriter writer = new java.io.StringWriter();
    grammar.emitStateClass(new PrintWriter(writer));
    return JastAddUtil.parseAspectBodyDeclarations(writer.toString(),
        config.stateClassName(), grammar);
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
