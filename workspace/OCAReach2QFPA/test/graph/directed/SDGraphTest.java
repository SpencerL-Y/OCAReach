package graph.directed;

import junit.framework.TestCase;
import parser.OCDGenerator;

public class SDGraphTest extends TestCase {
	
	private OCDGenerator ocdg;
	private DGraph kdg;
	private DGraph rdg;
	private SDGraph sdg;

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
		//this.kdg.addEdge(5, 2, 1);
		this.kdg.addEdge(3, 4, 1);
		this.kdg.addEdge(4, 3, -1);
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testGetConcreteSCC() {
		System.out.println("GetConcreteSCCTest:");
		this.sdg = new SDGraph(kdg);
		this.sdg.tarjan();
		for(SDGVertex v : this.sdg.getVertices()) {
			System.out.println("Vertex " + v.getVertexIndex() + " is in scc no."+ v.getSccMark() + " lowlink: " + v.getLowLink());
		}
		DGraph cg = this.sdg.getConcreteSCC(1);
		cg = this.sdg.getConcreteSCC(1);
		//cg = this.sdg.getConcreteSCC(3);
		System.out.println("vertices: ");
		for(DGVertex v : cg.getVertices()) {
			System.out.println(v.getIndex());
		}
		System.out.println("edges: ");
		for(DGVertex v : cg.getVertices()) {
			for(DGEdge e : v.getEdges()) {
				System.out.println("from " + e.getFrom().getIndex() + " to " + e.getTo().getIndex());
			}
		}
	}

	//TODO: need second review
	public final void testTarjan() {
		System.out.println("TarjanTest:");
		this.sdg = new SDGraph(kdg);
		this.sdg.tarjan();
		System.out.println("known: sccNum = " + this.sdg.getSccNum());
		for(SDGVertex v : this.sdg.getVertices()) {
			System.out.println("Vertex " + v.getVertexIndex() + " is in scc no."+ v.getSccMark() + " lowlink: " + v.getLowLink());
		}
			
		for(int i = 0; i < 10; i++) {
			this.rdg = this.ocdg.genRandomOca(10).toDGraph();
			this.sdg = new SDGraph(this.rdg);
			this.sdg.tarjan();
			System.out.println("sccNum = " + this.sdg.getSccNum());
		}
		
	}

	public final void testGetSkewTranspose() {
		System.out.println("getSkewTransposeTest:");
		for(int i = 0; i < 10; i++) {
			this.rdg = this.ocdg.genRandomOca(10).toDGraph();
			this.sdg = new SDGraph(this.rdg);
			this.sdg.getSkewTranspose();
		}
	}

	public final void testGetSccNum() {
		// tested in the tarjanTest
	}
}
