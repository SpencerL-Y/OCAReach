package automata;

import java.util.ArrayList;
import java.util.List;

public class State {
	
	private int index;
	private List<Transition> transitions;
	
	
	public State(int index) {
		this.setIndex(index);
		this.transitions = new ArrayList<Transition>();
	}
	
	public void addTransition(Transition tran) {
		if(this.checkTransitionExist(tran)) {
			System.out.println("ERROR: transition already exists");
			System.out.checkError();
		}
		this.transitions.add(tran);	
	}
	
	public void delTransition(int to) {
		for(Transition t : this.getTransitions()) {
			if(t.getTo().getIndex() == to) {
				this.getTransitions().remove(t);
				return;
			}
		}
		System.out.println("ERROR: Transition not found");
	}
	
	public boolean checkTransitionExist(Transition trans) {
		for(Transition t : this.transitions) {
			if(t.equals(trans)) {
				return true;
			} 
		}
		return false;
	}	
	//setters and getters
	
	public int getIndex() {
		return this.index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}

	public List<Transition> getTransitions() {
		return transitions;
	}

	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}
}
