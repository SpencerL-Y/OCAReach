package ocareach;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;

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
	// TODO: debug
	// input: A One-Counter Automaton and two states s, t of it
	// output: A QFPA formula that is satisfiable if there is a 
	// a possible run from (s,x) to (t,y) where x and y are integer
	// variable
	
	private OCA oca;
	private DGraph dgraph;
	private SDGraph sdg;
	private ASDGraph asdg;
	private QFPAGenerator qfpaGen;
	private State startState;
	private State endState;
	
	public Converter(OCA oca) {
		this.setOca(oca);
		this.dgraph = this.getOca().toDGraph();
		this.sdg = new SDGraph(this.getDgraph());
		this.qfpaGen = new QFPAGenerator();
	}
	
	
	
	
	
	// ALGORITHM
	public String convert() {
		return this.convert(this.oca.getInitState(), this.oca.getTargetState());
	}
	
	public String convert(State startState, State endState) {
		assert(this.getOca().containsState(startState) && this.getOca().containsState(endState));
		//set starting and ending vertex in dg
		this.getDgraph().setStartVertexIndex(startState.getIndex());
		this.getDgraph().setEndingVertexIndex(endState.getIndex());
		// mark sccIndex in SDG
		this.getSdg().tarjan();
		// construct abstract sdg
		this.asdg = new ASDGraph(this.getSdg());
		ASDGVertex absStart = this.getAsdg().getVertex(this.getSdg().getStartingVertex().getSccMark());
		ASDGVertex absEnd = this.getAsdg().getVertex(this.getSdg().getEndingVertex().getSccMark());
		// get all the possible abstract path
		List<ASDGPath> paths = this.getAsdg().DFSFindAbsPaths(absStart.getSccIndex(), absEnd.getSccIndex());
		List<BoolExpr> formulae = new ArrayList<BoolExpr>();
		IntExpr sVar = this.getQfpaGen().mkVariableInt("xs");
		IntExpr tVar = this.getQfpaGen().mkVariableInt("xt");
		for(ASDGPath p : paths) {
			System.out.print("ABSTRACT PATH layer0: ");
			for(ASDGVertex v : p.getPath()) {
				System.out.print(v.getSccIndex());
			}
			System.out.println();
			
			// there is no cycles in the SCCs (trivial case: every scc is a concrete vertex)
			boolean trivial = !p.containsCycledVertex();
			// there might be type-1 certificate
			// TODO: DEBUG CHECK AGAIN
			boolean type1 = true;
			// there might be type-1 . type-2 certificate
			boolean type12 = p.containsPosTagVertex();
			// there might be type-1 . type-3 . type-2 certificate
			boolean type132 = p.containsNegTagVertex() && p.containsPosTagVertex();
			BoolExpr trivialForm = this.getQfpaGen().mkFalse();
			BoolExpr type1Form = this.getQfpaGen().mkFalse();
			BoolExpr type12Form = this.getQfpaGen().mkFalse();
			BoolExpr type132Form = this.getQfpaGen().mkFalse();
			if(trivial) {
				System.out.println("TYPE TRIVIAL layer1:");
				trivialForm = this.genTrivialFormula(p);
				System.out.println("TRIVIAL Formula: ");
				System.out.println(trivialForm.toString());
			}
			if(type1 && !trivial) {
				System.out.println("TYPE 1 layer 1:");
				type1Form = this.genType1Formulae(p, startState.getIndex(), endState.getIndex(), sVar, tVar, false);
				System.out.println("TYPE1 Formula: ");
				System.out.println(type1Form.toString());
			}
			if(type12) {
				System.out.println("TYPE 12 layer 1:");
				type12Form = this.genType12Formulae(p, startState.getIndex(), endState.getIndex(), sVar, tVar);
				System.out.println("TYPE 12 Formula: ");
				System.out.println(type12Form.toString());
			}
			if(type132) {
				System.out.println("TYPE 132 layer1:");
				type132Form = this.genType132Formulae(p, startState.getIndex(), endState.getIndex(), sVar, tVar);
				System.out.println("TYPE 132 Formula: ");
				System.out.println(type132Form.toString());
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
		// simplification 
		//resultExpr = (BoolExpr) resultExpr.simplify();
		// debug:
		/*
		IntExpr iVar = this.getQfpaGen().mkVariableInt("i");
		IntExpr jVar = this.getQfpaGen().mkVariableInt("j");
		List<IntExpr> sum = new ArrayList<IntExpr>(3);
		sum.add(null);
		sum.add(null);
		sum.add(null);
		sum.set(0, this.getQfpaGen().mkScalarTimes(this.getQfpaGen().mkConstantInt(2), iVar));
		sum.set(1, this.getQfpaGen().mkScalarTimes(this.getQfpaGen().mkConstantInt(-1), jVar));
		sum.set(2, sVar);
		IntExpr[] bounds = new IntExpr[2];
		bounds[0] = iVar;
		bounds[1] = jVar;
		BoolExpr equiv = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(bounds,
					this.getQfpaGen().mkAndBool(
							this.getQfpaGen().mkEqBool(tVar, this.getQfpaGen().sumUpVars(sum)),
							this.getQfpaGen().mkRequireNonNeg(jVar),
					this.getQfpaGen().mkRequireNonNeg(iVar),
					this.getQfpaGen().mkRequireNonNeg(tVar),
					this.getQfpaGen().mkRequireNonNeg(sVar)
				));
		
		BoolExpr equiv = this.getQfpaGen().mkAndBool(
				this.getQfpaGen().mkGeBool(sVar, this.getQfpaGen().mkConstantInt(0)),
				this.getQfpaGen().mkRequireNonNeg(tVar),
				this.getQfpaGen().mkGtBool(tVar, sVar)
				);
		resultExpr = this.getQfpaGen().mkAndBool(this.getQfpaGen().getCtx().mkImplies(resultExpr, equiv), this.getQfpaGen().getCtx().mkImplies(equiv, resultExpr));
		resultExpr = this.getQfpaGen().mkNotBool(resultExpr);*/
		
		result = resultExpr.toString();
		
		return result;
	}
	

	
	public BoolExpr genTrivialFormula(ASDGPath p) {
		//TODO debug
		// convert abstract vertices to concrete vertices
		// we need the edge information so the path is constructed like this
		DGVertex startV = this.getDgraph().getVertex(p.getVertex(0).getConcreteDGraph().getVertices().get(0).getIndex());
		DGPath cp = new DGPath(startV);
		for(int i = 1; i <= p.length(); i++) {
			DGVertex currentV = this.getDgraph().getVertex(p.getVertex(i).getConcreteDGraph().getVertices().get(0).getIndex());
			cp.concatVertex(currentV);
		}
		IntExpr sVar = this.getQfpaGen().mkVariableInt("xs");
		IntExpr tVar = this.getQfpaGen().mkVariableInt("xt");
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
	
	// two kinds of implementation :
	// 1. guess support then determine which type certificate
	// 2. check the possible types of certificates then guess the support with requirements
	// Here we use the second one
	// TODO: debug trivial case from abstract state to concreate graph
	//TODO: debug
	public BoolExpr genType1Formulae(ASDGPath inputP, int startIndex, int endIndex,
									              IntExpr startVar, IntExpr endVar
									              , boolean isSkew) {
		System.out.println("p len: " + inputP.length());
		//assertion
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
		//int inOutSeqSize = allPossibleInOut.get(0).size();
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
			System.out.println("ALL INOUTS layer 2: " );
			for(int i = 0; i < l.size(); i++) {
				System.out.print(l.get(i).getVertexIndex());
			}
			System.out.println();
			// for every possible sequence of inoutports
			BoolExpr type1FormBodyItem = this.getQfpaGen().mkTrue();
			if(p.length() > 0) {

				System.out.println("AbsPath length > 0: " + p.length());
				for(int i = 0; i <= p.length(); i ++) {
					BoolExpr currentAbsVertexForm = this.getQfpaGen().mkFalse();
					List<BoolExpr> sccExprs;
					if(i == 0) {
						System.out.println(l.get(2*i).getVertexIndex() + " TO " + l.get(2*i+1).getVertexIndex());
						sccExprs = this.genAbsStateNoPosCycle(p.getVertex(i), 
							l.get(2*i), l.get(2*i + 1),
							null, p.getG().getBorderEdgeByInportOutport(l.get(2*i + 1), l.get(2*i + 2)),
							sVar, absPathVars[2*i], 
							null, absPathVars[2*i + 1], isSkew);
					} else if(i == p.length()) {
						System.out.println(l.get(2*i).getVertexIndex() + " TO " + l.get(2*i+1).getVertexIndex());
						sccExprs = this.genAbsStateNoPosCycle(p.getVertex(i), 
							l.get(2*i), l.get(2*i + 1),
							p.getG().getBorderEdgeByInportOutport(l.get(2*i - 1), l.get(2*i)), null,
							absPathVars[2*i - 1], tVar, 
							absPathVars[2*i - 2], null, isSkew);
					} else {
						System.out.println(l.get(2*i).getVertexIndex() + " TO " + l.get(2*1 + 1).getVertexIndex());
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
				// if the length of abspath is 0
				System.out.println("AbsPath length = " + p.length());
				List<BoolExpr> listExprs = this.type1ConcreteGraphPathFormula(p.getInit(), l.get(0), l.get(1), null, null, sVar, tVar, null, null, isSkew);
				for(BoolExpr e : listExprs) {
					type1FormBody = this.getQfpaGen().mkOrBool(type1FormBody, e);
				}
			}
		}
		// add ge 0 requirement
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
	
	//TODO: debug
	//TODO: imple add variable positive requirement
	public List<BoolExpr> genAbsStateNoPosCycle(ASDGVertex v, SDGVertex inport, SDGVertex outport, 
														   BorderEdge in, BorderEdge out, 
														   IntExpr thisInVar,  IntExpr thisOutVar,
														   IntExpr lastOutVar, IntExpr nextInVar, boolean isSkew) {
		System.out.println("ABS STATE layer 2.5: " + v.getSccIndex());
		//TODO: imple add special case the start vertex and the end vertex
		assert(v.containIndex(inport.getVertexIndex()) && v.containIndex(outport.getVertexIndex()));
		assert(thisInVar != null && thisOutVar != null && v != null);
		return this.type1ConcreteGraphPathFormula(v, inport, outport, in, out, thisInVar, thisOutVar, lastOutVar, nextInVar, isSkew);
		/*if(in == null && out != null && lastOutVar == null && nextInVar != null) {
			// start absVertex
			return this.type1ConcreteGraphPathFormula(v, inport, outport, in, out, thisInVar, thisOutVar, lastOutVar, nextInVar, isSkew);
		} else if(in != null && out == null && nextInVar == null && lastOutVar != null) {
			// end absVertex
			//System.out.println("edge:" + in.getFromVertex().getVertexIndex() + " to " + in.getToVertex().getVertexIndex());
			//System.out.println("thisInVar: " + thisInVar.toString() + " thisOutVar: " + thisOutVar.toString() );
			//System.out.println("lastOutVar: " + lastOutVar.toString() + " nextInVar: " + nextInVar.toString() );
			return this.type1ConcreteGraphPathFormula(v, inport, outport, in, out, thisInVar, thisOutVar, lastOutVar, nextInVar, isSkew);
		} else {
			assert(in != null && out != null && nextInVar != null && lastOutVar != null);
			/*System.out.println("in is null:" + (in == null));
			System.out.println("out is null:" + (out == null));
			System.out.println("nextIn is null:" + (nextInVar == null));
			System.out.println("lastOut is null:" + (lastOutVar == null));*/
			// other absVertex
			/*System.out.println("edge:" + in.getFromVertex().getVertexIndex() + " to " + in.getToVertex().getVertexIndex());
			System.out.println("thisInVar: " + thisInVar.toString() + " thisOutVar: " + thisOutVar.toString() );
			System.out.println("lastOutVar: " + lastOutVar.toString() + " nextInVar: " + nextInVar.toString() );
			return this.type1ConcreteGraphPathFormula(v, inport, outport, in, out, thisInVar, thisOutVar, lastOutVar, nextInVar, isSkew);
		}*/
	}
	
	
	
	
	//TODO: debug
	
	public BoolExpr vertexBorderEdgeWeightAndDropRequirements(BorderEdge in, BorderEdge out, IntExpr thisInVar, IntExpr thisOutVar,
																							 IntExpr lastOutVar, IntExpr nextInVar) {
		//System.out.println("vertexBorderFunc: ");
		//System.out.println(" in is null: " + (in == null) + " out is null: " + (out == null )+ "\n thisInVar is null: " + (thisInVar == null) 
		//	 + " thisOutVar is null: " + (thisOutVar == null) + " lastOutVar is null: " + (lastOutVar == null)
		//	 + "\n nextInVar is null: " + (nextInVar == null));
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
	
	//TODO: debug
	public BoolExpr startVertexBorderEdgeWeigthAndDropRequirements(BorderEdge out, 
																	IntExpr thisInVar, IntExpr thisOutVar,
																	IntExpr nextInVar) {
		BoolExpr formula = this.getQfpaGen().mkEqBool(
					this.getQfpaGen().mkSubInt(nextInVar, thisOutVar), 
					this.getQfpaGen().mkConstantInt(out.getWeight()));
		return formula;
	}
	//TODO: debug
	public BoolExpr endVertexBorderEdgeWeightAndDropRequirements(BorderEdge in,
																  IntExpr thisInVar, IntExpr thisOutVar,
																  IntExpr lastOutVar) {
		BoolExpr formula = this.getQfpaGen().mkEqBool(
					this.getQfpaGen().mkSubInt(thisInVar, lastOutVar), 
					this.getQfpaGen().mkConstantInt(in.getWeight()));
		return formula;
	}
	
	//TODO: debug
		public BoolExpr midBorderEdgeWeightAndDropRequirements(BorderEdge in, BorderEdge out, 
														IntExpr thisInVar, IntExpr thisOutVar, 
														IntExpr lastOutVar, IntExpr nextInVar) {
			BoolExpr formula = this.getQfpaGen().mkAndBool(
					// border edge weight add correctly
					this.getQfpaGen().mkEqBool(
						this.getQfpaGen().mkSubInt(thisInVar, lastOutVar), 
						this.getQfpaGen().mkConstantInt(in.getWeight())),
					// border edge weight add correctly
					//TODO: the formula can be redundant here
					this.getQfpaGen().mkEqBool(
						this.getQfpaGen().mkSubInt(nextInVar, thisOutVar), 
						this.getQfpaGen().mkConstantInt(out.getWeight()))
			);
			return formula;
		}
	
	/* Cases:
	 * 1. when ASDGVertex v to dgraph is a single vertex
	 * 2. when ASDGVertex v to dgraph is scc with the same inport and outport
	 * 3. when ASDGVertex v to dgraph is scc with different inport and outport 
	 */
	public List<BoolExpr> type1ConcreteGraphPathFormula(ASDGVertex v, SDGVertex inport, SDGVertex outport, 
			   										 BorderEdge in, BorderEdge out, 
			   										 IntExpr thisInVar,  IntExpr thisOutVar,
			   										 IntExpr lastOutVar, IntExpr nextInVar, boolean isSkew) {
		
		List<BoolExpr> exprs = new ArrayList<BoolExpr>();
		DGraph conGraph = v.getConcreteDGraph();
		// special case 1
		if(conGraph.getVertices().size() == 1 && conGraph.getVertices().get(0).getEdges().size() == 0) {
			// if the scc a plain vertex
			BoolExpr formula = this.getQfpaGen().mkAndBool(
				this.vertexBorderEdgeWeightAndDropRequirements(in, out, thisInVar, thisOutVar, lastOutVar, nextInVar),
				this.getQfpaGen().mkEqBool(thisOutVar, thisInVar)
			);
			exprs.add(formula);
			return exprs;
		} 
		
		// case 2 and 3
		System.out.println("NOT PLAIN " + inport.getVertexIndex() + " " + outport.getVertexIndex());
		List<DGraph> supports = conGraph.getAllPossibleSupport(inport.getVertexIndex(), outport.getVertexIndex());
		System.out.println("Support size: " + supports.size());
		for(DGraph support : supports) {
			System.out.println("SUPPORTS ENUM layer 3: ");
			// if there is a positive cycle in the support ignore it
			if(support.computeLoopTag() == LoopTag.Pos || support.computeLoopTag() == LoopTag.PosNeg) {
				System.out.println("contain pos cycle return null");
				continue;
			}
			if(support.containsCycle()) {
				//TODO: debug
				// increase the max length to 2n^2 + 1
				support.increaseDWTLenLimit();
				// guess that there is a cycle and apply the lemma
				// length > 2n^2
				BoolExpr formGe = this.getQfpaGen().mkFalse();
				// guess the mid vertex and init a int variable for it
				for(DGVertex ve : support.getVertices()) {
					IntExpr midVar = this.getQfpaGen().mkVariableInt("z"+ ve.getIndex());
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
							// z > |V|, this guarantee the counter value to be positive
							this.getQfpaGen().mkGeBool(midVar, this.getQfpaGen().mkConstantInt(support.getVertices().size())),
							formGeDynamic
						)
					);
					
					
					// add border edge requirements
					
					formGe = this.getQfpaGen().mkAndBool(formGe,
							this.vertexBorderEdgeWeightAndDropRequirements(in, out, thisInVar, thisOutVar, lastOutVar, nextInVar));
					
					// add positive requirement
					
					formGe = this.getQfpaGen().mkAndBool(this.getQfpaGen().mkRequireNonNeg(midVar), formGe);
					
					// add existential 
					IntExpr[] midVarQuant = new IntExpr[1];
					midVarQuant[0] = midVar;
					formGe = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(midVarQuant, formGe);
				}
				// length < 2n^2 + 1
				BoolExpr formLt = this.type1DynamicPartFormula(support, inport, outport, thisInVar, thisOutVar);
				
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
				
				
				// guess it is a simple path
				BoolExpr formula = this.getQfpaGen().mkAndBool(
					this.vertexBorderEdgeWeightAndDropRequirements(in, out, thisInVar, thisOutVar, lastOutVar, nextInVar),
					// there is a concrete path from inport to outport in concreteScc
					concretePathFormula
				);
				exprs.add(formula);
			}
		}
		return exprs;
	}
	
	public BoolExpr type1DynamicPartFormula(DGraph support, SDGVertex inport, SDGVertex outport, 
															IntExpr thisInVar, IntExpr thisOutVar) {
		System.out.println("dynamic from " +   inport.getVertexIndex() + " to " + outport.getVertexIndex());
		BoolExpr formDynamic = this.getQfpaGen().mkFalse();
		if(inport.getVertexIndex() == outport.getVertexIndex()) {
			// if inport and outport is the same vertex, it is  naturally reachable
			System.out.println("same in out");
			formDynamic = this.getQfpaGen().mkEqBool(thisInVar, thisOutVar);
			if(support.getTable().getEntry(inport.getVertexIndex(), outport.getVertexIndex()) == null) {
				// if the entry does not exists, return the natural one
				return formDynamic;
			}
		} else {
			System.out.println("different in out");
			formDynamic = this.getQfpaGen().mkFalse();
			if(support.getTable().getEntry(inport.getVertexIndex(), outport.getVertexIndex()) == null) {
				// if the entry does not exists, ignore the support
				return null;
			}
		}
		
		System.out.println("table entry null:" + (support.getTable().getEntry(inport.getVertexIndex(), outport.getVertexIndex()) == null) + " table len:" +  support.getTable().getMaxLength());
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
		//TODO debug
		System.out.println("genPathFlow: " + startIndex + " to " + endIndex);
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
			// different start and end 
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
			// same start and end
			} else {
				// following  formulae should be the same 
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
			// drop requirements are guranteed by the 3n^2 + 1 or type3 certificate
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
	//TODO: debug
	public List<IntExpr> getAllFlowInVars(DGVertex v, DGFlowTuple[] flowTuples){
		List<IntExpr> inVars = new ArrayList<IntExpr>();
		for(DGFlowTuple t : flowTuples) {
			if(t.getEdgeTo().getIndex() == v.getIndex()) {
				inVars.add(t.getEdgeVar());
			}
		}
		return inVars;
	}
	//TODO: debug
	public List<IntExpr> getAllFlowOutVars(DGVertex v, DGFlowTuple[] flowTuples){
		List<IntExpr> outVars = new ArrayList<IntExpr>();
		for(DGFlowTuple t : flowTuples) {
			if(t.getEdgeFrom().getIndex() == v.getIndex()) {
				outVars.add(t.getEdgeVar());
			}
		}
		return outVars;
	}	
	
	//TODO: debug
	public BoolExpr genType12Formulae(ASDGPath p, int startIndex, int endIndex,
												   IntExpr startVar, IntExpr endVar) {
		// assert that there exists a postive tag absVertex
		assert(p.containsPosTagVertex());
		List<ASDGVertex> posVertices = p.getAllType12Split();
		// split the absPath
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
				// for all the possible splited borderedges
				BoolExpr borderEdgesFormula = this.getQfpaGen().mkFalse();
				for(BorderEdge e : p.getG().getBorderEdgesByAbsEdge(paths[0].getLastVertex().getSccIndex(), 
																	paths[1].getInit().getSccIndex())) {
					BoolExpr expr1 = this.genType1Formulae(paths[0], startIndex, e.getFromVertex().getVertexIndex(), startVar, splitVars[0], false);
					BoolExpr expr2 = this.genType1Formulae(paths[1], e.getToVertex().getVertexIndex(), endIndex, splitVars[1], endVar, true);
					System.out.println("type1 of type12: " + expr1.toString());
					System.out.println("type2 of type12: " + expr2.toString());
					
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
				System.out.println(" path 0 is null: " + (paths[0] == null));
				System.out.println("type2 of type12 : " + type12Form.toString());
			}
		}
		type12Form = hasBorder ? (BoolExpr) this.getQfpaGen().mkExistsQuantifier(splitVars, type12Form) : type12Form;
		return type12Form;
	}
	
	
	
	public BoolExpr genType132Formulae(ASDGPath p, int startIndex, int endIndex, 
												 IntExpr startVar, IntExpr endVar) {
		//TODO: debug
		// assert there is a condition for type132
		assert(p.containsNegTagVertex() && p.containsNegTagVertex());
		List<ASDGVertex[]> splitVertices = p.getAllType132Split();
		BoolExpr type132Form = this.getQfpaGen().mkFalse();
		//132 mid vars
		IntExpr[] fullSplitVars = new IntExpr[4];
		fullSplitVars[0] = this.getQfpaGen().mkVariableInt("vt_1");
		fullSplitVars[1] = this.getQfpaGen().mkVariableInt("vs_3");
		fullSplitVars[2] = this.getQfpaGen().mkVariableInt("vt_3");
		fullSplitVars[3] = this.getQfpaGen().mkVariableInt("vs_2");
		//13 mid vars
		IntExpr[] splitVars13 = new IntExpr[2];
		splitVars13[0] = fullSplitVars[0];
		splitVars13[1] = fullSplitVars[1];
		//32 mid vars
		IntExpr[] splitVars32 = new IntExpr[2];
		splitVars32[0] = fullSplitVars[2];
		splitVars32[1] = fullSplitVars[3];
		
		for(ASDGVertex[] s : splitVertices) {
			System.out.println("HERERERERE " );
			System.out.println("split Vertices form gen: " + s[0].getSccIndex() + " & " + s[1].getSccIndex());
			ASDGPath[] paths = p.getAllType132SplitPaths(s);
			BoolExpr portForm = this.getQfpaGen().mkTrue();
			assert(paths[1] != null);
			if(paths[0] != null && paths[1] != null && paths[2] != null) {
				//132
				System.out.println("subtype 132 gen");
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
				//13
				System.out.println("subtype 13 gen");System.out.println("HERERERERE 13 " + p.getType132LinkInportOutport(s).size());
				for(SDGVertex[] inouts : p.getType132LinkInportOutport(s)) {

					System.out.println("HERERERERE 13" );
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
				//32
				System.out.println("subtype 32 gen");
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
				//3
				System.out.println("subtype 3 gen");
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
	
	//TODO: debug
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
			System.out.println("vms");
			startPosTemp = this.getQfpaGen().mkOrBool(startPosTemp, this.genPosCycleTemplateFormula(p3Graph, startIndex, vms.getIndex(), startVar, startMidVar));
		}
		for(DGVertex vme : p3SkewGraph.getVertices()) {
			System.out.println("vme");
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
		// generate the graph
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
	
	//TODO: formula for postive cycle template
	
	public BoolExpr genPosCycleTemplateFormula(DGraph g, int startIndex, int guessIndex, IntExpr startVar, IntExpr guessVar) {
		//TODO: DEBUG REDUNDANT 
		// desciption: return the possible positive cycle template with the minimum drop, if none return null
		assert(g.containsVertex(startIndex) && g.containsVertex(guessIndex));
		if(g.getTag() == null) {
			g.computeLoopTag();
		}
		BoolExpr form = this.getQfpaGen().mkFalse();
		DWTEntry startToGuessEntry = g.getTable().getEntry(startIndex, guessIndex);
		if(startToGuessEntry != null) {
			for(DWTuple t : startToGuessEntry.getSetOfDWTuples()) {
				DWTEntry guessLoopEntry = g.getTable().getEntry(guessIndex, guessIndex);
				if(guessLoopEntry != null) {
					for(DWTuple tg : guessLoopEntry.getSetOfDWTuples()) {
						if(tg.getWeight() > 0) {
							int drop = Math.min(t.getDrop(), tg.getDrop() + t.getWeight());
							form = this.getQfpaGen().mkOrBool(
								form,
								this.getQfpaGen().mkAndBool(
									this.getQfpaGen().mkGeBool(this.getQfpaGen().mkAddInt(startVar, this.getQfpaGen().mkConstantInt(drop)), this.getQfpaGen().mkConstantInt(0)),
									this.getQfpaGen().mkEqBool(this.getQfpaGen().mkAddInt(startVar, this.getQfpaGen().mkConstantInt(t.getWeight())), guessVar)
								)
							);
						} 
					}
				}
			}
		}
		return form;
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
