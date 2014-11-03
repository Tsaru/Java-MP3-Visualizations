package application;

import javafx.scene.media.AudioSpectrumListener;

public class SpectrumListener implements AudioSpectrumListener {

	Visualization myDisplay;
	
	SpectrumListener(Visualization display) {
		myDisplay = display;
	}
	
	public Visualization getDisplay() {
		return myDisplay;
	}
	
	public void setDisplay(Visualization display) {
		myDisplay = display;
	}
	
	@Override
	public void spectrumDataUpdate( double timestamp, double duration,
									float[] magnitudes, float[] phases) {
		myDisplay.Update(timestamp, duration, magnitudes, phases);
	}

}
