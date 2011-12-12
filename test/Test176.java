package test;

import java.util.*;
import test.ast.*;

public class Test176 {

  public static void main(String[] args) {

    A a = new A("a");

    // Activate rewrite
    a.a();
    
    System.out.println("-- Dependencies/Cache after a.a:");
    a.dumpDependencies();
    a.dumpCachedValues();

    // Change
    a.setName("b");
  
    System.out.println("-- Dependencies/Cache after a.setName:");
    a.dumpDependencies();
    a.dumpCachedValues();
  
  }
}
