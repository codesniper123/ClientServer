/*
 *  Removes and stores the _id key so all other entries are Floats. 
 */

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class MyJSONParserFloat {
	protected MyJSONParser parser;
	protected String _idValue;
	
	public MyJSONParserFloat() {
		parser = new MyJSONParser();
	}
	
	protected Hashtable<String, Float> parse(String line) {
		Hashtable<String, Object> hash = parser.parse(line);
		if( hash == null)
			return null;
		
		_idValue = (String)hash.remove("_id");
		if( _idValue == null) {
			System.out.printf( "No key present in line [%s]", line);
			return null;
		}
		
		Hashtable<String,Float> myhash =  new Hashtable<String, Float>();
		
		Iterator<Map.Entry<String,Object>> entries = hash.entrySet().iterator();
		while( entries.hasNext()) {
			Map.Entry<String,Object> entry = entries.next();
			myhash.put(entry.getKey(), (Float)entry.getValue());
		}
		
		return myhash;
	}
}
