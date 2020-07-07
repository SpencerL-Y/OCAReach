package table.dwt;

import java.util.List;
import java.util.Random;

import graph.directed.DGraph;
import junit.framework.TestCase;

public class DWTableTest extends TestCase {

	private DGraph g;
	private DWTable table;
	private Random r;
	protected void setUp() throws Exception {
		this.g = new DGraph(1);
		for(int i = 0; i < 4; i++) {
			this.g.addVertex(i);
		}
		this.g.addEdge(0, 1, -1);
		this.g.addEdge(1, 2, -1);
		this.g.addEdge(2, 0, 1);
		this.g.addEdge(2, 3, 1);
		this.table = new DWTableImpl(this.g);
		this.r = new Random();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testGetEntry() {
		System.out.println("GetEntryTest:");
		this.table.increMaxLenUpdate();
		this.table.increMaxLenUpdate();
		this.table.increMaxLenUpdate();
		this.table.increMaxLenUpdate();
		System.out.println("maxlen: " + this.table.getMaxLength());
		System.out.println("entries num: " + this.table.getEntryList().size());
		System.out.println("-----------------DWTEntries---------------------");
		for(DWTEntry e : this.table.getEntryList()) {
			System.out.println("entry : start at " + e.getStartIndex() + " end at " + e.getEndIndex());
			System.out.println("tuple size: " + e.getSetOfDWTuples().size());
			
		}
		System.out.println("----------------------------------------------");
		for(int i = 0; i < 30; i++) {
			this.table.getEntry(r.nextInt(4), r.nextInt(4));
		}
	}

	public final void testGetStartEntryList() {
		System.out.println("GetStartEntryListTest:");
		for(int i = 0; i < 4; i++) {
			this.table.increMaxLenUpdate();
		}
		List<DWTEntry> list = this.table.getStartEntryList(0);
		for(DWTEntry e : list) {
			e.printEntry();
		}
	}

	public final void testGetEndEntryList() {
		System.out.println("GetEndEntryListTest:");
		for(int i = 0; i < 5; i ++) {
			this.table.increMaxLenUpdate();
		}
		List<DWTEntry> list = this.table.getEndEntryList(3);
		for(DWTEntry e : list) {
			e.printEntry();
		}
	}

	public final void testGetEntryList() {
		System.out.println("GetEntryListTest:s");
		for(int i = 0; i < 4; i++) {
			this.table.increMaxLenUpdate();
		}
		for(int i = 0; i < 100; i++) {
			this.table.getEntryList();
		}
	}

	public final void testIncreMaxLenUpdate() {
		System.out.println("IncreMaxLenUpdateTest:");
		for(int i = 0; i < 50; i++) {
			this.table.increMaxLenUpdate();
		}
		System.out.println("IncreMaxLenUpdateTest Success!");
		
	}

	public final void testGetMaxLength() {
		System.out.println("GetMaxLengthTest:");
		int maxLen = this.table.getMaxLength();
		System.out.println(maxLen);
		this.table.increMaxLenUpdate();
		System.out.println(this.table.getMaxLength());
	}

}
