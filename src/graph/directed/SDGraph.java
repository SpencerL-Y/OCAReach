package graph.directed;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

public class SDGraph implements Graph{
	private DGraph graph;
	private Integer sccNum;
	private SDGVertex startingVertex;
	private SDGVertex endingVertex;
	private List<SDGVertex> vertices;
	
	public SDGraph(DGraph g) {
		this.graph = g;
		this.setSccNum(-1);
		this.vertices = new ArrayList<SDGVertex>();
		for(DGVertex v : this.getGraph().getVertices()) {
			this.vertices.add(new SDGVertex(v, this));
		}
		this.startingVertex = this.getVertex(this.getGraph().getStartVertexIndex());
		this.endingVertex = this.getVertex(this.getGraph().getEndingVertexIndex());
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
	
	public DGraph getConcreteSCC(int sccIndex) {
		DGraph graph = new DGraph(this.graph.getMaxAbsValue());
		for(SDGVertex v : this.getVertices()) {
			if(v.getSccMark() == sccIndex) {
				// add all SCCs vertices into the subgraph
				graph.addVertex(v.getVertexIndex());
			}
		}
		for(SDGVertex v : this.getVertices()) {
			for(DGEdge e : v.getVertex().getEdges()) {
				if(v.getSccMark() == sccIndex && this.getEdgeTo(e).getSccMark() == sccIndex) {
					// if the transition is in the SCC, add it to the sub directed graph
					graph.addEdge(e.getFrom().getIndex(), 
								  e.getTo().getIndex(),
								  e.getWeight());
				}
			}
		}
		return graph;
	}
	
	// algorithm
	// Tarjan's algorithm
	public void tarjan() {
		for(SDGVertex v : this.getVertices()) {
			v.setSccMark(-1);
		}
		AtomicInteger index = new AtomicInteger(0);
		AtomicInteger sccIndex = new AtomicInteger(1);
		Stack<SDGVertex> stack = new Stack<SDGVertex>();
		for(SDGVertex v : this.getVertices()) {
			if(v.getSccMark() == -1) {
				v.strongConnected(stack, index, sccIndex);
			}
		}
		this.setSccNum(sccIndex.get() - 1);
	}
	
	public SDGraph getSkewTranspose() {
		// Tarjan's algorithm must be computed before computing skew transpose
		DGraph sktG = this.getGraph().getSkewTranspose();
		SDGraph sktSG = new SDGraph(sktG);
		return sktSG;
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

	public SDGVertex getEndingVertex() {
		return endingVertex;
	}

	public void setEndingVertex(SDGVertex endingVertex) {
		this.endingVertex = endingVertex;
	}
	
}
