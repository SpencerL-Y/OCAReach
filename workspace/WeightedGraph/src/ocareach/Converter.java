package ocareach;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.z3.*;

import automata.State;
import automata.counter.OCA;
import formula.generator.QFPAGenerator;
import graph.directed.DGPath;
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
	// TODO: imple
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
	public String convert(State startState, State endState) {
		assert(this.getOca().containState(startState) && this.getOca().containState(endState));
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
		List<Expr> formulae = new ArrayList<Expr>();
		for(ASDGPath p : paths) {
			// there is no cycles in the SCCs (trivial case: every scc is a concrete vertex)
			boolean trivial = !p.containsCycledVertex();
			// there might be type-1 certificate
			boolean type1 = p.containsNegTagVertex();
			// there might be type-1 . type-2 certificate
			boolean type12 = p.containsPosTagVertex();
			// there might be type-1 . type-3 . type-2 certificate
			boolean type132 = p.containsNegTagVertex() && p.containsPosTagVertex();
			Expr trivialForm = null;
			List<Expr> type1Forms = new ArrayList<Expr>();
			List<Expr> type12Forms = new ArrayList<Expr>();
			List<Expr> type132Forms = new ArrayList<Expr>();
			if(trivial) {
				trivialForm = this.genTrivialFormula(p);
			}
			if(type1) {
				this.genType1Formulae(p, type1Forms, startState.getIndex(), endState.getIndex());
			}
			if(type12) {
				this.genType12Formulae(p, type12Forms);
			}
			if(type132) {
				this.genType132Formula(p, type132Forms);
			}
			Expr temp = (trivial)? trivialForm : this.combineAllFormlae(type1Forms, type12Forms, type132Forms);
			formulae.add(temp);
		}
		
		
		String result = null;
		
		//TODO imple
		return result;
	}
	
	private Expr genTrivialFormula(ASDGPath p) {
		//TODO debug
		// convert abstract vertices to concrete vertices
		DGPath cp = new DGPath(p.getVertex(0).getConcreteDGraph().getVertices().get(0));
		for(int i = 1; i <= p.length(); i++) {
			cp.concatVertex(p.getVertex(i).getConcreteDGraph().getVertices().get(0));
		}
		IntExpr sVar = this.getQfpaGen().mkVariableInt("xs");
		IntExpr tVar = this.getQfpaGen().mkVariableInt("xt");
		Expr result = this.getQfpaGen()
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
	private void genType1Formulae(ASDGPath p, List<Expr> type1Forms, int startIndex, int endIndex) {
		//TODO imple
		//assertion
		IntExpr sVar = this.getQfpaGen().mkVariableInt("xs");
		IntExpr tVar = this.getQfpaGen().mkVariableInt("xt");
		List<List<SDGVertex>> allPossibleInOut = p.inportsOutportsCartesianProduct(p.getG().getSdg().getVertex(startIndex),
																				   p.getG().getSdg().getVertex(endIndex));
		int inOutSeqSize = allPossibleInOut.get(0).size();
		IntExpr[] absPathVars = new IntExpr[l.size() - 2];
		for(int i = 0; i < l.size() - 2; i ++) {
			if(i % 2 == 0) {
				absPathVars[i] = this.getQfpaGen().mkVariableInt("v_o_" + p.getVertex((i)/2).getSccIndex());
			} else {
				absPathVars[i] = this.getQfpaGen().mkVariableInt("v_i_" + p.getVertex((i+1)/2).getSccIndex());
			}
		}
		for(List<SDGVertex> l : allPossibleInOut) {
			// for every possible sequence of inoutports
			Expr type1Form = this.getQfpaGen().mkTrue();
			for(int i = 0; i <= p.length(); i ++) {
				if(i == 0) {
					List<Expr> sccExprs = this.genAbsStateNoPosCycle(p.getVertex(i), 
						p.getG().getSdg().getVertex(startIndex), l.get(i/2),
						null, p.getG().getBorderEdgesByAbsEdge(l.get(i/2).getSccMark(), l.get((i+1)/2).getSccMark()),
						sVar, absPathVars[i], 
						null, absPathVars[i+1]);
				} else if(i == p.length()) {
					// TODO: imple BREAKPOINT, CHECK THE ARGUMENTS..
					List<Expr> sccExprs = this.genAbsStateNoPosCycle(p.getVertex(i), 
						l.get((i-1)/2), p.getG().getSdg().getVertex(endIndex),
						p.getG().getBorderEdgesByAbsEdge(l.get(i/2).getSccMark(), l.get), ),
						sVar, absPathVars[i], 
						null, absPathVars[i+1]);
				}
			}
		}
		
	}
	
	//TODO: imple add variable positive requirement
	private List<Expr> genAbsStateNoPosCycle(ASDGVertex v, SDGVertex inport, SDGVertex outport, 
														   BorderEdge in, BorderEdge out, 
														   IntExpr thisInVar,  IntExpr thisOutVar,
														   IntExpr lastOutVar, IntExpr nextInVar) {
		//TODO: imple add special case the start vertex and the end vertex
		assert(v.containIndex(inport.getVertexIndex()) && v.containIndex(outport.getVertexIndex()));
		List<Expr> exprs = new ArrayList<Expr>();
		DGraph conGraph = v.getConcreteDGraph();
		if(conGraph.getVertices().size() == 1) {
			// if the scc is trivial
			Expr formula = this.getQfpaGen().mkAndBool(
				this.borderEdgeWeightAndDropRequirements(in, out, thisInVar, thisOutVar, nextInVar, lastOutVar),
				// border edge weight add correctly
				//TODO: the formula can be redundant here
				this.getQfpaGen().mkEqBool(
					this.getQfpaGen().mkSubInt(nextInVar, thisOutVar), 
					this.getQfpaGen().mkConstantInt(out.getWeight()))
			);
			exprs.add(formula);
			return exprs;
		}
		List<DGraph> supports = conGraph.getAllPossibleSupport(inport.getVertexIndex(), outport.getVertexIndex());
		for(DGraph support : supports) {
			// assert there is no positive cycle in the support
			assert(support.computeLoopTag() != LoopTag.Pos && support.computeLoopTag() != LoopTag.PosNeg);
			if(support.containsCycle()) {
				//TODO: correctness check
				// increase the max length to 3n^2 + 1
				support.increaseDWTLenLimit();
				// guess that there is a cycle and apply the lemma
				// length <= 3n^2 + 1
				Expr formLt = this.getQfpaGen().mkFalse();
				for(DWTuple t : support.getTable().getEntry(inport.getVertexIndex(), outport.getVertexIndex()).getSetOfDWTuples()) {
					formLt = this.getQfpaGen().mkAndBool(
						// weight sum correctly in the concreteScc
						this.getQfpaGen().mkEqBool(
							thisOutVar, 
							this.getQfpaGen().mkAddInt(thisInVar, this.getQfpaGen().mkConstantInt(t.getWeight()))),
							// the minimum counter value >= 0
						this.getQfpaGen().mkGeBool(
								this.getQfpaGen().mkAddInt(thisInVar, this.getQfpaGen().mkConstantInt(t.getDrop())), 
								this.getQfpaGen().mkConstantInt(0))
					);
				}
			} else {
				BoolExpr concretePathFormula = this.getQfpaGen().mkFalse();
				DWTEntry entry = support.getTable().getEntry(inport.getVertexIndex(), outport.getVertexIndex());
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
				// guess it is a simple path
				Expr formula = this.getQfpaGen().mkAndBool(
					this.borderEdgeWeightAndDropRequirements(in, out, thisInVar, thisOutVar, nextInVar, lastOutVar),
					// there is a concrete path from inport to outport in concreteScc
					concretePathFormula
				);
				exprs.add(formula);
			}
		}
		return exprs;
	}
	
	private BoolExpr borderEdgeWeightAndDropRequirements(BorderEdge in, BorderEdge out, 
													IntExpr thisInVar, IntExpr thisOutVar, 
													IntExpr nextInVar, IntExpr lastOutVar) {
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
	
	private void genType12Formulae(ASDGPath p, List<Expr> type12Forms) {
		//TODO imple
	}
	
	private void genType132Formula(ASDGPath p, List<Expr> type132Forms) {
		//TODO imple
	}
	
	private Expr combineAllFormlae(List<Expr> type1, List<Expr> type12, List<Expr> type132) {
		//TODO imple
		return null;
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
