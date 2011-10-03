package test;

import java.util.*;
import test.ast.*;

public class Test132 {

  public static void main(String[] args) {

    A a = new A(new B());

    // Compute NTAs
    a.d();
    B b = a.makeB();
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
