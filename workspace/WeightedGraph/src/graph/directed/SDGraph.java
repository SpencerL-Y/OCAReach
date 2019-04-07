package graph.directed;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SDGraph{
	private DGraph graph;
	private Integer sccNum;
	private SDGVertex startingVertex;
	private List<SDGVertex> vertices;
	
	public SDGraph(DGraph g) {
		this.graph = g;
		this.setSccNum(-1);
		this.vertices = new ArrayList<SDGVertex>();
		for(DGVertex v : this.getGraph().getVertices()) {
			this.vertices.add(new SDGVertex(v, this));
		}
		this.startingVertex = this.getVertex(this.getGraph().getStartVertexIndex());
	}

	// basic operation
	
	public SDGVertex getVertex(int vertexIndex) {
		for(SDGVertex v : this.getVertices()) {
			if(v.getVertex().getIndex() == vertexIndex) {
				return v;
			}
		}
		System.out.println("ERROR: SDGVertex not found, no index matches");
		System.out.checkError();
		return null;
	}
	
	public SDGVertex getEdgeTo(DGEdge e) {
		return this.getVertex(e.getTo().getIndex());
	}
	
	// algorithm tarjan
	public void tarjan() {
		Integer index = 0;
		Integer sccIndex = 1;
		Stack<SDGVertex> stack = new Stack<SDGVertex>();
		for(SDGVertex v : this.getVertices()) {
			if(v.getSccMark() == -1) {
				v.strongConnected(stack, index, sccIndex);
			}
		}
		this.setSccNum(sccIndex);
	}
	
	// getters and setters

	public DGraph getGraph() {
		return graph;
	}

	public void setGraph(DGraph graph) {
		this.graph = graph;
	}

	public List<SDGVertex> getVertices() {
		return vertices;
	}

	public void setVertices(List<SDGVertex> vertices) {
		this.vertices = vertices;
	}

	public Integer getSccNum() {
		return sccNum;
	}

	public void setSccNum(Integer sccNum) {
		this.sccNum = sccNum;
	}

	public SDGVertex getStartingVertex() {
		return startingVertex;
	}

	public void setStartingVertex(SDGVertex startingVertex) {
		this.startingVertex = startingVertex;
	}
	
}
