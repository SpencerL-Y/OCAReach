package automata.counter;

import automata.State;
import automata.Transition;

public class OCATran implements Transition {
	
	private State from;
	private State to;
	private OCAOp op;
	
	
	public OCATran(State from, State to, OCAOp op) {
		this.setFrom(from);
		this.setTo(to);
		this.setOp(op);
	}
	
	// Interface
	@Override
	public State getFrom() {
		return this.from;
	}

	@Override
	public State getTo() {
		return this.to;
	}

	@Override
	public String getLabel() {
		//TODO: GEN number OCAOp
		if(this.getOp() == OCAOp.Add) {
			return "add";
		} else if(this.getOp() == OCAOp.Sub) {
			return "sub";
		} else if(this.getOp() == OCAOp.Zero){
			return "zero";
		} else {
			return null;
		}
	}
	
	
	@Override
	public void print() {
		System.out.println("("+this.getFrom().getIndex()+","+this.getLabel()+","+this.getTo().getIndex()+")");
	}
	
	//getters and setters
	public void setFrom(State from) {
		this.from = from;
	}

	public void setTo(State to) {
		this.to = to;
	}
	
	public OCAOp getOp() {
		return this.op;
	}
	
	public void setOp(OCAOp op) {
		this.op = op;
	}



}
