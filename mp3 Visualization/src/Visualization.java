
import javafx.scene.canvas.Canvas;

public abstract class Visualization extends Canvas {
	
	int maxFrequency;
	
	public abstract void Update(double timestamp, double duration, float[] magnitudes, float[] phases);
}
