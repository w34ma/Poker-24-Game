import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Remoteinterface extends Remote {
	public int registerservice(String name, String password, String password2)
			throws RemoteException;

	public int loginservice(String name, String password)
			throws RemoteException;

	public int logoutservice(String name) throws RemoteException;

	// making register,login,logout a service on server

	public String[] collectdata(String name) throws RemoteException;

	public String[][] rankBoard() throws RemoteException;
}
