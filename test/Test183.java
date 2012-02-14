package test;

import java.util.*;
import test.ast.*;

public class Test183 {

  public static void main(String[] args) {

        
    Root root = new Root("a", new A(new C()));
    Start start = new Start(root);

    A a = root.getA();
    C c = a.getC();

    // Compute NTAs
    B b = c.decl();
    
    System.out.println("-- Dependencies/Cache c.decl():");
    root.dumpDependencies();
    root.dumpCachedValues();
    a.dumpDependencies();
    a.dumpCachedValues();
    b.dumpDependencies();
    b.dumpCachedValues();
    c.dumpDependencies();
    c.dumpCachedValues();


    // Change
    root.setName("b");
  
    System.out.println("-- Dependencies/Cache after root.setName:");
    root.dumpDependencies();
    root.dumpCachedValues();
    a.dumpDependencies();
    a.dumpCachedValues();
    b.dumpDependencies();
    b.dumpCachedValues();
    c.dumpDependencies();
    c.dumpCachedValues();


  }
}
