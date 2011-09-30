package test;

import java.util.*;
import test.ast.*;

public class Test128 {

  public static void main(String[] args) {

    B b = new B();
    A a = new A(b, "b");

    // Activate rewrite
    C c = (C)a.getB();
    
    System.out.println("-- Dependencies/Cache after rewrite:");
    System.out.println("a:");
    a.dumpDependencies();
    a.dumpCachedValues();
    System.out.println("b:");
    b.dumpDependencies();
    b.dumpCachedValues();
    System.out.println("c:");
    c.dumpDependencies();
    c.dumpCachedValues();

    // Change
    a.setName("b");
  
    System.out.println("-- Dependencies/Cache after setName:");
    System.out.println("a:");
    a.dumpDependencies();
    a.dumpCachedValues();
    System.out.println("b:");
    b.dumpDependencies();
    b.dumpCachedValues();
    System.out.println("c:");
    c.dumpDependencies();
    c.dumpCachedValues();

  
  }
}
