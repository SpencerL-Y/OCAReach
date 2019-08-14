package automata.counter;

import automata.Operation;

public class OCAOpUtil {
	public static Operation parseOp(String str) {
		//TODO: GEN add number OCAOp
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
}
