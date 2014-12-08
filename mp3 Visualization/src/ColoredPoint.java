import javafx.scene.paint.Color;

/**
 * 
 * Jsut a Point that can hold a color by itself,
 * 
 * @author Kevin John Hemstreet-Grimmer
 *
 */


public class ColoredPoint {
	private int x, y;
	private Color color;
	/**
	 * Constructor 
	 * @param x - the x coordinate of the point
	 * @param y - the y coordinate of the point
	 * @param color - the color, that retains its color.
	 */
	
	ColoredPoint(int x, int y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}
	/**
	 * gets the x coordinate
	 * @return
	 */
	public int getX() {
		return x;
	}
	/**
	 * gets the y coordinate
	 * @return
	 */
	public int getY() {
		return y;
	}
	/**
	 * gets the color of the point
	 * @return
	 */
	public Color getColor() {
		return color;
	}
	/**
	 * sets the new location of the point
	 * @param x - the integer x coordinate of the point
	 * @param y - the integer y coordinate of the point
	 */
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	/**
	 * sets the new location of the point, for error handling purposes
	 * @param x - the double x coordinate of the point
	 * @param y - the double y coordinate of the point
	 */
	public void setLocation(double x, double y) {
		this.x = (int) x;
		this.y = (int) y;
	}
	/**
	 * Set the points new color
	 * @param color
	 */
	public void setColor(Color color) {
		this.color = color;
	}
}
