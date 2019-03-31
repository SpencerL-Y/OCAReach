package graph.directed;

import java.util.ArrayList;
import java.util.List;

public class Path {
	// path p on a directed weighted graph is a sequence:
	// p = p1 p2 p3...pk
	// where for each (pi, p_{i+1}), there is a edge from pi to p_{i+1}
	
	private List<Vertex> path;
	public Path(Vertex s) {
		path = new ArrayList<Vertex>();
		path.add(s);
	}
	
	public Vertex getLastVertex() {
		return this.path.get(this.path.size() - 1);
	}
	
	public void concatVertex(Vertex v) {
		Vertex last = this.getLastVertex();
		for(Edge edge : last.getEdges()) {
			if(edge.getTo().getIndex() == v.getIndex()) {
				this.getPath().add(v);
				return;
			}
		}
		System.out.println("Path Concat Not Valid");
	}

	public void removeLastVertex() {
		this.getPath().remove(this.getPath().size() - 1);
	}
	
	public boolean contains(Vertex v) {
		for(Vertex ve : this.getPath()) {
			if(ve == v) {
				return true;
			}
		}
		return false;
	}
	
	//Algorithms
	
	public int getWeightOfPath() {
		//TODO
		return 0;
	}
	
	public int getDropOfPath() {
		//TODO
		return 0;
	}
	
	// output 
	public void printPath() {
		System.out.print("Path: ");
		for(Vertex v : this.getPath()) {
			System.out.print(v.getIndex() + " ");
		}
	}
	
	// getters and setters
	public List<Vertex> getPath() {
		return this.path;
	}

	public void setPath(List<Vertex> path) {
		this.path = path;
	}
}
