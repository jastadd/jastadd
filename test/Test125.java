package test;
import java.io.*;
import java.util.*;
import test.ast.*;
public class Test125 {

  public static void main(String[] args) {

    B b1 = new B("b", "a");
    B b2 = new B("a", "b");    
    A a = new A(new test.ast.List().add(b1).add(b2));
    b1 = a.getB(0);
    b2 = a.getB(1);
    
    b1.decl();    
    b2.decl();

    System.out.println("Dependencies/Cache after b1.decl and b2.decl:");
    a.dumpDependencies();
    a.getChild(0).dumpDependencies();
    b1.dumpDependencies();
    b2.dumpDependencies();
    a.dumpCachedValues();
    a.getChild(0).dumpCachedValues();
    b1.dumpCachedValues();
    b2.dumpCachedValues();

    B b3 = new B("c", "b");
    a.getChild(0).addChild(b3);
  
    System.out.println("Dependencies/Cache after a.addChild():");
    a.dumpDependencies();
    a.getChild(0).dumpDependencies();
    b1.dumpDependencies();
    b2.dumpDependencies();
    b3.dumpDependencies();
    a.dumpCachedValues();
    a.getChild(0).dumpCachedValues();
    b1.dumpCachedValues();
    b2.dumpCachedValues();
    b3.dumpCachedValues();
  
  }
}
