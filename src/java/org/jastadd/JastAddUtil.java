package org.jastadd;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.jastadd.ast.AST.Ast;
import org.jastadd.ast.AST.Grammar;
import org.jastadd.ast.AST.ParseException;
import org.jastadd.jrag.AST.ASTCompilationUnit;
import org.jastadd.jrag.AST.JragParser;

/**
 *  A convenient place for utility functions like parsing. 
 */
public class JastAddUtil {
  
  public static ASTCompilationUnit parseJRAGSpecFromStream(InputStream resourceStream,
      String resourceName, Grammar rootGrammar, Collection<Problem> problems) {
    try {
      JragParser parser = new JragParser(resourceStream);
      parser.inputStream = resourceStream; // Hack to make input stream visible                                          // for ast-parser
      parser.setFileName(resourceName);
      parser.root = rootGrammar;
      ASTCompilationUnit cu = parser.CompilationUnit();
      return cu;
    } catch (org.jastadd.jrag.AST.ParseException e) {
      problems.add(new Problem.Error("syntax error", resourceName,
          e.currentToken.next.beginLine, e.currentToken.next.beginColumn));
    } catch (Throwable e) {
      problems.add(new Problem.Error("exception occurred while parsing",
          resourceName));
    }
    return null;
  }

  public static Grammar parseASTSpecFromStream(InputStream source, String sourceName, Collection<Problem> problems){
    Ast parser = new Ast(source);
    parser.fileName = sourceName;
    try {
      Grammar astGrammar = parser.Grammar();
      problems.addAll(parser.parseProblems());
      return astGrammar;
    }
    catch (org.jastadd.ast.AST.TokenMgrError e) {
      problems.add(new Problem.Error(e.getMessage(), sourceName));
    } catch (ParseException e) {
      // ParseExceptions actually caught by error recovery in parser
    }
    return null;
  }

  public static Collection<Problem> parseASTFiles(Grammar rootGrammar, Collection<String> fileNames) {
    Collection<Problem> problems = new LinkedList<Problem>();
    // out.println("parsing grammars");
    for (Iterator<String> iter = fileNames.iterator(); iter.hasNext();) {
      String fileName = iter.next();
      if (fileName.endsWith(".ast")) {
        InputStream inStream = null;
        try {
          inStream = new FileInputStream(fileName);
          Grammar astGrammar = parseASTSpecFromStream(inStream, fileName, problems);
          if (astGrammar != null) {
            for (int i = 0; i < astGrammar.getNumTypeDecl(); i++) {
              rootGrammar.addTypeDecl(astGrammar.getTypeDecl(i));
            }
  
            // EMMA_2011-12-12: Adding region declarations for incremental
            // evaluation
            for (int i = 0; i < astGrammar.getNumRegionDecl(); i++) {
              rootGrammar.addRegionDecl(astGrammar.getRegionDecl(i));
            }
          }
  
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
    }
    return problems;
  }

  public static Collection<Problem> parseJRAGFiles(Grammar rootGrammar, Collection<String> fileNames) {
    Collection<Problem> problems = new LinkedList<Problem>();
    for (Iterator<String> iter = fileNames.iterator(); iter.hasNext();) {
      String fileName = iter.next();
      if (fileName.endsWith(".jrag") || fileName.endsWith(".jadd")) {
        InputStream inStream = null;
        try {
          inStream = new FileInputStream(fileName);
          ASTCompilationUnit cu = parseJRAGSpecFromStream(inStream, fileName, rootGrammar, problems);
          if(cu!=null)
            rootGrammar.addCompUnit(cu);
        } catch (FileNotFoundException e) {
          problems.add(new Problem.Error("could not find aspect file '" + fileName + "'"));
        }
        finally {
          if (inStream != null)
            try {
              inStream.close();
            } catch (IOException e) {
              // do nothing
            }
        }
      }
    }
    return problems;
  }
  
  /**
   * Sequentially parses all inner aspect body declarations that may occur in the 
   * given input stream and adds them to the given Grammar. 
   * 
   * @param rootGrammar
   * @param inStream
   * @return
   */
  public static Collection<Problem> parseAspectBodyDeclarations(Grammar rootGrammar, Reader inStream){
    Collection<Problem> problems = new LinkedList<Problem>();
    JragParser jp = new JragParser(inStream);
    jp.root = rootGrammar;
    jp.setFileName("ASTNode");
    jp.className = "ASTNode";
    jp.pushTopLevelOrAspect(true);
    try {
      while(true)
        jp.AspectBodyDeclaration();
    } catch (Exception e) {
      problems.add(new Problem.Error("Internal Error: " + e.getMessage()));
    }
    jp.popTopLevelOrAspect();
    return problems;
  }
  
  public static void parseCacheDeclaration(Reader inStream,
      String resourceName, Grammar rootGrammar, Collection<Problem> problems) {
    try {
      JragParser jp = new JragParser(inStream);
      jp.root = rootGrammar;
      jp.setFileName(resourceName);
      jp.CacheDeclarations();
    } catch (org.jastadd.jrag.AST.ParseException e) {
      problems.add(new Problem.Error("syntax error", resourceName,
          e.currentToken.next.beginLine, e.currentToken.next.beginColumn));
    }
  }
  
}
