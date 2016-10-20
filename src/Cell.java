import java.awt.geom.Point2D;
import java.awt.Color;
import java.awt.RadialGradientPaint;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JButton;

public class Cell extends JButton {
	private int x, y;
	private int size = 29;
	private int state = 0; // 0: None; 1: White; 2: Black
	private boolean hover = false;
	private boolean played = false;
	
	Cell() {
		super("");
		setMinimumSize(new Dimension(this.size, this.size));
		setBorderPainted(false);
		
		addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Cell c = (Cell)e.getSource();
				c.toggleHover();					
				c.getParent().repaint();
			}
		});
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Cell c = (Cell)e.getComponent();
				c.setState(1);
				c.play();
			}
		});
	}
	
	Cell(int x, int y) {
		this();
		this.x = x;
		this.y = y;
		setBounds(this.x-size/2, this.y-size/2, this.size, this.size);
	}
	
	public int getState() {
		return this.state;
	}
	
	public void setState(int newState) {
		this.state = (newState >= 0 && newState <= 2 ? newState : 0);
	}
	
	public boolean isHover() {
		return this.hover;
	}
	
	public void toggleHover() {
		this.hover = !this.hover;
	}
	
	public void play() {
		this.played = true;
	}

	@Override
	public void paintComponent(Graphics G) {
		Graphics2D gr = (Graphics2D)G;
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if(this.state != 0 || this.hover) {
			int c = (this.state == 1 ? 200 : 50);
			int r = c;
			int g = c;
			int b = c;
			int a = (this.hover && !this.played ? 100 : 255);
			
			float[] dists = {
			 	0.f,
			 	1.f
			};
			Color[] colors = {
			 	new Color(r+50, g+50, b+50, a),
			 	new Color(r, g, b, a)
			};
			
			gr.setPaint(new RadialGradientPaint(
				new Point2D.Float(size/2, size/2),
				size/2,
				new Point2D.Float(size/3, size/3),
				dists,
				colors,
				CycleMethod.NO_CYCLE
			));
			gr.fillArc(0, 0, this.size, this.size, 0, 360);
		}
	}

}
