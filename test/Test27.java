package test;

import test.ast.*;

public class Test27 {

  public static void main(String[] args) {
    System.out.println("Inh: inherited attribute with equation for lists using index in equations");
    B b1 = new B();
    B b2 = new B();
    List list = new List().add(b1).add(b2);
    A a = new A(list);
    System.out.println(b1.value());
    System.out.println(b2.value());
  }
}
