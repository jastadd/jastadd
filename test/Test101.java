package test;

import test.ast.*;

public class Test101 {
  public static void main(String[] args) {
    System.out.println("AST: Semantic error: No root node available");
    Node n = new Node();
    System.out.println(n.getNumChild());
  }
}
