package graph.directed.abs;

import java.util.ArrayList;
import java.util.List;

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
		// after add all the border edges then we construct the vertices
		for(int i = 1; i <= sdg.getSccNum(); i++) {
			this.vertices.add(new ASDGVertex(this, i, this.borderEdges));
		}
	}
	
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
