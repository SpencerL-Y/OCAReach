package test;

import graph.directed.DGraph;
import graph.directed.DGraphUtil;
import graph.directed.Path;

public class TestGraph {
	public static void main(String[] args) {
		DGraph tg = new DGraph();
		tg.addVertex(0);
		tg.addVertex(1);
		tg.addVertex(2);
		tg.addVertex(3);
		tg.addEdge(0, 1, 2);
		tg.addEdge(0, 2, 3);
		tg.addEdge(1, 0, 1);
		tg.addEdge(1, 2, 5);
		tg.addEdge(2, 3, 1);
		Path p = tg.dfs(1);
		DGraphUtil.printAdjMatrix(tg);
		p.printPath();
	}
}
