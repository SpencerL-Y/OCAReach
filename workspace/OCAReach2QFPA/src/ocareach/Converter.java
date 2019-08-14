package ocareach;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import automata.State;
import automata.counter.OCA;
import formula.generator.QFPAGenerator;
import graph.directed.DGEdge;
import graph.directed.DGFlowTuple;
import graph.directed.DGPath;
import graph.directed.DGVertex;
import graph.directed.DGraph;
import graph.directed.SDGVertex;
import graph.directed.SDGraph;
import graph.directed.abs.ASDGPath;
import graph.directed.abs.ASDGVertex;
import graph.directed.abs.ASDGraph;
import graph.directed.abs.BorderEdge;
import graph.directed.abs.LoopTag;
import table.dwt.DWTEntry;
import table.dwt.DWTuple;

public class Converter {
	// Author: Xie Li
	// input: A One-Counter Automaton and two states s, t of it
	// output: A QFPA formula \phi(x, y) which is true iff
	// there is a run from (s, x) --->* (t, y)
	
	protected OCA oca;
	protected DGraph dgraph;
	protected SDGraph sdg;
	protected ASDGraph asdg;
	protected QFPAGenerator qfpaGen;
	protected State startState;
	protected State endState;
	
	public Converter(OCA oca) {
		this.setOca(oca);
		this.dgraph = this.getOca().toDGraph();
		this.sdg = new SDGraph(this.getDgraph());
		this.qfpaGen = new QFPAGenerator();
	}
	
	// ALGORITHM
	public String convert() {
		IntExpr sVar = this.getQfpaGen().mkVariableInt("xs");
		IntExpr tVar = this.getQfpaGen().mkVariableInt("xt");
		return this.convert(this.oca.getInitState(), this.oca.getTargetState(), sVar, tVar);
	}
	
	public String convert(State startState, State endState, IntExpr sVar, IntExpr tVar) {
		assert(this.getOca().containsState(startState) && this.getOca().containsState(endState));
		// set starting and ending vertex in DG
		this.getDgraph().setStartVertexIndex(startState.getIndex());
		this.getDgraph().setEndingVertexIndex(endState.getIndex());
		// run tarjan and get SCC marks
		this.getSdg().tarjan();
		// construct abstract SDG
		this.asdg = new ASDGraph(this.getSdg());
		ASDGVertex absStart = this.getAsdg().getVertex(this.getSdg().getStartingVertex().getSccMark());
		ASDGVertex absEnd = this.getAsdg().getVertex(this.getSdg().getEndingVertex().getSccMark());
		// get all the possible abstract path
		List<ASDGPath> paths = this.getAsdg().DFSFindAbsPaths(absStart.getSccIndex(), absEnd.getSccIndex());
		List<BoolExpr> formulae = new ArrayList<BoolExpr>();
		for(ASDGPath p : paths) {
			for(ASDGVertex v : p.getPath()) {
				System.out.print(v.getSccIndex());
			}
			System.out.println();
			
			// there is no cycle in  SCCs (trivial case: every SCC is a concrete vertex)
			boolean trivial = !p.containsCycledVertex();
			// there might be type-1 certificate
			boolean type1 = true;
			// there might be type-1 . type-2/ type-2 certificate
			boolean type12 = p.containsPosTagVertex();
			// there might be type-1 . type-3 . type-2/ type-1 . type-3/ type-3 . type-2/ type-3 certificate
			boolean type132 = p.containsNegTagVertex() && p.containsPosTagVertex();
			BoolExpr trivialForm = this.getQfpaGen().mkFalse();
			BoolExpr type1Form = this.getQfpaGen().mkFalse();
			BoolExpr type12Form = this.getQfpaGen().mkFalse();
			BoolExpr type132Form = this.getQfpaGen().mkFalse();
			if(trivial) {
				trivialForm = this.genTrivialFormula(p, sVar, tVar);
			}
			if(type1 && !trivial) {
				type1Form = this.genType1Formulae(p, startState.getIndex(), endState.getIndex(), sVar, tVar, false);
			}
			if(type12) {
				type12Form = this.genType12Formulae(p, startState.getIndex(), endState.getIndex(), sVar, tVar);
			}
			if(type132) {
				type132Form = this.genType132Formulae(p, startState.getIndex(), endState.getIndex(), sVar, tVar);
			}
			BoolExpr temp = (trivial)? trivialForm : this.combineAllFormlae(type1Form, type12Form, type132Form);
			formulae.add(temp);
		}
		
		
		String result = null;
		BoolExpr resultExpr = this.getQfpaGen().mkFalse();
		for(BoolExpr formula : formulae) {
			resultExpr = this.getQfpaGen().mkOrBool(resultExpr, formula);
		}
		BoolExpr xsXtPosRequirements = this.getQfpaGen().mkAndBool(
					this.getQfpaGen().mkRequireNonNeg(sVar),
					this.getQfpaGen().mkRequireNonNeg(tVar)
		);
		resultExpr = this.getQfpaGen().mkAndBool(resultExpr, xsXtPosRequirements);	
		String solveResult = null;
		/*// ----------------------EQUIV DEBUG-----------------------
		resultExpr = this.equivDebug(sVar, tVar, resultExpr);
		
		result = resultExpr.simplify().toString();
		Solver solver = this.getQfpaGen().getCtx().mkSolver();
		solver.add((BoolExpr)resultExpr.simplify());
		if(solver.check() == Status.UNSATISFIABLE) {
			solveResult = "\n UNSAT";
		} else {
			solveResult = "\n SAT \n" + solver.getModel().toString();
		}
		// --------------------------------------------------------*/
		return (solveResult == null) ? result : result + solveResult;
	}
	
