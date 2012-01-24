package test;
import java.io.*;
import java.util.*;
import test.ast.*;
public class Test120 {

  public static void main(String[] args) {
    
    A a = new A("b");
    
    a.a0();
    a.a1();

    a.a2();
    a.a3();
    a.a4();
    a.a5();
    a.a6();
    a.a7();
    a.a8();

    // dumpDeps
    System.out.println("Dependencies:");
    a.dumpDependencies();
    a.dumpCachedValues();

  }
}
