import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CapitalizerClient {
	public static void main(String args[]) {
		if( args.length != 2) {
			System.out.printf( "Usage: CapitalizerClient <server> <port>\n" );
			System.exit(1);
		}
		
		String server = args[0];
		int port = Integer.parseInt(args[1]);
		
		if( port <= 0 ) {
			System.out.printf( "Invalid port specified [%s]\n", args[1]);
			System.exit(1);
		}
		
		Socket socket = null;
		try {
			socket = new Socket(server, port);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			String response;
			for(int i = 0; i < 2; i++) {
				/* Read what the server wrote */
				response = in.readLine();
				System.out.printf( "message from server: [%s]\n",  response );
			}
			
			while( true ) {
				BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
				String inputString = console.readLine();
				out.println(inputString);
				if( inputString.length() == 0 || inputString.compareTo(".") == 0) 
					break;
				response = in.readLine();
				System.out.printf( "message from server: [%s]\n",  response );
			}
			
		} catch(IOException e) {
			System.out.printf( "Cannot connect to [%s] at [%d]\n", server, port);
			System.out.println(e);
		} finally {
			try {
				socket.close();
			} catch(IOException e) {
				System.out.println( "Error closing socket\n" );
			}
		}
	}
}
