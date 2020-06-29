package graph.directed.zerograph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ZTVertex {
	private ZeroEdgeDGraph zg;
	private int index;
	private int from;
	private int to;
	private List<ZTEdge> edges;
	
	public ZTVertex(ZeroEdgeDGraph g, int index, int fromStateIndex, int toStateIndex) {
		this.setZg(g);
		this.setIndex(index);
		this.setFrom(fromStateIndex);
		this.setTo(toStateIndex);
		this.edges = new ArrayList<ZTEdge>();
	}
	
	// basic operations
	
	public void addEdge(int to) {
		ZTEdge e = new ZTEdge(this, this.getZg().getVertex(to));
		this.getEdges().add(e);
	}
	
	public boolean isInitVertex() {
		if(this.getIndex() == this.getZg().getStartZTIndex()) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isTargetVertex() {
		if(this.getIndex() == this.getZg().getTargetZTIndex()) {
			return true;
		} else {
			return false;
		}
	}
	
	// algorithm
	
	// TODO: debug
	public void dfsFindZTPath(List<ZTPath> foundList, Stack<ZTVertex> searched, int targetIndex) {
		if(this.isTargetVertex()) {
			System.out.println("push " + this.getIndex());
			searched.push(this);
			foundList.add(new ZTPath(searched));
			searched.pop();
			return;
		}
		
		for(ZTEdge e : this.getEdges()) {
			if(!searched.contains(this)) {
				System.out.println("push " + this.getIndex());
				searched.push(this);
				e.getTo().dfsFindZTPath(foundList, searched, targetIndex);
				searched.pop();
			}
		}
		return;
	}
	
	//debug
	
	public void printZeroEdge() {
		System.out.println(this.getFrom() + "---------->" + this.getTo());
	}
	
	
	// setters and getters
	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public List<ZTEdge> getEdges() {
		return edges;
	}

	public void setEdges(List<ZTEdge> edges) {
		this.edges = edges;
	}

	public ZeroEdgeDGraph getZg() {
		return zg;
	}

	public void setZg(ZeroEdgeDGraph zg) {
		this.zg = zg;
	}
	
	
}
