package table.dwt;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Table {
	private JTable table;
	private DefaultTableModel model;
	public Table() {
		model = new DefaultTableModel();
		this.table = new JTable(model);
	}
	
	public void addCol(String title) {
		this.model.addColumn(title);
	}
	
	public void addRow(Object[] v) {
		this.model.addRow(v);
	}
	
	public DefaultTableModel getModel() {
		return this.model;
	}
	
	public JTable getTable() {
		return this.table;
	}
	
}
