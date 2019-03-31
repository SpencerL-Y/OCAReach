package table.dwt;

public class DWTuple {
	private int drop;
	private int weight;
	
	public DWTuple(int d, int w) {
		this.setDrop(d);
		this.setWeight(w);
	}
	
	public void printTuple() {
		System.out.println("(" + this.getDrop() + "," + this.getWeight() +  ")");
	}
	
	public void setTuple(int d, int w) {
		this.setDrop(d);
		this.setWeight(w);
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
