package test;

import test.ast.*;

public class Test32 {

  public static void main(String[] args) {
    System.out.println("Rewrite: rewrite node B into new C using conditional rewrite short form");
    Program p = 
      new Program(
        new B()
      );
    System.out.println(p.getA().value());
  }
}
