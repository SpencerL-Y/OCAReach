package other;

import graph.directed.DGraph;
import parser.OCDGenerator;

public class DGraphAddEdge {
	public static void main(String[] args) {
		System.out.println("init");
		OCDGenerator ocdg = new OCDGenerator();
		System.out.println("toG");
		DGraph g = ocdg.genRandomOca(10).toDGraph();
		System.out.println("addE");
		g.addEdge(1, 2, 1);
		System.out.println("over");
	}
}
