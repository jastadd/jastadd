package test;

import java.util.*;
import test.ast.*;

public class Test129 {

  public static void main(String[] args) {

    D d = new D("a");
    B b = new B(d);
    A a = new A(b, "b");

    // Activate rewrite
    C c = (C)a.getB();
    E e = (E)c.getD();
    
    System.out.println("-- Dependencies/Cache after rewrites:");
    System.out.println("a:");
    a.dumpDependencies();
    a.dumpCachedValues();
    System.out.println("b:");
    b.dumpDependencies();
    b.dumpCachedValues();
    System.out.println("c:");
    c.dumpDependencies();
    c.dumpCachedValues();
    System.out.println("d:");
    d.dumpDependencies();
    d.dumpCachedValues();
    System.out.println("e:");
    e.dumpDependencies();
    e.dumpCachedValues();

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
    System.out.println("d:");
    d.dumpDependencies();
    d.dumpCachedValues();
    System.out.println("e:");
    e.dumpDependencies();
    e.dumpCachedValues();


  
  }
}
