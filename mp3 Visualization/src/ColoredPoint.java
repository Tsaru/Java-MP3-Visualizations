import javafx.scene.paint.Color;


public class ColoredPoint {
	private int x, y;
	private Color color;
	
	ColoredPoint(int x, int y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setLocation(double x, double y) {
		this.x = (int) x;
		this.y = (int) y;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
}
