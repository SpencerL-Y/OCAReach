package other;

import automata.counter.OCA;
import automata.counter.OCAOp;
import ocareach.ConverterOpt;

public class ConverterOptTest {
	public static void main(String[] args) {
		OCA oca = new OCA();
		int stateNum = 1;
		for(int i = 0; i < stateNum; i++) {
			oca.addState(i);
		}
		//Example 1
		
		oca.addTransition(0, OCAOp.Add, 0);
		/*oca.addTransition(1, OCAOp.Sub, 0);
		oca.addTransition(0, OCAOp.Add, 2);
		oca.addTransition(2, OCAOp.Sub, 3);
		oca.addTransition(3, OCAOp.Sub, 2);
		oca.setInitIndex(0);
		oca.setTargetIndex(2);
		*/
		
		//oca.addTransition(0, OCAOp.Add, 1);
		//oca.addTransition(1, OCAOp.Add, 0);
		oca.setInitIndex(0);
		oca.setTargetIndex(0);
		ConverterOpt converter = new ConverterOpt(oca);
		String result = converter.convert();
		System.out.println("--------------------FORMULA OUTPUT--------------------");
		System.out.println(result);
		System.out.println("------------------------------------------------------");
	}
}
