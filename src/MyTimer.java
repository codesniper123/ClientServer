
public class MyTimer {
	protected long startTime;
	protected long stopTime;
	
	public void start() {
		startTime = System.currentTimeMillis();
	}
	
	public void stop() {
		stopTime = System.currentTimeMillis();
	}
	
	public long time() {
		stopTime = System.currentTimeMillis();
		return stopTime - startTime; 
	}
}


