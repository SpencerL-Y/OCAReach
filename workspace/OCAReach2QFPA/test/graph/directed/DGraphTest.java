package graph.directed;

import java.util.Random;

import junit.framework.TestCase;
import parser.OCDGenerator;

public class DGraphTest extends TestCase {
	
	
	
	private Random r;
	private OCDGenerator ocdg;
	private DGraph dg;
	
	public void setUp() {
		this.r = new Random();
		this.ocdg = new OCDGenerator();
		this.dg = ocdg.genRandomOca(10).toDGraph();
		
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
		fail();
		//TODO: test after the test of table
	}

	public final void testGetAllPossibleSupport() {
		fail();
		//TODO: test after other
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
		fail("Not yet implemented"); // TODO
	}

	public final void testIsConnected() {
		fail("Not yet implemented"); // TODO
	}

	public final void testIsSubgraphOf() {
		for(int i = 0; i < 10; i++) {
			DGraph sub = new DGraph();
			for(Vertex)
		}
	}

	public final void testUnion() {
		fail("Not yet implemented"); // TODO
	}

	public final void testContainsCycle() {
		fail("Not yet implemented"); // TODO
	}

	public final void testContainsVertex() {
		fail("Not yet implemented"); // TODO
	}

	public final void testIncreaseDWTLenLimit() {
		fail("Not yet implemented"); // TODO
	}

	public final void testGetVertices() {
		fail("Not yet implemented"); // TODO
	}
	
	public void tearDown() {
		
	}

}
