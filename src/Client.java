import java.io.IOException;
import java.net.Socket;

public class Client {
	private String host;
	private int port;
	private Socket socket;
	
	Client() {
		this(Server.defaultHost, Server.defaultPort);
	}
	
	Client(String host, int port) {
		this.host = host;
		this.port = port;
		try {
			this.socket = new Socket(this.host, this.port);
		} catch(IOException e) {
			System.err.println("Couldn't connect to host.");
		}
	}
	
}
