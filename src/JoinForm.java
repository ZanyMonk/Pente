import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class JoinForm extends JFrame {
	private SpringLayout layout;
	private JLabel hostLabel, portLabel;
	private JButton submitButton;
	private boolean layoutInitialized = false;
	
	public JTextField hostField, portField;
	public Board board;
	
	JoinForm(Board board) {
		super("Join a game");
		this.board = board;
		
		this.layout = new SpringLayout();
		setLayout(this.layout);
		setSize(185, 105);
		
		this.hostLabel = new JLabel("Host: ");
		this.portLabel = new JLabel("Port: ");
		
		this.hostField = new JTextField(Server.defaultHost, 10);
		this.portField = new JTextField(((Integer)Server.defaultPort).toString(), 10);
		
		this.submitButton = new JButton("Join");
		this.submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JoinForm form = (JoinForm)((JButton)e.getSource()).getParent().getParent().getParent().getParent();
				boolean success = form.board.join(form.hostField.getText(), Integer.parseInt(form.portField.getText()));
				if(success) {
					form.setVisible(false);
				}
			}
		});
	}
	
	private void initLayout() {
		Container pane = getContentPane();
		if(!this.layoutInitialized) {
			pane.add(this.hostLabel);
			pane.add(this.hostField);
			pane.add(this.portLabel);
			pane.add(this.portField);
			pane.add(this.submitButton);

			getRootPane().setDefaultButton(this.submitButton);
			
			this.layoutInitialized = true;
			this.layout.putConstraint(
				SpringLayout.WEST,
				this.hostLabel,
				5,
				SpringLayout.WEST,
				pane
			);
			this.layout.putConstraint(
				SpringLayout.NORTH,
				this.hostLabel,
				5,
				SpringLayout.NORTH,
				pane
			);
			this.layout.putConstraint(
				SpringLayout.WEST,
				this.hostField,
				50,
				SpringLayout.WEST,
				pane
			);
			this.layout.putConstraint(
				SpringLayout.NORTH,
				this.hostField,
				5,
				SpringLayout.NORTH,
				pane
			);
			
			
			this.layout.putConstraint(
				SpringLayout.WEST,
				this.portLabel,
				5,
				SpringLayout.WEST,
				pane
			);
			this.layout.putConstraint(
				SpringLayout.NORTH,
				this.portLabel,
				25,
				SpringLayout.NORTH,
				pane
			);
			this.layout.putConstraint(
				SpringLayout.WEST,
				this.portField,
				50,
				SpringLayout.WEST,
				pane
			);
			this.layout.putConstraint(
				SpringLayout.NORTH,
				this.portField,
				25,
				SpringLayout.NORTH,
				pane
			);
			
			this.layout.putConstraint(
				SpringLayout.WEST,
				this.submitButton,
				60,
				SpringLayout.WEST,
				pane
			);
			this.layout.putConstraint(
				SpringLayout.NORTH,
				this.submitButton,
				45,
				SpringLayout.NORTH,
				pane
			);
		}
	}
	
	public void showWindow() {
		this.initLayout();
		setVisible(true);
	}
	
}