	public BoolExpr equivDebug(IntExpr sVar, IntExpr tVar, BoolExpr tempResult) {
		BoolExpr resultExpr = null;
		IntExpr iVar = this.getQfpaGen().mkVariableInt("i");
		IntExpr jVar = this.getQfpaGen().mkVariableInt("j");
		List<IntExpr> sum = new ArrayList<IntExpr>(3);
		sum.add(null);
		sum.add(null);
		sum.add(null);
		sum.set(0, this.getQfpaGen().mkScalarTimes(this.getQfpaGen().mkConstantInt(2), iVar));
		sum.set(1, this.getQfpaGen().mkScalarTimes(this.getQfpaGen().mkConstantInt(-4), jVar));
		sum.set(2, sVar);
		IntExpr[] bounds = new IntExpr[2];
		bounds[0] = iVar;
		bounds[1] = jVar;
		/*BoolExpr equiv = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(bounds,
					this.getQfpaGen().mkAndBool(
							this.getQfpaGen().mkEqBool(tVar, this.getQfpaGen().mkSubInt(this.getQfpaGen().sumUpVars(sum), this.getQfpaGen().mkConstantInt(2))),
					this.getQfpaGen().mkRequireNonNeg(iVar),
					this.getQfpaGen().mkRequireNonNeg(tVar),
					this.getQfpaGen().mkRequireNonNeg(sVar)
					//this.getQfpaGen().mkGeBool(sVar, this.getQfpaGen().mkConstantInt(1))
				));
		*/
		BoolExpr equiv = this.getQfpaGen().mkAndBool(
			this.getQfpaGen().mkGeBool(sVar, this.getQfpaGen().mkConstantInt(2)),
			this.getQfpaGen().mkGeBool(tVar, this.getQfpaGen().mkConstantInt(0)),
			this.getQfpaGen().mkRequireNonNeg(tVar),
			this.getQfpaGen().mkGeBool(this.getQfpaGen().mkSubInt(sVar, this.getQfpaGen().mkConstantInt(2)), tVar)
				//this.getQfpaGen().mkGeBool(tVar, this.getQfpaGen().mkSubInt(sVar, this.getQfpaGen().mkConstantInt(2)))
		);
		resultExpr = this.getQfpaGen().mkAndBool(this.getQfpaGen().getCtx().mkImplies(tempResult, equiv), this.getQfpaGen().getCtx().mkImplies(equiv, tempResult));
		resultExpr = this.getQfpaGen().mkNotBool(resultExpr);
		return resultExpr;
	}
	
	public BoolExpr genTrivialFormula(ASDGPath p, IntExpr sVar, IntExpr tVar) {
		// convert abstract vertices to concrete vertices
		// we need the edge information so the path is constructed like this
		DGVertex startV = this.getDgraph().getVertex(p.getVertex(0).getConcreteDGraph().getVertices().get(0).getIndex());
		DGPath cp = new DGPath(startV);
		for(int i = 1; i <= p.length(); i++) {
			DGVertex currentV = this.getDgraph().getVertex(p.getVertex(i).getConcreteDGraph().getVertices().get(0).getIndex());
			cp.concatVertex(currentV);
		}
		BoolExpr result = this.getQfpaGen()
						  .mkAndBool(this.getQfpaGen().mkEqBool(this.getQfpaGen().mkSubInt(tVar, sVar), 
								  								this.getQfpaGen().mkConstantInt(cp.getWeight())),// weight correctly summed
								  	 this.getQfpaGen().mkGeBool(this.getQfpaGen().mkAddInt(sVar, this.getQfpaGen().mkConstantInt(cp.getDrop())), 
								  			 					this.getQfpaGen().mkConstantInt(0)), // counter value does not drop below 0
								  	 this.getQfpaGen().mkGeBool(sVar, this.getQfpaGen().mkConstantInt(0)), // sVar >= 0
								  	 this.getQfpaGen().mkGeBool(tVar, this.getQfpaGen().mkConstantInt(0)) // tVar >= 0
								    );
		return result;
	}
	
