import java.util.Random;

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
	private Color startColor, endColor, backgroundColor;
	private Color[] colorShiftVals;
	private boolean isStartColorShifting;
	private final int COLOR_SHIFT_SPEED = 3;
	private final boolean smooth = true;
	private final boolean allowneg = true;
	Canvas display;
	FrequencyCompressor compressor;

	SpectrumLine() {
		this(100);
	}
	
	SpectrumLine(int volMax) {
		this(volMax, 600, 300);
	}
	
	SpectrumLine(int volMax, int w, int h) {
		System.out.println("New Spectrum Line");
		display = new Canvas();
		compressor = new FrequencyCompressor(volMax);
		isStartColorShifting = false;
		maxVolume = volMax;
		width = w;
		height = h;
		ypos = new double[97];
		xpos = new double[97];
		display.setWidth(width);
		display.setHeight(height);
		startColor = Color.GOLD;
		endColor = Color.BLUE;
		backgroundColor = Color.BLACK;
		maxheight = ((height/2) - 10);
		xpadding = width/95;
		edgepadding = 10 + (width%95)/2;
		updateColorShiftVals();
	}
	
	private Color randomColor() {
		int startDifference = 0, endDifference = 0;
		int r = 0, g = 0, b = 0;
		Random randGen = new Random(System.currentTimeMillis());
		while(startDifference < 100 || endDifference < 100) {
			r = Math.abs(randGen.nextInt()%256);
			g = Math.abs(randGen.nextInt()%256);
			b = Math.abs(randGen.nextInt()%256);
			startDifference = Math.abs((int) ( (double) startColor.getRed()*255.0) - r) +
			  		  		  Math.abs((int) ( (double) startColor.getGreen()*255.0) - g) +
			  		  		  Math.abs((int) ( (double) startColor.getBlue()*255.0) - b);
			endDifference = Math.abs((int) ( (double) endColor.getRed()*255.0) - r) +
			  		  		Math.abs((int) ( (double) endColor.getGreen()*255.0) - g) +
			  		  		Math.abs((int) ( (double) endColor.getBlue()*255.0) - b);
		}
		return new Color((double)r/255.0, (double)g/255.0, (double)b/255.0, 1.0);
	}
	
	private void updateColorShiftVals() {
		Color currentColor, finalColor;
		colorShiftIndex = 0;
		if(isStartColorShifting) {
			isStartColorShifting = false;
			currentColor = endColor;
		}
		else {
			isStartColorShifting = true;
			currentColor = startColor;
		}
		finalColor = randomColor();
		double rStart = currentColor.getRed();
		double gStart = currentColor.getGreen();
		double bStart = currentColor.getBlue();
		double rDifference = finalColor.getRed()-rStart;
		double gDifference = finalColor.getGreen()-gStart;
		double bDifference = finalColor.getBlue()-bStart;
		
		double maxDifference = Math.abs(rDifference);
		if(maxDifference < Math.abs(gDifference))
			maxDifference = Math.abs(gDifference);
		if(maxDifference < Math.abs(bDifference))
			maxDifference = Math.abs(bDifference);
		
		int numSteps = (int)(((maxDifference*255.0)/COLOR_SHIFT_SPEED)+.5);
		colorShiftVals = new Color[numSteps];
		
		for(int i = 0; i < numSteps; ++i) {
			colorShiftVals[i] = new Color(rStart+rDifference/(double)numSteps*i,
					 				 gStart+gDifference/(double)numSteps*i,
					 				 bStart+bDifference/(double)numSteps*i,
									 1.0);
		}
	}
	
	private void incrementColors() {
		if(isStartColorShifting)
			startColor = colorShiftVals[colorShiftIndex];
		else
			endColor = colorShiftVals[colorShiftIndex];
		colorShiftIndex += 1;
		if(colorShiftIndex == colorShiftVals.length)
			updateColorShiftVals();
	}
	
	private Color[] getColorVals() {
		Color[] colorVals = new Color[128];
		double rStart = startColor.getRed();
		double gStart = startColor.getGreen();
		double bStart = startColor.getBlue();
		double rEnd = endColor.getRed();
		double gEnd = endColor.getGreen();
		double bEnd = endColor.getBlue();
		for(int i = 0; i < 128; ++i) {
			colorVals[i] = new Color(rStart+(rEnd-rStart)/128*i,
									 gStart+(gEnd-gStart)/128*i,
									 bStart+(bEnd-bStart)/128*i,
									 1.0);
		}
		return colorVals;
	}
	
	private int[] processHeights(float[] magnitudes, double timestamp) {
		float[] buckets = compressor.compressHeights(magnitudes, 128, timestamp);
		int[] heights = new int[95];
		for(int i=0;i<95;i++){
			heights[i] = (int) ((float) buckets[i]*maxheight);
		}
		return heights;
	}
	
	public void Update(double timestamp, double duration, float[] magnitudes, float[] phases) {
		GraphicsContext context = display.getGraphicsContext2D();
		display.setWidth(width);
		display.setHeight(height);
		incrementColors();
		Color[] colorVals = getColorVals();
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
			context.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.1, true, CycleMethod.REFLECT,new Stop(0.0, startColor),new Stop(1.0, endColor)));
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
			context.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.1, true, CycleMethod.REFLECT,new Stop(0.0, startColor),new Stop(1.0, endColor)));
			context.fillPolygon(xpos,ypos,97);
		}
	}

	@Override
	public Node getNode() {
		return display;
	}
	
}
