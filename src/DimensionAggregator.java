/*
 * TBD 1) We are storing a HashMap of String, String for the Map for a specific dimension value.
 *        We are doing this since we are storing the "_id=" value also in this map.
 *        If this turns out to be inefficient, we need to store the _id= separately and make
 *        the map to be a String,Float map. 
 *        
 *        ** update this is done kind of.  Not happy with the re-allocation of Hashtable.
 *        
 */

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/*
 * Aggregates data per dimension.
 * 
 * Stores Aggregated dimension (Index into Application.dimensions)
 *    This is the dimension we are aggregating on.
 * 
 * The structure is a Hashtable of Keys to SingleDimension
 * 
 * - A key is constructed with all dimensions except the one we are aggregating on.
 * - SingleDimension is another class that encapsulates an array of all possible values of the Aggregated Dimension.
 * - Each entry in the array will point to the "Map" of values which contains the three dimensions that are pre-aggregated. 
 * 
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
		private int maxDimensionValue;
		
		SingleDimension(int aggDimension) {
			/* create array of all possible values */
			this.maxDimensionValue = Application.getApp().dimensionArray.get(aggDimension).maxValue+1;
			aggDimensionMapArray = null;
		}
		
		protected boolean addMap(int aggDimensionValue, NVArrayList map) {
			if( aggDimensionMapArray == null )
				aggDimensionMapArray = new Object[this.maxDimensionValue];

			if( aggDimensionValue >= maxDimensionValue ) {
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
		
		public void createHashtable() {
			for( int i = 0; i < this.aggDimensionMapArray.length; i++ ) {
				Object o = this.aggDimensionMapArray[i];
				if( o != null ) {
					NVArrayList nva = (NVArrayList)o;
					this.aggDimensionMapArray[i] = nva.getHashtable();
				}
				o = null;
			}
		}
		
		public void clearHashtable() {
			for( int i = 0; i < this.aggDimensionMapArray.length; i++ ) {
				Object o = this.aggDimensionMapArray[i];
				if( o != null ) {
					o = null;
				}
				this.aggDimensionMapArray[i] = null;
			}
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
	
	/* TBD - check if are using this */
	public boolean addMapEntry(String key, int aggDimensionValue, NVArrayList map) {
		SingleDimension sd = aggDimensionHash.get(key);
		if( sd == null ) {
			sd = new SingleDimension(aggDimension);
			aggDimensionHash.put(key,  sd);
		}
		sd.addMap(aggDimensionValue, map);
		
		return true;
	}
	
	/* This is called after an initial read from the Mongo text file */
	@Override
	public boolean addMapEntry(String key, int aggDimensionValue, Hashtable<Short,Float> map) {
		SingleDimension sd = aggDimensionHash.get(key);
		if( sd == null ) {
			sd = new SingleDimension(aggDimension);
			aggDimensionHash.put(key,  sd);
		}
		
		/* convert this into a NVArrayList */
		NVArrayList nva = new NVArrayList();
		nva.readFrom(map);
		sd.addMap(aggDimensionValue, nva);

		map = null;
		
		return true;
	}
	
	@Override 
	public int getNumKeys() { 
		return this.aggDimensionHash.size();
	}
	
	@Override
	public int getNumValues() {
		Iterator<Map.Entry<String, SingleDimension>> entries = this.aggDimensionHash.entrySet().iterator();
		int totalEntries = 0;
		while( entries.hasNext()) {
			Map.Entry<String, SingleDimension> entry = entries.next();
			String key = entry.getKey();
			SingleDimension sd = entry.getValue();
			
			/* count the number of items */
			for( Object o : sd.aggDimensionMapArray ) 
				if( o != null) 
					totalEntries++;
		}
		
		return totalEntries;
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
	
	/* not used currently */
	public void saveToFile(String file) {
		try {
			PrintWriter out = new PrintWriter(file);
			
			Iterator<Map.Entry<String, SingleDimension>> entries = this.aggDimensionHash.entrySet().iterator();

			int total = 0;
			while( entries.hasNext()) {
				System.out.printf( "Writing [%d] key\n",  ++total);
				
				if( total > 100)
					break;
				
				Map.Entry<String, SingleDimension> entry = entries.next();
				String key = entry.getKey();
				SingleDimension sd = entry.getValue();
				
				out.printf( "%s ", key);
				for( int i = 0; i < sd.aggDimensionMapArray.length; i++ ) {
					Object o = sd.aggDimensionMapArray[i];
					if( o != null ) {
						out.printf( "%d ",  i );
						NVArrayList nva = (NVArrayList)o;
						nva.print(out);
					}
				}
				out.printf("\n");
			}
			
			out.close();
		} 
		catch(FileNotFoundException e) {
			System.out.printf( "Cannot open file [%s] for writing\n",  file);
		}
	}
	
	/*
	 * 1 - Populate the leaves by either processing from file or from a prior dimension processing.
	 * 2 - Populate the parents of the keys by traversing the dimension hierarchy.
	 * 3 - We do this for EACH key in this current aggregation.
	 * 4 - While doing this, we populate the next Dimension Aggregation so we can repeat the process.
	 */
	public void processKeys(KeysForNextAggregator nextAggregator, String filename) {
		Dimension thisDimension = Application.getApp().dimensionArray.get(this.aggDimension);
		
		// System.out.printf( "Printing the dimension\n");
		// thisDimension.root.printPreOrder();
		try {
			PrintWriter out = null;
			
			if( nextAggregator == null && filename != null ) out = new PrintWriter(filename);

			System.out.printf( "Entering: processKeys - Dimension=[%d] DimensionSize:[%d] #keys [%d] #values [%d]\n",  
								aggDimension,
								Application.getApp().dimensionArray.get(aggDimension).maxValue+1,
								this.getNumKeys(),
								this.getNumValues());
								
			
			/* Go through each key and process it */
			Iterator<Map.Entry<String, SingleDimension>> entries = this.aggDimensionHash.entrySet().iterator();
			int numKeys = 0;
			int totalEntries = 0;
			while( entries.hasNext()) {
				Map.Entry<String, SingleDimension> entry = entries.next();
				String key = entry.getKey();
				SingleDimension sd = entry.getValue();
				
				/* before processing it, convert into Hashtable */
				sd.createHashtable();
				
				doProcess(thisDimension.root, sd.aggDimensionMapArray);
				
				/* propagate these keys to the next level */
				if( aggDimension > 0 ) 
					addEntriesToNextAggregator(nextAggregator, key, sd);

				/* write to the file for the last aggregation */
				if( out != null ) {
					out.printf( "%s ", key);
					for( int i = 0; i < sd.aggDimensionMapArray.length; i++ ) {
						Object o = sd.aggDimensionMapArray[i];
						if( o != null ) {
							out.printf( "%d ",  i );
							Hashtable<Short, Float> map = (Hashtable<Short, Float>)o;
							Iterator<Map.Entry<Short, Float>> iter = map.entrySet().iterator();
							while( iter.hasNext() ) {
								Map.Entry<Short, Float> mapEntry = iter.next();
								out.printf("%d:%.2f,", mapEntry.getKey(), mapEntry.getValue());
							}
						}
					}
					out.printf("\n");
				}
				
				
				/* count the number of items */
				for( Object o : sd.aggDimensionMapArray ) 
					if( o != null) 
						totalEntries++;
				
				/* Free up storage for this key */
				this.aggDimensionHash.put(key, new SingleDimension(aggDimension));
				
				sd.clearHashtable();
				
				sd = null;
	
				numKeys++;
				
				if( numKeys % 50000 == 0 ) {
					System.out.printf( "processed: keys# [%d] Values# [%d] - added [%d] keys for next \n", 
							numKeys, totalEntries, nextAggregator != null ? nextAggregator.getNumKeys() : 0);
					if( numKeys % 100000 == 0 ) {
						Runtime runtime = Runtime.getRuntime();
						//runtime.gc();
						long memory = runtime.totalMemory() - runtime.freeMemory();
						System.out.printf( "Memory used: [%d]MB\n",  memory / (1024*1024));
						System.out.printf( "Time used [%d]\n",  Application.timer.time() / 1000 );
					}
				}
			}
			System.out.printf( "Completed: processKeys - Dimension [%d] #values [%d]; NEXT dimension #keys: [%d] #values: [%d]\n",  
					aggDimension, totalEntries, 
					nextAggregator != null ? nextAggregator.getNumKeys() : 0, 
					nextAggregator != null ? nextAggregator.getNumValues() : 0 );
			if( out != null ) out.close();
		} 
		catch(FileNotFoundException e) {
			System.out.printf( "Cannot open file [%s] for writing\n",  filename);
		}
		
	}
	
	/*
	 * Return a merged list of all children hashmaps.  Set the value in the array, indexed by the value 
	 * of the dimension in the tree.
	 * 
	 * Note that the leaf entries of the tree are already populated in the array.
	 * 
	 */
	private Hashtable<Short, Float> doProcess(MyTreeNode<Integer> node, Object aggDimensionMapArray[]) {
		Hashtable<Short, Float> mine = null;
		
		if( node.children.size() == 0 ) {
			mine = (Hashtable<Short, Float>)aggDimensionMapArray[node.data];
		} else {
			mine = new Hashtable<Short, Float>();
			Iterator<MyTreeNode<Integer>> iter = node.children.iterator();
			while( iter.hasNext() ) {
				Hashtable<Short,Float> childMap = doProcess(iter.next(), aggDimensionMapArray);
				if( childMap != null)
					mergeMaps(mine, childMap);
			}
			
			/* Store the map in the array */
			aggDimensionMapArray[node.data] = (mine != null && mine.size() > 0) ? mine : null;
		}
		return (mine != null && mine.size() > 0) ? mine : null;
	}
	
	private void mergeMaps(Hashtable<Short, Float> target, Hashtable<Short,Float> src) {
		Iterator<Map.Entry<Short,Float>> iter = src.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<Short, Float> entry = iter.next();
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
		/*
		 * Go through each possible value of the dimension.
		 */
		if(aggDimension == 0)
			return;
		
		DimensionKey dk = new DimensionKey();
		for( int i = 0; i < thisSingleDimension.aggDimensionMapArray.length; i++ ) {
			Hashtable<Short, Float> map = (Hashtable<Short, Float>)thisSingleDimension.aggDimensionMapArray[i];
			if(map != null) {
				/* generate the new key */
				dk.split(key,  aggDimension, i, aggDimension-1);
				nextAggregator.addMapEntry(dk.newKey, dk.aggDimensionValue, map);
				map = null;
			}
		}
	}
}