	public BoolExpr genType1Formulae(ASDGPath inputP, int startIndex, int endIndex,
									              IntExpr startVar, IntExpr endVar
									            , boolean isSkew) {
		IntExpr sVar;
		IntExpr tVar;
		ASDGPath p = null;
		List<List<SDGVertex>> allPossibleInOut;
		if(!isSkew) {
			sVar = startVar;
		    tVar = endVar;
		    p = inputP;
			allPossibleInOut = p.inportsOutportsCartesianProduct(p.getG().getSdg().getVertex(startIndex),
    														   	 p.getG().getSdg().getVertex(endIndex), false);
		} else {
			sVar = endVar;
			tVar = startVar;
			p = inputP.getSkewPath();
			allPossibleInOut = p.inportsOutportsCartesianProduct(p.getG().getSdg().getVertex(endIndex),
				   	 											 p.getG().getSdg().getVertex(startIndex), true);
			
		}
		IntExpr[] absPathVars = new IntExpr[2*(p.length())];
		for(int i = 0; i < 2*p.length(); i ++) {
			if(i % 2 == 0) {
				absPathVars[i] = this.getQfpaGen().mkVariableInt("v_o_" + p.getVertex((i)/2).getSccIndex());
			} else {
				absPathVars[i] = this.getQfpaGen().mkVariableInt("v_i_" + p.getVertex((i+1)/2).getSccIndex());
			}
		}
		
		BoolExpr type1FormBody = this.getQfpaGen().mkFalse();
		for(List<SDGVertex> l : allPossibleInOut) {
			for(int i = 0; i < l.size(); i++) {
				System.out.print(l.get(i).getVertexIndex());
			}
			System.out.println();
			// for every possible inport-outport sequence
			BoolExpr type1FormBodyItem = this.getQfpaGen().mkTrue();
			if(p.length() > 0) {
				for(int i = 0; i <= p.length(); i ++) {
					BoolExpr currentAbsVertexForm = this.getQfpaGen().mkFalse();
					List<BoolExpr> sccExprs;
					if(i == 0) {
						sccExprs = this.genAbsStateNoPosCycle(p.getVertex(i), 
							l.get(2*i), l.get(2*i + 1),
							null, p.getG().getBorderEdgeByInportOutport(l.get(2*i + 1), l.get(2*i + 2)),
							sVar, absPathVars[2*i], 
							null, absPathVars[2*i + 1], isSkew);
					} else if(i == p.length()) {
						sccExprs = this.genAbsStateNoPosCycle(p.getVertex(i), 
							l.get(2*i), l.get(2*i + 1),
							p.getG().getBorderEdgeByInportOutport(l.get(2*i - 1), l.get(2*i)), null,
							absPathVars[2*i - 1], tVar, 
							absPathVars[2*i - 2], null, isSkew);
					} else {
						sccExprs = this.genAbsStateNoPosCycle(p.getVertex(i),
							l.get(2*i), l.get(2*i+1), 
							p.getG().getBorderEdgeByInportOutport(l.get(2*i - 1), l.get(2*i)), p.getG().getBorderEdgeByInportOutport(l.get(2*i+1), l.get(2*i + 2)),
							absPathVars[2*i - 1], absPathVars[2*i],
							absPathVars[2*i - 2], absPathVars[2*i + 1], isSkew);
					}
					for(BoolExpr e : sccExprs) {
						currentAbsVertexForm = this.getQfpaGen().mkOrBool(e, currentAbsVertexForm);
					}
					type1FormBodyItem = this.getQfpaGen().mkAndBool(type1FormBodyItem, currentAbsVertexForm);
				}
				type1FormBody = this.getQfpaGen().mkOrBool(type1FormBody, type1FormBodyItem);
			} else {
				// if the length of abstract path is 0
				List<BoolExpr> listExprs = this.type1ConcreteGraphPathFormula(p.getInit(), l.get(0), l.get(1), null, null, sVar, tVar, null, null, isSkew);
				for(BoolExpr e : listExprs) {
					type1FormBody = this.getQfpaGen().mkOrBool(type1FormBody, e);
				}
			}
		}
		// add non-negative requirements
		BoolExpr posRequires = this.getQfpaGen().mkTrue();
		for(int i = 0; i < absPathVars.length; i++) {
			posRequires = this.getQfpaGen().mkAndBool(
				posRequires, 
				this.getQfpaGen().mkGeBool(absPathVars[i], this.getQfpaGen().mkConstantInt(0))
			);
		}
		type1FormBody = this.getQfpaGen().mkAndBool(type1FormBody, posRequires);
		Expr type1Form = null;
		if(absPathVars.length == 0) {
			type1Form = type1FormBody;
		} else {
			type1Form = this.getQfpaGen().mkExistsQuantifier(absPathVars, type1FormBody);
		}
		return (BoolExpr) type1Form;
	}
	

	public List<BoolExpr> genAbsStateNoPosCycle(ASDGVertex v, SDGVertex inport, SDGVertex outport, 
														   BorderEdge in, BorderEdge out, 
														   IntExpr thisInVar,  IntExpr thisOutVar,
														   IntExpr lastOutVar, IntExpr nextInVar, boolean isSkew) {
		assert(v.containIndex(inport.getVertexIndex()) && v.containIndex(outport.getVertexIndex()));
		assert(thisInVar != null && thisOutVar != null && v != null);
		return this.type1ConcreteGraphPathFormula(v, inport, outport, in, out, thisInVar, thisOutVar, lastOutVar, nextInVar, isSkew);
	}

	public BoolExpr vertexBorderEdgeWeightAndDropRequirements(BorderEdge in, BorderEdge out, IntExpr thisInVar, IntExpr thisOutVar,
																							 IntExpr lastOutVar, IntExpr nextInVar) {
		if(in == null && out == null && lastOutVar == null && nextInVar == null) {
			return this.singleAbsStateBorderEdgeWeightAndDropRequirements();
		} else if(in == null && lastOutVar == null) {
			return this.startVertexBorderEdgeWeigthAndDropRequirements(out, thisInVar, thisOutVar, nextInVar);
		} else if(out == null && nextInVar == null) {
			return this.endVertexBorderEdgeWeightAndDropRequirements(in, thisInVar, thisOutVar, lastOutVar);
		} else {
			assert(in != null && out != null && thisInVar != null && thisOutVar == null && nextInVar == null && lastOutVar == null);
			return this.midBorderEdgeWeightAndDropRequirements(in, out, thisInVar, thisOutVar, lastOutVar, nextInVar);
		}
	}
	
	
	public BoolExpr singleAbsStateBorderEdgeWeightAndDropRequirements() {
		BoolExpr result = this.getQfpaGen().mkTrue();
		return result;
	}

	public BoolExpr startVertexBorderEdgeWeigthAndDropRequirements(BorderEdge out, 
																	IntExpr thisInVar, IntExpr thisOutVar,
																	IntExpr nextInVar) {
		BoolExpr formula = this.getQfpaGen().mkEqBool(
					this.getQfpaGen().mkSubInt(nextInVar, thisOutVar), 
					this.getQfpaGen().mkConstantInt(out.getWeight()));
		return formula;
	}

