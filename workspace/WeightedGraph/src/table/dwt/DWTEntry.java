package table.dwt;

import java.util.List;

public interface DWTEntry<V, G> {
	// V may be SDGVertex or DGVertex
	public V getStartVertex();
	public V getEndVertex();
	public int getStartIndex();
	public int getEndIndex();
	public int getMaxLength();
	public List<DWTuple> getSetOfDWTuples();
	public V setStartVertex(V startVertex);
	public V setEndVertex(V endVertex);
	public int setMaxLength(int maxLength);
	public void addDWTuple(DWTuple tuple);
	public G getGraph();
	public G setGraph();
}
