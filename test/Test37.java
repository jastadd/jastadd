package test;

import test.ast.*;

public class Test37 {

  public static void main(String[] args) {
    System.out.println("NTA: check that NTA is not included in generic traversal");
    A a = new A(new B());
    for(int i = 0; i < a.getNumChild(); i++)
      System.out.println(((B)a.getChild(i)).value());
    System.out.println(a.getNTAB().value());
  }
}
