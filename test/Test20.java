package test;

import test.ast.*;

public class Test20 {
  
  public static void main(String[] args) {
    System.out.println("Syn: parameterized synthesized attribute with overriding equation in subclass");
    Node node = new Node();
    System.out.println(node.attr("A"));
    System.out.println(node.attr("B"));
    node = new SubNode();
    System.out.println(node.attr("A"));
    System.out.println(node.attr("B"));
  }
}
