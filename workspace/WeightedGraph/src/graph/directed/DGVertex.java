package graph.directed;

import java.util.LinkedList;
import java.util.List;

public class DGVertex {
	private List<DGEdge> edges;
	private int index;
	
	public DGVertex(int i) {
		this.setEdges(new LinkedList<DGEdge>());
		this.setIndex(i);
	}
	
	public void addEdge(DGVertex toVertex, int weight) {
		DGEdge e = new DGEdge(this, toVertex, weight);
		this.addEdge(e);
	}
	
	private void addEdge(DGEdge e) {
		for(int i = 0; i < this.getEdges().size(); i ++) {
			if(this.getEdges().get(i).getTo().getIndex() > e.getTo().getIndex()) {
				// making the list of edges in order regarding to the edge index
				this.getEdges().add(i, e);
			}
		}
		this.getEdges().add(e);
	}
	
	public void delEdge(int toIndex) {
		for(DGEdge e : this.getEdges()){
			if(e.getTo().getIndex() == toIndex) {
				this.getEdges().remove(e);
				return;
			}
		}
	}
	
	public Boolean checkEdge(int toIndex) {
		for(DGEdge e : this.getEdges()) {
			if(e.getTo().getIndex() == toIndex) {
				return true;
			}
		}
		return false;
	}
	
	//getters and setters
	public List<DGEdge> getEdges() {
		return edges;
	}
	public void setEdges(List<DGEdge> edges) {
		this.edges = edges;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	
}
