package app.datatype;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import vecmath.Vector;
import vecmath.vecmathimp.VectorImp;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.collect.TreeBasedTable;

/**
 * @author Benjamin Reemts
 *
 */

public class Level {
	private final float GRIDREFACTOR=2.f; 
	private Table<Float, Float, LevelNode> levelPoints;
	private final float hight;
	private Vector max, min;
	
	public Level(Vector centerPosition, float width, float depth){
		makeLevel(centerPosition, width, depth);
		hight=centerPosition.y();
		max=maxBorder(); 
		min=minBorder();
	}
	
	private void makeLevel(Vector centerPosition, float width, float depth){
		System.out.println("level center vec: "+centerPosition);
		//TODO: test big tabels with many values. Tree used bacause hash lose order
		levelPoints = TreeBasedTable.create();
//		levelPoints = HashBasedTable.create((int)(width*GRIDREFACTOR),(int)(depth*GRIDREFACTOR));
		for(float x=centerPosition.x()-(width/2)*GRIDREFACTOR; x<=centerPosition.x()+(width/2)*GRIDREFACTOR; x++){
			for(float z=centerPosition.z()-(depth/2)*GRIDREFACTOR; z<=centerPosition.z()+(depth/2)*GRIDREFACTOR; z++){
//				System.out.print(x+"/"+z+" ");
				levelPoints.put(x/GRIDREFACTOR, z/GRIDREFACTOR, new LevelNode(new VectorImp(x/GRIDREFACTOR, centerPosition.y(), z/GRIDREFACTOR)));
			}
		}
		
		for(Float row: levelPoints.rowKeySet()){
			LevelNode lastNode=null;
			for(Map.Entry<Float, LevelNode> pair:levelPoints.rowMap().get(row).entrySet()){
				LevelNode currentNode=pair.getValue();
				if(lastNode!=null){
					currentNode.addEdge(lastNode);
					lastNode.addEdge(currentNode);
				}
				lastNode=currentNode;
			}
			lastNode=null;
		}
		for(Float col: levelPoints.columnKeySet()){
			LevelNode lastNode=null;
			for(Map.Entry<Float, LevelNode> pair:levelPoints.columnMap().get(col).entrySet()){
				LevelNode currentNode=pair.getValue();
				if(lastNode!=null){
					currentNode.addEdge(lastNode);
					lastNode.addEdge(currentNode);
				}
				lastNode=currentNode;
			}
			lastNode=null;
		}
	}
	
	private void setWeigth(Set<LevelNode> positions, double multiplier){
		for(LevelNode node:positions) levelPoints.get(node.getPOS().x(), node.getPOS().z()).multEdgesVal(multiplier);
	}
	
	private void manageBlocked(LevelNode from, LevelNode toElement, boolean block){
		NavigableSet<LevelNode> nodes = new TreeSet<LevelNode>(){
			private static final long serialVersionUID = -2688655624601261964L;
			@Override
			public SortedSet<LevelNode> subSet(LevelNode fromElement, LevelNode toElement) {
				SortedSet<LevelNode> reSub=new TreeSet<LevelNode>();
				float maxX=toElement.getPOS().x(), minX=fromElement.getPOS().x();
				float maxZ=toElement.getPOS().z(), minZ=fromElement.getPOS().z();
				for(LevelNode lNode:this){
					float x=lNode.getPOS().x(), z=lNode.getPOS().z();
					if(x>=minX&&x<=maxX){
						if(z>=minZ&&z<=maxZ){
							reSub.add(lNode);
						}
					}
				}
				return reSub;
			}
		};
		Set<LevelNode> sub=new TreeSet<LevelNode>();
		//TODO: prüfen ob in sub immer noch sortiert
		if(block){
			nodes.addAll(levelPoints.values());
			//Erzeugt Set mit nur Positionswerten die nicht geblockt sind(>0) TODO: ueberschneidungen
//			for(LevelNode levelNode:levelPoints.values()){
//				if(levelNode.getVal()>0)nodes.add(levelNode);
//			}
			sub=nodes.subSet(from, toElement);
			System.out.println("from: "+from.getPOS().toString()+" to: "+toElement.getPOS().toString());
			System.out.println("block nodes:");
			for(LevelNode n:sub){
				System.out.print(n.getPOS().toString());
			}
			System.out.println();
		}else{
//			Set<LevelNode> blocked=new TreeSet<LevelNode>();
			nodes.addAll(levelPoints.values());
			sub=nodes.subSet(from, toElement);
			System.out.println("from: "+from.getPOS().toString()+" to: "+toElement.getPOS().toString());
			System.out.println("unblock nodes");
			for(LevelNode n:sub){
				System.out.print(n.getPOS().toString());
			}
			System.out.println();
//			for(LevelNode levelNode:levelPoints.values()){
//				if(levelNode.getVal()<0&&blocked.contains(levelNode)){
//					//TODO: Wenn ueberschneidungen, unterscheiden welche Nodes nicht -1 gesetzt werden muessen, weil sie noch von einem andern Objekt geblockt werden
//				}
//			}
		}
		setWeigth(sub, -1);
	}
	
	public void setBlocked(LevelNode from, LevelNode to){
		if(from!=null&&to!=null)manageBlocked(from, to, true);
	}
	public void setUnblocked(LevelNode from, LevelNode to){
		if(from!=null&&to!=null)manageBlocked(from, to, false);
	}
	
