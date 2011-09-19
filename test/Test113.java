package test;
import test.ast.*;
public class Test113 {
  public static void main(String[] args) {
    // Refinement of syn equations that uses refined() to invoke old implementation
    new X().m("TEST");
    System.out.println(new X().n(2,3));
  }
}
