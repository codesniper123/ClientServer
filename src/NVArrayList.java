import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

/*
 * Trying to save some memory.
 */
public class NVArrayList {
	static boolean asArray = true;
	
	static class NV {
		public short name;
		public float value;
		
		public NV(short name, float value) {
			this.name = name;
			this.value = value;
		}
	}
	
	protected ArrayList<NV> arrayList;
	private StringBuffer sb;
	
	public NVArrayList() { 
		if( asArray )
			arrayList = new ArrayList<NV>();
		else
			sb = new StringBuffer();
	}

	public void add(short name, float value) {
		if( asArray )
			arrayList.add( new NV(name, value));
		else
			sb.append(String.format("%d %.3f ",  name, value));
	}
	
	public Hashtable<Short, Float> getHashtable() {
		Hashtable<Short,Float> hash = new Hashtable<Short, Float>();
		if( asArray ) {
			for( int i = 0; i < arrayList.size(); i++ ) {
				NV nv = arrayList.get(i);
				if( hash.get(nv.name) != null) {
					System.out.printf("Duplicate key in NVArrayList::getHashtable\n");
				}
				hash.put(nv.name, nv.value);
			}
			
		} 
		else {
			Scanner s = new Scanner(sb.toString());
			s.useDelimiter(" ");
			while(s.hasNext()) {
				hash.put(s.nextShort(), s.nextFloat());
			}
			s.close();
		}
		return hash;
	}
	
	public boolean readFrom(Hashtable<Short, Float> hash) {
		if( asArray ) {
			this.arrayList = new ArrayList<NV>();
			
			Iterator<Map.Entry<Short,Float>> entries = hash.entrySet().iterator();
			while(entries.hasNext()) {
				Map.Entry<Short, Float> entry = entries.next();
				arrayList.add(new NV(entry.getKey(), entry.getValue()));
			}
		} 
		else {
			sb = new StringBuffer();
			Iterator<Map.Entry<Short,Float>> entries = hash.entrySet().iterator();
			while(entries.hasNext()) {
				Map.Entry<Short, Float> entry = entries.next();
				sb.append(String.format("%d %.3f ",  entry.getKey(), entry.getValue()));
			}
		}
		return true;
	}
	
	public void print(PrintWriter out) {
		if( asArray ) {
			for( int i = 0; i < arrayList.size(); i++ ) {
				NV nv = arrayList.get(i);
				out.printf( "%d:%.2f ", nv.name, nv.value);
			}
		}
	}
}
