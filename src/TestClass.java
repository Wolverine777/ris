import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import vecmath.Vector;
import vecmath.vecmathimp.MatrixImp;
import vecmath.vecmathimp.VectorImp;
import app.Renderer;
import app.datatype.AStarNodes;
import app.datatype.LevelNode;
import app.nodes.GroupNode;
import app.nodes.Node;
import app.nodes.shapes.Cube;
import app.nodes.shapes.Plane;
import app.shader.Shader;


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
//		tc.testMin();
		tc.floatTest();
		
	}
	
	private void floatTest(){
		float x=100;
		float y=0;
		MatrixImp m= (MatrixImp) MatrixImp.identity;
		VectorImp v=new VectorImp(1.0f, 0.0f, 0.0f);
		
		for(x=100;x>0;x--){
			System.out.println(m.mult(MatrixImp.translate(v)).getPosition());
			Vector sub=m.getPosition().sub(v);
			System.out.println(sub.x()+" "+sub.y()+" "+sub.z());
			for(y=0;y<100;y++){
//				System.out.print((x-y)+"; ");
			}
			System.out.println();
		}
	}
	
	private void testListcontains(){
		List<Node> flags=new LinkedList<Node>(){
			private static final long serialVersionUID = 7233857901815694877L;
			public boolean contains(Object o) {
				for(Node x:this){
					System.out.println(x.getId());
					if(((Node)o).getId().equals(x.getId()))return true;
				}
		        return false;
		    }
		};
		flags.add(new GroupNode("test"));
		flags.add(new GroupNode("test2"));
		flags.add(new GroupNode("test3"));
		System.out.println("contains: "+flags.contains(new GroupNode("test3")));
	}
	
	private void testMin(){
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
