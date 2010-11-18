package test;
import test.ast.*;
public class Test64 {
  public static void main(String[] args) {
	  System.out.println("Inh: inherited attribute defined using getChild()");
	  B b = new B();
	  A a = new A(b);
	  System.out.println(b.value());
  }
}
