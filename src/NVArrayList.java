import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	
	public enum Storage {
		AS_ARRAY, AS_STRING, AS_PERSIST
	};
	static Storage storage = Storage.AS_ARRAY;
	
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
	private byte[] byteArray;
	
	public NVArrayList() { 
		if( storage == Storage.AS_ARRAY)
			arrayList = new ArrayList<NV>();
		else if( storage == Storage.AS_STRING)
			sb = new StringBuffer();
		else if( storage == Storage.AS_PERSIST)
			byteArray = null;
	}

	/* not used? */
	public void add2(short name, float value) {
		if( storage == Storage.AS_ARRAY)
			arrayList.add( new NV(name, value));
		else if( storage == Storage.AS_STRING)
			sb.append(String.format("%d %.3f ",  name, value));
	}
	
	public Hashtable<Short, Float> getHashtable() {
		Hashtable<Short,Float> hash = new Hashtable<Short, Float>();
		if( storage == Storage.AS_ARRAY) {
			for( int i = 0; i < arrayList.size(); i++ ) {
				NV nv = arrayList.get(i);
				if( hash.get(nv.name) != null) {
					System.out.printf("Duplicate key in NVArrayList::getHashtable\n");
				}
				hash.put(nv.name, nv.value);
			}
			
		} 
		else if( storage == Storage.AS_STRING){
			Scanner s = new Scanner(sb.toString());
			s.useDelimiter(" ");
			while(s.hasNext()) {
				hash.put(s.nextShort(), s.nextFloat());
			}
			s.close();
		}
		else if( storage == Storage.AS_PERSIST) {
			if( byteArray == null ) {
				System.out.printf( "Empty byte array\n" );
				return null;
			}
			try {
				ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
				ObjectInputStream ois = new ObjectInputStream(bis);
				return (Hashtable<Short,Float>)ois.readObject();
			}
			catch(Exception e) {
				System.out.printf( "Error reading object from stream\n" );
				return null;
			}
		}
		return hash;
	}
	
	public boolean readFrom(Hashtable<Short, Float> hash) {
		if( storage == Storage.AS_ARRAY) {
			this.arrayList = new ArrayList<NV>();
			
			Iterator<Map.Entry<Short,Float>> entries = hash.entrySet().iterator();
			while(entries.hasNext()) {
				Map.Entry<Short, Float> entry = entries.next();
				arrayList.add(new NV(entry.getKey(), entry.getValue()));
			}
		} 
		else if( storage == Storage.AS_STRING){
			sb = new StringBuffer();
			Iterator<Map.Entry<Short,Float>> entries = hash.entrySet().iterator();
			while(entries.hasNext()) {
				Map.Entry<Short, Float> entry = entries.next();
				sb.append(String.format("%d %.3f ",  entry.getKey(), entry.getValue()));
			}
		}
		else if( storage == Storage.AS_PERSIST) {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				oos.writeObject(hash);
				this.byteArray = bos.toByteArray();
				oos.close();
			}
			catch(Exception e) {
				System.out.printf( "error creating output stream\n" );
				return false;
			}
		}
		return true;
	}
	
	public void print(PrintWriter out) {
		if( storage == Storage.AS_ARRAY) {
			for( int i = 0; i < arrayList.size(); i++ ) {
				NV nv = arrayList.get(i);
				out.printf( "%d:%.2f ", nv.name, nv.value);
			}
		}
	}
}
