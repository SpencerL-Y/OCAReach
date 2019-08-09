package other;

import automata.counter.OCA;
import automata.counter.OCAOp;
import ocareach.ConverterOpt;

public class ConverterOptTest {
	public static void main(String[] args) {
		OCA oca = new OCA();
		int stateNum = 2;
		for(int i = 0; i < stateNum; i++) {
			oca.addState(i);
		}
		oca.addTransition(0, OCAOp.Sub, 1);
		oca.setInitIndex(0);
		oca.setTargetIndex(1);
		
		ConverterOpt converter = new ConverterOpt(oca);
		String result = converter.convert();
		System.out.println("--------------------FORMULA OUTPUT--------------------");
		System.out.println(result);
		System.out.println("------------------------------------------------------");
	}
}
