package app.eventsystem;

import java.util.List;
import java.util.Map;

import app.vecmath.Vector;
import app.vecmathimp.VectorImp;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

/**
 * @author Benjamin Reemts
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
		//TODO: solange position um nachkommastellen runden, bis es den vector im level gibt--> return
		return null;
	}
}
