
import javafx.scene.Node;
/**
 * The super class for every visualization. Demands that it have a getNode function,
 * an Update function, and declares the FrequencyCompressor object and maximum
 * volume variables needed to every visualization.
 * @author Eden Doonan
 *
 */
public abstract class Visualization {
	
	// The object that processes the magnitudes array.
	FrequencyCompressor compressor;
	
	// The maximum value of any number in the magnitudes array.
	int maxVolume;
	
	// The update function to be called from an instance of SpectrumListener.
	public abstract void Update(double timestamp, double duration, float[] magnitudes, float[] phases);
	
	// Lets the GUI get the node to display it for the user.
	public abstract Node getNode();
	
	// Not actually used anywhere. Was added in case we had visualizations that
	// needed a particular maximum volume, so the interface could update the
	// MediaPlayer object.
	public int getMaxVolume() { return maxVolume; }
}
