package test;
import java.io.*;
import java.util.*;
import test.ast.*;
public class Test126 {

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
    a.getChildNoTransform(0).dumpDependencies();
    b1.dumpDependencies();
    b2.dumpDependencies();
    a.dumpCachedValues();
    a.getChildNoTransform(0).dumpCachedValues();
    b1.dumpCachedValues();
    b2.dumpCachedValues();

    a.getChild(0).removeChild(0);
    b2 = a.getB(0);
  
    System.out.println("Dependencies/Cache after a.removeChild(0):");
    a.dumpDependencies();
    a.getChildNoTransform(0).dumpDependencies();
    b2.dumpDependencies();
    a.dumpCachedValues();
    a.getChildNoTransform(0).dumpCachedValues();
    b2.dumpCachedValues();
    //System.out.println("Dependencies/Cache in removed child:");
    //b1.dumpDependencies();
    //b1.dumpCachedValues();
    
  
  }
}
