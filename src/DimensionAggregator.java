/*
 * TBD 1) We are storing a HashMap of String, String for the Map for a specific dimension value.
 *        We are doing this since we are storing the "_id=" value also in this map.
 *        If this turns out to be inefficient, we need to store the _id= separately and make
 *        the map to be a String,Float map. 
 *        
 *        ** update this is done kind of.  Not happy with the re-allocation of Hashtable.
 *        
 */

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/*
 * Aggregates data per dimension.
 * 
 * Stores Aggregated dimension (Index into Application.dimensions)
 * Hash table of keys.  A key is constructed of all dimensions except the dimension we are aggregating on
 * Array of possible values of the Aggregated Dimension
 * Each element of the array stores a Hashmap values for this combination.
 */

public class DimensionAggregator implements KeysForNextAggregator {
	/* Index into Application.DimensionArray */
	protected int aggDimension;  
	
	/* Hashtable of the keys to all possible values of AggregatedDimension */
	private Hashtable<String, SingleDimension> aggDimensionHash;
	
	private class SingleDimension {
		
		/* Map of values for a complete dimension combination
		 * This array is constructed based on how many values this dimension can take
		 */

		// private Hashtable<String,String>[] aggDimensionMap;
		private Object[] aggDimensionMapArray;
		
		SingleDimension(int aggDimension) {
			/* create array of all possible values */
			int maxDimensionValue = Application.getApp().dimensionArray.get(aggDimension).maxValue+1;
			aggDimensionMapArray = new Object[maxDimensionValue];
		}
		
		protected boolean addMap(int aggDimensionValue, Hashtable<String, Float> map) {
			int maxDimensionValue = Application.getApp().dimensionArray.get(aggDimension).maxValue+1;
			if( aggDimensionValue > maxDimensionValue ) {
				System.out.printf( "Aggregated dimension value exceeds possible values [%d]\n",  aggDimensionValue );
				return false;
			}
			
			if( aggDimensionMapArray[aggDimensionValue] != null) {
				System.out.printf( "Duplicate map specified for value = [%d]\n",  aggDimensionValue);
				return false;
			}
			
			/* store the map */
			aggDimensionMapArray[aggDimensionValue] = map;
			
			return true;
		}
	}
	
	public DimensionAggregator(int dimension) {
		aggDimension = dimension;
		this.aggDimensionHash = new Hashtable<String, SingleDimension>();
	}
	
	/*
	 * key is constructed with all dimensions except the one that we are aggregating on.
	 * aggDimensionValue is the value of the dimension we are aggregating on.
	 * Hashtable values represent the map of this combination
	 */
	@Override
	public boolean addMapEntry(String key, int aggDimensionValue, Hashtable<String,Float> map) {
		SingleDimension sd = aggDimensionHash.get(key);
		if( sd == null ) {
			sd = new SingleDimension(aggDimension);
			aggDimensionHash.put(key,  sd);
		}
		sd.addMap(aggDimensionValue, map);
		
		return true;
	}
	
	protected void print() {
		System.out.printf( "Aggregated Dimension Index is %d\n", this.aggDimension);
		
		for( Enumeration<String> enumKeys = aggDimensionHash.keys(); enumKeys.hasMoreElements(); ) {
			String key = enumKeys.nextElement();
			SingleDimension sd = aggDimensionHash.get(key);
			
			for( int i = 0; i < sd.aggDimensionMapArray.length; i++ ) {
				Hashtable<String,String>map = (Hashtable<String, String>)(sd.aggDimensionMapArray[i]);
				if( map != null ) {
					System.out.printf( "Key = [%s] Aggregated Dimension Value = [%d]\n", key, i);
					System.out.printf( "Map:\n");
					for( Enumeration<String> enumMapKeys = map.keys(); enumMapKeys.hasMoreElements(); ) {
						String mapKey = enumMapKeys.nextElement();
						System.out.printf( "Key: [%s] Value [%s]\n", mapKey, map.get(mapKey));
					}
				
				}
			}
		}
	}
	
	/*
	 * 1 - Populate the leaves by either processing from file or from a prior dimension processing.
	 * 2 - Populate the parents of the keys by traversing the dimension hierarchy.
	 * 3 - We do this for EACH key in this current aggregation.
	 * 4 - While doing this, we populate the next Dimension Aggregation so we can repeat the process.
	 */
	public void processKeys(KeysForNextAggregator nextAggregator) {
		Dimension thisDimension = Application.getApp().dimensionArray.get(this.aggDimension);
		
		// System.out.printf( "Printing the dimension\n");
		// thisDimension.root.printPreOrder();
		
		/* Go through each key and process it */
		Iterator<Map.Entry<String, SingleDimension>> entries = this.aggDimensionHash.entrySet().iterator();
		while( entries.hasNext()) {
			Map.Entry<String, SingleDimension> entry = entries.next();
			String key = entry.getKey();
			SingleDimension sd = entry.getValue();
			
			doProcess(thisDimension.root, sd.aggDimensionMapArray);
			
			/* propagate these keys to the next level */
			addEntriesToNextAggregator(nextAggregator, key, sd);
		}
	}
	
	/*
	 * Return a merged list of all children hashmaps.  Set the value in the array, indexed by the value 
	 * of the dimension in the tree.
	 * 
	 * Note that the leaf entries of the tree are already populated in the array.
	 * 
	 */
	private Hashtable<String, Float> doProcess(MyTreeNode<Integer> node, Object aggDimensionMapArray[]) {
		Hashtable<String, Float> mine = null;
		
		if( node.children.size() == 0 ) {
			mine = (Hashtable<String, Float>)aggDimensionMapArray[node.data];
		} else {
			mine = new Hashtable<String, Float>();
			Iterator<MyTreeNode<Integer>> iter = node.children.iterator();
			while( iter.hasNext() ) {
				Hashtable<String,Float> childMap = doProcess(iter.next(), aggDimensionMapArray);
				if( childMap != null)
					mergeMaps(mine, childMap);
			}
			
			/* Store the map in the array */
			aggDimensionMapArray[node.data] = mine;
		}
		return (mine != null && mine.size() > 0) ? mine : null;
	}
	
	private void mergeMaps(Hashtable<String, Float> target, Hashtable<String,Float> src) {
		Iterator<Map.Entry<String,Float>> iter = src.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<String, Float> entry = iter.next();
			Float targetVal = target.get(entry.getKey()); 
			if( targetVal != null ) {
				target.put(entry.getKey(), targetVal + entry.getValue());
			} else {
				target.put(entry.getKey(), entry.getValue());
			}
		}
	}
	
	/*
	 * After we have generated all combinations for this dimension, we move to the next dimension.
	 * 
	 * Note that the current key is for the current dimension.  We need to reconstruct the key
	 * for the next dimension. 
	 */
	private void addEntriesToNextAggregator(KeysForNextAggregator nextAggregator, String key, SingleDimension thisSingleDimension) {
	}
}




