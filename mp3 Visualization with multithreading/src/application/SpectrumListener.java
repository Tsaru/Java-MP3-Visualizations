package application;

import javafx.scene.media.AudioSpectrumListener;

public class SpectrumListener implements AudioSpectrumListener {

	Visualization visualization;
	
	SpectrumListener(Visualization visualization) {
		this.visualization = visualization;
	}
	
	public Visualization getVisualization() {
		return visualization;
	}
	
	public void setvisualization(Visualization visualization) {
		this.visualization = visualization;
	}
	
	@Override
	public void spectrumDataUpdate( double timestamp, double duration,
									float[] magnitudes, float[] phases) {
		visualization.Update(timestamp, duration, magnitudes, phases);
	}

}
