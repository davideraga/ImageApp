package application;

import java.io.File;
import java.util.concurrent.Semaphore;

import Camera.AndroidCamera2;
import Camera.CameraDevice;
import Camera.StereoCamera;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Controller {
		private ObservableList<CameraDevice> cameras;
		//private long interval;
		boolean stop;
		private long interval;
		boolean isRunning;
		private CameraDevice device;
		File folder;
		private Semaphore startSem=new Semaphore(1);
		public void setInterval(long interval) {
			this.interval = interval;
		}
		ObservableList<CameraDevice> getCameras(){
			return cameras;
		}
		public Controller(){
			cameras=FXCollections.observableArrayList();
			AndroidCamera2 android=new AndroidCamera2("android");
			StereoCamera stereo=new StereoCamera("stereocamera");
			cameras.add(android);
			cameras.add(stereo);
			stop=false;
			String nomeCartella = "dir";
			isRunning=false;
	        folder = new File(nomeCartella);
	        // Verifichiamo che non sia già esistente come cartella
	        if (!folder.isDirectory()) {
	            // In caso non sia già presente, la creiamo
	            folder.mkdir();
	        }
		}
		public void stop(){
			if(!isRunning){
				alert("Errore", "Not running");
			}
			stop=true;
		}
		public boolean start(){
			try {
				startSem.acquire();//mi accerto che non ce ne siano 2
				if(isRunning){
					alert("Errore start", "Running");
					startSem.release();
					return true;
				}
				isRunning=true;
				startSem.release();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			stop=false;
			while(!stop) {
				long start =System.currentTimeMillis();
				if(device.takePhoto(folder))
					System.out.println("completato");
				else{
					alert("Errore nel acquisizione foto", "stopped");
					stop=false;
					isRunning=false;
					return false;
				}
				long left =interval-(System.currentTimeMillis()-start);
				if(left>0) {
					try {
						while(!stop&&left>1000&&left<interval){
							Thread.sleep(1000);
							left-=1000;
						}
						//System.out.println("i sleep");
						if(!stop&&left>0&&left<interval)
							Thread.sleep(left);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else if(left<-100){
					alertWarning("Warning", "framerate too high");
					interval+=-left+10;//aumento l'intervallo per non dare il warning più volte
				}
			}
			stop=false;
			isRunning=false;
			return true;
		}
		 public boolean connect(CameraDevice device){
			if(isRunning){
				alert("Running", "Stop prima");
				return false;
			}
			if(device==null){
				alert("Errore connessione", "Camera null");
				return false;

			}
			if(this.device!=null)
				this.device.close();//chiudo il vecchio device
			this.device=device;
			boolean res=device.connect();
			if(!res)
				alert("Errore connesione", "Connessione fallita");
			return res;
		}
		 public void alert(String header, String content){
			Platform.runLater(() -> {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Dialog");
				alert.setHeaderText(header);
				alert.setContentText(content);
				alert.showAndWait();
			});

		}
		public void alertWarning(String header, String content){
			Platform.runLater(() -> {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Error Dialog");
				alert.setHeaderText(header);
				alert.setContentText(content);
				alert.showAndWait();
			});

		}
		public boolean isRunning(){
			return isRunning;
		}
		public void setFolder(File folder){
			if(isRunning)
				alert("Running", "Stop prima");
			else
				this.folder=folder;
		}

}