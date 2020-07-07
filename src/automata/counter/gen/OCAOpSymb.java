package automata.counter.gen;

import automata.Operation;

public class OCAOpSymb implements Operation{
	private int counterChange;
	
	public OCAOpSymb(int counterChange) {
		this.setCounterChange(counterChange);
	}

	public int getCounterChange() {
		return counterChange;
	}

	public void setCounterChange(int counterChange) {
		this.counterChange = counterChange;
	}

	@Override
	public String getLabel() {
		return Integer.toString(counterChange);
	}
}
