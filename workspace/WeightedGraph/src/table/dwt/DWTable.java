package table.dwt;

import java.util.HashSet;
import java.util.Set;

import graph.directed.DGraph;
import graph.directed.Vertex;

public class DWTable extends Table{
	// this implementation is not so efficient
	private int maxLength;
	private DGraph graph;
	
	public DWTable() {
		this.setMaxLength(0);
		// add a row for each pair of vertices
		this.addCol("fromIndex");
		this.addCol("toIndex");
		this.addCol("MaxLen");
		this.addCol("DWTupleSet");
		
		for(Vertex v : this.getGraph().getVertices()) {
			for(Vertex w : this.getGraph().getVertices()) {
				this.addRow(v.getIndex(), w.getIndex(), 0, new HashSet<DWTuple>(0));
			}
		}
	}
	
	public void increaseAndUpdate() {
		this.increaseMaxLen();
		this.updateTable();
	}
	
	private void increaseMaxLen() {
		this.setMaxLength(getMaxLength() + 1);
		this.updateTable();
	}
	
	private void updateTable() {
		// Change the maxLength of DWT
		// change all set in the entries by finding the path on the graph
		// Remember to remove all the redundant tuples in the set when updating 
		// Learn how to use the iterator of the set
		//TODO
		
		
	}
	
	public void addRow(int fromIndex, int toIndex, int maxLen, Set<DWTuple> set) {
		this.addRow(fromIndex, toIndex, maxLen, set);
	}
	
	public void delRow() {
		//TODO
	}
	
	//getters and setters 
	public int getMaxLength() {
		return maxLength;
	}
	
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public DGraph getGraph() {
		return graph;
	}

	public void setGraph(DGraph graph) {
		this.graph = graph;
	}
}
