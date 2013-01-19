package jrag;

import ast.AST.*;

import jrag.AST.*;
import jrag.AST.Token;
import jrag.AST.SimpleNode;

import java.util.Set;
import java.util.LinkedHashSet;

public aspect Unparse {

  
  
  // Get import declarations
   
  public void SimpleNode.getImports(Set imports) {
  }
  
  public Set ASTCompilationUnit.getImports() {
    Set imports = new LinkedHashSet();
    for(int i = 0; i < jjtGetNumChildren(); i++) {
      ((SimpleNode)jjtGetChild(i)).getImports(imports);
    }
    return imports;
  }

  public void ASTImportDeclaration.getImports(Set imports) {
    Unparser.unparseImport(this, imports);
  }


  // Unparse in aspectJ syntax

  public void SimpleNode.unparseClassBodyDeclaration(StringBuffer buf,
    String className, boolean aspectJ) {

      Token t = new Token();
      t.next = firstToken;

      SimpleNode n;
      for(int i = 0; i < jjtGetNumChildren(); i++) {
        n = (SimpleNode)jjtGetChild(i);
        while(true) {
          t = t.next;
          if(t == n.firstToken) break;
          Unparser.unparseToken(t, buf);
        }
        n.unparseClassBodyDeclaration(buf, className, aspectJ);
        t = n.lastToken;
      }

      while(t != lastToken && t != null) {
        t = t.next;
        Unparser.unparseToken(t, buf);
      }
  }

  
  public void ASTMethodDeclaration.unparseClassBodyDeclaration(StringBuffer buf,
    String className, boolean aspectJ) {
    // Ditch the comment, if one exists
    //firstToken.specialToken = null;

    // MethodDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit

    jrag.AST.Node node = this;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit) {
      Token t1 = ((SimpleNode)jjtGetChild(0)).lastToken;
      Token t2 = ((SimpleNode)jjtGetChild(1)).firstToken;
      Token t = new Token();
      if(aspectJ)
        t.image = " " + className + ".";
      else
        t.image = " ";
      t2.specialToken = t;
      t1.next = t2;
      Unparser.unparseSimple(new Unparser(), this, buf);
    }
    else {
      super.unparseClassBodyDeclaration(buf, className, aspectJ);
    }
  }
  
  public void ASTAspectMethodDeclaration.unparseClassBodyDeclaration(StringBuffer buf,
    String className, boolean aspectJ) {

    // Ditch the comment, if one exists
    firstToken.specialToken = null;

    // MethodDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit
    jrag.AST.Node node = this;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit || node == null) {
      Token t1 = ((SimpleNode)jjtGetChild(0)).lastToken;
      Token t2 = ((SimpleNode)jjtGetChild(1)).firstToken;
      Token t = new Token();
      if(aspectJ)
        t.image = " " + className + ".";
      else
        t.image = " ";
      t2.specialToken = t;
      t1.next = t2;
      Unparser.unparseSimple(new Unparser(), this, buf);
    }
    else {
      super.unparseClassBodyDeclaration(buf, className, aspectJ);
    }
  }
  public void ASTAspectMethodDeclaration.unparseAbstractClassBodyDeclaration(StringBuffer buf,
    String className, boolean aspectJ) {
      Token t1 = ((SimpleNode)jjtGetChild(0)).lastToken;
      Token t2 = ((SimpleNode)jjtGetChild(1)).firstToken;
      Token t = new Token();
      if(aspectJ)
        t.image = " " + className + ".";
      else
        t.image = " ";
      t2.specialToken = t;
      t1.next = t2;

      t1 = firstToken;
      t = new Token();
      t.next = t1;

      SimpleNode n;
      int lastIndex = jjtGetNumChildren() >= 3 && jjtGetChild(2) instanceof ASTNameList ? 3 : 2;
      for(int i = 0; i < lastIndex; i++) {
        n = (SimpleNode)jjtGetChild(i);
        while(true) {
          t = t.next;
          if(t == n.firstToken) break;
          Unparser.unparseToken(t, buf);
        }
        n.jjtAccept(new Unparser(), buf);
        t = n.lastToken;
      }
      if(jjtGetNumChildren() > 1)
        buf.append(";\n");

      //while(t != lastToken) {
      //  t = t.next;
      //  Unparser.unparseToken(t, buf);
      //}
  }
  public void ASTAspectRefineMethodDeclaration.unparseClassBodyDeclaration(StringBuffer buf,
    String className, boolean aspectJ) {
    // Ditch the comment, if one exists
    firstToken.specialToken = null;

    // MethodDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit
    jrag.AST.Node node = this;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit || node == null) {
      Token t1 = ((SimpleNode)jjtGetChild(0)).lastToken;
      Token t2 = ((SimpleNode)jjtGetChild(1)).firstToken;
      Token t = new Token();
      if(aspectJ)
        t.image = " " + className + ".";
      else
        t.image = " ";
      t2.specialToken = t;
      t1.next = t2;
      Unparser.unparseSimple(new Unparser(), this, buf);
    }
    else {
      super.unparseClassBodyDeclaration(buf, className, aspectJ);
    }
  }
  public void ASTAspectRefineMethodDeclaration.unparseAbstractClassBodyDeclaration(StringBuffer buf,
    String className, boolean aspectJ) {
      Token t1 = ((SimpleNode)jjtGetChild(0)).lastToken;
      Token t2 = ((SimpleNode)jjtGetChild(1)).firstToken;
      Token t = new Token();
      if(aspectJ)
        t.image = " " + className + ".";
      else
        t.image = " ";
      t2.specialToken = t;
      t1.next = t2;

      t1 = firstToken;
      t = new Token();
      t.next = t1;

      SimpleNode n;
      int lastIndex = jjtGetNumChildren() >= 3 && jjtGetChild(2) instanceof ASTNameList ? 3 : 2;
      for(int i = 0; i < lastIndex; i++) {
        n = (SimpleNode)jjtGetChild(i);
        while(true) {
          t = t.next;
          if(t == n.firstToken) break;
          Unparser.unparseToken(t, buf);
        }
        n.jjtAccept(new Unparser(), buf);
        t = n.lastToken;
      }
      if(jjtGetNumChildren() > 1)
        buf.append(";\n");

      //while(t != lastToken) {
      //  t = t.next;
      //  Unparser.unparseToken(t, buf);
      //}
  }
  
  public void ASTAspectConstructorDeclaration.unparseClassBodyDeclaration(StringBuffer buf,
    String className, boolean aspectJ) {
    // Ditch the comment, if one exists
    firstToken.specialToken = null;

    // ConstructorDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit
    jrag.AST.Node node = this;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit || node == null) {
      Token t1 = ((SimpleNode)jjtGetChild(0)).firstToken;
      Token t2 = firstToken;
      while(t2.next.next.next != t1)
        t2 = t2.next;
      t2.image ="";
      t2.next.image="";
      Unparser.unparseSimple(new Unparser(), this, buf);
    }
    else {
      super.unparseClassBodyDeclaration(buf, className, aspectJ);
    }
  }

  public void ASTAspectRefineConstructorDeclaration.unparseClassBodyDeclaration(StringBuffer buf,
    String className, boolean aspectJ) {
    // Ditch the comment, if one exists
    firstToken.specialToken = null;

    // ConstructorDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit
    jrag.AST.Node node = this;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit || node == null) {
      Token t1 = ((SimpleNode)jjtGetChild(0)).firstToken;
      Token t2 = firstToken;
      while(t2.next.next.next != t1)
        t2 = t2.next;
      t2.image ="";
      t2.next.image="";
      Unparser.unparseSimple(new Unparser(), this, buf);
    }
    else {
      super.unparseClassBodyDeclaration(buf, className, aspectJ);
    }
  }


  public void ASTModifiers.unparseClassBodyDeclaration(StringBuffer buf, String className, boolean aspectJ) {
    buf.append(Unparser.unparse(this));
    buf.append(" ");
  }
  
  public void ASTFieldDeclaration.unparseClassBodyDeclaration(StringBuffer buf,
    String className, boolean aspectJ) {
    // Ditch the comment, if one exists
    //firstToken.specialToken = null;

    // FieldDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit
    jrag.AST.Node node = this;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit) {
      Token t1 = ((SimpleNode)jjtGetChild(0)).lastToken;
      Token t2 = ((SimpleNode)jjtGetChild(1)).firstToken;
      Token t = new Token();
      if(aspectJ)
        t.image = " " + className + ".";
      else
        t.image = " ";
      t2.specialToken = t;
      t1.next = t2;
      Unparser.unparseSimple(new Unparser(), this, buf);
    }
    else {
      super.unparseClassBodyDeclaration(buf, className, aspectJ);
    }
  }
  
  public void ASTAspectFieldDeclaration.unparseClassBodyDeclaration(StringBuffer buf,
    String className, boolean aspectJ) {
    // Ditch the comment, if one exists
    //firstToken.specialToken = null;

    // FieldDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit
    jrag.AST.Node node = this;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit || node == null) {
      Token t1 = ((SimpleNode)jjtGetChild(0)).lastToken;
      Token t2 = ((SimpleNode)jjtGetChild(1)).firstToken;
      Token t = new Token();
      if(aspectJ)
        t.image = " " + className + ".";
      else
        t.image = " ";
      t2.specialToken = t;
      t1.next = t2;
      Unparser.unparseSimple(new Unparser(), this, buf);
    }
    else {
      super.unparseClassBodyDeclaration(buf, className, aspectJ);
    }
  }
}
