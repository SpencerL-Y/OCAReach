package other;

import automata.counter.OCA;
import automata.counter.OCAOp;
import ocareach.ConverterOpt;

public class ConverterOptTest {
	public static void main(String[] args) {
		OCA oca = new OCA();
		int stateNum = 5;
		for(int i = 0; i < stateNum; i++) {
			oca.addState(i);
		}
		oca.addTransition(0, OCAOp.Sub, 1);
		oca.addTransition(1, OCAOp.Sub, 4);
		oca.addTransition(4, OCAOp.Sub, 2);
		oca.addTransition(2, OCAOp.Add, 1);
		oca.addTransition(2, OCAOp.Add, 3);
		oca.setInitIndex(0);
		oca.setTargetIndex(3);
		
		ConverterOpt converter = new ConverterOpt(oca);
		String result = converter.convert();
		System.out.println("--------------------FORMULA OUTPUT--------------------");
		System.out.println(result);
		System.out.println("------------------------------------------------------");
	}
}
