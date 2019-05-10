package other;

import java.util.ArrayList;
import java.util.List;

import graph.directed.DGraphUtil;

public class PowerSetTest {
	
	public static void main(String[] args) {
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		List<List<Integer>> ps = DGraphUtil.getPowerSet(list);
		for(List<Integer> l : ps) {
			System.out.print("{");
			for(Integer i : l) {
				System.out.print(i);
			}
			System.out.println("}");
		}
	}
	
}
