package table.dwt;

import java.util.ArrayList;
import java.util.List;

import graph.directed.DGEdge;
import graph.directed.Graph;
import graph.directed.SDGVertex;
import graph.directed.DGVertex;
import graph.directed.SDGraph;

public class DWTableImpl implements DWTable {
	
	private SDGraph graph;
	private int maxLength;
	private List<DWTEntry> entryList;
	
	
	public DWTableImpl(SDGraph graph) {
		this.graph = graph;
		this.setMaxLength(0);
		this.setEntryList(new ArrayList<DWTEntry>());
	}
	
	// algorithm 
	
	//TODO: debug
	private void updateByGraph() {
		boolean updated = true;
		for(DWTEntry e : this.getEntryList()) {
			if(!(e.getMaxLength() == this.getMaxLength())) {
				updated = false;
			}
		}
		if(!updated) {
			//do updating
			if(this.getMaxLength() == 1) {
				// just add all the edge information
				for(SDGVertex v : this.graph.getVertices()) {
					for(DGEdge e : v.getEdges()) {
						DWTEntry entry = new DWTEntryImpl(v.getVertex(), e.getTo(), 1);
						this.getEntryList().add(entry);
						//TODO use hash table to increase efficiency?
					}
				}
			} else {
				// find all possible maxLen_1 + maxLen_2 = maxLen entries and compute
				for(int prefix = 1; prefix < this.getMaxLength(); prefix++) {
					int suffix = this.getMaxLength() - prefix;
					for(DWTEntry ep : this.getMaxLengthList(prefix)) {
						for(DWTEntry es : this.getMaxLengthList(suffix)) {
							if(ep.getEndIndex() == es.getStartIndex()) {
								// merge the path 
								DWTEntryImpl addToEntry = (DWTEntryImpl) this.getEntry(ep.getStartIndex(), es.getEndIndex());
								if(addToEntry == null) {
									addToEntry = new DWTEntryImpl((DGVertex)ep.getStartVertex(), 
																  (DGVertex)es.getEndVertex(), this.getMaxLength());
									this.mergeTupleList(ep.getSetOfDWTuples(), es.getSetOfDWTuples(), addToEntry.getSetOfDWTuples());
								} else {
									//update the length
									addToEntry.setMaxLength(this.getMaxLength());
									this.mergeTupleList(ep.getSetOfDWTuples(), es.getSetOfDWTuples(), addToEntry.getSetOfDWTuples());
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void mergeTupleList(List<DWTuple> pre, List<DWTuple> suf, List<DWTuple> merge) {
		for(DWTuple tp : pre) {
			for(DWTuple ts : suf) {
				DWTuple newT = DWTuple.merge(tp, ts);
				this.wiselyAddTuple(merge, newT);
			}
		}
	}
	
	private void wiselyAddTuple(List<DWTuple> merge, DWTuple add) {
		for(int i = 0; i < merge.size(); i ++) {
			DWTuple exists = merge.get(i);
			if(exists.getWeight() == add.getWeight()) {
				if(exists.getDrop() > add.getDrop()) {
					// replace the smaller drop tuple
					exists = add;
					return;
				} else {
					// not added
					return;
				}
			}
		}
		//different weight, added
		merge.add(add);
	}
	
	
	//interfaces
	
	@Override
	public DWTEntry getEntry(int startIndex, int endIndex) {
		for(DWTEntry e : this.getEntryList()) {
			if(e.getStartIndex() == startIndex && e.getEndIndex() == endIndex) {
				return e;
			}
		}
		//System.out.println("WARNING: DWTEntry not found");
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
	public List<DWTEntry> getMaxLengthList(int maxLen) {
		List<DWTEntry> list = new ArrayList<DWTEntry>();
		for(DWTEntry e : this.getEntryList()) {
			if(e.getMaxLength() == maxLen) {
				list.add(e);
			}
		}
		return list;
	}

	@Override
	public void increMaxLenUpdate() {
		this.maxLength += 1;
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
