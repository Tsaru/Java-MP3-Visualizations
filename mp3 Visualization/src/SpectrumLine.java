/**
 * Spectrum Line
 * 
 * Spectrum Line is a class that takes the data from spectrum listener and
 * outputs a visualization of a graph onto a canvas.  It extends the abstract
 * class Visualization.
 * 
 * @author Ryan Golden
 * @date: November 23, 2014. 
 */

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

public class SpectrumLine extends Visualization {
	private int maxheight, width, height, edgepadding, xpadding, colorShiftIndex;
	private double[] ypos, xpos;
	private Color bottomColor, topColor, backgroundColor;
	private Color[] colorShiftVals;
	private boolean isbottomColorShifting;
	private double timeSinceColorUpdate;
	private final int COLOR_SHIFT_SPEED = 3;
	private final double COLOR_UPDATE_FREQUENCY = .1;
	private boolean smooth, allowneg;
	Canvas display;
	FrequencyCompressor compressor;

	SpectrumLine() {
		this(100, true, true);
	}
	
	SpectrumLine(int volMax, boolean smoothness, boolean neg) {
		this(volMax, 600, 300, smoothness, neg);
	}
	
	/**
	 * The main constructor for SpectrumLine
	 * 
	 * @param volMax 		the max volume passed from the music player
	 * @param w 			the width of the canvas
	 * @param h 			the hieght of the canvas
	 * @param smoothness 	whether the graph is smooth or jagged
	 * @param neg			whether the graph has negative values
	 */
	SpectrumLine(int volMax, int w, int h, boolean smoothness, boolean neg) {
		System.out.println("New Spectrum Line");
		display = new Canvas();
		compressor = new FrequencyCompressor(volMax);
		isbottomColorShifting = false;
		maxVolume = volMax;
		width = w;
		height = h;
		ypos = new double[97];
		xpos = new double[97];
		display.setWidth(width);
		display.setHeight(height);
		topColor = Color.GOLD;
		bottomColor = Color.BLUE;
		backgroundColor = Color.BLACK;
		maxheight = ((height/2) - 10);
		xpadding = width/95;
		edgepadding = 10 + (width%95)/2;
		smooth = smoothness;
		allowneg = neg;
		colorShiftVals = Gradient.buildRandomGradient(topColor, 210, COLOR_SHIFT_SPEED);
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
		int[] heights = new int[95];
		for(int i=0;i<95;i++){
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
	public void Update(double timestamp, double duration, float[] magnitudes, float[] phases) {
		GraphicsContext context = display.getGraphicsContext2D();
		display.setWidth(width);
		display.setHeight(height);
		timeSinceColorUpdate += duration;
		if(timeSinceColorUpdate >= COLOR_UPDATE_FREQUENCY) {
			timeSinceColorUpdate = 0;
			incrementColors();
		}
		Color[] colorVals = Gradient.buildGradient(bottomColor, topColor, height);
		int[] heights = processHeights(magnitudes, timestamp);
		int origin = height/2;
		context.setFill(backgroundColor);
		context.fillRect(0, 0, display.getWidth(), display.getHeight());
		context.setStroke(Color.WHITE);
		context.strokeLine(0,(height/2),width,(height/2));
		if(smooth){
			context.beginPath();
			for(int i=0,xcur=edgepadding; i<95; xcur+=xpadding,i++) {
				if(phases[i] < 0 && allowneg){
					ypos[i] = origin + heights[i];
				}else{
					ypos[i] = origin - heights[i];
				}
				if(i == 0){
					context.moveTo(xcur, origin);
				}else if(i%3 == 0 && !(i >= 92)){
					context.bezierCurveTo(xpos[i-2], ypos[i-2], xpos[i-1], ypos[i-1], xpos[i], ypos[i]);
				}else if(i == 94){
					context.bezierCurveTo(xpos[i-2], ypos[i-2], xpos[i-1], ypos[i-1], xcur, origin);
				}else{
				}
				xpos[i] = xcur; 
			}
			context.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.1, true, CycleMethod.REFLECT,new Stop(0.0, topColor),new Stop(1.0, bottomColor)));
			context.fill();
			context.closePath();
		}else{
			for(int i=0,xcur=edgepadding; i<95; xcur+=xpadding,i++) {
				if(phases[i] < 0 && allowneg){
					ypos[i] = origin + heights[i];
				}else{
					ypos[i] = origin - heights[i];
				}
				if(i == 0){
				}else if(i == 1){
					context.setStroke(colorVals[i]);
					context.strokeLine(xpos[i-1],origin,xpos[i-1],ypos[i-1]);
					context.strokeLine(xpos[i-1],ypos[i-1],xcur,ypos[i]);
				}else if(i == 94){
					context.setStroke(colorVals[i]);
					context.strokeLine(xpos[i-1],ypos[i-1],xcur,ypos[i]);
					context.strokeLine(xcur,ypos[i],xcur,origin);
				}else{
					context.setStroke(colorVals[i]);
					context.strokeLine(xpos[i-1],ypos[i-1],xcur,ypos[i]);
				}
				xpos[i] = xcur; 
			}
			//really ugly fix for fill
			xpos[95] = xpos[94];
			xpos[96] = xpos[0];
			ypos[95] = origin;
			ypos[96] = origin;
			context.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.1, true, CycleMethod.REFLECT,new Stop(0.0, topColor),new Stop(1.0, bottomColor)));
			context.fillPolygon(xpos,ypos,97);
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
