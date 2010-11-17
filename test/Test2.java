package test;

import test.ast.*;

public class Test2 {
  public static void main(String[] args) {
    System.out.println("AST: explicit ASTNode inheritance");
    Node n = new Node();
    System.out.println(n.getNumChild());
  }
}
