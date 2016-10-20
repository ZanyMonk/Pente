import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

public class MainWindow extends JFrame {
	private Board board;

	MainWindow() {
		super("Pente");

		setSize(new Dimension(633, 656));
		setResizable(false);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		board = new Board();

		setContentPane(board);
		setVisible(true);
	}

}
