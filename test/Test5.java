package test;

import test.ast.*;

public class Test5 {
  
  public static void main(String[] args) {
    System.out.println("AST: single token, implicit type");
    Node n = new Node("name");
    System.out.println(n.getName());
    n.setName("changed");
    System.out.println(n.getName());
  }
}
