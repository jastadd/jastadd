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
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedList;

import org.jastadd.ast.AST.Ast;
import org.jastadd.ast.AST.Grammar;
import org.jastadd.ast.AST.List;
import org.jastadd.ast.AST.TokenMgrError;
import org.jastadd.ast.AST.TypeDecl;
import org.jastadd.jrag.AST.ASTCompilationUnit;
import org.jastadd.jrag.AST.JragParser;

/**
 *  A convenient place for utility functions like for parsing.
 */
public class JastAddUtil {


  /**
   * Parses a given AST specification from a given Reader and returns it as a fresh grammar object.
   *
   * @param source - the stream-based source
   * @param sourceName - the source's name
   * @param problems - a collection where problems should be added for later error handling
   * @return fresh grammar, but null if problems occur
   */
  public static Grammar parseASTSpec(Reader source, String sourceName, Collection<Problem> problems){
    Ast parser = new Ast(source);
    parser.fileName = sourceName;
    try {
      Grammar astGrammar = parser.Grammar();
      problems.addAll(parser.parseProblems());
      return astGrammar;
    } catch (org.jastadd.ast.AST.TokenMgrError e) {
      problems.add(new Problem.Error(e.getMessage(), sourceName));
    } catch (org.jastadd.ast.AST.ParseException e) {
      // ParseExceptions actually caught by error recovery in parser
    }
    return null;
  }

  /**
   * Parses a given AST specification from a given Reader and adds its content to a given grammar object.
   *
   * @param source - the stream-based source
   * @param resourceName - the source's name
   * @param problems - a collection where problems should be added for later error handling
   * @param grammar - the grammar object where the AST specifications contents should be added
   */
  public static void parseASTSpec(Reader source, String resourceName, Grammar grammar, Collection<Problem> problems){
    Grammar astGrammar = parseASTSpec(source, resourceName, problems);
    if (astGrammar != null) {
      List<TypeDecl> typeDecls = astGrammar.getTypeDeclListNoTransform();
      for (int i = 0; i < typeDecls.getNumChildNoTransform(); i++) {
        grammar.addTypeDecl(typeDecls.getChildNoTransform(i));
      }
      for (int i = 0; i < astGrammar.getNumRegionDecl(); i++) {
        grammar.addRegionDecl(astGrammar.getRegionDecl(i));
      }
    }
  }

  /**
   * Parses a given AST specification in a given File and adds its content to a given grammar object.
   *
   * @param source - the file based source
   * @param problems - a collection where problems should be added for later error handling
   * @param grammar - the grammar object where the AST specifications contents should be added
   */
  public static void parseASTSpec(File source, Grammar grammar, Collection<Problem> problems){
    Reader inStream = null;
    String fileName = source.getPath();
    try {
      inStream = new FileReader(source);
      parseASTSpec(inStream,fileName,grammar,problems);

    } catch (FileNotFoundException e) {
      problems.add(new Problem.Error(
          "could not find abstract syntax file '" + fileName + "'"));
    } finally {
      if (inStream != null)
        try {
          inStream.close();
        } catch (IOException e) {
          // do nothing
        }
    }
  }

  /**
   * Parses a given AST specification from a given string and adds its content to a given grammar object.
   *
   * @param source - the string-based source
   * @param sourceName - the name of the source
   * @param problems - a collection where problems should be added for later error handling
   * @param grammar - the grammar object where the AST specifications contents should be added
   */
  public static void parseASTSpec(String source, String sourceName, Grammar grammar, Collection<Problem> problems){
    Reader inStream = new StringReader(source);
    parseASTSpec(inStream,sourceName,grammar,problems);
  }

  /**
   * Parses a JRAG or JADD specification from a given stream.
   *
   * @param source - the stream-based source
   * @param sourceName - the name of the source
   * @param problems - the collection where new objects can be added
   * @param grammar - the grammar object to parameterize the parser
   *        (TODO: check why this parameter is actually needed!)
   * @return the corresponding compilation unit object
   */
  public static ASTCompilationUnit parseJRAGSpec(Reader source,
      String sourceName, Collection<Problem> problems, Grammar grammar) {
    try {
      JragParser parser = new JragParser(source);
      parser.setFileName(sourceName);
      parser.root = grammar;
      ASTCompilationUnit cu = parser.CompilationUnit();
      return cu;
    } catch (org.jastadd.jrag.AST.ParseException e) {
      problems.add(new Problem.Error("syntax error", sourceName,
          e.currentToken.next.beginLine, e.currentToken.next.beginColumn));
    } catch (TokenMgrError e) {
      problems.add(new Problem.Error(e.getMessage()));
    } catch (Throwable e) {
      problems.add(new Problem.Error("exception occurred while parsing: " + e.getMessage(),
          sourceName));
    }
    return null;
  }

