package graph.directed;

public class PosCycleTempl {
	private DGPath firstPath;
	private DGCycle simplePosCycle;
	
	public PosCycleTempl(DGPath first, DGCycle cycle) {
		assert(first.getLastVertex() == cycle.getStartVertex());
		this.setFirstPath(first);
		this.setSimplePosCycle(cycle);
	}
	
	// basic operations
	public int getFirstDrop() {
		return this.getFirstPath().getDrop();
	}
	
	public int getFirstWeight() {
		return this.getFirstPath().getWeight();
	}
	
	public int getLoopDrop() {
		return this.getSimplePosCycle().getDrop();
	}
	
	public int getLoopWeight() {
		return this.getSimplePosCycle().getWeight();
	}
	
	// setters and getters
	public DGPath getFirstPath() {
		return firstPath;
	}

	public void setFirstPath(DGPath firstPath) {
		this.firstPath = firstPath;
	}

	public DGPath getSimplePosCycle() {
		return simplePosCycle;
	}

	public void setSimplePosCycle(DGCycle simplePosCycle) {
		this.simplePosCycle = simplePosCycle;
	}
	
	
}
