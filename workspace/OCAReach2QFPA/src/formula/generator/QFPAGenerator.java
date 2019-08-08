package formula.generator;
import java.util.List;

import com.microsoft.z3.*;

public class QFPAGenerator {
	// by doing so we restrict Z3 on logic class  PA
	//TODO debug
	private Context ctx;
	private Sort intSort;
	private Sort boolSort;
	
	public QFPAGenerator() {
		this.ctx = new Context();
		this.intSort = this.getCtx().getIntSort();
		this.boolSort = this.getCtx().getBoolSort();
	}
	
	// operations
	// methods naming: mk+$NAME+Sort
	// integer expresions
	public IntExpr mkVariableInt(String name) {
		return (IntExpr)this.getCtx().mkConst(name, this.getIntSort());
	}
	
	public IntExpr mkConstantInt(int i) {
		return this.getCtx().mkInt(i);
	}
	

	public BoolExpr mkTrue() {
		return this.getCtx().mkTrue();
	}
	
	public BoolExpr mkFalse() {
		return this.getCtx().mkFalse();
	}
	
	public IntExpr mkAddInt(IntExpr exp1, IntExpr exp2) {
		return (IntExpr) this.getCtx().mkAdd(exp1, exp2);
	}
	
	public IntExpr mkSubInt(IntExpr exp1, IntExpr exp2) {
		return (IntExpr) this.getCtx().mkSub(exp1, exp2);
	}
	
	public IntExpr mkScalarTimes(IntExpr constant, IntExpr exp) {
		return (IntExpr) this.getCtx().mkMul(constant, exp);
	}
	//TODO: debug
	public IntExpr sumUpVars(List<IntExpr> l) {
		IntExpr result = this.getCtx().mkInt(0);
		for(IntExpr e : l) {
			result = this.mkAddInt(e, result);
		}
		return result;
	}
	// binary relations
	public BoolExpr mkEqBool(IntExpr left, IntExpr right) {
		return this.getCtx().mkEq(left, right);
	}
	public BoolExpr mkGeBool(IntExpr left, IntExpr right) {
		return this.getCtx().mkGe(left, right);
	}
	public BoolExpr mkGtBool(IntExpr left, IntExpr right) {
		return this.getCtx().mkGt(left, right);
	}
	public BoolExpr mkLeBool(IntExpr left, IntExpr right) {
		return this.getCtx().mkLe(left, right);
	}
	public BoolExpr mkLtBool(IntExpr left, IntExpr right) {
		return this.getCtx().mkLt(left, right);
	}
	//speciala
	public BoolExpr mkRequireNonNeg(IntExpr var) {
		return this.mkGeBool(var, this.mkConstantInt(0));
	}
	
	public BoolExpr mkNotEqual(IntExpr left, IntExpr right) {
		return this.getCtx().mkNot(this.getCtx().mkEq(left, right));
	}
	
	// propositional logic operation
	
	public BoolExpr mkNotBool(BoolExpr arg) {
		return this.getCtx().mkNot(arg);
	}
	
	public BoolExpr mkAndBool(BoolExpr... arg0) {
		return this.getCtx().mkAnd(arg0);
	}
	
	public BoolExpr mkOrBool(BoolExpr... arg0) {
		return this.getCtx().mkOr(arg0);
	}
	
	public BoolExpr mkImplies(BoolExpr left, BoolExpr right) {
		return this.getCtx().mkImplies(left, right);
	}
	
	// Quantifier
	
	public Expr mkForallQuantifier(IntExpr[] boundVars, Expr bodyFormula) {
		return this.getCtx().mkForall(boundVars, bodyFormula, 1, null, null, null, null);
	}
	
	public Expr mkExistsQuantifier(IntExpr[] boundVars, Expr bodyFormula) {
		return this.getCtx().mkExists(boundVars, bodyFormula, 1, null, null, null, null);
	}
	//getters and setters
	public Context getCtx() {
		return ctx;
	}
	public void setCtx(Context ctx) {
		this.ctx = ctx;
	}
	public Sort getIntSort() {
		return intSort;
	}
	public void setIntSort(Sort intSort) {
		this.intSort = intSort;
	}
	public Sort getBoolSort() {
		return boolSort;
	}
	public void setBoolSort(Sort boolSort) {
		this.boolSort = boolSort;
	}
}
