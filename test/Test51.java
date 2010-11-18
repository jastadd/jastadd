package test;
import test.ast.*;
public class Test51 {

  public static void main(String[] args) {
    System.out.println("Circular: self circular synthesized attribute");
    A node = new A();
    System.out.println(node.a() == false);
  }
}
