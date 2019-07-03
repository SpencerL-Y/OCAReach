package ocareach;

import java.util.List;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.IntExpr;

import automata.State;
import automata.counter.OCA;
import graph.directed.DGraph;
import graph.directed.zerograph.ZTPath;
import graph.directed.zerograph.ZeroEdgeDGraph;

public class ConverterZero extends ConverterOpt{
	// add ConverterOpt as a property
	public ConverterZero(OCA oca) {
		super(oca);
	}
	
	@Override
	public String convert() {
		if(this.oca.containsZeroTransition()) {
			return this.convertZero(this.oca.getInitState(), this.oca.getTargetState());
		} else {
			IntExpr sVar = this.getQfpaGen().mkVariableInt("xs");
			IntExpr tVar = this.getQfpaGen().mkVariableInt("xt");
			return this.convert(this.oca.getInitState(), this.oca.getTargetState(), sVar, tVar);
			
		}
	}
	
	public String convertZero(State startState, State endState) {
		ZeroEdgeDGraph z = new ZeroEdgeDGraph(this.getDgraph());
		List<ZTPath> ztPaths = z.dfsFindAllZTPath();
		BoolExpr resultExpr = this.getQfpaGen().mkFalse();
		IntExpr sVar = this.getQfpaGen().mkVariableInt("xs");
		IntExpr tVar = this.getQfpaGen().mkVariableInt("xt");
		for(ZTPath zp : ztPaths) {
			
		}
		return null;
	}
	
	
	//TODO: DEBUG start vertex and target vertex are the same in the OCA
	public BoolExpr genZTPathForm(ZTPath p, IntExpr sVar, IntExpr tVar) {
		BoolExpr pathResult = this.getQfpaGen().mkTrue();
		IntExpr[] midVars = new IntExpr[2*(p.getPath().size())];
		midVars[0] = sVar;
		for(int i = 0; i < p.getPath().size(); i++) {
			midVars[2*i+1] = this.getQfpaGen().mkVariableInt("zv_t_" + p.getVertex(i).getIndex());
			midVars[2*(i+1)] = this.getQfpaGen().mkVariableInt("zv_s_" + p.getVertex(i).getIndex());
		}
		midVars[2*p.getPath().size() - 1] = tVar;
		IntExpr[] boundVars = new IntExpr[2*(p.getPath().size() - 1)];
		for(int i = 1; i < p.getPath().size() - 1; i++) {
			boundVars[i - 1] = midVars[i];
		}
		ConverterOpt tempConvert = new ConverterOpt(this.getOca().removeZeroTransitionOCA());
		if(p.getPath().size() < 2) {
			System.out.println("ERROR: ZTPath length < 2");
			return null;
		} 
		if(p.getPath().size() == 2) {
			// if the path does not contain any zero vertex
			pathResult = tempConvert.convertToForm(tempConvert.getOca().getState(p.getInitVertex().getTo()),
					                               tempConvert.getOca().getState(p.getLastVertex().getFrom()),
					                               sVar, tVar);
		} else {
			for(int i = 0; i < p.getPath().size(); i++) {
				pathResult = this.getQfpaGen().mkAndBool(
							pathResult,
							tempConvert.convertToForm(tempConvert.getOca().getState(p.getVertex(i).getTo()), 
								              		  tempConvert.getOca().getState(p.getVertex(i+1).getFrom()),
								              		  midVars[2*i], midVars[2*i+1])
							
				);
				if(i == 0) {
					pathResult = this.getQfpaGen().mkAndBool(
						pathResult,
						this.getQfpaGen().mkEqBool(midVars[2*i + 1], this.getQfpaGen().mkConstantInt(0))
					);
				} else if(i == p.getPath().size() - 1) {
					pathResult = this.getQfpaGen().mkAndBool(
						pathResult,
						this.getQfpaGen().mkEqBool(midVars[2*i], this.getQfpaGen().mkConstantInt(0))
					);
				} else {
					pathResult = this.getQfpaGen().mkAndBool(
						pathResult,
						this.getQfpaGen().mkEqBool(midVars[2*i], this.getQfpaGen().mkConstantInt(0)),
						this.getQfpaGen().mkEqBool(midVars[2*i + 1], this.getQfpaGen().mkConstantInt(0))
					);
				}
			}
		}
		pathResult = (BoolExpr) this.getQfpaGen().mkExistsQuantifier(boundVars, pathResult);
		return pathResult;
	}
}
