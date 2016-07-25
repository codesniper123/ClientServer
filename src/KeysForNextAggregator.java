import java.util.Hashtable;

public interface KeysForNextAggregator {
	public boolean addMapEntry(String key, int dimensionValue, Hashtable<String, Float> map);
}

