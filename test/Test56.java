package test;
import test.ast.*;
public class Test56 {  
  public static void main(String[] args) {
    System.out.println("Circular: two mutually circular parameterized attributes");
    B b = new B();
    A a = new A(b);
    System.out.println("x(a) == true: " + (a.x("a") == true));
    System.out.println("y(a) == true: " + (b.y("a") == true));
    System.out.println("x(b) == true: " + (a.x("b") == true));
    System.out.println("y(b) == true: " + (b.y("b") == true));
  }
}
