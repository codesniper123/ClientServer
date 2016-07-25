
/*
 * utility class to parse key into two parts:
 * 
 * Aggregated Dimension Value
 * Remaining Key.
 * 
 * e.g:
 * 
 * Complete Key = 100_3_3_128_2_2_4
 * This stands for 7 dimensions with values: 
 * Index  Value
 * 0		100
 * 1		3
 * 2		3
 * 3		128
 * 4		2
 * 5		2
 * 6		4
 * 
 * if the Aggregated Dimension is 3, then we split the key into:
 * 
 * Aggregated Dimension Value = 128
 * New Key = 100_3_3_2_2_4
 */
public class DimensionKey {
	protected int aggDimensionValue;
	String newKey;
	
	public DimensionKey() {
		aggDimensionValue = -1;
		newKey = null;
	}
	
	public boolean split(String completeKey, int aggDimension) {
		int completeKeyLen = completeKey.length();
		
		if( aggDimension >= Application.getApp().dimensionArray.size() ) {
			System.out.printf( "Aggregated Dimension exceeds known dimensions\n" );
			return false;
		}
		
		/* skip so many underscores */
		int index = 0;
		for( int i = 0; i < aggDimension; i++ ) {
			for( ; index < completeKeyLen; index++ ) {
				if(completeKey.charAt(index) == '_') {
					index++;
					break;
				}
			}
			if( index == completeKeyLen) {
				System.out.printf( "Invalid complete key [%s]\n",  completeKey);
				return false;
			}
		}
		
		/* find the end index: */
		int endIndex = index+1;
		for( ; endIndex < completeKeyLen && completeKey.charAt(endIndex) != '_'; endIndex++ )
			;
		if( (endIndex - index) == 0) {
			System.out.printf( "Could not find number for specified aggregated dimension [%d] in [%s]", 
							aggDimension, completeKey );
			return false;
		}
		
		String value = completeKey.substring(index, endIndex);
		aggDimensionValue = Integer.parseInt(value);
		
		/* form the new key */
		String pre = "";
		if( index > 0 )
			pre = endIndex < completeKeyLen ?  completeKey.substring(0, index) : completeKey.substring(0,index-1);
		
		String post = endIndex < completeKeyLen ? completeKey.substring(endIndex+1, completeKey.length()) : "";
		newKey = pre + post;
		
		return true;
	}
}
