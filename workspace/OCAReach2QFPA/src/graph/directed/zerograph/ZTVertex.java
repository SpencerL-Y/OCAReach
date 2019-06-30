package graph.directed.zerograph;

import java.util.List;

public class ZTVertex {
	private int index;
	private int from;
	private int to;
	private List<ZTEdge> edges;
	
	public ZTVertex(int index, int fromStateIndex, int toStateIndex) {
		this.setIndex(index);
		this.setFrom(fromStateIndex);
		this.setTo(toStateIndex);
	}

	// setters and getters
	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public List<ZTEdge> getEdges() {
		return edges;
	}

	public void setEdges(List<ZTEdge> edges) {
		this.edges = edges;
	}
	
	
}
