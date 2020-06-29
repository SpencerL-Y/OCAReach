package automata.counter.gen;

import automata.State;
import automata.Transition;
import automata.counter.OCA;
import automata.counter.OCAOp;
import graph.directed.DGraph;

public class OCAGen extends OCA{
	private OCAOpGen ocaOpGen;
	private int maxAbsVal;
	
	public OCAGen(int maxAbsVal) {
		super();
		this.setOcaOpGen(new OCAOpGen(maxAbsVal));
		this.setMaxAbsVal(maxAbsVal);
	}
	
	@Override
	public void addTransition(int fromIndex, OCAOp op, int toIndex) {
		System.out.println("ERROR: OCAOp not available");
		return;
	}
	
	public void addTransition(int fromIndex, int counterChange, int toIndex) {
		if(!(Math.abs(counterChange) <= this.getMaxAbsVal())) {
			System.out.println("ERROR: counter Change absValue error");
			return;
		}
		OCATranGen tran = new OCATranGen(this.getState(fromIndex),
										 counterChange,
										 this.getState(toIndex), this);
		this.getState(fromIndex).addTransition(tran);
	}
	
	@Override
	public boolean testZeroTransition(Transition t) {
		if(t.getLabel().equals("0")) {
			return true;
		}
		return false;
	}
	

	@Override
	public OCA removeZeroTransitionOCA() {
		System.out.println("ERROR: OCAOp not available");
		return null;
	}
	
	public OCAGen removeZeroTransitionOCAGen() {
		System.out.println("Original: start: " + this.getInitIndex() + " end: " +  this.getTargetIndex());
		OCAGen newOca = new OCAGen(this.getMaxAbsVal());
		for(State s : this.getStates()) {
			newOca.addState(s.getIndex());
		}
		for(State s : this.getStates()) {
			for(Transition t : s.getTransitions()) {
				if(!t.getLabel().equals("0")) {
					newOca.addTransition(t.getFrom().getIndex(),
										 Integer.parseInt(t.getLabel()),
										 t.getTo().getIndex());
				}
			}
		}
		newOca.setInitIndex(this.getInitIndex());
		newOca.setTargetIndex(this.getTargetIndex());
		System.out.println("New: start: " + newOca.getInitIndex() + " end: " +  newOca.getTargetIndex());
		return newOca;
	}
	
	@Override
	public DGraph toDGraph() {
		DGraph udg = new DGraph(this.getMaxAbsVal());
		for(State s : this.getStates()) {
			udg.addVertex(s.getIndex());
		}
		for(State s : this.getStates()) {
			for(Transition t : s.getTransitions()) {
				int fromIndex = t.getFrom().getIndex();
				int toIndex = t.getTo().getIndex();
				int weight = 0;
				weight = Integer.parseInt(t.getLabel());
				udg.addEdge(fromIndex, toIndex, weight);
			}
		}
		udg.setStartVertexIndex(this.getInitIndex());
		udg.setEndingVertexIndex(this.getTargetIndex());
		return udg;
	}
	
	
	// getters and setters
	public OCAOpGen getOcaOpGen() {
		return ocaOpGen;
	}

	public void setOcaOpGen(OCAOpGen ocaOpGen) {
		this.ocaOpGen = ocaOpGen;
	}

	public int getMaxAbsVal() {
		return maxAbsVal;
	}

	public void setMaxAbsVal(int maxAbsVal) {
		this.maxAbsVal = maxAbsVal;
	}
}
