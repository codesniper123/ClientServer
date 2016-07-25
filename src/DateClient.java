import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class DateClient {
	public static void main(String args[]) throws IOException {
		System.out.printf( "number of arguments %d\n", args.length);
		
		if( args.length != 1) {
			System.out.printf( "Usage: DataClient <server>\n");
			System.exit(0);
		}
		
		String serverAddress = args[0];
		Socket s = new Socket(serverAddress, 9100);
		BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
		String answer = input.readLine();
		System.out.printf( "Date is %s\n", answer);
		System.exit(0);
	}
}
