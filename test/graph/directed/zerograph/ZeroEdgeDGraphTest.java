package graph.directed.zerograph;

import java.util.ArrayList;
import java.util.List;

import formula.generator.QFPAGenerator;
import graph.directed.DGraph;
import junit.framework.TestCase;

public class ZeroEdgeDGraphTest extends TestCase {

	private DGraph graph;
	protected void setUp() throws Exception {
		this.graph = new DGraph(1);
		for(int i = 0; i < 6; i++) {
			this.graph.addVertex(i);
		}
		this.graph.setStartVertexIndex(0);
		this.graph.setEndingVertexIndex(0);
		this.graph.addEdge(0, 1, 1);
		this.graph.addEdge(0, 2, -1);
		this.graph.addEdge(0, 3, 0);
		this.graph.addEdge(3, 4, 0);
		this.graph.addEdge(4, 5, 1);
		this.graph.addEdge(1, 3, 1);
		this.graph.addEdge(2, 3, 1);
		this.graph.addEdge(5, 0, 1);
		this.graph.addEdge(3, 5, -1);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testZeroEdgeDGraph() {
		ZeroEdgeDGraph zg = new ZeroEdgeDGraph(this.graph);
		System.out.println("ZTVertex: ");
		for(ZTVertex v : zg.getVertices()) {
			System.out.println("from: " + v.getFrom() + " to: " + v.getTo() + " index: " + v.getIndex());
			
		}
		for(ZTVertex v : zg.getVertices()) {
			System.out.println("from: " + v.getFrom() + " to: " + v.getTo() + " index: " + v.getIndex());
			for(ZTEdge e : v.getEdges()) {
				System.out.println("edges: ");
				System.out.println("edge from: " + e.getFrom().getIndex() + " edge to: " + e.getTo().getIndex());
			}
		}
	}

	public void testDfsFindAllZTPath() {
		ZeroEdgeDGraph zg = new ZeroEdgeDGraph(this.graph);
		List<ZTPath> paths = zg.dfsFindAllZTPath();
		for(ZTPath p : paths) {
			p.print();
		}
	}

}
