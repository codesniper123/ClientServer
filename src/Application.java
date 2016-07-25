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
	
}
