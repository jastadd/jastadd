package test;

import java.util.*;
import test.ast.*;

public class Test178 {

  public static void main(String[] args) {

    B b1 = new B();
    A a = new A(b1);

    // Compute NTAs
    a.d();
    B b = a.makeB();
    b.a();
    
    System.out.println("-- Dependencies/Cache after a.makeB and b.a:");
    a.dumpDependencies();
    a.dumpCachedValues();
    b.dumpDependencies();
    b.dumpCachedValues();
    b1.dumpDependencies();
    b1.dumpCachedValues();

  //  System.out.println("=== DDG === ");
  //  a.dumpDDG();

    // Change
    a.setName("b");
  
    System.out.println("-- Dependencies/Cache after a.setName:");
    a.dumpDependencies();
    a.dumpCachedValues();
    b.dumpDependencies();
    b.dumpCachedValues();
    b1.dumpDependencies();
    b1.dumpCachedValues();

  }
}
