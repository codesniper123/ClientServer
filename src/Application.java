import java.util.ArrayList;

/*
 * 
 * Global Application Class
 * 
 */

public class Application {
	protected static ArrayList<Dimension> dimensionArray;

	private static Application app;
	protected static MyTimer timer;
	
	/* singleton Application object */
	static protected final Application getApp() {
		if( Application.app == null ) 
			app = new Application();
		return app;
	}
	
	private Application() {
		timer = new MyTimer();
		timer.start();
	}
	
	public static void main(String args[]) {
		/* initialize Application Object */
		Application app = Application.getApp();
		
		String dimensionsFile = "../SampleModel.txt";
		
		String mongoFile = "../complete_mongo.txt";
		String outputFile = null;
		
		// String mongoFile = "../mongo.txt";
		// String outputFile = "../output.txt";
		
		timer.start();
		
		app.dimensionArray = Dimension.readDimensions(dimensionsFile);
		
		MongoReader mr = new MongoReader();
		
		if( mr.readFile(mongoFile)) {
			// mr.da.print();
		}
		
		DimensionAggregator current = mr.da;
		for( int nextDimension = current.aggDimension - 1; nextDimension >= 0; nextDimension-- ) {
			DimensionAggregator next = new DimensionAggregator(nextDimension);
			current.processKeys(next, outputFile);
			
			//System.out.printf( "Processing dimension [%d]\n",  current.aggDimension);
			//current.print();
			
			current = null;
			current = next;
			
			Runtime runtime = Runtime.getRuntime();
			runtime.gc();
			long memory = runtime.totalMemory() - runtime.freeMemory();
			System.out.printf( "Memory used: [%d]MB\n",  memory / (1024*1024));
			
			System.out.printf( "Time used [%d]\n",  timer.time() / 1000 );
		}
		
		/* do the last dimension */
		current.processKeys(null, outputFile);
		
		/* dump next to a file for inspection */
		//current.saveToFile("../output.txt");
	}
}


