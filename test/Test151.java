package test;
import java.io.*;
import java.util.*;
import test.ast.*;

public class Test151 {

  public static void main(String[] args) {
    
    A a = new A("b");
    
    a.a0();
    a.a1();

    System.out.println("Dependencies:");
    a.dumpDependencies();
    System.out.println("Cached values:");
    a.dumpCachedValues();
    
  }
}
