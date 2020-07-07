package ocareach;

import automata.counter.gen.OCAGen;
import graph.directed.DGEdge;
import graph.directed.DGVertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.microsoft.z3.*;

public class ConverterGenTest  {
	public static void main(String[] args) { 
			OCAGen oca = new OCAGen(1);
			for(int i = 0; i < 2; i++) {
				oca.addState(i);
			}
			oca.addTransition(0, 1, 1);
			oca.addTransition(1, -1, 0);
			oca.setInitIndex(0);
			oca.setTargetIndex(1);
			ConverterGen con = new ConverterGen(oca); 
			ConverterGen converter = new ConverterGen(oca);
			String appendStr = "_first";
			System.out.println("-------------------------Bellman Ford Test--------------------------");
			HashMap<String, IntExpr> edgeFlowVarsFirst = new HashMap<String, IntExpr>();

			HashMap<String, IntExpr> maxWeightVarsFirst = new HashMap<String, IntExpr>();

			HashMap<String, IntExpr> indexVars = new HashMap<String, IntExpr>();
			
			for(DGVertex v : oca.toDGraph().getVertices()) {
				for(DGEdge e : v.getEdges()) {
					edgeFlowVarsFirst.put("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first", 
								converter.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first"));
					indexVars.put("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first", converter.getQfpaGen().mkVariableInt("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first"));
				}
			}
			//String bfStr = converter.genBellmanFord(edgeFlowVarsFirst, maxWeightVarsFirst, false).toString();
			//System.out.println(bfStr + edgeFlowVarsFirst.size());

			System.out.println("-------------------------APC Form Test--------------------------");
			//String APCStr = converter.genAPCForm(edgeFlowVarsFirst, false).toString();
			//System.out.println(APCStr);
			System.out.println("-------------------------IDX Form Test--------------------------");
			//String IDXStr = converter.genIDXForm(oca.getInitIndex(), oca.getTargetIndex(), edgeFlowVarsFirst, indexVars, false).toString();
			//System.out.println(IDXStr);
			
			HashMap<String, IntExpr> edgeDecomVars = new HashMap<String, IntExpr>();

			for(DGEdge e : oca.toDGraph().getEdges()) {
				for(DGEdge ep : oca.toDGraph().getEdges()) {
					edgeDecomVars.put("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_edge("+ep.getFrom().getIndex()+","+ep.getTo().getIndex()+")" + appendStr,
									  converter.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_edge("+ep.getFrom().getIndex()+","+ep.getTo().getIndex()+")" + appendStr
							));
				}
			}
			System.out.println("-------------------------PF Form Test--------------------------");
			String PFStr = converter.genPathFlowFormEdgeDecom(oca.getInitIndex(), oca.getTargetIndex(), edgeDecomVars, converter.getDgraph(false).getEdges().get(1), false).toString();
			//System.out.println(PFStr);
			
			System.out.println("-------------------------EDC Form Test--------------------------");
			String EDCStr = converter.genEDCForm(oca.getInitIndex(), oca.getTargetIndex(), edgeFlowVarsFirst, indexVars, edgeDecomVars, false).toString();
			//System.out.println(EDCStr);
			System.out.println("-------------------------NZE Form Test--------------------------");
			IntExpr sVar = converter.getQfpaGen().mkVariableInt("sVar");
			IntExpr tVar = converter.getQfpaGen().mkVariableInt("tVar");
			HashMap<String, IntExpr> sumVars = new HashMap<String, IntExpr>();
			for(DGEdge e : converter.getDgraph(false).getEdges()) {
				sumVars.put("sum_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first", converter.getQfpaGen().mkVariableInt("sum_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first"));
			}
			String NZEStr = converter.genNZEForm(oca.getInitIndex(), oca.getTargetIndex(), edgeFlowVarsFirst, indexVars, edgeDecomVars, sumVars, sVar, tVar, false).toString();
			//System.out.println(NZEStr);
			System.out.println("-------------------------WGT Form Test--------------------------");
			String WGTStr = converter.genWGTForm(oca.getInitIndex(), oca.getTargetIndex(), edgeFlowVarsFirst, indexVars, sumVars, sVar, tVar, false).toString();
			//System.out.println(WGTStr);
			oca = new OCAGen(1);
			oca.addState(0);oca.addState(1);
			oca.addTransition(0, 1, 0);
			oca.addTransition(0, -1, 1);
			oca.addTransition(1, -1, 1);
			oca.setInitIndex(0);
			oca.setTargetIndex(1);
			converter = new ConverterGen(oca);
			List<HashMap<String, IntExpr>> indexList = new ArrayList<HashMap<String, IntExpr>>();//6
			for(int i = 0; i < 3; i ++) {
				indexList.add(new HashMap<String, IntExpr>());
				for(DGEdge e : converter.getDgraph(false).getEdges()) {
					indexList.get(i).put(
						"idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + (i+1) + ")",
						converter.getQfpaGen().mkVariableInt("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + (i+1) + ")")
					);
				}
			}
			for(int i = 3; i < 6; i ++) { 
				indexList.add(new HashMap<String, IntExpr>());
				for(DGEdge e : converter.getDgraph(true).getEdges()) {
					indexList.get(i).put(
						"idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + (i+1) + ")",
						converter.getQfpaGen().mkVariableInt("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + (i+1) + ")")
					);
				}
			}
			
			List<HashMap<String, IntExpr>> sumVarList = new ArrayList<HashMap<String, IntExpr>>();//4
			List<HashMap<String, IntExpr>> dropVarList = new ArrayList<HashMap<String, IntExpr>>();//4
			for(int i = 0; i < 4; i ++) {
				sumVarList.add(new HashMap<String, IntExpr>());
				dropVarList.add(new HashMap<String, IntExpr>());
				for(DGVertex v : converter.getDgraph((i < 2) ? false : true).getVertices()) {
					sumVarList.get(i).put(
						"sum_" + v.getIndex() + "_(" + (i+1) + ")",
						converter.getQfpaGen().mkVariableInt("sum_" + v.getIndex() + "_(" + (i+1) + ")")
					);
					dropVarList.get(i).put(
						"drop_" + v.getIndex() + "_(" + (i+1) + ")",
						converter.getQfpaGen().mkVariableInt("drop_" + v.getIndex() + "_(" + (i+1) + ")")
					);
				}
			}
			HashMap<String, IntExpr> edgeFlowVarsThird = new HashMap<String, IntExpr>();
			for(DGEdge e : converter.getDgraph(false).getEdges()) {
				edgeFlowVarsThird.put("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third", converter.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third"));
			}
			System.out.println("-------------------------PCT Form Test--------------------------");
			System.out.println("------------------SPA SubTest----------------");
			String SPANotBoundStr = converter.genSPAForm(oca.getInitIndex(), oca.getTargetIndex(), sumVarList.get(0), dropVarList.get(0), indexList.get(0), edgeFlowVarsThird, false).toString();
			String SPCStr = converter.genSPCForm(0, sumVarList.get(1), dropVarList.get(1), indexList.get(1), edgeFlowVarsThird, false).toString();
			
			System.out.println("------------------SPC SubTest----------------");
			System.out.println();
			//System.out.println(SPCStr);
			oca = new OCAGen(1);
			oca.addState(0);
			oca.addTransition(0, -1, 0);
			oca.setInitIndex(0);oca.setTargetIndex(0);
			con = new ConverterGen(oca);
			IntExpr[] mids = new IntExpr[2];
			mids[0] = con.getQfpaGen().mkVariableInt("mid_1");
			mids[1] = con.getQfpaGen().mkVariableInt("mid_2");

			sVar = con.getQfpaGen().mkVariableInt("sVar");
			tVar = con.getQfpaGen().mkVariableInt("tVar");
			edgeFlowVarsFirst.clear();
			indexVars.clear();
			for(DGVertex v : oca.toDGraph().getVertices()) {
				for(DGEdge e : v.getEdges()) {
					edgeFlowVarsFirst.put("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first", 
								con.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first"));
					indexVars.put("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first", con.getQfpaGen().mkVariableInt("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first"));
				}
			}
			System.out.println("------------------------T1RC Test------------------------");
			String t1rcStr = con.genType1RCForm(0, 0, sVar, tVar, edgeFlowVarsFirst, false).toString();
			System.out.println(t1rcStr);
	}
}
