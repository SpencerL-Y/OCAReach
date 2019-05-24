package graph.directed;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import graph.directed.abs.LoopTag;
import junit.framework.TestCase;
import parser.OCDGenerator;

public class DGraphTest extends TestCase {
	
	
	
	private Random r;
	private OCDGenerator ocdg;
	private DGraph dg;
	private DGraph ddg;
	
	public void setUp() {
		this.r = new Random();
		this.ocdg = new OCDGenerator();
		this.dg = ocdg.genRandomOca(10).toDGraph();
		this.ddg = new DGraph();
		this.ddg = new DGraph();
		for(int i = 0; i < 4; i++) {
			this.ddg.addVertex(i);
		}
		this.ddg.addEdge(0, 1, -1);
		this.ddg.addEdge(1, 2, 1);
		this.ddg.addEdge(2, 0, -1);
		this.ddg.addEdge(2, 3, 1);
		this.ddg.setStartVertexIndex(0);
		this.ddg.setEndingVertexIndex(0);
		
	}
	
	public final void testAddVertexInt() {
		System.out.println("MarginTest: AddVertexInt");
		for(int i = 0; i < 100; i ++) {
			this.dg.addVertex(i);
		}
	}

	public final void testAddVertexDGVertex() {
		System.out.println("MarginTest: AddVertexDGVertex");
		for(int i = 0; i < 100; i ++) {
			DGVertex v = new DGVertex(i);
			this.dg.addVertex(v);
		}
	}

	public final void testAddEdge() {
		System.out.println("AddEdgeTest: ");
		for(int i = 0; i < 10; i++) {
			this.dg.addEdge(r.nextInt(10), r.nextInt(10), r.nextBoolean() ? 1 : -1);
		}
	}

	public final void testDelEdge() {
		System.out.println("DelEdgeTest: ");
		for(int i = 0; i < 10; i ++) {
			this.dg.delEdge(r.nextInt(10), r.nextInt(10));
		}
	}

	public final void testComputeLoopTag() {
		//TODO: test after the test of table
		System.out.println("ComputeLoopTagTest:");
		LoopTag t = this.ddg.computeLoopTag();
		System.out.println("known looptag: " + t.toString());
		System.out.println("stress testing: ");
		for(int i = 0; i < 15; i++) {
			this.dg = this.ocdg.genRandomOca(4).toDGraph();
			t = this.dg.computeLoopTag();
			System.out.println("tag " + i + ": " + t.toString());
		}
	}

	public final void testGetAllPossibleSupport() {
		System.out.println("GetAllPossibleSupportTest: ");
		this.dg = this.ocdg.genRandomOca(5).toDGraph();
		List<DGraph> list = this.dg.getAllPossibleSupport(0, 3);
		for(DGraph g : list) {
			assert(g.isSubgraphOf(this.dg));
		}
	}

	public final void testGetSkewTranspose() {
		for(int i = 0; i < 1000; i++) {
			
			int edgeFrom = r.nextInt(10);
			int edgeTo = r.nextInt(10);
			int weight = r.nextBoolean() ? -1 : 1;
			this.dg.delEdge(edgeFrom, edgeTo);
			this.dg.addEdge(edgeFrom, edgeTo, weight);
			DGraph skew = this.dg.getSkewTranspose();
			for(DGEdge e : skew.getEdges()) {
				if(e.getFrom().getIndex() == edgeTo && e.getTo().getIndex() == edgeFrom) {
					assert(e.getWeight() == -weight);
				}
			}
			assert(this.dg.getEndingVertexIndex() == skew.getStartVertexIndex() 
					&& this.dg.getStartVertexIndex() == skew.getEndingVertexIndex());
		}
	}

	public final void testEdgeListToGraph() {
		System.out.println("EdgeListToGraphTest:");
		System.out.println("known: ");
		List<DGEdge> list = new ArrayList<DGEdge>();
		for(DGVertex v : this.ddg.getVertices()) {
			for(DGEdge e : v.getEdges()) {
				if(r.nextBoolean()) {
					list.add(e);
				}
			}
		}
		DGraph newG = this.ddg.edgeListToGraph(list, 0, 3);
		
		for(int i = 0; i < 10; i++) {
			list.clear();
			this.dg = this.ocdg.genRandomOca(8).toDGraph();
			for(DGVertex v : this.dg.getVertices()) {
				for(DGEdge e : v.getEdges()) {
					if(r.nextBoolean()) {
						list.add(e);
					}
				}
			}
			this.dg.edgeListToGraph(list, r.nextInt(10), r.nextInt(10));
		}
	}

	public final void testIsConnected() {
		System.out.println("IsConnectedTest:");
		
		System.out.println("known: " + this.ddg.isConnected());
		System.out.println("stress testing");
		for(int i = 0; i < 15; i++) {
			this.dg = this.ocdg.genRandomOca(4).toDGraph();
			boolean t = this.dg.isConnected();
			System.out.println("tag " + i + ": " + t);
		}
	}

	public final void testIsSubgraphOf() {
		System.out.println("IsSubGraphTest:");
		for(int i = 0; i < 10; i++) {
			DGraph sub = new DGraph();
			for(DGVertex v : this.dg.getVertices()) {
				if(r.nextBoolean()) {
					sub.addVertex(v.getIndex());
				}
			}
			for(DGVertex sv : sub.getVertices()) {
				for(DGEdge e : this.dg.getVertex(sv.getIndex()).getEdges()) {
					if(r.nextBoolean() && sub.containsVertex(e.getTo().getIndex())) {
						sub.addEdge(sv.getIndex(), e.getTo().getIndex(), e.getWeight());
					}
				}
			}
			assert(sub.isSubgraphOf(this.dg));
		}
	}

	public final void testUnion() {
		DGraph dg2 = ocdg.genRandomOca(15).toDGraph();
		DGraph newG = this.dg.union(dg2);
		assert(newG.getVertices().size() == 15);
		for(DGVertex v : this.dg.getVertices()) {
			for(DGEdge e : v.getEdges()) {
				assert(newG.containsEdge(v.getIndex(), e.getTo().getIndex()));
			}
		}
		for(DGVertex v : dg2.getVertices()) {
			for(DGEdge e : v.getEdges()) {
				assert(newG.containsEdge(v.getIndex(), e.getTo().getIndex()));
			}
		}
	}

	public final void testContainsCycle() {
		System.out.println("ContainsCycleTest:");
		System.out.println("known: " + this.ddg.containsCycle());
		for(int i = 0; i < 15; i++) {
			this.dg = this.ocdg.genRandomOca(6).toDGraph();
			boolean t = this.dg.containsCycle();
			System.out.println("tag " + i + ": " + t);
		}
	}

	public final void testContainsVertex() {
		System.out.println("ContainsVertexTest:");
		for(int i = 0; i < 10; i++) {
			assert(this.dg.containsVertex(i));
		}
		for(int i = 11; i < 16; i++) {
			assert(!this.dg.containsVertex(i));
		}
	}

	public final void testIncreaseDWTLenLimit() {
		//TODO: problem, it takes to much time to increase the bound to 3n^2+1 
		System.out.println("IncreaseDWTLenLimitTest:");
		this.ddg.increaseDWTLenLimit();
		for(int i = 0; i < 5; i++) {
			this.dg = this.ocdg.genRandomOca(5).toDGraph();
			this.dg.increaseDWTLenLimit();
		}
	}

	public void tearDown() {
		
	}

}
