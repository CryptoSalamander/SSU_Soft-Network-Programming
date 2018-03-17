package speedquiz;
import java.rmi.*;
public interface Server extends Remote{
	public void send(String msg) throws RemoteException;
	public void register(Client client, String name) throws RemoteException;
	public void unregister(Client client, String name) throws RemoteException;
	public int getIndex() throws RemoteException;
}
