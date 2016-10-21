import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;

import javax.swing.JPanel;

public class Board extends JPanel {
	private int 	margin = 25;
	private int 	cellSize = 30;
	private int		headerSize = 25;
	
	public int		player = 0; // White begins
	
	Board() {
		super();

		setLayout(null);

		initBoard();
	}

	private void initBoard() {
		Cell b;
		for (int y = 19; y > 0; y--) {
			for (int x = 19; x > 0; x--) {
				b = new Cell(x*this.cellSize+20, y*this.cellSize+20);
				add(b);
			}
		}
	}
	
	public int getPlayer() {
		return this.player;
	}
	
	public void nextTurn() {
		this.player = (this.player+1)%2;
	}

	@Override
	public void paintComponent(Graphics G) {
		super.paintComponent(G);
		Graphics2D g = (Graphics2D)G;
		g.setColor(Color.black);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.white);
		
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
