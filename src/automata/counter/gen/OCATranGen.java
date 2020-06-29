package automata.counter.gen;

import automata.State;
import automata.counter.OCATran;

public class OCATranGen extends OCATran{
	private OCAGen ocaGen;
	
	public OCATranGen(State from, int counterChange, State to, OCAGen oca) {
		super(from, to, null);
		this.ocaGen = oca;
		this.setOp(this.getOcaGen().getOcaOpGen().getSymbol(counterChange));
	}
	
	@Override
	public String getLabel() {
		return this.getOp().getLabel();
	}

	public OCAGen getOcaGen() {
		return ocaGen;
	}

	public void setOcaGen(OCAGen ocaGen) {
		this.ocaGen = ocaGen;
	}
}
