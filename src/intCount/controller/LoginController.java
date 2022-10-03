package intCount.controller;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class LoginController implements Initializable {
	
	@FXML
	private MediaView mediaView ; 
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		Media media = new Media("file:////Users/mac/Desktop/FxB/intCount 2-2/Grand-Teton-Road.mp4");
		
		MediaPlayer player = new MediaPlayer(media);
		mediaView.setMediaPlayer(player);
		player.setVolume(0);
		player.play();
		
		
	}
	
	
	
	
	
}
