package test;
import test.ast.*;
public class Test111 {
  public static void main(String[] args) {
    // Refinement of method that uses Aspect.Nodeclass.method to invoke old implementation
    new X().m();
    System.out.println(new X().n());
  }
}
