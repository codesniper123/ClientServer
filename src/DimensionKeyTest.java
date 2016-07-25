import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class DimensionKeyTest {

	@Test
	public void test() {
		
		/* initialize Application Object */
		Application app = Application.getApp();
		
		String filename = "/Users/aravind123/Documents/learn-workspace/ClientServer/SampleModel.txt";
		app.dimensionArray = Dimension.readDimensions(filename);
		
		DimensionKey dk = new DimensionKey();
		String origKey = "100_3_3_128_2_2_4"; 
		if( !dk.split(origKey, 3) ) {
			System.out.printf( "Invalid format in key [%s]\n",  origKey );
		} else {
			System.out.printf( "Original Key = [%s] New Key = [%s] Aggregated Dimension Value [%d]\n",
				origKey, dk.newKey, dk.aggDimensionValue);
		}

		if( !dk.split(origKey, 0) ) {
			System.out.printf( "Invalid format in key [%s]\n",  origKey );
		} else {
			System.out.printf( "Original Key = [%s] New Key = [%s] Aggregated Dimension Value [%d]\n",
				origKey, dk.newKey, dk.aggDimensionValue);
		}

		if( !dk.split(origKey, 6) ) {
			System.out.printf( "Invalid format in key [%s]\n",  origKey );
		} else {
			System.out.printf( "Original Key = [%s] New Key = [%s] Aggregated Dimension Value [%d]\n",
				origKey, dk.newKey, dk.aggDimensionValue);
		}

	
	}

}
