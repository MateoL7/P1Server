package comm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import comm.Receptor.OnMessageListener;
import model.User;


//CLASE OBSERVADA

public class TCPConnection extends Thread {

	//SINGLETON
	private static TCPConnection instance;

	private TCPConnection() {
		sessions = new ArrayList<>();
		salaDeEspera = new ArrayList<>();
	}

	public static synchronized TCPConnection getInstance() {
		if(instance == null) {
			instance = new TCPConnection();
		}
		return instance;
	}

	//GLOBAL
	private ServerSocket server;
	private int puerto;
	private OnConnectionListener connectionListener;
	private OnMessageListener messageListener;
	private ArrayList<Session> sessions;
	private ArrayList<Session> salaDeEspera;

	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}

	@Override
	public void run() {
		try {
			server = new ServerSocket(puerto);
			while(true) {
				System.out.println("Esperando en el puerto: " + puerto);
				Socket socket = server.accept();
				System.out.println("Nuevo cliente Conectado");
				Session session = new Session(socket);
				salaDeEspera.add(session);
				setAllMessageListener(messageListener);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setAllMessageListener(OnMessageListener listener) {
		for(int i = 0; i < salaDeEspera.size(); i++) {
			Session s = salaDeEspera.get(i);
			s.getReceptor().setListener(listener);
		}

	}


	public void setConnectionListener(OnConnectionListener connectionListener) {
		this.connectionListener = connectionListener;
	}

	public interface OnConnectionListener{
		public void onConnection();
	}

	public void setMessageListener(OnMessageListener messageListener) {
		this.messageListener = messageListener;
	}

	
	public void sendUpdate(String msg) {
		for(int i = 0; i < sessions.size(); i++) {
			Session s = sessions.get(i);
			s.getEmisor().sendMessage(msg);
		}
	}

	public void sendAttack(Session s, String msg) {
		for(int i = 0; i < sessions.size(); i++) {
			if(sessions.get(i) != s) {
				sessions.get(i).getEmisor().sendMessage(msg);
				break;
			}
		}
	}

	public void addUserToConnect(Session s, User u) {
		int index = salaDeEspera.indexOf(s);
		salaDeEspera.remove(index);
		sessions.add(s);
		connectionListener.onConnection();
	}
	
	public boolean gotSpace() {
		boolean space = false;
		if(sessions.size()<2) space = true;
		return space;
	}
	
	public void removeFromWaitList(Session s) {
		int index = salaDeEspera.indexOf(s);
		salaDeEspera.remove(index);
	}

	public void sendApproval(Session s, String jsonConnection) {
		for(int i = 0; i < salaDeEspera.size(); i++) {
			if(salaDeEspera.get(i) == s) {
				salaDeEspera.get(i).getEmisor().sendMessage(jsonConnection);
				break;
			}
		}
	}

	public ArrayList<Session> getSessions() {
		return sessions;
	}

	public void disconnected(Session s) {
		sessions.remove(s);
	}

	public void startGame(String msg) {
		for(int i = 0; i < sessions.size(); i++) {
			Session s = sessions.get(i);
			s.getEmisor().sendMessage(msg);
		}
	}	

}
