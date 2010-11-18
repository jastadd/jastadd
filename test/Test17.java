package test;

import test.ast.*;

public class Test17 {

  public static void main(String[] args) {
    System.out.println("Syn: synthesized attribute with initializing equation, overriden in subclass");
    Node node = new Node();
    System.out.println("Attribute value: " + node.attr());
    node = new SubNode();
    System.out.println("Attribute value: " + node.attr());
  }
}
