import java.awt.event.ActionListener;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.event.ItemEvent;

public class MenuListener implements ActionListener, ItemListener {
	Board	board;
	
	MenuListener(Board board) {
		this.board = board;
	}
	
	public void actionPerformed(ActionEvent e) {
		Window frame = JFrame.getWindows()[0];
		switch(e.getActionCommand()) {
			case "Quit":
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				break;
			case "About":
				JOptionPane.showMessageDialog(
					frame,
					"We, Lucien A. & Auguste T., coded this program as a project for our Java course.",
					"About Pente",
					JOptionPane.INFORMATION_MESSAGE
				);
				break;
			case "New":
				this.board.newGame();
				break;
			case "Host":
				this.board.host((String)JOptionPane.showInputDialog(
					frame,
					"Port",
					"Host settings",
					JOptionPane.INFORMATION_MESSAGE,
					null,
					null,
					"1337"
				));
				break;
			case "HELLO":
				System.out.println("Opponent connected");
				break;
			case "MOVE":
				System.out.println("Opponent puts a pawn at "+this.board.opponentMove);
				break;
			default:
				break;
		}
	}
	
	public void itemStateChanged(ItemEvent e) {
		
	}
}
