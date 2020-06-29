package other;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import automata.counter.OCA;
import automata.counter.gen.OCAGen;
import ocareach.Converter;
import ocareach.ConverterGen;
import parser.OCDGenerator;

public class ConverterGenCrossTest {
	public static void main(String[] args) {
		int i = 0;
		OCDGenerator ocdg = new OCDGenerator();
		int stateNum = 1;
		int maxStateNum = 5;
		for(stateNum = 1; stateNum < maxStateNum; stateNum++) {
			i ++;
			OCA oca = ocdg.genRandomOca(stateNum);
			OCAGen ocag = oca.toOCAGen();
			Converter converter = new Converter(oca);
			ConverterGen convGen = new ConverterGen(ocag);
			convGen.setQfpaGen(converter.getQfpaGen());
			IntExpr sVar = converter.getQfpaGen().mkVariableInt("sVar");
			IntExpr tVar = converter.getQfpaGen().mkVariableInt("tVar");
			BoolExpr converterForm = converter.convertExpr(sVar, tVar);
			BoolExpr convGenForm = convGen.convertExpr(sVar, tVar);
			BoolExpr equivDebugForm = converter.getQfpaGen().mkAndBool(
				converter.getQfpaGen().mkImplies(converterForm, convGenForm),
				converter.getQfpaGen().mkImplies(convGenForm, converterForm)
			);
			
			equivDebugForm = converter.getQfpaGen().mkNotBool(equivDebugForm);
			equivDebugForm = converter.getQfpaGen().mkAndBool(
				equivDebugForm,
				converter.getQfpaGen().mkRequireNonNeg(sVar),
				converter.getQfpaGen().mkRequireNonNeg(tVar)
			);
			Solver solver = converter.getQfpaGen().getCtx().mkSolver();
			solver.add((BoolExpr)equivDebugForm.simplify());
			if(solver.check() == Status.UNSATISFIABLE) {
				System.out.println("UNSAT");
			} else {
				System.out.println(i);
				System.out.println("SAT! BUG INSTANCE: ");
				System.out.println("------------------OCA-----------------");
				oca.print();
				System.out.println("------------------SAT Instance-----------------");
				System.out.println(solver.getModel().toString());
				break;
			}
		}
	}
}
