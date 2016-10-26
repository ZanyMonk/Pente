import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	private JMenuBar menuBar;
	private Board board;
	
	public MenuListener menuListener;

	MainWindow() {
		super("Pente");

		setSize(new Dimension(633, 676));
		setResizable(false);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		menuBar = new JMenuBar();
		board = new Board(this);
		
		this.menuListener = new MenuListener(board);
		
		JMenu fileMenu = new JMenu("File");
		JMenu helpMenu = new JMenu("?");

		JMenuItem quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.setAccelerator(
			KeyStroke.getKeyStroke(
				KeyEvent.VK_Q,
				ActionEvent.CTRL_MASK
			)
		);
		quitMenuItem.addActionListener(this.menuListener);

		JMenuItem newMenuItem = new JMenuItem("Start a new game");
		newMenuItem.setAccelerator(
			KeyStroke.getKeyStroke(
				KeyEvent.VK_N,
				ActionEvent.CTRL_MASK
			)
		);
		newMenuItem.setActionCommand("New");
		newMenuItem.addActionListener(this.menuListener);
		
		JMenuItem joinMenuItem = new JMenuItem("Join a game");
		joinMenuItem.setAccelerator(
			KeyStroke.getKeyStroke(
				KeyEvent.VK_J,
				ActionEvent.CTRL_MASK
			)
		);
		joinMenuItem.setActionCommand("Join");
		joinMenuItem.addActionListener(this.menuListener);
		
		JMenuItem hostMenuItem = new JMenuItem("Host a game");
		hostMenuItem.setAccelerator(
			KeyStroke.getKeyStroke(
				KeyEvent.VK_H,
				ActionEvent.CTRL_MASK
			)
		);
		hostMenuItem.setActionCommand("Host");
		hostMenuItem.addActionListener(this.menuListener);
		
		JMenuItem aboutMenuItem = new JMenuItem("About this program");
		aboutMenuItem.setActionCommand("About");
		aboutMenuItem.addActionListener(this.menuListener);
		
		fileMenu.add(newMenuItem);
		fileMenu.add(joinMenuItem);
		fileMenu.add(hostMenuItem);
		fileMenu.add(quitMenuItem);
		
		helpMenu.add(aboutMenuItem);
		
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
		setContentPane(board);
		setVisible(true);
	}

}
