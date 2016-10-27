import java.io.IOException;
import java.net.Socket;

public class Client {
	private String host;
	private int port;
	private Socket socket;
	
	Client() throws IOException {
		this(Server.defaultHost, Server.defaultPort);
	}
	
	Client(String host, int port) throws IOException{
		this.host = host;
		this.port = port;
		this.socket = new Socket(this.host, this.port);
	}
	
}
