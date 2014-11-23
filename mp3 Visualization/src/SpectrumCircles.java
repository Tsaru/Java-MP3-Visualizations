/**
 * Spectrum Circles
 * 
 * Spectrum Circles is a class that takes the data from spectrum listener and
 * outputs a visualization of multiple circles onto a canvas.  The user can create more
 * circles and move existing ones. It extends the abstract class Visualization.
 * 
 * @author Ryan Golden
 * @date: November 23, 2014. 
 */

import java.awt.Point;
import java.util.ArrayList;
import java.lang.Math;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;


public class SpectrumCircles extends Visualization  {
		private int maxheight, width, height, colorShiftIndex;
		private Color bottomColor, topColor, backgroundColor;
		private Color[] colorShiftVals;
		private boolean isbottomColorShifting;
		private double timeSinceColorUpdate;
		private final int COLOR_SHIFT_SPEED = 3;
		private final double COLOR_UPDATE_FREQUENCY = .1;
		private final int NUMPOINTS = 120;
		private final int CLICK_ADJUSTMENT = 25;
		private final int RADIUS = 50;
		private ArrayList<Point> centers;
		Canvas display;
		FrequencyCompressor compressor;

		/**
		 * The main Constructor for spectrum circles
		 * @param maxh	the size of the spikes coming out of the circles
		 */
		SpectrumCircles(int maxh){
    		System.out.println("New Spectrum Circles");
        	maxVolume = 100;
        	maxheight = maxh;
        	display = new Canvas();
    		compressor = new FrequencyCompressor(maxVolume);
    		isbottomColorShifting = false;
        	width = 500;
        	height = 500;
        	centers = new ArrayList<Point>();
    		topColor = Color.GOLD;
    		bottomColor = Color.BLUE;
    		backgroundColor = Color.BLACK;
    		colorShiftVals = Gradient.buildRandomGradient(topColor, 210, COLOR_SHIFT_SPEED);
        	display.setHeight(height);
        	display.setWidth(width);
    		display.addEventHandler(MouseEvent.MOUSE_DRAGGED, 
 			       new EventHandler<MouseEvent>() {
 			           @Override
 			           public void handle(MouseEvent e) {
 			        	   int index = insideCircle(e.getX(), e.getY());
 			        	   if(index >= 0){
 			        		   centers.remove(index);
 			        	   }
 			        	   addCircle((int)e.getX(),(int)e.getY());
 			           }
 			       });
    		display.addEventHandler(MouseEvent.MOUSE_CLICKED, 
			       new EventHandler<MouseEvent>() {
			           @Override
			           public void handle(MouseEvent e) {
			        	   int index = insideCircle(e.getX(), e.getY());
			        	   if(index >= 0){
			        		   centers.remove(index);
			        	   }
			        	   addCircle((int)e.getX(),(int)e.getY());
			           }
			       });
        }
        
		/**
		 * Adds a point to the centers data member for the center of a circle
		 * 
		 * @param x		x pos of the point
		 * @param y		y pos of the point
		 */
        public void addCircle(int x, int y){
        	Point p = new Point(x-RADIUS-CLICK_ADJUSTMENT,y-RADIUS-CLICK_ADJUSTMENT);
        	centers.add(p);
        }
        
        /**
         * Checks to see if the person clicked within a circle that is alread made
         * @param x		the x pos of where the user clicked
         * @param y		the y pos of where the user clicked
         * @return		the index of the circle is is inside, -1 if none
         */
        public int insideCircle(double x, double y){
        	int size = centers.size();
        	int newx = (int)x-RADIUS-CLICK_ADJUSTMENT;
        	int newy = (int)y-RADIUS-CLICK_ADJUSTMENT;
        	for(int i = 0; i < size; i++){
        		if(((centers.get(i).getX() + 50)>newx && newx>(centers.get(i).getX() - 50)) && 
        				((centers.get(i).getY() + 50)>newy && newy>(centers.get(i).getY() - 50))){
        			return i;
        		}
        	}
        	return -1;
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
    	 * Uses the compressor to logarithmically make the magnitudes even and gives asthetically pleasing heights back
    	 * @param magnitudes 	the magnitudes supplied by Update
    	 * @param timestamp		the time supplied by Update
    	 * @return				The heights used on the line graph
    	 */
    	private int[] processHeights(float[] magnitudes, double timestamp) {
    		float[] buckets = compressor.compressHeights(magnitudes, 128, timestamp);
    		int[] heights = new int[NUMPOINTS];
    		for(int i=0;i<NUMPOINTS;i++){
    			heights[i] = (int) ((float) buckets[i]*maxheight);
    		}
    		return heights;
    	}

    	/**
    	 * Updates the Canvas of the graph for a certain time interval
    	 * 
    	 * @param timestamp		the time after the spectrum listener started	
    	 * @param duration		the time between each interval
    	 * @param magnitudes	the magnitudes of each frequency used for height
    	 * @param phases		the phases of each frequency used to make negative points
    	 */
        public void Update(double timestamp, double duration, float[] magnitudes, float[] phases){
    		final GraphicsContext context = display.getGraphicsContext2D();
    		display.setWidth(width);
    		display.setHeight(height);
    		context.setFill(backgroundColor);
    		context.fillRect(0, 0, display.getWidth(), display.getHeight());
        	int size = centers.size();
        	timeSinceColorUpdate += duration;
    		if(timeSinceColorUpdate >= COLOR_UPDATE_FREQUENCY) {
    			timeSinceColorUpdate = 0;
    			incrementColors();
    		}
    		int[] heights = processHeights(magnitudes, timestamp);
        	for(int i = 0; i < size; i++){
        		int xcent = ((int) centers.get(i).getX());
        		int ycent = ((int) centers.get(i).getY());
        		double[] ypos = new double[NUMPOINTS];
        		double[] xpos = new double[NUMPOINTS];
    			context.beginPath();
	    		for(int j=0;j<NUMPOINTS;j++) {
					ypos[j] = ycent+(heights[j]*Math.sin((360/NUMPOINTS)*j)+RADIUS+CLICK_ADJUSTMENT);
					xpos[j] = xcent+(heights[j]*Math.cos((360/NUMPOINTS)*j)+RADIUS+CLICK_ADJUSTMENT);
        			if(j == 0){
    					context.moveTo(xpos[j], ypos[j]);
    				}else if(j%3 == 0 && !(j >= 92)){
    					context.bezierCurveTo(xpos[j-2], ypos[j-2], xpos[j-1], ypos[j-1], xpos[j], ypos[j]);
    				}
	    		}
    			context.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.1, true, CycleMethod.REPEAT,new Stop(0.0, topColor),new Stop(1.0, bottomColor)));
    			context.fill();
    			context.closePath();
        		context.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.1, true, CycleMethod.REPEAT,new Stop(0.0, topColor),new Stop(1.0, bottomColor)));
        		context.fillOval((xcent + 50), (ycent + 50), RADIUS, RADIUS);
        	}
        }

    	/**
    	 * Returns the Canvas to the interface
    	 */
    	@Override
    	public Node getNode() {
    		return display;
    	}
}
