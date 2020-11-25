package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.paint.Color;


public class Main extends Application {
	private Controller controller;

	public void start(Stage stage) {
		controller = new Controller();
		stage.setTitle("ImageApp");
		MainPane root = new MainPane(controller);

		Scene scene = new Scene(root, 500, 300, Color.ALICEBLUE);
		stage.setScene(scene);
		stage.show();
	}
	public static void main(String[] args) {
		launch(args);
	}
}
