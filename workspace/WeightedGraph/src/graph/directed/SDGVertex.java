package graph.directed;

import java.util.Stack;


public class SDGVertex {
	private SDGraph graph;
	private DGVertex vertex;
	private Integer sccMark;
	private Integer lowLink;
	private Boolean onStack;
	
	
	public SDGVertex(DGVertex v, SDGraph g) {
		this.setGraph(g);
		this.setVertex(v);
		this.sccMark = -1;
		this.lowLink = -1;
		this.setOnStack(false);
	}
	
	//algorithm 
	//TODO: debug
	public void strongConnected(Stack<SDGVertex> stack, Integer index, Integer sccIndex) {
		this.setSccMark(index);
		this.setLowLink(index);
		index = index + 1;
		stack.push(this);
		this.setOnStack(true);
		for(DGEdge e: this.getVertex().getEdges()) {
			// the edge is (this, w) in SDGraph
			SDGVertex w  = this.getGraph().getVertex(e.getTo().getIndex());
			if(w.getSccMark() == -1) {
				w.strongConnected(stack, index, sccIndex);
			} else if(w.getOnStack()) {
				this.setLowLink(Math.min(this.lowLink, w.getSccMark()));
			} 
		}
		
		if(this.lowLink == this.getSccMark()) {
			SDGVertex w;
			do {
				w = stack.pop();
				w.setOnStack(false);
				w.setSccMark(sccIndex);
			} while(w != this);
			sccIndex += 1;
		}
	}
	
	
	//getters and setters
	public Integer getSccMark() {
		return sccMark;
	}
	public void setSccMark(Integer sccMark) {
		this.sccMark = sccMark;
	}
	public Integer getLowLink() {
		return lowLink;
	}
	public void setLowLink(Integer lowLink) {
		this.lowLink = lowLink;
	}
	public DGVertex getVertex() {
		return vertex;
	}
	public void setVertex(DGVertex vertex) {
		this.vertex = vertex;
	}

	public Boolean getOnStack() {
		return onStack;
	}

	public void setOnStack(Boolean onStack) {
		this.onStack = onStack;
	}

	public SDGraph getGraph() {
		return graph;
	}

	public void setGraph(SDGraph graph) {
		this.graph = graph;
	}
}
