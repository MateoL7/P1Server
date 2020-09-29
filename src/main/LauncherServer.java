package main;

import javafx.application.Application;
import javafx.stage.Stage;
import view.GameWindow;

public class LauncherServer extends Application {

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage arg0) throws Exception {
		GameWindow chatWindow = new GameWindow();
		chatWindow.show();
	}

}
