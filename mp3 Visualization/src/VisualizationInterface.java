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
	  private final BorderPane interfacePane = new BorderPane();
	  private final Button directoryBrowseButton = new Button("Open Playlist");
	  private final DirectoryChooser directoryBrowse = new DirectoryChooser();
	  private final Button playButton = new Button("Play");
	  private final Button pauseButton = new Button("Pause");
	  
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
	    	  mediaPlayer.stop();
	    	  mediaPlayer = new MediaPlayer(new Media("file:/"+song_pathname));
	    	  mediaPlayer.setAudioSpectrumListener(spectrumListener);
	    	  UpdateVisualization();
        	  mediaPlayer.play();
	      } catch (Exception exc) {
	    	  exc.printStackTrace();
	    	  JOptionPane.showMessageDialog(null,
	        			"An error occurred trying to load the selected song. Please try again.", "Visual Eyes",
	        			JOptionPane.WARNING_MESSAGE);
	      }
	  }
	  
	  private void UpdateVisualization() {
		  if(visualizationChooserComboBox.getValue() != null) {
			  if(visualization != null)
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
			  try {
				  File[] allFiles = dir.listFiles();
				  for(File f : allFiles) {
					  if(f.isFile() && (f.getName().endsWith(".wav") || f.getName().endsWith(".mp3")))
						  listOfSongs.add(f.getAbsolutePath());
					  else if(f.isDirectory())
						  loadAllAudioFiles(f); // recursive call to search sub-directories
				  }
				  
			  }
			  catch(SecurityException se) { } // i.e. permissions not granted to read specified directory
			  catch(Exception e) { }
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
				  holder += sChar;
		  }
		  return holder;
	  }
	  
	  @Override
	  public void start(Stage primaryStage) {
		  stage = primaryStage;
		  primaryStage.setTitle("Visual Eyes"); // clever name sounds like "visualize"
		  Scene scene = new Scene(root);
		  GridPane interfaceElements = new GridPane();
		  interfaceElements.setAlignment(Pos.TOP_LEFT);
		  interfaceElements.setVgap(5);
		  interfaceElements.setHgap(5);
		  
		  HBox topBarElements = new HBox();
		  topBarElements.getChildren().addAll(visualizationChooserLabel,visualizationChooserComboBox,directoryBrowseButton);
		  
		  HBox controlElements = new HBox();
		  controlElements.getChildren().addAll(playButton,pauseButton);
		  controlElements.setAlignment(Pos.CENTER);
		  
		  directoryBrowseButton.setOnAction(new EventHandler<ActionEvent>() {
			  @Override
			  public void handle(ActionEvent e) {
				  File dir = directoryBrowse.showDialog(primaryStage);
				  loadAllAudioFiles(dir);
			  }
		  });
		  
		  playButton.setOnAction(new EventHandler<ActionEvent>() {
			  @Override
			  public void handle(ActionEvent e) {
				  mediaPlayer.play();
			  }
		  });
		  pauseButton.setOnAction(new EventHandler<ActionEvent>() {
			  @Override
			  public void handle(ActionEvent e) {
				  mediaPlayer.pause();
			  }
		  });
		  
		  visualizationChooserComboBox.valueProperty().addListener(new ChangeListener<String>() {
	            public void changed(ObservableValue<? extends String> ov,
	                String old_val, String new_val) {
	                	UpdateVisualization();
	            }
	        });
		  
		  songListView.getSelectionModel().selectedItemProperty().addListener(
				  new ChangeListener<String>() { // event-listener for the user selecting a different song from the listview
					  public void changed(ObservableValue<? extends String> ov,
							  String old_val, String new_val) {
						  StartNewSong(encodeURL(new_val));
					  }
				  });
		  

		  interfacePane.setTop(topBarElements);
		  interfacePane.setRight(songListView);
		  interfacePane.setBottom(controlElements);
		  root.getChildren().add(interfacePane);
		  primaryStage.setScene(scene);
		  
    	  mediaPlayer = new MediaPlayer(new Media("file:/"+getClass().getResource("Train - Drops of Jupiter.mp3").toString().substring(6)));
    	  spectrumListener = new SpectrumListener();
    	  mediaPlayer.setAudioSpectrumListener(spectrumListener);
		  visualizationChooserComboBox.getSelectionModel().selectFirst();
    	  mediaPlayer.play();

		  primaryStage.show();
	  }
}
