package parser;

import java.util.Random;

import automata.counter.OCA;
import automata.counter.OCAOp;

public class OCDGenerator {
	private OCA tempOca;
	
	public OCDGenerator() {
		
	}
	
	public String generateRandomOcd(int stateNum) {
		assert(stateNum > 0);
		this.generateRandomOca(stateNum);
		return this.generateOcd();
	}
	
	public String generateRandomOcdEta(int stateNum, double eta) {
		this.generateRandomOcaEta(stateNum, eta);
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
		ocd += "[Target:";
		ocd += this.getTempOca().getTargetIndex(); 
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
		ocd += "]\n";
		return ocd;
	}
	
	private void generateRandomOcaEta(int stateNum, double eta) {
		if(!(stateNum > 0) || !(eta >= 0 && eta <= 1)) {
			System.out.println("argument error");
			return;
		}
		Random r = new Random();
		this.tempOca = new OCA();
		for(int i = 0; i < stateNum; i ++) {
			this.getTempOca().addState(i);
		}
		for(int i = 0; i < stateNum; i ++) {
			for(int j = 0; j < stateNum; j ++) {
				//change here to control the number of edges
				int num = r.nextInt();
				if(num % 10 == 1  ) {
					int op = r.nextInt(10000);
					if(op <= 10000*eta) {
						this.getTempOca().addTransition(i, OCAOp.Zero, j);
					} else if (op < 5){
						this.getTempOca().addTransition(i, OCAOp.Sub, j);
					} else {
						this.getTempOca().addTransition(i, OCAOp.Add, j);
					}
				}
			}
		}
		tempOca.setInitIndex(0);
		tempOca.setTargetIndex(r.nextInt(stateNum));
	}
	
	private void generateRandomOca(int stateNum) {
		this.generateRandomOcaEta(stateNum, 0.3);
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
