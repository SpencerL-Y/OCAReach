package graph.direct;

import java.util.LinkedList;
import java.util.List;

public class Vertex {
	private List<Edge> edges;
	private int index;
	public Vertex(int i) {
		this.setEdges(new LinkedList<Edge>());
		this.setIndex(i);
	}
	
	public void addEdge(Vertex toVertex, int weight) {
		Edge e = new Edge(this, toVertex, weight);
		this.addEdge(e);
	}
	
	private void addEdge(Edge e) {
		for(int i = 0; i < this.getEdges().size(); i ++) {
			if(this.getEdges().get(i).getTo().getIndex() > e.getTo().getIndex()) {
				// making the list of edges in order regarding to the edge index
				this.getEdges().add(i, e);
			}
		}
		this.getEdges().add(e);
	}
	
	public void delEdge(int toIndex) {
		for(Edge e : this.getEdges()){
			if(e.getTo().getIndex() == toIndex) {
				this.getEdges().remove(e);
				return;
			}
		}
	}
	
	public Boolean checkEdge(int toIndex) {
		for(Edge e : this.getEdges()) {
			if(e.getTo().getIndex() == toIndex) {
				return true;
			}
		}
		return false;
	}
	
	//Algorithm 
	public Path dfs(Vertex v) {
		Path p = new Path(this);
		Boolean flag = false;
		this.dfsRecur(v, p, flag);
		return p;
	}
	
	//TODO: debug
	private void dfsRecur(Vertex v, Path path, Boolean flag) {
		if(path.getLastVertex() == v || flag == true) {
			// stop searching for already found
			flag = true;
			return;
		}
		for(Edge e : path.getLastVertex().getEdges()) {
			if(!(path.contains(e.getTo()))) {
				path.concatVertex(e.getTo());
				e.getTo().dfsRecur(v, path, flag);
			} else {
				//del
				path.removeLastVertex();
				return;
			}
		}
		//del
		path.removeLastVertex();
		return;
	}
	
	//getters and setters
	public List<Edge> getEdges() {
		return edges;
	}
	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	
}
