import java.util.ArrayList;

/*
 * 
 * Global Application Class
 * 
 */

public class Application {
	protected static ArrayList<Dimension> dimensionArray;

	private static Application app;
	
	/* singleton Application object */
	static protected final Application getApp() {
		if( Application.app == null ) 
			app = new Application();
		return app;
	}
	
	private Application() {}
	
	public static void main(String args[]) {
		/* initialize Application Object */
		Application app = Application.getApp();
		
		String dimensionsFile = "/Users/aravind123/Documents/learn-workspace/ClientServer/SampleModel.txt";
		app.dimensionArray = Dimension.readDimensions(dimensionsFile);
		
		String mongoFile = "/Users/aravind123/Documents/learn-workspace/ClientServer/mongo.txt";
		MongoReader mr = new MongoReader();
		if( mr.readFile(mongoFile)) {
			mr.da.print();
		}
		
		DimensionAggregator current = mr.da;
		for( int nextDimension = current.aggDimension - 1; nextDimension >= 0; nextDimension-- ) {
			DimensionAggregator next = new DimensionAggregator(nextDimension);
			current.processKeys(next);
			
			System.out.printf( "Processing dimension [%d]\n",  current.aggDimension);
			//current.print();
			
			current = next;
		}
	}
}


