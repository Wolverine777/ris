package app.eventsystem;

import java.util.List;
import java.util.Map;

import app.vecmath.Vector;
import app.vecmathimp.VectorImp;

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
	
	public Level(Vector centerPosition, float width, float hight, float depth){
		makeLevel(centerPosition, width, hight, depth);
	}
	
	private void makeLevel(Vector centerPosition, float width, float hight, float depth){
		levelPoints = HashBasedTable.create((int)(width*GRIDREFACTOR),(int)(depth*GRIDREFACTOR));
		for(float x=centerPosition.x()-(width/2)*GRIDREFACTOR; x<=centerPosition.x()+(width/2)*GRIDREFACTOR; x++){
			for(float z=centerPosition.z()-(depth/2)*GRIDREFACTOR; z<=centerPosition.z()+(depth/2)*GRIDREFACTOR; z++){
				levelPoints.put(x/GRIDREFACTOR, z/GRIDREFACTOR, new LevelNode(new VectorImp(x/GRIDREFACTOR, centerPosition.y(), z/GRIDREFACTOR)));
			}
		}
	}
	
	public void setSpaceWeigth(List<Vector> positions, int multiplier){
		for(Vector vec:positions) levelPoints.get(vec.x(), vec.z()).multEdgesVal(multiplier);
	}
	
	public void setSpaceBlocked(List<Vector> positions){
		for(Vector vec:positions) levelPoints.get(vec.x(), vec.z()).multEdgesVal(-1);
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
		int roundvalue = 10000;
	
		position = roundVector(position, roundvalue);
			
		
		//TODO: solange position um nachkommastellen runden, bis es den vector im level gibt--> return
		return position;
	}
	
	public Vector roundVector(Vector position, int roundvalue){
		float newx = 0;
		float newz = 0;
		
		for(Float row:levelPoints.rowMap().keySet()){
			for(Map.Entry<Float, LevelNode> pair:levelPoints.rowMap().get(row).entrySet()){
				if(pair.getValue().getPOS().x() == position.x() && pair.getValue().getPOS().z() == position.z()){
					return position;
				}
//				else if(pair.getValue().getPOS().x() != position.x() && pair.getValue().getPOS().z() != position.z()){
//					newx = Math.round(position.x()*roundvalue)/roundvalue;
//					newz = Math.round(position.z()*roundvalue)/roundvalue;
//				}
				else if(pair.getValue().getPOS().x() == position.x() && pair.getValue().getPOS().z() != position.z()){
					newx = pair.getValue().getPOS().x();
//					newz = Math.round(position.z()*roundvalue)/roundvalue; 
				}
				else if(pair.getValue().getPOS().x() != position.x() && pair.getValue().getPOS().z() == position.z()){
					newz = pair.getValue().getPOS().z();
//					newx = Math.round(position.x()*roundvalue)/roundvalue;
				}
				
			}
		}
		if(newx == 0 ){
			System.out.println("position: " + position.x());
			newx = Math.round(position.x()*roundvalue)/roundvalue;
			System.out.println("Newx: " + newx);
		}
		if(newz == 0){
			newz = Math.round(position.z()*roundvalue)/roundvalue;
		}
		roundvalue= roundvalue/10;
		return roundVector(new VectorImp(newx, position.y(), newz), roundvalue);
		
	}
	
	

	public Table<Float, Float, LevelNode> getLevelPoints() {
		return levelPoints;
	}
	
	
}
