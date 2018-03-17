package speedquiz;
import java.rmi.server.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.*;
import java.util.*;

import speedquiz.ClientImpl.quiz;
public class ServerImpl extends UnicastRemoteObject implements Server{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Vector<Client> clientList;
	ArrayList<quiz> QA = new ArrayList<quiz>();
	private int index = 0;
	
	class quiz{
		String question = "";
		String answer = "";
	}
	public void initialize(String filename){
		try{
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String s = "";
			boolean Q = true;
			quiz temp = new quiz();
			while((s = in.readLine()) != null){
				if(Q){
					temp.question = s;
					Q = false;
				}else{
					temp.answer = s;
					Q = true;
					QA.add(temp);
					temp = new quiz();
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public ServerImpl() throws RemoteException
	{
		super();
		clientList = new Vector<Client>();
		initialize("Input.txt");
	}
	public synchronized void register(Client client,String name) throws RemoteException
	{
		clientList.add(client);
		System.out.println("register called");
		send(name + ": ¥‘¿Ã ¿‘¿Â«œºÃΩ¿¥œ¥Ÿ.");
	}
	public void send(String msg) throws RemoteException {
		synchronized(clientList){
			Enumeration<Client> e = clientList.elements();
			String ans = msg;
			String temp[] = ans.split(":");
			if(temp.length != 1 ){
				if(temp[1].equals(QA.get(index).answer.trim())){
					index++;
				}
			}
			while(e.hasMoreElements())
			{
				Client c = (Client)e.nextElement();
				c.back(msg);
				if(temp.length != 1 && index != 0){
					if(temp[1].equals(QA.get(index-1).answer.trim())){
						c.back(temp[0] + " is correct");
					}
				}
				c.update();
			}
		}
	}
	public synchronized void unregister(Client client,String name) throws RemoteException {
		clientList.removeElement(client);
		send(name + "¥‘¿Ã ≈¿Â«œºÃΩ¿¥œ¥Ÿ.");
	}
	public static void main(String[] args)
	{
		try{
			ServerImpl s = new ServerImpl();
			System.setProperty("java.rmi.server.hostname","127.0.0.1");
			Naming.rebind("rmi://localhost:5140/chat", s);
			System.out.println("RMI Chat Server is Ready.");
		}catch (Exception e)
		{
			System.out.println("Server 45 line");
			e.printStackTrace();
		}
	}
	public int getIndex(){
		return index;
	}
}
