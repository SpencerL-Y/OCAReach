package graph.directed;

public class DGCycle extends DGPath{
	
	public DGCycle(DGPath s, DGVertex start) {
		super(start);
		assert(s.isCycle() && s.contains(start));
		if(start == s.getVertex(0)) {
			this.setPath(s.getPath());
		} else {
			for(int i = s.getVertexIndex(start); i <= s.getLength(); i ++) {
				this.concatVertex(s.getVertex(i));
			}
			for(int i = 0; i <= s.getVertexIndex(start); i++) {
				this.concatVertex(s.getVertex(i));
			}
		}
	}
	
	public DGVertex getStartVertex() {
		return this.getVertex(0);
	}
}
