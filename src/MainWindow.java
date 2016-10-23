import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.JFrame;

public class MainWindow extends JFrame {
	private JMenuBar menuBar;
	private Board board;

	MainWindow() {
		super("Pente");

		setSize(new Dimension(633, 676));
		setResizable(false);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		menuBar = new JMenuBar();
		board = new Board();
		
		MenuListener menuListener = new MenuListener(board);
		
		JMenu fileMenu = new JMenu("File");
		JMenu helpMenu = new JMenu("?");

		JMenuItem quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		quitMenuItem.addActionListener(menuListener);
		
		JMenuItem newMenuItem = new JMenuItem("Start a new game");
		newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		newMenuItem.setActionCommand("New");
		newMenuItem.addActionListener(menuListener);
		
		JMenuItem aboutMenuItem = new JMenuItem("About this program");
		aboutMenuItem.setActionCommand("About");
		aboutMenuItem.addActionListener(menuListener);
		
		fileMenu.add(newMenuItem);
		fileMenu.add(quitMenuItem);
		
		helpMenu.add(aboutMenuItem);
		
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
		setContentPane(board);
		setVisible(true);
	}

}
