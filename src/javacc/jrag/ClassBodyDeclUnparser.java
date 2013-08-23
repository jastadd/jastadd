package org.jastadd.jrag;

import org.jastadd.jrag.AST.*;

public class ClassBodyDeclUnparser implements JragParserVisitor {
  public Object unparseSimple(SimpleNode self, StringBuffer buf) {
      Token t = new Token();
      t.next = self.firstToken;

      SimpleNode n;
      for(int i = 0; i < self.jjtGetNumChildren(); i++) {
        n = (SimpleNode) self.jjtGetChild(i);
        while(true) {
          t = t.next;
          if(t == n.firstToken) break;
          Unparser.unparseToken(t, buf);
        }
        n.jjtAccept(this, buf);
        t = n.lastToken;
      }

      while(t != self.lastToken && t != null) {
        t = t.next;
        Unparser.unparseToken(t, buf);
      }
    return null;
  }

  public static void unparseAbstract(ASTAspectMethodDeclaration self, StringBuffer buf) {
    Token t1 = ((SimpleNode) self.jjtGetChild(0)).lastToken;
    Token t2 = ((SimpleNode) self.jjtGetChild(1)).firstToken;
    Token t = new Token();
    t.image = " ";
    t2.specialToken = t;
    t1.next = t2;

    t1 = self.firstToken;
    t = new Token();
    t.next = t1;

    SimpleNode n;
    int lastIndex = self.jjtGetNumChildren() >= 3 && self.jjtGetChild(2) instanceof ASTNameList ? 3 : 2;
    for(int i = 0; i < lastIndex; i++) {
      n = (SimpleNode) self.jjtGetChild(i);
      while(true) {
        t = t.next;
        if(t == n.firstToken) break;
        Unparser.unparseToken(t, buf);
      }
      n.jjtAccept(new Unparser(), buf);
      t = n.lastToken;
    }
    if(self.jjtGetNumChildren() > 1)
      buf.append(";\n");

    //while(t != self.lastToken) {
    //  t = t.next;
    //  Unparser.unparseToken(t, buf);
    //}
  }

  public static void unparseAbstract(ASTAspectRefineMethodDeclaration self,
      StringBuffer buf) {

    Token t1 = ((SimpleNode) self.jjtGetChild(0)).lastToken;
    Token t2 = ((SimpleNode) self.jjtGetChild(1)).firstToken;
    Token t = new Token();
    t.image = " ";
    t2.specialToken = t;
    t1.next = t2;

    t1 = self.firstToken;
    t = new Token();
    t.next = t1;

    SimpleNode n;
    int lastIndex = self.jjtGetNumChildren() >= 3 && self.jjtGetChild(2) instanceof ASTNameList ? 3 : 2;
    for(int i = 0; i < lastIndex; i++) {
      n = (SimpleNode) self.jjtGetChild(i);
      while(true) {
        t = t.next;
        if(t == n.firstToken) break;
        Unparser.unparseToken(t, buf);
      }
      n.jjtAccept(new Unparser(), buf);
      t = n.lastToken;
    }
    if(self.jjtGetNumChildren() > 1)
      buf.append(";\n");

    //while(t != self.lastToken) {
    //  t = t.next;
    //  Unparser.unparseToken(t, buf);
    //}
  }

