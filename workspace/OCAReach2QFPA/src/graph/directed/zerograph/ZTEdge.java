package graph.directed.zerograph;

public class ZTEdge {
	private ZTVertex from, to;
	
	public ZTEdge(ZTVertex from, ZTVertex to) {
		if(from == null || to == null) {
			System.out.println("ERROR: ZTEdge init error");
		}
		this.setFrom(from);
		this.setTo(to);
	}
	
	// setters and getters
	public ZTVertex getFrom() {
		return from;
	}

	public void setFrom(ZTVertex from) {
		this.from = from;
	}

	public ZTVertex getTo() {
		return to;
	}

	public void setTo(ZTVertex to) {
		this.to = to;
	}
	
}	
