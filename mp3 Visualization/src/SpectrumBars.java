import java.util.Random;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class SpectrumBars extends Visualization {
	
	private int numBars, barHeight, rectangleWidth, rectangleHeight, horizontalGap, verticalGap, colorShiftIndex;
	private Color startColor, endColor, backgroundColor;
	private final int VERTICAL_PADDING = 6;
	private final int HORIZONTAL_PADDING = 8;
	private Color[] colorShiftVals;
	private boolean isStartColorShifting;
	private final int COLOR_SHIFT_SPEED = 3;
	private float[] minMagnitudes, maxMagnitudes;
	Canvas display;

	SpectrumBars() {
		this(100);
	}
	
	SpectrumBars(int volMax) {
		this(volMax, 8, 12, 40, 15, 7, 3);
	}
	
	SpectrumBars(int volMax, int bars) {
		this(volMax, bars, 12, 40, 15, 7, 3);
	}
	
	SpectrumBars(int volMax, int bars, int height, int rectWidth, int rectHeight, int hGap, int vGap) {
		display = new Canvas();
		isStartColorShifting = false;
		maxVolume = volMax;
		numBars = bars;
		barHeight = height;
		rectangleWidth = rectWidth;
		rectangleHeight = rectHeight;
		horizontalGap = hGap;
		verticalGap = vGap;
		display.setWidth(rectangleWidth*numBars+horizontalGap*(numBars+1)+HORIZONTAL_PADDING);
		display.setHeight(rectangleHeight*barHeight+verticalGap*(barHeight+1)+VERTICAL_PADDING);
		startColor = Color.GOLD;
		endColor = Color.BLUE;
		backgroundColor = Color.BLACK;
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
		Color[] colorVals = new Color[barHeight];
		double rStart = startColor.getRed();
		double gStart = startColor.getGreen();
		double bStart = startColor.getBlue();
		double rEnd = endColor.getRed();
		double gEnd = endColor.getGreen();
		double bEnd = endColor.getBlue();
		for(int i = 0; i < barHeight; ++i) {
			colorVals[i] = new Color(rStart+(rEnd-rStart)/barHeight*i,
									 gStart+(gEnd-gStart)/barHeight*i,
									 bStart+(bEnd-bStart)/barHeight*i,
									 1.0);
		}
		return colorVals;
	}
	
	private int[] processHeights(float[] magnitudes) {

		double sum = 0;
		int bar_count = 0;
		int[] heights = new int[numBars];
		double maxRange = ((((double) magnitudes.length/(double) numBars)*maxVolume));
		double max = 0;
		double min = 0;
		int maxHeight = barHeight;
		for(int i = 0; i < magnitudes.length; ++i) {
			if(i+1 >= (bar_count+1)*(magnitudes.length/numBars)) {
				//double val = (double) (numBars-bar_count-1)/numBars*.8;
				max = max*1.25;
				if(max > maxRange)
					max = maxRange;
				min = min*0.75;
				//System.out.format("bar: %d, max: %f, min: %f, sum: %f%n", bar_count, max, min, sum);
				sum = (sum - min);
				//sum += (int)(max-sum)*val;
				heights[bar_count] = (int) ( (double) Math.pow(sum/(maxRange-min), 6)*maxHeight);
				bar_count+=1;
				sum = (double) -1*magnitudes[i];
				max = 0;
				min = 0;
			}
			else  {
				sum += (double) -1*magnitudes[i];
				max += maxMagnitudes[i];
				min += minMagnitudes[i];
			}
		}
		if(bar_count != numBars)
			heights[bar_count] = -1*((int) ( (sum/(maxRange-(numBars-bar_count)*20+300))*((double) barHeight)));
		return heights;
	}
	
	private void initializeMinMax(float[] magnitudes) {
		//System.out.println("INITIALIZING");
		minMagnitudes = new float[magnitudes.length];
		maxMagnitudes = new float[magnitudes.length];
		for(int i = 0; i < magnitudes.length; ++i) {
			minMagnitudes[i] = -1*magnitudes[i];
			maxMagnitudes[i] = -1*magnitudes[i];
		}
	}
	
	private void updateMinMax(float[] magnitudes) {
		if(minMagnitudes == null || maxMagnitudes == null)
			initializeMinMax(magnitudes);
		else if(minMagnitudes.length != magnitudes.length || maxMagnitudes.length != magnitudes.length)
			initializeMinMax(magnitudes);
		else
			for(int i = 0; i < magnitudes.length; ++i) {
				if(-1*magnitudes[i] < minMagnitudes[i]) {
					minMagnitudes[i] = -1*magnitudes[i];
					//System.out.println("New min");
				}
				if(-1*magnitudes[i] > maxMagnitudes[i]) {
					//System.out.format("magnitude: %f, max: %f, result: ", magnitudes[i], maxMagnitudes[i]);
					//System.out.println(magnitudes[i] > maxMagnitudes[i]);
					maxMagnitudes[i] = -1*magnitudes[i];
				}
			}
	}
	
	public void Update(double timestamp, double duration, float[] magnitudes, float[] phases) {
		updateMinMax(magnitudes);
		GraphicsContext context = display.getGraphicsContext2D();
		display.setWidth(rectangleWidth*numBars+horizontalGap*(numBars+1)+HORIZONTAL_PADDING);
		display.setHeight(rectangleHeight*barHeight+verticalGap*(barHeight+1)+VERTICAL_PADDING);
		incrementColors();
		Color[] colorVals = getColorVals();
		int[] heights = processHeights(magnitudes);
		context.setFill(backgroundColor);
		context.fillRect(0, 0, display.getWidth(), display.getHeight());
		for(int bar_index = 0; bar_index < numBars; ++bar_index) {
			if(heights[bar_index] >= barHeight)
				heights[bar_index] = barHeight-1;
			int x_val = rectangleWidth*bar_index+horizontalGap*(bar_index+1)+HORIZONTAL_PADDING/2;
			for(int rectangle_index = 1; rectangle_index <= heights[bar_index]; ++rectangle_index) {
				int y_val = (int) display.getHeight() - (rectangleHeight*rectangle_index+verticalGap*(rectangle_index+1)+VERTICAL_PADDING/2);
				context.setFill(colorVals[rectangle_index]);
				context.fillRect(x_val, y_val, rectangleWidth, rectangleHeight);
			}
		}
		
	}

	@Override
	public Node getNode() {
		return display;
	}
	
}
