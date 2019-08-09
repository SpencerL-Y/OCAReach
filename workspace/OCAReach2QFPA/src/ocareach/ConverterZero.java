package ocareach;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.IntExpr;

import automata.State;
import automata.counter.OCA;
import graph.directed.DGraph;
import graph.directed.zerograph.ZTPath;
import graph.directed.zerograph.ZTVertex;
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
		IntExpr[] vertexVars = new IntExpr[z.getVerticesNum()];
		IntExpr[] counterVars = new IntExpr[2*z.getVerticesNum() - 2];
		int startIndex = -1;
		int endIndex = -1;
		for(int i = 0; i < z.getVerticesNum(); i++) {
			vertexVars[i] = this.getConverter().getQfpaGen().mkVariableInt("z_"+ startState.getIndex() + "_" + endState.getIndex() + "_" + 
							z.getVertices().get(i).getIndex());
			if(z.getVertices().get(i).isTargetVertex()) {
				endIndex = i;
			} else if(z.getVertices().get(i).isInitVertex()) {
				startIndex = i;
			} 
		}
		counterVars[0] = this.getConverter().getQfpaGen().mkVariableInt("xs");
		counterVars[1] = this.getConverter().getQfpaGen().mkVariableInt("xt");
		IntExpr[] existsCounterVars = new IntExpr[1];
		List<IntExpr> existsArray = new ArrayList<IntExpr>();
		BoolExpr dfsForm = this.getConverter().getQfpaGen().mkEqBool(vertexVars[startIndex], this.getConverter().getQfpaGen().mkConstantInt(1));
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
		System.out.println("startIndex: " + startIndex + " endIndex: " + endIndex);
		for(int i = 0; i < z.getVerticesNum(); i++) {
			if(i != startIndex) {
				BoolExpr onThePath = this.getConverter().getQfpaGen().mkGtBool(vertexVars[i], this.getConverter().getQfpaGen().mkConstantInt(0));
				BoolExpr hasLessNumForm = this.getConverter().getQfpaGen().mkFalse();
				
				for(int j = 0; j < z.getVerticesNum(); j++) {
					if(j != i && z.containsEdge(z.getVertex(j).getIndex(), z.getVertex(i).getIndex())) {
						System.out.println("ContainEdge: " + z.getVertex(j).getIndex() + " " + z.getVertex(i).getIndex());
						BoolExpr partForm = null;
						if(j == startIndex) {
							if(i == endIndex) {
								System.out.println("HERE 1");
								partForm = this.getConverter().convertToForm(this.getConverter().getOca().getState(z.getVertex(j).getTo()),	
																		     this.getConverter().getOca().getState(z.getVertex(i).getFrom()),
																		     counterVars[0], 
																		     counterVars[1]);
							} else {
								System.out.println("HERE 2 " + z.getVertex(j).getTo() + " " + z.getVertex(i).getFrom());
								IntExpr newVar = this.getConverter().getQfpaGen().mkVariableInt("zv_t_" + z.getVertex(i).getFrom());
								existsArray.add(newVar);
								partForm = this.getConverter().convertToForm(this.getConverter().getOca().getState(z.getVertex(j).getTo()),
																		     this.getConverter().getOca().getState(z.getVertex(i).getFrom()),
																		     counterVars[0], 
																		     newVar);
							}
						} else if (j == endIndex){
							System.out.println("HERE 3 " + z.getVertex(j).getTo() + " " + z.getVertex(i).getFrom());
							IntExpr newVar1 = this.getConverter().getQfpaGen().mkVariableInt("zv_s_" + z.getVertex(j).getFrom());
							IntExpr newVar2 = this.getConverter().getQfpaGen().mkVariableInt("zv_t_" + z.getVertex(i).getFrom());
							existsArray.add(newVar1); existsArray.add(newVar2);
							partForm = this.getConverter().convertToForm(this.getConverter().getOca().getState(z.getVertex(j).getFrom()), 
	 									 								 this.getConverter().getOca().getState(z.getVertex(i).getFrom()), 
	 									 								 newVar1, 
	 									 								 newVar2);
						} else {
							if(i == endIndex) {
								System.out.println("HERE 4 " + z.getVertex(j).getTo() + " " + z.getVertex(i).getFrom());
								IntExpr newVar = this.getConverter().getQfpaGen().mkVariableInt("zv_s_" + z.getVertex(j).getTo());
								existsArray.add(newVar);
								partForm = this.getConverter().convertToForm(this.getConverter().getOca().getState(z.getVertex(j).getTo()), 
																			 this.getConverter().getOca().getState(z.getVertex(i).getFrom()), 
																			 newVar, 
																			 counterVars[1]);
							} else {
								System.out.println("HERE 5 " + z.getVertex(j).getTo() + " " + z.getVertex(i).getFrom());
								IntExpr newVar1 = this.getConverter().getQfpaGen().mkVariableInt("zv_s_" + z.getVertex(j).getTo());
								IntExpr newVar2 = this.getConverter().getQfpaGen().mkVariableInt("zv_t_" + z.getVertex(i).getFrom());
								existsArray.add(newVar1); existsArray.add(newVar2);
								partForm = this.getConverter().convertToForm(this.getConverter().getOca().getState(z.getVertex(j).getTo()), 
	 									 									 this.getConverter().getOca().getState(z.getVertex(i).getFrom()), 
	 									 									 newVar1, 
	 									 									 newVar2);
							}
						}
						
						System.out.println("PART: " + partForm.toString());
				
						
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
		IntExpr[] a = new IntExpr[existsArray.size()];
		reachForm = (BoolExpr) this.getConverter().getQfpaGen().mkExistsQuantifier(existsArray.toArray(a), reachForm);
		reachForm = (BoolExpr) this.getConverter().getQfpaGen().mkExistsQuantifier(vertexVars, reachForm);
		
		return reachForm.toString();
	}
	
	
	public String convertZeroNaive(State startState, State endState) {
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

	public ConverterOpt getConverter() {
		return converter;
	}

	public void setConverter(ConverterOpt converter) {
		this.converter = converter;
	}
}
