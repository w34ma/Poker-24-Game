import java.io.Serializable;


public class JMSMessage implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	public String win;
	public String avg;
	public String total;


	public JMSMessage(String name) {
		this.name = name;
		win = "0";
		avg = "0.0";
	}
	public JMSMessage(String name, String win, String avg,String total) {
		this.name = name;
		this.win = win;
		this.avg = avg;
		this.total = total;
	}
}