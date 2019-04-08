package table.dwt;

public class DWTuple {
	private int drop;
	private int weight;
	
	public DWTuple(int d, int w) {
		this.setDrop(d);
		this.setWeight(w);
	}
	
	//basic operations
	
	public void printTuple() {
		System.out.println("(" + this.getDrop() + "," + this.getWeight() +  ")");
	}
	
	public void setTuple(int d, int w) {
		this.setDrop(d);
		this.setWeight(w);
	}
	
	//algorithm
	
	public static DWTuple merge(DWTuple former, DWTuple latter) {
		int d = Math.min(former.getDrop(), former.getWeight() + latter.getDrop());
		int w = former.getWeight() + latter.getWeight();
		DWTuple result = new DWTuple(d, w);
		return result;
	}
	
	//setters and getters
	public int getDrop() {
		return drop;
	}
	public void setDrop(int drop) {
		this.drop = drop;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
