package table.dwt;

import java.util.List;

import graph.directed.SDGVertex;
import graph.directed.Vertex;

public class SDWTEntryImpl implements DWTEntry {
	
	private SDGVertex startVertex;
	private SDGVertex endVertex;
	private int maxLength;
	private List<DWTuple> setOfDWTuples;
	
	public SDWTEntryImpl(SDGVertex startVertex, SDGVertex endVertex){
		this.setStartVertex(startVertex);
		this.setEndVertex(endVertex);
		this.maxLength = 0;
	}
	
	@Override
	public SDGVertex getStartVertex() {
		return this.startVertex;
	}

	@Override
	public SDGVertex getEndVertex() {
		return this.endVertex;
	}

	@Override
	public int getStartIndex() {
		return this.getStartVertex().getVertex().getIndex();
	}

	@Override
	public int getEndIndex() {
		return this.getEndVertex().getVertex().getIndex();
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
		this.startVertex = (SDGVertex)startVertex;
	}

	@Override
	public void setEndVertex(Vertex endVertex) {
		this.endVertex = (SDGVertex)endVertex;
	}

	@Override
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	@Override
	public void addDWTuple(DWTuple tuple) {
		this.getSetOfDWTuples().add(tuple);
	}
}
