import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class DimensionTest {

	@Test
	public void test() {
		String filename = "/Users/aravind123/Documents/learn-workspace/ClientServer/SampleModel.txt";
		
		ArrayList<Dimension> ad = Dimension.readDimensions(filename);
		
		System.out.printf( "Successfully read [%d] dimensions\n", ad.size());
		
		for(int i = 0; i < ad.size(); i++ ) {
			Dimension d = ad.get(i);
			System.out.printf( "Max Value in Dimension[%d] is [%d]\n", i, d.maxValue);
		}
		
		System.out.printf( "Printing last dimension\n" );
		Dimension d = ad.get(6);
		d.root.printPreOrder();
	}

}
