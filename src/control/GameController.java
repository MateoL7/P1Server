package control;



import java.util.ArrayList;

import com.google.gson.Gson;

import comm.TCPConnection;
import comm.TCPConnection.OnConnectionListener;
import javafx.application.Platform;
import model.ConnectionPossible;
import model.GameStatus;
import model.Generic;
import model.ShotStatus;
import model.User;
import comm.Receptor.OnMessageListener;
import comm.Session;
import view.GameWindow;

public class GameController implements OnMessageListener, OnConnectionListener{

	private GameWindow view;
	private TCPConnection connection;
	
	public GameController(GameWindow view) {
		this.view = view;
		init();
	}

	public void init() {
		connection = TCPConnection.getInstance();
		connection.setPuerto(5000);
		connection.start();
		connection.setConnectionListener(this);
		connection.setMessageListener(this);	

	}


	@Override
	public void onConnection(String username) {
		Platform.runLater(

				()->{
					view.getMessagesArea().appendText("<<< "+ username +" se ha conectado >>>\n");
				}

				);
	}

	@Override
	public void OnMessage(Session s, String msg) {
		Gson gson = new Gson();
		Generic type = gson.fromJson(msg, Generic.class);
		switch (type.getType()) {
		case "Attack":
			connection.sendAttack(s, msg);
			break;
		case "User":
			User u = gson.fromJson(msg, User.class);			
			boolean gotSpace = connection.gotSpace();
			ConnectionPossible cp = new ConnectionPossible(gotSpace);
			String jsonConnection = gson.toJson(cp);
			connection.sendApproval(s, jsonConnection);
			if(!gotSpace) {

				connection.removeFromWaitList(s);

			}
			//Hay espacio
			else {
				connection.addUserToConnect(s, u);
			}
			break;
		case "ConnectionPossible":
			boolean missingPlayer = connection.gotSpace();
			if(!missingPlayer) {
				GameStatus gs = new GameStatus("Ready");
				String status = gson.toJson(gs);
				connection.startGame(status);
			}
			break;
		case "ShotStatus":
			ShotStatus ss = gson.fromJson(msg, ShotStatus.class);
			boolean gotHit = ss.isBullseye();
			if(gotHit) {
				connection.disconnected(s);
				ArrayList<Session> sessions = connection.getSessions();
				for(int i = 0; i < sessions.size(); i++) {
					if(sessions.get(i) != s)  {
						sessions.get(i).getEmisor().sendMessage(msg);
					}
				}
			}
			
			break;
		}
	}
}
