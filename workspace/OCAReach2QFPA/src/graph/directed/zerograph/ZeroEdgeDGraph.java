package graph.directed.zerograph;

import java.util.ArrayList;
import java.util.List;

import graph.directed.DGEdge;
import graph.directed.DGVertex;
import graph.directed.DGraph;

public class ZeroEdgeDGraph {
	
	private List<ZTVertex> vertices;
	private int startZTIndex;
	private int targetZTIndex;
	private DGraph dgZero;
	private DGraph dg;
	
	public ZeroEdgeDGraph(DGraph dg) {
		this.vertices = new ArrayList<ZTVertex>();
		this.dgZero = dg;
		this.setDg(this.dgZero.getGraphZeroEdgeRemoved());
		// add all the zero test vertices
		int index = 2;
		ZTVertex startZTVertex = new ZTVertex(0, -1, this.dgZero.getStartVertexIndex());
		ZTVertex targetZTVertex = new ZTVertex(1, this.dgZero.getEndingVertexIndex(), -1);
		this.startZTIndex = 0;
		this.targetZTIndex = 1;
		this.vertices.add(startZTVertex);
		this.vertices.add(targetZTVertex);
		for(DGVertex v : this.dgZero.getVertices()) {
			for(DGEdge e : v.getEdges()) {
				if(e.getWeight() == 0) {
					ZTVertex newV = new ZTVertex(index, e.getFrom().getIndex(), e.getTo().getIndex());
					this.addVertex(newV);
					index ++;
				}
			}
		}
		// add edges according to the reachability
		for(ZTVertex v : this.getVertices()) {
			for(ZTVertex w : this.getVertices()) {
				if(v.getIndex() != w.getIndex()) {
					if(v.getFrom() != -1 && w.getFrom() != -1 && v.getTo() != -1 && w.getTo() != -1) {
						if(this.getDg().isReachable(v.getTo(), w.getFrom())) {
							
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
	
	public void addEdge(int from, int to) {
		if(!(this.containsVertex(from) && this.containsVertex(to))) {
			System.out.println("ERROR: add ztedge error");
			return;
		}
		//TODO: add edge imple
		
		
	}
	
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
