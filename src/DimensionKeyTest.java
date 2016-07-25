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

		System.out.printf( "Extracting all combinations, starting from dimension = 6\n");
		
		if( !dk.split(origKey, 6) ) {
			System.out.printf( "Invalid format in key [%s]\n",  origKey);
		} else {
			System.out.printf( "Original Key = [%s] New Key = [%s] Aggregated Dimension Value [%d]\n",
				origKey, dk.newKey, dk.aggDimensionValue);
		}
		
		for( int i = 6; i > 0; i--) {
			String key = dk.newKey;
			int dimValue = dk.aggDimensionValue;
			
			if( !dk.split(key, i, dimValue, i-1)) {
				System.out.printf( "error in splitting...key = [%s]\n",  key);
			}
			else {
				System.out.printf( "new key [%s] new dim value [%d]\n",  dk.newKey, dk.aggDimensionValue);
			}
		}
	
	}

}
