package graph.directed.abs;

import java.util.ArrayList;
import java.util.List;

import graph.directed.SDGVertex;
import graph.directed.SDGraph;
import table.dwt.DWTEntry;
import table.dwt.DWTable;
import table.dwt.DWTableImpl;
import table.dwt.DWTuple;

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
	public SDGraph getConcreteSDGraph() {
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
		SDGraph concreteG = this.getConcreteSDGraph();
		DWTable table = new DWTableImpl(concreteG);
		for(int i = 0; i <= concreteG.getVertices().size(); i ++) {
			table.increMaxLenUpdate();
		}
		boolean hasPos = false;
		boolean hasNeg = false;
		boolean noCycle = true;
		for(SDGVertex v : concreteG.getVertices()) {
			DWTEntry entry = table.getEntry(v.getVertexIndex(), v.getVertexIndex());
			if(entry.getSetOfDWTuples().size() != 0) {
				noCycle = false;
			}
			for(DWTuple t : entry.getSetOfDWTuples()) {
				if(t.getWeight() > 0) {
					hasPos = true;
				} else if(t.getWeight() < 0) {
					hasNeg = true;
				} else {
					
				}
			}
		}
		if(noCycle) {
			this.setLoopTag(LoopTag.None);
		} else {
			if(hasPos && hasNeg) {
				this.setLoopTag(LoopTag.PosNeg);
			} else if(hasPos) {
				this.setLoopTag(LoopTag.Pos);
			} else if(hasNeg) {
				this.setLoopTag(LoopTag.Neg);
			} else {
				this.setLoopTag(LoopTag.Zero);
			}
		}      
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
