package ocareach;

import automata.State;
import automata.counter.OCA;
import graph.directed.DGraph;

public class ConverterZero extends ConverterOpt{

	public ConverterZero(OCA oca) {
		super(oca);
	}
	
	@Override
	public String convert() {
		if(this.oca.containsZeroTransition()) {
			return this.convertZero(this.oca.getInitState(), this.oca.getTargetState());
		} else {
			return this.convert(this.oca.getInitState(), this.oca.getTargetState());
		}
	}
	
	public String convertZero(State startState, State endState) {
		DGraph dg = this.oca.toDGraph();
		return null;
		
	}
}
