package test;

import test.ast.*;

public class Test21 {

	public static void main(String[] args) {
		System.out.println("Inh: inherited attribute with single equation");
		B b = new B();
		A a = new A(b);
		System.out.println(b.value());
	}
}
