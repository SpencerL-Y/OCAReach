package other;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import automata.counter.OCA;
import automata.counter.OCAOp;
import ocareach.ConverterZero;

public class ConverterZeroTest {
	public static void main(String[] args) throws IOException {
		OCA oca = new OCA();
		for(int i = 0; i < 7; i++) {
			oca.addState(i);
		}
		/*
		oca.addTransition(0, OCAOp.Add, 1);
		oca.addTransition(0, OCAOp.Sub, 2);
		oca.addTransition(0, OCAOp.Zero, 3);
		oca.addTransition(3, OCAOp.Zero, 4);
		oca.addTransition(4, OCAOp.Add, 5);
		oca.addTransition(1, OCAOp.Add, 3);
		oca.addTransition(2, OCAOp.Add, 3);
		//oca.addTransition(5, OCAOp.Add, 0);
		oca.addTransition(3, OCAOp.Sub, 5);
		oca.setInitIndex(0);
		oca.setTargetIndex(5);
		*/
		// example 1 
		/*
		oca.addTransition(0, OCAOp.Sub, 1);
		oca.addTransition(1, OCAOp.Zero, 2);
		oca.addTransition(2, OCAOp.Add, 3);
		oca.setInitIndex(0);
		oca.setTargetIndex(3);
		*/
		//example 2
		/*
		oca.addTransition(0, OCAOp.Add, 1);
		oca.addTransition(0, OCAOp.Sub, 4);
		oca.addTransition(1, OCAOp.Zero, 2);
		oca.addTransition(4, OCAOp.Zero, 5);
		oca.addTransition(2, OCAOp.Sub, 3);
		oca.addTransition(5, OCAOp.Add, 3);
		oca.setInitIndex(0);
		oca.setTargetIndex(3);
		*/
		/*
		oca.addTransition(0, OCAOp.Sub, 1);
		oca.addTransition(1, OCAOp.Zero, 2);
		oca.addTransition(2, OCAOp.Add, 3);
		oca.addTransition(3, OCAOp.Zero, 4);
		oca.addTransition(3, OCAOp.Zero, 5);
		oca.addTransition(4, OCAOp.Add, 6);
		oca.addTransition(5, OCAOp.Sub, 6);
		oca.setInitIndex(0);
		oca.setTargetIndex(6);
		*/
		/*
		oca.addTransition(0, OCAOp.Sub, 1);
		oca.addTransition(1, OCAOp.Zero, 2);
		oca.addTransition(2, OCAOp.Add, 3);
		oca.addTransition(3, OCAOp.Zero, 4);
		oca.addTransition(4, OCAOp.Sub, 1);
		oca.addTransition(4, OCAOp.Add, 5);
		oca.setInitIndex(0);
		oca.setTargetIndex(5);
		*/
		
		/*oca.addTransition(0, OCAOp.Sub, 1);
		oca.addTransition(1, OCAOp.Sub, 0);
		oca.addTransition(0, OCAOp.Add, 2);
		oca.addTransition(2, OCAOp.Sub, 3);
		oca.addTransition(3, OCAOp.Sub, 2);
		oca.addTransition(2, OCAOp.Zero, 4);
		oca.addTransition(1, OCAOp.Zero, 4);
		oca.setInitIndex(0);
		oca.setTargetIndex(4);*/
		//Exmaple 1
		/*oca.addTransition(0, OCAOp.Zero, 1);
		oca.setInitIndex(0);
		oca.setTargetIndex(1);*/
		//Example 2
		
		/*
		oca.addTransition(0, OCAOp.Sub, 1);
		oca.addTransition(1, OCAOp.Zero, 2);
		oca.addTransition(2, OCAOp.Sub, 3);
		oca.setInitIndex(0);
		oca.setTargetIndex(3);*/
		
		//Example 3
		/*oca.addTransition(0, OCAOp.Zero, 1);
		oca.addTransition(0, OCAOp.Sub, 1);
		oca.setInitIndex(0);
		oca.setTargetIndex(1);*/
		
		//Example 4
		/*
		oca.addTransition(0, OCAOp.Sub, 0);
		oca.addTransition(0, OCAOp.Zero, 1);
		oca.setInitIndex(0);
		oca.setTargetIndex(1);*/
		
		//Example 5
		/*
		oca.addTransition(0, OCAOp.Sub, 1);
		oca.addTransition(1, OCAOp.Sub, 0);
		oca.addTransition(1, OCAOp.Zero, 2);
		oca.setInitIndex(0);
		oca.setTargetIndex(2);*/
		
		//Example 6
		/*
		oca.addTransition(0, OCAOp.Add, 1);
		oca.addTransition(0, OCAOp.Sub, 4);
		oca.addTransition(1, OCAOp.Sub, 2);
		oca.addTransition(2, OCAOp.Sub, 3);
		oca.addTransition(3, OCAOp.Sub, 1);
		oca.addTransition(4, OCAOp.Sub, 5);
		oca.addTransition(5, OCAOp.Sub, 4);
		oca.addTransition(1, OCAOp.Zero, 6);
		oca.addTransition(4, OCAOp.Zero, 6);
		oca.setInitIndex(0);
		oca.setTargetIndex(6);
		*/
		/*
		oca.addTransition(0, OCAOp.Zero, 1);
		oca.addTransition(1, OCAOp.Add, 2);
		oca.addTransition(2, OCAOp.Sub, 3);
		oca.addTransition(3, OCAOp.Add, 3);
		oca.addTransition(3, OCAOp.Sub, 0);
		oca.setInitIndex(0);
		oca.setTargetIndex(1);*/
		oca.addTransition(0, OCAOp.Sub, 1);
		oca.addTransition(1, OCAOp.Zero, 2);
		oca.addTransition(2, OCAOp.Zero, 1);
		oca.addTransition(2, OCAOp.Add, 3);
		oca.setInitIndex(0);
		oca.setTargetIndex(3);
		
		ConverterZero cz = new ConverterZero(oca);
		String resultStr = cz.convert();
		DataOutputStream out = new DataOutputStream(new FileOutputStream("/home/clexma/Desktop/test.smt"));
		out.writeChars(resultStr);
		System.out.println();
		System.out.println("--------------------FORMULA OUTPUT--------------------");
		System.out.println(resultStr);
		System.out.println("------------------------------------------------------");
	}
}
