package automata.counter;

import automata.Operation;

public class OCAOpUtil {
	public static Operation parseOpSimple(String str) {
		if(str.equals("sub")) {
			return OCAOp.Sub;
		} else if(str.equals("add")) {
			return OCAOp.Add;
		} else if(str.equals("zero")) {
			return OCAOp.Zero; 
		} else {
			return null;
		}
	}
	
	public static Integer parseOpGen(String str) {
		int counterChange = Integer.getInteger(str);
		return counterChange;
	}
	
}
