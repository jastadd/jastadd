package test;
import test.ast.*;
public class Test63 {

	public static void main(String[] args) {
		System.out.println("Circular: two mutually circular paramterized attributes");
		A root  = new A();
		try{
			System.out.println("a(\"x\") == 3: " + (root.a("x") == 3));
			System.out.println("a(1) == 3: " + (root.a(1) == 3));
		}
		catch (RuntimeException e) {
			System.out.println("RuntimeException!");
		}
	}
}
