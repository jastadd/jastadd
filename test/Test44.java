package test;
import test.ast.*;
public class Test44 {

  public static void main(String[] args) {
    System.out.println("Attributes: check that revisited attributes throws an exception");
    B b = new B();
    A a = new A(b);
    try {
      a.a();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    try {
      b.b();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
