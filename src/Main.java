1package application;
	
import java.net.URL;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
	  int max = 100;
	  primaryStage.setTitle("Audio Player 1");
	  VBox root = new VBox(10);
	  Scene scene = new Scene(root);
	  //SpectrumBars my_bars = new SpectrumBars();
	  final URL resource = getClass().getResource("Train - Drops of Jupiter.mp3");
	  final Media media = new Media(resource.toString());
	  final MediaPlayer mediaPlayer = new MediaPlayer(media);
	  mediaPlayer.setAudioSpectrumThreshold((-1)*max);
	  SpectrumListener myListener = new SpectrumListener(max);
	  mediaPlayer.setAudioSpectrumListener(myListener);
	  GridPane mainGrid = new GridPane();
	  mainGrid.add(myListener.getDisplay(), 0, 0);
	  root.getChildren().add(mainGrid);
    
	  mediaPlayer.play();

	  primaryStage.setScene(scene);
	  primaryStage.sizeToScene();
	  primaryStage.show();
	  primaryStage.show();
  }
}