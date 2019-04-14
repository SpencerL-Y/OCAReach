package graph.directed;

import java.util.ArrayList;
import java.util.List;

public class DGraphUtil {
	public static void printAdjMatrix(DGraph g) {
		System.out.print("# ");
		for(DGVertex v : g.getVertices()) {
			System.out.print(v.getIndex() + " ");
		}
		System.out.println();
		for(DGVertex v : g.getVertices()) {
			System.out.print(v.getIndex() + " ");
			for(DGVertex ve : g.getVertices()) {
				int flag = 0;
				for(DGEdge e : v.getEdges()) {
					if(e.getTo().getIndex() == ve.getIndex()) {
						System.out.print(e.getWeight() + " ");
						flag = 1;
						break;
					}
				}
				if(flag == 0) {
					System.out.print(0 + " ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	// algorithm
	//TODO: debug
	public static <T> List<List<T>> getPowerSet(List<T> set){
		return DGraphUtil.powerSet(set);
	}
	
	private static <T> List<List<T>> powerSet(List<T> set){
		if(set.size() == 1) {
			List<List<T>> arrayList = new ArrayList<List<T>>();
			arrayList.add(new ArrayList<T>());
			arrayList.add(set);
			return arrayList;
		}
		List<T> head = new ArrayList<T>();
		head.add(set.get(0));
		List<List<T>> result = DGraphUtil.powerSet(set.subList(1, set.size()));
		List<List<T>> tempResult = new ArrayList<List<T>>();
		for(List<T> list : result) {
			List<T> temp = DGraphUtil.union(list, head);
			tempResult.add(temp);
		}
		return DGraphUtil.union(result, tempResult);
	}
	
	public static <T> List<T> union(List<T> set1, List<T> set2){
		List<T> list = new ArrayList<T>();
		for(T t : set1) {
			if(!list.contains(t)) {
				list.add(t);
			}
		}
		for(T t : set2) {
			if(!list.contains(t)) {
				list.add(t);
			}
		}
		return list;
	}
}
