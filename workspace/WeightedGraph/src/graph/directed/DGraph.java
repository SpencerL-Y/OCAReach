package graph.directed;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import graph.directed.abs.LoopTag;
import table.dwt.DWTEntry;
import table.dwt.DWTable;
import table.dwt.DWTableImpl;
import table.dwt.DWTuple;

public class DGraph implements Graph{
	private List<DGVertex> vertices;
	private int startVertexIndex;
	private int endingVertexIndex;
	public DGraph() {
		this.setVertices(new ArrayList<DGVertex>());
		this.setStartVertexIndex(0);
		this.setEndingVertexIndex(0);
	}
	
	public void addVertex(int index) {
		for(DGVertex v : this.getVertices()) {
			if(v.getIndex() == index) {
				System.out.println("ERROR: Index repeat");
			}
		}
		this.vertices.add(new DGVertex(index));
	}
	
	public void delVertex(int index) {
		for(DGVertex v : this.getVertices()) {
			if(v.getIndex() == index) {
				this.getVertices().remove(v);
				return;
			}
		}
		System.out.println("ERROR: Vertex not exist");
	}
	
	public DGVertex getVertex(int index) {
		for(DGVertex v : this.getVertices()) {
			if(v.getIndex() == index) {
				return v;
			}
		}
		System.out.println("ERROR: Vertex not found");
		return null;
	}
	
	public void setVertex(int index, DGVertex v) {
		for(DGVertex ve : this.getVertices()) {
			if(ve.getIndex() == index) {
				ve = v;
			}
		}
	}
	
	public int size() {
		return this.getVertices().size();
	}
	
	public void addEdge(int fromIndex, int toIndex, int weight) {
		if(this.checkEdge(fromIndex, toIndex)) {
			System.out.println("ERROR: Add edge error, Edge already exists");
			return;
		} 
		this.getVertex(fromIndex).addEdge(this.getVertex(toIndex), weight);
	}
	
	public void delEdge(int fromIndex, int toIndex) {
		if(!this.checkEdge(fromIndex, toIndex)) {
			System.out.println("ERROR: Del edge error, edge does not exists");
			return;
		}
		this.getVertex(fromIndex).delEdge(toIndex);
	}
	
	private Boolean checkEdge(int fromIndex, int toIndex) {
		return this.getVertex(fromIndex).checkEdge(toIndex);
	}
	
	
	private Boolean checkVertex(int vertexIndex) {
		for(DGVertex v : this.getVertices()) {
			if(v.getIndex() == vertexIndex) {
				return true;
			}
		}
		return false;
	}
	
	//Algorithms
	//TODO: debug
	public LoopTag computeLoopTag() {
		DWTable table = new DWTableImpl(this);
		for(int i = 0; i <= this.getVertices().size(); i ++) {
			table.increMaxLenUpdate();
		}
		boolean hasPos = false;
		boolean hasNeg = false;
		boolean noCycle = true;
		for(DGVertex v : this.getVertices()) {
			DWTEntry entry = table.getEntry(v.getIndex(), v.getIndex());
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
			return LoopTag.None;
		} else {
			if(hasPos && hasNeg) {
				return LoopTag.PosNeg;
			} else if(hasPos) {
				return LoopTag.Pos;
			} else if(hasNeg) {
				return LoopTag.Neg;
			} else {
				return LoopTag.Zero;
			}
		}      
	}
	
	public List<DGraph> getAllPossibleSupport(int startIndex, int endIndex){
		// find the supports that contains startVertex and endVertex
		// the support also needs to be a strong connect component
		return null;
	}
	
	//TODO: debug
	public DGraph getSkewTranspose() {
		DGraph g = new DGraph();
		for(DGVertex v : this.getVertices()) {
			g.addVertex(v.getIndex());
		}
		for(DGVertex v : this.getVertices()) {
			for(DGEdge e : v.getEdges()) {
				g.addEdge(e.getTo().getIndex(), e.getFrom().getIndex(), -e.getWeight());
			}
		} h
		return g;
	}
	
	
	//TODO: debug
	public DGraph edgeListToGraph(List<DGEdge> list) {
		DGraph g = new DGraph();
		for(DGEdge e : list) {
			if(this.checkVertex(e.getTo().getIndex())) {
				g.addVertex(e.getTo().getIndex());
			}
			if(this.checkVertex(e.getFrom().getIndex())) {
				g.addVertex(e.getFrom().getIndex());
			}
			g.addEdge(e.getFrom().getIndex(), e.getTo().getIndex(), e.getWeight());
		}
		return g;
	}
	
	//TODO: debug
	public boolean isConnected() {
		// return true if the graph is a connected graph
		// here we only need the integrity of the graph
		// idea? use DWT? or use DFS
		
		//TODO imple
		DGVertex startVertex = this.getVertex(this.getStartVertexIndex());
		Stack<DGVertex> stack = new Stack<DGVertex>();
		stack.push(startVertex);
		List<DGVertex> visited = new ArrayList<DGVertex>();
		//TODO imple
		return false;
	}
	
	//TODO debug
	private void connectedBFS(Stack<Vertex> stack, DGVertex currrentVertex, List<DGVertex> visited) {
		// add all the vertices into a list
		// do dfs and remove the object when reaching the vertex
		// after finishing if there exists a vertex in the list, the graph is not connected
		//TODO imple
	}
	
	//getters and setters
	public List<DGVertex> getVertices() {
		return vertices;
	}
	
	public void setVertices(List<DGVertex> vertices) {
		this.vertices = vertices;
	}

	public int getStartVertexIndex() {
		return startVertexIndex;
	}

	public void setStartVertexIndex(int startVertexIndex) {
		this.startVertexIndex = startVertexIndex;
	}

	public int getEndingVertexIndex() {
		return endingVertexIndex;
	}

	public void setEndingVertexIndex(int endingVertexIndex) {
		this.endingVertexIndex = endingVertexIndex;
	}
}
