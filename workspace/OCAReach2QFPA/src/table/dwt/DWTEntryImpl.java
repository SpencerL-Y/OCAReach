package table.dwt;

import java.util.ArrayList;
import java.util.List;

import graph.directed.DGVertex;
import graph.directed.Vertex;

public class DWTEntryImpl implements DWTEntry {
	
	private DGVertex startVertex;
	private DGVertex endVertex;
	private int maxLength;
	private List<DWTuple> setOfDWTuples;
	
	public DWTEntryImpl(DGVertex startVertex, DGVertex endVertex, int maxLength){
		this.setStartVertex(startVertex);
		this.setEndVertex(endVertex);
		this.maxLength = maxLength;
		this.setOfDWTuples = new ArrayList<DWTuple>();
	}
	
	@Override
	public DGVertex getStartVertex() {
		return this.startVertex;
	}

	@Override
	public DGVertex getEndVertex() {
		return this.endVertex;
	}

	@Override
	public int getStartIndex() {
		return this.getStartVertex().getIndex();
	}

	@Override
	public int getEndIndex() {
		return this.getEndVertex().getIndex();
	}

	@Override
	public int getMaxLength() {
		return this.maxLength;
	}

	@Override
	public List<DWTuple> getSetOfDWTuples() {
		return this.setOfDWTuples;
	}

	public void setSetOfDWTuples(List<DWTuple> setOfDWTuples) {
		this.setOfDWTuples = setOfDWTuples;
	}

	@Override
	public void setStartVertex(Vertex startVertex) {
		this.startVertex = (DGVertex)startVertex;
	}

	@Override
	public void setEndVertex(Vertex endVertex) {
		this.endVertex = (DGVertex)endVertex;
	}

	@Override
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	@Override
	public void addDWTuple(DWTuple tuple) {
		this.getSetOfDWTuples().add(tuple);
	}

	@Override
	public void printEntry() {
		System.out.println("----------------DWTEntry( " + this.getStartIndex()+" to " +this.getEndIndex()+" )---------------------");
		System.out.println("Tuples:");
		for(DWTuple t : this.getSetOfDWTuples()) {
			t.printTuple();
		}
		System.out.println("-------------------------------------------------------");
	}
}