	/**
	 * Sets the Values for a Field of LevelNodes
	 * @param from Value with the minimum x and z value of the Field
	 * @param to Value with the maximum x and z value of the Field
	 * @param value The value to be multiplied with
	 */
	public void setSpace(LevelNode from, LevelNode to, double value){
		if(from!=null&&to!=null){
			NavigableSet<LevelNode> nodes = new TreeSet<LevelNode>(new Comparator<LevelNode>() {
				@Override
				public int compare(LevelNode o1, LevelNode o2) {
					return Double.compare((o1.getPOS().x()+o1.getPOS().z()), (o2.getPOS().x()+o2.getPOS().z()));
				}
			});
			nodes.addAll(levelPoints.values());
			setWeigth(nodes.subSet(from, true, to, true), value);
		}
	}
	
	@Override
	public String toString(){
		String out="";
		for(Float row:levelPoints.rowMap().keySet()){
			out+="\n";
			for(Map.Entry<Float, LevelNode> pair:levelPoints.rowMap().get(row).entrySet()){
//				out+=String.format(" %.2f/ %.2f(%d)", row,pair.getKey(), pair.getValue().getVal());
				if(row>=0) out+=" ";
				out+=row+"/";
				if(pair.getKey()>=0)out+=" ";
				out+=pair.getKey()+"("+pair.getValue().getVal()+"); ";
			}
		}
		return out;
	}
	
	/**
	 * @param position
	 * @return the position of the nearest LevelNode, null if there is no unblocked LevelNode
	 */
	public Vector getNearestinLevel(Vector position){
		Float x=getNearest(position.x(),true, false, 0),z=getNearest(position.z(),false, false,0);
		if(x!=null&&z!=null)return new VectorImp(x, position.y(), z);
		return null;
	}
	
	public LevelNode getBiggerPosInLevel(Vector position, boolean higher){
		int minMax=-1;
		if(higher)minMax=1;
		Float x=getNearest(position.x(),true, true, minMax);
		Float z=getNearest(position.z(),false, true, minMax);
		if(x!=null&&z!=null)return levelPoints.get(x,z);
		return null;
	}
	
	private Float getNearest(Float posVal, boolean xOrz, boolean allPoints, int minMax){
		Float max=0.0F,min=0.0F;
		if(xOrz){
			NavigableSet<Float> xValues = new TreeSet<Float>();
			if(allPoints){
				xValues.addAll(levelPoints.rowKeySet());
			}else{
				//Erzeugt Set mit nur Positionswerten die nicht geblockt sind(>0)
				for(Cell<Float, Float, LevelNode> c:levelPoints.cellSet()){
					if(c.getValue().getVal()>0)xValues.add(c.getRowKey());
				}
			}
			min= xValues.floor(posVal);
			max= xValues.ceiling(posVal);
			if(minMax<0){
				max= xValues.floor(posVal);				
			}else if(minMax>0){
				min= xValues.ceiling(posVal);
			}
		}else{
			NavigableSet<Float> zValues = new TreeSet<Float>();
			if(allPoints){
				zValues.addAll(levelPoints.rowKeySet());
			}else{
				//Erzeugt Set mit nur Positionswerten die nicht geblockt sind(>0)
				for(Cell<Float, Float, LevelNode> c:levelPoints.cellSet()){
					if(c.getValue().getVal()>0)zValues.add(c.getRowKey());
				}
			}
			min= zValues.floor(posVal);
			max= zValues.ceiling(posVal);
			if(minMax<0){
				max= zValues.floor(posVal);				
			}else if(minMax>0){
				min= zValues.ceiling(posVal);
			}
		}
		if(max==null)return min;
		else if(min==null)return max;
		else{
			if(Math.min(Math.abs((max-posVal)), Math.abs((min-posVal)))==Math.abs((max-posVal)))return max;
			else return min;
		}
	}
	
	public LevelNode getLevelNode(float x, float z){
		return levelPoints.get(x, z);
	}
	
	public LevelNode getLevelNode(Vector vec){
		return levelPoints.get(vec.x(), vec.z());
	}
	
	public int size(){
		return levelPoints.size();
	}
	
	public LevelNode[] toArray(){
		return levelPoints.values().toArray(new LevelNode[0]); 
	}
	
	private Vector maxBorder(){
		NavigableSet<Float> xValues = new TreeSet<Float>(levelPoints.rowKeySet());
		NavigableSet<Float> zValues = new TreeSet<Float>(levelPoints.columnKeySet());
		return new VectorImp(xValues.last(), 0, zValues.last());
	}
	
	private Vector minBorder(){
		NavigableSet<Float> xValues = new TreeSet<Float>(levelPoints.rowKeySet());
		NavigableSet<Float> zValues = new TreeSet<Float>(levelPoints.columnKeySet());
		return new VectorImp(xValues.first(), 0, zValues.first());
	}

	public float getHight() {
		return hight;
	}
	
	public int inLevel(Vector center, float rad){
		float maxX=max.x(), maxZ=max.z(), minX=min.x(), minZ=min.z();
		if(center.x()+rad>maxX||center.x()-rad<minX||center.z()+rad>maxZ||center.z()-rad<minZ){
			//one side out
			
			if(center.x()-rad>maxX)return -1;
			if(center.x()+rad<minX)return -1;
			if(center.z()-rad>maxZ)return -1;
			if(center.z()+rad<minZ)return -1;
			return 0;
		}
		return 1;
	}

	public Vector getMaxBorder() {
		return max;
	}

	public Vector getMinBorder() {
		return min;
	}
}
