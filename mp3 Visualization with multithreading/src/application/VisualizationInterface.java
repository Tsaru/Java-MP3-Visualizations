package application;

import java.io.File;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// So far, this is an exact duplicate of the previous VisualizationInterface.java,
// but we will hopefully implement multithreading in this version.

public class VisualizationInterface extends Application {
	
	  final int maximumVolume = 100;
	  private Thread t;
	
	  private final Label songChooserLabel = new Label("Song Location:");
	  private final TextField songChooserTextField = new TextField(getClass().getResource("Train - Drops of Jupiter.mp3").toString().substring(6));
	  private final Button songOpenButton = new Button("Open");
	  private final Button songBrowseButton = new Button("Browse");
	  private final FileChooser songFileChooser = new FileChooser();
	  
	  private final Label visualizationChooserLabel = new Label("Visualization:");
	  private final ObservableList<String> visualizationList = 
			    FXCollections.observableArrayList(
				    "Animated Circle",
			        "Spectrum Bars",
			        "Visualization 3"
			    );
	  private final ComboBox<String> visualizationChooserComboBox = new ComboBox<String>(visualizationList);
	  
	  private Visualization visualization;
	  private SpectrumListener spectrumListener;
	  private MediaPlayer mediaPlayer;
	  private VBox root = new VBox(10);
	  
	  private void StartNewSong() {
	      try{
	    	  //System.out.println("file:/"+songChooserTextField.getText());
	    	  mediaPlayer.stop();
	    	  mediaPlayer = new MediaPlayer(new Media("file:/"+songChooserTextField.getText().toString()));
	    	  UpdateVisualization();
        	  mediaPlayer.play();
	      } catch (Exception exc) {
	    	  exc.printStackTrace();
	    	  JOptionPane.showMessageDialog(null,
	        			"Error loading song!", "Mp3 Visualizer",
	        			JOptionPane.WARNING_MESSAGE);
	      }
	  }
	  
	  private void UpdateVisualization() {
		  if(visualizationChooserComboBox.getValue() != null) {
			  if(visualization != null)
	        	  root.getChildren().remove(1);
			  if(visualizationChooserComboBox.getValue() == "Spectrum Bars") {
				  visualization = new SpectrumBars(maximumVolume, root);
			  }
			  if(visualizationChooserComboBox.getValue() == "Animated Circle") {
				  visualization = new AnimatedCircle(maximumVolume, root);
			  }
			  //t = new Thread(visualization, "visualization");
			  //t.start();
			  spectrumListener = new SpectrumListener(visualization);
			  mediaPlayer.setAudioSpectrumThreshold((-1)*maximumVolume);
	    	  mediaPlayer.setAudioSpectrumListener(spectrumListener);
	    	  //root.getChildren().add(visualization.getNode());
		  }
	  }
	  
	  public static void main(String[] args) {
		  launch(args);
	  }

	  @Override
	  public void start(Stage primaryStage) {
		  songFileChooser.setTitle("Open Mp3 File");
		  primaryStage.setTitle("Audio Player 1");
		  Scene scene = new Scene(root);
		  GridPane interfaceElements = new GridPane();
		  interfaceElements.setAlignment(Pos.TOP_LEFT);
		  interfaceElements.setVgap(5);
		  interfaceElements.setHgap(5);
		  
		  interfaceElements.add(songChooserLabel, 0, 0);
		  interfaceElements.add(songChooserTextField, 1, 0, 2, 1);
		  interfaceElements.add(songOpenButton, 3, 0);
		  interfaceElements.add(songBrowseButton, 4, 0);
		  
		  interfaceElements.add(visualizationChooserLabel, 0, 1);
		  interfaceElements.add(visualizationChooserComboBox, 1, 1, 2, 1);
		  

		  songOpenButton.setOnAction(new EventHandler<ActionEvent>() {
				 
			  @Override
			  public void handle(ActionEvent e) {
				  StartNewSong();
			  	}
		  });
			
		  songBrowseButton.setOnAction(new EventHandler<ActionEvent>() {
				
			  @Override
			  public void handle(ActionEvent e) {
				  File file = songFileChooser.showOpenDialog(songBrowseButton.getScene().getWindow());
				  if(file != null) {
					  try {
						  songChooserTextField.setText(file.toURI().toURL().toString().substring(6));
					  }	catch (Exception exc) {
						  exc.printStackTrace();
						  JOptionPane.showMessageDialog(null,
			        				"Error initializing your image!", "Image loader",
			        				JOptionPane.WARNING_MESSAGE);
					  }
					  StartNewSong();
				  }
			  }
		  });
		  
		  visualizationChooserComboBox.valueProperty().addListener(new ChangeListener<String>() {
	            public void changed(ObservableValue<? extends String> ov,
	                String old_val, String new_val) {
	                	UpdateVisualization();
	            }
	        });

		  root.getChildren().add(interfaceElements);
    	  mediaPlayer = new MediaPlayer(new Media("file:/"+songChooserTextField.getText().toString()));
		  visualizationChooserComboBox.getSelectionModel().selectFirst();
    	  mediaPlayer.play();
		  primaryStage.setScene(scene);
		  primaryStage.sizeToScene();
		  primaryStage.show();
		  primaryStage.show();
	  }
}
