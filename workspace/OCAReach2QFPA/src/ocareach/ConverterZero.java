package ocareach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import automata.State;
import automata.counter.OCA;
import graph.directed.DGraph;
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
			IntExpr sVar = this.converter.getQfpaGen().mkVariableInt("xs");
			IntExpr tVar = this.converter.getQfpaGen().mkVariableInt("xt");
			return this.converter.convert(this.oca.getInitState(), this.oca.getTargetState(), sVar, tVar);
			
		}
	}
	
	public String convertZero(State startState, State endState) {
		//System.out.println("ConvertZero");
		System.out.println("--------------------Original OCA--------------------");
		this.oca.print();
		ZeroEdgeDGraph z = new ZeroEdgeDGraph(this.getOriginOCA().toDGraph());
		
		IntExpr sVar = this.getConverter().getQfpaGen().mkVariableInt("xs");
		IntExpr tVar = this.getConverter().getQfpaGen().mkVariableInt("xt");
		System.out.print("DIRECT");
		BoolExpr directForm = this.getConverter().convertToForm(this.getConverter().getOca().getInitState(),
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
					this.getConverter().convertToForm(this.getConverter().getOca().getState(startState.getIndex()),
													  this.getConverter().getOca().getState(v.getFrom()),
													  sVar, 
													  this.getConverter().getQfpaGen().mkConstantInt(0)),
					this.getConverter().convertToForm(this.getConverter().getOca().getState(v.getTo()), 
													  this.getConverter().getOca().getState(endState.getIndex()), 
													  this.getConverter().getQfpaGen().mkConstantInt(0), 
													  tVar)
				);
				System.out.println("HERE: ");
				
				System.out.println(midForm.toString());
				System.out.println();
				System.out.println("test");
				System.out.println(this.getConverter().convertToForm(this.getConverter().getOca().getState(v.getTo()), 
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
		
		for(ZTVertex v : z.getVertices()) {
			if(v.getFrom() >= 0 && v.getTo() >= 0) {
				for(ZTVertex w : z.getVertices()) {
					if(w.getFrom() >= 0 && w.getTo() >= 0 && w.getIndex() != v.getIndex()) {
						BoolExpr temp = this.getConverter().getQfpaGen().mkImplies(
							this.getConverter().getQfpaGen().mkAndBool(
								this.getConverter().getQfpaGen().mkRequireNonNeg(varsMap.get("the_" + v.getIndex())),
								this.getConverter().getQfpaGen().mkRequireNonNeg(varsMap.get("the_" + w.getIndex()))
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
							this.getConverter().convertToForm(this.getConverter().getOca().getState(v.getTo()),
								                        	  this.getConverter().getOca().getState(w.getFrom()),
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
						BoolExpr preForm = this.getConverter().convertToForm(this.getConverter().getOca().getInitState(), 
																			 this.getConverter().getOca().getState(v.getFrom()),
																			 sVar,
																			 this.getConverter().getQfpaGen().mkConstantInt(0));
						BoolExpr sufForm = this.getConverter().convertToForm(this.getConverter().getOca().getState(w.getTo()),
																			 this.getConverter().getOca().getTargetState(),
																			 this.getConverter().getQfpaGen().mkConstantInt(0),
																			 tVar);
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
		System.out.println("TEMPORARY RESULT----------------------");
		System.out.println("DIRECT: " + directForm.toString());
		System.out.println("ONESTEP: " + oneStepForm.toString());
		System.out.println("LONG: " + longForm.toString());
		BoolExpr resultExpr = this.getConverter().getQfpaGen().mkOrBool(
			oneStepForm,
			directForm,
			longForm
		);
		
		/*BoolExpr dfsForm = this.getConverter().getQfpaGen().mkEqBool(vertexVars[startIndex], this.getConverter().getQfpaGen().mkConstantInt(1));
		dfsForm = this.getConverter().getQfpaGen().mkAndBool(
				dfsForm, 
				this.getConverter().getQfpaGen().mkGtBool(vertexVars[endIndex], this.getConverter().getQfpaGen().mkConstantInt(0)));
		BoolExpr startUniqueForm = this.getConverter().getQfpaGen().mkTrue();
		for(int i = 0; i < z.getVerticesNum(); i ++) {
			if(!(i == startIndex)) {
				startUniqueForm = this.getConverter().getQfpaGen().mkAndBool(
					startUniqueForm, 
					this.getConverter().getQfpaGen().mkNotEqual(vertexVars[i], this.getConverter().getQfpaGen().mkConstantInt(1))
				);
			}
		}
		dfsForm = this.getConverter().getQfpaGen().mkAndBool(dfsForm, startUniqueForm);
		BoolExpr reachForm = this.getConverter().getQfpaGen().mkTrue();
		///System.out.println("startIndex: " + startIndex + " endIndex: " + endIndex);
		HashMap<String, IntExpr> varsMap = new HashMap<String, IntExpr>();
		for(int i = 0; i < z.getVerticesNum(); i++) {
			if(i != startIndex) {
				BoolExpr onThePath = this.getConverter().getQfpaGen().mkGtBool(vertexVars[i], this.getConverter().getQfpaGen().mkConstantInt(0));
				BoolExpr hasLessNumForm = this.getConverter().getQfpaGen().mkFalse();
				for(int j = 0; j < z.getVerticesNum(); j++) {
					if(j != i && z.containsEdge(z.getVertex(j).getIndex(), z.getVertex(i).getIndex())) {
						//System.out.println("ContainEdge: " + z.getVertex(j).getIndex() + " " + z.getVertex(i).getIndex());
						BoolExpr partForm = null;
						if(j == startIndex) {
							if(i == endIndex) {
								//System.out.println("HERE 1");
								partForm = this.getConverter().convertToForm(this.getConverter().getOca().getState(z.getVertex(j).getTo()),	
																		     this.getConverter().getOca().getState(z.getVertex(i).getFrom()),
																		     sVar, 
																		     tVar);
							} else {
								//System.out.println("HERE 2 " + z.getVertex(j).getTo() + " " + z.getVertex(i).getFrom());
								IntExpr newVar = null;
								if(varsMap.containsKey("zv_t_" + z.getVertex(i).getFrom())) {
									newVar = varsMap.get("zv_t_" + z.getVertex(i).getFrom());
								} else {
									 newVar = this.getConverter().getQfpaGen().mkVariableInt("zv_t_" + z.getVertex(i).getFrom());
									 varsMap.put("zv_t_" + z.getVertex(i).getFrom(), newVar);
								}
								partForm = this.getConverter().convertToForm(this.getConverter().getOca().getState(z.getVertex(j).getTo()),
																		     this.getConverter().getOca().getState(z.getVertex(i).getFrom()),
																		     sVar, 
																		     newVar);
								partForm = this.getConverter().getQfpaGen().mkAndBool(
									partForm,
									this.getConverter().getQfpaGen().mkEqBool(newVar, this.getConverter().getQfpaGen().mkConstantInt(0))
								);
							}
						} else if (j == endIndex){
							//System.out.println("HERE 3 " + z.getVertex(j).getTo() + " " + z.getVertex(i).getFrom());
							IntExpr newVar1 = null;
							IntExpr newVar2 = null;
							if(varsMap.containsKey("zv_s_" + z.getVertex(j).getFrom())) {
								newVar1 = varsMap.get("zv_s_" + z.getVertex(j).getFrom());
							} else {
								newVar1 = this.getConverter().getQfpaGen().mkVariableInt("zv_s_" + z.getVertex(j).getFrom());
								varsMap.put("zv_s_" + z.getVertex(j).getFrom(), newVar1);
							}
							if(varsMap.containsKey("zv_t_" + z.getVertex(i).getFrom())){
								newVar2 = varsMap.get("zv_t_" + z.getVertex(i).getFrom());
							} else{
								newVar2 = this.getConverter().getQfpaGen().mkVariableInt("zv_t_" + z.getVertex(i).getFrom());
								varsMap.put("zv_t_" + z.getVertex(i).getFrom(), newVar2);
							}
							partForm = this.getConverter().convertToForm(this.getConverter().getOca().getState(z.getVertex(j).getFrom()), 
	 									 								 this.getConverter().getOca().getState(z.getVertex(i).getFrom()), 
	 									 								 newVar1, 
	 									 								 newVar2);
							partForm = this.getConverter().getQfpaGen().mkAndBool(
									partForm,
									this.getConverter().getQfpaGen().mkEqBool(newVar1, this.getConverter().getQfpaGen().mkConstantInt(0)),
									this.getConverter().getQfpaGen().mkEqBool(newVar2, this.getConverter().getQfpaGen().mkConstantInt(0))
							);
						} else {
							if(i == endIndex) {
								//System.out.println("HERE 4 " + z.getVertex(j).getTo() + " " + z.getVertex(i).getFrom());
								
								IntExpr newVar = null;
								if(varsMap.containsKey("zv_s_" + z.getVertex(j).getTo())) {
									newVar = varsMap.get("zv_s_" + z.getVertex(j).getTo());
								} else {
									newVar = this.getConverter().getQfpaGen().mkVariableInt("zv_s_" + z.getVertex(j).getTo());
									varsMap.put("zv_s_" + z.getVertex(j).getTo(), newVar);
								}
								partForm = this.getConverter().convertToForm(this.getConverter().getOca().getState(z.getVertex(j).getTo()), 
																			 this.getConverter().getOca().getState(z.getVertex(i).getFrom()), 
																			 newVar, 
																			 tVar);
								partForm = this.getConverter().getQfpaGen().mkAndBool(
										partForm,
										this.getConverter().getQfpaGen().mkEqBool(newVar, this.getConverter().getQfpaGen().mkConstantInt(0))
								);
								
							} else {
								//System.out.println("HERE 5 " + z.getVertex(j).getTo() + " " + z.getVertex(i).getFrom());
								IntExpr newVar1 = null;
								IntExpr newVar2 = null;
								if(varsMap.containsKey("zv_s_" + z.getVertex(j).getTo())) {
									newVar1 = varsMap.get("zv_s_" + z.getVertex(j).getTo());
								} else {
									newVar1 = this.getConverter().getQfpaGen().mkVariableInt("zv_s_" + z.getVertex(j).getTo());
									varsMap.put("zv_s_" + z.getVertex(j).getTo(), newVar1);
								}
								if(varsMap.containsKey("zv_t_" + z.getVertex(i).getFrom())){
									newVar2 = varsMap.get("zv_t_" + z.getVertex(i).getFrom());
								} else{
									newVar2 = this.getConverter().getQfpaGen().mkVariableInt("zv_t_" + z.getVertex(i).getFrom());
									varsMap.put("zv_t_" + z.getVertex(i).getFrom(), newVar2);
								}
								partForm = this.getConverter().convertToForm(this.getConverter().getOca().getState(z.getVertex(j).getTo()), 
	 									 									 this.getConverter().getOca().getState(z.getVertex(i).getFrom()), 
	 									 									 newVar1, 
	 									 									 newVar2);
								partForm = this.getConverter().getQfpaGen().mkAndBool(
										partForm,
										this.getConverter().getQfpaGen().mkEqBool(newVar1, this.getConverter().getQfpaGen().mkConstantInt(0)),
										this.getConverter().getQfpaGen().mkEqBool(newVar2, this.getConverter().getQfpaGen().mkConstantInt(0))
								);
							}
						}
						//System.out.println("PART: " + partForm.toString());
						BoolExpr tempForm = this.getConverter().getQfpaGen().mkAndBool(
							this.getConverter().getQfpaGen().mkGtBool(vertexVars[j], this.getConverter().getQfpaGen().mkConstantInt(0)),
							this.getConverter().getQfpaGen().mkGtBool(vertexVars[i], vertexVars[j]),
							partForm
						);
						hasLessNumForm = this.getConverter().getQfpaGen().mkOrBool(
							hasLessNumForm,
							tempForm
						);
					}
				}
				reachForm = this.getConverter().getQfpaGen().mkAndBool(
					reachForm,
					this.getConverter().getQfpaGen().mkImplies(onThePath, hasLessNumForm)
				);
			}
		}
		reachForm = this.getConverter().getQfpaGen().mkAndBool(
			reachForm,
			dfsForm
		);
		
		System.out.println("varsMapSize: " + varsMap.size());
		varsMap.values().toArray(a);
		for(IntExpr v : a) {
			System.out.println(v.toString());
		}
		reachForm = this.getConverter().getQfpaGen().mkAndBool(
			reachForm,
			this.getConverter().getQfpaGen().mkRequireNonNeg(vertexVars),
			this.getConverter().getQfpaGen().mkRequireNonNeg(a),
			this.getConverter().getQfpaGen().mkRequireNonNeg(sVar),
			this.getConverter().getQfpaGen().mkRequireNonNeg(tVar)
		);*/
		
		
		
		// simplified
		//resultStr = longForm.simplify().toString();
		
		String resultStr = null;
		resultStr = resultExpr.toString();
		String result = null;
		String solveResult = null;
		// ----------------------EQUIV DEBUG-----------------------
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
			//this.getConverter().getQfpaGen().mkRequireNonNeg(iVar),
			//this.getConverter().getQfpaGen().mkRequireNonNeg(jVar),
			this.getConverter().getQfpaGen().mkRequireNonNeg(sVar),
			this.getConverter().getQfpaGen().mkRequireNonNeg(tVar),
			this.getConverter().getQfpaGen().mkEqBool(tVar, con0),
			this.getConverter().getQfpaGen().mkEqBool(sVar, con0)
		);
		IntExpr[] exists = new IntExpr[2];
		exists[0] = iVar;
		exists[1] = jVar;
		//equiv = (BoolExpr) this.getConverter().getQfpaGen().mkExistsQuantifier(exists, equiv);
		
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
			pathResult = this.converter.convertToForm(this.converter.oca.getState(p.getInitVertex().getTo()),
					this.converter.oca.getState(p.getLastVertex().getFrom()),
													  sVar, tVar);
		} else {
			for(int i = 0; i < p.getPath().size()-1; i++) {
				pathResult = this.converter.getQfpaGen().mkAndBool(
							pathResult,
							this.converter.convertToForm(this.converter.oca.getState(p.getVertex(i).getTo()), 
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
