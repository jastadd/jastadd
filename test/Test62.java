package test;
import test.ast.*;
public class Test62 {

	public static void main(String[] args) {
		System.out.println("Circular: two mutually circular paramterized attributes");
		A root  = new A();
		System.out.println("a(\"x\") == true: " + (root.a("x") == true));
		System.out.println("a(1) == 1: " + (root.a(1) == 1));

	}

}
