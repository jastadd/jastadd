package test;

import test.ast.*;

public class Test25 {
  
  public static void main(String[] args) {
    System.out.println("Inh: inherited attribute with equation for optionals");
    B b = new B();
    B mayb = new B();
    A a = new A(b, new Opt(mayb));
    System.out.println(b.value());
    System.out.println(mayb.value());
  }
}
