package test;

import test.ast.*;

public class Test22 {
  
  public static void main(String[] args) {
    System.out.println("Inh: inherited attribute with multiple equations");
    B b1 = new B();
    B b2 = new B();
    A a = new A(b1, b2);
    System.out.println(b1.value());
    System.out.println(b2.value());
  }
}
