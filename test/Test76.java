package test;

import java.util.*;
import test.ast.*;

public class Test76 {
  public static void main(String[] args) {
	  // Circular: Circularly computed hash set
    A a = new A();
    if(a.m() == a.emptyHashSet())
      System.out.println("Found circularly computed hash set");
    if(a.n() == a.emptyHashSet())
      System.out.println("Found circularly computed hash set");
  }
}

