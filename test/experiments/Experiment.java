package experiments;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import automata.counter.OCA;
import automata.counter.OCAOp;
import ocareach.ConverterZero;
import parser.OCDGenerator;

public class Experiment {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		boolean handcraft = false;
		if(handcraft) {

			/*-------------------HANDCRAFT-----------------------*/
			int index;
			OCA oca = new OCA();
			for(int i = 0; i < 15; i++) {
				oca.addState(i);
			}
			index = 0;
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
			// example 3
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
			//example 4
			/*index = 4;
			oca.addTransition(0, OCAOp.Sub, 1);
			oca.addTransition(1, OCAOp.Zero, 2);
			oca.addTransition(2, OCAOp.Add, 3);
			oca.addTransition(3, OCAOp.Zero, 4);
			oca.addTransition(4, OCAOp.Sub, 1);
			oca.addTransition(4, OCAOp.Add, 5);
			oca.setInitIndex(0);
			oca.setTargetIndex(5);
			*/
			// example 5
			/*
			index = 5;
			oca.addTransition(0, OCAOp.Sub, 1);
			oca.addTransition(1, OCAOp.Sub, 0);
			oca.addTransition(0, OCAOp.Add, 2);
			oca.addTransition(2, OCAOp.Sub, 3);
			oca.addTransition(3, OCAOp.Sub, 2);
			oca.addTransition(2, OCAOp.Zero, 4);
			oca.addTransition(1, OCAOp.Zero, 4);
			oca.setInitIndex(0);
			oca.setTargetIndex(4);
			*/
			//index = 6;
			/*
			//Exmaple 6
			oca.addTransition(0, OCAOp.Zero, 1);
			oca.setInitIndex(0);
			oca.setTargetIndex(1);*/
			//Example 7
			//index = 7;
			/*
			oca.addTransition(0, OCAOp.Sub, 1);
			oca.addTransition(1, OCAOp.Zero, 2);
			oca.addTransition(2, OCAOp.Sub, 3);
			oca.setInitIndex(0);
			oca.setTargetIndex(3);*/
			//index = 8;
			//Example 8
			/*oca.addTransition(0, OCAOp.Zero, 1);
			oca.addTransition(0, OCAOp.Sub, 1);
			oca.setInitIndex(0);
			oca.setTargetIndex(1);*/
			//index = 9;
			//Example 9
			/*
			oca.addTransition(0, OCAOp.Sub, 0);
			oca.addTransition(0, OCAOp.Zero, 1);
			oca.setInitIndex(0);
			oca.setTargetIndex(1);*/
			//index = 10;
			//Example 10
			/*
			oca.addTransition(0, OCAOp.Sub, 1);
			oca.addTransition(1, OCAOp.Sub, 0);
			oca.addTransition(1, OCAOp.Zero, 2);
			oca.setInitIndex(0);
			oca.setTargetIndex(2);*/
			/*index = 11;
			//Example 11
			
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
			
			//Example 12
			/*index = 12;
			
			oca.addTransition(0, OCAOp.Zero, 1);
			oca.addTransition(1, OCAOp.Add, 2);
			oca.addTransition(2, OCAOp.Sub, 3);
			oca.addTransition(3, OCAOp.Add, 3);
			oca.addTransition(3, OCAOp.Sub, 0);
			oca.setInitIndex(0);
			oca.setTargetIndex(1);*/
			/*index = 13;
			//Example 13
			
			oca.addTransition(0, OCAOp.Sub, 1);
			//oca.addTransition(1, OCAOp.Zero, 1);
			oca.addTransition(1, OCAOp.Add, 2);
			oca.setInitIndex(0);
			oca.setTargetIndex(2);
			*/
			// Example 14
			/*
			index = 14;
			
			oca.addTransition(0, OCAOp.Sub, 1);
			oca.addTransition(1, OCAOp.Zero, 2);
			oca.addTransition(2, OCAOp.Add, 3);
			oca.addTransition(3, OCAOp.Add, 2);
			oca.addTransition(2, OCAOp.Add, 4);
			oca.addTransition(4, OCAOp.Sub, 4);
			oca.addTransition(4, OCAOp.Zero, 6);
			oca.addTransition(6, OCAOp.Add, 7);
			oca.addTransition(7, OCAOp.Sub, 7);
			oca.addTransition(7, OCAOp.Zero, 8);
			oca.addTransition(8, OCAOp.Add, 9);
			oca.setInitIndex(0);
			oca.setTargetIndex(9);

			*/
			
			//Example 15
			/*index = 15;
			
			oca.addTransition(0, OCAOp.Sub, 1);
			oca.addTransition(1, OCAOp.Sub, 2);
			oca.addTransition(2, OCAOp.Sub, 3);
			oca.addTransition(2, OCAOp.Add, 1);
			oca.addTransition(3, OCAOp.Zero, 4);
			oca.addTransition(4, OCAOp.Add, 5);
			oca.addTransition(5, OCAOp.Sub, 6);
			oca.addTransition(6, OCAOp.Zero, 7);
			oca.addTransition(7, OCAOp.Add, 8);
			oca.addTransition(8, OCAOp.Add, 7);
			oca.addTransition(7, OCAOp.Sub, 9);
			oca.setInitIndex(0);
			oca.setTargetIndex(9);
			*/
			
			//Example 16
			/*
			index = 16;
			oca.addTransition(0, OCAOp.Sub, 1);
			oca.addTransition(1, OCAOp.Sub, 2);
			oca.addTransition(1, OCAOp.Add, 1);
			oca.addTransition(2, OCAOp.Zero, 3);
			oca.addTransition(3, OCAOp.Add, 4);
			oca.addTransition(4, OCAOp.Add, 3);
			oca.setInitIndex(0);
			oca.setTargetIndex(4);*/
			long startTime = System.currentTimeMillis();
			ConverterZero cz = new ConverterZero(oca);
			String resultStr = cz.convert();
			long endTime = System.currentTimeMillis();
			DataOutputStream out = new DataOutputStream(new FileOutputStream("/home/clexma/Desktop/handcraft.csv", true));
			out.writeChars(index + "," + (endTime - startTime) + "," + resultStr.trim().length()+ "\n");
			System.out.println();
			System.out.println("--------------------FORMULA OUTPUT--------------------");
			System.out.println(resultStr);
			System.out.println("------------------------------------------------------");

		} else {
			for(int i = 0; i < 550; i ++) {
					int stateNum = 4;
					OCDGenerator ocdg = new OCDGenerator();
					OCA oca = ocdg.genRandomOca(stateNum);
					long startTime = System.currentTimeMillis();
					ConverterZero cz = new ConverterZero(oca);
					String resultStr = cz.convert();
					long endTime = System.currentTimeMillis();
					DataOutputStream out = new DataOutputStream(new FileOutputStream("/home/clexma/Desktop/stateResult4.csv", true));
					out.writeChars(i + "," + stateNum + "," + (oca.toDGraph().getEdges().size()) +"," +(endTime - startTime) + "," + resultStr.length() + "\n");
					
			}
		}
	}
}
