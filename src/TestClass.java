import java.util.NavigableSet;
import java.util.TreeSet;


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
	}
}
