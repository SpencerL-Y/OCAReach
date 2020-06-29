package graph.directed.zerograph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import graph.directed.DGEdge;
import graph.directed.DGVertex;
import graph.directed.DGraph;

public class ZeroEdgeDGraph {
	//TODO: DEBUG
	private List<ZTVertex> vertices;
	private int startZTIndex;
	private int targetZTIndex;
	private DGraph dgZero;
	private DGraph dg;
	
	public ZeroEdgeDGraph(DGraph dg) {
		this.vertices = new ArrayList<ZTVertex>();
		this.dgZero = dg;
		this.dg = this.dgZero.getGraphZeroEdgeRemoved();
		// add all the zero test vertices
		int index = 2;
		ZTVertex startZTVertex = new ZTVertex(this, 0, -1, this.dgZero.getStartVertexIndex());
		ZTVertex targetZTVertex = new ZTVertex(this, 1, this.dgZero.getEndingVertexIndex(), -1);
		this.startZTIndex = 0;
		this.targetZTIndex = 1;
		this.vertices.add(startZTVertex);
		this.vertices.add(targetZTVertex);
		for(DGVertex v : this.dgZero.getVertices()) {
			for(DGEdge e : v.getEdges()) {
				if(e.getWeight() == 0) {
					ZTVertex newV = new ZTVertex(this, index, e.getFrom().getIndex(), e.getTo().getIndex());
					System.out.println("ZTVertex: " + newV.getIndex());
					newV.printZeroEdge();
					this.addVertex(newV);
					index ++;
				}
			}
		}
		System.out.println();
		// add edges according to the reachability
		// TODO: DEBUG 
		for(ZTVertex v : this.getVertices()) {
			for(ZTVertex w : this.getVertices()) {
				if(v.getIndex() != w.getIndex()) {
					if(v.getFrom() != -1 && w.getFrom() != -1 && v.getTo() != -1 && w.getTo() != -1) {
						// if vertices are not start or target
						if(this.getDg().isReachable(v.getTo(), w.getFrom())) {
							// if there is a path in dg from v.to to w.from, then add a edge in the graph
							this.addEdge(v.getIndex(), w.getIndex());
						}
					} else if(w.getFrom() == -1 && v.getTo() != -1) {
						// if v is not, w is start vertex
						if(this.getDg().isReachable(v.getTo(), w.getTo())) {
							this.addEdge(v.getIndex(), w.getIndex());
						}
					} else if(v.getTo() == -1 && w.getFrom() != -1) {
						// if w is not, v is target vertex
						if(this.getDg().isReachable(v.getFrom(), w.getFrom())) {
							//System.out.println("ADD EDGE " + v.getIndex() + w.getIndex());
							this.addEdge(v.getIndex(), w.getIndex());
						}
					} else if(v.getTo() == -1 && w.getFrom() == -1){
						// if v is target vertex and w is start vertex
						if(this.getDg().isReachable(v.getFrom(), w.getTo())) {
							this.addEdge(v.getIndex(), w.getIndex());
						}
					} else {
						// if v is start vertex and w is target vertex
						if(this.getDg().isReachable(v.getTo(), w.getFrom())) {
							this.addEdge(v.getIndex(), w.getIndex());
						}
					}
				}
			}
		}
	}
	
	// basic operations
	
	public ZTVertex getVertex(int zTIndex) {
		for(ZTVertex zv : this.getVertices()) {
			if(zv.getIndex() == zTIndex) {
				return zv;
			}
		}
		System.out.println("ERROR: ZTVertex not found");
		return null;
	}
	
	public ZTVertex getVertexByFromTo(int fromIndex, int toIndex) {
		for(ZTVertex v : this.getVertices()) {
			if(v.getFrom() == fromIndex && v.getTo() == toIndex) {
				return v;
			}
		}
		System.out.println("ERROR: ZTVertex by from to not found");
		return null;
	}
	
	public void addVertex(ZTVertex v) {
		this.getVertices().add(v);
	}
	
	public boolean containsVertex(int vertexIndex) {
		for(ZTVertex v : this.getVertices()) {
			if(v.getIndex() == vertexIndex) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsVertex(ZTVertex v) {
		for(ZTVertex ve : this.getVertices()) {
			if(ve == v) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsEdge(int fromIndex, int toIndex) {
		if(!containsVertex(fromIndex) || !containsVertex(toIndex)) {
			System.out.println("ERROR: ZTEdge does not contains vertex");
		}
		for(ZTEdge e : this.getVertex(fromIndex).getEdges()) {
			if(e.getFrom().getIndex() == fromIndex && e.getTo().getIndex() == toIndex) {
				return true;
			}
		}
		return false;
	}
	
	public void addEdge(int from, int to) {
		if(!(this.containsVertex(from) && this.containsVertex(to))) {
			System.out.println("ERROR: add ztedge error");
			return;
		}
		//TODO
		System.out.println("Add zt edge: " + from + " to " + to);
		this.getVertex(from).addEdge(to);
	}
	
	public int getVerticesNum() {
		return this.getVertices().size();
	}
	
	// algorithm
	
	public List<ZTPath> dfsFindAllZTPath(){
		List<ZTPath> paths = new ArrayList<ZTPath>();
		Stack<ZTVertex> stack = new Stack<ZTVertex>();
		int targetIndex = this.getTargetZTIndex();
		this.getVertex(this.getStartZTIndex()).dfsFindZTPath(paths, stack, targetIndex);
		return paths;
	}
	
	
	// getters and setters
	public List<ZTVertex> getVertices() {
		return vertices;
	}


	public void setVertices(List<ZTVertex> vertices) {
		this.vertices = vertices;
	}


	public int getTargetZTIndex() {
		return targetZTIndex;
	}


	public void setTargetZTIndex(int targetZTIndex) {
		this.targetZTIndex = targetZTIndex;
	}


	public int getStartZTIndex() {
		return startZTIndex;
	}


	public void setStartZTIndex(int startZTIndex) {
		this.startZTIndex = startZTIndex;
	}

	public DGraph getDg() {
		return dg;
	}

	public void setDg(DGraph dg) {
		this.dg = dg;
	}
}
