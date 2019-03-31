package graph.directed;

public class DGraphUtil {
	public static void printAdjMatrix(DGraph g) {
		System.out.print("# ");
		for(Vertex v : g.getVertices()) {
			System.out.print(v.getIndex() + " ");
		}
		System.out.println();
		for(Vertex v : g.getVertices()) {
			System.out.print(v.getIndex() + " ");
			for(Vertex ve : g.getVertices()) {
				int flag = 0;
				for(Edge e : v.getEdges()) {
					if(e.getTo().getIndex() == ve.getIndex()) {
						System.out.print(e.getWeight() + " ");
						flag = 1;
						break;
					}
				}
				if(flag == 0) {
					System.out.print(0 + " ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
}
