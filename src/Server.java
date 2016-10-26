import java.util.Arrays;
import java.util.HashMap;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;

public class Server extends Thread {
	public static int defaultPort = 1337;
	public static String defaultHost = "127.0.0.1";
	private int port = Server.defaultPort;
	private ServerSocketChannel server;
	private SocketChannel socket;
	private Board board;

	Server(Board board) {
		this(Server.defaultPort, board);
	}

	Server(int port, Board board) {
		super("Server");

		this.port = port;
		this.board = board;

		try {
			this.server = ServerSocketChannel.open();
			this.server.socket().bind(new InetSocketAddress(this.port));
			this.server.configureBlocking(false);
		} catch(IOException e) {
			System.err.println("Couldn't start server. Shit happens ...");
		}		
	}

	public void handleConnection() throws IOException {
		this.socket = this.server.accept();

		if(this.socket != null) {
			ByteBuffer buff = ByteBuffer.allocateDirect(1024);
			String data = "";
			@SuppressWarnings("unused")
			int code = 0;
			while((code = this.socket.read(buff)) > 0) {
				byte[] bytes = new byte[buff.position()];
				buff.flip();
				buff.get(bytes);
				data += new String(bytes, Charset.forName("UTF-8"));
			}

			this.board.makeMove(this.parsePacket(data.trim()));
		}
	}

	public HashMap<String, String> parsePacket(String data) {
		HashMap<String, String> cmd = new HashMap();
		String[] s = data.split(":");
		String[] validCmd = { "HELLO", "MOVE" };

		if(s.length > 1 && s.length%2 == 0) {
			for(int i = s.length/2; i >= 0; i -= 2) {
				if(Arrays.binarySearch(validCmd, s[i-1]) > -1) {
					cmd.put(s[i-1], s[i]);
				}
			}
		}
		return cmd;
	}
	
	public void closeSocket() {
		try {
			this.socket.close();
		} catch(IOException e) {
			System.err.println("Couldn't close socket.");
		}
	}

	@Override
	public void run() {
		try {
			while(true) {
				this.handleConnection();

				try {
					Thread.sleep(1000);

					synchronized(this) {
						while (Thread.currentThread().isInterrupted()) {
							wait();
						}
					}
				} catch(InterruptedException e) {
					System.err.println("Sleep error.");
				}
			}
		} catch(IOException e) {
			System.err.println("Error");
		}
	}

}
