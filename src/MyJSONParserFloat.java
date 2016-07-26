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
	
	protected Hashtable<Short, Float> parse(String line) {
		Hashtable<String, Object> hash = parser.parse(line);
		if( hash == null)
			return null;
		
		_idValue = (String)hash.remove("_id");
		if( _idValue == null) {
			System.out.printf( "No key present in line [%s]", line);
			return null;
		}
		
		Hashtable<Short,Float> myhash =  new Hashtable<Short, Float>();
		
		Iterator<Map.Entry<String,Object>> entries = hash.entrySet().iterator();
		while( entries.hasNext()) {
			Map.Entry<String,Object> entry = entries.next();
			myhash.put(MapStringToShort.getShort(entry.getKey()), (Float)entry.getValue());
		}
		
		return myhash;
	}
	
	public static int getNoOfShorts() {
		return MapStringToShort.getMapStringToShort().hash.size();
	}
	
	static class MapStringToShort {
		static Hashtable<String, Short> hash;
		static MapStringToShort mapStringToShort;
		static short val = 0;
		
		private MapStringToShort() {
			hash = new Hashtable<String,Short> ();
		}
		
		public static MapStringToShort getMapStringToShort() {
			if( mapStringToShort == null ) 
				mapStringToShort = new MapStringToShort();
			return mapStringToShort;
		}
		
		public static short getShort(String s) {
			if( getMapStringToShort().hash.get(s) == null) {
				getMapStringToShort().hash.put(s,val++);
			}
			// System.out.printf( "value of val [%d]\n",  val);
			return getMapStringToShort().hash.get(s);
		}
	}
}
