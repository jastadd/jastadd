package test;
import test.ast.*;
public class Test114 {
  public static void main(String[] args) {
    // Refinement of syn equations that uses refined() to invoke old implementation
    System.out.println(new X(true).getLazy());
  }
}
