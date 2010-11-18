package test;

import test.ast.*;

public class Test19 {

  public static void main(String[] args) {
    System.out.println("Syn: synthesized attribute in abstract class with overriding equation in subclass");
    Node node = new SubNode();
    System.out.println("Attribute attr: " + node.attr());
  }
}
