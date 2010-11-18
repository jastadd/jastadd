package test;
import test.ast.*;
public class Test60 {
	
	public static void main(String[] args) {
		System.out.println("60, Circular: inherited attrs, no stack of strongly connected components");
		A a = new A();
		B root = new B(a);
    System.out.println("Order: c1 before c2");
		System.out.println("c1_a() = " + a.c1_a());
		System.out.println("c2_a() = " + a.c2_a());
		System.out.println("c1_acount " + root.c1_acount);
		System.out.println("c1_bcount " + root.c1_bcount);
		System.out.println("c3count " + root.c3count);
		System.out.println("c2_acount " + root.c2_acount);
		System.out.println("c2_bcount " + root.c2_bcount);
    
		a = new A();
		root = new B(a);
    System.out.println("Order: c2 before c1");
		System.out.println("c2_a() = " + a.c2_a());
		System.out.println("c1_a() = " + a.c1_a());
		System.out.println("c1_acount " + root.c1_acount);
		System.out.println("c1_bcount " + root.c1_bcount);
		System.out.println("c3count " + root.c3count);
		System.out.println("c2_acount " + root.c2_acount);
		System.out.println("c2_bcount " + root.c2_bcount);
	}
}
