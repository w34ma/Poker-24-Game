import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;


public class GamePlayMsg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3078331462882325631L;
	public ArrayList<JMSMessage> playerinfos;
	String [] cards = new String[4];
	int [] num = new int[4];

	public GamePlayMsg(ArrayList<JMSMessage> playerinfos) {
		this.playerinfos = playerinfos;
		char [] prefix = {'a','b','c','d'};
		//String [] test = {"a3.png","b11.png","c4.png","d6.png"};
		//int [] testnum = {3,11,4,6};
		for(int i = 0; i < 4;i++){
			Random rand = new Random();
			int  m = rand.nextInt(4);
			int  n = rand.nextInt(13) + 1;
			
			while(i == 1 && n == num[0]){
			  n = rand.nextInt(13) + 1;
			}
			
			while(i == 2 && (n == num[0] || n == num[1])){
				  n = rand.nextInt(13) + 1;
			}
			
			while(i == 3 && (n == num[0] || n == num[1] || n == num[2]) ){
				  n = rand.nextInt(13) + 1;
			}
			num[i] = n;
			cards[i] = prefix[m]+Integer.toString(n)+".png";
		}
		//cards = test;
		//num = testnum;
	}
}