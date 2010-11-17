package test;

import test.ast.*;

public class Test3 {
	public static void main(String[] args) {
    System.out.println("AST: implicit ASTNode inheritance");
    Node n = new Node();
    System.out.println(n.getNumChild());
  }
}
