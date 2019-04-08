package table.dwt;

import java.util.List;

public class DWTEntryImpl<V, G> implements DWTEntry<V, G> {
	
	private V startVertex;
	private V endVertex;
	private int maxLength;
	private List<DWTuple> setOfDWTuples;
	private G graph;
	
	public DWTEntryImpl(V startVertex, V endVertex){
		this.setStartVertex(startVertex);
		this.maxLength = 0;
	}
	
	@Override
	public V getStartVertex() {
		return this.startVertex;
	}

	@Override
	public V getEndVertex() {
		return this.endVertex;
	}

	@Override
	public int getStartIndex() {
		return this.getStartVertex().
	}

	@Override
	public int getEndIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<DWTuple> getSetOfDWTuples() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSetOfDWTuples(List<DWTuple> setOfDWTuples) {
		this.setOfDWTuples = setOfDWTuples;
	}

	@Override
	public V setStartVertex(V startVertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V setEndVertex(V endVertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int setMaxLength(int maxLength) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addDWTuple(DWTuple tuple) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public G getGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G setGraph() {
		// TODO Auto-generated method stub
		return null;
	}

}
