package graph.directed;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

public class DGPath {
	// path p on a directed weighted graph is a sequence:
	// p = p1 p2 p3...pk
	// where for each (pi, p_{i+1}), there is a edge from pi to p_{i+1}
	
	private List<DGVertex> path;
	private int drop;
	private int weight;
	
	public DGPath(DGVertex s) {
		path = new ArrayList<DGVertex>();
		path.add(s);
		this.setDrop(0);
		this.setWeight(0);
	}
	
	// basic operations
	
	public DGVertex getLastVertex() {
		return this.path.get(this.length());
	}
	
	public DGVertex getVertex(int index) {
		return this.getPath().get(index);
	}
	
	public void concatVertex(DGVertex v) {
		DGVertex last = this.getLastVertex();
		for(DGEdge edge : last.getEdges()) {
			if(edge.getTo().getIndex() == v.getIndex()) {
				this.getPath().add(v);
				this.setWeight(this.getWeight() + edge.getWeight());
				this.setDrop(Math.min(this.getWeight(), this.getDrop()));
				return;
			}
		}
		assert(false);
		System.out.println("ERROR: Path Concat Not Valid: " + last.getIndex() + " to " + v.getIndex());
	}
	
	public void concatPath(DGPath path) {
		assert(this.getLastVertex() == path.getVertex(0));
		for(int i = 1; i <= this.length(); i++) {
			this.concatVertex(path.getVertex(i));
		}
	}

	public void removeLastVertex() {
		this.setWeight(this.getWeight() - 
					   this.getSubpath(this.length() - 1, this.length()).getWeight());
		this.getPath().remove(this.getPath().size() - 1);
		this.updateDrop();
		
	}
	
	public boolean contains(DGVertex v) {
		for(DGVertex ve : this.getPath()) {
			if(ve == v) {
				return true;
			}
		}
		return false;
	}	
	
	public boolean isCycle() {
		if(this.getVertex(0) == this.getLastVertex() && this.length() > 0) {
			return true;
		}
		return false;
	}
	
	public boolean isPosCycle() {
		if(this.isCycle() && this.getWeight() > 0) {
			return true;
		}
		return false;
	}
	
	public int getVertexIndex(DGVertex vertex) {
		for(int i = 0; i <= this.length(); i ++) {
			if(this.getVertex(i) == vertex) {
				return i;
			}
		}
		System.out.println("ERROR: Vertex not in the path");
		System.out.checkError();
		return -1;
	}
	
	public int length() {
		return this.getPath().size() - 1;
	}
	
	//TODO debug
	public DGPath getSubpath(int startIndex, int endIndex) {
		assert(startIndex >= 0 && endIndex <= this.length() && startIndex <= endIndex);
		DGPath p = new DGPath(this.getVertex(startIndex));
		for(int i = startIndex + 1; i <= endIndex; i++) {
			p.concatVertex(this.getVertex(i));
		}
		return p;
	}
	
	private void updateDrop() {
		int s = 0;
		int minWeight = 0;
		for(int e = 0; e <= this.length(); e ++) {
			DGPath p = this.getSubpath(s, e);
			minWeight = Math.min(p.getWeight(), minWeight);
		}
		this.setDrop(minWeight);
	}
	
	// output 
	public void printPath() {
		System.out.print("Path: ");
		for(DGVertex v : this.getPath()) {
			System.out.print(v.getIndex());
		}
		System.out.println();
	}
	
	// getters and setters
	public List<DGVertex> getPath() {
		return this.path;
	}

	public void setPath(List<DGVertex> path) {
		this.path = path;
	}

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