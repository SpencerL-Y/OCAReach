package graph.directed.zerograph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ZTPath {
	private List<ZTVertex> path;
	private ZeroEdgeDGraph zGraph;
	
	public ZTPath(ZTVertex start) {
		this.path = new ArrayList<ZTVertex>();
		this.path.add(start);
		this.setzGraph(start.getZg());
	}
	
	public ZTPath(Stack<ZTVertex> stack) {
		if(stack.size() == 0) {
			System.out.println("ERROR: stack init ztpath failed");
		}
		this.path = new ArrayList<ZTVertex>();
		this.zGraph = stack.peek().getZg();
		for(ZTVertex v : stack) {
			// TODO: DEBUG
			this.getPath().add(v);
		}
	}
	
	// basic operations
	public ZTVertex getVertex(int i) {
		return this.getPath().get(i);
	}
	
	public ZTVertex getLastVertex() {
		return this.getPath().get(this.getPath().size() - 1);
	}
	
	public ZTVertex getInitVertex() {
		return this.getPath().get(0);
	}
	
	public void concatVertex(ZTVertex v) {
		if(!this.getzGraph().containsVertex(v)) {
			System.out.println("ERROR: zpath concat vertex not exist");
			return;
		}
		if(!concatValid(v) || this.containsVertex(v)) {
			System.out.println("ERROR: zpath concat not valid");
			return;
		}
		this.getPath().add(v);
	}
	
	public boolean containsVertex(ZTVertex v) {
		// zero test edge only appears once 
		for(ZTVertex ve : this.getPath()) {
			if(v.getIndex() == ve.getIndex()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean concatValid(ZTVertex v) {
		for(ZTEdge e : this.getLastVertex().getEdges()) {
			if(e.getTo() == v) {
				return true;
			}
		}
		return false;
	}
	
	public void print() {
		System.out.print("ZTPath print: ");
		for(ZTVertex v : this.getPath()) {
			System.out.print(v.getIndex());
		}
		System.out.println();
	}
	
	// setters and getters
	public List<ZTVertex> getPath() {
		return this.path;
	}

	public void setPath(List<ZTVertex> path) {
		this.path = path;
	}

	public ZeroEdgeDGraph getzGraph() {
		return zGraph;
	}

	public void setzGraph(ZeroEdgeDGraph zGraph) {
		this.zGraph = zGraph;
	}
	
}
