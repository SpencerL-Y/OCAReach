package graph.undirected;

import graph.directed.DGraph;

public class UDGraph extends DGraph{
	// A Undirected graph can be regarded as a directed graph 
	// where two reversed directed edges can be regarded as an edge in a UDG
	
	@Override 
	public void addEdge(int fromIndex, int toIndex, int weight) {
		if(this.checkEdge(fromIndex, toIndex)) {
			System.out.println("ERROR: Add edge error, Edge already exists");
			return;
		} 
		this.getVertex(fromIndex).addEdge(this.getVertex(toIndex), weight);
		this.getVertex(toIndex).addEdge(this.getVertex(fromIndex), weight);
	}

	@Override 
	public void delEdge(int fromIndex, int toIndex) {
		if(!this.checkEdge(fromIndex, toIndex) && !this.checkEdge(toIndex, fromIndex)) {
			System.out.println("ERROR: Del edge error, edge does not exists");
			return;
		}
		this.getVertex(fromIndex).delEdge(toIndex);
		this.getVertex(toIndex).delEdge(fromIndex);
	}

	private Boolean checkEdge(int fromIndex, int toIndex) {
		return this.getVertex(fromIndex).checkEdge(toIndex) || this.getVertex(toIndex).checkEdge(fromIndex);
	}
}
