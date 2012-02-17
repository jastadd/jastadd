package test;

import java.util.*;
import test.ast.*;

public class Test185 {

  public static void main(String[] args) {

        
    C c = new C();
    c.setID("a");
    Start start = new Start(new Root(new A(c)));
    Root root = start.getRoot();
    A a = root.getA();
    c = a.getC();

    // Compute NTAs
    B b = c.decl();
    D d = b.getD();
    
    System.out.println("-- Dependencies/Cache c.decl():");
    root.dumpDependencies();
    root.dumpCachedValues();
    a.dumpDependencies();
    a.dumpCachedValues();
    b.dumpDependencies();
    b.dumpCachedValues();
    c.dumpDependencies();
    c.dumpCachedValues();
    d.dumpDependencies();
    d.dumpCachedValues();

    // Change
    
    d.setName("b");
  
    System.out.println("-- Dependencies/Cache after root.setName:");
    root.dumpDependencies();
    root.dumpCachedValues();
    a.dumpDependencies();
    a.dumpCachedValues();
    b.dumpDependencies();
    b.dumpCachedValues();
    c.dumpDependencies();
    c.dumpCachedValues();
    d.dumpDependencies();
    d.dumpCachedValues();


    // Compute NTAs
    B b2 = c.decl();
    D d2 = b.getD();    

    System.out.println("-- Dependencies/Cache c.decl():");
    System.out.println("b == b2 : " + (b == b2));
    System.out.println("d == d2 : " + (d == d2));
    root.dumpDependencies();
    root.dumpCachedValues();
    a.dumpDependencies();
    a.dumpCachedValues();
    b.dumpDependencies();
    b.dumpCachedValues();
    c.dumpDependencies();
    c.dumpCachedValues();
    d.dumpDependencies();
    d.dumpCachedValues();


  }
}
