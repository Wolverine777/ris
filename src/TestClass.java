import java.util.NavigableSet;
import java.util.TreeSet;

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
		System.out.println(ro);
		ro=ro/100;
		System.out.println(ro);
		
	}
}
