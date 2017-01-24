public class Pente {
	@SuppressWarnings("unused")
	private MainWindow W;

	Pente() {
		W = new MainWindow();
	}

	public static void main(String [] args) {
		System.setProperty("awt.useSystemAAFontSettings", "on");
		
		@SuppressWarnings("unused")
		Pente game = new Pente();
	}

}
