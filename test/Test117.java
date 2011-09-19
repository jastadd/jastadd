package test;

import test.ast.*;

public class Test117 {
  public static void main(String[] args) {
    System.out.println("Syn: synthesized attribute with initializing equation");
    SubNode node = new SubNode();
    System.out.println("Attribute value: " + node.attr(""));
  }
}
