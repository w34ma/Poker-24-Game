import java.io.Serializable;
import java.util.ArrayList;

public class GameoverMsg implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5058977788215413122L;
	String name;
	String func;
	ArrayList<String> playerlist;

	public GameoverMsg(String name, String func, ArrayList<String> playerlist) {
		this.name = name;
		this.func = func;
		this.playerlist = playerlist;
	}
}