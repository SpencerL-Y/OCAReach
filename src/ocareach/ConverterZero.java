package ocareach;

import java.util.HashMap;
import java.util.List;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import automata.State;
import automata.counter.OCA;
import graph.directed.zerograph.ZTPath;
import graph.directed.zerograph.ZTVertex;
import graph.directed.zerograph.ZeroEdgeDGraph;

public class ConverterZero {
	// add ConverterOpt as a property
	private OCA originOCA;
	private OCA oca;
	private ConverterOpt converter;
	public ConverterZero(OCA ocaOrigin) {
		this.originOCA = ocaOrigin;
		this.oca = this.getOriginOCA().removeZeroTransitionOCA();
		this.converter = new ConverterOpt(this.oca);
	}
	
	public String convert() {
		if(this.originOCA.containsZeroTransition()) {
			return this.convertZero(this.oca.getInitState(), this.oca.getTargetState());
		} else {
			return this.converter.convert();
		}
	}
	
	public String convertGen() {
		return null;
	}
	
	public String convertZero(State startState, State endState) {
		//System.out.println("ConvertZero");
		System.out.println("--------------------Original OCA--------------------");
		this.oca.print();
		ZeroEdgeDGraph z = new ZeroEdgeDGraph(this.getOriginOCA().toDGraph());
		
		IntExpr sVar = this.getConverter().getQfpaGen().mkVariableInt("xs");
		IntExpr tVar = this.getConverter().getQfpaGen().mkVariableInt("xt");
		System.out.print("DIRECT");
		BoolExpr directForm = this.getConverter().convertExpr(this.getConverter().getOca().getInitState(),
																this.getConverter().getOca().getTargetState(),
																sVar,
																tVar);
		
		System.out.println("ONESTEP");
		BoolExpr oneStepForm = this.getConverter().getQfpaGen().mkFalse();
		for(ZTVertex v : z.getVertices()) {
			if(v.getFrom() >= 0 && v.getTo() >= 0) {
				System.out.println("vFrom: " + this.getConverter().getOca().getState(v.getFrom()).getIndex()
						   + " " + "vTo: "   + this.getConverter().getOca().getState(v.getTo()).getIndex());
				System.out.println("startState: " + this.getConverter().getOca().getInitState().getIndex());
				System.out.println("targetState: " + this.getConverter().getOca().getTargetState().getIndex());
				//make sure the vertex represents a zero edge rather than the starting vertex and ending vertex
				
				BoolExpr midForm = this.getConverter().getQfpaGen().mkAndBool(
					this.getConverter().convertExpr(this.getConverter().getOca().getInitState(),
													  this.getConverter().getOca().getState(v.getFrom()),
													  sVar, 
													  this.getConverter().getQfpaGen().mkConstantInt(0)),
					this.getConverter().convertExpr(this.getConverter().getOca().getState(v.getTo()), 
													  this.getConverter().getOca().getTargetState(), 
													  this.getConverter().getQfpaGen().mkConstantInt(0), 
													  tVar)
				);
				System.out.println("HERE: ");
				
				System.out.println(midForm.toString());
				System.out.println();
				System.out.println("test");
				System.out.println(this.getConverter().convertExpr(this.getConverter().getOca().getState(v.getTo()), 
						  this.getConverter().getOca().getTargetState(), 
						  this.getConverter().getQfpaGen().mkConstantInt(0), 
						  tVar).toString());
				oneStepForm = this.getConverter().getQfpaGen().mkOrBool(
					oneStepForm,
					midForm
				);
			}
		}
		
		HashMap<String, IntExpr> varsMap = new HashMap<String, IntExpr>();
		for(ZTVertex v : z.getVertices()) {
			if(v.getFrom() >= 0 && v.getTo() >= 0) {
				if(!varsMap.containsKey("the_" + v.getIndex())) {
					varsMap.put("the_" + v.getIndex(), this.getConverter().getQfpaGen().mkVariableInt("the_" + v.getIndex()));
				}
			}
		}
		System.out.println("LONGFORM");
		BoolExpr longForm = this.getConverter().getQfpaGen().mkFalse();
		
		IntExpr[] varsMapArray = new IntExpr[varsMap.size()];
		varsMap.values().toArray(varsMapArray);
		
		BoolExpr varsMapNonNegForm = this.getConverter().getQfpaGen().mkRequireNonNeg(varsMapArray);
		
		BoolExpr thetaNotSameForm = this.getConverter().getQfpaGen().mkTrue();
		
		
		// theta_v > 0 /\ theta_w > 0 ==> theta_v \ne theta_w
		
		for(ZTVertex v : z.getVertices()) {
			if(v.getFrom() >= 0 && v.getTo() >= 0) {
				for(ZTVertex w : z.getVertices()) {
					if(w.getFrom() >= 0 && w.getTo() >= 0 && w.getIndex() != v.getIndex()) {
						BoolExpr temp = this.getConverter().getQfpaGen().mkImplies(
							this.getConverter().getQfpaGen().mkAndBool(
								this.getConverter().getQfpaGen().mkGtBool((varsMap.get("the_" + v.getIndex())), this.getConverter().getQfpaGen().mkConstantInt(0)),
								this.getConverter().getQfpaGen().mkGtBool((varsMap.get("the_" + w.getIndex())), this.getConverter().getQfpaGen().mkConstantInt(0))
							),
							this.getConverter().getQfpaGen().mkNotEqual(
								varsMap.get("the_" + v.getIndex()),
								varsMap.get("the_" + w.getIndex())
							)
						);
						thetaNotSameForm = this.getConverter().getQfpaGen().mkAndBool(thetaNotSameForm, temp);
					}
				}
			}
		}
		
		
		BoolExpr dfsForm = this.getConverter().getQfpaGen().mkTrue();
		for(ZTVertex v : z.getVertices()) {
			if(v.getFrom() >= 0 && v.getTo() >= 0) {
				BoolExpr impliesTo = this.getConverter().getQfpaGen().mkFalse();
				for(ZTVertex w : z.getVertices()) {
					if(w.getIndex() != v.getIndex() && w.getFrom() >= 0 && w.getTo() >= 0) {
						BoolExpr impliesToTemp = this.getConverter().getQfpaGen().mkAndBool(
							this.getConverter().convertExpr(this.getConverter().getOca().getState(w.getTo()),
								                        	  this.getConverter().getOca().getState(v.getFrom()),
								                        	  this.getConverter().getQfpaGen().mkConstantInt(0),
								                        	  this.getConverter().getQfpaGen().mkConstantInt(0)),
							this.getConverter().getQfpaGen().mkGtBool(
								varsMap.get("the_" + w.getIndex()), 
								this.getConverter().getQfpaGen().mkConstantInt(0)
							),
							this.getConverter().getQfpaGen().mkEqBool(
								varsMap.get("the_" + v.getIndex()), 
								this.getConverter().getQfpaGen().mkAddInt(varsMap.get("the_" + w.getIndex()), this.getConverter().getQfpaGen().mkConstantInt(1)))
						);
						
						impliesTo = this.getConverter().getQfpaGen().mkOrBool(impliesTo, impliesToTemp);
					}
					
				}
				BoolExpr temp = this.getConverter().getQfpaGen().mkImplies(
					this.getConverter().getQfpaGen().mkGtBool(varsMap.get("the_" + v.getIndex()), this.getConverter().getQfpaGen().mkConstantInt(1)), 
					impliesTo
				); 
				dfsForm = this.getConverter().getQfpaGen().mkAndBool(dfsForm, temp);
			}
		}
		for(ZTVertex v : z.getVertices()) {
			if(v.getFrom() >= 0 && v.getTo() >= 0) {
				for(ZTVertex w : z.getVertices()) {
					if(v.getIndex() != w.getIndex() && w.getFrom() >= 0 && w.getTo() >= 0) {
						System.out.println("EDGES: ");
						v.printZeroEdge(); w.printZeroEdge();
						BoolExpr preForm = this.getConverter().convertExpr(this.getConverter().getOca().getInitState(), 
																			 this.getConverter().getOca().getState(v.getFrom()),
																			 sVar,
																			 this.getConverter().getQfpaGen().mkConstantInt(0));
						System.out.println("PREFORM------- " + preForm.toString());
						BoolExpr sufForm = this.getConverter().convertExpr(this.getConverter().getOca().getState(w.getTo()),
																			 this.getConverter().getOca().getTargetState(),
																			 this.getConverter().getQfpaGen().mkConstantInt(0),
																			 tVar);
						System.out.println("SUFFORM------- " + sufForm.toString());
						BoolExpr tauReachableForm = null;
						tauReachableForm = this.getConverter().getQfpaGen().mkAndBool(
							this.getConverter().getQfpaGen().mkEqBool(varsMap.get("the_" + v.getIndex()), this.getConverter().getQfpaGen().mkConstantInt(1)),
							this.getConverter().getQfpaGen().mkGtBool(varsMap.get("the_" + w.getIndex()), this.getConverter().getQfpaGen().mkConstantInt(1)),
							varsMapNonNegForm,
							thetaNotSameForm,
							dfsForm
						);
						
						longForm = this.getConverter().getQfpaGen().mkOrBool(
							longForm,
							this.getConverter().getQfpaGen().mkAndBool(
								preForm,
								sufForm,
								tauReachableForm
							)
						);
					}
				}
			}
		}
		longForm = (BoolExpr) this.getConverter().getQfpaGen().mkExistsQuantifier(varsMapArray, longForm);
		
		//longForm = this.getConverter().getQfpaGen().mkFalse();
		System.out.println("TEMPORARY RESULT----------------------");
		System.out.println("DIRECT: " + directForm.toString());
		System.out.println("ONESTEP: " + oneStepForm.toString());
		System.out.println("LONG: " + longForm.toString());
		BoolExpr resultExpr = this.getConverter().getQfpaGen().mkOrBool(
			oneStepForm,
			directForm,
			longForm
		);
		
		// simplified
		//resultStr = longForm.simplify().toString();
		
		String resultStr = null;
		resultStr = resultExpr.toString();
		String result = null;
		String solveResult = null;
		/*// ----------------------EQUIV DEBUG-----------------------
		resultExpr = this.equivDebug(sVar, tVar, resultExpr);
					
		result = resultExpr.toString();
		Solver solver = this.getConverter().getQfpaGen().getCtx().mkSolver();
		solver.add((BoolExpr)resultExpr);
		if(solver.check() == Status.UNSATISFIABLE) {
			solveResult = "\n UNSAT";
		} else {
			solveResult = "\nSAT \n" + solver.getModel().toString();
		}
		/*/// --------------------------------------------------------*/
		
		return (result == null) ? resultStr : result + solveResult;
	}
	
	
	@SuppressWarnings("unused")
	public BoolExpr equivDebug(IntExpr sVar, IntExpr tVar, BoolExpr tempResult) {
		//TODO: add equiv debug
		BoolExpr equiv = null;
		IntExpr con0 = this.getConverter().getQfpaGen().mkConstantInt(0);
		IntExpr iVar = this.getConverter().getQfpaGen().mkVariableInt("i");
		IntExpr jVar = this.getConverter().getQfpaGen().mkVariableInt("j");
		BoolExpr weightForm = this.getConverter().getQfpaGen().mkOrBool(
			this.getConverter().getQfpaGen().mkEqBool(
				this.getConverter().getQfpaGen().mkAddInt(
						this.getConverter().getQfpaGen().mkAddInt(
							sVar, 
							this.getConverter().getQfpaGen().mkScalarTimes(iVar, this.getConverter().getQfpaGen().mkConstantInt(-2))
						),
						this.getConverter().getQfpaGen().mkConstantInt(-1)
				)
				,
				tVar
			),
			this.getConverter().getQfpaGen().mkEqBool(
					this.getConverter().getQfpaGen().mkAddInt(
							this.getConverter().getQfpaGen().mkAddInt(
								sVar, 
								this.getConverter().getQfpaGen().mkScalarTimes(jVar, this.getConverter().getQfpaGen().mkConstantInt(-3))
							),
							this.getConverter().getQfpaGen().mkConstantInt(1)
					)
					,
					tVar
				)
		);
		equiv = this.getConverter().getQfpaGen().mkAndBool(
			this.getConverter().getQfpaGen().mkRequireNonNeg(iVar),
			//this.getConverter().getQfpaGen().mkRequireNonNeg(jVar),
			this.getConverter().getQfpaGen().mkRequireNonNeg(sVar),
			this.getConverter().getQfpaGen().mkRequireNonNeg(tVar),
			this.getConverter().getQfpaGen().mkGeBool(sVar, this.getConverter().getQfpaGen().mkConstantInt(1)),
			this.getConverter().getQfpaGen().mkLtBool(sVar, this.getConverter().getQfpaGen().mkConstantInt(3)),
			this.getConverter().getQfpaGen().mkEqBool(tVar, 
				this.getConverter().getQfpaGen().mkAddInt(
						this.getConverter().getQfpaGen().mkScalarTimes(iVar, this.getConverter().getQfpaGen().mkConstantInt(2)), 
						this.getConverter().getQfpaGen().mkConstantInt(1))
				)
		);
		
		
		IntExpr[] exists = new IntExpr[1];
		exists[0] = iVar;
		//exists[1] = jVar;
		//equiv = (BoolExpr) this.getConverter().getQfpaGen().mkExistsQuantifier(exists, equiv);
		
		equiv = (BoolExpr) this.getConverter().getQfpaGen().mkExistsQuantifier(exists, equiv);
		/*equiv = this.getConverter().getQfpaGen().mkAndBool(
			this.getConverter().getQfpaGen().mkEqBool(sVar, this.getConverter().getQfpaGen().mkConstantInt(1)),
			this.getConverter().getQfpaGen().mkEqBool(tVar, this.getConverter().getQfpaGen().mkConstantInt(1))
			
		);*/
		BoolExpr resultExpr = this.getConverter().getQfpaGen().mkAndBool(
			this.getConverter().getQfpaGen().mkImplies(equiv, tempResult),
			this.getConverter().getQfpaGen().mkImplies(tempResult, equiv)
		);
		resultExpr = this.getConverter().getQfpaGen().mkNotBool(resultExpr);
		
		return resultExpr;
	}
	
	
	public String convertZeroNaive(State startState, State endState) {
		ZeroEdgeDGraph z = new ZeroEdgeDGraph(this.getOriginOCA().toDGraph());
		List<ZTPath> ztPaths = z.dfsFindAllZTPath();
		for(ZTPath p : ztPaths) {
			p.print();
		}
		BoolExpr resultExpr = this.converter.getQfpaGen().mkFalse();
		IntExpr sVar = this.converter.getQfpaGen().mkVariableInt("xs");
		IntExpr tVar = this.converter.getQfpaGen().mkVariableInt("xt");
		for(ZTPath zp : ztPaths) {
			zp.print();
			resultExpr = this.converter.getQfpaGen().mkOrBool(
				resultExpr,
				this.genZTPathForm(zp, sVar, tVar)
			);
			
		}
		return resultExpr.toString();
	}
	
