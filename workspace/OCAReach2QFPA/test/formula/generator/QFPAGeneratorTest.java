package formula.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.microsoft.z3.Expr;
import com.microsoft.z3.*;

import junit.framework.TestCase;

public class QFPAGeneratorTest extends TestCase {

	private QFPAGenerator gen;
	
	
	protected void setUp() throws Exception {
		this.gen = new QFPAGenerator();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}



	public final void testSumUpVars() {
		System.out.println("SumUpVarsTest:");
		List<IntExpr> vars = new ArrayList<IntExpr>(5);
		for(int i = 0; i < 5; i++) {
			vars.add(this.gen.mkVariableInt("x_"+i));
		}
		IntExpr result = this.gen.sumUpVars(vars);
		System.out.println(result.toString());
	}



	public final void testMkRequireNonNeg() {
		IntExpr var = this.gen.mkVariableInt("x");
		BoolExpr result  = this.gen.mkRequireNonNeg(var);
		System.out.println(result.toString());
	}
	
}

