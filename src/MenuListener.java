import java.awt.event.ActionListener;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import java.awt.event.ItemEvent;

public class MenuListener implements ActionListener, ItemListener {
	Board	board;
	
	MenuListener(Board board) {
		this.board = board;
	}
	
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
			case "Quit":
				Window frame = JFrame.getWindows()[0];
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				break;
			case "About":
				System.out.println("About");
				break;
			case "New":
				this.board.newGame();
				break;
		}
	}
	
	public void itemStateChanged(ItemEvent e) {
		
	}
}
