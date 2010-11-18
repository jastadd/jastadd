package test;

import test.ast.*;

public class Test36 {

  public static void main(String[] args) {
    System.out.println("NTA: non terminal attribute implemented using syn eq");
    A a = new A(new B());
    System.out.println(a.getB().value());
    System.out.println(a.getNTAB().value());
  }
}
