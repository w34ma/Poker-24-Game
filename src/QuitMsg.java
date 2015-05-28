import java.io.Serializable;
import java.util.ArrayList;

public class QuitMsg implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6042721754362231127L;
	public String name;
	public ArrayList<String> players;

	public QuitMsg(String name,  ArrayList<String> players) {
		this.name = name;
		this.players = players;
	}
}