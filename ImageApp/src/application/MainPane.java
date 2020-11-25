package application;

import javafx.scene.layout.BorderPane;

import java.io.File;

import Camera.CameraDevice;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

public class MainPane extends BorderPane {
	private Button buttonConnect;
	private VBox vbox;
	private ComboBox<CameraDevice> comboCameras;
	private Controller controller;
	private Label connected;
	private boolean con;
	public MainPane(Controller controller) {
			this.controller = controller;
			//
			vbox = new VBox();
			HBox cameraBox = new HBox();
			comboCameras = new ComboBox<>(this.controller.getCameras());
			buttonConnect = new Button("Connect");
			buttonConnect.setOnAction(this::connect);
			connected=new Label("Not connected");
			cameraBox.getChildren().addAll(comboCameras, buttonConnect, connected);

			HBox rateBox = new HBox();
			Label rateLabel=new Label("Framerate");
			Slider slider = new Slider(0, 15, 0.2);
			slider.setMajorTickUnit(1);
			slider.setMinorTickCount(4);
			slider.setShowTickMarks(true);
			slider.setShowTickLabels(true);
			slider.setPrefWidth(400);
			slider.valueProperty().addListener(new ChangeListener<Number>() {
		         public void changed(ObservableValue <?extends Number>observable, Number oldValue, Number newValue){
		        	 if(slider.getValue()>0){
		        		 double interval=1000/slider.getValue();//ms
		        		 controller.setInterval((long)interval);
		        	 }
		 			 //Controller.alert("New interval", ""+interval);
		         }
		      });
			controller.setInterval(5000);
			rateBox.getChildren().addAll(rateLabel, slider);
			HBox dirBox= new HBox();
			Button dirButton=new Button("Choose directory");
			dirButton.setOnAction(this::chooseDir);
			dirBox.getChildren().add(dirButton);
			HBox ssBox = new HBox();
			Button buttonStart = new Button("Start");
			Button buttonStop = new Button("Stop");
			buttonStop.setOnAction(this::stop);
			buttonStart.setOnAction(this::start);
			ssBox.getChildren().addAll(buttonStart, buttonStop);
			ssBox.setSpacing(30);
			rateBox.setSpacing(10);
			cameraBox.setSpacing(20);
			cameraBox.setAlignment(Pos.CENTER);
			rateBox.setAlignment(Pos.CENTER);
			ssBox.setAlignment(Pos.CENTER);
			dirBox.setAlignment(Pos.CENTER);
			Separator sep1= new Separator();
			sep1.setOrientation(Orientation.HORIZONTAL);
			Separator sep2= new Separator();
			sep2.setOrientation(Orientation.HORIZONTAL);
			Separator sep3= new Separator();
			sep3.setOrientation(Orientation.HORIZONTAL);
			vbox.getChildren().addAll(cameraBox, sep1, rateBox, sep2, dirBox, sep3, ssBox);
			vbox.setSpacing(20);

			vbox.setAlignment(Pos.TOP_CENTER);
			vbox.setPadding(new Insets(10, 10, 10, 10));
			this.setCenter(vbox);

		}

		private void start(ActionEvent event) {
			if(!connected.getText().equals("Connected")){
				controller.alert("Errore",  "Nessun dispositivo connesso");
				return;
			}
			Thread startT=new Thread(){
				    public void run() {
				            if(!controller.start())//se c'è un errore si disconnette
				            	Platform.runLater(() ->{
										connected.setText("Not Connected");
								});
				    }
				};
			startT.start();
		}
		private void stop(ActionEvent event) {
			controller.stop();
		}
		private void connect(ActionEvent event) {
			if(connected.getText().equals("Connecting..."))
				return;
			connected.setText("Connecting...");
			Thread conT=new Thread(){
			    public void run() {
			            con=controller.connect(comboCameras.getValue());
			            Platform.runLater(() -> {
			            	if(con){
								connected.setText("Connected");
							}else
								connected.setText("Not Connected");
						});
			    }
			};
			conT.start();
		}
		private void chooseDir(ActionEvent event){
			DirectoryChooser directoryChooser = new DirectoryChooser();
	        File selectedDirectory = directoryChooser.showDialog(null);
	        controller.setFolder(selectedDirectory);
		}
}
