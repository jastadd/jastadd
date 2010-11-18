package test;
import test.ast.*;
public class Test53 {
  public static void main(String[] args) {
    System.out.println("Circular: two mutually circular attributes that may require an iteration before termination");
    A node = new A();
    System.out.println("x() == true: " + (node.x() == true));
    System.out.println("y() == true: " + (node.y() == true));
  }
}
