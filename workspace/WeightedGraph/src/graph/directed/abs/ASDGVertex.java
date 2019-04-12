package graph.directed.abs;

import java.util.ArrayList;
import java.util.List;

import graph.directed.DGEdge;
import graph.directed.DGraph;
import graph.directed.SDGVertex;

public class ASDGVertex {
	
	private ASDGraph graph;
	private int sccIndex;
	private List<SDGVertex> inports;
	private List<SDGVertex> outports;
	private List<ASDGEdge> absEdges;
	private LoopTag loopTag;
	
	public ASDGVertex(ASDGraph g, int sccIndex, List<BorderEdge> borderEdges) {
		this.setGraph(g);
		this.sccIndex = sccIndex;
		this.inports = new ArrayList<SDGVertex>();
		this.outports = new ArrayList<SDGVertex>();
		this.absEdges = new ArrayList<ASDGEdge>();
		for(BorderEdge e : borderEdges) {
			if(e.getToScc() == this.getSccIndex()) {
				this.inports.add(e.getToVertex());
			} else if(e.getFromScc() == this.getSccIndex()) {
				this.outports.add(e.getFromVertex());
				this.absEdges.add(new ASDGEdge(this, this.getGraph().getVertex(e.getToScc())));
			}
		}
	}
	
	// map to concrete subgraph
	//TODO: debug
	public DGraph getConcreteDGraph() {
		return this.getGraph().getSdg().getConcreteSCC(this.getSccIndex());
	}
	
	//basic operations
	
	public ASDGEdge getAbsEdge(int toSccIndex) {
		for(ASDGEdge e : this.getAbsEdges()) {
			if(e.getTo().getSccIndex() == toSccIndex) {
				return e;
			}
		}
		System.out.println("ERROR: ASDGEdge not found");
		System.out.checkError();
		return null;
	}
	
	public Boolean checkAbsEdge(int toScc) {
		for(ASDGEdge e : this.getAbsEdges()) {
			if(e.getTo().getSccIndex() == toScc) {
				return true;
			}
		}
		return false;
	}
	
	// algorithm
	
	public void computeLoopTag() {
		//TODO debug
		// compute loop information using DWT
		DGraph concreteG = this.getConcreteDGraph();
		this.setLoopTag(concreteG.computeLoopTag());
	}
	
	public List<BorderEdge> getAllConcreteEdgesTo(ASDGVertex v) {
		List<BorderEdge> conEdges = new ArrayList<BorderEdge>();
		for(BorderEdge e : this.getGraph().getBorderEdges()) {
			if(e.getFromScc() == this.getSccIndex() && e.getToScc() == v.getSccIndex()) {
				conEdges.add(e);
			}
		}
		return conEdges;
	}
	
	// getters and setters
	public int getSccIndex() {
		return sccIndex;
	}
	public void setSccIndex(int sccIndex) {
		this.sccIndex = sccIndex;
	}
	public List<SDGVertex> getOurports() {
		return outports;
	}
	public void setOurports(List<SDGVertex> ourports) {
		this.outports = ourports;
	}
	public List<SDGVertex> getInports() {
		return inports;
	}
	public void setInports(List<SDGVertex> inports) {
		this.inports = inports;
	}

	public List<ASDGEdge> getAbsEdges() {
		return absEdges;
	}

	public void setAbsEdges(List<ASDGEdge> absEdges) {
		this.absEdges = absEdges;
	}

	public ASDGraph getGraph() {
		return graph;
	}

	public void setGraph(ASDGraph graph) {
		this.graph = graph;
	}
	
	public void setLoopTag(LoopTag loopTag) {
		this.loopTag = loopTag;
	}
	
	public LoopTag getLoopTag() {
		return this.loopTag;
	}
}
