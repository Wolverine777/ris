package app.eventsystem;

import java.util.List;

import app.vecmath.Vector;
import app.vecmathimp.VectorImp;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * @author Benjamin Reemts
 *
 */

public class Level {
	private final float GRIDRESOLUTION=0.1f;
	private Table<Float, Float, LevelNode> levelPoints = HashBasedTable.create();
	
	public Level(Vector centerPosition, float width, float hight, float depth){
		makeLevel(centerPosition, width, hight, depth);
	}
	
	private void makeLevel(Vector centerPosition, float width, float hight, float depth){
		for(float x=centerPosition.x()-(width/2); x<centerPosition.x()+(width/2); x=x+GRIDRESOLUTION){
			for(float z=centerPosition.z()-(depth/2); z<centerPosition.z()+(depth/2); z=z+GRIDRESOLUTION){
				levelPoints.put(x, z, new LevelNode(new VectorImp(x, centerPosition.y(), z)));
			}
		}
	}
	
	public void setSpaceWeigth(List<Vector> positions, int multiplier){
		for(Vector vec:positions) levelPoints.get(vec.x(), vec.z()).multEdgesVal(multiplier);
	}
	
	public void setSpaceBlocked(List<Vector> positions){
		for(Vector vec:positions) levelPoints.get(vec.x(), vec.z()).multEdgesVal(-1);
	}
	
}
