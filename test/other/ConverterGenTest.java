package other;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.microsoft.z3.ApplyResult;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Goal;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Tactic;

import automata.counter.gen.OCAGen;
import graph.directed.DGEdge;
import ocareach.ConverterGen;

public class ConverterGenTest {
	public static void main(String[] args) throws IOException {
		OCAGen oca = new OCAGen(1);
		for(int i = 0; i < 1; i++) {
			oca.addState(i);
		}
		oca.addTransition(0, -1, 0);
		oca.setInitIndex(0);
		oca.setTargetIndex(0);
		ConverterGen con = new ConverterGen(oca); 
		
		String result = con.convert();
		DataOutputStream out = new DataOutputStream(new FileOutputStream("/home/clexma/Desktop/test.smt"));
		out.writeChars("(declare-fun xs () Int) (declare-fun xt () Int)");
		out.writeChars("(assert ");
		out.writeChars(result);
		out.writeChars(")");
		out.writeChars("(check-sat)");
		
		
		System.out.println("----------------------------OCA  INPUT----------------------------");
		oca.print();
		System.out.println("------------------------------------------------------------------");
		System.out.println("--------------------------FORMULA OUTPUT--------------------------");
		System.out.println(result);
		System.out.println("------------------------------------------------------------------");
		HashMap<String, IntExpr> edgeFlowVarFirst = new HashMap<String, IntExpr>();
		HashMap<String, IntExpr> indexVars = new HashMap<String, IntExpr>();
		for(DGEdge e : con.getDgraph(false).getEdges()) {
			edgeFlowVarFirst.put("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first" , con.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first" ));
			indexVars.put("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first" , con.getQfpaGen().mkVariableInt("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first" ));
			
		}
		BoolExpr resultExpr = con.genIDXForm(0, 0, edgeFlowVarFirst, indexVars, false);
		//System.out.println(resultExpr.toString());
		//}
		
	}

	private static ApplyResult applyTactic(Context ctx, Tactic qeTac, Goal goal) {
		// TODO Auto-generated method stub
		 System.out.println("\nGoal: " + goal);

	        ApplyResult res = qeTac.apply(goal);
	        System.out.println("Application result: " + res);
	        return res;
	}
}
