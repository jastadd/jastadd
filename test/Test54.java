package test;
import test.ast.*;
public class Test54 {
  public static void main(String[] args) {
    System.out.println("Circular: a self-circular inherited attribute");
    B b = new B();
    A a = new A(b);
    System.out.println("t() == false: " + (b.t() == false));
  }
}