  public Object visit(SimpleNode self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTCompilationUnit self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTPackageDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTImportDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTModifiers self, Object data) {
    StringBuffer buf = (StringBuffer) data;
    buf.append(Unparser.unparse(self));
    buf.append(" ");
    return null;
  }
  public Object visit(ASTTypeDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectBody self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectBodyDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectClassDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectClassBody self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectInterfaceDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectInterfaceMemberDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectSonsDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectInterfaceSynAttributeDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectInterfaceInhAttributeDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectInterfaceMethodDeclarationLookahead self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectInterfaceMethodDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectInterfaceFieldDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectNestedInterfaceDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectNestedClassDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectMethodDeclarationLookahead self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectMethodDeclaration self, Object data) {
    StringBuffer buf = (StringBuffer) data;
    // Ditch the comment, if one exists
    self.firstToken.specialToken = null;

    // MethodDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit
    org.jastadd.jrag.AST.Node node = self;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit || node == null) {
      // skip the "<ASTNode>." part
      Token t1 = ((SimpleNode) self.jjtGetChild(0)).lastToken;
      Token t2 = ((SimpleNode) self.jjtGetChild(1)).firstToken;
      Token t = new Token();
      t.image = " ";
      t2.specialToken = t;
      t1.next = t2;
      Unparser.unparseSimple(new Unparser(), self, buf);
    } else {
      unparseSimple(self, buf);
    }
    return null;
  }
  public Object visit(ASTAspectRefineMethodDeclarationLookahead self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectRefineMethodDeclaration self, Object data) {
    StringBuffer buf = (StringBuffer) data;
    // Ditch the comment, if one exists
    self.firstToken.specialToken = null;

    // MethodDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit
    org.jastadd.jrag.AST.Node node = self;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit || node == null) {
      // skip the "<ASTNode>." part
      Token t1 = ((SimpleNode) self.jjtGetChild(0)).lastToken;
      Token t2 = ((SimpleNode) self.jjtGetChild(1)).firstToken;
      Token t = new Token();
      t.image = " ";
      t2.specialToken = t;
      t1.next = t2;
      Unparser.unparseSimple(new Unparser(), self, buf);
    }
    else {
      unparseSimple(self, buf);
    }
    return null;
  }
  public Object visit(ASTAspectConstructorDeclaration self, Object data) {
    StringBuffer buf = (StringBuffer) data;
    // Ditch the comment, if one exists
    self.firstToken.specialToken = null;

    // ConstructorDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit
    org.jastadd.jrag.AST.Node node = self;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit || node == null) {
      // skip the "<ASTNode>." part
      Token t1 = ((SimpleNode) self.jjtGetChild(0)).firstToken;
      Token t2 = self.firstToken;
      while(t2.next.next.next != t1)
        t2 = t2.next;
      t2.image ="";
      t2.next.image="";
      Unparser.unparseSimple(new Unparser(), self, buf);
    }
    else {
      unparseSimple(self, buf);
    }
    return null;
  }
  public Object visit(ASTAspectRefineConstructorDeclaration self, Object data) {
    StringBuffer buf = (StringBuffer) data;
    // Ditch the comment, if one exists
    self.firstToken.specialToken = null;

    // ConstructorDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit
    org.jastadd.jrag.AST.Node node = self;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit || node == null) {
      Token t1 = ((SimpleNode) self.jjtGetChild(0)).firstToken;
      Token t2 = self.firstToken;
      while(t2.next.next.next != t1)
        t2 = t2.next;
      t2.image ="";
      t2.next.image="";
      Unparser.unparseSimple(new Unparser(), self, buf);
    }
    else {
      unparseSimple(self, buf);
    }
    return null;
  }
  public Object visit(ASTAspectFieldDeclarationLookahead self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectFieldDeclaration self, Object data) {
    StringBuffer buf = (StringBuffer) data;
    // Ditch the comment, if one exists
    self.firstToken.specialToken = null;

    // FieldDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit
    org.jastadd.jrag.AST.Node node = self;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit || node == null) {
      // skip the "<ASTNode>." part
      Token t1 = ((SimpleNode) self.jjtGetChild(0)).lastToken;
      Token t2 = ((SimpleNode) self.jjtGetChild(1)).firstToken;
      Token t = new Token();
      t.image = " ";
      t2.specialToken = t;
      t1.next = t2;
      Unparser.unparseSimple(new Unparser(), self, buf);
    }
    else {
      unparseSimple(self, buf);
    }
    return null;
  }
  public Object visit(ASTAspectSynAttributeDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectInhAttributeDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectRewrite self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectSynEquation self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectRefineSynEquation self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectInhEquation self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectRefineInhEquation self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTCollectionAttribute self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTCollectionContribution self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectAddInterface self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAspectExtendInterface self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTClassDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTTypeNameList self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTUnmodifiedClassDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTEnumDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTEnumBody self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTEnumConstant self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTTypeParameters self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTTypeParameter self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTTypeBound self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTClassBody self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTClassBodyDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTInterfaceDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTUnmodifiedInterfaceDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTInterfaceMemberDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTFieldDeclaration self, Object data) {
    StringBuffer buf = (StringBuffer) data;
    // Ditch the comment, if one exists
    //self.firstToken.specialToken = null;

    // FieldDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit
    org.jastadd.jrag.AST.Node node = self;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit) {
      // skip the "<ASTNode>." part
      Token t1 = ((SimpleNode) self.jjtGetChild(0)).lastToken;
      Token t2 = ((SimpleNode) self.jjtGetChild(1)).firstToken;
      Token t = new Token();
      t.image = " ";
      t2.specialToken = t;
      t1.next = t2;
      Unparser.unparseSimple(new Unparser(), self, buf);
    }
    else {
      unparseSimple(self, buf);
    }
    return null;
  }
  public Object visit(ASTVariableDeclarator self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTVariableDeclaratorId self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTVariableInitializer self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTArrayInitializer self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTMethodDeclaration self, Object data) {
    StringBuffer buf = (StringBuffer) data;
    // Ditch the comment, if one exists
    //self.firstToken.specialToken = null;

    // MethodDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit

    org.jastadd.jrag.AST.Node node = self;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit) {
      // skip the "<ASTNode>." part
      Token t1 = ((SimpleNode) self.jjtGetChild(0)).lastToken;
      Token t2 = ((SimpleNode) self.jjtGetChild(1)).firstToken;
      Token t = new Token();
      t.image = " ";
      t2.specialToken = t;
      t1.next = t2;
      Unparser.unparseSimple(new Unparser(), self, buf);
    }
    else {
      unparseSimple(self, buf);
    }
    return null;
  }
  public Object visit(ASTMethodDeclarator self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTFormalParameters self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTFormalParameter self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTConstructorDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTExplicitConstructorInvocation self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTInitializer self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTType self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTReferenceType self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTClassOrInterfaceType self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTTypeArguments self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTTypeArgument self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTWildcardBounds self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTPrimitiveType self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTResultType self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTName self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTNameList self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAssignmentOperator self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTConditionalExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTConditionalOrExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTConditionalAndExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTInclusiveOrExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTExclusiveOrExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAndExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTEqualityExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTInstanceOfExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTRelationalExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTShiftExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAdditiveExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTMultiplicativeExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTUnaryExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTPreIncrementExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTPreDecrementExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTUnaryExpressionNotPlusMinus self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTCastLookahead self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTPostfixExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTCastExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTPrimaryExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTMemberSelector self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTPrimaryPrefix self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTPrimarySuffix self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTLiteral self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTBooleanLiteral self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTNullLiteral self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTArguments self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTArgumentList self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAllocationExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTArrayDimsAndInits self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTStatement self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAssertStatement self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTLabeledStatement self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTBlock self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTBlockStatement self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTLocalVariableDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTEmptyStatement self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTStatementExpression self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTSwitchStatement self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTSwitchLabel self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTIfStatement self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTWhileStatement self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTDoStatement self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTForStatement self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTForInit self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTStatementExpressionList self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTForUpdate self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTBreakStatement self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTContinueStatement self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTReturnStatement self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTThrowStatement self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTSynchronizedStatement self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTTryStatement self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTRUNSIGNEDSHIFT self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTRSIGNEDSHIFT self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAnnotation self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTNormalAnnotation self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTMarkerAnnotation self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTSingleMemberAnnotation self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTMemberValuePairs self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTMemberValuePair self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTMemberValue self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTMemberValueArrayInitializer self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAnnotationTypeDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAnnotationTypeBody self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTAnnotationTypeMemberDeclaration self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTDefaultValue self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
  public Object visit(ASTCacheDeclarations self, Object data) {
    return unparseSimple(self, (StringBuffer) data);
  }
}
