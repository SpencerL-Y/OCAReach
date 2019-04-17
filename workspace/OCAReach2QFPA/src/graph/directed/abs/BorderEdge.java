package graph.directed.abs;

import graph.directed.DGEdge;
import graph.directed.SDGVertex;

public class BorderEdge {
	private int fromScc, toScc;
	private SDGVertex fromVertex, toVertex;
	private int weight;
	
	public BorderEdge(SDGVertex from, SDGVertex to) {
		this.setFromVertex(from);
		this.setToVertex(to);
		this.fromScc = this.getFromVertex().getSccMark();
		this.toScc = this.getToVertex().getSccMark();
		for(DGEdge e : from.getEdges()) {
			if(e.getTo().getIndex() == to.getVertexIndex()) {
				this.weight = e.getWeight();
			}
		}
	}
	
	//setters and getters
	public int getFromScc() {
		return fromScc;
	}

	public void setFromScc(int fromScc) {
		this.fromScc = fromScc;
	}

	public int getToScc() {
		return toScc;
	}

	public void setToScc(int toScc) {
		this.toScc = toScc;
	}

	public SDGVertex getToVertex() {
		return toVertex;
	}

	public void setToVertex(SDGVertex toVertex) {
		this.toVertex = toVertex;
	}

	public SDGVertex getFromVertex() {
		return fromVertex;
	}

	public void setFromVertex(SDGVertex fromVertex) {
		this.fromVertex = fromVertex;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
