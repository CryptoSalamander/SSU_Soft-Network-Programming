package speedquiz;
import java.rmi.*;
public interface Client extends Remote {
	public void back(String msg) throws RemoteException;
	public String getName() throws RemoteException;
	public void update() throws RemoteException;
}
