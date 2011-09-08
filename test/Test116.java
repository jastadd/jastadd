package test;
import test.ast.*;
public class Test116 {
  public static void main(String[] args) {
    // Refinement of syn equations that uses refined() to invoke old implementation
    new X().m();
    System.out.println(new X().n());
  }
}
