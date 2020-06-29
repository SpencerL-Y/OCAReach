package ocareach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.microsoft.z3.ApplyResult;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Goal;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Tactic;

import automata.State;
import automata.counter.gen.OCAGen;
import formula.generator.QFPAGenerator;
import graph.directed.DGEdge;
import graph.directed.DGVertex;
import graph.directed.DGraph;

public class ConverterGen {
	private OCAGen oca;
	private DGraph dgraph;
	private QFPAGenerator qfpaGen;
	private int verticeNum;
	private int maxAbsVal;
	
	public ConverterGen(OCAGen oca) {
		this.oca = oca;
		this.dgraph = oca.toDGraph();
		this.qfpaGen = new QFPAGenerator();
		this.verticeNum = this.getDgraph().getVertices().size();
		this.setMaxAbsVal(this.getOca().getMaxAbsVal());
	}

	public String convert() {
		// TODO: implement - consider special cases 
		/*-----debug mode selection-----*/
		boolean debug = true;

		IntExpr sVar = this.getQfpaGen().mkVariableInt("xs");
		IntExpr tVar = this.getQfpaGen().mkVariableInt("xt");
		if(debug) {
			return this.convertDebug(this.getOca().getInitState(), this.getOca().getTargetState(), sVar, tVar);
		} else {
			return convertExpr(sVar, tVar).toString();			
		}
	}
	
	public BoolExpr convertExpr(IntExpr sVar, IntExpr tVar) {
		if(this.getOca().containsZeroTransition()) {
			System.out.println("ERROR: No Zero Edge allowed");
			return null;
		} 
		return this.convertExpr(this.getOca().getInitState(), this.getOca().getTargetState(), sVar, tVar);
	}
	
