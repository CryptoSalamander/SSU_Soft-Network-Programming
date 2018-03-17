package speedquiz;

import java.awt.*;
import java.awt.event.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import javax.swing.*;

import java.io.*;
import java.net.*;
import javax.net.ssl.HttpsURLConnection;


public class ClientImpl extends JFrame implements Client,ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextArea display, display2;
	JTextArea question;
	JTextField input,id;
	JLabel label,nick;
	Server server;
	JButton register,close,enter,clear;
	JButton help1,help2;
	String name,serverName;
	CardLayout card;
	JPanel npanel;
	String url = "https://www.google.com";
	
	ArrayList<quiz> QA = new ArrayList<quiz>();
	//int QA_index = 0;;
	
	class quiz{
		String question = "";
		String answer = "";
	}
	
	public ClientImpl()
	{
		super("RMI 채팅");
		
		JPanel questionPanel = new JPanel();
		questionPanel.setPreferredSize(new Dimension(this.getWidth(),150));
		question = new JTextArea();
		question.setPreferredSize(new Dimension(320,140));
		question.setEditable(false);
		questionPanel.add(question);
		question.setText("여기에 문제가 나와요");
		
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		display = new JTextArea();
		display.setEditable(false);
		JScrollPane spane = new JScrollPane(display);
		spane.setViewportView(display);
		spane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		c.add(spane, "Center");
		npanel = new JPanel();
		card = new CardLayout();
		npanel.setLayout(card);
		
		label = new JLabel("대화명 : ");
		id = new JTextField(10);
		register = new JButton("등록");
		register.addActionListener(this);
		close = new JButton("종료");
		close.addActionListener(this);
		JPanel loginPanel = new JPanel();
		help1 = new JButton("도움말");
		help1.addActionListener(this);
		
		loginPanel.add(label);
		loginPanel.add(id);
		loginPanel.add(register);
		loginPanel.add(help1);
		loginPanel.add(close);
		
		nick = new JLabel("즐팅!!");
		input = new JTextField(8);
		input.addActionListener(this);
		enter = new JButton("입력");
		enter.addActionListener(this);
		clear = new JButton("지우기");
		clear.addActionListener(this);
		help2 = new JButton("도움말");
		help2.addActionListener(this);
		JPanel chatPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		
		chatPanel.add(nick);
		chatPanel.add(input);
		chatPanel.add(enter);
		chatPanel.add(clear);
		chatPanel.add(help2);

		npanel.add(loginPanel,"login");
		npanel.add(chatPanel,"chat");
		
		c.add(npanel, "North");
		c.add(questionPanel, "South");
		
		this.setResizable(false);
		
		
		
		card.show(npanel, "login");
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				setVisible(false);
				if( server != null)
				{
					try{
						server.unregister(ClientImpl.this, name);
					}catch(Exception ee)
					{
						System.out.println("120Line");
						ee.printStackTrace();
					}
				}
				dispose();
				System.exit(0);
			}
		});
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(400,500);
		setVisible(true);
		initialize("Input.txt");
		for(int i = 0; i < QA.size(); i ++){
			System.out.println(QA.get(i).question);
			System.out.println(QA.get(i).answer);
		}
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
			System.out.println("155Line");
			e.printStackTrace();
		}
	}
	@SuppressWarnings("deprecation")
	private void connect(){
		try{
			UnicastRemoteObject.exportObject(this);
			server = (Server)Naming.lookup("rmi://localhost:5140/chat");
			server.register(this, name);
		}catch(Exception e)
		{
			System.out.println("167Line");
			e.printStackTrace();
		}
	}
	public synchronized void back(String msg)
	{
		try{
			display.append(msg+"\n");
			display.setCaretPosition(display.getDocument().getLength());
		}catch(Exception e)
		{
			System.out.println("177Line");
			display.append(e.toString());
		}
	}
	 public static void openWebpage(URI uri) {
	     Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	     if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	         try {
	             desktop.browse(uri);
	         } catch (Exception e) {
	        	 System.out.println("187Line");
	             e.printStackTrace();
	         }
	     }
	 }

	 public static void openWebpage(URL url) {
	     try {
	         openWebpage(url.toURI());
	     } catch (URISyntaxException e) {
	    	 System.out.println("197Line");
	         e.printStackTrace();
	     }
	 }

	 public void update() throws RemoteException{
		 if(server.getIndex() == QA.size()-1){
			 question.setText("문제가 다 떨어졌어요!");
		 }
		 else{
			 question.setText(QA.get(server.getIndex()).question);
			 this.setVisible(true);
		 }
	 }
	public void actionPerformed(ActionEvent e)
	{
		Object o = e.getSource();
		
		if(o == register)
		{
			name = id.getText().trim();
			card.next(npanel);
			connect();
			nick.setText(name+"님");
			if(QA.size() != 0){
				try {
					question.setText(QA.get(server.getIndex()).question);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				this.setVisible(true);
			}
			else{
				question.setText("문제가없어요");
				this.setVisible(true);
			}
		}else if(o == input || o == enter)
		{
			try{
				server.send(name + ":" + input.getText());
				input.setText("");
				update();
			}catch(Exception ex)
			{
				System.out.println("231Line");
				display.append(ex.toString());
			}
		}else if(o == clear)
		{
			display.setText("");
		}else if( o == close)
		{
			System.exit(0);
		}
		else if( o == help1 || o == help2){
			try{
				URL obj = new URL(url);
				openWebpage(obj);
			}catch(MalformedURLException Me){
				System.out.println("246Line");
				System.out.println("Erroe occures");
			}
		}
	}
	public static void main(String[] args)
	{
		new ClientImpl();
	}
}
