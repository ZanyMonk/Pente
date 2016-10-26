
/**
 *
 * @author zanymonk
 */
public class Opponent {
	private String  name = "";
	private boolean connected = false;

	Opponent() {
	}

	public void login(String name) {
		this.name = name;
		this.connected = true;
	}

	public void logout() {
		this.connected = false;
	}

	public boolean isConnected() {
		return this.connected;
	}

}
