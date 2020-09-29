package view;



import control.GameController;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameWindow extends Stage{

	//UI Elements

	private Scene scene;
	private TextArea messagesArea;
	
	private GameController control;

	public GameWindow() {
		messagesArea = new TextArea();

		VBox vBox = new VBox();
		vBox.getChildren().add(messagesArea);
		scene = new Scene(vBox, 400,400);
		this.setScene(scene);
		control = new GameController(this);
	}

	public TextArea getMessagesArea() {
		return messagesArea;
	}



}
