import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Dimension {

	protected MyTreeNode<Integer> root = null;
	protected int maxValue = -1;
	
	public Dimension(MyTreeNode<Integer>root, int maxValue) {
		this.root = root;
		this.maxValue = maxValue;
	}
	
	public static Dimension readSingleDimension(BufferedReader in) throws IOException {
		String line;
		MyTreeNode<Integer> root = null;

		/* keep track of the dimensions created so far */
		final int MAX_DIMENSION_VALUE = 1000;
		
        @SuppressWarnings("unchecked")
		Object [] dIndex = new Object[MAX_DIMENSION_VALUE];
		
		int maxValue = -1;
		for(int i = 0; i < MAX_DIMENSION_VALUE;i++)
			dIndex[i] = null;
			
		while( (line = in.readLine()) != null) {
			if( line.length() == 0) 
				return new Dimension(root, maxValue);
			
			String[] result = line.split(",");
			if( result.length == 1) {
				if( root != null ) {
					System.out.printf( "Error in input [%s]\n", line);
					return null;
				}
				root = new MyTreeNode<Integer>(Integer.parseInt(result[0]));
				dIndex[root.getData()] = root;
				maxValue = root.getData();
			} else {
				int child = Integer.parseInt(result[0]); 
				int parent = Integer.parseInt(result[1]);
				
				if( child > maxValue )
					maxValue = child;
				
				MyTreeNode<Integer> dParent = (MyTreeNode<Integer>)dIndex[parent];
				if( dParent == null ) {
					System.out.printf( "Cannot find parent dimension [%d]\n",  child);
					return null;
				}
				dIndex[child]  = dParent.addChild(child);
			}
		}

		return new Dimension(root, maxValue);
	}
	
	public static ArrayList<Dimension> readDimensions(String filename) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));

			ArrayList<Dimension> ad = new ArrayList<Dimension>();
			
			String line;
			
			while( (line = in.readLine()) != null) {
				// System.out.printf( "%s\n",  line);
				if( line.length() == 0 ) 
					continue;
				
				if( line.compareTo("Dimension") == 0) {
					Dimension d = readSingleDimension(in);
					ad.add(d);
				}
			}
			in.close();
			return ad;
		} 
		catch(FileNotFoundException e) {
			System.out.printf( "Cannot open file [%s]\n", filename);
			return null;
		}
		catch(IOException e) {
			System.out.printf("Error reading file [%s]\n", filename);
		}
		return null;
	}
}
