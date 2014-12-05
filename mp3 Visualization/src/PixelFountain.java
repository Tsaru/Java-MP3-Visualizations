
/**
 * Pixel Fountain
 * 
 * Pixel Fountain is a class that takes sound from spectrum listener and
 * outputs a visualization of points radiating out from a center onto a canvas.  
 * It extends the abstract class Visualization.
 * 
 * @author Kevin John Hemstreet-Grimmer
 */

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.awt.Point;
import java.util.Random;

public class PixelFountain extends Visualization
{
	private int  width, height, colorShiftIndex;
	private ArrayList<ArrayList<Point>> drops;//holds the points and directions of points
	private float[] dropBuildUp;
	private Color bottomColor, topColor, backgroundColor;
	private Color[] colorShiftVals;
	private double [] angles;
	private boolean isbottomColorShifting;
	private double timeSinceColorUpdate;
	private final int COLOR_SHIFT_SPEED = 3;
	private final double COLOR_UPDATE_FREQUENCY = .1;
	private final static float THRESHOLD = (float).1; 
	private final static int SPEED = 5; 
	private int increment = 0;
	
	Canvas display;
	FrequencyCompressor compressor;
	
	
	PixelFountain()
	{
		this(100);
	}
	PixelFountain(int volMax)
	{
		this(volMax, 400, 400);		
	}
	
	PixelFountain(int volMax, int w, int h) {//constructor Intitializes all value needed for the visual effects.
		System.out.println("New Pixel Fountain");
		display = new Canvas();
		//dropBuildUp = new float[90];
		angles = new double[90];
		drops = new ArrayList<ArrayList<Point>>();
		compressor = new FrequencyCompressor(volMax);
		isbottomColorShifting = false;
		bottomColor = Color.CYAN;
		topColor = Color.RED;
		colorShiftVals = Gradient.buildRandomGradient(topColor, 210, COLOR_SHIFT_SPEED);
		maxVolume = volMax;
		width = w;
		height = h;
		display.setWidth(width);
		display.setHeight(height);
		
		backgroundColor = Color.BLACK;
		Intitialize();
	}
	/**
	 * Creates the nested ArrayList to hold each point for
	 * each update of the canvas. 
	 * 
	 * that is the inner ArrayList not the entire structure
	 */
	public void Intitialize()
	{
		for(int i = 0; i < 90; i++)
		{
			drops.add(new ArrayList<Point>());
			//dropBuildUp[i] = 0;
			//System.out.println(dropBuildUp[i]);
		}
	}
	/**
	 * Using the Gradient Class increments the colors seen in the graph
	 */
	private void incrementColors() {
		if(isbottomColorShifting)
			bottomColor = colorShiftVals[colorShiftIndex];
		else
			topColor = colorShiftVals[colorShiftIndex];
		colorShiftIndex += 1;
		if(colorShiftIndex == colorShiftVals.length) {
			colorShiftIndex = 0;
			if(isbottomColorShifting) {
				isbottomColorShifting = false;
				colorShiftVals = Gradient.buildRandomGradient(topColor, 210, COLOR_SHIFT_SPEED);
			}
			else {
				isbottomColorShifting = true;
				colorShiftVals = Gradient.buildRandomGradient(bottomColor, 210, COLOR_SHIFT_SPEED);
			}
		}
	}
	/**
	 * Uses the compressor to logarithmically make the magnitudes even and gives esthetically pleasing number of drops back
	 * 
	 * @param magnitudes 	the magnitudes supplied by Update
	 * @param timestamp		the time supplied by Update
	 * @return				The heights used on the line graph
	 */
	private float[] ProcessDropCount(float[] magnitudes, double timestamp) {
		float[] buckets = compressor.compressHeights(magnitudes, 90, timestamp);
		return buckets;
	}
	
	/**
	 * Where all the action happens. updates the canvas and moves the drops across it.
	 *
	 * @param timestamp		the time after the spectrum listener started	
	 * @param duration		the time between each interval
	 * @param magnitudes	the magnitudes of each frequency used for height
	 * @param phases		the phases of each frequency used to make negative points
	 */
	
	public void Update(double timestamp, double duration, float[] magnitudes,
			float[] phases) 
	{
		GraphicsContext context = display.getGraphicsContext2D();
		display.setWidth(width);
		display.setHeight(height);
		context.setFill(backgroundColor);
		context.fillRect(0, 0, display.getWidth(), display.getHeight());
		
		//change color of the drops
		timeSinceColorUpdate += duration;
		if(timeSinceColorUpdate >= COLOR_UPDATE_FREQUENCY) {
			timeSinceColorUpdate = 0;
			incrementColors();
		}
		//grabs the sound levels from the Compressor for the points.
		float [] buckets = ProcessDropCount(magnitudes, timestamp);
		//sets up for animation
		//increment++;
		for (int i = 0; i < 90;i++)
		{
			angles[i] = 2 * Math.PI *i;
			if(buckets[i] > THRESHOLD)//checks to see if the bucket's value is above the threshold  
			{
				//if so, creates the center for which all drops radiate.
				//should be one for every updated bucket value.
				Point center = new Point(width/2,height/2);
				drops.get(i).add(center);
			}
			for (int j = 0, size = drops.get(i).size(); j<size ; j++)
			{
				//make the trig fucntions cosntant and make an array to hold those.
				
				//the visual path
				if(Math.cos((angles[i])) > 0 && Math.sin((angles[i])) > 0)
				{
					drops.get(i).get(j).setLocation(drops.get(i).get(j).getX()+Math.cos(angles[i])*SPEED, drops.get(i).get(j).getY()+Math.sin(angles[i])*SPEED);
				}
				else if(Math.cos((angles[i])) < 0 && Math.sin((angles[i])) < 0)
				{
					drops.get(i).get(j).setLocation(drops.get(i).get(j).getX()-Math.cos((angles[i]))*SPEED, drops.get(i).get(j).getY()-Math.sin((angles[i]))*SPEED);
				}
				else if(Math.sin((angles[i])) > 0 && Math.cos((angles[i])) < 0)
				{
					drops.get(i).get(j).setLocation(drops.get(i).get(j).getX()+Math.cos((angles[i]))*SPEED, drops.get(i).get(j).getY()-Math.sin((angles[i]))*SPEED);
				}
				else if(Math.sin((angles[i])) < 0 && Math.cos((angles[i])) > 0)
				{
					drops.get(i).get(j).setLocation(drops.get(i).get(j).getX()-Math.cos((angles[i]))*SPEED, drops.get(i).get(j).getY()+Math.sin((angles[i]))*SPEED);
				}
				context.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.1, true, CycleMethod.REPEAT,new Stop(0.0, topColor),new Stop(1.0, bottomColor)));
				context.fillRect(drops.get(i).get(j).getX(),drops.get(i).get(j).getY(), 1, 1);
			} 
		}
		CheckDrops();
	}
	/**
	 * deletes the drops as they go off screen
	 */
	public void CheckDrops()
	{
		for(int i = 0; i < 90;i++)
		{
			for(int j = 0, size = drops.get(i).size(); j < size;j++)
			{
				if (drops.get(i).get(j).getX() > 400 || drops.get(i).get(j).getY() > 400)
				{
					drops.get(i).remove(j);
					size--;
					j--;
				}
			}
		}
	}
	/**
	 * Returns the Canvas to the GUI 
	 */
	@Override
	public Node getNode() {
		return display;
	}
}

