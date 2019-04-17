package graph.directed;

import com.microsoft.z3.IntExpr;

public class DGFlowTuple {
	private DGEdge edge;
	private IntExpr edgeVar;
	
	public DGFlowTuple(DGEdge e, IntExpr edgeVar) {
		this.edge = e;
		this.edgeVar = edgeVar;
	}

	//basic operations
	
	public DGVertex getEdgeFrom() {
		return this.getEdge().getFrom();
	}
	
	public DGVertex getEdgeTo() {
		return this.getEdge().getTo();
	}
	
	//getters and setters
	public IntExpr getEdgeVar() {
		return edgeVar;
	}

	public void setEdgeVar(IntExpr edgeVar) {
		this.edgeVar = edgeVar;
	}

	public DGEdge getEdge() {
		return edge;
	}

	public void setEdge(DGEdge edge) {
		this.edge = edge;
	}
}
