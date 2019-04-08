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
	
	public OCA() {
		this.states = new ArrayList<State>();
		this.setInitIndex(-1);
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
	
	public void addTransition(int fromIndex, OCAOp op, int toIndex) {
		OCATran tran = new OCATran(this.getState(fromIndex), this.getState(toIndex), op);
		this.getState(fromIndex).addTransition(tran);
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
					System.out.checkError();
				}
				udg.addEdge(fromIndex, toIndex, weight);
			}
		}
		udg.setStartVertexIndex(this.getInitIndex());
		return udg;
	}
	
	// interfaces
	@Override
	public void print() {
		System.out.println("InitState: "+this.getInitIndex());
		for(State s : this.getStates()) {
			System.out.println("State: " + s.getIndex()+" ");
			for(Transition t : s.getTransitions()) {
				t.print();
			}
		}
		System.out.println();
	}
}
