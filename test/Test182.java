package test;

import java.util.*;
import test.ast.*;

public class Test182 {

  public static void main(String[] args) {

    A a = new A();

    // Compute NTAs
    List list = a.getBList();
    C c = a.getC();
    D d = a.makeD();
    
    System.out.println("-- Dependencies/Cache after a.makeB and b.a:");
    list.dumpDependencies();
    list.dumpCachedValues();
    c.dumpDependencies();
    c.dumpCachedValues();
    d.dumpDependencies();
    d.dumpCachedValues();

/*
    // Change
    a.setNode(null);
  
    System.out.println("-- Dependencies/Cache after a.setName:");
    a.dumpDependencies();
    a.dumpCachedValues();
    b.dumpDependencies();
    b.dumpCachedValues();
    b1.dumpDependencies();
    b1.dumpCachedValues();
*/
  }
}
