package app.datatype;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import vecmath.Vector;
import vecmath.vecmathimp.VectorImp;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

/**
 * @author Benjamin Reemts, Fabian Unruh
 *
 */

public class Level {
	private final float GRIDREFACTOR=2.f; 
	private Table<Float, Float, LevelNode> levelPoints;
	
	public Level(Vector centerPosition, float width, float depth){
		makeLevel(centerPosition, width, depth);
	}
	
	private void makeLevel(Vector centerPosition, float width, float depth){
		System.out.println("level center vec: "+centerPosition);
		levelPoints = HashBasedTable.create((int)(width*GRIDREFACTOR),(int)(depth*GRIDREFACTOR));
		for(float x=centerPosition.x()-(width/2)*GRIDREFACTOR; x<=centerPosition.x()+(width/2)*GRIDREFACTOR; x++){
			for(float z=centerPosition.z()-(depth/2)*GRIDREFACTOR; z<=centerPosition.z()+(depth/2)*GRIDREFACTOR; z++){
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
	
	private void setWeigth(Set<LevelNode> positions, int multiplier){
		for(LevelNode node:positions) levelPoints.get(node.getPOS().x(), node.getPOS().z()).multEdgesVal(multiplier);
	}
	
	public void setSpaceBlocked(LevelNode from, LevelNode to){
		NavigableSet<LevelNode> nodes = new TreeSet<LevelNode>(new Comparator<LevelNode>() {
			@Override
			public int compare(LevelNode o1, LevelNode o2) {
				return Double.compare((o1.getPOS().x()+o1.getPOS().z()), (o2.getPOS().x()+o2.getPOS().z()));
			}
		});
		//TODO: Add filter for already fields with -1
		setWeigth(new TreeSet<LevelNode>(nodes.subSet(from, true, to, true)), -1);
	}
	
	public void setSpace(LevelNode from, LevelNode to, int value){
		NavigableSet<LevelNode> nodes = new TreeSet<LevelNode>(new Comparator<LevelNode>() {
			@Override
			public int compare(LevelNode o1, LevelNode o2) {
				return Double.compare((o1.getPOS().x()+o1.getPOS().z()), (o2.getPOS().x()+o2.getPOS().z()));
			}
		});
		setWeigth(new TreeSet<LevelNode>(nodes.subSet(from, true, to, true)), value);
	}
	
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
	
	public Vector getNearestinLevel(Vector position){
		return new VectorImp(getNearest(position.x(),true), position.y(), getNearest(position.z(),false));
	}
	
	//TODO: methode für nächste aber überschätzen also kleiner nicht beachten
	private Float getNearest(Float posVal, boolean xOrz){
		Float max=0.0F,min=0.0F;
		if(xOrz){
			NavigableSet<Float> xValues = new TreeSet<Float>();
			//Erzeugt Set mit nur Positionswerten die nicht geblockt sind(>0)
			for(Cell<Float, Float, LevelNode> c:levelPoints.cellSet()){
				if(c.getValue().getVal()>0)xValues.add(c.getRowKey());
			}
			max= xValues.ceiling(posVal);
			min= xValues.floor(posVal);
		}else{
			NavigableSet<Float> zValues = new TreeSet<Float>();
			for(Cell<Float, Float, LevelNode> c:levelPoints.cellSet()){
				if(c.getValue().getVal()>0)zValues.add(c.getColumnKey());
			}
			max= zValues.ceiling(posVal);
			min= zValues.floor(posVal);
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
	
	public Vector maxBorder(){
		NavigableSet<Float> xValues = new TreeSet<Float>(levelPoints.rowKeySet());
		NavigableSet<Float> zValues = new TreeSet<Float>(levelPoints.columnKeySet());
		return new VectorImp(xValues.last(), 0, zValues.last());
	}
	
	public Vector minBorder(){
		NavigableSet<Float> xValues = new TreeSet<Float>(levelPoints.rowKeySet());
		NavigableSet<Float> zValues = new TreeSet<Float>(levelPoints.columnKeySet());
		return new VectorImp(xValues.first(), 0, zValues.first());
	}
}
