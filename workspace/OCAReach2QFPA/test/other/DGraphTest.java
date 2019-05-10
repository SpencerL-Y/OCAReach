package other;

import automata.counter.OCA;
import graph.directed.DGraph;
import graph.directed.DGraphUtil;
import parser.OCDGenerator;

public class DGraphTest {
	public static void main(String[] args) {
		OCDGenerator ocdg = new OCDGenerator();
		OCA a = ocdg.genRandomOca(6);
		a.print();
		DGraph dg = a.toDGraph();
		DGraphUtil.printAdjMatrix(dg);
	}
	
	public void addVertex() {
		
	}
}
