package application;

import javafx.concurrent.Task;
import javafx.scene.Node;

public abstract class Visualization extends Task {
	
	
	protected int maxFrequency;
	
	public abstract void Update(double timestamp, double duration, float[] magnitudes, float[] phases);
	
	public abstract Node getNode();
}