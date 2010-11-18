package test;
import test.ast.*;
public class Test78 {
  public static void main(String[] args) {
	  // Inh: Equation values
    A a = new SubA(new B());
    System.out.println(a.getRight().value());
    System.out.println(a.getLeft().value());
    System.out.println(a.getNumChild());
  }
}
