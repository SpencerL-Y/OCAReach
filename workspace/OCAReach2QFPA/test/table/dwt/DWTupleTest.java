package table.dwt;

import java.util.Random;



import junit.framework.TestCase;

public class DWTupleTest extends TestCase{

	private DWTuple originTuple;
	private Random r;
	
	
	public void setUp() throws Exception {
		this.originTuple = new DWTuple(-4, 10);
		this.r = new Random();
	}

	
	public void tearDown() throws Exception {
		
	}

	public final void testPrintTuple() {
		System.out.println("PrintTupleTest:");
		for(int i = 0; i < 20; i++) {
			DWTuple t = new DWTuple(r.nextInt(5)-7, r.nextInt(10));
			t.printTuple();
		}
	}

	public final void testSetTuple() {
		System.out.println("SetTupleTest:");
		this.originTuple.printTuple();
		this.originTuple.setTuple(10, 10);
		this.originTuple.printTuple();
	}

	public final void testMerge() {
		for(int i = 0; i < 20; i++) {
			DWTuple t1 = new DWTuple(r.nextInt(5)-7, r.nextInt(10));
			DWTuple t2 = new DWTuple(r.nextInt(5)-7, r.nextInt(10));
			DWTuple t = DWTuple.merge(t1, t2);
			assert(t.getDrop() == Math.min(t1.getDrop(), t2.getDrop()+t1.getWeight()));
			assert(t.getWeight() == t1.getWeight() + t2.getWeight());
		}
	}

}
