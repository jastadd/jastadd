package test;

import test.ast.*;

public class Test29 {
  public static void main(String[] args) {
    System.out.println("Rewrite: always rewrite node B into new C with common supertype");
    Program p = 
      new Program(
        new B()
      );
    System.out.println(p.getA().value());
  }
}
