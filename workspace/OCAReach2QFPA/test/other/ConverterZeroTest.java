package other;

import automata.counter.OCA;
import automata.counter.OCAOp;
import ocareach.ConverterZero;

public class ConverterZeroTest {
	public static void main(String[] args) {
		OCA oca = new OCA();
		for(int i = 0; i < 6; i++) {
			oca.addState(i);
		}
		oca.addTransition(0, OCAOp.Add, 1);
		oca.addTransition(0, OCAOp.Sub, 2);
		oca.addTransition(0, OCAOp.Zero, 3);
		//oca.addTransition(3, OCAOp.Zero, 4);
		oca.addTransition(4, OCAOp.Add, 5);
		oca.addTransition(1, OCAOp.Add, 3);
		oca.addTransition(2, OCAOp.Add, 3);
		//oca.addTransition(5, OCAOp.Add, 0);
		oca.addTransition(3, OCAOp.Sub, 5);
		oca.setInitIndex(0);
		oca.setTargetIndex(5);
		
		ConverterZero cz = new ConverterZero(oca);
		System.out.println(cz.convert());
	}
}
