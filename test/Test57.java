package test;
import test.ast.*;
public class Test57 {
  public static void main(String[] args) {
    System.out.println("Circular: avoid re-computation of potentially circular attributes");
    A node = new A();
    System.out.println("a() == true: " + (node.a() == true));
    System.out.println("b() == true: " + (node.b() == true));
    System.out.println("c() == true: " + (node.c() == true));
  }
}
