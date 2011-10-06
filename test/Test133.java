package test;

import java.util.*;
import test.ast.*;

public class Test133 {

  public static void main(String[] args) {


    A a = new A(new test.ast.List().add(new B()).add(new B()));

    // Trigger rewrite
    B b = a.getB(0);
    
    System.out.println("-- Dependencies/Cache after a.getB(0):");
    a.dumpDependencies();
    a.dumpCachedValues();
    b.dumpDependencies();
    b.dumpCachedValues();

    // Change
    a.addChild(new C());
  
    System.out.println("-- Dependencies/Cache after a.addChild(new C()):");
    a.dumpDependencies();
    a.dumpCachedValues();
    b.dumpDependencies();
    b.dumpCachedValues();

  }
}
