package test;

import java.util.*;
import test.ast.*;

public class Test182 {

  public static void main(String[] args) {
    
    C c = new C();
    A a = new A(c);
    c = a.getC();

    // Compute NTAs
    B b = c.c();
    
    System.out.println("-- Dependencies/Cache c.c():");
    a.dumpDependencies();
    a.dumpCachedValues();
    b.dumpDependencies();
    b.dumpCachedValues();
    c.dumpDependencies();
    c.dumpCachedValues();


    // Change
    b.setName("b");
  
    System.out.println("-- Dependencies/Cache after b.setName:");
    a.dumpDependencies();
    a.dumpCachedValues();
    b.dumpDependencies();
    b.dumpCachedValues();
    c.dumpDependencies();
    c.dumpCachedValues();


  }
}
