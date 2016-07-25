import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class CapitalizeServer {
	public static void main(String args[]) throws Exception {
		if( args.length != 1) {
			System.out.printf( "Usage: CapitalizeServer <portnumber>\n", args);
			System.exit(1);
		}
		int port = Integer.parseInt(args[0]);
		if( port <= 1000 ) {
			System.out.printf( "Invalid port number %s\n", args[0]);
			System.exit(1);
		}
		
		System.out.println( "Capitalization server is running" );
		int clientNumber = 0;
		ServerSocket listener = new ServerSocket(port);
		try {
			while(true) {
				new Capitalizer(listener.accept(), clientNumber++).start();
			}
		} finally {
			listener.close();
		}
	}
	
	private static class Capitalizer extends Thread {
		private Socket socket;
		private int clientNumber;
		
		public Capitalizer(Socket socket, int clientNumber) {
			this.socket = socket;
			this.clientNumber = clientNumber;
			log( "new connection with client% " + clientNumber + " at " + socket );
		}
		
		public void run() {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				
				out.println( "you are client # " + clientNumber + "." );
				out.println( "Enter a line with only a period to quit.");
				
				while( true ) {
					String input = in.readLine();
					if( input == null || input.equals(".")) {
						break;
					}
					out.println(input.toUpperCase());
				}
			} catch(IOException e) {
				log("Error jandling client #" + clientNumber + ":" + e);
			} finally {
				try {
					socket.close();
				} catch(IOException e) {
					log( "could not close socket");
				}
				log("connection with client #" + clientNumber + " closed");
			}
		}
		
		public void log(String message) {
			System.out.println(message);
		}
	}
}
