import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
	private int port = 1337;
	private String host = "127.0.0.1";
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	
	Client() {
		try {
			this.socket = new Socket(this.host, this.port);
			this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.out = new PrintWriter(this.socket.getOutputStream(), true);
		} catch(IOException e) {
			System.err.println("Couldn't connect to host.");
		}
	}
	
}
