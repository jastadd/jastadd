package test;

import test.ast.*;

public class Test30 {
  
  public static void main(String[] args) {
    System.out.println("Rewrite: rewrite node B into new C using multiple matching rewrites choosing using lexical order");
    Program p = 
      new Program(
        new B()
      );
    System.out.println(p.getA().value());
  }
}
