package graph.directed.abs;

import graph.directed.DGraph;
import graph.directed.SDGraph;
import junit.framework.TestCase;
import parser.OCDGenerator;

public class ASDGraphTest extends TestCase {
	
	private OCDGenerator ocdg;
	private DGraph kdg;
	private DGraph rdg;
	private SDGraph sdg;
	private ASDGraph asdg;

	protected void setUp() throws Exception {
		this.ocdg = new OCDGenerator();
		this.kdg = new DGraph();
		for(int i = 0; i < 6; i++) {
			this.kdg.addVertex(i);
		}
		this.kdg.addEdge(0, 1, 1);
		this.kdg.addEdge(1, 2, -1);
		this.kdg.addEdge(2, 0, -1);
		this.kdg.addEdge(1, 3, 1);
		this.kdg.addEdge(1, 5, 1);
		this.kdg.addEdge(5, 3, -1);
		this.kdg.addEdge(3, 4, 1);
		this.kdg.addEdge(4, 3, -1);
		this.sdg = new SDGraph(this.kdg);
		this.sdg.tarjan();
		this.asdg = new ASDGraph(this.sdg);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testContainsBorderEdge() {
		System.out.println("ConstainsBorderEdgeTest:");
		System.out.println("contains 0 -> 1 : " + this.asdg.containsBorderEdge(this.sdg.getVertex(0), this.sdg.getVertex(1)));
		System.out.println("contains 1 -> 3 : " + this.asdg.containsBorderEdge(this.sdg.getVertex(1), this.sdg.getVertex(3)));
		
	}

	public final void testContainsAbsEdgeIntInt() {
		fail("Not yet implemented"); // TODO
	}

	public final void testContainsAbsEdgeASDGVertexASDGVertex() {
		fail("Not yet implemented"); // TODO
	}

	public final void testGetAbsEdge() {
		fail("Not yet implemented"); // TODO
	}

	public final void testGetBorderEdgesByAbsEdge() {
		fail("Not yet implemented"); // TODO
	}

	public final void testGetBorderEdgeByInportOutport() {
		fail("Not yet implemented"); // TODO
	}

	public final void testGetSkewTranspose() {
		fail("Not yet implemented"); // TODO
	}

	public final void testDFSFindAbsPaths() {
		fail("Not yet implemented"); // TODO
	}

}
