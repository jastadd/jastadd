package test;

import test.ast.*;

public class Test16 {
  public static void main(String[] args) {
    System.out.println("Syn: synthesized attribute with initializing equation");
    Node node = new Node();
    System.out.println("Attribute value: " + node.attr());
  }
}
