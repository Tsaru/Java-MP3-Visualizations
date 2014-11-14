
import javafx.scene.Node;

public abstract class Visualization {
	
	int maxVolume;
	
	public abstract void Update(double timestamp, double duration, float[] magnitudes, float[] phases);
	
	public abstract Node getNode();
	
	public int getMaxVolume() { return maxVolume; }
}