	public BoolExpr convertExpr(State startState, State endState, IntExpr sVar, IntExpr tVar) {
		BoolExpr result = null;
		HashMap<String, IntExpr> edgeFlowVars = new HashMap<String, IntExpr>();
		for(DGEdge e : this.getDgraph().getEdges()) {
			// TODO: special
			edgeFlowVars.put("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex(), this.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex()));
		}
		IntExpr[] edgeFlowVarsBound = new IntExpr[edgeFlowVars.size()];
		edgeFlowVars.values().toArray(edgeFlowVarsBound);
		
		BoolExpr resultExpr = this.genReachabilityCertificate(startState.getIndex(), endState.getIndex(), sVar, tVar, edgeFlowVars);
		resultExpr = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(edgeFlowVarsBound, resultExpr);
		/*System.out.println("-----------APPLY TACTIC---------");*/
		Goal goal = this.getQfpaGen().getCtx().mkGoal(true, false, false);
		goal.add(resultExpr);
		Tactic qeTac = this.getQfpaGen().getCtx().mkTactic("qe");
		ApplyResult ar = applyTactic(this.getQfpaGen().getCtx(), qeTac, goal);
		resultExpr = ar.getSubgoals()[0].AsBoolExpr();
		/*----------------------------------------------------------------*/
		result = (BoolExpr) resultExpr;
		return result;
	}
	
	public String convertDebug(State startState, State endState, IntExpr sVar, IntExpr tVar) {
		String result = null;
		HashMap<String, IntExpr> edgeFlowVars = new HashMap<String, IntExpr>();
		//explore how to deal with speacial cases:
		//1. startState is the same as endState
		//2. endState is not reachable from start state
		//3. there is no edge in the graph
		//4. check sum, index, drop variables
		for(DGEdge e : this.getDgraph().getEdges()) {
			edgeFlowVars.put("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex(), this.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex()));
		}
		IntExpr[] edgeFlowVarsBound = new IntExpr[edgeFlowVars.size()];
		edgeFlowVars.values().toArray(edgeFlowVarsBound);
		
		BoolExpr resultExpr = this.genReachabilityCertificate(startState.getIndex(), endState.getIndex(), sVar, tVar, edgeFlowVars);
		resultExpr = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(edgeFlowVarsBound, resultExpr);
		System.out.println("Origin: ");
		/*{----------------------------QUANTIFIER ELIMINATION-----------------------------------
		System.out.println("-----------APPLY TACTIC---------");
		Goal goal = this.getQfpaGen().getCtx().mkGoal(true, false, false);
		goal.add(resultExpr);
		Tactic qeTac = this.getQfpaGen().getCtx().mkTactic("qe");
		ApplyResult ar = applyTactic(this.getQfpaGen().getCtx(), qeTac, goal);
		resultExpr = ar.getSubgoals()[0].AsBoolExpr();
		//-------------------------------------------------------------------------------------}*/
		BoolExpr xsXtPosRequirements = this.getQfpaGen().mkAndBool(
			this.getQfpaGen().mkRequireNonNeg(sVar),
			this.getQfpaGen().mkRequireNonNeg(tVar)
		);
		resultExpr = this.getQfpaGen().mkAndBool(resultExpr, xsXtPosRequirements);
		result = resultExpr.toString();
		System.out.println(result);

//		result = resultExpr.simplify().toString();
		String solveResult = null;
		//{----------------------EQUIV DEBUG-----------------------
		resultExpr = this.equivDebug(sVar, tVar, resultExpr);
		
		result = resultExpr.simplify().toString();
		Solver solver = this.getQfpaGen().getCtx().mkSolver();
		solver.add((BoolExpr)resultExpr.simplify());
		if(solver.check() == Status.UNSATISFIABLE) {
			solveResult = "\n UNSAT";
		} else {
			solveResult = "\n SAT \n" + solver.getModel().toString();
		}
		/// --------------------------------------------------------}*/
		return (solveResult == null) ? result : result + solveResult;
	}
	
	public BoolExpr equivDebug(IntExpr sVar, IntExpr tVar, BoolExpr tempResult) {
		BoolExpr equiv = this.getQfpaGen().mkTrue();
		BoolExpr resultExpr = null;
		/*-------------EQUIV FORMULA-------------*/
		equiv = this.getQfpaGen().mkAndBool(
			this.getQfpaGen().mkRequireNonNeg(tVar),
			this.getQfpaGen().mkRequireNonNeg(sVar),
			this.getQfpaGen().mkLeBool(tVar, sVar)
		);

		resultExpr = this.getQfpaGen().mkAndBool(this.getQfpaGen().getCtx().mkImplies(tempResult, equiv), this.getQfpaGen().getCtx().mkImplies(equiv, tempResult));
		resultExpr = this.getQfpaGen().mkNotBool(resultExpr);
		return resultExpr;
	}
	
	public ApplyResult applyTactic(Context ctx, Tactic t, Goal g)
    {
        System.out.println("\nGoal: " + g);

        ApplyResult res = t.apply(g);
        System.out.println("Application result: " + res);
        return res;
    }
	
	public BoolExpr genBellmanFord(HashMap<String, IntExpr> edgeFlowVarsFirst, HashMap<String, IntExpr> maxWeightVarsFirst, Boolean isSkew) {
		//TODO: DEBUG whether mkTrue is correct 
		String appendStr = isSkew ? "_second_skew" : "_first";
		int minusInfinite = -this.getVerticeNum()*this.getMaxAbsVal() - 1;
		
		BoolExpr bfResult = this.getQfpaGen().mkTrue();
		
		BoolExpr zeroLengthCycleForm = this.getQfpaGen().mkTrue();
		for(DGVertex v : this.getDgraph(isSkew).getVertices()) {
			if(!maxWeightVarsFirst.containsKey(v.getIndex() + "_" + v.getIndex() + "_" + 0 + appendStr)) {
				maxWeightVarsFirst.put(v.getIndex() + "_" + v.getIndex() + "_" + 0 + appendStr, this.getQfpaGen().mkVariableInt( "z_" + v.getIndex() + "_" + v.getIndex() + "_" + 0 + appendStr));
			}
			zeroLengthCycleForm = this.getQfpaGen().mkAndBool(
				zeroLengthCycleForm, 
				this.getQfpaGen().mkEqBool(
					maxWeightVarsFirst.get(v.getIndex() + "_" + v.getIndex() + "_" + 0 + appendStr),
					this.getQfpaGen().mkConstantInt(0)
				)
			);
		}
		bfResult = this.getQfpaGen().mkAndBool(zeroLengthCycleForm);
		BoolExpr zeroLengthPathMinusInfiniteForm = this.getQfpaGen().mkTrue();
		for(DGVertex v : this.getDgraph(isSkew).getVertices()) {
			for(DGVertex w : this.getDgraph(isSkew).getVertices()) {
				if(v.getIndex() != w.getIndex()) {
					if(!maxWeightVarsFirst.containsKey(v.getIndex() + "_" + w.getIndex() + "_" + 0 + appendStr)) {
						maxWeightVarsFirst.put(v.getIndex() + "_" + w.getIndex() + "_" + 0 + appendStr, this.getQfpaGen().mkVariableInt("z_" + v.getIndex() + "_" + w.getIndex() + "_" + 0 + appendStr));
					}
					zeroLengthPathMinusInfiniteForm = this.getQfpaGen().mkAndBool(
						this.getQfpaGen().mkEqBool(
								maxWeightVarsFirst.get(v.getIndex() + "_" + w.getIndex() + "_" + 0 + appendStr),
								this.getQfpaGen().mkConstantInt(minusInfinite)
						), zeroLengthPathMinusInfiniteForm
					);
				}
			}
		}
		bfResult = this.getQfpaGen().mkAndBool(zeroLengthPathMinusInfiniteForm, bfResult);
		BoolExpr computationOfVarsForm = this.getQfpaGen().mkTrue();
		
		for(DGVertex v : this.getDgraph(isSkew).getVertices()) {
			for(DGVertex w : this.getDgraph(isSkew).getVertices()) {
				for(int k = 1; k <= this.getDgraph(isSkew).getVertices().size(); k++) {
					BoolExpr remainUnchangedForm = this.getQfpaGen().mkTrue();
					if(!maxWeightVarsFirst.containsKey(v.getIndex() + "_" + w.getIndex() + "_" + k + appendStr)) {
						maxWeightVarsFirst.put(v.getIndex() + "_" + w.getIndex() + "_" + k + appendStr, this.getQfpaGen().mkVariableInt("z_" + v.getIndex() + "_" + w.getIndex() + "_" + k + appendStr));
					}
					if(!maxWeightVarsFirst.containsKey(v.getIndex() + "_" + w.getIndex() + "_" + (k-1) + appendStr)) {
						maxWeightVarsFirst.put(v.getIndex() + "_" + w.getIndex() + "_" + (k+1) + appendStr, this.getQfpaGen().mkVariableInt("z_" + v.getIndex() + "_" + w.getIndex() + "_" + (k+1) + appendStr));
					}
					remainUnchangedForm = this.getQfpaGen().mkAndBool(
						remainUnchangedForm, 
						this.getQfpaGen().mkEqBool(
							maxWeightVarsFirst.get(v.getIndex() + "_" + w.getIndex() + "_" + k + appendStr),
							maxWeightVarsFirst.get(v.getIndex() + "_" + w.getIndex() + "_" + (k-1) + appendStr)
						)
					);
					
					for(DGVertex u : this.getDgraph(isSkew).getVertices()) {
						for(DGEdge e : u.getEdges()) {
							if(e.getTo().getIndex() == w.getIndex()) {
								if(!edgeFlowVarsFirst.containsKey("y_" + e.getFrom().getIndex() + "_" +  e.getTo().getIndex() + appendStr)) {
									edgeFlowVarsFirst.put("y_" + e.getFrom().getIndex() + "_" +  e.getTo().getIndex() + appendStr, this.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" +  e.getTo().getIndex() + appendStr));
								}
								if(!maxWeightVarsFirst.containsKey(v.getIndex() + "_" + u.getIndex() + "_" +(k-1) + appendStr)) {
									maxWeightVarsFirst.put(v.getIndex() + "_" + u.getIndex() + "_" +(k-1) + appendStr, this.getQfpaGen().mkVariableInt("z_" + v.getIndex() + "_" + u.getIndex() + "_" +(k-1) + appendStr));
								}
								if(!maxWeightVarsFirst.containsKey(v.getIndex() + "_" + u.getIndex() + "_" +(k-1) + appendStr)) {
									maxWeightVarsFirst.put(v.getIndex() + "_" + w.getIndex() + "_" + (k-1) + appendStr, this.getQfpaGen().mkVariableInt("z_" + v.getIndex() + "_" + w.getIndex() + "_" + (k-1) + appendStr));
								}
								remainUnchangedForm = this.getQfpaGen().mkAndBool(
									remainUnchangedForm,
									this.getQfpaGen().mkImplies(
										this.getQfpaGen().mkAndBool(
											this.getQfpaGen().mkGtBool(
												edgeFlowVarsFirst.get("y_" + e.getFrom().getIndex() + "_" +  e.getTo().getIndex() + appendStr),
												this.getQfpaGen().mkConstantInt(0)
											),
											this.getQfpaGen().mkGtBool(
												maxWeightVarsFirst.get(v.getIndex() + "_" + u.getIndex() + "_" +(k-1) + appendStr),
												this.getQfpaGen().mkConstantInt(minusInfinite)
											)
										),
										this.getQfpaGen().mkGeBool(
											maxWeightVarsFirst.get(v.getIndex() + "_" + w.getIndex() + "_" + (k-1) + appendStr),
											this.getQfpaGen().mkAddInt(
												maxWeightVarsFirst.get(v.getIndex() + "_" + u.getIndex() + "_" + (k-1) + appendStr),
												this.getQfpaGen().mkConstantInt(e.getWeight())
											)
										)
									)
								);
							}
						}
					}
					
					BoolExpr changedForm = this.getQfpaGen().mkFalse();
					for(DGVertex u : this.getDgraph(isSkew).getVertices()) {
						for(DGEdge e : u.getEdges()) {
							if(e.getTo().getIndex() == w.getIndex()) {
								BoolExpr andTemp = this.getQfpaGen().mkTrue();
								if(!edgeFlowVarsFirst.containsKey("y_" + e.getFrom().getIndex() + "_" +  e.getTo().getIndex() + appendStr)) {
									edgeFlowVarsFirst.put("y_" + e.getFrom().getIndex() + "_" +  e.getTo().getIndex() + appendStr, this.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" +  e.getTo().getIndex() + appendStr));
								}
								if(!maxWeightVarsFirst.containsKey(v.getIndex() + "_" + u.getIndex() + "_" + (k - 1) + appendStr)) {
									maxWeightVarsFirst.put(v.getIndex() + "_" + u.getIndex() + "_" + (k - 1) + appendStr, this.getQfpaGen().mkVariableInt("z_" + v.getIndex() + "_" + u.getIndex() + "_" + (k - 1) + appendStr));
								}
								if(!maxWeightVarsFirst.containsKey(v.getIndex() + "_" + w.getIndex() + "_" + k + appendStr)) {
									maxWeightVarsFirst.put(v.getIndex() + "_" + w.getIndex() + "_" + k + appendStr, this.getQfpaGen().mkVariableInt("z_" + v.getIndex() + "_" + w.getIndex() + "_" + k + appendStr));
								}
								if(!maxWeightVarsFirst.containsKey(v.getIndex() + "_" + u.getIndex() + "_" + (k - 1) + appendStr)) {
									maxWeightVarsFirst.put(v.getIndex() + "_" + u.getIndex() + "_" + (k - 1) + appendStr, this.getQfpaGen().mkVariableInt("z_" + v.getIndex() + "_" + u.getIndex() + "_" + (k - 1) + appendStr));
								}
								
								andTemp = this.getQfpaGen().mkAndBool(
										this.getQfpaGen().mkGtBool(
											edgeFlowVarsFirst.get("y_" + e.getFrom().getIndex() + "_" +  e.getTo().getIndex() + appendStr),
											this.getQfpaGen().mkConstantInt(0)
										),
										this.getQfpaGen().mkGtBool(
											maxWeightVarsFirst.get(v.getIndex() + "_" + u.getIndex() + "_" + (k - 1) + appendStr),
											this.getQfpaGen().mkConstantInt(minusInfinite)
										),
										this.getQfpaGen().mkEqBool(
											maxWeightVarsFirst.get(v.getIndex() + "_" + w.getIndex() + "_" + k + appendStr),
											this.getQfpaGen().mkAddInt(
												maxWeightVarsFirst.get(v.getIndex() + "_" + u.getIndex() + "_" + (k - 1) + appendStr),
												this.getQfpaGen().mkConstantInt(e.getWeight())
										)
									)
								);
								
								for(DGVertex n : this.getDgraph(isSkew).getVertices()) {
									for(DGEdge ep : n.getEdges()) {
										if(ep.getTo().getIndex() == w.getIndex() && 
										 !(ep.getFrom().getIndex() == e.getFrom().getIndex() && ep.getTo().getIndex() == e.getTo().getIndex()) ) {
											if(!edgeFlowVarsFirst.containsKey("y_" + ep.getFrom().getIndex() + "_" +  ep.getTo().getIndex() + appendStr)) {
												edgeFlowVarsFirst.put("y_" + ep.getFrom().getIndex() + "_" +  ep.getTo().getIndex() + appendStr, this.getQfpaGen().mkVariableInt("y_" + ep.getFrom().getIndex() + "_" +  ep.getTo().getIndex() + appendStr));
											}
											if(!maxWeightVarsFirst.containsKey(v.getIndex() + "_" + n.getIndex() + "_" + (k - 1) + appendStr)) {
												maxWeightVarsFirst.put(v.getIndex() + "_" + n.getIndex() + "_" + (k - 1) + appendStr, this.getQfpaGen().mkVariableInt("z_" + v.getIndex() + "_" + n.getIndex() + "_" + (k - 1) + appendStr));
											}
											if(!maxWeightVarsFirst.containsKey(v.getIndex() + "_" + u.getIndex() + "_" + (k - 1) + appendStr)) {
												maxWeightVarsFirst.put(v.getIndex() + "_" + u.getIndex() + "_" + (k - 1) + appendStr, this.getQfpaGen().mkVariableInt("z_" + v.getIndex() + "_" + u.getIndex() + "_" + (k - 1) + appendStr));
											}
											if(!maxWeightVarsFirst.containsKey(v.getIndex() + "_" + n.getIndex() + "_" + (k - 1) + appendStr)) {
												maxWeightVarsFirst.put(v.getIndex() + "_" + n.getIndex() + "_" + (k - 1) + appendStr, this.getQfpaGen().mkVariableInt("z_" + v.getIndex() + "_" + n.getIndex() + "_" + (k - 1) + appendStr));
											}
											BoolExpr andAndTemp = this.getQfpaGen().mkImplies(
												this.getQfpaGen().mkAndBool(
													this.getQfpaGen().mkGtBool(
														edgeFlowVarsFirst.get("y_" + ep.getFrom().getIndex() + "_" +  ep.getTo().getIndex() + appendStr),
														this.getQfpaGen().mkConstantInt(0)
													),
													this.getQfpaGen().mkGtBool(
														maxWeightVarsFirst.get(v.getIndex() + "_" + n.getIndex() + "_" + (k - 1) + appendStr),
														this.getQfpaGen().mkConstantInt(minusInfinite)
													)
												),
												this.getQfpaGen().mkGeBool(
													maxWeightVarsFirst.get(v.getIndex() + "_" + u.getIndex() + "_" + (k - 1) + appendStr),
													this.getQfpaGen().mkSubInt(
														maxWeightVarsFirst.get(v.getIndex() + "_" + n.getIndex() + "_" + (k - 1) + appendStr),
														this.getQfpaGen().mkConstantInt(ep.getWeight() - e.getWeight())
													)
												)
											);
											andTemp = this.getQfpaGen().mkAndBool(
												andTemp,
												andAndTemp
											);
										}
									}
								}
								changedForm = this.getQfpaGen().mkOrBool(
									changedForm,
									andTemp
								);
							}
						}
					}
					computationOfVarsForm = this.getQfpaGen().mkAndBool(
						computationOfVarsForm,
						this.getQfpaGen().mkOrBool(
							remainUnchangedForm,
							changedForm
						)
					);
				}
			}
		}
		bfResult = this.getQfpaGen().mkAndBool(bfResult, computationOfVarsForm);
		return bfResult;
	}
	
	public BoolExpr genAPCForm(HashMap<String, IntExpr> edgeFlowVarFirst, Boolean isSkew) {
		//TODO: DEBUG

		String appendStr = isSkew ? "_second_skew" : "_first";
		HashMap<String, IntExpr> maxWeightVarsFirst  = new HashMap<String, IntExpr>();
		BoolExpr existBody = this.genBellmanFord(edgeFlowVarFirst, maxWeightVarsFirst, isSkew);
		BoolExpr zNonPosRequireForm = this.getQfpaGen().mkTrue();
		for(DGVertex v : this.getDgraph(isSkew).getVertices()) {
				zNonPosRequireForm = this.getQfpaGen().mkAndBool(
					zNonPosRequireForm,
					this.getQfpaGen().mkLeBool(maxWeightVarsFirst.get(v.getIndex() + "_" + v.getIndex() + "_" + this.getDgraph().getVertices().size() + appendStr), this.getQfpaGen().mkConstantInt(0))
				);
		}
		existBody = this.getQfpaGen().mkAndBool(existBody, zNonPosRequireForm);
		IntExpr[] maxWeightVarsArray = new IntExpr[maxWeightVarsFirst.size()];
		maxWeightVarsFirst.values().toArray(maxWeightVarsArray);
		return (BoolExpr) this.getQfpaGen().mkExistsQuantifier(maxWeightVarsArray, existBody);
	}
	
	public BoolExpr genReachabilityCertificate(int startIndex, int endIndex,
											   IntExpr sVar, IntExpr tVar, HashMap<String, IntExpr> edgeFlowVars) {
		// TODO: debug
		//hashmap vars for different types
		HashMap<String, IntExpr> edgeFlowVarFirst = new HashMap<String, IntExpr>();
		HashMap<String, IntExpr> edgeFlowVarSec = new HashMap<String, IntExpr>();
		HashMap<String, IntExpr> edgeFlowVarThird = new HashMap<String, IntExpr>();
		for(DGEdge e : this.getDgraph(false).getEdges()) {
			edgeFlowVarFirst.put("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first", this.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first"));
			edgeFlowVarSec.put("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_second", this.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_second"));
			edgeFlowVarThird.put("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third", this.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third"));
		}
		// bound vars array
		IntExpr[] firstBoundVars = new IntExpr[edgeFlowVarFirst.values().size()];
		IntExpr[] secondBoundVars = new IntExpr[edgeFlowVarSec.values().size()];
		IntExpr[] thirdBoundVars = new IntExpr[edgeFlowVarThird.values().size()];
		edgeFlowVarFirst.values().toArray(firstBoundVars);
		edgeFlowVarSec.values().toArray(secondBoundVars);
		edgeFlowVarThird.values().toArray(thirdBoundVars);
		
		// equal forms of different types
		BoolExpr type1FlowSumEqualForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(false).getEdges()) {
			BoolExpr temp = this.getQfpaGen().mkEqBool(
				edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first"),
				edgeFlowVars.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex())
			);
			type1FlowSumEqualForm = this.getQfpaGen().mkAndBool(type1FlowSumEqualForm, temp);
		}
		BoolExpr type2FlowSumEqualForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(false).getEdges()) {
			BoolExpr temp = this.getQfpaGen().mkEqBool(
				edgeFlowVarSec.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_second"),
				edgeFlowVars.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex())
			);
			type2FlowSumEqualForm = this.getQfpaGen().mkAndBool(type2FlowSumEqualForm, temp);
		}
		BoolExpr type3FlowSumEqualForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(false).getEdges()) {
			BoolExpr temp = this.getQfpaGen().mkEqBool(
				edgeFlowVarThird.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third"),
				edgeFlowVars.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex())
			);
			type3FlowSumEqualForm = this.getQfpaGen().mkAndBool(type3FlowSumEqualForm, temp);
		}
		BoolExpr type12FlowSumEqualForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(false).getEdges()) {
			BoolExpr temp = this.getQfpaGen().mkEqBool(
				this.getQfpaGen().mkAddInt(
					edgeFlowVarSec.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_second"),
					edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first")
				),
				edgeFlowVars.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex())
			);
			type12FlowSumEqualForm = this.getQfpaGen().mkAndBool(type12FlowSumEqualForm, temp);
		}
		BoolExpr type13FlowSumEqualForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(false).getEdges()) {
			BoolExpr temp = this.getQfpaGen().mkEqBool(
				this.getQfpaGen().mkAddInt(
					edgeFlowVarThird.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third"),
					edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first")
				),
				edgeFlowVars.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex())
			);
			type13FlowSumEqualForm = this.getQfpaGen().mkAndBool(type13FlowSumEqualForm, temp);
		}
		BoolExpr type23FlowSumEqualForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(false).getEdges()) {
			BoolExpr temp = this.getQfpaGen().mkEqBool(
				this.getQfpaGen().mkAddInt(
					edgeFlowVarSec.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_second"),
					edgeFlowVarThird.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third")
				),
				edgeFlowVars.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex())
			);
			type23FlowSumEqualForm = this.getQfpaGen().mkAndBool(type23FlowSumEqualForm, temp);
		}
		BoolExpr type123FlowSumEqualForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(false).getEdges()) {
			BoolExpr temp = this.getQfpaGen().mkEqBool(
				this.getQfpaGen().mkAddInt(
					this.getQfpaGen().mkAddInt(
						edgeFlowVarSec.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_second"),
						edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first")
					),
					edgeFlowVarThird.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third")
				),
				edgeFlowVars.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex())
			);
			type123FlowSumEqualForm = this.getQfpaGen().mkAndBool(type123FlowSumEqualForm, temp);
		}
		
		// certificates for different types
		BoolExpr type1CertForm = this.getQfpaGen().mkAndBool(
			this.genType1RCForm(startIndex, endIndex, sVar, tVar, edgeFlowVarFirst, false),
			type1FlowSumEqualForm
		);
		type1CertForm = (BoolExpr)this.getQfpaGen().mkExistsQuantifier(firstBoundVars, type1CertForm);
		
		BoolExpr type2CertForm = this.getQfpaGen().mkAndBool(
				this.genType2RCForm(startIndex, endIndex, sVar, tVar, edgeFlowVarSec),
				type2FlowSumEqualForm
			);
		type2CertForm = (BoolExpr)this.getQfpaGen().mkExistsQuantifier(secondBoundVars, type2CertForm);
		
		BoolExpr type3CertForm = this.getQfpaGen().mkAndBool(
				this.genType3RCForm(startIndex, endIndex, sVar, tVar, edgeFlowVarThird),
				type3FlowSumEqualForm
			);
		type3CertForm = (BoolExpr)this.getQfpaGen().mkExistsQuantifier(thirdBoundVars, type3CertForm);
		
		IntExpr type12MidVar = this.getQfpaGen().mkVariableInt("Mid_12");
		IntExpr[] type12MidBound = new IntExpr[1];
		type12MidBound[0] = type12MidVar;
		BoolExpr type12CertPathForm = this.getQfpaGen().mkFalse();
		for(DGVertex v : this.getDgraph(false).getVertices()) {
			type12CertPathForm = this.getQfpaGen().mkOrBool(
				type12CertPathForm,
				this.getQfpaGen().mkAndBool(
					this.genType1RCForm(startIndex, v.getIndex(), sVar, type12MidVar, edgeFlowVarFirst, false),
					this.genType2RCForm(v.getIndex(), endIndex, type12MidVar, tVar, edgeFlowVarSec)
				)
			);
		}
		BoolExpr type12CertForm = this.getQfpaGen().mkAndBool(
			type12CertPathForm,
			type12FlowSumEqualForm
		);
		type12CertForm = (BoolExpr)this.getQfpaGen().mkExistsQuantifier(type12MidBound, type12CertForm);
		type12CertForm = (BoolExpr)this.getQfpaGen().mkExistsQuantifier(firstBoundVars, type12CertForm);
		type12CertForm = (BoolExpr)this.getQfpaGen().mkExistsQuantifier(secondBoundVars, type12CertForm);
		
		IntExpr type13MidVar = this.getQfpaGen().mkVariableInt("Mid_13");
		IntExpr[] type13MidBound = new IntExpr[1];
		type13MidBound[0] = type13MidVar;
		BoolExpr type13CertPathForm = this.getQfpaGen().mkFalse();
		for(DGVertex v : this.getDgraph(false).getVertices()) {
			type13CertPathForm = this.getQfpaGen().mkOrBool(
				type13CertPathForm,
				this.getQfpaGen().mkAndBool(
					this.genType1RCForm(startIndex, v.getIndex(), sVar, type13MidVar, edgeFlowVarFirst, false),
					this.genType3RCForm(v.getIndex(), endIndex, type12MidVar, tVar, edgeFlowVarThird)
				)
			);
		}
		BoolExpr type13CertForm = this.getQfpaGen().mkAndBool(
			type13CertPathForm,
			type13FlowSumEqualForm
		);
		type13CertForm = (BoolExpr)this.getQfpaGen().mkExistsQuantifier(type13MidBound, type13CertForm);
		type13CertForm = (BoolExpr)this.getQfpaGen().mkExistsQuantifier(firstBoundVars, type13CertForm);
		type13CertForm = (BoolExpr)this.getQfpaGen().mkExistsQuantifier(thirdBoundVars, type13CertForm);
		
		IntExpr type23MidVar = this.getQfpaGen().mkVariableInt("Mid_23");
		IntExpr[] type23MidBound = new IntExpr[1];
		type23MidBound[0] = type23MidVar;
		BoolExpr type23CertPathForm = this.getQfpaGen().mkFalse();
		for(DGVertex v : this.getDgraph(false).getVertices()) {
			type23CertPathForm = this.getQfpaGen().mkOrBool(
				type23CertPathForm,
				this.getQfpaGen().mkAndBool(
					this.genType2RCForm(startIndex, v.getIndex(), sVar, type23MidVar, edgeFlowVarSec),
					this.genType3RCForm(v.getIndex(), endIndex, type23MidVar, tVar, edgeFlowVarThird)
				)
			);
		}
		BoolExpr type23CertForm = this.getQfpaGen().mkAndBool(
			type23CertPathForm,
			type23FlowSumEqualForm
		);
		type23CertForm = (BoolExpr)this.getQfpaGen().mkExistsQuantifier(type23MidBound, type23CertForm);
		type23CertForm = (BoolExpr)this.getQfpaGen().mkExistsQuantifier(secondBoundVars, type23CertForm);
		type23CertForm = (BoolExpr)this.getQfpaGen().mkExistsQuantifier(thirdBoundVars, type23CertForm);

		IntExpr type123MidVar_1 = this.getQfpaGen().mkVariableInt("Mid_123_1");
		IntExpr type123MidVar_2 = this.getQfpaGen().mkVariableInt("Mid_123_2");
		IntExpr[] type123MidBound = new IntExpr[2];
		type123MidBound[0] = type123MidVar_1;
		type123MidBound[1] = type123MidVar_2;
		BoolExpr type123CertPathForm = this.getQfpaGen().mkFalse();
		for(DGVertex v : this.getDgraph(false).getVertices()) {
			for(DGVertex vp : this.getDgraph(false).getVertices()) {
				type13CertPathForm = this.getQfpaGen().mkOrBool(
					type13CertPathForm,
					this.getQfpaGen().mkAndBool(
						this.genType1RCForm(startIndex, v.getIndex(), sVar, type123MidVar_1, edgeFlowVarFirst, false),
						this.genType3RCForm(v.getIndex(), vp.getIndex(), type123MidVar_1, type123MidVar_2, edgeFlowVarThird),
						this.genType2RCForm(vp.getIndex(), endIndex, type123MidVar_2, tVar, edgeFlowVarSec)
					)
				);
			}
		}
		BoolExpr type123CertForm = this.getQfpaGen().mkAndBool(
			type123CertPathForm,
			type123FlowSumEqualForm
		);
		type123CertForm = (BoolExpr)this.getQfpaGen().mkExistsQuantifier(type123MidBound, type123CertForm);
		type123CertForm = (BoolExpr)this.getQfpaGen().mkExistsQuantifier(firstBoundVars, type123CertForm);
		type123CertForm = (BoolExpr)this.getQfpaGen().mkExistsQuantifier(secondBoundVars, type123CertForm);
		type123CertForm = (BoolExpr)this.getQfpaGen().mkExistsQuantifier(thirdBoundVars, type123CertForm);
		
		BoolExpr result = this.getQfpaGen().mkOrBool(
			this.getQfpaGen().mkImplies(
				this.getQfpaGen().mkEqBool(this.getQfpaGen().mkConstantInt(startIndex), this.getQfpaGen().mkConstantInt(endIndex)),
				this.getQfpaGen().mkEqBool(sVar, tVar)
			),
			type1CertForm,
			type12CertForm,
			type13CertForm,
			type23CertForm,
			type123CertForm,
			type2CertForm,
			type3CertForm
		);
		//result = type1CertForm;
		return result;
	}
	
	public BoolExpr genReachabilityCertificateBack(int startIndex, int endIndex,
			   IntExpr sVar, IntExpr tVar, HashMap<String, IntExpr> edgeFlowVars) {
		BoolExpr result = this.getQfpaGen().mkTrue();
		IntExpr[] midVars = new IntExpr[2];
		midVars[0] = this.getQfpaGen().mkVariableInt("Mid_1");
		midVars[1] = this.getQfpaGen().mkVariableInt("Mid_2");
		HashMap<String, IntExpr> edgeFlowVarFirst = new HashMap<String, IntExpr>();
		HashMap<String, IntExpr> edgeFlowVarSec = new HashMap<String, IntExpr>();
		HashMap<String, IntExpr> edgeFlowVarThird = new HashMap<String, IntExpr>();
		for(DGEdge e : this.getDgraph(false).getEdges()) {
			edgeFlowVarFirst.put("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first", this.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first"));
			edgeFlowVarSec.put("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_second", this.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_second"));
			edgeFlowVarThird.put("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third", this.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third"));
		}
		BoolExpr type123FlowSumEqualForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(false).getEdges()) {
			BoolExpr temp = this.getQfpaGen().mkEqBool(
				this.getQfpaGen().mkAddInt(
					this.getQfpaGen().mkAddInt(
						edgeFlowVarSec.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_second"),
						edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_first")
					),
					edgeFlowVarThird.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third")
				),
				edgeFlowVars.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex())
			);
			type123FlowSumEqualForm = this.getQfpaGen().mkAndBool(type123FlowSumEqualForm, temp);
		}
		for(DGVertex mid1 : this.getDgraph(false).getVertices()) {
			for(DGVertex mid2 : this.getDgraph(false).getVertices()) {
				BoolExpr certTemp = this.getQfpaGen().mkAndBool(
					this.genType1RCForm(startIndex, mid1.getIndex(), sVar, midVars[0], edgeFlowVarFirst, false),
					this.genType3RCForm(mid1.getIndex(), mid2.getIndex(), midVars[0], midVars[1], edgeFlowVarThird),
					this.genType2RCForm(mid2.getIndex(), endIndex, midVars[1], tVar, edgeFlowVarSec),
					type123FlowSumEqualForm
				);
				result = this.getQfpaGen().mkAndBool(result, certTemp);
			}
		}
		result = (BoolExpr)this.getQfpaGen().mkExistsQuantifier(midVars, result);
		return result;
	}
	
	public BoolExpr genType1RCForm(int startIndex, int endIndex,
								   IntExpr s1, IntExpr t1, HashMap<String, IntExpr> edgeFlowVarFirst,
								   Boolean isSkew) {
		//TODO: DEBUG
		HashMap<String, IntExpr> edgeDecomVars = new HashMap<String, IntExpr>();
		HashMap<String, IntExpr> indexVars = new HashMap<String, IntExpr>();
		HashMap<String, IntExpr> sumVars = new HashMap<String, IntExpr>();
		String appendStr = isSkew? "_second_skew" : "_first";
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
				edgeDecomVars.put("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_edge(" + ep.getFrom().getIndex() + "," + ep.getTo().getIndex() + ")" + appendStr, 
								  this.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_edge(" + ep.getFrom().getIndex() + "," + ep.getTo().getIndex() + ")" + appendStr));
			}
		}
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			indexVars.put("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr, 
					      this.getQfpaGen().mkVariableInt("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr));
		}
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			sumVars.put("sum_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr, 
					    this.getQfpaGen().mkVariableInt("sum_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr));
		}
		//TODO: check the appendStr whether is needed to add after the variables
 		BoolExpr result = this.genAPCForm(edgeFlowVarFirst, isSkew);
		BoolExpr residue = this.getQfpaGen().mkAndBool(
			this.genIDXForm(startIndex, endIndex, edgeFlowVarFirst, indexVars, isSkew),
			this.genEDCForm(startIndex, endIndex, edgeFlowVarFirst, indexVars, edgeDecomVars, isSkew),
			this.genNZEForm(startIndex, endIndex, edgeFlowVarFirst, indexVars, edgeDecomVars, sumVars, s1, t1, isSkew),
			this.genWGTForm(startIndex, endIndex, edgeFlowVarFirst, indexVars, sumVars, s1, t1, isSkew)
		);
		IntExpr[] edgeDecomVarsArray = new IntExpr[edgeDecomVars.size()];
		IntExpr[] indexVarsArray = new IntExpr[indexVars.size()];
		IntExpr[] sumVarsArray = new IntExpr[sumVars.size()];
		edgeDecomVars.values().toArray(edgeDecomVarsArray);
		indexVars.values().toArray(indexVarsArray);
		sumVars.values().toArray(sumVarsArray);
		residue = this.getQfpaGen().mkAndBool(
			residue,
			this.getQfpaGen().mkRequireNonNeg(indexVarsArray)
		);
		residue = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(sumVarsArray, residue);
		residue = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(indexVarsArray, residue);
		residue = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(edgeDecomVarsArray, residue);
		result = this.getQfpaGen().mkAndBool(result, residue);
		return result;
	}
	
	public BoolExpr genType2RCForm(int startIndex, int endIndex,
								   IntExpr s2, IntExpr t2, HashMap<String, IntExpr> edgeFlowVarSec) {
		//TODO: imple change _second_skew here
		HashMap<String, IntExpr> edgeFlowVarSecSkew = new HashMap<String, IntExpr>();
		for(DGEdge e : this.getDgraph(true).getEdges()) {
			edgeFlowVarSecSkew.put("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_second_skew", 
								   this.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_second_skew"));
		}
		BoolExpr flowVarSkewEqualForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(false).getEdges()) {
			flowVarSkewEqualForm = this.getQfpaGen().mkEqBool(
				edgeFlowVarSec.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_second"),
				edgeFlowVarSecSkew.get("y_" + e.getTo().getIndex() + "_" + e.getFrom().getIndex() + "_second_skew")
			);
		}
		
		IntExpr[] bound = new IntExpr[edgeFlowVarSecSkew.values().size()];
		edgeFlowVarSecSkew.values().toArray(bound);
		BoolExpr existBody = this.genType1RCForm(startIndex, endIndex, s2, t2, edgeFlowVarSecSkew, true);
		existBody = this.getQfpaGen().mkAndBool(
			existBody,
			this.getQfpaGen().mkRequireNonNeg(bound)
		);
		BoolExpr existForm = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(bound, existBody);
		BoolExpr result = this.getQfpaGen().mkAndBool(
			flowVarSkewEqualForm,
			existForm
		);
		return result;
	}
	
	
	public BoolExpr genType3RCForm(int startIndex, int endIndex,
								   IntExpr s3, IntExpr t3, HashMap<String, IntExpr> edgeFlowVarsThird) {
		// TODO: DEBUG
		List<HashMap<String, IntExpr>> indexList = new ArrayList<HashMap<String, IntExpr>>();//6
		for(int i = 0; i < 3; i ++) {
			indexList.add(new HashMap<String, IntExpr>());
			for(DGEdge e : this.getDgraph(false).getEdges()) {
				indexList.get(i).put(
					"idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + (i+1) + ")",
					this.getQfpaGen().mkVariableInt("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + (i+1) + ")")
				);
			}
		}
		for(int i = 3; i < 6; i ++) { 
			indexList.add(new HashMap<String, IntExpr>());
			for(DGEdge e : this.getDgraph(true).getEdges()) {
				indexList.get(i).put(
					"idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + (i+1) + ")",
					this.getQfpaGen().mkVariableInt("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + (i+1) + ")")
				);
			}
		}
		
		List<HashMap<String, IntExpr>> sumVarList = new ArrayList<HashMap<String, IntExpr>>();//4
		List<HashMap<String, IntExpr>> dropVarList = new ArrayList<HashMap<String, IntExpr>>();//4
		for(int i = 0; i < 4; i ++) {
			sumVarList.add(new HashMap<String, IntExpr>());
			dropVarList.add(new HashMap<String, IntExpr>());
			for(DGVertex v : this.getDgraph((i < 2) ? false : true).getVertices()) {
				sumVarList.get(i).put(
					"sum_" + v.getIndex() + "_(" + (i+1) + ")",
					this.getQfpaGen().mkVariableInt("sum_" + v.getIndex() + "_(" + (i+1) + ")")
				);
				dropVarList.get(i).put(
					"drop_" + v.getIndex() + "_(" + (i+1) + ")",
					this.getQfpaGen().mkVariableInt("drop_" + v.getIndex() + "_(" + (i+1) + ")")
				);
			}
		}
		
		HashMap<String, IntExpr> edgeFlowVarsThirdSkew = new HashMap<String, IntExpr>();
		for(DGEdge e : this.getDgraph(true).getEdges()) {
			edgeFlowVarsThirdSkew.put(
				"y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third_skew",
				this.getQfpaGen().mkVariableInt("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third_skew")
			);
		}
		
		BoolExpr pathFlowFormType3 = this.genPathFlowFormType3(startIndex, endIndex, edgeFlowVarsThird);
		IntExpr weightSumForm = this.getQfpaGen().mkConstantInt(0);
		for(DGEdge e : this.getDgraph(false).getEdges()) {
			weightSumForm = this.getQfpaGen().mkAddInt(weightSumForm, 
							this.getQfpaGen().mkScalarTimes(edgeFlowVarsThird.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third"), this.getQfpaGen().mkConstantInt(e.getWeight())));
		}
		BoolExpr weightRequirement = this.getQfpaGen().mkEqBool(
			this.getQfpaGen().mkAddInt(s3, weightSumForm),
			t3
		);
		BoolExpr skewAndNormalEqualForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(false).getEdges()) {
			skewAndNormalEqualForm = this.getQfpaGen().mkAndBool(
				skewAndNormalEqualForm,
				this.getQfpaGen().mkEqBool(
					edgeFlowVarsThird.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third"), 
					edgeFlowVarsThirdSkew.get("y_" + e.getTo().getIndex() + "_" + e.getFrom().getIndex() + "_third_skew")
				)
			);
		}
		
		BoolExpr twoPosCyclesForm = this.getQfpaGen().mkAndBool(
			this.genPCTForm(startIndex, s3, indexList, sumVarList, dropVarList, edgeFlowVarsThird, false),
			this.genPCTForm(endIndex, t3, indexList, sumVarList, dropVarList, edgeFlowVarsThirdSkew, true)
		);
		
		BoolExpr existBody = this.getQfpaGen().mkAndBool(twoPosCyclesForm, skewAndNormalEqualForm);
		BoolExpr result = existBody;
		for(HashMap<String, IntExpr> m : indexList) {
			IntExpr[] temp = new IntExpr[m.values().size()];
			m.values().toArray(temp);
			result = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(temp, result);
		}
		for(HashMap<String, IntExpr> m : sumVarList) {
			IntExpr[] temp = new IntExpr[m.values().size()];
			m.values().toArray(temp);
			result = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(temp, result);
		}
		for(HashMap<String, IntExpr> m : dropVarList) {
			IntExpr[] temp = new IntExpr[m.values().size()];
			m.values().toArray(temp);
			result = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(temp, result);
		}
		IntExpr[] edgeFlowVarsThirdSkewArray = new IntExpr[edgeFlowVarsThirdSkew.values().size()];
		edgeFlowVarsThirdSkew.values().toArray(edgeFlowVarsThirdSkewArray);
		result = this.getQfpaGen().mkAndBool(pathFlowFormType3, weightRequirement, result);
		result = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(edgeFlowVarsThirdSkewArray, result);
		return result;
	}
	
	
	public BoolExpr genIDXForm(int startIndex, int endIndex,
							   HashMap<String, IntExpr> edgeFlowVarFirst, HashMap<String, IntExpr> indexVars,
							   Boolean isSkew) {
		//TODO: DEBUG
		String appendStr = isSkew ? "_second_skew" : "_first"; 
		BoolExpr edgeFlowImpliesForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			BoolExpr largeThanZeroForm = this.getQfpaGen().mkImplies(
				this.getQfpaGen().mkGtBool(edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(0)),
				this.getQfpaGen().mkGtBool(indexVars.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(0))
			);
			BoolExpr equalToZeroForm = this.getQfpaGen().mkImplies(
				this.getQfpaGen().mkEqBool(edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(0)),
				this.getQfpaGen().mkEqBool(indexVars.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(0))
			);
			edgeFlowImpliesForm = this.getQfpaGen().mkAndBool(edgeFlowImpliesForm, largeThanZeroForm, equalToZeroForm);
		}
		BoolExpr indexNotSameForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
				if(e.getFrom().getIndex() != ep.getFrom().getIndex() || e.getTo().getIndex() != ep.getTo().getIndex()) {
					BoolExpr temp = this.getQfpaGen().mkImplies(
						this.getQfpaGen().mkAndBool(
							this.getQfpaGen().mkGtBool(edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(0)),
							this.getQfpaGen().mkGtBool(edgeFlowVarFirst.get("y_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(0))
						),
						this.getQfpaGen().mkNotEqual(
							indexVars.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr),
							indexVars.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + appendStr)
						)
					);
					indexNotSameForm = this.getQfpaGen().mkAndBool(
						indexNotSameForm, temp
					);
				}
			}
		}
		BoolExpr indexPathForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			BoolExpr impliesFrom = this.getQfpaGen().mkAndBool(
				this.getQfpaGen().mkGtBool(edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(0)),
				this.getQfpaGen().mkGtBool(indexVars.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(1))
			);
			BoolExpr impliesTo = this.getQfpaGen().mkFalse();
			for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
				BoolExpr temp = this.getQfpaGen().mkAndBool(
					this.getQfpaGen().mkGtBool(edgeFlowVarFirst.get("y_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(0)),
					this.getQfpaGen().mkEqBool(
						this.getQfpaGen().mkAddInt(indexVars.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(1)),
						indexVars.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr)
					)
				);
				impliesTo = this.getQfpaGen().mkOrBool(impliesTo, temp);
			}
			indexPathForm = this.getQfpaGen().mkAndBool(
				indexPathForm,
				this.getQfpaGen().mkImplies(impliesFrom, impliesTo)
			);
		}
		BoolExpr endVertexMaxForm = this.getQfpaGen().mkFalse();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			if(e.getTo().getIndex() == endIndex) {
				BoolExpr temp = this.getQfpaGen().mkGtBool(
					edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr),
					this.getQfpaGen().mkConstantInt(0)
				);
				BoolExpr tempTemp = this.getQfpaGen().mkTrue();
				for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
					tempTemp = this.getQfpaGen().mkAndBool(
						tempTemp,
						this.getQfpaGen().mkLeBool(
							indexVars.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + appendStr),
							indexVars.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr)
						)
					);
				}
				temp = this.getQfpaGen().mkAndBool(temp, tempTemp);
				endVertexMaxForm = this.getQfpaGen().mkOrBool(endVertexMaxForm, temp);

			}
		}
		BoolExpr startVertexMinForm = this.getQfpaGen().mkFalse();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			if(e.getFrom().getIndex() == startIndex) {
				BoolExpr temp = this.getQfpaGen().mkGtBool(
					edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr),
					this.getQfpaGen().mkConstantInt(0)
				);
				BoolExpr tempTemp = this.getQfpaGen().mkTrue();
				for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
					tempTemp = this.getQfpaGen().mkAndBool(
						tempTemp,
						this.getQfpaGen().mkGeBool(
							indexVars.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + appendStr),
							indexVars.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr)
						)
					);
				}
				temp = this.getQfpaGen().mkAndBool(temp, tempTemp);
				startVertexMinForm = this.getQfpaGen().mkOrBool(startVertexMinForm, temp);
			}
		}
		BoolExpr result = this.getQfpaGen().mkAndBool(
			edgeFlowImpliesForm,
			indexNotSameForm,
			indexPathForm,
			endVertexMaxForm,
			startVertexMinForm
		);
		return result;
	}
	
	public BoolExpr genEDCForm(int startIndex, int endIndex,
							   HashMap<String, IntExpr> edgeFlowVarFirst, HashMap<String, IntExpr> indexVars,
							   HashMap<String, IntExpr> edgeDecomVars, 
							   Boolean isSkew) {
		//TODO: DEBUG
		String appendStr = isSkew ? "_second_skew" : "_first"; 
		BoolExpr startEdgeForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) { 
			BoolExpr impliesTemp = this.getQfpaGen().mkImplies(
				this.getQfpaGen().mkAndBool(
					this.getQfpaGen().mkGtBool(edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(0)),
					this.getQfpaGen().mkEqBool(indexVars.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(1))
				),
				this.genPathFlowFormEdgeDecom(startIndex, e.getFrom().getIndex(), edgeDecomVars, e, isSkew)
			);
			startEdgeForm = this.getQfpaGen().mkAndBool(
				startEdgeForm, impliesTemp
			);
		}
		BoolExpr edgeToEdgeForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
				//TODO: check whether e and e' are same
				if(true) {
					BoolExpr impliesTemp = this.getQfpaGen().mkImplies(
						this.getQfpaGen().mkAndBool(
							this.getQfpaGen().mkGtBool(edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(0)),
							this.getQfpaGen().mkGtBool(edgeFlowVarFirst.get("y_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(0)),
							this.getQfpaGen().mkEqBool(
								this.getQfpaGen().mkAddInt(indexVars.get("idx_" + ep.getFrom().getIndex() + "_" +  ep.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(1)),
								indexVars.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr)
							)
						),
						this.genPathFlowFormEdgeDecom(ep.getTo().getIndex(), e.getFrom().getIndex(), edgeDecomVars, e, isSkew)
					);
					edgeToEdgeForm = this.getQfpaGen().mkAndBool(
						edgeToEdgeForm,
						impliesTemp
					);
				}
			}
		}
		BoolExpr edgeDecomOrderForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
				//TODO: check e and e' are same
				if(true) {
					BoolExpr impliesFrom = this.getQfpaGen().mkAndBool(
						this.getQfpaGen().mkGtBool(edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(0)),
						this.getQfpaGen().mkGtBool(edgeFlowVarFirst.get("y_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(0)),
						this.getQfpaGen().mkLtBool(indexVars.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr), indexVars.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + appendStr))
					);
					BoolExpr impliesTo = this.getQfpaGen().mkEqBool(
						edgeDecomVars.get("y_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_edge(" + e.getFrom().getIndex() + "," + e.getTo().getIndex() + ")" + appendStr),
						this.getQfpaGen().mkConstantInt(0)
					);
					edgeDecomOrderForm = this.getQfpaGen().mkImplies(impliesFrom, impliesTo);
				}
			}
		}
		BoolExpr flowVarCorrectSumForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			IntExpr sum = this.getQfpaGen().mkConstantInt(0);
			for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
				sum = this.getQfpaGen().mkAddInt(
					sum, 
					edgeDecomVars.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_edge(" + ep.getFrom().getIndex() + "," + ep.getTo().getIndex() + ")" + appendStr)
				);
			}
			// add y_{e}^1 to the flow
			sum = this.getQfpaGen().mkAddInt(sum, this.getQfpaGen().mkConstantInt(1));
			flowVarCorrectSumForm = this.getQfpaGen().mkAndBool(
				flowVarCorrectSumForm,
				this.getQfpaGen().mkEqBool(
					sum,
					edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr)
				)
			);
		}
		
		BoolExpr result = this.getQfpaGen().mkAndBool(
			startEdgeForm,
			edgeToEdgeForm,
			edgeDecomOrderForm,
			flowVarCorrectSumForm
		);
		return result;
	}
	
	
	public BoolExpr genNZEForm(int startIndex, int endIndex,
							   HashMap<String, IntExpr> edgeFlowVarFirst, HashMap<String, IntExpr> indexVars,
							   HashMap<String, IntExpr> edgeDecomVars, HashMap<String, IntExpr> sumVars,
							   IntExpr sVar, IntExpr tVar,
							   Boolean isSkew) {
		//TODO: DEBUG
		String appendStr = isSkew ? "_second_skew" : "_first"; 
		BoolExpr sumAddCorrectlyForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			BoolExpr impliesFrom = this.getQfpaGen().mkAndBool(
				this.getQfpaGen().mkGtBool(edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(0)),
				this.getQfpaGen().mkEqBool(indexVars.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr), this.getQfpaGen().mkConstantInt(1))
			);
			
			IntExpr epWeightSum = this.getQfpaGen().mkConstantInt(0);
			for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
				epWeightSum = this.getQfpaGen().mkAddInt(
					epWeightSum,
					this.getQfpaGen().mkScalarTimes(
						this.getQfpaGen().mkConstantInt(ep.getWeight()),
						edgeDecomVars.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_edge(" + ep.getFrom().getIndex() + "," + ep.getTo().getIndex() + ")" + appendStr)
					)
				);
			}
			BoolExpr impliesTo = this.getQfpaGen().mkEqBool(
				sumVars.get("sum_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr),
				this.getQfpaGen().mkAddInt(
					this.getQfpaGen().mkConstantInt(e.getWeight()),
					epWeightSum
				)
			);
			sumAddCorrectlyForm = this.getQfpaGen().mkAndBool(
				sumAddCorrectlyForm,
				this.getQfpaGen().mkImplies(impliesFrom, impliesTo)
			);
		}
		BoolExpr edgeSumOrderForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
				BoolExpr impliesFrom = this.getQfpaGen().mkAndBool(
					this.getQfpaGen().mkGtBool(
						edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr),
						this.getQfpaGen().mkConstantInt(0)
					),
					this.getQfpaGen().mkGtBool(
						edgeFlowVarFirst.get("y_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + appendStr),
						this.getQfpaGen().mkConstantInt(0)
					),
					this.getQfpaGen().mkEqBool(
						indexVars.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + appendStr),
						this.getQfpaGen().mkAddInt(
							indexVars.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr),
							this.getQfpaGen().mkConstantInt(1)
						)
					)
				);
				IntExpr eppWeightSum = this.getQfpaGen().mkConstantInt(0);
				for(DGEdge epp : this.getDgraph(isSkew).getEdges()) {
					eppWeightSum = this.getQfpaGen().mkAddInt(
						eppWeightSum,
						this.getQfpaGen().mkScalarTimes(
							this.getQfpaGen().mkConstantInt(epp.getWeight()),
							edgeDecomVars.get("y_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_edge(" + epp.getFrom().getIndex() + "," + epp.getTo().getIndex() + ")" + appendStr)
						)
					);
				}
				BoolExpr impliesTo = this.getQfpaGen().mkEqBool(
					this.getQfpaGen().mkAddInt(
						this.getQfpaGen().mkAddInt(
							sumVars.get("sum_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr),
							this.getQfpaGen().mkConstantInt(ep.getWeight())
						),
						eppWeightSum
					),
					sumVars.get("sum_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + appendStr)
				);
				edgeSumOrderForm = this.getQfpaGen().mkAndBool(
					edgeSumOrderForm,
					this.getQfpaGen().mkImplies(impliesFrom, impliesTo)
				);
			}
		}
		BoolExpr nonNegIdxSumRequireForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			BoolExpr impliesFrom = this.getQfpaGen().mkGtBool(
				edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr),
				this.getQfpaGen().mkConstantInt(0)
			);
			BoolExpr impliesTo = this.getQfpaGen().mkGeBool(
				this.getQfpaGen().mkAddInt(sVar, sumVars.get("sum_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr)),
				this.getQfpaGen().mkConstantInt(0)
			);
			nonNegIdxSumRequireForm = this.getQfpaGen().mkAndBool(
				nonNegIdxSumRequireForm,
				this.getQfpaGen().mkImplies(
					impliesFrom,
					impliesTo
				)
			);
		}
		BoolExpr result = this.getQfpaGen().mkAndBool(
			sumAddCorrectlyForm,
			edgeSumOrderForm,
			nonNegIdxSumRequireForm
		);
		return result;
	}
	
	public BoolExpr genWGTForm(int startIndex, int endIndex,
							   HashMap<String, IntExpr> edgeFlowVarFirst, HashMap<String, IntExpr> indexVars,
							   HashMap<String, IntExpr> sumVars, 
							   IntExpr sVar, IntExpr tVar,
							   Boolean isSkew) {
		String appendStr = isSkew ? "_second_skew" : "_first"; 
		BoolExpr result = this.getQfpaGen().mkFalse();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			if(e.getTo().getIndex() == endIndex) {
				BoolExpr flowNotZeroForm = this.getQfpaGen().mkGtBool(
					edgeFlowVarFirst.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr),
					this.getQfpaGen().mkConstantInt(0)
				);
				BoolExpr maxIndexForm = this.getQfpaGen().mkTrue();
				for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
					maxIndexForm = this.getQfpaGen().mkAndBool(
				   		maxIndexForm,
						this.getQfpaGen().mkGeBool(
							indexVars.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr),
							indexVars.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + appendStr)
						)
					);
				}
				BoolExpr correctWeightSumForm = this.getQfpaGen().mkEqBool(
					this.getQfpaGen().mkAddInt(
						sVar,
						sumVars.get("sum_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + appendStr)
					),
					tVar
				);
				result = this.getQfpaGen().mkOrBool(
					result,
					this.getQfpaGen().mkAndBool(
						flowNotZeroForm,
						maxIndexForm,
						correctWeightSumForm
					)
				);
			}
		}
		return result;
	}
	
	public BoolExpr genPathFlowFormEdgeDecom(int startIndex, int endIndex,
									HashMap<String, IntExpr> edgeDecomVars, DGEdge regardingEdge, Boolean isSkew) {
		//TODO: DEBUG
		if(endIndex != regardingEdge.getFrom().getIndex()) {
			System.out.println("ERROR: genPathFlow error!");
			return null;
		}
		int startEqualsEndInt = (startIndex == endIndex) ? 0 : 1;
		BoolExpr notStartEndForm = this.getQfpaGen().mkTrue();
		for(DGVertex v : this.getDgraph(isSkew).getVertices()) {
			if(v.getIndex() != startIndex && v.getIndex() != endIndex) {
				notStartEndForm = this.getQfpaGen().mkAndBool(
					notStartEndForm,
					this.getQfpaGen().mkEqBool(
						this.getFlowSumFromVertex(v.getIndex(), edgeDecomVars, regardingEdge, isSkew),
						this.getFlowSumToVertex(v.getIndex(), edgeDecomVars, regardingEdge, isSkew)
					)
				);
			}
		}
		BoolExpr endForm = this.getQfpaGen().mkEqBool(
			this.getFlowSumToVertex(endIndex, edgeDecomVars, regardingEdge, isSkew),
			this.getQfpaGen().mkAddInt(
				this.getFlowSumFromVertex(endIndex, edgeDecomVars, regardingEdge, isSkew),
				this.getQfpaGen().mkConstantInt(startEqualsEndInt)
			)
		);
		BoolExpr startForm = this.getQfpaGen().mkEqBool(
			this.getFlowSumFromVertex(startIndex, edgeDecomVars, regardingEdge, isSkew),
			this.getQfpaGen().mkAddInt(
				this.getFlowSumToVertex(startIndex, edgeDecomVars, regardingEdge, isSkew),
				this.getQfpaGen().mkConstantInt(startEqualsEndInt)
			)
		);
		BoolExpr result = this.getQfpaGen().mkAndBool(notStartEndForm, endForm, startForm);
		return result;
	}
	
	public BoolExpr genPathFlowFormType3(int startIndex, int endIndex, HashMap<String, IntExpr> edgeFlowVarsThird) {

		int startEqualsEndInt = (startIndex == endIndex) ? 0 : 1;
		BoolExpr notStartEndForm = this.getQfpaGen().mkTrue();
		for(DGVertex v : this.getDgraph().getVertices()) {
			if(v.getIndex() != startIndex && v.getIndex() != endIndex) {
				notStartEndForm = this.getQfpaGen().mkAndBool(
					notStartEndForm,
					this.getQfpaGen().mkEqBool(
						this.getFlowSumFromVertexThird(v.getIndex(), edgeFlowVarsThird),
						this.getFlowSumToVertexThird(v.getIndex(), edgeFlowVarsThird)
					)
				);
			}
		}
		BoolExpr endForm = this.getQfpaGen().mkEqBool(
			this.getQfpaGen().mkAddInt(
				this.getFlowSumFromVertexThird(endIndex, edgeFlowVarsThird),
				this.getQfpaGen().mkConstantInt(startEqualsEndInt)
			),
			this.getFlowSumToVertexThird(endIndex, edgeFlowVarsThird)
		);
		BoolExpr startForm = this.getQfpaGen().mkEqBool(
			this.getFlowSumFromVertexThird(startIndex, edgeFlowVarsThird),
			this.getQfpaGen().mkAddInt(
				this.getFlowSumToVertexThird(startIndex, edgeFlowVarsThird),
				this.getQfpaGen().mkConstantInt(startEqualsEndInt)
			)
		);
		return this.getQfpaGen().mkAndBool(notStartEndForm, startForm, endForm);
	}
	
	public IntExpr getFlowSumToVertexThird(int toVertexIndex, HashMap<String, IntExpr> edgeFlowVarsThird) {
		IntExpr sum = this.getQfpaGen().mkConstantInt(0);
		for(DGEdge e : this.getDgraph().getEdges()) {
			if(e.getTo().getIndex() == toVertexIndex) {
				sum = this.getQfpaGen().mkAddInt(
					sum,
					edgeFlowVarsThird.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third")
				);
			}
		}
		return sum;
	}
	// TODO: HERE DEBUG
	public IntExpr getFlowSumToVertex(int toVertexIndex, HashMap<String, IntExpr> edgeFlowVars, DGEdge regardingEdge, boolean isSkew) {
		String appendStr = isSkew ? "_second_skew" : "_first";
		String regardingEdgeSuffix = regardingEdge.getFrom().getIndex() + "_" + regardingEdge.getTo().getIndex();  
		IntExpr sum = this.getQfpaGen().mkConstantInt(0);
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			if(e.getTo().getIndex() == toVertexIndex) {
				sum = this.getQfpaGen().mkAddInt(
					sum, 
					edgeFlowVars.get("y_" + regardingEdgeSuffix + "_edge(" + e.getFrom().getIndex() + "," + e.getTo().getIndex() + ")" + appendStr)
				);
			}
		}
		return sum;
	}
	
	public IntExpr getFlowSumFromVertexThird(int fromVertexIndex, HashMap<String, IntExpr> edgeFlowVarsThird) {
		//TODO:
		IntExpr sum = this.getQfpaGen().mkConstantInt(0);
		for(DGEdge e : this.getDgraph().getEdges()) {
			if(e.getFrom().getIndex() == fromVertexIndex) {
				sum = this.getQfpaGen().mkAddInt(
					sum,
					edgeFlowVarsThird.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third")
				);
			}
		}
		return sum;
	}
	// TODO: HERE DEBUG
	public IntExpr getFlowSumFromVertex(int fromVertexIndex, HashMap<String, IntExpr> edgeFlowVars, DGEdge regardingEdge, boolean isSkew) {
		String appendStr = isSkew ? "_second_skew" : "_first";
		String regardingEdgeSuffix = regardingEdge.getFrom().getIndex() + "_" + regardingEdge.getTo().getIndex();   
		IntExpr sum = this.getQfpaGen().mkConstantInt(0);
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			if(e.getFrom().getIndex() == fromVertexIndex) {
				sum = this.getQfpaGen().mkAddInt(
					sum,
					edgeFlowVars.get("y_" + regardingEdgeSuffix + "_edge(" + e.getFrom().getIndex() + "," + e.getTo().getIndex() + ")" + appendStr)
				);
			}
		}
		return sum;
	}

	public BoolExpr genPCTForm(int cycleStartIndex, IntExpr sVar, 
			List<HashMap<String, IntExpr>> indexVarsList, List<HashMap<String, IntExpr>> sumVarsList, List<HashMap<String, IntExpr>> dropVarsList,
			HashMap<String, IntExpr> edgeFlowVarsThird, boolean isSkew) {
		int sumDropNum = isSkew ? 3 : 1;
		int appendNum = isSkew ? 4 : 1;
		//System.out.println(sumDropNum +" " + appendNum);
		BoolExpr result = this.getQfpaGen().mkFalse();
		for(DGVertex v : this.getDgraph(isSkew).getVertices()) {
			result = this.getQfpaGen().mkAndBool(
				result,
				this.getQfpaGen().mkAndBool(
					this.genSPAForm(cycleStartIndex, v.getIndex(), sumVarsList.get(0 + sumDropNum - 1), dropVarsList.get(0 + sumDropNum - 1), indexVarsList.get(0 + appendNum - 1), edgeFlowVarsThird, isSkew),
					this.genSPCForm(v.getIndex(), sumVarsList.get(1 + sumDropNum - 1), dropVarsList.get(1 + sumDropNum - 1), indexVarsList.get(1 + appendNum - 1), edgeFlowVarsThird, isSkew),
					this.genSPANotBoundedForm(v.getIndex(), cycleStartIndex, edgeFlowVarsThird, indexVarsList.get(2 + appendNum - 1), isSkew),
					this.genDROForm(sVar, sumVarsList.get(0 + sumDropNum - 1).get("sum_" + v.getIndex() + "_(" + sumDropNum + ")"),
										  dropVarsList.get(0 + sumDropNum - 1).get("drop_" + v.getIndex() + "_(" + sumDropNum + ")"), 
										  dropVarsList.get(1 + sumDropNum - 1).get("drop_" + v.getIndex() + "_(" + (sumDropNum + 1) + ")"))
				)
			);
		}
		return result;
	}
	
	public BoolExpr genSPAForm(int startIndex, int guessIndex, 
							   HashMap<String, IntExpr> sumVars1, HashMap<String, IntExpr> dropVars1, 
							   HashMap<String, IntExpr> indexVars1, HashMap<String, IntExpr> edgeFlowVarsThird, boolean isSkew) {
		//TODO: debug
		int appendNum = isSkew ? 4 : 1;
		int sumDropNum = isSkew ? 3 : 1;
		String skewAppendStr = isSkew ? "_skew" : "";
		/*-----------q1 == q---------*/
		BoolExpr startAndGuessAreSameForm = this.getQfpaGen().mkTrue();
		BoolExpr indicesAreZeroForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			indicesAreZeroForm = this.getQfpaGen().mkAndBool(
				indicesAreZeroForm,
				this.getQfpaGen().mkEqBool(indexVars1.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(0))
			);
		}
		startAndGuessAreSameForm = this.getQfpaGen().mkAndBool(
			indicesAreZeroForm,
			this.getQfpaGen().mkEqBool(this.getQfpaGen().mkConstantInt(startIndex), this.getQfpaGen().mkConstantInt(guessIndex)),
			this.getQfpaGen().mkEqBool(sumVars1.get("sum_" + guessIndex + "_(" + sumDropNum + ")"), this.getQfpaGen().mkConstantInt(0)),
			this.getQfpaGen().mkEqBool(dropVars1.get("drop_" + guessIndex + "_(" + sumDropNum + ")"), this.getQfpaGen().mkConstantInt(0))
		);
		/*---------------q1 ne q------------*/
		
		BoolExpr startAndGuessAreNotSameForm = this.getQfpaGen().mkTrue();
		startAndGuessAreNotSameForm = this.getQfpaGen().mkAndBool(
			this.getQfpaGen().mkNotEqual(this.getQfpaGen().mkConstantInt(startIndex), this.getQfpaGen().mkConstantInt(guessIndex))
		);
		BoolExpr indicesGreaterThanZeroForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			indicesGreaterThanZeroForm = this.getQfpaGen().mkAndBool(
				indicesGreaterThanZeroForm,
				this.getQfpaGen().mkGeBool(
					indexVars1.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"),
					this.getQfpaGen().mkConstantInt(0)
				)
			);
		}
		//check here index or flow
		BoolExpr selfLoopZeroIndexForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			if(e.getFrom().getIndex() == e.getTo().getIndex()) {
				selfLoopZeroIndexForm = this.getQfpaGen().mkAndBool(
					selfLoopZeroIndexForm,
					this.getQfpaGen().mkEqBool(
						indexVars1.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"),
						this.getQfpaGen().mkConstantInt(0)
					)
				);
			}
		}
		
		BoolExpr startIndexFlowForm = this.getQfpaGen().mkFalse();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			if(e.getFrom().getIndex() != e.getTo().getIndex() && e.getFrom().getIndex() == startIndex) {
				startIndexFlowForm = this.getQfpaGen().mkOrBool(
					startIndexFlowForm,
					this.getQfpaGen().mkAndBool(
						this.getQfpaGen().mkGtBool(edgeFlowVarsThird.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third" + skewAppendStr), this.getQfpaGen().mkConstantInt(0)),
						this.getQfpaGen().mkEqBool(indexVars1.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(1))
					)
				);
			}
		}
		
		BoolExpr indexImpliesFlowForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			indexImpliesFlowForm = this.getQfpaGen().mkAndBool(
				indexImpliesFlowForm,
				this.getQfpaGen().mkImplies(
					this.getQfpaGen().mkGtBool(indexVars1.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(0)),
					this.getQfpaGen().mkEqBool(edgeFlowVarsThird.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third" + skewAppendStr), this.getQfpaGen().mkConstantInt(0))
				)
			);
		}
	
		BoolExpr endIndexFlowForm = this.getQfpaGen().mkFalse();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			if(e.getTo().getIndex() == guessIndex) {
				BoolExpr indexSmallerForm = this.getQfpaGen().mkTrue();
				for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
					indexSmallerForm = this.getQfpaGen().mkAndBool(
						indexSmallerForm,
						this.getQfpaGen().mkLeBool(
							indexVars1.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_(" + appendNum + ")"),
							indexVars1.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")")
						)
					);
				}
				endIndexFlowForm = this.getQfpaGen().mkOrBool(
					endIndexFlowForm,
					indexSmallerForm
				);
			}
		}
		
		BoolExpr differentIndexForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
				if(e.getFrom().getIndex() != ep.getFrom().getIndex() || 
				   e.getTo().getIndex() != ep.getTo().getIndex()) {
					BoolExpr impliesFrom = this.getQfpaGen().mkAndBool(
						this.getQfpaGen().mkGtBool(indexVars1.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), 
							                       this.getQfpaGen().mkConstantInt(0)),
						this.getQfpaGen().mkGtBool(indexVars1.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_(" + appendNum + ")"), 
							                       this.getQfpaGen().mkConstantInt(0))
					);
					BoolExpr impliesTo = this.getQfpaGen().mkNotEqual(
						indexVars1.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"),
						indexVars1.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_(" + appendNum + ")")
					);
					differentIndexForm = this.getQfpaGen().mkAndBool(
						differentIndexForm,
						this.getQfpaGen().mkImplies(impliesFrom, impliesTo)
					);
				}
			}
		}
		
		BoolExpr indexGTZeroImpliesSmallerEdgeIndex = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			BoolExpr impliesFrom = this.getQfpaGen().mkGtBool(
				indexVars1.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"),
				this.getQfpaGen().mkConstantInt(1)
			);
			
			BoolExpr impliesTo = this.getQfpaGen().mkFalse();
			for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
				if(ep.getTo().getIndex() == e.getFrom().getIndex()) {
					impliesTo = this.getQfpaGen().mkOrBool(
						impliesTo,
						this.getQfpaGen().mkEqBool(
							this.getQfpaGen().mkAddInt(indexVars1.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(1)),
							indexVars1.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")")
						)
					);
				}
			}
			indexGTZeroImpliesSmallerEdgeIndex = this.getQfpaGen().mkAndBool(
				indexGTZeroImpliesSmallerEdgeIndex,
				this.getQfpaGen().mkImplies(impliesFrom, impliesTo)
			);
		}
		
		BoolExpr linkedEdgeIndexOrderForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
				if(ep.getFrom().getIndex() == e.getTo().getIndex()) {
					BoolExpr impliesFrom = this.getQfpaGen().mkAndBool(
						this.getQfpaGen().mkGtBool(indexVars1.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(0)),
						this.getQfpaGen().mkGtBool(indexVars1.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(0))
					);
					BoolExpr impliesTo = this.getQfpaGen().mkEqBool(
						this.getQfpaGen().mkAddInt(indexVars1.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(1)),
						indexVars1.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_(" + appendNum + ")")
					);
					linkedEdgeIndexOrderForm = this.getQfpaGen().mkAndBool(
						linkedEdgeIndexOrderForm,
						this.getQfpaGen().mkImplies(impliesFrom, impliesTo)
					);
				}
			}
		}
		
		BoolExpr startEdgeSumDropForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			if(e.getFrom().getIndex() == startIndex) {
				BoolExpr impliesFrom = this.getQfpaGen().mkEqBool(
					indexVars1.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"),
					this.getQfpaGen().mkConstantInt(1)
				);
				//System.out.println("sum_" + e.getTo().getIndex() + "_(" + sumDropNum + ")");
				int startDrop = (e.getWeight() >= 0) ? 0 : e.getWeight();
				BoolExpr impliesTo = this.getQfpaGen().mkAndBool(
					this.getQfpaGen().mkEqBool(sumVars1.get("sum_" + e.getTo().getIndex() + "_(" + sumDropNum + ")"), this.getQfpaGen().mkConstantInt(e.getWeight())),
					this.getQfpaGen().mkEqBool(dropVars1.get("drop_" + e.getTo().getIndex() + "_(" + sumDropNum + ")"), this.getQfpaGen().mkConstantInt(startDrop))
				);
				
				startEdgeSumDropForm = this.getQfpaGen().mkAndBool(
					startEdgeSumDropForm,
					this.getQfpaGen().mkImplies(impliesFrom, impliesTo)
				);
			}
		}
		
		BoolExpr notStartEdgeSumDropForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			BoolExpr impliesFrom = this.getQfpaGen().mkGtBool(
				indexVars1.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"),
				this.getQfpaGen().mkConstantInt(1)
			);
			BoolExpr impliesTo = this.getQfpaGen().mkAndBool(
				this.getQfpaGen().mkEqBool(sumVars1.get("sum_" + e.getTo().getIndex() + "_(" + sumDropNum + ")"), 
					this.getQfpaGen().mkAddInt(sumVars1.get("sum_" + e.getFrom().getIndex() + "_(" + sumDropNum + ")"), this.getQfpaGen().mkConstantInt(1))),
				this.getQfpaGen().mkOrBool(
					this.getQfpaGen().mkAndBool(
						this.getQfpaGen().mkGtBool(dropVars1.get("drop_" + e.getFrom().getIndex() + "_(" + sumDropNum + ")"), sumVars1.get("sum_" + e.getTo().getIndex() + "_(" + sumDropNum + ")")),
						this.getQfpaGen().mkEqBool(dropVars1.get("drop_" + e.getTo().getIndex() + "_(" + sumDropNum + ")"), sumVars1.get("sum_" + e.getTo().getIndex() + "_(" + sumDropNum + ")"))
					),
					this.getQfpaGen().mkAndBool(
						this.getQfpaGen().mkLeBool(dropVars1.get("drop_" + e.getFrom().getIndex() + "_(" + sumDropNum + ")"), sumVars1.get("sum_" + e.getTo().getIndex() + "_(" + sumDropNum + ")")),
						this.getQfpaGen().mkEqBool(dropVars1.get("drop_" + e.getTo().getIndex() + "_(" + sumDropNum + ")"), dropVars1.get("drop_" + e.getFrom().getIndex() + "_(" + sumDropNum + ")"))
					)
				)
			);
			notStartEdgeSumDropForm = this.getQfpaGen().mkAndBool(
				notStartEdgeSumDropForm,
				this.getQfpaGen().mkImplies(impliesFrom, impliesTo)
			);
		}
		
		BoolExpr notSameResult = this.getQfpaGen().mkAndBool(
				startAndGuessAreNotSameForm,
				indicesGreaterThanZeroForm,
				selfLoopZeroIndexForm,
				startIndexFlowForm,
				indexImpliesFlowForm,
				endIndexFlowForm,
				differentIndexForm,
				indexGTZeroImpliesSmallerEdgeIndex,
				linkedEdgeIndexOrderForm,
				startEdgeSumDropForm,
				notStartEdgeSumDropForm
		);
		BoolExpr result = this.getQfpaGen().mkOrBool(
			startAndGuessAreSameForm,
			notSameResult
		);
		return result;
	}
	
	public BoolExpr genSPCForm(int guessIndex, 
					           HashMap<String, IntExpr> sumVars2, HashMap<String, IntExpr> dropVars2,
					           HashMap<String, IntExpr> indexVars2, HashMap<String, IntExpr> edgeFlowVarsThird, boolean isSkew) {
		//TODO: DEBUG
		int appendNum = isSkew ? 5 : 2;
		int sumDropNum = isSkew ? 4 : 2;
		String skewAppendStr =  isSkew ? "_skew" : "";
		BoolExpr indexGeZeroForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			indexGeZeroForm = this.getQfpaGen().mkAndBool(
				indexGeZeroForm,
				this.getQfpaGen().mkGeBool(indexVars2.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(0))
			);
		}
		
		BoolExpr startEdgeIndexFlowForm = this.getQfpaGen().mkFalse();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			if(e.getFrom().getIndex() == guessIndex) {
				startEdgeIndexFlowForm = this.getQfpaGen().mkOrBool(
					startEdgeIndexFlowForm,
					this.getQfpaGen().mkAndBool(
						this.getQfpaGen().mkGtBool(edgeFlowVarsThird.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third" + skewAppendStr), this.getQfpaGen().mkConstantInt(0)),
						this.getQfpaGen().mkEqBool(indexVars2.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(1))
					)
				);
			}
		}
		
		BoolExpr indexImpliesFlowForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			indexImpliesFlowForm = this.getQfpaGen().mkAndBool(
				indexImpliesFlowForm,
					this.getQfpaGen().mkImplies(
					this.getQfpaGen().mkGtBool(indexVars2.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(0)),
					this.getQfpaGen().mkGtBool(edgeFlowVarsThird.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third" + skewAppendStr), this.getQfpaGen().mkConstantInt(0))
				)
			);
		}
		
		BoolExpr endIndexForm = this.getQfpaGen().mkFalse();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			if(e.getTo().getIndex() == guessIndex) {
				BoolExpr indexMaxForm = this.getQfpaGen().mkTrue();
				for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
					indexMaxForm = this.getQfpaGen().mkAndBool(
						indexMaxForm,
						this.getQfpaGen().mkLeBool(indexVars2.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_(" + appendNum + ")"),	 
					                               indexVars2.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"))
					);
				}
				endIndexForm = this.getQfpaGen().mkOrBool(
					endIndexForm,
					indexMaxForm
				);
			}
		}
		
		BoolExpr indexNotSameForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
				if(e.getFrom().getIndex() != ep.getFrom().getIndex() ||
				   e.getTo().getIndex() != ep.getTo().getIndex()) {
					BoolExpr impliesFrom = this.getQfpaGen().mkAndBool(
						this.getQfpaGen().mkGtBool(indexVars2.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(0)),
						this.getQfpaGen().mkGtBool(indexVars2.get("idx_" + ep.getFrom().getIndex() + "_" +  ep.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(0))
					);
					BoolExpr impliesTo = this.getQfpaGen().mkNotEqual(
						indexVars2.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"),
						indexVars2.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_(" + appendNum + ")")
					);
					indexNotSameForm = this.getQfpaGen().mkAndBool(
						indexNotSameForm,
						this.getQfpaGen().mkImplies(impliesFrom, impliesTo)
					);
				}					
			}
		}
		
		BoolExpr linkedEdgeIndexOrderForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			BoolExpr impliesFrom = this.getQfpaGen().mkGtBool(
				indexVars2.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"),
				this.getQfpaGen().mkConstantInt(1)
			);
			
			BoolExpr impliesTo = this.getQfpaGen().mkFalse();
			for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
				if(ep.getTo().getIndex() == e.getFrom().getIndex()) {
					impliesTo = this.getQfpaGen().mkOrBool(
						impliesTo,
						this.getQfpaGen().mkEqBool(
							this.getQfpaGen().mkAddInt(indexVars2.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(1)), 
							indexVars2.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")")
						)
					);
				}
			}
			linkedEdgeIndexOrderForm = this.getQfpaGen().mkAndBool(
				linkedEdgeIndexOrderForm,
				this.getQfpaGen().mkImplies(impliesFrom, impliesTo)
			);
		}
		
		BoolExpr noLoopLinkedEdgeIndexOrderForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
				if(e.getTo().getIndex() == ep.getFrom().getIndex() && e.getTo().getIndex() != guessIndex) {
					BoolExpr impliesFrom = this.getQfpaGen().mkAndBool(
						this.getQfpaGen().mkGtBool(indexVars2.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(0)),
						this.getQfpaGen().mkGtBool(indexVars2.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(0))
					);
					
					BoolExpr impliesTo = this.getQfpaGen().mkEqBool(
						this.getQfpaGen().mkAddInt(indexVars2.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(1)),
						indexVars2.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_(" + appendNum + ")")
					);
					noLoopLinkedEdgeIndexOrderForm = this.getQfpaGen().mkAndBool(
						noLoopLinkedEdgeIndexOrderForm,
						this.getQfpaGen().mkImplies(impliesFrom, impliesTo)
					);
				}
			}
		}
		
		BoolExpr startEdgeSumAndDropForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			if(e.getFrom().getIndex() == guessIndex) {
				BoolExpr impliesFrom = this.getQfpaGen().mkEqBool(
					indexVars2.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"),
					this.getQfpaGen().mkConstantInt(1)
				);
				int startDrop = (e.getWeight() >= 0)? 0 : e.getWeight();
				BoolExpr impliesTo = this.getQfpaGen().mkAndBool(
					this.getQfpaGen().mkEqBool(sumVars2.get("sum_" + e.getTo().getIndex() + "_(" + sumDropNum + ")"), this.getQfpaGen().mkConstantInt(e.getWeight())),
					this.getQfpaGen().mkEqBool(dropVars2.get("drop_" + e.getTo().getIndex() + "_(" + sumDropNum + ")"), this.getQfpaGen().mkConstantInt(startDrop))
				);
				startEdgeSumAndDropForm = this.getQfpaGen().mkAndBool(
					startEdgeSumAndDropForm,
					this.getQfpaGen().mkImplies(impliesFrom, impliesTo)
				);
			}
		}
		
		BoolExpr otherEdgeSumAndDropForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			if(e.getFrom().getIndex() != e.getTo().getIndex()) {
				BoolExpr impliesFrom = this.getQfpaGen().mkGtBool(indexVars2.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(1));
				BoolExpr impliesTo = this.getQfpaGen().mkAndBool(		
					this.getQfpaGen().mkEqBool(
						this.getQfpaGen().mkAddInt(sumVars2.get("sum_" + e.getFrom().getIndex() + "_(" + sumDropNum + ")"), this.getQfpaGen().mkConstantInt(1)),
						sumVars2.get("sum_" + e.getTo().getIndex() + "_(" + sumDropNum + ")")
					),
					this.getQfpaGen().mkOrBool(
						this.getQfpaGen().mkAndBool(
							this.getQfpaGen().mkEqBool(dropVars2.get("drop_" + e.getTo().getIndex() + "_(" + sumDropNum + ")"), 
												   	   dropVars2.get("drop_" + e.getFrom().getIndex() +"_(" + sumDropNum + ")")),
							this.getQfpaGen().mkGtBool(sumVars2.get("sum_" + e.getTo().getIndex() + "_(" + sumDropNum + ")"), 
													   dropVars2.get("drop_" + e.getFrom().getIndex() + "_(" + sumDropNum + ")"))
						),
						this.getQfpaGen().mkAndBool(
							this.getQfpaGen().mkEqBool(dropVars2.get("drop_" + e.getTo().getIndex() + "_(" + sumDropNum + ")"),
													    sumVars2.get("sum_" + e.getTo().getIndex() + "_(" + sumDropNum + ")")),
							this.getQfpaGen().mkLeBool(sumVars2.get("sum_" + e.getTo().getIndex() + "_(" + sumDropNum + ")"), 
									   dropVars2.get("drop_" + e.getFrom().getIndex() + "_(" + sumDropNum + ")"))
						)
					)
				);
				otherEdgeSumAndDropForm = this.getQfpaGen().mkAndBool(
					this.getQfpaGen().mkImplies(impliesFrom, impliesTo),
					otherEdgeSumAndDropForm
				);
			}
		}
		
		BoolExpr positiveLoop = this.getQfpaGen().mkGtBool(
			sumVars2.get("sum_" + guessIndex + "_(" + sumDropNum + ")"),
			this.getQfpaGen().mkConstantInt(0)
		);
		
		BoolExpr result = this.getQfpaGen().mkAndBool(
				indexGeZeroForm,
				startEdgeIndexFlowForm,
				indexImpliesFlowForm,
				endIndexForm,
				indexNotSameForm,
				linkedEdgeIndexOrderForm,
				noLoopLinkedEdgeIndexOrderForm,
				startEdgeSumAndDropForm,
				otherEdgeSumAndDropForm,
				positiveLoop
		);
		return result;
	}
	
	public BoolExpr genSPANotBoundedForm(int guessIndex, int endIndex,
										 HashMap<String, IntExpr> edgeFlowVarsThird, HashMap<String, IntExpr> indexVars3, boolean isSkew) {
		//TODO: DEBUG
		/*-----------q1 == q---------*/
		int appendNum = isSkew ? 6 : 3;
		String skewAppendStr = isSkew ? "_skew" : "";
		BoolExpr startAndGuessAreSameForm = this.getQfpaGen().mkTrue();
		BoolExpr indicesAreZeroForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			indicesAreZeroForm = this.getQfpaGen().mkAndBool(
				indicesAreZeroForm,
				this.getQfpaGen().mkEqBool(indexVars3.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(0))
			);
		}
		startAndGuessAreSameForm = this.getQfpaGen().mkAndBool(
			indicesAreZeroForm,
			this.getQfpaGen().mkEqBool(this.getQfpaGen().mkConstantInt(guessIndex), this.getQfpaGen().mkConstantInt(guessIndex))
			);
		/*---------------q1 ne q------------*/
		
		BoolExpr startAndGuessAreNotSameForm = this.getQfpaGen().mkTrue();
		startAndGuessAreNotSameForm = this.getQfpaGen().mkAndBool(
			this.getQfpaGen().mkNotEqual(this.getQfpaGen().mkConstantInt(guessIndex), this.getQfpaGen().mkConstantInt(guessIndex))
		);
		BoolExpr indicesGreaterThanZeroForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			indicesGreaterThanZeroForm = this.getQfpaGen().mkAndBool(
				indicesGreaterThanZeroForm,
				this.getQfpaGen().mkGeBool(
					indexVars3.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"),
					this.getQfpaGen().mkConstantInt(0)
				)
			);
		}
		//check here index or flow
		BoolExpr selfLoopZeroIndexForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			if(e.getFrom().getIndex() == e.getTo().getIndex()) {
				selfLoopZeroIndexForm = this.getQfpaGen().mkAndBool(
					selfLoopZeroIndexForm,
					this.getQfpaGen().mkEqBool(
						indexVars3.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"),
						this.getQfpaGen().mkConstantInt(0)
					)
				);
			}
		}
		
		BoolExpr startIndexFlowForm = this.getQfpaGen().mkFalse();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			if(e.getFrom().getIndex() != e.getTo().getIndex() && e.getFrom().getIndex() == guessIndex) {
				startIndexFlowForm = this.getQfpaGen().mkOrBool(
					startIndexFlowForm,
					this.getQfpaGen().mkAndBool(
						this.getQfpaGen().mkGtBool(edgeFlowVarsThird.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third" + skewAppendStr), this.getQfpaGen().mkConstantInt(0)),
						this.getQfpaGen().mkEqBool(indexVars3.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(1))
					)
				);
			}
		}
		
		BoolExpr indexImpliesFlowForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			indexImpliesFlowForm = this.getQfpaGen().mkAndBool(
				indexImpliesFlowForm,
				this.getQfpaGen().mkImplies(
					this.getQfpaGen().mkGtBool(indexVars3.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(0)),
					this.getQfpaGen().mkEqBool(edgeFlowVarsThird.get("y_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_third" + skewAppendStr), this.getQfpaGen().mkConstantInt(0))
				)
			);
		}
	
		BoolExpr endIndexFlowForm = this.getQfpaGen().mkFalse();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			if(e.getTo().getIndex() == guessIndex) {
				BoolExpr indexSmallerForm = this.getQfpaGen().mkTrue();
				for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
					indexSmallerForm = this.getQfpaGen().mkAndBool(
						indexSmallerForm,
						this.getQfpaGen().mkLeBool(
							indexVars3.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_(" + appendNum + ")"),
							indexVars3.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")")
						)
					);
				}
				endIndexFlowForm = this.getQfpaGen().mkOrBool(
					endIndexFlowForm,
					indexSmallerForm
				);
			}
		}
		
		BoolExpr differentIndexForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
				if(e.getFrom().getIndex() != ep.getFrom().getIndex() || 
				   e.getTo().getIndex() != ep.getTo().getIndex()) {
					BoolExpr impliesFrom = this.getQfpaGen().mkAndBool(
						this.getQfpaGen().mkGtBool(indexVars3.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), 
							                       this.getQfpaGen().mkConstantInt(0)),
						this.getQfpaGen().mkGtBool(indexVars3.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_(" + appendNum + ")"), 
							                       this.getQfpaGen().mkConstantInt(0))
					);
					BoolExpr impliesTo = this.getQfpaGen().mkNotEqual(
						indexVars3.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"),
						indexVars3.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_(" + appendNum + ")")
					);
					differentIndexForm = this.getQfpaGen().mkAndBool(
						differentIndexForm,
						this.getQfpaGen().mkImplies(impliesFrom, impliesTo)
					);
				}
			}
		}
		
		BoolExpr indexGTZeroImpliesSmallerEdgeIndex = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			BoolExpr impliesFrom = this.getQfpaGen().mkGtBool(
				indexVars3.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"),
				this.getQfpaGen().mkConstantInt(1)
			);
			
			BoolExpr impliesTo = this.getQfpaGen().mkFalse();
			for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
				if(ep.getTo().getIndex() == e.getFrom().getIndex()) {
					impliesTo = this.getQfpaGen().mkOrBool(
						impliesTo,
						this.getQfpaGen().mkEqBool(
							this.getQfpaGen().mkAddInt(indexVars3.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(1)),
							indexVars3.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")")
						)
					);
				}
			}
			indexGTZeroImpliesSmallerEdgeIndex = this.getQfpaGen().mkAndBool(
				indexGTZeroImpliesSmallerEdgeIndex,
				this.getQfpaGen().mkImplies(impliesFrom, impliesTo)
			);
		}
		
		BoolExpr linkedEdgeIndexOrderForm = this.getQfpaGen().mkTrue();
		for(DGEdge e : this.getDgraph(isSkew).getEdges()) {
			for(DGEdge ep : this.getDgraph(isSkew).getEdges()) {
				if(ep.getFrom().getIndex() == e.getTo().getIndex()) {
					BoolExpr impliesFrom = this.getQfpaGen().mkAndBool(
						this.getQfpaGen().mkGtBool(indexVars3.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(0)),
						this.getQfpaGen().mkGtBool(indexVars3.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(0))
					);
					BoolExpr impliesTo = this.getQfpaGen().mkEqBool(
						this.getQfpaGen().mkAddInt(indexVars3.get("idx_" + e.getFrom().getIndex() + "_" + e.getTo().getIndex() + "_(" + appendNum + ")"), this.getQfpaGen().mkConstantInt(1)),
						indexVars3.get("idx_" + ep.getFrom().getIndex() + "_" + ep.getTo().getIndex() + "_(" + appendNum + ")")
					);
					linkedEdgeIndexOrderForm = this.getQfpaGen().mkAndBool(
						linkedEdgeIndexOrderForm,
						this.getQfpaGen().mkImplies(impliesFrom, impliesTo)
					);
				}
			}
		}
		
		BoolExpr sameResult = startAndGuessAreSameForm;
		BoolExpr result = this.getQfpaGen().mkOrBool(
			sameResult,
			this.getQfpaGen().mkAndBool(
				startAndGuessAreNotSameForm,
				indicesGreaterThanZeroForm,
				selfLoopZeroIndexForm,
				startIndexFlowForm,
				indexImpliesFlowForm,
				endIndexFlowForm,
				differentIndexForm,
				indexGTZeroImpliesSmallerEdgeIndex,
				linkedEdgeIndexOrderForm
			)
		);
		return result;
	}
	
	public BoolExpr genDROForm(IntExpr sVar, IntExpr sumGuess, IntExpr dropGuess1, IntExpr dropGuess2) {
		//TODO: DEBUG
		BoolExpr startCounterValueNotLtZeroForm = this.getQfpaGen().mkGeBool(
			this.getQfpaGen().mkAddInt(sVar, dropGuess1),
			this.getQfpaGen().mkConstantInt(0)
		);
		BoolExpr loopCounterValueNotLtZeroForm = this.getQfpaGen().mkGeBool(
			this.getQfpaGen().mkAddInt(
				this.getQfpaGen().mkAddInt(sVar, sumGuess),
				dropGuess2
			),
			this.getQfpaGen().mkConstantInt(0)
		);
		
		BoolExpr result = this.getQfpaGen().mkAndBool(startCounterValueNotLtZeroForm, loopCounterValueNotLtZeroForm);
		return result;
	}
	
	// getters and setters
	public OCAGen getOca() {
		return oca;
	}
	public void setOca(OCAGen oca) {
		this.oca = oca;
	}

	public int getVerticeNum() {
		return verticeNum;
	}

	public void setVerticeNum(int verticeNum) {
		this.verticeNum = verticeNum;
	}

	public int getMaxAbsVal() {
		return maxAbsVal;
	}

	public void setMaxAbsVal(int maxAbsVal) {
		this.maxAbsVal = maxAbsVal;
	}
	
	public DGraph getDgraph(Boolean isSkew) {
		return isSkew ? this.dgraph.getSkewTranspose() : this.dgraph ;
	}

	public DGraph getDgraph() {
		return this.dgraph;
	}
	
	public void setDgraph(DGraph dgraph) {
		this.dgraph = dgraph;
	}

	public QFPAGenerator getQfpaGen() {
		return qfpaGen;
	}

	public void setQfpaGen(QFPAGenerator qfpaGen) {
		this.qfpaGen = qfpaGen;
	}
}
