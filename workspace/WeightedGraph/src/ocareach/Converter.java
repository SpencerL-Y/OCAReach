package ocareach;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.z3.Expr;

import automata.State;
import automata.counter.OCA;
import formula.generator.QFPAGenerator;
import graph.directed.DGraph;
import graph.directed.SDGraph;
import graph.directed.abs.ASDGPath;
import graph.directed.abs.ASDGVertex;
import graph.directed.abs.ASDGraph;

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
				this.genType1Formulae(p, type1Forms);
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
		//TODO imple
		return null;
	}
	
	private void genType1Formulae(ASDGPath p, List<Expr> type1Forms) {
		//TODO imple
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
