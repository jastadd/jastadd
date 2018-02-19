package org.jastadd.jrag;

import org.jastadd.jrag.AST.*;

/**
 * Create signature used to match refined method declarations.
 *
 * signature = method name _ formal param1 _ formal param2
 */
public class SignatureUnparser implements JragParserVisitor {
  public Object visit(SimpleNode node, Object data) {
    return "";
  }
  public Object visit(ASTCompilationUnit node, Object data) {
    return "";
  }
  public Object visit(ASTImportDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTModifiers node, Object data) {
    return "";
  }
  public Object visit(ASTTypeDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTAspectDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTAspectBody node, Object data) {
    return "";
  }
  public Object visit(ASTAspectBodyDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTAspectBodyDeclarationsEOF node, Object data) {
    return "";
  }
  public Object visit(ASTAspectClassDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTAspectClassBody node, Object data) {
    return "";
  }
  public Object visit(ASTAspectInterfaceDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTAspectInterfaceMemberDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTAspectInterfaceSynAttributeDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTAspectInterfaceInhAttributeDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTAspectInterfaceMethodDeclarationLookahead node, Object data) {
    return "";
  }
  public Object visit(ASTAspectInterfaceMethodDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTAspectInterfaceFieldDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTAspectNestedInterfaceDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTAspectNestedClassDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTAspectMethodDeclarationLookahead node, Object data) {
    return "";
  }
  public Object visit(ASTAspectMethodDeclaration node, Object data) {
    // AspectMethodDeclaration = Modifiers() [ TypeArguments() ] ResultType() MethodDeclarator() [ Block() ]
    for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
      if (node.jjtGetChild(i) instanceof ASTMethodDeclarator) {
        return node.jjtGetChild(i).jjtAccept(this, data);
      }
    }
    return "";
  }
  public Object visit(ASTAspectRefineMethodDeclarationLookahead node, Object data) {
    return "";
  }
  public Object visit(ASTAspectRefineMethodDeclaration node, Object data) {
    // signature = ResultType [ TypeParameters() ] MethodDeclarator() Block()
    // Look for the method declarator:
    for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
      if (node.jjtGetChild(i) instanceof org.jastadd.jrag.AST.ASTMethodDeclarator) {
        return node.jjtGetChild(i).jjtAccept(this, data);
      }
    }
    return "<unknown refinement target signature>";
  }
  public Object visit(ASTAspectConstructorDeclaration node, Object data) {
    // AspectConstructorDeclaration = FormalParameters
    return "#constructor#" + node.jjtGetChild(0).jjtAccept(this, data);
  }
  public Object visit(ASTAspectRefineConstructorDeclaration node, Object data) {
    // AspectRefineConstructorDeclaration = FormalParameters
    return "#constructor#" + node.jjtGetChild(0).jjtAccept(this, data);
  }
  public Object visit(ASTAspectFieldDeclarationLookahead node, Object data) {
    return "";
  }
  public Object visit(ASTAspectFieldDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTAspectSynAttributeDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTAspectInhAttributeDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTAspectRewrite node, Object data) {
    return "";
  }
  public Object visit(ASTAspectSynEquation node, Object data) {
    return "";
  }
  public Object visit(ASTAspectRefineSynEquation node, Object data) {
    return "";
  }
  public Object visit(ASTAspectInhEquation node, Object data) {
    return "";
  }
  public Object visit(ASTAspectRefineInhEquation node, Object data) {
    return "";
  }
  public Object visit(ASTCollectionAttribute node, Object data) {
    return "";
  }
  public Object visit(ASTCollectionContribution node, Object data) {
    return "";
  }
  public Object visit(ASTAspectAddInterface node, Object data) {
    return "";
  }
  public Object visit(ASTAspectExtendInterface node, Object data) {
    return "";
  }
  public Object visit(ASTClassDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTTypeNameList node, Object data) {
    return "";
  }
  public Object visit(ASTUnmodifiedClassDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTAspectEnumDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTUnmodifiedEnumDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTEnumBody node, Object data) {
    return "";
  }
  public Object visit(ASTEnumConstant node, Object data) {
    return "";
  }
  public Object visit(ASTTypeParameters node, Object data) {
    return "";
  }
  public Object visit(ASTTypeParameter node, Object data) {
    return "";
  }
  public Object visit(ASTTypeBound node, Object data) {
    return "";
  }
  public Object visit(ASTClassBody node, Object data) {
    return "";
  }
  public Object visit(ASTClassBodyDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTInterfaceDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTUnmodifiedInterfaceDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTInterfaceMemberDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTFieldDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTVariableDeclarator node, Object data) {
    return "";
  }
  public Object visit(ASTVariableDeclaratorId node, Object data) {
    return "";
  }
  public Object visit(ASTVariableInitializer node, Object data) {
    return "";
  }
  public Object visit(ASTArrayInitializer node, Object data) {
    return "";
  }
  public Object visit(ASTMethodDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTMethodDeclarator node, Object data) {
    return ((String) node.jjtGetChild(0).jjtAccept(this, data)) +
      ((String) node.jjtGetChild(1).jjtAccept(this, data));
  }
  public Object visit(ASTFormalParameters node, Object data) {
    StringBuffer s = new StringBuffer();
    for(int i = 0; i < node.jjtGetNumChildren(); i++)
      s.append(node.jjtGetChild(i).jjtAccept(this, data));
    return s.toString();
  }
  public Object visit(ASTFormalParameter node, Object data) {
    // FormalParameter = Type VariableDeclaratorId
    return "_" + Unparser.unparse((SimpleNode) node.jjtGetChild(0));
  }
  public Object visit(ASTConstructorDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTExplicitConstructorInvocation node, Object data) {
    return "";
  }
  public Object visit(ASTInitializer node, Object data) {
    return "";
  }
  public Object visit(ASTType node, Object data) {
    return "";
  }
  public Object visit(ASTReferenceType node, Object data) {
    return "";
  }
  public Object visit(ASTClassOrInterfaceType node, Object data) {
    return "";
  }
  public Object visit(ASTTypeArguments node, Object data) {
    return "";
  }
  public Object visit(ASTTypeArgument node, Object data) {
    return "";
  }
  public Object visit(ASTWildcardBounds node, Object data) {
    return "";
  }
  public Object visit(ASTPrimitiveType node, Object data) {
    return "";
  }
  public Object visit(ASTResultType node, Object data) {
    return "";
  }
  public Object visit(ASTName node, Object data) {
    return "";
  }
  public Object visit(ASTNameList node, Object data) {
    return "";
  }
  public Object visit(ASTAttributeName node, Object data) {
    return "";
  }
  public Object visit(ASTJavaIdentifier node, Object data) {
    return node.firstToken.image.trim();
  }
  public Object visit(ASTAspectType node, Object data) {
    return "";
  }
  public Object visit(ASTAspectReferenceType node, Object data) {
    return "";
  }
  public Object visit(ASTAspectResultType node, Object data) {
    return "";
  }
  public Object visit(ASTAspectClassOrInterfaceType node, Object data) {
    return "";
  }
  public Object visit(ASTExpression node, Object data) {
    return "";
  }
  public Object visit(ASTAssignmentOperator node, Object data) {
    return "";
  }
  public Object visit(ASTConditionalExpression node, Object data) {
    return "";
  }
  public Object visit(ASTConditionalOrExpression node, Object data) {
    return "";
  }
  public Object visit(ASTConditionalAndExpression node, Object data) {
    return "";
  }
  public Object visit(ASTInclusiveOrExpression node, Object data) {
    return "";
  }
  public Object visit(ASTExclusiveOrExpression node, Object data) {
    return "";
  }
  public Object visit(ASTAndExpression node, Object data) {
    return "";
  }
  public Object visit(ASTEqualityExpression node, Object data) {
    return "";
  }
  public Object visit(ASTInstanceOfExpression node, Object data) {
    return "";
  }
  public Object visit(ASTRelationalExpression node, Object data) {
    return "";
  }
  public Object visit(ASTShiftExpression node, Object data) {
    return "";
  }
  public Object visit(ASTAdditiveExpression node, Object data) {
    return "";
  }
  public Object visit(ASTMultiplicativeExpression node, Object data) {
    return "";
  }
  public Object visit(ASTUnaryExpression node, Object data) {
    return "";
  }
  public Object visit(ASTPreIncrementExpression node, Object data) {
    return "";
  }
  public Object visit(ASTPreDecrementExpression node, Object data) {
    return "";
  }
  public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
    return "";
  }
  public Object visit(ASTCastLookahead node, Object data) {
    return "";
  }
  public Object visit(ASTPostfixExpression node, Object data) {
    return "";
  }
  public Object visit(ASTCastExpression node, Object data) {
    return "";
  }
  public Object visit(ASTPrimaryExpression node, Object data) {
    return "";
  }
  public Object visit(ASTMemberSelector node, Object data) {
    return "";
  }
  public Object visit(ASTPrimaryPrefix node, Object data) {
    return "";
  }
  public Object visit(ASTPrimarySuffix node, Object data) {
    return "";
  }
  public Object visit(ASTLiteral node, Object data) {
    return "";
  }
  public Object visit(ASTBooleanLiteral node, Object data) {
    return "";
  }
  public Object visit(ASTNullLiteral node, Object data) {
    return "";
  }
  public Object visit(ASTArguments node, Object data) {
    return "";
  }
  public Object visit(ASTArgumentList node, Object data) {
    return "";
  }
  public Object visit(ASTAllocationExpression node, Object data) {
    return "";
  }
  public Object visit(ASTArrayDimsAndInits node, Object data) {
    return "";
  }
  public Object visit(ASTStatement node, Object data) {
    return "";
  }
  public Object visit(ASTAssertStatement node, Object data) {
    return "";
  }
  public Object visit(ASTLabeledStatement node, Object data) {
    return "";
  }
  public Object visit(ASTBlock node, Object data) {
    return "";
  }
  public Object visit(ASTBlockStatement node, Object data) {
    return "";
  }
  public Object visit(ASTLocalVariableDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTEmptyStatement node, Object data) {
    return "";
  }
  public Object visit(ASTStatementExpression node, Object data) {
    return "";
  }
  public Object visit(ASTSwitchStatement node, Object data) {
    return "";
  }
  public Object visit(ASTSwitchLabel node, Object data) {
    return "";
  }
  public Object visit(ASTIfStatement node, Object data) {
    return "";
  }
  public Object visit(ASTWhileStatement node, Object data) {
    return "";
  }
  public Object visit(ASTDoStatement node, Object data) {
    return "";
  }
  public Object visit(ASTForStatement node, Object data) {
    return "";
  }
  public Object visit(ASTForInit node, Object data) {
    return "";
  }
  public Object visit(ASTStatementExpressionList node, Object data) {
    return "";
  }
  public Object visit(ASTForUpdate node, Object data) {
    return "";
  }
  public Object visit(ASTBreakStatement node, Object data) {
    return "";
  }
  public Object visit(ASTContinueStatement node, Object data) {
    return "";
  }
  public Object visit(ASTReturnStatement node, Object data) {
    return "";
  }
  public Object visit(ASTThrowStatement node, Object data) {
    return "";
  }
  public Object visit(ASTSynchronizedStatement node, Object data) {
    return "";
  }
  public Object visit(ASTTryStatement node, Object data) {
    return "";
  }
  public Object visit(ASTRUNSIGNEDSHIFT node, Object data) {
    return "";
  }
  public Object visit(ASTRSIGNEDSHIFT node, Object data) {
    return "";
  }
  public Object visit(ASTAnnotation node, Object data) {
    return "";
  }
  public Object visit(ASTNormalAnnotation node, Object data) {
    return "";
  }
  public Object visit(ASTMarkerAnnotation node, Object data) {
    return "";
  }
  public Object visit(ASTSingleMemberAnnotation node, Object data) {
    return "";
  }
  public Object visit(ASTMemberValuePairs node, Object data) {
    return "";
  }
  public Object visit(ASTMemberValuePair node, Object data) {
    return "";
  }
  public Object visit(ASTMemberValue node, Object data) {
    return "";
  }
  public Object visit(ASTMemberValueArrayInitializer node, Object data) {
    return "";
  }
  public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTAnnotationTypeBody node, Object data) {
    return "";
  }
  public Object visit(ASTAnnotationTypeMemberDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTDefaultValue node, Object data) {
    return "";
  }
  public Object visit(ASTAspectCacheDeclaration node, Object data) {
    return "";
  }
  public Object visit(ASTLambdaExpression node, Object data) {
    return "";
  }
  public Object visit(ASTLambdaParameters node, Object data) {
    return "";
  }
  public Object visit(ASTTypedLambdaParameters node, Object data) {
    return "";
  }
  public Object visit(ASTUntypedLambdaParameters node, Object data) {
    return "";
  }
}
