package graph.directed.abs;

import graph.directed.SDGVertex;

public class BorderEdge {
	private int fromScc, toScc;
	private SDGVertex fromVertex, toVertex;
	
	public BorderEdge(SDGVertex from, SDGVertex to) {
		this.setFromVertex(from);
		this.setToVertex(to);
		this.fromScc = this.getFromVertex().getSccMark();
		this.toScc = this.getToVertex().getSccMark();
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
}
