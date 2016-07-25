import static org.junit.Assert.*;

import java.util.Enumeration;
import java.util.Hashtable;

import org.junit.Test;

public class MyJSONParserTest {

	@Test
	public void test() {
		String s = "{\"_id\":\"12\", \"12_34_56\":12.233}";
		
		MyJSONParser p = new MyJSONParser();
		Hashtable<String, Object> hash = p.parse(s);
		
		if( hash == null ) {
			System.out.println( "error in input\n" ); 
		} else {
			
			for( Enumeration<String> enumKeys = hash.keys(); enumKeys.hasMoreElements(); ) {
				String key = enumKeys.nextElement();
				if( key.compareTo("_id") == 0) {
					String val = (String)hash.get(key);
					System.out.printf( "Key [%s] Value [%s]\n", key, val);
				} else {
					float val = (Float)hash.get(key);
					System.out.printf( "Key [%s] Value [%f]\n", key, val);
					
				}
			}
		}
	}
}
