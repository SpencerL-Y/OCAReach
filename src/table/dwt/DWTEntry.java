package table.dwt;

import java.util.List;

import graph.directed.Vertex;

public interface DWTEntry {
	public Vertex getStartVertex();
	public Vertex getEndVertex();
	public int getStartIndex();
	public int getEndIndex();
	public int getMaxLength();
	public List<DWTuple> getSetOfDWTuples();
	public void setStartVertex(Vertex startVertex);
	public void setEndVertex(Vertex endVertex);
	public void setMaxLength(int maxLength);
	public void addDWTuple(DWTuple tuple);
	public void printEntry();
}
