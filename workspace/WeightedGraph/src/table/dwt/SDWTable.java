package table.dwt;

import java.util.ArrayList;
import java.util.List;

import graph.directed.Graph;
import graph.directed.SDGraph;

public class SDWTable implements DWTable {
	
	private SDGraph graph;
	private int maxLength;
	private List<DWTEntry> entryList;
	
	
	public SDWTable(SDGraph graph) {
		this.graph = graph;
		this.setMaxLength(0);
		this.setEntryList(new ArrayList<DWTEntry>());
	}
	
	// algorithm 
	
	private void updateByGraph() {
		//TODO
	}
	
	//interfaces
	
	@Override
	public DWTEntry getEntry(int startIndex, int endIndex) {
		for(DWTEntry e : this.getEntryList()) {
			if(e.getStartIndex() == startIndex && e.getEndIndex() == endIndex) {
				return e;
			}
		}
		System.out.println("ERROR: DWTEntry not found");
		System.out.checkError();
		return null;
	}

	@Override
	public List<DWTEntry> getStartEntryList(int startIndex) {
		List<DWTEntry> startList = new ArrayList<DWTEntry>();
		for(DWTEntry e : this.getEntryList()) {
			if(e.getStartIndex() == startIndex) {
				startList.add(e);
			}
		}
		return startList;
	}

	@Override
	public List<DWTEntry> getEndEntryList(int endIndex) {
		List<DWTEntry> endList = new ArrayList<DWTEntry>();
		for(DWTEntry e : this.getEntryList()) {
			if(e.getEndIndex() == endIndex) {
				endList.add(e);
			}
		}
		return endList;
	}

	@Override
	public void increMaxLenUpdate() {
		this.maxLength += 1;
		//TODO check
		this.updateByGraph();
	}

	@Override
	public Graph getGraph() {
		return this.graph;
	}

	@Override
	public void setGraph(Graph graph) {
		this.graph = (SDGraph)graph;
	}
	
	//getters and setters
	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public List<DWTEntry> getEntryList() {
		return entryList;
	}

	public void setEntryList(List<DWTEntry> entryList) {
		this.entryList = entryList;
	}

}
