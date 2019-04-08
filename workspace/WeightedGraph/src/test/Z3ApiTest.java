package test;

import com.microsoft.z3.*;

public class Z3ApiTest {
	/*
	 * This is a tutorial of QFPA for Z3 java api
	 * - Context: A context manages all other Z3 objects, 
	 * global configurations options, etc.
	 * - Sort: implements type information for ASTs(definition of Sort: smt-lib 2.0)
	 * - Expr: Use to represent formlas and terms. For Z3, a formula is any exression of sort Boolean
	 * Every expression has a sort.
	 * 		extends of Expr: IntExpr, RealExpr, ArithExpr...
	 * 		these implements of Expr are not visible and can only be 
	 * 		obtained through ctx.mk*() methods
	 * 
	 * Symbol: ..
	 * 
	 * Problem: context.mkInt methods does not support big number:
	 * possible solutions:
	 * 1. use int bound
	 * 2. use bit array
	 */
	public static void main(String[] args) {
		Context ctx = new Context();
		Sort[] types = new Sort[3];
		IntExpr[] xs = new IntExpr[3];
		Symbol[] names = new Symbol[3];
		IntExpr[] vars = new IntExpr[3];
		
		for(int i = 0; i < 3; i++) {
			types[i] = ctx.getIntSort();
			names[i] = ctx.mkSymbol("x_" + i);
			xs[i] = (IntExpr) ctx.mkConst(names[i], types[i]);
			vars[i] = (IntExpr) ctx.mkBound(2 - i, types[i]);
		}
		
		Expr body_vars = ctx.mkAnd(
                ctx.mkEq(ctx.mkAdd(vars[0], ctx.mkInt(1)), ctx.mkInt(2)),
                ctx.mkEq(ctx.mkAdd(vars[1], ctx.mkInt(2)),
                        ctx.mkAdd(vars[2], ctx.mkInt(3))));
		
		
		
		Expr my_body_vars = ctx.mkOr(
					ctx.mkGe(ctx.mkAdd(vars[0], vars[1]), ctx.mkInt(1)),
					ctx.mkLe(ctx.mkSub(vars[1], vars[2]), ctx.mkInt(5)),
					ctx.mkEq(vars[0], ctx.mkAdd(vars[2], ctx.mkInt(1)))
				);
		
        Expr body_const = ctx.mkAnd(
                ctx.mkEq(ctx.mkAdd(xs[0], ctx.mkInt(100000000)), ctx.mkInt(2)),
                ctx.mkEq(ctx.mkAdd(xs[1], ctx.mkInt(2)),
                        ctx.mkAdd(xs[2], ctx.mkInt(3))));
        
        Expr x = ctx.mkForall(types, names, body_vars, 0, null, null,
                ctx.mkSymbol("Q1"), ctx.mkSymbol("skid1"));
        System.out.println("Quantifier X: " + x.toString());

        Expr y = ctx.mkForall(xs, body_const, 1, null, null,
                ctx.mkSymbol("Q2"), ctx.mkSymbol("skid2"));
        
        Expr my = ctx.mkExists(types, names, my_body_vars, 1, null, null, 
        		null, null);
        
        System.out.println("Quantifier Y: " + y.toString());
		System.out.println("Quantifier Z: " + my.toString());
        ctx.close();
	}
}
