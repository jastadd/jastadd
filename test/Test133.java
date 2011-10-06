package test;

import java.util.*;
import test.ast.*;

public class Test133 {

  public static void main(String[] args) {


    A a = new A(new test.ast.List().add(new B()).add(new B()));

    // Trigger rewrite
    B b1 = a.getB(0);
    B b2 = a.getB(1);
    C c = (C)a.getB(2);
    
    System.out.println("-- Dependencies/Cache after a.getB(0):");
    a.dumpDependencies();
    a.dumpCachedValues();
    b1.dumpDependencies();
    b1.dumpCachedValues();
    b2.dumpDependencies();
    b2.dumpCachedValues();
    c.dumpDependencies();
    c.dumpCachedValues();


    // Change
    C cNew = new C();
    System.out.println("cNew=" + cNew);
    a.addB(cNew);
    b1 = a.getB(0);
    b2 = a.getB(1);
    c = (C)a.getB(2);
    System.out.println("c=" + c + ", cNew=" + cNew);
  
    System.out.println("-- Dependencies/Cache after a.addChild(new C()):");
    a.dumpDependencies();
    a.dumpCachedValues();
    b1.dumpDependencies();
    b1.dumpCachedValues();
    b2.dumpDependencies();
    b2.dumpCachedValues();
    c.dumpDependencies();
    c.dumpCachedValues();

  }
}