	public BoolExpr endVertexBorderEdgeWeightAndDropRequirements(BorderEdge in,
																  IntExpr thisInVar, IntExpr thisOutVar,
																  IntExpr lastOutVar) {
		BoolExpr formula = this.getQfpaGen().mkEqBool(
					this.getQfpaGen().mkSubInt(thisInVar, lastOutVar), 
					this.getQfpaGen().mkConstantInt(in.getWeight()));
		return formula;
	}

	public BoolExpr midBorderEdgeWeightAndDropRequirements(BorderEdge in, BorderEdge out, 
														IntExpr thisInVar, IntExpr thisOutVar, 
														IntExpr lastOutVar, IntExpr nextInVar) {
		BoolExpr formula = this.getQfpaGen().mkAndBool(
			// border edge weight add correctly
			this.getQfpaGen().mkEqBool(
					this.getQfpaGen().mkSubInt(thisInVar, lastOutVar), 
					this.getQfpaGen().mkConstantInt(in.getWeight())
			),
			this.getQfpaGen().mkEqBool(
					this.getQfpaGen().mkSubInt(nextInVar, thisOutVar), 
					this.getQfpaGen().mkConstantInt(out.getWeight()))
			);
			return formula;
		}
	
	/* Cases:
	 * 1. when ASDGVertex v to dgraph is a single vertex
	 * 2. when ASDGVertex v to dgraph is SCC with the same inport and outport
	 * 3. when ASDGVertex v to dgraph is SCC with different inport and outport 
	 */
	public List<BoolExpr> type1ConcreteGraphPathFormula(ASDGVertex v, SDGVertex inport, SDGVertex outport, 
			   										 BorderEdge in, BorderEdge out, 
			   										 IntExpr thisInVar,  IntExpr thisOutVar,
			   										 IntExpr lastOutVar, IntExpr nextInVar, boolean isSkew) {
		
		List<BoolExpr> exprs = new ArrayList<BoolExpr>();
		DGraph conGraph = v.getConcreteDGraph();
		// case 1
		if(conGraph.getVertices().size() == 1 && conGraph.getVertices().get(0).getEdges().size() == 0) {
			// if the SCC is a plain vertex
			BoolExpr formula = this.getQfpaGen().mkAndBool(
				this.vertexBorderEdgeWeightAndDropRequirements(in, out, thisInVar, thisOutVar, lastOutVar, nextInVar),
				this.getQfpaGen().mkEqBool(thisOutVar, thisInVar)
			);
			exprs.add(formula);
			return exprs;
		} 
		
		// case 2 and 3
		List<DGraph> supports = conGraph.getAllPossibleSupport(inport.getVertexIndex(), outport.getVertexIndex());
		for(DGraph support : supports) {
			// if there is a positive cycle in the support ignore it
			if(support.computeLoopTag() == LoopTag.Pos || support.computeLoopTag() == LoopTag.PosNeg) {
				continue;
			}
			if(support.containsCycle()) {
				// increase the max length to 2n^2 + 1
				support.increaseDWTLenLimit();
				// guess that there is a cycle and apply the lemma
				// length > 2n^2
				BoolExpr formGe = this.getQfpaGen().mkFalse();
				// guess the mid vertex and initialize an Int type variable for it
				for(DGVertex ve : support.getVertices()) {
					//TODO: REC change the name here
					IntExpr midVar = this.getQfpaGen().mkVariableInt("z_mid_"+ ve.getIndex());
					// latter dynamic part
					BoolExpr formGeDynamic = this.type1DynamicPartFormula(support, this.getSdg().getVertex(ve.getIndex()), outport, midVar, thisOutVar);
					if(formGeDynamic == null) {
						continue;
					}
					// former flow part
					formGe = this.getQfpaGen().mkOrBool(
						formGe, 
						this.getQfpaGen().mkAndBool(
							// path flow
							this.genPathFlowFormula(support, inport.getVertexIndex(), ve.getIndex(), thisInVar, midVar),
							// z > |V|, this guarantee the counter value along the path to be positive
							//TODO: GEN change the midVar bound
							this.getQfpaGen().mkGeBool(midVar, this.getQfpaGen().mkConstantInt(support.getVertices().size())),
							formGeDynamic
						)
					);
					// add border edge requirements
					formGe = this.getQfpaGen().mkAndBool(formGe,
							this.vertexBorderEdgeWeightAndDropRequirements(in, out, thisInVar, thisOutVar, lastOutVar, nextInVar));
					// add non-negative requirements
					formGe = this.getQfpaGen().mkAndBool(this.getQfpaGen().mkRequireNonNeg(midVar), formGe);
					// add existential quantifier
					IntExpr[] midVarQuant = new IntExpr[1];
					midVarQuant[0] = midVar;
					formGe = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(midVarQuant, formGe);
				}
				// length < 2n^2 + 1
				BoolExpr formLt = this.type1DynamicPartFormula(support, inport, outport, thisInVar, thisOutVar);
				// if not reachable ignore the support and does not generate any formula 
				if(formLt == null) {
					continue;
				}
				// add border edge requirements
				formLt = this.getQfpaGen().mkAndBool(formLt, 
						this.vertexBorderEdgeWeightAndDropRequirements(in, out, thisInVar, thisOutVar, lastOutVar, nextInVar));
				BoolExpr formula = this.getQfpaGen().mkOrBool(formLt, formGe);
				exprs.add(formula);
			} else {
				BoolExpr concretePathFormula = this.getQfpaGen().mkFalse();
				DWTEntry entry = support.getTable().getEntry(inport.getVertexIndex(), outport.getVertexIndex());
				if(entry == null) {
					concretePathFormula = this.getQfpaGen().mkOrBool(concretePathFormula, 
								this.getQfpaGen().mkEqBool(thisInVar, thisOutVar)
							);
				} else {
					for(DWTuple t : entry.getSetOfDWTuples()) {
						concretePathFormula = this.getQfpaGen().mkOrBool(
							concretePathFormula, 
							this.getQfpaGen().mkAndBool(
								// weight sum correctly in the concreteScc
								this.getQfpaGen().mkEqBool(
									thisOutVar, 
									this.getQfpaGen().mkAddInt(thisInVar, this.getQfpaGen().mkConstantInt(t.getWeight()))),
									// the minimum counter value >= 0
								this.getQfpaGen().mkGeBool(
										this.getQfpaGen().mkAddInt(thisInVar, this.getQfpaGen().mkConstantInt(t.getDrop())), 
										this.getQfpaGen().mkConstantInt(0))
							)
						);
					}
				}
				
				
				// guess it to be a simple path
				BoolExpr formula = this.getQfpaGen().mkAndBool(
					this.vertexBorderEdgeWeightAndDropRequirements(in, out, thisInVar, thisOutVar, lastOutVar, nextInVar),
					// there is a concrete path from inport to outport in concrete SCC graph
					concretePathFormula
				);
				exprs.add(formula);
			}
		}
		return exprs;
	}
	
