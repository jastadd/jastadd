package test;

import test.ast.*;

public class Test35 {
  public static void main(String[] args) {
    System.out.println("LAZY: check that lazy is used for syn and inh decl");
    A a = new A(new B());
    a.is$Final = true;
    B b = a.getB();

    a.syn1();
    a.syn1();
    a.syn2();
    a.syn2();

    b.inh1();
    b.inh1();
    b.inh2();
    b.inh2();
  }
}
