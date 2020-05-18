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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Scanner;

import org.jastadd.ast.AST.ASTDecl;
import org.jastadd.ast.AST.Ast;
import org.jastadd.ast.AST.CacheDecl;
import org.jastadd.ast.AST.Component;
import org.jastadd.ast.AST.Grammar;
import org.jastadd.ast.AST.InhDecl;
import org.jastadd.ast.AST.InhEq;
import org.jastadd.ast.AST.List;
import org.jastadd.ast.AST.SynDecl;
import org.jastadd.ast.AST.SynEq;
import org.jastadd.ast.AST.TokenComponent;
import org.jastadd.ast.AST.TypeDecl;
import org.jastadd.jrag.ClassBodyObject;
import org.jastadd.jrag.AST.ASTCompilationUnit;
import org.jastadd.jrag.AST.JragParser;
import org.jastadd.jrag.AST.TokenMgrError;

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

  public JastAdd(Configuration configuration) {
    this.config = configuration;
  }

  /**
   * Calls System.exit when finished if something went wrong.
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
      out.println("Copyright (c) 2005-2020, The JastAdd Team. All rights reserved.");
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

      if (checkErrors("cache configuration", applyCacheConfiguration(grammar), err)) {
        return 1;
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

      if (config.shouldWriteStatistics()) {
        grammar.writeStatistics(config.statisticsFile());
      }

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
   * Print problems and check for errors.
   * @param err error output stream
   * @return {@code true} if any of the problems was an error
   */
  private boolean checkErrors(Collection<Problem> problems, PrintStream err) {
    return checkErrors("",  problems, err);
  }

  /**
   * Print problems with a description and check for errors.
   * @param err error output stream
   * @return {@code true} if any of the problems was an error
   */
  private boolean checkErrors(String description, Collection<Problem> problems, PrintStream err) {
    boolean hasError = false;
    boolean first = true;
    for (Problem problem : problems) {
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
    for (String fileName : fileNames) {
      if (fileName.endsWith(".ast")) {
        File source = new File(fileName);
        parseAbstractGrammar(source, grammar, problems);
      }
    }
    return problems;
  }

  private static Collection<Problem> readJragFiles(Grammar grammar, Collection<String> fileNames) {
    Collection<Problem> problems = new LinkedList<Problem>();
    for (String fileName : fileNames) {
      if (fileName.endsWith(".jrag") || fileName.endsWith(".jadd")) {
        File source = new File(fileName);
        parseAspect(source, grammar, problems);
      }
    }
    problems.addAll(weaveInterTypeObjects(grammar));
    return problems;
  }

  protected static Collection<Problem> weaveInterTypeObjects(Grammar grammar) {
    Collection<Problem> problems = new LinkedList<Problem>();
    for (Map.Entry<String, Collection<ClassBodyObject>> entry : grammar.interTypeDecls.entrySet()) {
      String className = entry.getKey();
      TypeDecl clazz = grammar.lookup(className);
      for (ClassBodyObject decl : entry.getValue()) {
        if (clazz != null) {
          clazz.classBodyDecls.add(decl);
        } else {
          problems.add(Problem.builder()
              .message("can not add member to unknown class %s", className)
              .sourceFile(decl.fileName)
              .sourceLine(decl.line)
              .buildError());
        }
      }
    }
    // TODO(jesper): Remove the below line.
    grammar.interTypeDecls.clear(); // Reset to avoid double weaving.
    return problems;
  }

  protected static Collection<Problem> weaveAttributes(Grammar grammar) {
    Collection<Problem> problems = new LinkedList<Problem>();
    for (SynDecl decl : grammar.synDecls) {
      String className = decl.hostName;
      TypeDecl clazz = grammar.lookup(className);
      if (clazz == null) {
        problems.add(decl.errorf(
            "can not add synthesized attribute %s %s to unknown class %s",
            decl.getType(), decl.getName(), className));
      }
    }
    for (SynEq equ : grammar.synEqs) {
      String className = equ.hostName;
      TypeDecl clazz = grammar.lookup(className);
      if (clazz != null) {
        clazz.addSynEq(equ);
      } else {
        problems.add(equ.errorf(
            "can not add equation for synthesized attribute %s to unknown class %s",
            equ.getName(), className));
      }
    }
    grammar.synEqs.clear();
    for (InhDecl decl : grammar.inhDecls) {
      String className = decl.hostName;
      TypeDecl clazz = grammar.lookup(className);
      if (clazz != null) {
        clazz.addInhDecl(decl);
      } else {
        problems.add(decl.errorf(
            "can not add inherited attribute %s %s to unknown class %s",
            decl.getType(), decl.getName(), className));
      }
    }
    grammar.inhDecls.clear();
    for (InhEq equ : grammar.inhEqs) {
      String className = equ.hostName;
      TypeDecl clazz = grammar.lookup(className);
      if (clazz != null) {
        clazz.addInhEq(equ);
      } else {
        problems.add(equ.errorf(
            "can not add equation for inhertied attribute %s to unknown class %s",
            equ.getName(), className));
      }
    }
    grammar.inhEqs.clear();
    return problems;
  }

  protected static Collection<Problem> applyCacheConfiguration(Grammar grammar) {
    Collection<Problem> problems = new LinkedList<Problem>();
    for (int i = 0; i < grammar.cacheDecls.size(); ++i) {
      CacheDecl decl = grammar.cacheDecls.get(i);
      for (int j = 0; j < i; ++j) {
        CacheDecl prev = grammar.cacheDecls.get(j);
        if (decl.signature.equals(prev.signature) && !decl.mode.equals(prev.mode)) {
          problems.add(Problem.builder()
              .message("conflicting cache declaration for attribute %s.%s, "
                  + "previous declaration is at %s:%d", decl.hostName, decl.attrName,
                  prev.fileName, prev.startLine)
              .sourceFile(decl.fileName)
              .sourceLine(decl.startLine)
              .buildError());
          break;
        }
      }
      grammar.applyCacheMode(decl, problems);
    }
    grammar.cacheDecls.clear();
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
        StringWriter writer = new StringWriter();
        decl.emitImplicitDeclarations(new PrintWriter(writer));

        Collection<Problem> problems = parseAspectBodyDeclarations(
            writer.toString(), config.astNodeType(), grammar);
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

  private Collection<Problem> genIncrementalDDGNode(Grammar grammar) {
    if (config.incremental()) {
      StringWriter writer = new StringWriter();
      grammar.genIncrementalDDGNode(new PrintWriter(writer));
      return parseAspectBodyDeclarations(writer.toString(), config.astNodeType(), grammar);
    }
    return new LinkedList<Problem>();
  }

  private Collection<Problem> genStateClass(Grammar grammar) {
    StringWriter writer = new StringWriter();
    grammar.emitStateClass(new PrintWriter(writer));
    return parseAspectBodyDeclarations(writer.toString(), config.stateClassName(), grammar);
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

  /**
   * Parses a given AST specification in a given File and adds its content to a
   * given grammar object.
   *
   * @param source the file based source
   * @param problems a collection where problems should be added for later error handling
   * @param grammar the grammar object where the AST specifications contents should be added
   */
  protected static void parseAbstractGrammar(File source, Grammar grammar,
      Collection<Problem> problems) {
    Reader inStream = null;
    String fileName = source.getPath();
    try {
      inStream = new FileReader(source);
      Ast parser = new Ast(inStream);
      parser.fileName = fileName;
      Grammar astGrammar = parser.Grammar();
      problems.addAll(parser.parseProblems());
      if (astGrammar != null) {
        List<TypeDecl> typeDecls = astGrammar.getTypeDeclListNoTransform();
        for (int i = 0; i < typeDecls.getNumChildNoTransform(); i++) {
          grammar.addTypeDecl(typeDecls.getChildNoTransform(i));
        }
        for (int i = 0; i < astGrammar.getNumRegionDecl(); i++) {
          grammar.addRegionDecl(astGrammar.getRegionDecl(i));
        }
      }
    } catch (org.jastadd.ast.AST.TokenMgrError e) {
      problems.add(Problem.builder()
          .message(e.getMessage())
          .sourceFile(fileName)
          .buildError());
    } catch (org.jastadd.ast.AST.ParseException e) {
      int startLine = e.currentToken.next.beginLine;
      int startColumn = e.currentToken.next.beginColumn;
      int endColumn = e.currentToken.next.endColumn;
      String offendingToken = e.currentToken.next.image;
      String context = syntaxErrorContext(fileName, startLine, startColumn, endColumn);
      problems.add(Problem.builder()
          .message("unexpected token \"%s\":\n%s", offendingToken, context)
          .sourceFile(fileName)
          .sourceLine(startLine)
          .sourceColumn(startColumn)
          .buildError());
    } catch (FileNotFoundException e) {
      problems.add(Problem.builder()
          .message("could not find abstract syntax file '%s'", fileName)
          .buildError());
    } finally {
      if (inStream != null)
        try {
          inStream.close();
        } catch (IOException e) {
          // Failed to close input. Not a problem.
        }
    }
  }

  /**
   * Parses a JRAG or JADD specification from a given file and adds it to the given grammar object.
   *
   * @param source the file-based source
   * @param grammar the grammar where the parsed attributes/inter-type declarations should be added
   * @param problems the collection where new objects can be added
   *
   */
  protected static void parseAspect(File source, Grammar grammar, Collection<Problem> problems) {
    Reader inStream = null;
    String fileName = source.getPath();
    try {
      inStream = new FileReader(source);
      JragParser parser = new JragParser(inStream);
      parser.setFileName(fileName);
      parser.root = grammar;
      ASTCompilationUnit cu = parser.CompilationUnit();
      grammar.addCompUnit(cu);
    } catch (org.jastadd.jrag.AST.ParseException e) {
      int startLine = e.currentToken.next.beginLine;
      int startColumn = e.currentToken.next.beginColumn;
      int endColumn = e.currentToken.next.endColumn;
      String offendingToken = e.currentToken.next.image;
      String context = syntaxErrorContext(fileName, startLine, startColumn, endColumn);
      problems.add(Problem.builder()
          .message("unexpected token \"%s\":\n%s", offendingToken, context)
          .sourceFile(fileName)
          .sourceLine(startLine)
          .sourceColumn(startColumn)
          .buildError());
    } catch (TokenMgrError e) {
      problems.add(Problem.builder()
          .message(e.getMessage())
          .sourceFile(fileName)
          .buildError());
    } catch (FileNotFoundException e) {
      problems.add(Problem.builder()
          .message("could not find aspect file '%s'", fileName)
          .buildError());
    } finally {
      if (inStream != null) {
        try {
          inStream.close();
        } catch (IOException e) {
          // Failed to close input. Not a problem.
        }
      }
    }
  }

  /**
   * Sequentially parses all inner aspect body declarations that may occur in the
   * given input stream and adds them to the given Grammar.
   *
   * @return A list of problems that may only contain internal errors.
   */
  protected static Collection<Problem> parseAspectBodyDeclarations(String source, String sourceName,
      Grammar grammar) {
    Collection<Problem> problems = new LinkedList<Problem>();
    JragParser jp = new JragParser(new StringReader(source));
    jp.root = grammar;
    jp.setFileName(sourceName);
    jp.className = sourceName;
    jp.pushTopLevelOrAspect("aspect");
    try {
      jp.AspectBodyDeclarationsEOF();
      problems.addAll(JastAdd.weaveInterTypeObjects(grammar));
    } catch (org.jastadd.jrag.AST.ParseException e) {
      problems.add(Problem.builder()
          .message("Internal Error in %s: %s", sourceName, e.getMessage())
          .buildError());
    }
    jp.popTopLevelOrAspect();
    return problems;
  }

  /**
   * Reads the line containing a syntax error, and adds highlighting characters under the line.
   */
  private static String syntaxErrorContext(String fileName, int startLine,
      int startColumn, int endColumn) {
    try {
      StringBuilder buf = new StringBuilder();
      InputStream sourceInput = new FileInputStream(fileName);
      Scanner scanner = new Scanner(sourceInput);
      for (int i = 1; i < startLine && scanner.hasNextLine(); ++i) {
        scanner.nextLine();
      }
      if (scanner.hasNextLine()) {
        buf.append(scanner.nextLine()).append("\n");
        for (int i = 1; i < startColumn; ++i) {
          buf.append(" ");
        }
        for (int i = startColumn; i <= endColumn; ++i) {
          buf.append("^");
        }
      }
      return buf.toString();
    } catch (IOException e) {
      // Failed to unparse the offending line, so we skip the context part of the error message.
      return "";
    }
  }
}
