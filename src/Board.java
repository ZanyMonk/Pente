import java.util.ArrayList;
import java.util.HashMap;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.awt.BorderLayout;
import java.awt.event.WindowEvent;

import java.io.IOException;

import javax.imageio.ImageIO;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class Board extends JPanel {
	private BufferedImage	background;
	private int 		margin = 25;
	private int 		cellSize = 30;
	private int			headerSize = 25;
	private int			playerColor = 0;
	private int			currentPlayer = 0; // White begins
	private int[]		eatenPawns = new int[2];
	private boolean		playing = false;
	private JPanel		status = new JPanel();
	private MainWindow	parent = null;
	private Server		server = null;
	private Client		client = null;
	private Opponent	opponent = new Opponent();
	
	Board() {
		super();
		
		try {
			background = ImageIO.read(Board.class.getResource("/background.jpg"));
		} catch (IOException e) {
			System.out.println("Couldn't load background image.");
		}

		setLayout(null);
		
		initBoard();
	}
	
	Board(MainWindow parent) {
		this();
		
		this.parent = parent;
		
		this.parent.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				((MainWindow)e.getWindow()).board.quit();
				System.exit(0);
			}
		});
	}
	
	private void setStatusText(String text) {
		this.setStatusText(text, "white");
	}
	
	private void setStatusText(String text, String color) {
		((JLabel)this.status.getComponents()[0]).setText("");
		this.repaint();
		((JLabel)this.status.getComponents()[0]).setText(
			"<html>"
				+"<font style='font-weight:100;' color='white'>White " + this.eatenPawns[0] + " | Black " + this.eatenPawns[1] + " - </font>"
				+"<font style='font-weight:100;' color='"+color+"'>"+text+"</font>"
			+"</html>"
		);
		this.status.repaint();
	}
	
	private void setStatusError(String text) {
		this.setStatusText(text, "red");
	}
	
	private void setStatusSuccess(String text) {
		this.setStatusText(text, "#7ABF30");
	}

	private void initBoard() {
		removeAll();
		
		this.eatenPawns[0] = 0;
		this.eatenPawns[1] = 0;
		
		this.status = new JPanel(new BorderLayout());
		this.status.add(new JLabel(""));
		this.status.setBounds(5, 613, 623, 20);
		this.status.setBackground(new Color(0,0,0,0));
		add(this.status);
		this.setStatusText("");
		Cell c;
		for (int y = 19; y > 0; y--) {
			for (int x = 19; x > 0; x--) {
				c = new Cell(x*this.cellSize+20, y*this.cellSize+20, x, y);
				add(c);
			}
		}
		repaint();
	}
	
	private Cell getCellAt(int x, int y) {
		x = (x < 0 ? 0 : (x > 18 ? 18 : x)); // x & y in [0, 18]
		y = (y < 0 ? 0 : (y > 18 ? 18 : y));
		Component[] compos = getComponents();
		return (Cell)compos[compos.length-(19*y+x)-1];
	}
	
	private void win() {
		this.playing = false;
		
		if(this.isPlayingOnline()) {
			if(this.server != null) {
				this.server.sendWin(this.playerColor);
			} else {
				this.client.sendWin(this.playerColor);
			}
		}
		
		if(this.win(this.currentPlayer)) {
			this.nextTurn();
			this.newGame();
			this.nextTurn();
		}
	}
	
	private int countPlayedCells() {
		int count = 0;
		
		Cell c;
		for (int y = 18; y >= 0; y--) {
			for (int x = 18; x >= 0; x--) {
				c = this.getCellAt(x, y);
				if(c.isPlayed()) {
					count++;
				}
			}
		}
		
		return count;
	}
	
	public boolean win(int color) {
		return JOptionPane.showConfirmDialog(
			this,
			(color == 0 ? "White" : "Black")+" wins the game !\nDo you want to start a new game ?",
			"Victory !",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.INFORMATION_MESSAGE
		) == 0;
	}

	public void blurCells() {
		if(this.isPlaying()) {
			Cell c;
			for (int y = 18; y >= 0; y--) {
				for (int x = 18; x >= 0; x--) {
					c = this.getCellAt(x, y);
					c.blur();
				}
			}
		}
	}
	
	public int getPlayer() {
		return this.currentPlayer;
	}
	
	public int nextTurn() {
		this.currentPlayer = (this.currentPlayer+1)%2;
		
		this.setStatusText((this.currentPlayer == 0 ? "White" : "Black")+" turn.");
		
		return this.currentPlayer;
	}
	
	// Indexes
	//			0
	//				1
	//			C		2
	//				3
	//
	// 0 = North, 1 = North-East etc ...
	//
	// Value
	// -1 = no alignment
	// 0 = five aligned
	// 1 = two enemies aligned and surrounded
	public int[] checkAlignement(Cell source, int player) {
		int[] res = { -1, -1, -1, -1 };
		
		int v = 0, h = 1;
		
		for(int i = 0; i < 4; i++) { // Test N, NE, E, SE
			if(!(v == 1 && h == 1)) {	// Exclude null speed
				int n = 5;
				int x, y;
				int winCount = 1;
				int eatCount = 0;
				boolean sourcePassed = false;
				
				// We go to the farthest accessible intersection from the source toward a direction between N and SE
				// clockwise.
				// For each direction, we go back to our origin cell (source) and continue further until we reach the
				// fourth cell behind the origin or a side of the board.
				while(n > -4) {
					n--;
					x = source.iX + (h - 1) * n;
					y = source.iY + (v - 1) * n;
					
					// Is intersection out of the board ?
					if(x > 18 || 0 > x || y > 18 || 0 > y)
						continue;
					
					// Intersection is valid
					Cell cell = this.getCellAt(x, y);
					
					if(cell == source || cell.getColor() == player) { // Pawn is ours
						// Check fourth pawn of eat move
						if(
							sourcePassed && eatCount == 3 ||
							cell != source && !sourcePassed && eatCount == 0 ||
							cell == source && (eatCount == 3 || eatCount == 0)
						) {
							eatCount++;
						} else {
							eatCount = 0;
						}
						winCount++;
					} else {
						// Check second and third pawns of eat move
						if(cell.isPlayed() && (eatCount == 1 || eatCount == 2))
							eatCount++;
						else
							eatCount = 0;
						winCount = 0;
					}

					// Check if two enemy pawns are surrounded by two of our ones
					if(eatCount >= 4) {
						res[i] = (sourcePassed ? 1 : 2);
					}

					if(winCount >= 5) {	// Yeah ... you win !
						res[i] = 0;
					}
					
					if(cell == source)
						sourcePassed = true;
				}
			}

			// Rotate N > NE > E > SE
			if(i % 3 == 0)
				h = (h + 1) % 3;
			else
				v = (v + 1) % 3;
		}
		
		
		return res;
	}
	
	public boolean checkMove(Cell cell) {
		return this.checkMove(cell, true);
	}
	
	public boolean checkMove(Cell cell, boolean played) {
		if(cell.isPlayed()) {
			return false;
		}
		
		if(this.countPlayedCells() == 1 && (Math.abs(cell.iX-9) > 3 || Math.abs(cell.iY-9) > 3)) {
			return false;
		}
		if(played) {
			int[] r = this.checkAlignement(cell, this.currentPlayer);
		
			for(int i = r.length - 1; i >= 0; i--) {
				if(r[i] == 0) {
					this.win();
				} else if(r[i] == 1 || r[i] == 2) {
					int xWay = 0, yWay = 0;
					switch(i){
						case 0:	// North
							xWay = 0;
							yWay = r[i] == 1 ? 1 : -1;
							break;
						case 1:	// North-East
							xWay = r[i] == 1 ? -1 : 1;
							yWay = r[i] == 1 ? 1 : -1;
							break;
						case 2:	// East
							xWay = r[i] == 1 ? -1 : 1;
							yWay = 0;
							break;
						case 3:	// South-East
							xWay = r[i] == 1 ? -1 : 1;
							yWay = r[i] == 1 ? -1 : 1;
							break;
						default:	// Error ?
							break;
					}
					this.getCellAt(cell.iX+1*xWay, cell.iY+1*yWay).eat();
					this.getCellAt(cell.iX+2*xWay, cell.iY+2*yWay).eat();
					this.eatenPawns[this.currentPlayer] += 2;
					
					int n = this.eatenPawns[this.currentPlayer];
					if(n >= 10) {
						this.win();
					}
				}
			}
		}
		
		return true;
	}
	
	public boolean isPlaying() {
		return this.playing;
	}
	
	public boolean confirmNoSave() {
		return JOptionPane.showConfirmDialog(
			this,
			"Your current game won't be saved. Are you sure to start a new game ?",
			"New game",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE
		) == 0;
	}
	
	public void setPlayerColor(int c) {
		this.playerColor = c;
	}
	
	public void newGame() {
		if(!this.isPlaying() || this.confirmNoSave()) {
			this.playing = false;
			this.initBoard();
			Cell first = this.getCellAt(9, 9);
			first.setColor(this.currentPlayer);
			first.play();
			this.nextTurn();
			this.setStatusText("New game started.");
			this.playing = true;
		}
	}
	
	public void quit() {
		if(this.isPlayingOnline()) {
			if(this.client != null) {
				this.client.sendQuit();
			}
		}
	}
	
	public boolean join(String host, int port) {
		try {
			this.client = new Client("Unknown", host, port, this);
			this.setStatusSuccess("Client connected.");
			return true;
		} catch(IOException e) {
			this.setStatusError("Couldn't connect to host.");
			return false;
		}
	}
	
	public boolean host(String rawPort) {
		int port = Server.defaultPort;
		boolean error = false;
		try {
			port = Integer.parseInt(rawPort);	
		} catch(NumberFormatException err) {
			error = true;
			this.setStatusError("This is no valid port.");
		}
		if(port == 0 || error) {
			return false;
		}
		
		this.server = new Server(port, this);
		this.server.start();
		this.setStatusText("Waiting for an opponent on port "+port+" ...");
		return true;
	}
	
	public void shutdownServer() {
		if(this.server != null) {
			this.server.closeSocket();
			this.server = null;
		}
	}
	
	public void play(Cell cell) {
		cell.play();
		if(this.isPlayingOnline()) {
			if(this.server != null) {
				this.server.sendMove(cell);
			} else {
				this.client.sendMove(cell);
			}
		}
	}
	
	public boolean makeMove(HashMap<String, String> cmd) {
		for(String c : cmd.keySet()) {  // Java8 needed
			String data = cmd.get(c);
			switch(c) {
				case "HELLO":
					if(!this.opponent.isConnected()) {
						this.opponent.login(data);
						this.newGame();
						this.setStatusText("Opponent named \""+data+"\" has connected");
						this.server.sendStart();
					}
					break;
				case "QUIT":
					if(this.opponent.isConnected()) {
						this.opponent.logout();
						this.setStatusError("Opponent just quitted");
						this.server.closeSocket();
					}
					break;
				case "MOVE":
					if(this.opponent.isConnected() && this.currentPlayer != this.playerColor) {
						int x, y;
						String[] coords = data.split(",");
						x = Integer.parseInt(coords[0]);
						y = Integer.parseInt(coords[1]);
						return this.playOpponentMove(x, y);
					}
					break;
				case "WIN":
					this.win(data == "WHITE" ? 0 : 1);
					break;
			}
		}
		return true;
	}
	
	public boolean playOpponentMove(int x, int y) {
		Cell cell = this.getCellAt(x, y);
		if(this.checkMove(cell)) {
			cell.setColor((this.playerColor+1)%2);
			cell.play();
			this.checkMove(cell);
			this.nextTurn();
			this.repaint();
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isPlayingOnline() {
		return this.server != null || this.client != null;
	}

	public boolean isLocalPlayerTurn() {
		return this.currentPlayer == this.playerColor;
	}

	@Override
	public void paintComponent(Graphics G) {
		super.paintComponent(G);
		Graphics2D g = (Graphics2D)G;
		g.drawImage(this.background, 0, 0, this);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(new Color(235, 235, 235));
		
		// Draw grid
		FontMetrics fm = g.getFontMetrics();
		int n = 19;
		int max = this.margin+this.headerSize+this.cellSize*(n-1);
		int tWidth;
		String t;
		for(int i = max; i >= this.margin+this.headerSize; i -= this.cellSize) {
			g.drawLine(i, this.margin+this.headerSize, i, max);	// Vertical
			t = String.valueOf(n);
			tWidth = fm.stringWidth(t);
			g.drawString(t, this.margin+this.headerSize-25-tWidth/2, i+5);
			
			g.drawLine(this.margin+this.headerSize, i, max, i);		// Horizontal
			g.drawString(String.valueOf((char)(n+64)), i-4, this.margin+this.headerSize-20);
			
			n--;
		}
		
		// Draw point of reference
		int begin = this.margin+this.headerSize+this.cellSize*3;
		int end = this.margin+this.headerSize+this.cellSize*15;
		for(int y = end; y >= begin; y -= this.cellSize*6) {
			for(int x = end; x >= begin; x -= this.cellSize*6) {
				g.fillArc(x-4, y-4, 9, 9, 0, 360);
			}
		}
	}

}
