package automata.counter;

import java.util.ArrayList;
import java.util.List;

import automata.Automaton;
import automata.State;

public class OCA implements Automaton{
	private List<State> states;
	private int initIndex;
	
	public OCA() {
		this.states = new ArrayList<State>();
		this.setInitIndex(-1);
	}
	
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
}
