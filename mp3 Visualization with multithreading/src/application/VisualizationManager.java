package application;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class VisualizationManager extends Task<Void> {
	
	Pane pane;
	int maxVolume;
	double timestamp;
	double duration;
	float[] magnitudes;
	float[] phases;
	Visualization visualization;
	
	VisualizationManager(int maxVolume, Pane pane) {
		this.pane = pane;
		this.maxVolume = maxVolume;
		visualization = new SpectrumBars(maxVolume);
		pane.getChildren().add(visualization.getNode());
	}
	
	void setData(double timestamp, double duration, float[] magnitudes, float[] phases) {
		this.timestamp = timestamp;
		this.duration = duration;
		this.magnitudes = magnitudes;
		this.phases = phases;
	}
	
	void changeVisualization(String visualizationName) {
		  if(visualization != null)
        	  pane.getChildren().remove(1);
		  if(visualizationName == "Spectrum Bars") {
			  visualization = new SpectrumBars(maxVolume);
		  }
		  if(visualizationName == "Animated Circle") {
			  visualization = new SpectrumBars(maxVolume);
		  }
		  pane.getChildren().add(visualization.getNode());
	}
	
	@Override
	protected Void call() throws Exception {
		wait();
		visualization.Update(timestamp, duration, magnitudes, phases);
		return null;
	}

}
