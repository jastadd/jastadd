package test;
import test.ast.*;
public class Test55 {
  public static void main(String[] args) {
    System.out.println("Circular: two mutually circular attributes that may require an iteration before termination");
    B b = new B();
    A a = new A(b);
    System.out.println("x() == true: " + (a.x() == true));
    System.out.println("y() == true: " + (b.y() == true));
  }
}
