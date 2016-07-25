/*
 * TBD 1. Rewrite using Scanner.
 */

import java.util.Hashtable;

/*
 * 
 * Parses strings such as:
 * 
 * { "name":"value", "name2":"value2" }
 * 
 * State Machine:
 * 
 * INIT_STATE: Expect {
 * { STATE: NV
 * NV: Expect String
 *     Expect :
 *     Expect String OR Number
 * 	   Expect , OR }
 * } : EOF
 * , : NV 
 */
public class MyJSONParser {	
	final char COMMA = ',';
	final char OPEN_BRACE = '{';
	final char CLOSE_BRACE = '}';
	final char NULL_CHAR = 0;
	final char QUOTE = '"';
	final int EOF = -1;

	private int currentIndex = 0;
	private String str;
	private String currentToken = "";
	private Hashtable<String,Object> hash = new Hashtable<String, Object>();
	
	protected Hashtable<String, Object> parse(String s) {
		str = s;
		
		if( !expect(OPEN_BRACE, true)) return null;
		
		while(!eof(false)) {
			readNV();
			
			if (peeknext() == COMMA) {
				expect(COMMA, true);
				continue;
			} else if(peeknext() == CLOSE_BRACE) {
				/* make sure we hit EOF */
				expect(CLOSE_BRACE, true);
				
				skipBlanks();
				
				if( !eof(false) ) {
					System.out.printf( "Extra characters after processning data\n");
					return null;
				}
				break;
			} else if(eof(true)) {
				break;
			}
		}
		return hash;
	}
	
	boolean eof(boolean reportError) { 
		boolean ret  = currentIndex >= str.length();
		if( ret && reportError )
			System.out.printf( "Reached EOF without completing parsing.\n" );
		return ret;
	}
	
	boolean expect(char token, boolean reportError) {
		boolean ret = skipBlanks();
		if( ret ) { 
			ret = str.charAt(currentIndex) == token ? true : false;
			currentIndex++;
		}
		
		if( !ret )
			System.out.printf( "Could not find token [%c]\n", token);
		
		return ret;
	}
	
	boolean skipBlanks() {
		while(currentIndex < str.length() && (this.str.charAt(currentIndex) == ' ') )
			currentIndex++;
		return currentIndex >= str.length() ? false : true;
	}
	
	char peeknext() {
		if( !skipBlanks() ) return NULL_CHAR;
		return str.charAt(currentIndex);
	}
	
	/* returns string in currentToken */
	boolean readString() {
		currentToken = null;
		if( !expect( '"', true) ) 
			return false;
		
		int start=currentIndex;
		while( currentIndex < str.length() && str.charAt(currentIndex) != '"' ) {
			currentIndex++;
		}
		
		if( eof(true) ) {
			return false;
		}
		
		if( (currentIndex - start) == 0 ) {
			System.out.println( "Empty string for name or value\n" );
			return false;
		}
		
		currentToken = str.substring(start, currentIndex);
		
		return expect( '"', true);
	}
	
	boolean readNV() {
		if( !readString() )
			return false;
		String name = currentToken;

		if( !expect(':', true) )
			return false;

		/* We support a String Value or a Float */
		if( peeknext() == QUOTE) {
			if( !readString() )
				return false;
			if( hash.containsKey(name))
				return error("Duplicate key [%s]\n", name);
			hash.put(name,  currentToken);
		} else {
			if( !readNumber() )
				return false;
			if( hash.containsKey(name))
				return error("Duplicate key [%s]\n", name);
			hash.put(name, Float.parseFloat(currentToken));
		}
		
		return true;
	}
	
	boolean readNumber() {
		int index = currentIndex;
		for( ; currentIndex < str.length() ; currentIndex++)
			if( str.charAt(currentIndex) == ' '  ||
				str.charAt(currentIndex) == CLOSE_BRACE || 
				str.charAt(currentIndex) == COMMA)
			break;
			
		currentToken = str.substring(index, currentIndex);
		
		try {
			Float f = Float.valueOf(currentToken);
		} catch(NumberFormatException e ) {
			System.out.printf( "Invalid float in file: [%s]\n", currentToken);
			return false;
		}
		
		return true;
	}
	
	public boolean error(String format, Object...args) {
		System.out.format(format,  args);
		return false;
	}
}
