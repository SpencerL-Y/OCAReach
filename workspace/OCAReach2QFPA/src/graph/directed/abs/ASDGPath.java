package graph.directed.abs;

import java.util.ArrayList;
import java.util.List;

import graph.directed.SDGVertex;
 
public class ASDGPath {
	private List<ASDGVertex> path;
	private ASDGraph g;
	
	public ASDGPath(ASDGVertex startVertex) {
		this.path = new ArrayList<ASDGVertex>();
		this.path.add(startVertex);
		this.setG(startVertex.getGraph());
	}
	
	//basic operations
	
	public void concatVertex(ASDGVertex vertex) {
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
	
	public ASDGPath concatPath(ASDGPath suffix) {
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
	
	// algorithm
	
	//TODO: debug
	public List<List<SDGVertex>>inportsOutportsCartesianProduct(SDGVertex start, SDGVertex end) {
		List<List<SDGVertex>> list = new ArrayList<List<SDGVertex>>();
		List<SDGVertex> startList = new ArrayList<SDGVertex>();
		startList.add(start);
		list.add(startList);
		ASDGraph g = this.getPath().get(0).getGraph();
		for(int i = 1; i < this.length(); i++) {
			List<List<SDGVertex>> connect = new ArrayList<List<SDGVertex>>();
			for(SDGVertex lastOut : this.getPath().get(i-1).getOutports()) {
				for(SDGVertex nextIn : this.getPath().get(i).getInports()) {
					if(g.containsBorderEdge(lastOut, nextIn)) {
						List<SDGVertex> newCon = new ArrayList<SDGVertex>();
						newCon.add(lastOut);
						newCon.add(nextIn);
					}
				}
			}
			this.connectIOSequence(list, connect);
		}
		for(List<SDGVertex> l : list) {
			l.add(end);
		}
		return list;
	}
	
	
	//TODO: debug
	private void connectIOSequence(List<List<SDGVertex>> list, List<List<SDGVertex>> connect){
		if(list.size() == 0) {
			for(List<SDGVertex> l : connect) {
				list.add(l);
			}
			return;
		}
		List<List<SDGVertex>> newList = new ArrayList<List<SDGVertex>>();
		for(List<SDGVertex> l : connect) {
			for(List<SDGVertex> pre : list) {
				List<SDGVertex> newSeq = new ArrayList<SDGVertex>();
				for(SDGVertex p : pre) {
					newSeq.add(p);
				}
				for(SDGVertex v : l) {
					newSeq.add(v);
				}
				newList.add(newSeq);
			}
		}
		list = newList;
	}
	
	
	//getters and setters
	public List<ASDGVertex> getPath() {
		return path;
	}

	public void setPath(List<ASDGVertex> path) {
		this.path = path;
	}

	public ASDGraph getG() {
		return g;
	}

	public void setG(ASDGraph g) {
		this.g = g;
	}
}
