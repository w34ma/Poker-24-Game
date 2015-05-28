import java.io.Serializable;
import java.util.ArrayList;


public class RoomManagement implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1916493279021327594L;
	
	ArrayList<String> playerlist;

	public int[] cardsVal;
	
	public RoomManagement(ArrayList<JMSMessage> playerinfos, int[] cards) {
		playerlist = new ArrayList<String>();
		this.cardsVal = cards.clone();
		for(JMSMessage playinfo : playerinfos){
			playerlist.add(playinfo.name);
		}
	}
}