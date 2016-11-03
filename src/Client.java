import java.io.IOException;

import java.net.InetSocketAddress;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public final class Client extends Thread {
	private String host, name;
	private int port;
	private SocketChannel socket;
	
	Client() throws IOException {
		this("Anonymous", Server.defaultHost, Server.defaultPort);
	}
	
	Client(String name, String host, int port) throws IOException {
		this.name = name;
		this.host = host;
		this.port = port;
		this.socket = SocketChannel.open(
			new InetSocketAddress(
				this.host,
				this.port
			)
		);
		
		this.sendHello();
	}
	
	private void sendMsg(String msg) {
		byte[] bytes = msg.getBytes();
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		try {
			this.socket.write(buff);
		} catch(IOException e) {
			System.err.println("Couldn't send "+msg.split(":")[0]+" to server.");
		}
	}
	
	public void sendHello() {
		this.sendMsg("HELLO:"+this.name);
	}
	
	public void sendQuit() {
		this.sendMsg("QUIT:"+this.name);
	}
	
}
