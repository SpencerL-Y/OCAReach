package test;

import table.dwt.DWTuple;
import table.dwt.Table;

public class TestTable {
	public static void main(String[] args){
		Table t = new Table();
		t.addCol("from");
		t.addCol("to");
		t.addCol("tuple");
		Object[] o = new Object[3];
		o[0] = 1; o[1] = 2; o[2] = new DWTuple(3,4);
		t.addRow(o);
		t.addRow(o);
		System.out.println(t.getModel().getRowCount());
	}	
}
