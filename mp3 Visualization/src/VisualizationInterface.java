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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class VisualizationInterface extends Application {
	
	  final int maximumVolume = 100;
	  final double AUDIO_SPECTRUM_INTERVAL = 0.08;
	
	  private Stage stage;
	  //private final Label songChooserLabel = new Label("Song Location:");
	  private final TextField songChooserTextField = new TextField(getClass().getResource("Train - Drops of Jupiter.mp3").toString().substring(6));
	  //private final Button songOpenButton = new Button("Open");
	  //private final Button songBrowseButton = new Button("Browse");
	  private final FileChooser songFileChooser = new FileChooser();
	  private final BorderPane interfacePane = new BorderPane();
	  private final Button directoryBrowseButton = new Button("Open Playlist");
	  private final DirectoryChooser directoryBrowse = new DirectoryChooser();
	  
	  private final Label visualizationChooserLabel = new Label("Visualization Mode");
	  private final ObservableList<String> visualizationList = 
			    FXCollections.observableArrayList(
			        "Spectrum Bars",
			        "Spectrum Bars Wide",
			        "Spectrum Line",
			        "Spectrum Circles"
			    );
	  private final ComboBox<String> visualizationChooserComboBox = new ComboBox<String>(visualizationList);
	  
	  private Visualization visualization;
	  private SpectrumListener spectrumListener;
	  private MediaPlayer mediaPlayer;
	  private VBox root = new VBox(10);
	  
	  private ObservableList<String> listOfSongs = FXCollections.observableArrayList(); // contains the list of songs the user can play
	  private final ListView<String> songListView = new ListView<String>(listOfSongs);
	  
	  private void StartNewSong(String song_pathname) {
	      try {
	    	  //System.out.println("file:/"+songChooserTextField.getText());
	    	  mediaPlayer.stop();
	    	  mediaPlayer = new MediaPlayer(new Media("file:/"+song_pathname));//+songChooserTextField.getText().toString()));
	    	  mediaPlayer.setAudioSpectrumListener(spectrumListener);
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
	        	  //root.getChildren().remove(1);
				  interfacePane.setCenter(null);
			  if(visualizationChooserComboBox.getValue() == "Spectrum Bars") {
				  visualization = new SpectrumBars(maximumVolume);
			  }else if(visualizationChooserComboBox.getValue() == "Spectrum Bars Wide") {
				  visualization = new SpectrumBars(maximumVolume, 12);
			  }else if(visualizationChooserComboBox.getValue() == "Spectrum Line") {
				  visualization = new SpectrumLine(maximumVolume, true, false); //want to be able to choose smoothness and neg
			  }else if(visualizationChooserComboBox.getValue() == "Spectrum Circles") {
				  visualization = new SpectrumCircles(200); //want to configure 100-200
			  }
			  spectrumListener.setVisualization(visualization);
			  mediaPlayer.setAudioSpectrumInterval(AUDIO_SPECTRUM_INTERVAL);
			  mediaPlayer.setAudioSpectrumThreshold((-1)*visualization.getMaxVolume());
	    	  //root.getChildren().add(visualization.getNode());
			  interfacePane.setCenter(visualization.getNode()); // The visualizer occupies the center portion of the screen
			  stage.sizeToScene();
		  }
	  }
	  
	  public static void main(String[] args) {
		  launch(args);
	  }
	  
	  /**
	   * Loads all the available audio files (extension) from a given directory
	   * Sets the listOfSongs to contain the path of each discovered audio file
	   * @param dir The directory in which to search for audio files
	   */
	  private void loadAllAudioFiles(File dir) {
		  // listOfSongs.clear(); // TODO: do we want loading audio files to destroy the list of previous songs?
		  if(dir!=null) { // null can be passed in if the user cancels the directory-chooser dialog
			  File[] allFiles = dir.listFiles();
			  
			  for(File f : allFiles) {
				  if(f.isFile() && (f.getName().endsWith(".wav") || f.getName().endsWith(".mp3")))
					  listOfSongs.add(f.getAbsolutePath());
				  
			  }
		  }
		  else { /* Do nothing */ }
	  }
	  
	  /**
	   * Changes the format of a given string.
	   * This is particularly needed when the operating system formats file paths with '\' instead of '/',
	   * and keeps spaces unescaped instead of in the form %20.
	   * @param str The string to format
	   * @return A modified string with spaces escaped as %20 and forward-slashes instead of backslashes
	   */
	  private String encodeURL(String str) {
		  String holder = "";
		  for(int i = 0; i < str.length(); i++) {
			  String sChar = str.substring(i, i+1);
			  if(sChar.toCharArray()[0]==' ') 
				  holder += "%20";
			  else if(sChar.toCharArray()[0]=='\\') 
				 holder += "/";
			  else 
				  holder += str.substring(i, i+1);
		  }
		  return holder;
	  }
	  
	  @Override
	  public void start(Stage primaryStage) {
		  stage = primaryStage;
		  songFileChooser.setTitle("Open Mp3 File");
		  primaryStage.setTitle("Visual Eyes"); // clever name sounds like "visualize"
		  Scene scene = new Scene(root);
		  GridPane interfaceElements = new GridPane();
		  interfaceElements.setAlignment(Pos.TOP_LEFT);
		  interfaceElements.setVgap(5);
		  interfaceElements.setHgap(5);
		  
		  HBox topBarElements = new HBox();
		  
		  /*interfaceElements.add(songChooserLabel, 0, 0);
		  interfaceElements.add(songChooserTextField, 1, 0, 2, 1);
		  interfaceElements.add(songOpenButton, 3, 0);
		  interfaceElements.add(songBrowseButton, 4, 0);*/
		  
		  topBarElements.getChildren().addAll(/*songChooserLabel,songChooserTextField,songOpenButton,songBrowseButton,*/
				  visualizationChooserLabel,visualizationChooserComboBox,directoryBrowseButton);
		  
		  //interfaceElements.add(songListView, 20, 20); // goes on the right side of the screen
		  
		  //interfaceElements.add(visualizationChooserLabel, 0, 1);
		  //interfaceElements.add(visualizationChooserComboBox, 1, 1, 2, 1);
		  
		  
		  directoryBrowseButton.setOnAction(new EventHandler<ActionEvent>() {
			  @Override
			  public void handle(ActionEvent e) {
				  File dir = directoryBrowse.showDialog(primaryStage);
				  loadAllAudioFiles(dir);
			  }
			  
		  });

		  /*songOpenButton.setOnAction(new EventHandler<ActionEvent>() {
				 
			  @Override
			  public void handle(ActionEvent e) {
				  StartNewSong();
			  	}
		  });*/
			
		  /*songBrowseButton.setOnAction(new EventHandler<ActionEvent>() {
				
			  @Override
			  public void handle(ActionEvent e) {
				  File file = songFileChooser.showOpenDialog(songBrowseButton.getScene().getWindow());
				  if(file != null) {
					  try {
						  songChooserTextField.setText(file.toURI().toURL().toString().substring(6));
					  }	catch (Exception exc) {
						  exc.printStackTrace();
						  JOptionPane.showMessageDialog(null,
			        				"An error occurred while trying to load specified song.", "Open Song",
			        				JOptionPane.WARNING_MESSAGE);
					  }
					  StartNewSong();
				  }
			  }
		  });*/
		  
		  visualizationChooserComboBox.valueProperty().addListener(new ChangeListener<String>() {
	            public void changed(ObservableValue<? extends String> ov,
	                String old_val, String new_val) {
	                	UpdateVisualization();
	            }
	        });
		  
		  songListView.getSelectionModel().selectedItemProperty().addListener(
				  new ChangeListener<String>() { // event-listener associated with the user selecting a different song from the listview
					  public void changed(ObservableValue<? extends String> ov,
							  String old_val, String new_val) {
						  StartNewSong(encodeURL(new_val));
					  }
				  });
		  

		  //root.getChildren().add(interfaceElements);
		  interfacePane.setTop(topBarElements);
		  interfacePane.setRight(songListView);
		  // interfacePane.setCenter(_); // we already set the center pane to be the visualizer
		  root.getChildren().add(interfacePane);
		  primaryStage.setScene(scene);
		  
    	  mediaPlayer = new MediaPlayer(new Media("file:/"+songChooserTextField.getText().toString()));
    	  spectrumListener = new SpectrumListener();
    	  mediaPlayer.setAudioSpectrumListener(spectrumListener);
		  visualizationChooserComboBox.getSelectionModel().selectFirst();
    	  mediaPlayer.play();
    	  
		  primaryStage.show();
	  }
}
