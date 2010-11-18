package test;

import test.ast.*;

public class Test38 {
 
  public static void main(String[] args) {
    System.out.println("Intertype declarations: introduce method");
    A a = new A();
    a.test();
    a.test("test2");
    System.out.println(a.result());
    A.staticTest();
  }
}
