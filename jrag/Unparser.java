package jrag;

import java.util.Set;
import java.util.LinkedHashSet;

import jrag.AST.*;

public class Unparser implements JragParserVisitor {

  public static Set getImports(ASTCompilationUnit self) {
    Set imports = new LinkedHashSet();
    for(int i = 0; i < self.jjtGetNumChildren(); i++) {
      Unparser.getImports((SimpleNode) self.jjtGetChild(i), imports);
    }
    return imports;
  }

  public static void getImports(SimpleNode self, Set imports) {
    if (self instanceof ASTImportDeclaration) {
      Token t = new Token();
      t.next = self.firstToken;

      StringBuffer buf = new StringBuffer(64);

      while(t != null && t != self.lastToken) {
        t = t.next;
        if (t.specialToken != null)
          buf.append(' ');
        buf.append(Util.addUnicodeEscapes(t.image));
      }

      imports.add(buf.toString().trim());
    }
  }

  public static void unparseComment(SimpleNode node, StringBuffer buf) {
    Token tt = node.firstToken.specialToken;
    if (tt != null) {
      while (tt.specialToken != null) tt = tt.specialToken;
      while (tt != null) {
        buf.append(Util.addUnicodeEscapes(tt.image));
        tt = tt.next;
      }
    }
  }

  public static String unparseComment(SimpleNode node) {
    StringBuffer buf = new StringBuffer();
    unparseComment(node, buf);
    return buf.toString();
  }

  public static String unparse(SimpleNode node) {
    StringBuffer buf = new StringBuffer();
    node.jjtAccept(new Unparser(), buf);
    return buf.toString().trim();
  }

  /**
   * Unparse a node to a string buffer
   */
  public static void unparseSimple(JragParserVisitor visitor, SimpleNode node, StringBuffer buf) {
    Token t1 = node.firstToken;
    Token t = new Token();
    t.next = t1;

    SimpleNode n;
    for(int i = 0; i < node.jjtGetNumChildren(); i++) {
      n = (SimpleNode)node.jjtGetChild(i);
      if(n != null) {
        while(true) {
          // unparse linked tokens until the first token of the current child is found
          t = t.next;
          if(t == n.firstToken) break;
          unparseToken(t, buf);
        }
        // unparse the current child
        n.jjtAccept(visitor, buf);
        t = n.lastToken;
      }
    }

    while(t != node.lastToken && t != null) {
      t = t.next;
      unparseToken(t, buf);
    }
  }

  /**
   * Unparse a token to a string buffer
   */
  public static void unparseToken(Token t, StringBuffer buf) {
    if(t == null)
      return;
    Token tt = t.specialToken;
    if (tt != null) {
      while (tt.specialToken != null) tt = tt.specialToken;
      while (tt != null) {
        buf.append(Util.addUnicodeEscapes(tt.image));
        tt = tt.next;
      }
    }
    if(t instanceof Token.GTToken) {
      buf.append(">");
    } else {
      buf.append(t.image);
    }
  }

