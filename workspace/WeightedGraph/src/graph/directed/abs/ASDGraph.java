package graph.directed.abs;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import graph.directed.DGEdge;
import graph.directed.SDGVertex;
import graph.directed.SDGraph;

public class ASDGraph {
	private SDGraph sdg;
	private List<ASDGVertex> vertices;
	private List<BorderEdge> borderEdges;
	
	public ASDGraph(SDGraph sdg) {
		this.setSdg(sdg);
		this.vertices = new ArrayList<ASDGVertex>();
		this.borderEdges = new ArrayList<BorderEdge>();
		for(SDGVertex v : this.getSdg().getVertices()) {
			for(DGEdge e : v.getVertex().getEdges()) {
				SDGVertex w = v.getGraph().getVertex(e.getTo().getIndex());
				if(v.getSccMark() != w.getSccMark()) {
					// if the two vertices are not in the same scc
					// create a border edge in ASDG
					BorderEdge edge = new BorderEdge(v, w);
					this.borderEdges.add(edge);
				}
			}
		}
		if(sdg.getSccNum() == -1) {
			System.out.println("ERROR: SDG haven't run tarjan algorithm");
			System.out.checkError();
		}
		// after add all the border edges then we construct the vertices
		for(int i = 1; i <= sdg.getSccNum(); i++) {
			this.vertices.add(new ASDGVertex(this, i, this.borderEdges));
		}
		// after constructing the graph, compute the tags
		this.computeAllLoopTag();
	}
	
	//basic operations
	
	public ASDGVertex getVertex(int sccIndex) {
		for(ASDGVertex v : this.getVertices()) {
			if(v.getSccIndex() == sccIndex) {
				return v;
			}
		}
		System.out.println("ERROR: ASDGVertex not found");
		System.out.checkError();
		return null;
	}
	
	public Boolean containsBorderEdge(SDGVertex from, SDGVertex to) {
		for(BorderEdge e : this.getBorderEdges()) {
			if(e.getFromVertex() == from && e.getToVertex() == to) {
				return true;
			}
		}
		return false;
	}
	
	
	public Boolean containsAbsEdge(int fromScc, int toScc) {
		ASDGVertex v = this.getVertex(fromScc);
		return v.checkAbsEdge(toScc);
	}
	
	public Boolean containsAbsEdge(ASDGVertex from, ASDGVertex to) {
		return this.containsAbsEdge(from.getSccIndex(), to.getSccIndex());
	}
	
	public ASDGEdge getAbsEdge(int fromSccIndex, int toSccIndex) {
		if(this.containsAbsEdge(fromSccIndex, toSccIndex)) {
			return this.getVertex(fromSccIndex).getAbsEdge(toSccIndex);
		}
		System.out.println("ERROR: AbsEdge not found");
		System.out.checkError();
		return null;
	}
	
	// algorithm
	
	public List<BorderEdge> getBorderEdgesByAbsEdge(int fromSccIndex, int toSccIndex){
		List<BorderEdge> edges = new ArrayList<BorderEdge>();
		for(BorderEdge e : this.getBorderEdges()) {
			if(e.getFromScc() == fromSccIndex && e.getToScc() == toSccIndex) {
				edges.add(e);
			}
		}
		return edges;
	}
	
	public BorderEdge getBorderEdgeByInportOutport(SDGVertex inport, SDGVertex outport) {
		List<BorderEdge> list = this.getBorderEdgesByAbsEdge(inport.getSccMark(), outport.getSccMark());
		for(BorderEdge e : list) {
			if(e.getFromVertex() == inport && e.getToVertex() == outport) {
				return e;
			}
		}
		return null;
	}
	
	public ASDGraph getSkewTranspose() {
		ASDGraph sktASG = new ASDGraph(this.getSdg().getSkewTranspose());
		return sktASG;
	}
	
	//TODO: debug
	public List<ASDGPath> DFSFindAbsPaths(int startSccIndex, int toSccIndex) {
		List<ASDGPath> result = new ArrayList<ASDGPath>();
		Stack<ASDGVertex> stack = new Stack<ASDGVertex>();
		stack.push(this.getVertex(startSccIndex));
		this.dfsProcess(result, stack, toSccIndex);
		stack.pop();
		return result;
	}
	//TODO: debug
	private void dfsProcess(List<ASDGPath> result, Stack<ASDGVertex> stack, int toSccIndex) {
		// termination situations 1 & 2
		if(stack.peek().getSccIndex() == toSccIndex) {
			// if we reach the target in dfs, store  the path into result list
			ASDGPath path = new ASDGPath(stack.get(0));
			for(int i = 1; i <= stack.size() - 1; i ++) {
				path.concatVertex(stack.get(i));
			}
			result.add(path);
			return;
		} else if(stack.peek().getAbsEdges().size() == 0) {
			// no outports
			return;
		} else if(stack.empty()) {
			// stack empty (this should not happen)
			System.out.checkError();
			return;
		}
		for(ASDGEdge e : stack.peek().getAbsEdges()) {
			stack.push(e.getTo());
			this.dfsProcess(result, stack, toSccIndex);
			stack.pop();
		}
	}
	
	//TODO: debug
	private void computeAllLoopTag() {
		for(ASDGVertex v : this.getVertices()) {
			v.computeLoopTag();
		}
	}
	
	public List<BorderEdge> getConcreteEdges(int formScc, int toScc){
		return this.getVertex(formScc).getAllConcreteEdgesTo(this.getVertex(toScc));
	}
	
	//setters and getters
	
	public SDGraph getSdg() {
		return sdg;
	}

	public void setSdg(SDGraph sdg) {
		this.sdg = sdg;
	}

	public List<ASDGVertex> getVertices(){
		return this.vertices;
	}
	
	public void setVertices(List<ASDGVertex> vertices) {
		this.vertices = vertices;
	}

	public List<BorderEdge> getBorderEdges() {
		return borderEdges;
	}

	public void setBorderEdges(List<BorderEdge> borderEdges) {
		this.borderEdges = borderEdges;
	}
}
