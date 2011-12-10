package test;

import java.util.*;
import test.ast.*;

public class Test162 {

  public static void main(String[] args) {

    A a = new A(new B());

    // Compute NTAs
    System.out.println("computing a.d()");
    a.d();
    System.out.println("computing a.makeB()");
    B b = a.makeB();
    System.out.println("computing b.a()");
    b.a();
    
    System.out.println("-- Dependencies/Cache after a.makeB and b.a:");
    a.dumpDependencies();
    a.dumpCachedValues();
    b.dumpDependencies();
    b.dumpCachedValues();

    // Change
    a.setName("b");
  
    System.out.println("-- Dependencies/Cache after a.setName:");
    a.dumpDependencies();
    a.dumpCachedValues();
    b.dumpDependencies();
    b.dumpCachedValues();
  
  }
}
