package test;

import java.util.*;
import test.ast.*;
public class Test71 {

  public static void main(String[] args) {
    // testing collection attributes
    A a = new A(new test.ast.List().add(new B()).add(new B()));
    for(B b : a.set())
      System.out.println("Found a B");
  }
}
