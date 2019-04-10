package graph.directed.abs;

import java.util.ArrayList;
import java.util.List;

public class ASDGPath {
	private List<ASDGVertex> path;
	
	public ASDGPath(ASDGVertex startVertex) {
		this.path = new ArrayList<ASDGVertex>();
		this.path.add(startVertex);
	}
	
	//basic operations
	
	public void append(ASDGVertex vertex) {
		ASDGVertex v = this.getLastVertex();
		if(v.checkAbsEdge(vertex.getSccIndex())) {
			this.getPath().add(vertex);
			return;
		}
		System.out.println("ERROR: append not valid, abstract edge not exists");
	}
	
	public ASDGVertex getLastVertex() {
		return this.getPath().get(this.getPath().size() - 1);
	}
	
	public void removeLastVertex() {
		if(this.getPath().size() == 1) {
			System.out.println("WARNING: AbsPath is already empty");
			return;
		}
		this.getPath().remove(this.getPath().size() - 1);
	}
	
	public ASDGVertex getVertex(int position) {
		return this.getPath().get(position);
	}
	
	public ASDGVertex getInit() {
		return this.getPath().get(0);
	}
	
	public ASDGPath concat(ASDGPath suffix) {
		ASDGPath p = new ASDGPath(this.getInit());
		for(int i = 1; i <= this.length(); i++) {
			p.getPath().add(p.getVertex(i));
		}
		if(suffix.getInit() == p.getLastVertex()) {
			for(int i = 1; i <= suffix.length(); i++) {
				p.getPath().add(suffix.getVertex(i));
			}
			return p;
		}
		System.out.println("ERROR: abspath concat error");
		return null;
	}
	
	public boolean containsPosTagVertex() {
		for(ASDGVertex v : this.getPath()) {
			if(v.getLoopTag() == LoopTag.Pos || v.getLoopTag() == LoopTag.PosNeg) {
				return true;
			} 
		}
		return false;
	}
	
	public boolean containsNegTagVertex() {
		for(ASDGVertex v : this.getPath()) {
			if(v.getLoopTag() == LoopTag.Neg || v.getLoopTag() == LoopTag.PosNeg) {
				return true;
			} 
		}
		return false;
	}
	
	public boolean containsCycledVertex() {
		for(ASDGVertex v : this.getPath()) {
			if(v.getLoopTag() != LoopTag.None) {
				return true;
			} 
		}
		return false;
	}
	
	public int length() {
		return this.getPath().size() - 1;
	}
	
	//getters and setters
	public List<ASDGVertex> getPath() {
		return path;
	}

	public void setPath(List<ASDGVertex> path) {
		this.path = path;
	}
}
