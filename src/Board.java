import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.awt.BorderLayout;

import java.io.IOException;

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
	}
	
	private void setStatusText(String text) {
		this.setStatusText(text, "white");
	}
	
	private void setStatusText(String text, String color) {
		((JLabel)this.status.getComponents()[0]).setText(
			"<html>"
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
		if(JOptionPane.showConfirmDialog(
			this,
			(this.currentPlayer == 0 ? "White" : "Black")+" wins the game !\nDo you want to start a new game ?",
			"Victory !",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.INFORMATION_MESSAGE
		) == 0) {
			this.nextTurn();
			this.newGame();
			this.nextTurn();
		}
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
			if(!(v == 1 && h == 1)) {
				int n = 5;
				int x, y;
				int winCount = 1;
				
				// We go to the farthest accessible intersection from the source toward a direction between N and SE
				// clockwise.
				// For each direction, we go back to our origin cell (source) and continue further until we reach the
				// fourth cell behind the origin or a side of the board.
				while(n > -4) {
					n--;
					x = source.iX + (h - 1) * n;
					y = source.iY + (v - 1) * n;
					
					if(x > 18 || y > 18) {	// Intersection is not accessible
						continue;
					}
					if(x < 0) {		// Intersection is out of the board
						break;
					}
					// Intersection is valid
					Cell cell = this.getCellAt(x, y);
					if(cell.isPlayed() && cell.getState() == player) { // Pawn is ours
						winCount++;
					} else {
						winCount = 1;
					}

					// DEV__Check if two enemy pawns are surrounded by two of our ones

					if(winCount >= 5) {	// Yeah ... you win !
						res[i] = 0;
					}
				}
			}

			if(i % 3 == 0) { // Rotate N > NE > E > SE
				h = (h + 1) % 3;
			} else {
				v = (v + 1) % 3;
			}
		}
		
		return res;
	}
	
	public boolean checkMove(Cell cell) {
		if(cell.isPlayed()) {
			return false;
		}

		if(Math.abs(cell.iX-9) > 3 || Math.abs(cell.iY-9) > 3) {
			return false;
		}
		
		int[] r = this.checkAlignement(cell, this.currentPlayer);
		
		for(int i = r.length - 1; i >= 0; i--) {
			if(r[i] == 0 && this.currentPlayer == this.playerColor) {
				this.win();
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
	
	public void newGame() {
		boolean confirm = true;
		
		if(this.playing) {
			confirm = this.confirmNoSave();
		}
		
		if(confirm) {
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
	
	public boolean join(String host, int port) {
		try {
			this.client = new Client("Unknown", host, port);
			this.setStatusSuccess("Client connected.");
			return true;
		} catch(IOException e) {
			this.setStatusError("Couldn't connect to host.");
			return false;
		}
	}
	
	public void host(String rawPort) {
		int port = Server.defaultPort;
		try {
			port = Integer.parseInt(rawPort);
		} catch(NumberFormatException err) {
			this.setStatusError("This is no valid port.");
		}
		this.server = new Server(port, this);
		this.server.start();
		this.setStatusText("Waiting for an opponent on port "+port+" ...");
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
						this.playOpponentMove(x, y);
					}
					break;
			}
		}
		return true;
	}
	
	public boolean playOpponentMove(int x, int y) {
		Cell cell = this.getCellAt(x, y);
		if(this.checkMove(cell)) {
			System.out.println("("+x+","+y+")");
			cell.setColor(this.currentPlayer);
			cell.play();
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
