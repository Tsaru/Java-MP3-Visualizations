

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * This class provides a GUI for our music visualization program. It plays
 * the songs, manages a playlist of songs, and manages which visualization
 * is active.s
 * @author Eden Doonan
 * @author Greg Lang
 */
public class VisualizationInterface extends Application {
	
	  // The maximum value of any element of the magnitudes array passed to
	  // the SpectrumListener object by the MediaPlayer object.
	  final int maximumVolume = 100;
	  // The value we will set with MediaPlayer.setAudioSpectrumInterval().
	  final double AUDIO_SPECTRUM_INTERVAL = 0.08;
	
	  // UI elements
	  private Stage stage;
	  private final BorderPane interfacePane = new BorderPane();
	  private final BorderPane visualizationPane = new BorderPane();
	  private final Button directoryBrowseButton = new Button("Open Playlist");
	  private final DirectoryChooser directoryBrowse = new DirectoryChooser();
	  private final Button playButton = new Button("Play");
	  private final Button pauseButton = new Button("Pause");
	  private final Slider slideDisplay = new Slider(0,1,0);
	  private final Label durationLabel = new Label("");
	  
	  // UI elements for choosing the active visualization
	  private final Label visualizationChooserLabel = new Label("Visualization Mode");
	  private final ObservableList<String> visualizationList = 
			    FXCollections.observableArrayList(
			        "Spectrum Bars",
			        "Spectrum Bars Wide",
			        "Spectrum Line",
			        "Spectrum Circles",
			        "Pixel Fountain"
			    );
	  private final ComboBox<String> visualizationChooserComboBox = new ComboBox<String>(visualizationList);
	  
	  // The visualization object being displayed on the screen.
	  private Visualization visualization;
	  
	  // The object to connect the Visualization to the MediaPlayer.
	  private SpectrumListener spectrumListener;
	  
	  // The object to play the music
	  private MediaPlayer mediaPlayer;
	  private VBox root = new VBox(10);
	  
	  private ObservableList<String> listOfSongs = FXCollections.observableArrayList(); // contains the list of songs the user can play
	  private final ListView<String> songListView = new ListView<String>(listOfSongs);
	  
	  private final Map<String,String> songNameLocMap = new HashMap<String,String>(); // Song name -> Song path (eg. Song1 -> C:\Users\..\Song1.mp3)
	  
	  /*
	   * Starts a new song by setting the mediaPlayer to a new MediaPlayer object,
	   * and playing it. Also re-sets the spectrumListener because it's a new object.
	   */
	  private void StartNewSong(String song_pathname) {
	      try {
	    	  mediaPlayer.stop();
	    	  mediaPlayer = new MediaPlayer(new Media("file:/"+song_pathname));
	    	  mediaPlayer.setAudioSpectrumListener(spectrumListener);
	    	  UpdateVisualization();
	    	  
	    	  slideDisplay.setMin(0d);
	    	  slideDisplay.setMax(mediaPlayer.getTotalDuration().toSeconds());
	    	  durationLabel.setText(mediaPlayer.getCurrentTime().toString());
	    	  
        	  mediaPlayer.play();
        	  
	      } catch (Exception exc) {
	    	  exc.printStackTrace();
	    	  JOptionPane.showMessageDialog(null,
	        			"An error occurred trying to load the selected song. Please try again.", "Visual Eyes",
	        			JOptionPane.WARNING_MESSAGE);
	      }
	  }
	  
	  /*
	   * Changes the active visualization by updating the internal spectrumListener and
	   * mediaPlayer objects, removing the old visualization from the GUI, and adding
	   * the new one.
	   */
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
			  }else if(visualizationChooserComboBox.getValue() == "Pixel Fountain") {
				  visualization = new PixelFountain(maximumVolume, 300, 300); //want to configure 100-200
			  }
			  spectrumListener.setVisualization(visualization);
			  mediaPlayer.setAudioSpectrumInterval(AUDIO_SPECTRUM_INTERVAL);
			  mediaPlayer.setAudioSpectrumThreshold((-1)*visualization.getMaxVolume());
			  visualizationPane.setCenter(visualization.getNode());
			  interfacePane.setCenter(visualizationPane); // The visualizer occupies the center portion of the screen
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
	  private void loadAllAudioFiles(File dir, int depth) {
		  // listOfSongs.clear(); // remove comment if we want loading audio files to destroy the current list of songs
		  if(depth==2) {return;} // prevent too much recursion (searching through too many sub-directories)
		  
		  // prevents user from selecting a drive, C:\ D:\ etc., which will crash the program -> too many files
		  for(File drive : File.listRoots()) {
			  if (dir == drive) {
					// There is no easy way to display a message dialog to the user
					// without creating another stage
					return;
			  }
		  }

		  if(dir!=null) { // null can be passed in if the user cancels the directory-chooser dialog
			  try {
				  File[] allFiles = dir.listFiles();
				  for(File f : allFiles) {
					  if(f.isFile() && (f.getName().endsWith(".wav") || f.getName().endsWith(".mp3"))) {
						  String songNameNoExt = f.getName().substring(0, f.getName().indexOf(".")); // Drops of Jupiter, not Drops of Jupiter.mp3
						  songNameLocMap.put(songNameNoExt, f.getAbsolutePath());
						  listOfSongs.add(songNameNoExt);
					  }
					  else if(f.isDirectory()) {
						  loadAllAudioFiles(f,++depth); // recursive call to search sub-directories
					  }
				  }
				  
			  }
			  catch(SecurityException se) { } // i.e. permissions not granted to read specified directory
			  catch(Exception e) { } // error occurred in loading directory or adding file to map
		  }
		  else { /* Do nothing */ /* Maximum depth may have been reached */}
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
	  public void start(final Stage primaryStage) {
		  stage = primaryStage;
		  primaryStage.setTitle("Visual Eyes"); // clever name sounds like "visualize"
		  Scene scene = new Scene(root);
		  GridPane interfaceElements = new GridPane();
		  interfaceElements.setAlignment(Pos.TOP_LEFT);
		  interfaceElements.setVgap(5);
		  interfaceElements.setHgap(5);
		  
		  HBox topBarElements = new HBox(8); // the number in the constructor specifies margin between each element
		  topBarElements.getChildren().addAll(visualizationChooserLabel,visualizationChooserComboBox/*,directoryBrowseButton*/);
		  
		  VBox controlElements = new VBox();
		  
		  VBox playListControls = new VBox();
		  directoryBrowseButton.setMinWidth(250d);
		  playListControls.getChildren().addAll(songListView,directoryBrowseButton);
		  
		  HBox songControls = new HBox();
		  songControls.getChildren().addAll(playButton,pauseButton);
		  songControls.setAlignment(Pos.CENTER);
		  
		  HBox songDurationDisplay = new HBox();
		  slideDisplay.setMinWidth(400d);
		  songDurationDisplay.getChildren().addAll(durationLabel,slideDisplay);
		  
		  controlElements.getChildren().addAll(songControls/*,songDurationDisplay*/);
		  
		  directoryBrowseButton.setOnAction(new EventHandler<ActionEvent>() {
			  @Override
			  public void handle(ActionEvent e) {
				  File dir = directoryBrowse.showDialog(primaryStage);
				  loadAllAudioFiles(dir,0);
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
						  StartNewSong(encodeURL(songNameLocMap.get(new_val)));
					  }
				  });
		  
		  interfacePane.setTop(topBarElements);
		  interfacePane.setRight(playListControls);
		  visualizationPane.setBottom(controlElements);
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
