package test;
import test.ast.*;
public class Test42 {
  
  public static void main(String[] args) {
    System.out.println("Intertype declarations: implement interface");
    Object o = new A();
    System.out.println("Object implements B: " + (o instanceof B));
  }
}
