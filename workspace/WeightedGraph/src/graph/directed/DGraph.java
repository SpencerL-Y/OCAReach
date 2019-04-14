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
		for(DGVertex v : this.getVertices()) {
			if(this.getTable().getEntry(v.getIndex(), v.getIndex()).getSetOfDWTuples().size() != 0) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsVertex(int index) {
		for(DGVertex v : this.getVertices()) {
			if(v.getIndex() == index) {
				return true;
			}
		}
		return false;
	}
	

	//TODO: debug
	public List<PosCycleTempl> getAllPosCycleTempls(int startIndex){
		List<PosCycleTempl> cyclTempls = new ArrayList<PosCycleTempl>();
		//TODO: imple
		return cyclTempls;
	}
	

	//TODO: debug
	private List<DGCycle> getAllSimplePosCycles(){
		// find all the simple positive cycles length in |V|
		if(this.getTable() == null) {
			this.computeLoopTag();
		}
		List<DGCycle> cycles = new ArrayList<DGCycle>();
		for(DGVertex v : this.getVertices()) {
			boolean hasPosCycle = false;
			List<DWTuple> set = this.getTable().getEntry(v.getIndex(), v.getIndex()).getSetOfDWTuples(); 
			if(set.size() != 0) {
				for(DWTuple t : set) {
					if(t.getWeight() > 0) {
						// if there is a positive cycle here, check a simple cycle
						hasPosCycle = true;
						break;
					}
				}
			}
			if(hasPosCycle) {
				List<DGCycle> cycleList = this.dfsGetPosCycles(v);
				cycles = DGraphUtil.union(cycles, cycleList);
			}
		}
		
		return cycles;
	}
	
	//TODO: debug
	private List<DGCycle> dfsGetPosCycles(DGVertex v){
		Stack<DGVertex> stack = new Stack<DGVertex>();
		List<DGCycle> cycles = new ArrayList<DGCycle>();
		stack.push(v);
		this.dfsGetPosCycle(stack, cycles);
		stack.pop();
		return cycles;
	}
	
	
	//TODO: debug
	private void dfsGetPosCycle(Stack<DGVertex> stack, List<DGCycle> cycles) {
		if(stack.peek() == stack.get(0) && stack.size() != 1) {
			DGPath p = new DGPath(stack.get(0));
			for(int i = 1; i < stack.size(); i++) {
				p.concatVertex(stack.get(i));
			}
			if(p.getWeight() > 0) {
				cycles.add(new DGCycle(p, p.getVertex(0)));
			}
			return;
		} else if(stack.contains(stack.peek())) {
			return;
		}
		
		for(DGEdge e : stack.peek().getEdges()) {
			stack.push(e.getTo());
			this.dfsGetPosCycle(stack, cycles);
			stack.pop();
		}
	}
	
	private List<DGCycle> dfsGetPathToCycle(DGVertex start, DGCycle cycle){
		//TODO: imple
		// if the vertex is on the cycle, over
		// else dfs for each vertex on the cycle
	}
	
	public void increaseDWTLenLimit() {
		//TODO imple
		// increase the length limit to 3|V|^2 + 1 and 
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
}
