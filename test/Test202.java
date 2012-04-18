package test;

import test.ast.*;

public class Test202 {
  public static void main(String[] args) {
    System.out.println("AST: refine interface method");

    Interface node = new Node();
    node.toBeRefined();
  }
}
