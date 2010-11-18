package test;
import test.ast.*;
public class Test66 {

  public static void main(String[] args) {
    // Refinement of inh equations that uses refined() to invoke old implementation
    System.out.println(new X(new Y()).getY().m());
  }
}