	public BoolExpr genZTPathForm(ZTPath p, IntExpr sVar, IntExpr tVar) {
		BoolExpr pathResult = this.converter.getQfpaGen().mkTrue();
		IntExpr[] midVars = new IntExpr[2*(p.getPath().size()-1)];
		midVars[0] = sVar;
		for(int i = 1; i < p.getPath().size()-1; i++) {
			midVars[2*i-1] = this.converter.getQfpaGen().mkVariableInt("zv_t_" + p.getVertex(i).getIndex());
			midVars[2*i] = this.converter.getQfpaGen().mkVariableInt("zv_s_" + p.getVertex(i).getIndex());
		}
		midVars[2*(p.getPath().size()-1) - 1] = tVar;
		IntExpr[] boundVars = new IntExpr[2*(p.getPath().size() - 2)];
		for(int i = 1; i < midVars.length-1; i++) {
			boundVars[i - 1] = midVars[i];
		}
		
		if(p.getPath().size() < 2) {
			System.out.println("ERROR: ZTPath length < 2");
			return null;
		} 
		if(p.getPath().size() == 2) {
			// if the path does not contain any zero vertex
			pathResult = this.converter.convertExpr(this.converter.oca.getState(p.getInitVertex().getTo()),
					this.converter.oca.getState(p.getLastVertex().getFrom()),
													  sVar, tVar);
		} else {
			for(int i = 0; i < p.getPath().size()-1; i++) {
				pathResult = this.converter.getQfpaGen().mkAndBool(
							pathResult,
							this.converter.convertExpr(this.converter.oca.getState(p.getVertex(i).getTo()), 
											   			 this.converter.oca.getState(p.getVertex(i+1).getFrom()),
											   			 midVars[2*i], midVars[2*i+1])
							
				);
			}
			pathResult = (BoolExpr) this.converter.getQfpaGen().mkExistsQuantifier(boundVars, pathResult);
		}
		return pathResult;
	}

	public OCA getOriginOCA() {
		return originOCA;
	}

	public void setOriginOCA(OCA originOCA) {
		this.originOCA = originOCA;
	}

	public ConverterOpt getConverter() {
		return converter;
	}

	public void setConverter(ConverterOpt converter) {
		this.converter = converter;
	}
}
