import javafx.scene.media.AudioSpectrumListener;


/**
 * This class connects a MediaPlayer object to a visualization.
 * 
 * By implementing the AudioSpectrumListener interface, This class
 * can be set as a spectrum listener for a MediaPlayer object,
 * which calls the spetctrumDataUpdate function every x seconds,
 * where x is an adjustable value defaulting to 0.1. This class
 * just passes the information on to the visualization for processing.
 * @author Tsaru
 */
public class SpectrumListener implements AudioSpectrumListener {

	// The visualization we're communicating to.
	Visualization visualization;
	
	/**
	 * Initializes this object with a SpectrumBars visualization.
	 */
	SpectrumListener() {
		visualization = new SpectrumBars();
	}
	
	/**
	 * Initializes this object with the provided visualization.
	 * @param visualization The visualization to send data to.
	 */
	SpectrumListener(Visualization visualization) {
		this.visualization = visualization;
	}
	
	/**
	 * Gets the internal visualization.
	 * @return The visualization this object is communicating to.
	 */
	public Visualization getVisualization() {
		return visualization;
	}

	/**
	 * Sets this object to communicate with the provided visualization.
	 * @param visualization The visualization to send data to.
	 */
	public void setVisualization(Visualization visualization) {
		this.visualization = visualization;
	}
	
	/**
	 * Calls the visualization's update method with all of the provided variables.
	 */
	@Override
	public void spectrumDataUpdate( double timestamp, double duration,
									float[] magnitudes, float[] phases) {
		visualization.Update(timestamp, duration, magnitudes, phases);
	}

}
