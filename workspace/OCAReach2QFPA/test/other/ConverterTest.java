package other;

import automata.counter.OCA;
import automata.counter.OCAOp;
import ocareach.Converter;

public class ConverterTest {
	public static void main(String[] args) {
		OCA oca = new OCA();
		for(int i = 0; i < 5; i++) {
			oca.addState(i);
		}
		oca.addTransition(0, OCAOp.Sub, 1);
		oca.addTransition(1, OCAOp.Add, 4);
		oca.addTransition(4, OCAOp.Sub, 0);
		oca.addTransition(1, OCAOp.Add, 2);
		oca.addTransition(2, OCAOp.Sub, 2);
		oca.addTransition(2, OCAOp.Add, 3);
		oca.setInitIndex(0);
		oca.setTargetIndex(3);
		Converter con = new Converter(oca);
		con.convert();
	}
}
