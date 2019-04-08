package table.dwt;

import java.util.List;

public interface DWTable<V, G> {
	// V may be SDGVertex or DGVertex
	public DWTEntry<V, G> getEntry(int startIndex, int endIndex);
	public List<DWTEntry<V, G>> getStartEntryList(int startIndex);
	public List<DWTEntry<V, G>> getEndEntryList(int endIndex);
	public void increMaxLenUpdate();
}