  public Object visit(SimpleNode self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTCompilationUnit self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTPackageDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTImportDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTModifiers self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTTypeDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectBody self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectBodyDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectClassDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectClassBody self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectInterfaceDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectInterfaceMemberDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectSonsDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectInterfaceSynAttributeDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectInterfaceInhAttributeDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectInterfaceMethodDeclarationLookahead self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectInterfaceMethodDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectInterfaceFieldDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectNestedInterfaceDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectNestedClassDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectMethodDeclarationLookahead self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectMethodDeclaration self, Object data) {
    StringBuffer buf = (StringBuffer) data;
    // MethodDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit
    jrag.AST.Node node = self;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit || node == null) {
      // Remove optional "Class." before IdDecl in method declaration
      Token t1 = ((SimpleNode) self.jjtGetChild(0)).lastToken;
      Token t2 = ((SimpleNode) self.jjtGetChild(1)).firstToken;
      Token t = new Token();
      t.image = " ";
      t2.specialToken = t;
      t1.next = t2;
    }
    unparseSimple(this, self, buf);
    return null;
  }
  public Object visit(ASTAspectRefineMethodDeclarationLookahead self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectRefineMethodDeclaration self, Object data) {
    StringBuffer buf = (StringBuffer) data;
    // MethodDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit
    jrag.AST.Node node = self;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit || node == null) {
      // Remove optional "Class." before IdDecl in method declaration
      Token t1 = ((SimpleNode) self.jjtGetChild(0)).lastToken;
      Token t2 = ((SimpleNode) self.jjtGetChild(1)).firstToken;
      Token t = new Token();
      t.image = " ";
      t2.specialToken = t;
      t1.next = t2;
    }
    unparseSimple(this, self, buf);
    return null;
  }
  public Object visit(ASTAspectConstructorDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectRefineConstructorDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectFieldDeclaration self, Object data) {
    StringBuffer buf = (StringBuffer) data;
    // FieldDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit
    jrag.AST.Node node = self;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit || node == null) {
      // Remove optional "Class." before IdDecl in field declaration 
      Token t1 = ((SimpleNode) self.jjtGetChild(0)).lastToken;
      Token t2 = ((SimpleNode) self.jjtGetChild(1)).firstToken;
      Token t = new Token();
      t.image = " ";
      t2.specialToken = t;
      t1.next = t2;
    }
    unparseSimple(this, self, buf);
    return null;
  }
  public Object visit(ASTAspectSynAttributeDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectInhAttributeDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectRewrite self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectSynEquation self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectRefineSynEquation self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectInhEquation self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectRefineInhEquation self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTCollectionAttribute self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTCollectionContribution self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectAddInterface self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAspectExtendInterface self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTClassDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTTypeNameList self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTUnmodifiedClassDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTEnumDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTEnumBody self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTEnumConstant self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTTypeParameters self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTTypeParameter self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTTypeBound self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTClassBody self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTClassBodyDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTInterfaceDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTUnmodifiedInterfaceDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTInterfaceMemberDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTFieldDeclaration self, Object data) {
    StringBuffer buf = (StringBuffer) data;
    // FieldDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit
    jrag.AST.Node node = self;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit) {
      // Remove optional "Class." before IdDecl in field declaration 
      Token t1 = ((SimpleNode) self.jjtGetChild(0)).lastToken;
      Token t2 = ((SimpleNode) self.jjtGetChild(1)).firstToken;
      Token t = new Token();
      t.image = " ";
      t2.specialToken = t;
      t1.next = t2;
    }
    unparseSimple(this, self, buf);
    return null;
  }
  public Object visit(ASTVariableDeclarator self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTVariableDeclaratorId self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTVariableInitializer self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTArrayInitializer self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTMethodDeclaration self, Object data) {
    StringBuffer buf = (StringBuffer) data;
    // MethodDeclaration <- ClassBodyDeclaration <- 
    // ClassBody <- UnmodifiedClassDecl <- ClassDecl <- TypeDecl <- CompilationUnit
    jrag.AST.Node node = self;
    for(int i = 0; node != null && !(node instanceof ASTCompilationUnit) && i < 8; i++) {
      node = node.jjtGetParent();
    }
    if(node instanceof ASTCompilationUnit) {
      // Remove optional "Class." before IdDecl in method declaration
      Token t1 = ((SimpleNode) self.jjtGetChild(0)).lastToken;
      Token t2 = ((SimpleNode) self.jjtGetChild(1)).firstToken;
      Token t = new Token();
      t.image = " ";
      t2.specialToken = t;
      t1.next = t2;
    }
    unparseSimple(this, self, buf);
    return null;
  }
  public Object visit(ASTMethodDeclarator self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTFormalParameters self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTFormalParameter self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTConstructorDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTExplicitConstructorInvocation self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTInitializer self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTType self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTReferenceType self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTClassOrInterfaceType self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTTypeArguments self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTTypeArgument self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTWildcardBounds self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTPrimitiveType self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTResultType self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTName self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTNameList self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAssignmentOperator self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTConditionalExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTConditionalOrExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTConditionalAndExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTInclusiveOrExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTExclusiveOrExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAndExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTEqualityExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTInstanceOfExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTRelationalExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTShiftExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAdditiveExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTMultiplicativeExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTUnaryExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTPreIncrementExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTPreDecrementExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTUnaryExpressionNotPlusMinus self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTCastLookahead self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTPostfixExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTCastExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTPrimaryExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTMemberSelector self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTPrimaryPrefix self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTPrimarySuffix self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTLiteral self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTBooleanLiteral self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTNullLiteral self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTArguments self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTArgumentList self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAllocationExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTArrayDimsAndInits self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTStatement self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAssertStatement self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTLabeledStatement self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTBlock self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTBlockStatement self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTLocalVariableDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTEmptyStatement self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTStatementExpression self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTSwitchStatement self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTSwitchLabel self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTIfStatement self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTWhileStatement self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTDoStatement self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTForStatement self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTForInit self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTStatementExpressionList self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTForUpdate self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTBreakStatement self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTContinueStatement self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTReturnStatement self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTThrowStatement self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTSynchronizedStatement self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTTryStatement self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTRUNSIGNEDSHIFT self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTRSIGNEDSHIFT self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAnnotation self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTNormalAnnotation self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTMarkerAnnotation self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTSingleMemberAnnotation self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTMemberValuePairs self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTMemberValuePair self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTMemberValue self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTMemberValueArrayInitializer self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAnnotationTypeDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAnnotationTypeBody self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTAnnotationTypeMemberDeclaration self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTDefaultValue self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
  public Object visit(ASTCacheDeclarations self, Object data) {
    Unparser.unparseSimple(this, self, (StringBuffer) data);
    return null;
  }
}
