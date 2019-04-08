package table.dwt;

import java.util.List;

import graph.directed.Graph;

public interface DWTable {
	// V may be SDGVertex or DGVertex
	public DWTEntry getEntry(int startIndex, int endIndex);
	public List<DWTEntry> getStartEntryList(int startIndex);
	public List<DWTEntry> getEndEntryList(int endIndex);
	public void increMaxLenUpdate();
	public Graph getGraph();
	public void setGraph(Graph graph);
}
