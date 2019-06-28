package graph.directed;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;


public class SDGVertex implements Vertex{
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
	public void strongConnected(Stack<SDGVertex> stack, AtomicInteger index, AtomicInteger sccIndex) {
		this.setSccMark(index.get());
		this.setLowLink(index.get());
		index.set(index.get()+1);
		stack.push(this);
		this.setOnStack(true);
		for(DGEdge e: this.getVertex().getEdges()) {
			// the edge is (this, w) in SDGraph
			SDGVertex w  = this.getGraph().getVertex(e.getTo().getIndex());
			if(w.getSccMark() == -1) {
				w.strongConnected(stack, index, sccIndex);
				this.setLowLink(Math.min(this.lowLink, w.getLowLink()));
			} else if(w.getOnStack()) {
				this.setLowLink(Math.min(this.lowLink, w.getSccMark()));
			} 
		}
		if(this.lowLink == this.getSccMark()) {
			SDGVertex w = null;
			do {
				w = stack.pop();
				w.setOnStack(false);
				w.setSccMark(sccIndex.get());
			} while(w != this);
			sccIndex.set(sccIndex.get()+1);
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
	
	public List<DGEdge> getEdges(){
		return this.getVertex().getEdges();
	}
	
	public int getVertexIndex() {
		return this.getVertex().getIndex();
	}
}
