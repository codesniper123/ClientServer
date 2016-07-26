import java.util.Hashtable;

public interface KeysForNextAggregator {
	public boolean addMapEntry(String key, int dimensionValue, Hashtable<Short, Float> map);
	public int getNumKeys();
	public int getNumValues();
}

