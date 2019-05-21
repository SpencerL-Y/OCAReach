package graph.directed.abs;

import java.util.List;
import java.util.Random;

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
	private Random r;

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
		this.r = new Random();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testContainsBorderEdge() {
		System.out.println("ConstainsBorderEdgeTest:");
		System.out.println("contains 0 -> 1 : " + this.asdg.containsBorderEdge(this.sdg.getVertex(0), this.sdg.getVertex(1)));
		System.out.println("contains 1 -> 3 : " + this.asdg.containsBorderEdge(this.sdg.getVertex(1), this.sdg.getVertex(3)));
		for(int i = 0; i < 10; i++) {
			this.rdg = this.ocdg.genRandomOca(10).toDGraph();
			this.sdg = new SDGraph(this.rdg);
			this.sdg.tarjan();
			this.asdg = new ASDGraph(this.sdg);
			System.out.println("contains: " + this.asdg.containsBorderEdge(this.sdg.getVertex(1), this.sdg.getVertex(2)));
		}
	}

	
	public final void testGetVertex() {
		System.out.println("GetVertexTest: ");
		this.asdg.getVertex(1);
		this.asdg.getVertex(2);
		this.asdg.getVertex(3);
	}
	
	public final void testContainsAbsEdgeIntInt() {
		System.out.println("ContainsAbsEdgeTest: ");
		System.out.println("kdg scc number: " + this.sdg.getSccNum());
		System.out.println("containSccEdge 1 -> 3: " + this.asdg.containsAbsEdge(1, 3));
		System.out.println("containsSccEdge 1 -> 2: " + this.asdg.containsAbsEdge(1, 2));

		System.out.println("containSccEdge 3 -> 1: " + this.asdg.containsAbsEdge(3, 1));
		System.out.println("containsSccEdge 3 -> 2: " + this.asdg.containsAbsEdge(3, 2));

		System.out.println("containSccEdge 2 -> 1: " + this.asdg.containsAbsEdge(2, 1));
		System.out.println("containsSccEdge 2 -> 3: " + this.asdg.containsAbsEdge(2, 3));
		
	}


	public final void testGetAbsEdge() {
		System.out.println("GetAbsEdgeTest: ");
		for(int i = 0; i < 10; i++) {
			this.rdg = this.ocdg.genRandomOca(5).toDGraph();
			this.sdg = new SDGraph(this.rdg);
			this.sdg.tarjan();
			this.asdg = new ASDGraph(this.sdg);
			this.asdg.getAbsEdge(this.r.nextInt(this.sdg.getSccNum()) + 1, this.r.nextInt(this.sdg.getSccNum()) + 1);
		}
	}

	public final void testGetBorderEdgesByAbsEdge() {
		System.out.println("GetBorderEdgesByAbsEdge:");
		System.out.println("sccEdge 3 -> 1:");
		
		for(BorderEdge e : this.asdg.getBorderEdgesByAbsEdge(3, 1)) {
			System.out.println("from " + e.getConcreteEdge().getFrom().getIndex() + " to " + e.getConcreteEdge().getTo().getIndex());
		}
	}

	public final void testGetBorderEdgeByInportOutport() {
		System.out.println("GetBorderEdgeByInportOutport:");
		//TODO: test later
	}

	public final void testGetSkewTranspose() {
		System.out.println("GetSkewTransposeTest:");
		for(int i = 0; i < 10; i++) {
			this.rdg = this.ocdg.genRandomOca(5).toDGraph();
			this.sdg = new SDGraph(this.rdg);
			this.sdg.tarjan();
			this.asdg = new ASDGraph(this.sdg);
			this.asdg.getSkewTranspose();
		}
	}

	public final void testDFSFindAbsPaths() {
		System.out.println("DFSFindAbsPathsTest:");
		System.out.println("known:");
		List<ASDGPath> ps = this.asdg.DFSFindAbsPaths(3, 1);
		for(ASDGPath p : ps) {
			p.print();
		}
	}

}
