package parser;

import java.io.InputStream;
import java.io.InputStreamReader;

import automata.counter.OCA;
import automata.counter.OCAOp;

public class OCAParser {
	
	private InputStreamReader i;
	
	public OCAParser(InputStream is) {
		this.setI(new InputStreamReader(is));
	}
	
	public OCA parse(String ocd) {
		OCA oca = new OCA();
		String[] text = this.parseBlock(ocd);
		boolean result = true;
		result &= this.parseTag(text, oca);
		result &= this.parseStates(text, oca);
		result &= this.parseTransitions(text, oca);
		if(!result) {
			System.out.println("ERROR: OCD format error, please check");
			System.out.checkError();
		}
		return oca; 
	}
	private String[] parseBlock(String origin) {
		System.out.println("parseBlock");
		String[] first = origin.split("]");
		for(int i = 0; i < 4; i ++) {
			String s = first[i];
			if(s == null) {
				System.out.println("ERROR: format incorrect, please check");
				assert(false);
			}
			s = s.trim();
			if(s.toCharArray()[0] != '[') {
				System.out.println("ERROR: format incorrect, please check");
				assert(false);
			}
			s = s.substring(1).trim();
			System.out.println(s);
		}
		
		return first;
	}
	
	private boolean parseTag(String[] text, OCA oca) {
		System.out.println("parseTag");
		String ocaStr = text[0];
		ocaStr = ocaStr.trim().substring(1);
		if(ocaStr.equals("OCA")) {
			System.out.println(true);
			return true;
  		} else {
  			System.out.println(false);
			return false;
		}
	}
	
	private boolean parseStates(String[] text, OCA oca) {
		System.out.println("parseStates");
		String states = text[1].trim();
		String[] statesFmt = states.split(":");
		String stateStr = statesFmt[0].trim().substring(1);

		System.out.println(stateStr);
		if(!stateStr.equals("State")) {
			System.out.println(false);
			return false;
		}
		String[] statesEnum = statesFmt[1].trim().split(",");
		for(String state : statesEnum) {
			state = state.trim();

			System.out.println(state);
			int id = Integer.parseInt(state);
			oca.addState(id);
		}
		
		String init = text[2].trim();
		String[] initFmt = init.split(":");
		String initStr = initFmt[0].trim().substring(1);
		if(!initStr.equals("Init")) {

			System.out.println(initStr);
			System.out.println(false);
			return false;
		}
		String initNum = initFmt[1].trim();
		int initId = Integer.parseInt(initNum);
		oca.setInitIndex(initId);
		System.out.println(true);
		return true;
	}
	
	private boolean parseTransitions(String[] text, OCA oca) {
		System.out.println("parseTransitions");
		String trans = text[3].trim();
		String[] transFmt = trans.split(":");
		String transStr = transFmt[0].trim().substring(1);
		if(!transStr.equals("Delta")) {
			System.out.println(false + "Delta" + transStr);
			return false;
		}

		System.out.println(transFmt[1]);	
		String[] tuples =  transFmt[1].trim().split(";");

		System.out.println(tuples[0]);
		for(String t : tuples) {
			t = t.trim();

			System.out.println(t);
			String[] tMem = t.split(",");
			String tFrom = tMem[0].trim();
			String tLabel = tMem[1].trim();
			String tTo = tMem[2].trim();

			System.out.println(tFrom + " "+tTo);
			if(!(tFrom.toCharArray()[0] =='(' && 
				(tLabel.equals("add") || tLabel.equals("sub") || tLabel.equals("zero")) && 
				 tTo.toCharArray()[tTo.length() - 1] == ')' )) {
				System.out.println(false);
				return false;
			}
			int tFromNum = Integer.parseInt(tFrom.substring(1));
			int tToNum = Integer.parseInt(tTo.substring(0, tTo.length() - 1));
			OCAOp op = OCAOp.parseOp(tLabel);
			oca.addTransition(tFromNum, op, tToNum);

			System.out.println("add");
		}
		System.out.println(true);
		return true;
	}
	
	public InputStreamReader getI() {
		return i;
	}

	public void setI(InputStreamReader i) {
		this.i = i;
	}
}