	public BoolExpr type1DynamicPartFormula(DGraph support, SDGVertex inport, SDGVertex outport, 
															IntExpr thisInVar, IntExpr thisOutVar) {
		BoolExpr formDynamic = this.getQfpaGen().mkFalse();
		if(inport.getVertexIndex() == outport.getVertexIndex()) {
			// if inport and outport is the same vertex, it is naturally reachable
			formDynamic = this.getQfpaGen().mkEqBool(thisInVar, thisOutVar);
			if(support.getTable().getEntry(inport.getVertexIndex(), outport.getVertexIndex()) == null) {
				// if the entry does not exists, return the natural one
				return formDynamic;
			}
		} else {
			formDynamic = this.getQfpaGen().mkFalse();
			if(support.getTable().getEntry(inport.getVertexIndex(), outport.getVertexIndex()) == null) {
				// if the entry does not exists, ignore the support
				return null;
			}
		}
		
		
		for(DWTuple t : support.getTable().getEntry(inport.getVertexIndex(), outport.getVertexIndex()).getSetOfDWTuples()) {
			BoolExpr temp = this.getQfpaGen().mkAndBool(
				// weight sum correctly in the concreteScc
				this.getQfpaGen().mkEqBool(
					thisOutVar, 
					this.getQfpaGen().mkAddInt(thisInVar, this.getQfpaGen().mkConstantInt(t.getWeight()))),
					// the minimum counter value >= 0
				this.getQfpaGen().mkGeBool(
						this.getQfpaGen().mkAddInt(thisInVar, this.getQfpaGen().mkConstantInt(t.getDrop())), 
						this.getQfpaGen().mkConstantInt(0))
			);
			formDynamic = this.getQfpaGen().mkOrBool(formDynamic, temp);
		} 
		return formDynamic;
	}
	
	
	public BoolExpr genPathFlowFormula(DGraph g, int startIndex, int endIndex,
												 IntExpr startVar, IntExpr endVar) {
		List<DGEdge> edgeList = g.getEdges();
		IntExpr[] flowVars = new IntExpr[edgeList.size()];
		DGFlowTuple[] flowTuples = new DGFlowTuple[edgeList.size()];
		for(int i = 0; i < edgeList.size(); i ++) {
			flowVars[i] = this.getQfpaGen().mkVariableInt("f_" + edgeList.get(i).getFrom().getIndex() + "_" +
																 edgeList.get(i).getTo().getIndex());
			flowTuples[i] = new DGFlowTuple(edgeList.get(i),flowVars[i]);
		}
		if(g.isConnected()) {
			DGVertex vs = g.getVertex(startIndex);
			DGVertex vt = g.getVertex(endIndex);
			BoolExpr body = this.getQfpaGen().mkTrue();
			BoolExpr startVertexForm = null;
			BoolExpr endVertexForm = null;
			// different start and end vertex
			if(startIndex != endIndex) {
				// start vertex flow requirements
				startVertexForm = this.getQfpaGen().mkEqBool(
					this.getQfpaGen().mkAddInt(this.getQfpaGen().sumUpVars(this.getAllFlowInVars(vs, flowTuples)), this.getQfpaGen().mkConstantInt(1)),
					this.getQfpaGen().sumUpVars(this.getAllFlowOutVars(vs, flowTuples))
				);
				// end vertex flow requirements
				endVertexForm = this.getQfpaGen().mkEqBool(
					this.getQfpaGen().sumUpVars(this.getAllFlowInVars(vt, flowTuples)), 
					this.getQfpaGen().mkAddInt(this.getQfpaGen().sumUpVars(this.getAllFlowOutVars(vt, flowTuples)), this.getQfpaGen().mkConstantInt(1))
				);
			// same start and end vertex
			} else {
				// the following formulae should be the same 
				startVertexForm = this.getQfpaGen().mkEqBool(
						this.getQfpaGen().sumUpVars(this.getAllFlowInVars(vs, flowTuples)),
						this.getQfpaGen().sumUpVars(this.getAllFlowOutVars(vs, flowTuples))
				);
				endVertexForm = this.getQfpaGen().mkEqBool(
						this.getQfpaGen().sumUpVars(this.getAllFlowInVars(vt, flowTuples)),
						this.getQfpaGen().sumUpVars(this.getAllFlowOutVars(vt, flowTuples))
				);
			}
			
			body = this.getQfpaGen().mkAndBool(startVertexForm, endVertexForm);
			for(DGVertex v : g.getVertices()) {
				// all other internal vertices flow requirements
				if(v.getIndex() != startIndex && v.getIndex() != endIndex) {
					BoolExpr vertexForm = this.getQfpaGen().mkEqBool(
						this.getQfpaGen().sumUpVars(this.getAllFlowInVars(v, flowTuples)), 
						this.getQfpaGen().sumUpVars(this.getAllFlowOutVars(v, flowTuples)));
					body = this.getQfpaGen().mkAndBool(vertexForm, body);
				}
			}
			// weight requirements
			// drop requirements are guaranteed by the 2n^2 + 1 or type-3 certificate
			IntExpr sum = this.getQfpaGen().mkConstantInt(0);
			for(DGFlowTuple t : flowTuples) {
				sum = this.getQfpaGen().mkAddInt(
					sum, 
					this.getQfpaGen().mkScalarTimes(this.getQfpaGen().mkConstantInt(t.getEdge().getWeight()), t.getEdgeVar())
				);
			}
			BoolExpr weightSumCorrect = this.getQfpaGen().mkEqBool(sum, this.getQfpaGen().mkSubInt(endVar, startVar));
			BoolExpr flowVarsPosRequirements = this.getQfpaGen().mkTrue();
			for(IntExpr var : flowVars) {
				flowVarsPosRequirements = this.getQfpaGen().mkAndBool(flowVarsPosRequirements, this.getQfpaGen().mkRequireNonNeg(var));
			}
			body = this.getQfpaGen().mkAndBool(body, weightSumCorrect, flowVarsPosRequirements);
			
			BoolExpr result = null;
			if(flowVars.length > 0) {
				result = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(flowVars, body);
			} else {
				result = body;
			}
			return result;
		} else {
			return this.getQfpaGen().mkFalse();
		}
	}

