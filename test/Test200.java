package test;

import test.ast.*;

public class Test200 {
  public static void main(String[] args) {
    System.out.println("AST: childIndex updates after insertChild & removeChild");

    testInsert();
    testRemove();
  }

  private static void testInsert() {
    Node n = new Node();
    for (int i = 0; i < 10; ++i)
      n.addNode(new Node());
    n.getNodeList().insertChild(new Node(), 5);
    System.out.println(n.getNode(5).childIndex());
    System.out.println(n.getNode(6).childIndex());
  }

  private static void testRemove() {
    Node n = new Node();
    for (int i = 0; i < 10; ++i)
      n.addNode(new Node());
    n.getNodeList().removeChild(6);
    System.out.println(n.getNode(5).childIndex());
    System.out.println(n.getNode(6).childIndex());
  }
}
