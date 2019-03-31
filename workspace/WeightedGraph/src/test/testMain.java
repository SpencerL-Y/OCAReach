package test;

import graph.direct.DGraph;
import graph.direct.DGraphUtil;
import graph.direct.Path;

public class testMain {
	public static void main(String[] args) {
		DGraph tg = new DGraph();
		tg.addVertex(0);
		tg.addVertex(1);
		tg.addVertex(2);
		tg.addVertex(3);
		tg.addEdge(0, 1, 2);
		tg.addEdge(0, 2, 3);
		tg.addEdge(1, 0, 1);
		tg.addEdge(1, 3, 5);
		tg.addEdge(2, 3, 1);
		Path p = tg.dfs(2);
		DGraphUtil.printAdjMatrix(tg);
		p.printPath();
	}
}