	public List<IntExpr> getAllFlowInVars(DGVertex v, DGFlowTuple[] flowTuples){
		List<IntExpr> inVars = new ArrayList<IntExpr>();
		for(DGFlowTuple t : flowTuples) {
			if(t.getEdgeTo().getIndex() == v.getIndex()) {
				inVars.add(t.getEdgeVar());
			}
		}
		return inVars;
	}

	public List<IntExpr> getAllFlowOutVars(DGVertex v, DGFlowTuple[] flowTuples){
		List<IntExpr> outVars = new ArrayList<IntExpr>();
		for(DGFlowTuple t : flowTuples) {
			if(t.getEdgeFrom().getIndex() == v.getIndex()) {
				outVars.add(t.getEdgeVar());
			}
		}
		return outVars;
	}	
	
	public BoolExpr genType12Formulae(ASDGPath p, int startIndex, int endIndex,
												   IntExpr startVar, IntExpr endVar) {
		// assert there exists a positive tag abstract vertex
		assert(p.containsPosTagVertex());
		List<ASDGVertex> posVertices = p.getAllType12Split();
		// split the abstrac path
		IntExpr[] splitVars = new IntExpr[2];
		splitVars[0] = this.getQfpaGen().mkVariableInt("vt_1");
		splitVars[1] = this.getQfpaGen().mkVariableInt("vs_2");
		boolean hasBorder = false;
		BoolExpr type12Form = this.getQfpaGen().mkFalse();
		for(ASDGVertex v : posVertices) {
			ASDGPath[] paths = p.splitPathAt(v);
			if(paths[0] != null) {
				hasBorder = true;
				// if there exists type1 path
				// for all the possible split border edges
				BoolExpr borderEdgesFormula = this.getQfpaGen().mkFalse();
				for(BorderEdge e : p.getG().getBorderEdgesByAbsEdge(paths[0].getLastVertex().getSccIndex(), 
																	paths[1].getInit().getSccIndex())) {
					BoolExpr expr1 = this.genType1Formulae(paths[0], startIndex, e.getFromVertex().getVertexIndex(), startVar, splitVars[0], false);
					BoolExpr expr2 = this.genType1Formulae(paths[1], e.getToVertex().getVertexIndex(), endIndex, splitVars[1], endVar, true);
					borderEdgesFormula = this.getQfpaGen().mkOrBool(
						this.getQfpaGen().mkAndBool(
							expr1,
							expr2,
							this.getQfpaGen().mkEqBool(
								this.getQfpaGen().mkAddInt(
									splitVars[0], 
									this.getQfpaGen().mkConstantInt(e.getWeight()
								)),
								splitVars[1])),
						borderEdgesFormula
					);
				}
				type12Form = this.getQfpaGen().mkOrBool(
					type12Form,
					borderEdgesFormula
				);
			} else {
				// if there is no type1 path
				type12Form = this.genType1Formulae(paths[1], startIndex, endIndex, startVar, endVar, true);
			}
		}
		type12Form = hasBorder ? (BoolExpr) this.getQfpaGen().mkExistsQuantifier(splitVars, type12Form) : type12Form;
		return type12Form;
	}
	
	
	