  /**
   * Parses a JRAG or JADD specification from a given stream and adds the resulting contents to the given grammar object.
   *
   * @param source - the stream-based source
   * @param sourceName - the name of the source
   * @param grammar - the grammar object to parameterize the parser
   * @param problems - the collection where new objects can be added
   */
  public static void parseJRAGSpec(Reader source,
      String sourceName, Grammar grammar, Collection<Problem> problems) {
    ASTCompilationUnit cu = parseJRAGSpec(source, sourceName, problems, grammar);
    if (cu!=null) {
      grammar.addCompUnit(cu);
    }
  }

  /**
   * Parses a JRAG or JADD specification from a given file and adds it to the given grammar object.
   *
   * @param source - the file-based source
   * @param grammar - the grammar where the content should be added
   * @param problems - the collection where new objects can be added
   *
   */
  public static void parseJRAGSpec(File source, Grammar grammar, Collection<Problem> problems) {
    Reader inStream = null;
    String fileName = source.getPath();
    try {
      inStream = new FileReader(source);
      parseJRAGSpec(inStream, fileName, grammar, problems);
    } catch (FileNotFoundException e) {
      problems.add(new Problem.Error("could not find aspect file '" + fileName + "'"));
    } finally {
      if (inStream != null)
        try {
          inStream.close();
        } catch (IOException e) {
          // do nothing
        }
    }
  }

  /**
   * Parses a JRAG or JADD specification from a given string and adds it to the given grammar object.
   *
   * @param source - the string-based source
   * @param sourceName - the source name
   * @param grammar - the grammar where the content should be added
   * @param problems - the collection where new objects can be added
   *
   */
  public static void parseJRAGSpec(String source, String sourceName, Grammar grammar, Collection<Problem> problems) {
    Reader inStream = new StringReader(source);
    parseJRAGSpec(inStream, sourceName, grammar, problems);
  }

  /**
   * Sequentially parses all inner aspect body declarations that may occur in the
   * given input stream and adds them to the given Grammar.
   *
   * @param source
   * @param sourceName
   * @param grammar
   * @return A list of problems that may only contain internal errors.
   */
  public static Collection<Problem> parseAspectBodyDeclarations(Reader source, String sourceName, Grammar grammar){
    Collection<Problem> problems = new LinkedList<Problem>();
    JragParser jp = new JragParser(source);
    jp.root = grammar;
    jp.setFileName(sourceName);
    jp.className = sourceName;
    jp.pushTopLevelOrAspect("aspect");
    try {
      jp.AspectBodyDeclarationsEOF();
      problems.addAll(JastAdd.weaveInterTypeObjects(grammar));
    } catch (org.jastadd.jrag.AST.ParseException e) {
      problems.add(new Problem.Error("Internal Error in " + sourceName + ": " + e.getMessage()));
    }
    jp.popTopLevelOrAspect();
    return problems;
  }

  /**
   * Sequentially parses all inner aspect body declarations that may occur in the
   * given string and adds them to the given Grammar.
   *
   * @param source
   * @param sourceName
   * @param grammar
   * @return A list of problems that may only contain internal errors.
   */
  public static Collection<Problem> parseAspectBodyDeclarations(String source, String sourceName, Grammar grammar){
    StringReader inStream = new StringReader(source);
    return parseAspectBodyDeclarations(inStream, sourceName, grammar);
  }

  /**
   * Parses cache declaration objects from a given source and adds them to
   * the given grammar object
   *
   * @param source - the source stream
   * @param sourceName - the name of the source
   * @param grammar - the root grammar object
   * @param problems - the collection problems should be added to
   */
  public static void parseCacheDeclarations(Reader source,
    String sourceName, Grammar grammar, Collection<Problem> problems) {
    try {
      JragParser jp = new JragParser(source);
      jp.root = grammar;
      jp.setFileName(sourceName);
      jp.CacheDeclarations();
    } catch (org.jastadd.jrag.AST.ParseException e) {
      problems.add(new Problem.Error("syntax error", sourceName,
          e.currentToken.next.beginLine, e.currentToken.next.beginColumn));
    }
  }

  /**
   * @param source
   * @param grammar
   * @param problems
   */
  public static void parseCacheDeclarations(File source, Grammar grammar, Collection<Problem> problems){
    Reader inStream = null;
    String fileName = source.getName();
    try {
      inStream = new FileReader(source);
      parseCacheDeclarations(inStream, fileName, grammar, problems);
    } catch (FileNotFoundException e) {
      problems.add(new Problem.Error("could not find cache file '" + fileName + "'"));
    } finally {
      if (inStream != null)
        try {
          inStream.close();
        } catch (IOException e) {
          // do nothing
        }
    }

  }

}
