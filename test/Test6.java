package test;

import test.ast.*;

public class Test6 {

  public static void main(String[] args) {
    System.out.println("AST: single token, explicit primitive type");
    Node n = new Node(true);
    System.out.println(n.getValue());
    n.setValue(false);
    System.out.println(n.getValue());
  }
}