	public BoolExpr genType132Formulae(ASDGPath p, int startIndex, int endIndex, 
												 IntExpr startVar, IntExpr endVar) {
		// assert there is a condition for type132
		assert(p.containsNegTagVertex() && p.containsNegTagVertex());
		List<ASDGVertex[]> splitVertices = p.getAllType132Split();
		BoolExpr type132Form = this.getQfpaGen().mkFalse();
		// subtype 132 mid vars
		IntExpr[] fullSplitVars = new IntExpr[4];
		fullSplitVars[0] = this.getQfpaGen().mkVariableInt("vt_1");
		fullSplitVars[1] = this.getQfpaGen().mkVariableInt("vs_3");
		fullSplitVars[2] = this.getQfpaGen().mkVariableInt("vt_3");
		fullSplitVars[3] = this.getQfpaGen().mkVariableInt("vs_2");
		// subtype 13 mid vars
		IntExpr[] splitVars13 = new IntExpr[2];
		splitVars13[0] = fullSplitVars[0];
		splitVars13[1] = fullSplitVars[1];
		// subtype 32 mid vars
		IntExpr[] splitVars32 = new IntExpr[2];
		splitVars32[0] = fullSplitVars[2];
		splitVars32[1] = fullSplitVars[3];
		
		for(ASDGVertex[] s : splitVertices) {
			ASDGPath[] paths = p.getAllType132SplitPaths(s);
			BoolExpr portForm = this.getQfpaGen().mkTrue();
			assert(paths[1] != null);
			if(paths[0] != null && paths[1] != null && paths[2] != null) {
				// subtype 132
				for(SDGVertex[] inouts : p.getType132LinkInportOutport(s)) {
					portForm = this.getQfpaGen().mkAndBool(
							this.genType1Formulae(paths[0], startIndex, inouts[0].getVertexIndex(), startVar, fullSplitVars[0], false),
							this.genType3Formula(paths[1], inouts[1].getVertexIndex(), inouts[2].getVertexIndex(), fullSplitVars[1], fullSplitVars[2]),
							this.genType1Formulae(paths[2], inouts[3].getVertexIndex(), endIndex, fullSplitVars[3], endVar, true),
							this.getQfpaGen().mkEqBool(
									fullSplitVars[1], 
									this.getQfpaGen().mkAddInt(
											fullSplitVars[0], 
											this.getQfpaGen().mkConstantInt(p.getG().getBorderEdgeByInportOutport(inouts[0], inouts[1]).getWeight())
									)
							),
							this.getQfpaGen().mkEqBool(
									fullSplitVars[3], 
									this.getQfpaGen().mkAddInt(
											fullSplitVars[2], 
											this.getQfpaGen().mkConstantInt(p.getG().getBorderEdgeByInportOutport(inouts[2], inouts[3]).getWeight())
									)
							)
							
					);
					type132Form = this.getQfpaGen().mkOrBool(portForm, type132Form);
				}
				type132Form = this.getQfpaGen().mkAndBool(
						type132Form,
						this.genVarNonNegRequirement(fullSplitVars)
				);
				type132Form = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(fullSplitVars, type132Form);
			} else if(paths[0] != null && paths[2] == null) {
				// subtype 13
				for(SDGVertex[] inouts : p.getType132LinkInportOutport(s)) {
					portForm = this.getQfpaGen().mkAndBool(
							this.genType1Formulae(paths[0], startIndex, inouts[0].getVertexIndex(), startVar, splitVars13[0], false),
							this.genType3Formula(paths[1], inouts[1].getVertexIndex(), endIndex, splitVars13[1], endVar),
							this.getQfpaGen().mkEqBool(
									splitVars13[1], 
									this.getQfpaGen().mkAddInt(
											splitVars13[0], 
											this.getQfpaGen().mkConstantInt(p.getG().getBorderEdgeByInportOutport(inouts[0], inouts[1]).getWeight())
									)
							)
					);
					type132Form = this.getQfpaGen().mkOrBool(portForm, type132Form);
				}
				type132Form = this.getQfpaGen().mkAndBool(
						type132Form,
						this.genVarNonNegRequirement(splitVars13)
				);
				type132Form = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(splitVars13, type132Form);
			} else if(paths[0] == null && paths[2] != null) {
				// subtype 32
				for(SDGVertex[] inouts : p.getType132LinkInportOutport(s)) {
					portForm = this.getQfpaGen().mkAndBool(
							this.genType3Formula(paths[1], startIndex, inouts[0].getVertexIndex(), startVar, splitVars32[0]),
							this.genType1Formulae(paths[2], inouts[1].getVertexIndex(), endIndex, splitVars32[1], endVar, true),
							this.getQfpaGen().mkEqBool(
									splitVars32[1], 
									this.getQfpaGen().mkAddInt(
											splitVars32[0], 
											this.getQfpaGen().mkConstantInt(p.getG().getBorderEdgeByInportOutport(inouts[0], inouts[1]).getWeight())
									)
							)
					);
					type132Form = this.getQfpaGen().mkOrBool(portForm, type132Form);
				}
				type132Form = this.getQfpaGen().mkAndBool(
						type132Form,
						this.genVarNonNegRequirement(splitVars32)
				);
				type132Form = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(splitVars32, type132Form);
			} else {
				assert(paths[0] == null && paths[2] == null);
				// subtype 3
				portForm = this.genType3Formula(paths[1], startIndex, endIndex, startVar, endVar);
				type132Form = (BoolExpr) portForm;
			}
		}
		return type132Form;
	}
	
	public BoolExpr genVarNonNegRequirement(IntExpr[] vars) {
		BoolExpr result = this.getQfpaGen().mkTrue();
		for(IntExpr var : vars) {
			result = this.getQfpaGen().mkAndBool(
					result,
					this.getQfpaGen().mkGeBool(var, this.getQfpaGen().mkConstantInt(0))
			);
		}
		return result;
	}
	
