import java.io.File;
import java.net.URL;

import javax.swing.JOptionPane;

import javafx.application.Application;
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


public class VisualizationInterface extends Application {
	
	  final int max = 100;
	
	  private final Label songChooserLabel = new Label("Song Location:");
	  private final TextField songChooserTextField = new TextField("Train - Drops of Jupiter.mp3");
	  private final Button songOpenButton = new Button("Open");
	  private final Button songBrowseButton = new Button("Browse");
	  private final FileChooser songFileChooser = new FileChooser();
	  
	  private final Label visualizationChooserLabel = new Label("Visualization:");
	  private final ObservableList<String> visualizationList = 
			    FXCollections.observableArrayList(
			        "Spectrum Bars",
			        "Visualization 2",
			        "Visualization 3"
			    );
	  private final ComboBox visualizationChooserComboBox = new ComboBox(visualizationList);
	  
	  private Visualization visualization;
	  private SpectrumListener spectrumListener;
	  private MediaPlayer mediaPlayer;
	  private VBox root = new VBox(10);
	  
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
				  URL songLocation = getClass().getResource(songChooserTextField.getText());
				  
			      try{
			    	  //Most of this code should be put into it's own function.
			    	  Media song = new Media(songLocation.toString());
					  System.out.println(songLocation.toString());
			    	  mediaPlayer.stop();
			    	  mediaPlayer = new MediaPlayer(song);
			    	  visualization = new SpectrumBars(max);
			    	  spectrumListener = new SpectrumListener(visualization);
					  mediaPlayer.setAudioSpectrumThreshold((-1)*max);
			    	  mediaPlayer.setAudioSpectrumListener(spectrumListener);
		        	  mediaPlayer.play();
			    	  root.getChildren().remove(1);
			    	  root.getChildren().add(visualization);
			      } catch (Exception exc) {
			    	  exc.printStackTrace();
			    	  JOptionPane.showMessageDialog(null,
			        			"Error loading song!", "Mp3 Visualizer",
			        			JOptionPane.WARNING_MESSAGE);
			      }
			  	}
			});
			
		  	
		  	//This is not working. Most of the code is functional in songOpenButton's event handler.
			songBrowseButton.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent e) {
					File file = songFileChooser.showOpenDialog(songBrowseButton.getScene().getWindow());
					if(file != null) {
						try {
							System.out.println(file.toURI().toURL().toString());
							Media song = new Media(file.toURI().toURL().toString());
							if(song.getError() == null) {
								try{
					        		mediaPlayer.pause();
					        		mediaPlayer = new MediaPlayer(song);
					        		mediaPlayer.play();
					        		songChooserTextField.setText(file.getPath());
					        	} catch (Exception exc) {
					        		mediaPlayer.play();
					        		exc.printStackTrace();
					        		JOptionPane.showMessageDialog(null,
					        				"Error initializing your image!", "Image loader",
					        				JOptionPane.WARNING_MESSAGE);
					        	}
					    	  
							} else {
					    	  JOptionPane.showMessageDialog(null,
					    			  "Error loading song!", "mp3 Visualization",
					    			  JOptionPane.WARNING_MESSAGE);
					        }
						} catch (Exception exc) {
			        		exc.printStackTrace();
			        		JOptionPane.showMessageDialog(null,
			        				"Error initializing your image!", "Image loader",
			        				JOptionPane.WARNING_MESSAGE);
						}
					}
				}
			});
			
			//TODO: add a handler for a change of the selected visualization.
		  
		  visualization = new SpectrumBars(max);
		  
		  final URL resource = getClass().getResource("Train - Drops of Jupiter.mp3");
		  //final URL resource = getClass().getResource("file:C:/Angel - Wonderful.mp3");
		  System.out.println(getClass().getResource("Train - Drops of Jupiter.mp3"));
		  System.out.println(resource.toString());
		  final Media media = new Media(resource.toString());
		  mediaPlayer = new MediaPlayer(media);
		  
		  spectrumListener = new SpectrumListener(visualization);
		  mediaPlayer.setAudioSpectrumThreshold((-1)*max);
		  mediaPlayer.setAudioSpectrumListener(spectrumListener);
		  
		  root.getChildren().add(interfaceElements);
		  root.getChildren().add(visualization);
	    
		  mediaPlayer.play();

		  primaryStage.setScene(scene);
		  primaryStage.sizeToScene();
		  primaryStage.show();
		  primaryStage.show();
	  }

}
