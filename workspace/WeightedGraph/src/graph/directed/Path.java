package graph.directed;

import java.util.ArrayList;
import java.util.List;

public class Path {
	// path p on a directed weighted graph is a sequence:
	// p = p1 p2 p3...pk
	// where for each (pi, p_{i+1}), there is a edge from pi to p_{i+1}
	
	private List<Vertex> path;
	private int drop;
	private int weight;
	private int length;
	
	public Path(Vertex s) {
		path = new ArrayList<Vertex>();
		path.add(s);
		this.setDrop(0);
		this.setLength(0);
		this.setWeight(0);
	}
	
	public Vertex getLastVertex() {
		return this.path.get(this.getLength());
	}
	
	public Vertex getVertex(int index) {
		return this.getPath().get(index);
	}
	
	public void concatVertex(Vertex v) {
		Vertex last = this.getLastVertex();
		for(Edge edge : last.getEdges()) {
			if(edge.getTo().getIndex() == v.getIndex()) {
				this.getPath().add(v);
				this.setWeight(this.getWeight() + edge.getWeight());
				this.setDrop(Math.min(this.getWeight(), this.getDrop()));
				return;
			}
		}
		assert(false);
		System.out.println("ERROR: Path Concat Not Valid");
	}
	
	public void concatPath(Path path) {
		assert(this.getLastVertex() == path.getVertex(0));
		for(int i = 1; i <= this.getLength(); i++) {
			this.concatVertex(path.getVertex(i));
		}
	}

	public void removeLastVertex() {
		this.setWeight(this.getWeight() - 
					   this.getSubpath(this.getLength() - 1, this.getLength()).getWeight());
		this.getPath().remove(this.getPath().size() - 1);
		this.setLength(this.getLength() - 1);
		this.updateDrop();
		
	}
	
	public boolean contains(Vertex v) {
		for(Vertex ve : this.getPath()) {
			if(ve == v) {
				return true;
			}
		}
		return false;
	}	
	
	//TODO debug
	public Path getSubpath(int startIndex, int endIndex) {
		assert(startIndex >= 0 && endIndex <= this.getLength() && startIndex <= endIndex);
		Path p = new Path(this.getVertex(startIndex));
		for(int i = startIndex + 1; i <= endIndex; i++) {
			p.concatVertex(this.getVertex(i));
		}
		return p;
	}
	
	private void updateDrop() {
		int s = 0;
		int minWeight = 0;
		for(int e = 0; e <= this.getLength(); e ++) {
			Path p = this.getSubpath(s, e);
			minWeight = Math.min(p.getWeight(), minWeight);
		}
		this.setDrop(minWeight);
	}
	
	// output 
	public void printPath() {
		System.out.print("Path: ");
		for(Vertex v : this.getPath()) {
			System.out.print(v.getIndex());
		}
		System.out.println();
	}
	
	// getters and setters
	public List<Vertex> getPath() {
		return this.path;
	}

	public void setPath(List<Vertex> path) {
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

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
}
