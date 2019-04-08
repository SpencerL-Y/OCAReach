package parser;

import java.util.Random;

import automata.counter.OCA;
import automata.counter.OCAOp;

public class OCDGenerator {
	private OCA tempOca;
	
	public OCDGenerator() {
		
	}
	
	public String generateRandomOcd(int stateNum) {
		this.generateRandomOca(stateNum);
		return this.generateOcd();
	}
	
	public String generateOcd() {
		String ocd = "[OCA]";
		ocd += "\n";
		ocd += "[State:";
		for(int i = 0; i <  this.getTempOca().getStates().size(); i ++) {
			ocd += this.getTempOca().getStates().get(i).getIndex();
			if(i  != this.getTempOca().getStates().size() - 1) {
				ocd += ",";
			}
		}
		ocd += "]";
		ocd += "\n";
		ocd += "[Init:";
		ocd += this.getTempOca().getInitIndex();
		ocd += "]\n";
		ocd +=  "[Delta:";
		for(int i = 0; i <  this.getTempOca().getStates().size(); i ++) {
			for(int j = 0; j < this.getTempOca().getState(i).getTransitions().size(); j++) {
				ocd += "(";
				ocd += this.getTempOca().getState(i).getTransitions().get(j).getFrom().getIndex();
				ocd += ",";
				ocd += this.getTempOca().getState(i).getTransitions().get(j).getLabel();
				ocd += ",";
				ocd += this.getTempOca().getState(i).getTransitions().get(j).getTo().getIndex();
				ocd += ")";
				if(!(i == this.getTempOca().getStates().size() -1 && j == this.getTempOca().getState(i).getTransitions().size() - 1)) {
					ocd += ";";
				}
			}
		}
		return ocd;
	}
	
	private void generateRandomOca(int stateNum) {
		this.tempOca = new OCA();
		Random r = new Random();
		for(int i = 0; i < stateNum; i ++) {
			this.getTempOca().addState(i);
		}
		for(int i = 0; i < stateNum; i ++) {
			for(int j = 0; j < stateNum; j ++) {
				if(r.nextInt() % 3 == 0) {
					int op = r.nextInt(3);
					if(op == 0) {
						this.getTempOca().addTransition(i, OCAOp.Add, j);
					} else if(op == 1) {
						this.getTempOca().addTransition(i, OCAOp.Sub, j);
					} else {
						this.getTempOca().addTransition(i, OCAOp.Zero, j);
					}
				}
			}
		} 
	}
	
	public OCA genRandomOca(int stateNum) {
		this.generateRandomOca(stateNum);
		return this.getTempOca();
	}
	//getters and setters

	public OCA getTempOca() {
		return tempOca;
	}

	public void setTempOca(OCA tempOca) {
		this.tempOca = tempOca;
	}
}
