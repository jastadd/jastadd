package test;

import test.ast.*;

public class Test24 {
  
  public static void main(String[] args) {
    System.out.println("Inh: inherited attribute with equations being overridden in one path only");
    C c1 = new C();
    C c2 = new C();
    B b = new B(c1, c2);
    A a = new A(b);
    System.out.println(c1.value());
    System.out.println(c2.value());
  }
}
