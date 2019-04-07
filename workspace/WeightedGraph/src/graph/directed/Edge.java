package graph.directed;

public class Edge {
	
	private DGVertex from, to;
	// TODO generalize int type
	private int weight;
	
	public Edge(DGVertex f, DGVertex t, int w) {
		this.from = f;
		this.to= t;
		this.setWeight(w);
	}
	
	//getters and setters
	public DGVertex getFrom() {
		return this.from;
	}
	
	public void setFrom(DGVertex from) {
		this.from = from;
	}
	
	public DGVertex getTo() {
		return this.to;
	}
	
	public void setTo(DGVertex to) {
		this.to = to;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
