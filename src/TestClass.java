import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import app.datatype.AStarNodes;
import app.eventsystem.LevelNode;
import app.vecmath.Vector;
import app.vecmathimp.VectorImp;


/**
 * @author Benjamin Reemts
 *
 */

public class TestClass {
	public static void main(String args[]){
		NavigableSet<Double> numbers = new TreeSet<Double>();
		numbers.add(89000.0);
		numbers.add(78900.0);
		numbers.add(56999.12);

		Double value = numbers.ceiling(88908.0);
		System.out.println(value);
		Vector toRound=new VectorImp(0.3f, 1, 0.2f);
		float ro=((float)Math.round((toRound.x()*100)))/100;
//		System.out.println(ro);
		ro=ro/100;
//		System.out.println(ro);
		TestClass tc=new TestClass();
		tc.testMin();
		
		
	}
	
	public void testMin(){
		Map<LevelNode, AStarNodes> visited=new LinkedHashMap<LevelNode, AStarNodes>();
		visited.put(new LevelNode(new VectorImp(0, 0, 0)), new AStarNodes(2, 0, new LinkedList<LevelNode>()));
		visited.put(new LevelNode(new VectorImp(0, 0, 0)), new AStarNodes(10, 0, new LinkedList<LevelNode>()));
		visited.put(new LevelNode(new VectorImp(0, 0, 0)), new AStarNodes(-1, 0, new LinkedList<LevelNode>()));
		visited.put(new LevelNode(new VectorImp(0, 0, 0)), new AStarNodes(6, 0, new LinkedList<LevelNode>()));
		visited.put(new LevelNode(new VectorImp(0, 0, 0)), new AStarNodes(8, 0, new LinkedList<LevelNode>()));
		
		System.out.println(Collections.min(visited.entrySet(), new Comparator<Map.Entry<LevelNode, AStarNodes>>() {
			@Override
			public int compare(Entry<LevelNode, AStarNodes> o1,
					Entry<LevelNode, AStarNodes> o2) {
				return Double.compare(o1.getValue().getLength(), o2.getValue().getLength());
			}}).getKey().getVal());
	}
}
