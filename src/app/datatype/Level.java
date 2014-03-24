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
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Coordinate.DimensionalComparator;

/**
 * @author Benjamin Reemts
 *
 */

public class Level {
	private final float GRIDREFACTOR=2.0f; 
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
		Float prevRow=null;
//		for(Cell<Float, Float, LevelNode> cell:levelPoints.cellSet()){
//			if(prevColum!=null){
//				cell.getValue().addEdge(levelPoints.get(cell.getRowKey(), prevColum));
//				levelPoints.get(cell.getRowKey(), prevColum).addEdge(cell.getValue());
//				
//				
//			}
//		}
		
		for(Float row:levelPoints.rowMap().keySet()){
			Float prevColum=null;
			for(Map.Entry<Float, LevelNode> pair:levelPoints.rowMap().get(row).entrySet()){
				if(prevRow!=null){
					//to prevRow up-down
					pair.getValue().addEdge(levelPoints.get(prevRow, pair.getKey()));
					levelPoints.get(prevRow, pair.getKey()).addEdge(pair.getValue());
					if(prevColum!=null){
						//to prevRow and prevCol to top left
						pair.getValue().addEdge(levelPoints.get(prevRow, prevColum));
						levelPoints.get(prevRow, prevColum).addEdge(pair.getValue());
					}
					//is there a next Colum?
					if(levelPoints.containsColumn(pair.getKey()+(1/GRIDREFACTOR))){
						//than add to top right
						pair.getValue().addEdge(levelPoints.get(prevRow, pair.getKey()+(1/GRIDREFACTOR)));
						levelPoints.get(prevRow, pair.getKey()+(1/GRIDREFACTOR)).addEdge(pair.getValue());
					}
				}
				if(prevColum!=null){
					//to prevCol left-right
					pair.getValue().addEdge(levelPoints.get(row, prevColum));
					levelPoints.get(row, prevColum).addEdge(pair.getValue());
				}
				prevColum=pair.getKey();
			}
			prevRow=row;
		}
		
//		for(Float row: levelPoints.rowKeySet()){
//			LevelNode lastNode=null;
//			for(Map.Entry<Float, LevelNode> pair:levelPoints.rowMap().get(row).entrySet()){
//				LevelNode currentNode=pair.getValue();
//				if(lastNode!=null){
//					currentNode.addEdge(lastNode);
//					lastNode.addEdge(currentNode);
//				}
//				lastNode=currentNode;
//			}
//			lastNode=null;
//		}
//		for(Float col: levelPoints.columnKeySet()){
//			LevelNode lastNode=null;
//			for(Map.Entry<Float, LevelNode> pair:levelPoints.columnMap().get(col).entrySet()){
//				LevelNode currentNode=pair.getValue();
//				if(lastNode!=null){
//					currentNode.addEdge(lastNode);
//					lastNode.addEdge(currentNode);
//				}
//				lastNode=currentNode;
//			}
//			lastNode=null;
//		}
	}
	
	private void setWeigth(Set<LevelNode> positions, double multiplier){
		for(LevelNode node:positions) {
			levelPoints.get(node.getPOS().x(), node.getPOS().z()).multEdgesVal(multiplier);
		}
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
//			System.out.println("from: "+from.getPOS().toString()+" to: "+toElement.getPOS().toString());
//			System.out.println("block nodes:");
//			for(LevelNode n:sub){
//				System.out.print(n.getPOS().toString());
//			}
//			System.out.println();
		}else{
//			Set<LevelNode> blocked=new TreeSet<LevelNode>();
			nodes.addAll(levelPoints.values());
			sub=nodes.subSet(from, toElement);
//			System.out.println("from: "+from.getPOS().toString()+" to: "+toElement.getPOS().toString());
//			System.out.println("unblock nodes");
//			for(LevelNode n:sub){
//				System.out.print(n.getPOS().toString());
//			}
//			System.out.println();
//			for(LevelNode levelNode:levelPoints.values()){
//				if(levelNode.getVal()<0&&blocked.contains(levelNode)){
//					//TODO: Wenn ueberschneidungen, unterscheiden welche Nodes nicht -1 gesetzt werden muessen, weil sie noch von einem andern Objekt geblockt werden
//				}
//			}
		}
		setWeigth(sub, -1.0);
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
				if(row.floatValue()>=0) out+=" ";
				out+=row.floatValue()+"/";
				if(pair.getKey()>=0)out+=" ";
				out+=pair.getKey()+"("+pair.getValue().getVal()+"); ";
			}
		}
		return out;
	}
	
	/**
	 * @param position
	 * @return the position of the nearest LevelNode, null if there is no LevelNode
	 */
	public LevelNode getNearestinLevel(Vector position, boolean withBlocked){
		Coordinate c=getNearest(new Coordinate(position.x(), position.z()), withBlocked, 0);
		if(c!=null)return levelPoints.get((float)c.x, (float)c.y) ;
		return null;
	}
	
	public LevelNode getBiggerPosInLevel(Vector position, boolean higher){
		int minMax=-1;
		if(higher)minMax=1;
		Coordinate c=getNearest(new Coordinate(position.x(), position.z()), true, minMax);
		if(c!=null) return levelPoints.get((float)c.x, (float)c.y) ;
		return null;
	}
	
	public boolean nearestIsBlocked(Vector pos){
		Coordinate c=getNearest(new Coordinate(pos.x(), pos.z()), true, 0);
		if(c!=null){
			if(!levelPoints.get((float)c.x, (float)c.y).isBlocked())return false;
		}
		return true;
	}
	
	private Coordinate getNearest(Coordinate pos, boolean allPoints, int minMax){
		NavigableSet<Coordinate> values = new TreeSet<Coordinate>(new DimensionalComparator(2));
		if(allPoints){
			for(Cell<Float, Float, LevelNode> c:levelPoints.cellSet()){
				values.add(c.getValue().getCoordinate());
			}
		}else{
			//Erzeugt Set mit nur Positionswerten die nicht geblockt sind(>0)
			for(Cell<Float, Float, LevelNode> c:levelPoints.cellSet()){
				if(!c.getValue().isBlocked()){
					values.add(c.getValue().getCoordinate());
				}
			}
		}
		Coordinate near=null;
		double dist=-1;
		if(minMax<0){
			Set<Coordinate> less=values.headSet(pos, true);
			for(Coordinate c:less){
				double tmpdist= c.distance(pos);
				if(tmpdist<dist||dist<0){
					dist=tmpdist;
					near=c;
				}
			}
			
		}else if(minMax>0){
			Set<Coordinate> more =values.tailSet(pos, true);
			for(Coordinate c:more){
				double tmpdist= c.distance(pos);
				if(tmpdist<dist||dist<0){
					dist=tmpdist;
					near=c;
				}
			}
			
		}else{
			for(Coordinate c:values){
				double tmpdist= c.distance(pos);
				if(tmpdist<dist||dist<0){
					dist=tmpdist;
					near=c;
				}
			}
		}
		return near;
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
//		System.out.println("inlevel center"+center);
		if(center.x()+rad>maxX||center.x()-rad<minX||center.z()+rad>maxZ||center.z()-rad<minZ){
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
