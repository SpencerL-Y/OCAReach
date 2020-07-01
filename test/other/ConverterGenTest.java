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
		for(int i = 0; i < 3; i++) {
			oca.addState(i);
		}
		oca.addTransition(0, -1, 1);
		oca.addTransition(1, -1, 2);
		oca.setInitIndex(0);
		oca.setTargetIndex(2);
		ConverterGen con = new ConverterGen(oca); 

		String resultStr = con.convert();
		DataOutputStream out = new DataOutputStream(new FileOutputStream("/home/clexma/Desktop/test.smt"));
		out.writeChars(resultStr);
		IntExpr xs = con.getQfpaGen().mkVariableInt("xs");
		IntExpr xt = con.getQfpaGen().mkVariableInt("xt");
		HashMap<String, IntExpr> edgeFlowVarFirst = new HashMap<String, IntExpr>();
		HashMap<String, IntExpr> indexVars = new HashMap<String, IntExpr>();
		for(DGEdge e : con.getDgraph(false).getEdges()) {
			edgeFlowVarFirst.put("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first" , con.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first" ));
			indexVars.put("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first" , con.getQfpaGen().mkVariableInt("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first" ));
			
		}
		
		BoolExpr resultExpr = con.genType1RCForm(0, 2, xs, xt, edgeFlowVarFirst, false);
		System.out.println("----------------------------OCA  INPUT----------------------------");
		oca.print();
		System.out.println("------------------------------------------------------------------");
		System.out.println("--------------------------FORMULA OUTPUT--------------------------");
		System.out.println(resultStr);
		System.out.println("------------------------------------------------------------------");
		//System.out.println(result.toString());
		//System.out.println(resultExpr.toString());
		//}
		System.out.println(resultExpr.toString());
	}
	

	
	private static ApplyResult applyTactic(Context ctx, Tactic qeTac, Goal goal) {
		// TODO Auto-generated method stub
		 System.out.println("\nGoal: " + goal);

	        ApplyResult res = qeTac.apply(goal);
	        System.out.println("Application result: " + res);
	        return res;
	}
}
