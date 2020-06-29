package automata.counter.gen;

import java.util.HashMap;

public class OCAOpGen {
	private int maxAbsValue;
	private HashMap<Integer, OCAOpSymb> symbolMap;
	
	public OCAOpGen(int maxAbsVal) {
		this.setSymbolMap(new HashMap<Integer, OCAOpSymb>());
		this.setMaxAbsValue(maxAbsVal);
	}
	
	public OCAOpSymb getSymbol(int counterChange) {
		if(this.getSymbolMap().containsKey(counterChange)){
			return this.getSymbolMap().get(counterChange);
		} else {
			OCAOpSymb newS = new OCAOpSymb(counterChange);
			this.getSymbolMap().put(counterChange, newS);
			return newS;
		}
	}

	public HashMap<Integer, OCAOpSymb> getSymbolMap() {
		return symbolMap;
	}

	public void setSymbolMap(HashMap<Integer, OCAOpSymb> symbolMap) {
		this.symbolMap = symbolMap;
	}

	public int getMaxAbsValue() {
		return maxAbsValue;
	}

	public void setMaxAbsValue(int maxAbsValue) {
		this.maxAbsValue = maxAbsValue;
	}

}
