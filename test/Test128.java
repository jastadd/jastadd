package test;

import java.util.*;
import test.ast.*;

public class Test128 {

  public static void main(String[] args) {

    A a = new A(new B(), "a");

    // Activate rewrite
//    B bOld = a.getBNoTransform();
    B b = a.getB();
    
    System.out.println("Dependencies/Cache after rewrite:");
    a.dumpDependencies();
    b.dumpDependencies();
    a.dumpCachedValues();
    b.dumpCachedValues();

  //  System.out.println("Dependencies/Cache in initial rewritten child:");
  //  bOld.dumpDependencies();
  //  bOld.dumpCachedValues();

/*
    // Change a dependency of the rewrite condition
    a.setName("b");
  
    System.out.println("Dependencies/Cache after setName:");

    a.dumpDependencies();
    b.dumpDependencies();
    a.dumpCachedValues();
    b.dumpCachedValues();

    System.out.println("Dependencies/Cache in initial child:");
    bOld.dumpDependencies();
    bOld.dumpCachedValues();
*/  
  }
}
