public class Pente {
	private MainWindow W;

	Pente() {
		System.setProperty("awt.useSystemAAFontSettings","on");
		System.setProperty("swing.aatext", "true");
		W = new MainWindow();
	}

	public static void main(String [] args) {
		Pente game = new Pente();
	}

}
