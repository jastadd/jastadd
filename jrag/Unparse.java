package jrag;

import ast.AST.*;

import jrag.AST.*;
import jrag.AST.Token;
import jrag.AST.SimpleNode;

import java.util.Set;
import java.util.LinkedHashSet;

public aspect Unparse {
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
}
