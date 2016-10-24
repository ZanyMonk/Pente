import java.io.IOException;
import java.net.Socket;

public class Client {
	private int port = Server.defaultPort;
	private String host = Server.defaultHost;
	private Socket socket;
	
	Client() {
		try {
			this.socket = new Socket(this.host, this.port);
		} catch(IOException e) {
			System.err.println("Couldn't connect to host.");
		}
	}
	
}
