package java10;

import java.io.IOException;

public class AnonymousClasses {
	
	abstract class MyAbstractClass {
		abstract void read () throws IOException;
	}
	
	public void myMethod() {
		MyAbstractClass myAc = new MyAbstractClass() {
			@Override
			void read() {
			}
			
		};
		
		try {
			myAc.read();
		} catch (IOException e) {
			
		}
	}

}
