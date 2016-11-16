import java.awt.event.ActionListener;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MenuListener implements ActionListener {
	Board	board;
	
	MenuListener(Board board) {
		this.board = board;
	}
	
	public void actionPerformed(ActionEvent e) {
		Window frame = JFrame.getWindows()[0];
		switch(e.getActionCommand()) {
			case "Quit":
				if(this.board.confirmNoSave()) {
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				}
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
			case "Join":
				if(!this.board.isPlayingOnline() || this.board.confirmNoSave()) {
					this.board.shutdownServer();
					JoinForm form = new JoinForm(this.board);
					form.showWindow();
				}
				break;
			case "Host":
				String port = null;
				boolean err = false;
				
				while(port == null || err) {
					port = (String)JOptionPane.showInputDialog(
						frame,
						"Port",
						"Host settings",
						JOptionPane.INFORMATION_MESSAGE,
						null,
						null,
						"1337"
					);
					
					System.out.println(port);
					
					err = !this.board.host(port);
					if(port == null) {
						break;
					}
				}
				break;
			default:
				break;
		}
	}
}
