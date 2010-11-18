package test;
import test.ast.*;
public class Test43 {
 
 public static void main(String[] args) {
   System.out.println("Boxing/Unboxing used when caching attribute values");
   A a = new A();
   System.out.println(a.byteValue());
   System.out.println(a.byteValue());
   System.out.println(a.shortValue());
   System.out.println(a.shortValue());
   System.out.println(a.intValue());
   System.out.println(a.intValue());
   System.out.println(a.charValue());
   System.out.println(a.charValue());
   System.out.println(a.longValue());
   System.out.println(a.longValue());
   System.out.println(a.floatValue());
   System.out.println(a.floatValue());
   System.out.println(a.doubleValue());
   System.out.println(a.doubleValue());
   System.out.println(a.booleanValue());
   System.out.println(a.booleanValue());
   System.out.println(a.stringValue());
   System.out.println(a.stringValue());
 }

}
