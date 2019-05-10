package automata.counter;

import java.util.Random;

import automata.counter.OCA;
import graph.directed.DGraph;
import junit.framework.TestCase;
import parser.OCDGenerator;

public class OCATest extends TestCase {

	public final void testToDGraph() {
		for(int i = 0; i < 1000; i ++) {
			OCDGenerator ocdg = new OCDGenerator();
			Random r = new Random();
			OCA a = ocdg.genRandomOca(r.nextInt(9)+1);
			a.print();
			@SuppressWarnings("unused")
			DGraph dg = a.toDGraph();
		}
	}
	
	public final void testAddEdge() {
		for(int i = 0; i < 100; i ++) {
			OCDGenerator ocdg = new OCDGenerator();
			Random r = new Random();
			OCA a = ocdg.genRandomOca(10);
			a.addTransition(r.nextInt(10), r.nextBoolean() ? OCAOp.Add:OCAOp.Sub, r.nextInt(10));
		}
	}
}
