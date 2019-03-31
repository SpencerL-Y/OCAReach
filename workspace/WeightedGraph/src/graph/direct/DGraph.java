package graph.direct;

import java.util.ArrayList;
import java.util.List;

public class DGraph {
	private List<Vertex> vertices;
	private int startVertexIndex;
	public DGraph() {
		this.setVertices(new ArrayList<Vertex>());
		this.setStartVertexIndex(0);
	}
	
	public void addVertex(int index) {
		for(Vertex v : this.getVertices()) {
			if(v.getIndex() == index) {
				System.out.println("ERROR: Index repeat");
			}
		}
		this.vertices.add(new Vertex(index));
	}
	
	public void delVertex(int index) {
		for(Vertex v : this.getVertices()) {
			if(v.getIndex() == index) {
				this.getVertices().remove(v);
				return;
			}
		}
		System.out.println("ERROR: Vertex not exist");
	}
	
	public Vertex getVertex(int index) {
		for(Vertex v : this.getVertices()) {
			if(v.getIndex() == index) {
				return v;
			}
		}
		System.out.println("ERROR: Vertex not found");
		return null;
	}
	
	public void setVertex(int index, Vertex v) {
		for(Vertex ve : this.getVertices()) {
			if(ve.getIndex() == index) {
				ve = v;
			}
		}
	}
	
	public void addEdge(int fromIndex, int toIndex, int weight) {
		if(this.checkEdge(fromIndex, toIndex)) {
			System.out.println("ERROR: Add edge error, Edge already exists");
			return;
		} 
		this.getVertex(fromIndex).addEdge(this.getVertex(toIndex), weight);
	}
	
	public void delEdge(int fromIndex, int toIndex) {
		if(!this.checkEdge(fromIndex, toIndex)) {
			System.out.println("ERROR: Del edge error, edge does not exists");
			return;
		}
		this.getVertex(fromIndex).delEdge(toIndex);
	}
	
	private Boolean checkEdge(int fromIndex, int toIndex) {
		return this.getVertex(fromIndex).checkEdge(toIndex);
	}
	
	public Path dfs(int vertexIndex) {
		return this.getVertex(this.getStartVertexIndex()).dfs(this.getVertex(vertexIndex));
	}
	
	
	//getters and setters
	public List<Vertex> getVertices() {
		return vertices;
	}
	
	public void setVertices(List<Vertex> vertices) {
		this.vertices = vertices;
	}

	public int getStartVertexIndex() {
		return startVertexIndex;
	}

	public void setStartVertexIndex(int startVertexIndex) {
		this.startVertexIndex = startVertexIndex;
	}
}
