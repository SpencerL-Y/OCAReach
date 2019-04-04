package automata.counter;

public enum OCAOp {
	Add,
	Sub,
	Zero;
	
	public static OCAOp parseOp(String str) {
		if(str.equals("sub")) {
			return Sub;
		} else if(str.equals("add")) {
			return Add;
		} else if(str.equals("zero")) {
			return Zero; 
		} else {
			return null;
		}
	}
}
