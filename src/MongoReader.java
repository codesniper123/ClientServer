import java.io.*;
import java.util.Hashtable;

public class MongoReader {
	protected DimensionAggregator da;
	
	public MongoReader() {
		da = new DimensionAggregator( Application.getApp().dimensionArray.size() - 1);
	}
	
	public boolean readFile(String filename) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			
			String line = null;
			while( (line=in.readLine()) != null ) {
				if( line.length() == 0) 
					continue;
				parseLine(line);
			}
			
			in.close();
			return true;
		} catch(FileNotFoundException e) {
			System.out.printf( "Cannot open file [%s] for reading", filename);
			return false;
		} catch(IOException e) {
			System.out.printf( "Error while reading file [%s]\n", filename);
			return false;
		}
	}
	
	/*
	 * 1. Create a hash table with the entries.
	 * 2. Split the key for dimension = 6 using DimensionKey
	 * 3. Call DimensionAggregator with key, value, and the remaining hash table.
	 */
	public boolean parseLine(String line) {
		/* Step 1 - create Hash Table for the JSON line */
		MyJSONParserFloat mjp = new MyJSONParserFloat();
		Hashtable<String, Float> hash = mjp.parse(line);
		if( hash == null ) {
			System.out.printf( "Error in line [%s]\n", line);
			return false;
		}
		
		/* Now, form a new key and the value of the Aggregated Dimension */
		DimensionKey dk = new DimensionKey();
		if( !dk.split(mjp._idValue,  Application.getApp().dimensionArray.size() - 1)) {
			System.out.printf( "Invalid key [%s]\n", mjp._idValue );
			return false;
		}
		
		/* OK, we have the new key, value, and the hash map.
		 * Install it in the DimensionAggregator.
		 */
		da.addMapEntry(dk.newKey, dk.aggDimensionValue, hash);
		
		return true;
	}

}
