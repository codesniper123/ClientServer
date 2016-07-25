import static org.junit.Assert.*;

import org.junit.Test;

public class MongoReaderTest {

	@Test
	public void test() {
		/* initialize Application Object */
		Application app = Application.getApp();
		
		String dimensionsFile = "/Users/aravind123/Documents/learn-workspace/ClientServer/SampleModel.txt";
		app.dimensionArray = Dimension.readDimensions(dimensionsFile);
		
		String mongoFile = "/Users/aravind123/Documents/learn-workspace/ClientServer/mongo.txt";
		MongoReader mr = new MongoReader();
		if( mr.readFile(mongoFile)) {
			mr.da.print();
		}
		
		String s1 = "ABC";
		String s2 = "DEF";
		
		System.out.printf( "hashcode of ABC [%d] DEF [%d]\n",  s1.hashCode(), s2.hashCode());
	}

}
