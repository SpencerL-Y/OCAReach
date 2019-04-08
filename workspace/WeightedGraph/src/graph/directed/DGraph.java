package graph.directed;

import java.util.ArrayList;
import java.util.List;

public class DGraph implements Graph{
	private List<DGVertex> vertices;
	private int startVertexIndex;
	public DGraph() {
		this.setVertices(new ArrayList<DGVertex>());
		this.setStartVertexIndex(0);
	}
	
	public void addVertex(int index) {
		for(DGVertex v : this.getVertices()) {
			if(v.getIndex() == index) {
				System.out.println("ERROR: Index repeat");
			}
		}
		this.vertices.add(new DGVertex(index));
	}
	
	public void delVertex(int index) {
		for(DGVertex v : this.getVertices()) {
			if(v.getIndex() == index) {
				this.getVertices().remove(v);
				return;
			}
		}
		System.out.println("ERROR: Vertex not exist");
	}
	
	public DGVertex getVertex(int index) {
		for(DGVertex v : this.getVertices()) {
			if(v.getIndex() == index) {
				return v;
			}
		}
		System.out.println("ERROR: Vertex not found");
		return null;
	}
	
	public void setVertex(int index, DGVertex v) {
		for(DGVertex ve : this.getVertices()) {
			if(ve.getIndex() == index) {
				ve = v;
			}
		}
	}
	
	public int size() {
		return this.getVertices().size();
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
	
	
	//Algorithms

	
	//getters and setters
	public List<DGVertex> getVertices() {
		return vertices;
	}
	
	public void setVertices(List<DGVertex> vertices) {
		this.vertices = vertices;
	}

	public int getStartVertexIndex() {
		return startVertexIndex;
	}

	public void setStartVertexIndex(int startVertexIndex) {
		this.startVertexIndex = startVertexIndex;
	}
}