	public BoolExpr genType3Formula(ASDGPath p3, int startIndex, int endIndex,
												  IntExpr startVar, IntExpr endVar) {
		ASDGVertex startVertex = p3.getInit();
		ASDGVertex endVertex   = p3.getLastVertex();
		assert(startVertex.containIndex(startIndex) && endVertex.containIndex(endIndex));
		DGraph p3Graph = this.genType3ConcreteGraph(p3, startIndex, endIndex);
		DGraph p3SkewGraph = p3Graph.getSkewTranspose();
		assert(p3Graph.containsVertex(startIndex) && p3Graph.containsVertex(endIndex));
		IntExpr startMidVar = this.getQfpaGen().mkVariableInt("vm_3_s");
		IntExpr endMidVar = this.getQfpaGen().mkVariableInt("vm_3_e");
		IntExpr[] boundVars = new IntExpr[2];
		boundVars[0] = startMidVar;
		boundVars[1] = endMidVar;
		BoolExpr startPosTemp = this.getQfpaGen().mkFalse();
		BoolExpr endPosTemp = this.getQfpaGen().mkFalse();
		for(DGVertex vms : p3Graph.getVertices()) {
			startPosTemp = this.getQfpaGen().mkOrBool(startPosTemp, this.genPosCycleTemplateFormula(p3Graph, startIndex, vms.getIndex(), startVar, startMidVar));
		}
		for(DGVertex vme : p3SkewGraph.getVertices()) {
			endPosTemp = this.getQfpaGen().mkOrBool(endPosTemp, this.genPosCycleTemplateFormula(p3SkewGraph, endIndex, vme.getIndex(), endVar, endMidVar));
		}
		BoolExpr posTemp = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(boundVars, this.getQfpaGen().mkAndBool(
																											startPosTemp, 
																											endPosTemp,
																											this.genVarNonNegRequirement(boundVars))
																										);
		BoolExpr pathFlow = this.genPathFlowFormula(p3Graph, startIndex, endIndex, startVar, endVar);
		BoolExpr result = this.getQfpaGen().mkAndBool(
				posTemp, 
				pathFlow
		);
		return result;
	}
	
	public DGraph genType3ConcreteGraph(ASDGPath p3, int startIndex, int endIndex) {
		List<DGEdge> concreteEdgeList = new ArrayList<DGEdge>();
		for(int i = 0; i < p3.getPath().size() - 1; i++) {
			List<BorderEdge> borderEdges = p3.getVertex(i).getAllConcreteEdgesTo(p3.getVertex(i+1));
			for(BorderEdge e : borderEdges) {
				concreteEdgeList.add(e.getConcreteEdge());
			}
		}
		for(ASDGVertex v : p3.getPath()) {
			for(DGEdge e : v.getConcreteDGraph().getEdges()) {
				concreteEdgeList.add(e);
			}
		}
		DGraph type3Graph = p3.getVertex(0).getGraph().getSdg().getGraph().edgeListToGraph(concreteEdgeList, startIndex, endIndex);
		return type3Graph;
	}
	
	public BoolExpr genPosCycleTemplateFormula(DGraph g, int startIndex, int guessIndex, IntExpr startVar, IntExpr guessVar) {
		// description: return the possible positive cycle template with the minimum drop, if none return null
		assert(g.containsVertex(startIndex) && g.containsVertex(guessIndex));
		if(g.getTag() == null) {
			g.computeLoopTag();
		}
		BoolExpr form = this.getQfpaGen().mkFalse();
		DWTEntry startToGuessEntry = g.getTable().getEntry(startIndex, guessIndex);
		int maxDrop = -2*g.getVertices().size() - 1;
		BoolExpr maxForm = this.getQfpaGen().mkFalse();
		if(startToGuessEntry != null) {
			for(DWTuple t : startToGuessEntry.getSetOfDWTuples()) {
				DWTEntry guessLoopEntry = g.getTable().getEntry(guessIndex, guessIndex);
				if(guessLoopEntry != null) {
					for(DWTuple tg : guessLoopEntry.getSetOfDWTuples()) {
						if(tg.getWeight() > 0) {
							int drop = Math.min(t.getDrop(), tg.getDrop() + t.getWeight());
							form = this.getQfpaGen().mkAndBool(
									this.getQfpaGen().mkGeBool(this.getQfpaGen().mkAddInt(startVar, this.getQfpaGen().mkConstantInt(drop)), this.getQfpaGen().mkConstantInt(0)),
									this.getQfpaGen().mkEqBool(this.getQfpaGen().mkAddInt(startVar, this.getQfpaGen().mkConstantInt(t.getWeight())), guessVar)
							);
							if(drop > maxDrop) {
								maxDrop = drop;
								maxForm = form;
							}
						} 
					}
				}
			}
		}
		return maxForm;
	}
	
	public BoolExpr combineAllFormlae(BoolExpr type1, BoolExpr type12, BoolExpr type132) {
		BoolExpr result = this.getQfpaGen().mkOrBool(
			type1, type12, type132
		);
		return result;
	}
	
	//getters and setters
	public OCA getOca() {
		return oca;
	}
	public void setOca(OCA oca) {
		this.oca = oca;
	}
	public SDGraph getSdg() {
		return sdg;
	}
	public void setSdg(SDGraph sdg) {
		this.sdg = sdg;
	}
	public DGraph getDgraph() {
		return dgraph;
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
	public ASDGraph getAsdg() {
		return asdg;
	}
	public void setAsdg(ASDGraph asdg) {
		this.asdg = asdg;
	}

	public State getStartState() {
		return startState;
	}

	public void setStartState(State startState) {
		this.startState = startState;
	}

	public State getEndState() {
		return endState;
	}

	public void setEndState(State endState) {
		this.endState = endState;
	}
	
}
