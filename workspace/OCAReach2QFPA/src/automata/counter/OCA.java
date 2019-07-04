package automata.counter;

import java.util.ArrayList;
import java.util.List;

import automata.Automaton;
import automata.State;
import automata.Transition;
import graph.directed.DGraph;

public class OCA implements Automaton{
	private List<State> states;
	private int initIndex;
	private int targetIndex;
	
	public OCA() {
		this.states = new ArrayList<State>();
		this.initIndex = -1;
		this.targetIndex = -1;
	}
	
	//basic operations
	public void addState(int index) {
		for(State s : this.getStates()) {
			if(s.getIndex() == index) {
				System.out.println("ERROR: state already exists");
				assert(false);
				return;
			}
		}
		this.states.add(new State(index));
	}
	
	public State getState(int index) {
		for(State s : this.getStates()) {
			if(s.getIndex() == index) {
				return s;
			}
		}
		System.out.println("ERROR: State not found");
		return null;
	}
	
	public State getInitState() {
		return this.getState(this.getInitIndex());
	}
	
	public State getTargetState() {
		return this.getStates().get(this.getTargetIndex());
	}
	
	public void addTransition(int fromIndex, OCAOp op, int toIndex) {
		OCATran tran = new OCATran(this.getState(fromIndex), this.getState(toIndex), op);
		this.getState(fromIndex).addTransition(tran);
	}
	
	public boolean containsState(State state) {
		for(State s : this.getStates()) {
			if(state == s) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsZeroTransition() {
		for(State s : this.getStates()) {
			for(Transition t : s.getTransitions()) {
				if(t.getLabel().equals("zero")) {
					return true;
				}
			}
		}
		return false;
	}
	
	public OCA removeZeroTransitionOCA() {
		OCA newOca = new OCA();
		for(State s : this.getStates()) {
			newOca.addState(s.getIndex());
		}
		for(State s : this.getStates()) {
			for(Transition t : s.getTransitions()) {
				if(!t.getLabel().equals("zero")) {
					if(t.getLabel().equals("add")) {
						newOca.addTransition(s.getIndex(), OCAOp.Add, t.getTo().getIndex());
					} else if(t.getLabel().equals("sub")) {
						newOca.addTransition(s.getIndex(), OCAOp.Sub, t.getTo().getIndex());
					}
				}
			}
		}
		newOca.setInitIndex(this.getInitIndex());
		newOca.setTargetIndex(this.getTargetIndex());
		return newOca;
	}
	
	
	// getters and setters
	public List<State> getStates() {
		return states;
	}

	public void setStates(List<State> states) {
		this.states = states;
	}

	public int getInitIndex() {
		return initIndex;
	}

	public void setInitIndex(int initIndex) {
		for(State s : this.getStates()) {
			if(s.getIndex() == initIndex) {
				this.initIndex = initIndex;
				return;
			}
		}
		if(initIndex != -1) {
			System.out.println("ERROR: init index does not exists");
		}
	}
	
	
	// transformation
	
	public DGraph toDGraph() {
		DGraph udg = new DGraph();
		for(State s : this.getStates()) {
			udg.addVertex(s.getIndex());
		}
		for(State s : this.getStates()) {
			for(Transition t : s.getTransitions()) {
				int fromIndex = t.getFrom().getIndex();
				int toIndex = t.getTo().getIndex();
				int weight = 0;
				if(t.getLabel().equals("add")) {
					weight = 1;
				} else if(t.getLabel().equals("sub")) {
					weight = -1;
				} else if(t.getLabel().equals("zero")) {
					weight = 0;
				} else {
					assert(false);
				}
				udg.addEdge(fromIndex, toIndex, weight);
			}
		}
		udg.setStartVertexIndex(this.getInitIndex());
		udg.setEndingVertexIndex(this.getTargetIndex());
		return udg;
	}
	
	// interfaces
	@Override
	public void print() {
		System.out.println("InitState: "+this.getInitIndex());
		System.out.println("TargetState: " + this.getTargetIndex());
		for(State s : this.getStates()) {
			System.out.println("State: " + s.getIndex()+" ");
			for(Transition t : s.getTransitions()) {
				t.print();
			}
		}
		System.out.println();
	}

	public int getTargetIndex() {
		return targetIndex;
	}

	public void setTargetIndex(int targetIndex) {
		for(State s : this.getStates()) {
			if(s.getIndex() == targetIndex) {
				this.targetIndex = targetIndex;
				return;
			}
		}
		if(initIndex != -1) {
			System.out.println("ERROR: init index does not exists");
		}
	}
}
