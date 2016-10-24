import java.util.HashMap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class Board extends JPanel {
	private BufferedImage	background;
	private int 		margin = 25;
	private int 		cellSize = 30;
	private int			headerSize = 25;
	private int			player = 0; // White begins
	private boolean		playing = false;
	private MainWindow	parent = null;
	private Server		server;
	private Client		client;
	
	public String		opponentMove = "";
	
	
	Board() {
		super();
		
		try {
			background = ImageIO.read(Board.class.getResource("/background.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Couldn't load background image.");
		}

		setLayout(null);

		initBoard();
	}
	
	Board(MainWindow parent) {
		this();
		
		this.parent = parent;
	}

	private void initBoard() {
		removeAll();
		Cell b;
		for (int y = 19; y > 0; y--) {
			for (int x = 19; x > 0; x--) {
				b = new Cell(x*this.cellSize+20, y*this.cellSize+20, x, y);
				add(b);
			}
		}
		repaint();
	}
	
	private Cell getCellAt(int x, int y) {
		return (Cell)this.getComponent(19*19-(19*(y < 0 ? 0 : y)+x)-1);
	}
	
	private void win() {
		this.playing = false;
		if(JOptionPane.showConfirmDialog(
			this,
			(this.player == 0 ? "White" : "Black")+" wins the game !\nDo you want to start a new game ?",
			"Victory !",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE
		) == 0) {
			this.nextTurn();
			this.newGame();
			this.nextTurn();
		}
	}
	
	public int getPlayer() {
		return this.player;
	}
	
	public int nextTurn() {
		this.player = (this.player+1)%2;
		
		return this.player;
	}
	
	// Indexes
	//			0
	//		7		1
	//	6				2
	//		5		3
	//			4
	// Value
	// -1 = no alignment
	// 0 = five aligned
	// 1 = two enemies aligned and surrounded
	public int[] checkAlignement(Cell source, int player) {
		int[] res = { -1, -1, -1, -1, -1, -1, -1, -1 };
		
		int v = 0, h = 1;
		
		for(int i = 0; i < 4; i++) { // Test N, NE, E, SE
			if(!(v == 1 && h == 1)) {
				int n = 5;
				int x = 0;
				int y = 0;
				int winCount = 1;
				
				// We go to the farthest accessible intersection from the source toward a direction between N and SE
				// clockwise.
				// For each direction, we go back to our origin cell (source) and continue further until we reach the
				// fourth cell behind the origin or a side of the board.
				while(n > -4) {
					n--;
					x = source.iX+(h-1)*n;
					y = source.iY+(v-1)*n;
					
					if(x > 18 || y > 18) {	// Intersection is not accessible
						continue;
					} else if(x < 0) {		// Intersection is out of the board
						break;
					} else {				// Intersection is valid
						Cell cell = this.getCellAt(x, y);
						if(cell.isPlayed() && cell.getState() == player) { // Pawn is ours
							winCount++;
						} else {
							winCount = 1;
						}
						
						if(winCount >= 5) {	// Yeah ... you win !
							res[i] = 0;
						}
					}
					
				}
			}

			if(i%3 == 0) { // Rotate N > NE > E > SE
				h = (h+1)%3;
			} else {
				v = (v+1)%3;
			}
		}
		
		
		
		// DEV__Check if two enemy pawns are surrounded by two of our ones
		
		return res;
	}
	
	public boolean checkMove(Cell cell) {
		boolean win = false;
		int[] r = this.checkAlignement(cell, this.player);
		
		for(int i = r.length-1; i >= 0; i--) {
			if(r[i] == 0) {
				win = true;
			}
		}
		
		if(win) {
			this.win();
		}
		
		return true;
	}
	
	public boolean isPlaying() {
		return this.playing;
	}
	
	public boolean confirmNewGame() {
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
			confirm = this.confirmNewGame();
		}
		
		if(confirm) {
			this.playing = true;
			this.initBoard();
			Cell first = this.getCellAt(9, 9);
			first.setColor(this.player);
			first.play();
			this.nextTurn();
		}
	}
	
	public void host(String rawPort) {
		int port = Server.defaultPort;
		try {
			port = Integer.parseInt(rawPort);
		} catch(NumberFormatException err) {
			System.err.println("This is no valid port.");
		}
		this.server = new Server(port, this);
		this.server.start();
		this.newGame();
	}
	
	public boolean makeMove(HashMap<String, String> cmd) {
		for(String c : cmd.keySet()) {
			this.opponentMove = cmd.get(c);
			System.out.println(c);
			this.parent.menuListener.actionPerformed(new ActionEvent(this, 1, c));
		}
		return true;
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
