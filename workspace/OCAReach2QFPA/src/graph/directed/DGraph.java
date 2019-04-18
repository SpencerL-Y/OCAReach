package graph.directed;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
	private DWTable table;
	private LoopTag tag;
	public DGraph() {
		this.setVertices(new ArrayList<DGVertex>());
		this.setStartVertexIndex(0);
		this.setEndingVertexIndex(0);
		this.table = null;
	}
	
	
	// basic operations
	public void addVertex(int index) {
		for(DGVertex v : this.getVertices()) {
			if(v.getIndex() == index) {
				System.out.println("ERROR: Index repeat");
			}
		}
		this.vertices.add(new DGVertex(index));
	}
	
	public void addVertex(DGVertex v) {
		if(!this.getVertices().contains(v)) {
			this.getVertices().add(v);
		}
	
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
		if(this.containsEdge(fromIndex, toIndex)) {
			System.out.println("ERROR: Add edge error, Edge already exists");
			return;
		} 
		this.getVertex(fromIndex).addEdge(this.getVertex(toIndex), weight);
	}
	
	public void delEdge(int fromIndex, int toIndex) {
		if(!this.containsEdge(fromIndex, toIndex)) {
			System.out.println("ERROR: Del edge error, edge does not exists");
			return;
		}
		this.getVertex(fromIndex).delEdge(toIndex);
	}
	
	public List<DGEdge> getEdges(){
		List<DGEdge> edgeList = new ArrayList<DGEdge>();
		for(DGVertex v : this.getVertices()) {
			for(DGEdge e : v.getEdges()) {
				edgeList.add(e);
			}
		}
		return edgeList;
	}
	
	private Boolean containsEdge(int fromIndex, int toIndex) {
		return this.getVertex(fromIndex).containsEdge(toIndex);
	}
	
	//Algorithms
	//TODO: debug
	public LoopTag computeLoopTag() {
		if(this.table != null) {
			return this.getTag();
		}
		DWTable table = new DWTableImpl(this);
		this.table = table;
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
				this.setTag(LoopTag.PosNeg);
				return LoopTag.PosNeg;
			} else if(hasPos) {
				this.setTag(LoopTag.Pos);
				return LoopTag.Pos;
			} else if(hasNeg) {
				this.setTag(LoopTag.Neg);
				return LoopTag.Neg;
			} else {
				this.setTag(LoopTag.Zero);
				return LoopTag.Zero;
			}
		}      
	}
	
	public List<DGraph> getAllPossibleSupport(int startIndex, int endIndex){
		// find the supports that contains startVertex and endVertex
		// the support also needs to be a strong connect component
		// TODO: debug
		List<DGraph> graphs = new ArrayList<DGraph>();
		List<DGEdge> edges = new ArrayList<DGEdge>();
		for(DGVertex v : this.getVertices()) {
			for(DGEdge e : v.getEdges()) {
				edges.add(e);
			}
		}
		List<List<DGEdge>> edgePow = DGraphUtil.getPowerSet(edges);
		for(List<DGEdge> list : edgePow) {
			DGraph temp = this.edgeListToGraph(list);
			if(temp.containsVertex(startIndex) && temp.containsVertex(endIndex)) {
				graphs.add(temp);
			}
		}
		return graphs;
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
		}
		g.setStartVertexIndex(this.getEndingVertexIndex());
		g.setEndingVertexIndex(this.getStartVertexIndex());
		return g;
	}
	
	
	//TODO: debug
	public DGraph edgeListToGraph(List<DGEdge> list) {
		DGraph g = new DGraph();
		if(list.size() == 0 && this.getVertices().size() == 1) {
			// trivial case
			g.addVertex(this.getVertices().get(0));
		}
		for(DGEdge e : list) {
			if(this.containsVertex(e.getTo().getIndex())) {
				g.addVertex(e.getTo().getIndex());
			}
			if(this.containsVertex(e.getFrom().getIndex())) {
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
		// if there is a vertex not appear in the visited list
		// the graph is not connected
		
		DGVertex startVertex = this.getVertex(this.getStartVertexIndex());
		Queue<DGVertex> list = new LinkedList<DGVertex>();
		List<DGVertex> visited = new ArrayList<DGVertex>();
		list.add(startVertex);
		this.connectedBFS(list,  visited);
		for(DGVertex v : this.getVertices()) {
			if(!visited.contains(v)) {
				return false;
			}
		}
		return true;
	}
	
	//TODO debug
	private void connectedBFS(Queue<DGVertex> list, List<DGVertex> visited) {
		// BFS and store all the reached vertices into list visited
		while(!list.isEmpty()) {
			visited.add(list.peek());
			for(DGEdge e : list.poll().getEdges()) {
				if(!visited.contains(e.getTo())) {
					list.add(e.getTo());
				}
			}
		}
	}
	
	public boolean isSubgraphOf(DGraph graph) {
		//TODO: debug
		// a graph is a subgraph if the vertices are covered
		for(DGVertex v : this.getVertices()) {
			if(!graph.getVertices().contains(v)) {
				return false;
			}
			for(DGEdge e : v.getEdges()) {
				if(!graph.containsEdge(e.getFrom().getIndex(), e.getTo().getIndex())) {
					return false;
				}
			}
		}
		return true;
	}

	//TODO: debug
	public DGraph union(DGraph graph) {
		DGraph newG = new DGraph();
		for(DGVertex v : this.getVertices()) {
			newG.addVertex(v);
		}
		for(DGVertex w : graph.getVertices()) {
			newG.addVertex(w);
		}
		return newG;
	}
	
	//TODO: debug
	public boolean containsCycle() {
		if(this.table == null) {
			this.computeLoopTag();
		}
		if(this.getTag() == LoopTag.None){
			return false;
		} else {
			return true;
		}
	}
	
	public boolean containsVertex(int index) {
		for(DGVertex v : this.getVertices()) {
			if(v.getIndex() == index) {
				return true;
			}
		}
		return false;
	}
	
	public void increaseDWTLenLimit() {
		//TODO imple
		// increase the length limit to 3|V|^2 + 1 and 
		for(int i = this.getTable().getMaxLength(); 
				i < 3 * this.getVertices().size() * this.getVertices().size() + 1; 
				i = this.getTable().getMaxLength()) {
			this.getTable().increMaxLenUpdate();
		}
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

	public DWTable getTable() {
		return table;
	}

	public void setTable(DWTable table) {
		this.table = table;
	}
	
	public LoopTag getTag() {
		return this.tag;
	}
	
	public void setTag(LoopTag tag) {
		this.tag = tag;
	}
}
