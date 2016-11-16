import java.io.IOException;

import java.util.Arrays;
import java.util.HashMap;

import java.net.InetSocketAddress;

import java.nio.charset.Charset;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public final class Client extends Thread {
	private String host, name;
	private int port;
	private SocketChannel socket;
	private Board board;
	
	Client(Board board) throws IOException {
		this("Anonymous", Server.defaultHost, Server.defaultPort, board);
	}
	
	Client(String name, String host, int port, Board board) throws IOException {
		this.name = name;
		this.host = host;
		this.port = port;
		this.board = board;
		
		this.sendHello();
	}
	

	public HashMap<String, String> parsePacket(String data) {
		HashMap<String, String> cmd = new HashMap();
		String[] s = data.split(":");
		String[] validCmd = { "START", "MOVE", "QUIT", "WIN" };

		if(s.length > 1 && s.length%2 == 0) {
			for(int i = s.length/2; i >= 0; i -= 2) {
				for(String c : validCmd) {
					if(c.compareTo(s[i-1]) == 0) {
						cmd.put(s[i-1], s[i]);
					}
				}
			}
		}
		return cmd;
	}
	
	private void sendMsg(String msg) {
		try {
			this.socket = SocketChannel.open(
				new InetSocketAddress(
					this.host,
					this.port
				)
			);
		} catch(IOException e) {
			System.err.println("Couldn't open socket.");
		}
		
		byte[] bytes = msg.getBytes();
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		try {
			this.socket.write(buff);
			this.handleConnection();
		} catch(IOException e) {
			System.err.println("Couldn't send "+msg.split(":")[0]+" to server.");
		}
	}
	
	public void sendHello() {
		this.sendMsg("HELLO:"+this.name);
	}
	
	public void sendQuit() {
		this.sendMsg("QUIT:"+this.name);
		System.out.println("ok");
	}
	
	public void sendMove(Cell cell) {
		this.sendMsg("MOVE:"+cell.iX+","+cell.iY);
	}
	
	public void closeSocket() {
		try {
			this.socket.close();
		} catch(IOException e) {
			System.err.println("Couldn't close socket.");
		}
	}

	public void handleConnection() throws IOException {
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
			
			if(code == -1) {
				this.closeSocket();
			} else {
				buff.clear();
			}
			

			this.makeMove(this.parsePacket(data.trim()));
		}
	}
	
	public boolean makeMove(HashMap<String, String> cmd) {
		for(String c : cmd.keySet()) {  // Java8 needed
			String data = cmd.get(c);
			switch(c) {
				case "START":
					if(data == "WHITE") {
						this.board.setPlayerColor(0);
					} else {
						this.board.setPlayerColor(1);
					}
					this.board.newGame();
					break;
				case "MOVE":
					if(this.board.isLocalPlayerTurn()) {
						int x, y;
						String[] coords = data.split(",");
						x = Integer.parseInt(coords[0]);
						y = Integer.parseInt(coords[1]);
						return this.board.playOpponentMove(x, y);
					}
					break;
				case "WIN":
					this.board.win(data == "WHITE" ? 0 : 1);
			}
		}
		
		return true;
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				// Wait for a move only when it's not our turn
				// or when the game isn't started yet
				if(!this.board.isPlaying() || !this.board.isLocalPlayerTurn()) {
					this.handleConnection();
				}

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
