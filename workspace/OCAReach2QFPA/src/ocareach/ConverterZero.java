package ocareach;

import java.util.List;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.IntExpr;

import automata.State;
import automata.counter.OCA;
import graph.directed.DGraph;
import graph.directed.zerograph.ZTPath;
import graph.directed.zerograph.ZeroEdgeDGraph;

public class ConverterZero {
	//TODO: DEBUG
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
		System.out.println("ConvertZero");
		ZeroEdgeDGraph z = new ZeroEdgeDGraph(this.getOriginOCA().toDGraph());
		List<ZTPath> ztPaths = z.dfsFindAllZTPath();
		for(ZTPath p : ztPaths) {
			p.print();
		}
		BoolExpr resultExpr = this.converter.getQfpaGen().mkFalse();
		IntExpr sVar = this.converter.getQfpaGen().mkVariableInt("xs");
		IntExpr tVar = this.converter.getQfpaGen().mkVariableInt("xt");
		for(ZTPath zp : ztPaths) {
			System.out.print("p form: ");
			zp.print();
			resultExpr = this.converter.getQfpaGen().mkOrBool(
				resultExpr,
				this.genZTPathForm(zp, sVar, tVar)
			);
			
		}
		return resultExpr.toString();
	}
	
	
	//TODO: DEBUG start vertex and target vertex are the same in the OCA
	public BoolExpr genZTPathForm(ZTPath p, IntExpr sVar, IntExpr tVar) {
		System.out.println("genZTPathForm");
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
			System.out.println("one step");
			// if the path does not contain any zero vertex
			pathResult = this.converter.convertToForm(this.converter.oca.getState(p.getInitVertex().getTo()),
					this.converter.oca.getState(p.getLastVertex().getFrom()),
													  sVar, tVar);
			System.out.println("finished");
		} else {

			System.out.println("more steps");
			for(int i = 0; i < p.getPath().size()-1; i++) {
				pathResult = this.converter.getQfpaGen().mkAndBool(
							pathResult,
							this.converter.convertToForm(this.converter.oca.getState(p.getVertex(i).getTo()), 
											   			 this.converter.oca.getState(p.getVertex(i+1).getFrom()),
											   			 midVars[2*i], midVars[2*i+1])
							
				);System.out.println("make and");
			}
			System.out.println("existstence");
			pathResult = (BoolExpr) this.converter.getQfpaGen().mkExistsQuantifier(boundVars, pathResult);
			System.out.println("finished");
		}
		return pathResult;
	}

	public OCA getOriginOCA() {
		return originOCA;
	}

	public void setOriginOCA(OCA originOCA) {
		this.originOCA = originOCA;
	}

	public ConverterOpt getConvert() {
		return converter;
	}

	public void setConvert(ConverterOpt converter) {
		this.converter = converter;
	}
}
