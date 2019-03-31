package graph.direct;

public class Edge {
	
	private Vertex from, to;
	// TODO generalize int type
	private int weight;
	
	public Edge(Vertex f, Vertex t, int w) {
		this.from = f;
		this.to= t;
		this.setWeight(w);
	}
	
	//getters and setters
	public Vertex getFrom() {
		return this.from;
	}
	
	public void setFrom(Vertex from) {
		this.from = from;
	}
	
	public Vertex getTo() {
		return this.to;
	}
	
	public void setTo(Vertex to) {
		this.to = to;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
