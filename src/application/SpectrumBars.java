package application;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SpectrumBars extends Canvas {
	//Declaration of all the variables in the class SpectrumBars
	
	private int numBars, barHeight, rectangleWidth, rectangleHeight, horizontalGap, verticalGap;
	private Color startColor, endColor, backgroundColor;
	private final int VERTICAL_PADDING = 6;
	private final int HORIZONTAL_PADDING = 8;
	private Color[] colorVals;
	
	// Class constructor that initiallizes previously mention variables
	SpectrumBars() {
		numBars = 8;
		barHeight = 12;
		rectangleWidth = 40;
		rectangleHeight = 15;
		horizontalGap = 7;
		verticalGap = 3;
		setWidth(rectangleWidth*numBars+horizontalGap*(numBars+1)+HORIZONTAL_PADDING);
		setHeight(rectangleHeight*barHeight+verticalGap*(barHeight+1)+VERTICAL_PADDING);
		startColor = Color.GOLD;
		endColor = Color.BLUE;
		backgroundColor = Color.BLACK;
		setColorVals();
	}
	
	
	//Defined methods that  gets and stores values into the declared variables above
	public int getNumBars() {
		return numBars;
	}
	public int getBarHeight() {
		return barHeight;
	}
	public int getRectangleWidth() {
		return rectangleWidth;
	}
	public int getRectangleHeight() {
		return rectangleHeight;
	}
	public int getHorizontalGap() {
		return horizontalGap;
	}
	public int getVerticalGap() {
		return verticalGap;
	}
	public Color getStartColor() {
		return startColor;
	}
	public Color getEndColor() {
		return endColor;
	}
	
	
	//Defined methods that allows for input to be stored into the written variables
	public void setNumBars(int numBars) {
		 this.numBars = numBars;
	}
	public void setBarHeight(int barHeight) {
		this.barHeight = barHeight;
	}
	public void setRectangleWidth(int rectangleWidth) {
		this.rectangleWidth = rectangleWidth;
	}
	public void setRectangleHeight(int rectangleHeight) {
		this.rectangleHeight = rectangleHeight;
	}
	public void setHorizontalGap(int horizontalGap) {
		this.horizontalGap = horizontalGap;
	}
	public void setVerticalGap(int verticalGap) {
		this.verticalGap = verticalGap;
	}
	public void setStartColor(Color startColor) {
		this.startColor = startColor;
	}
	public void setEndColor(Color endColor) {
		this.endColor = endColor;
	}
	
	//This is a method that colors the spectrum bar when the song is being played
	private void setColorVals() {
		colorVals = new Color[barHeight];
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
	}
	
	
	//This method tests to see if the spectrum bars will be drawn
	public void DrawTest() {
		int[] heights;
		heights = new int[numBars];
		for(int i = 0; i < numBars; ++i) {
			heights[i] = i+4;
		}
		DrawSpectrumBars(heights);
	}
	
	
	//This method actually draws the spectrum bars
	public void DrawSpectrumBars(int[] heights) {
		GraphicsContext context = getGraphicsContext2D();
		setWidth(rectangleWidth*numBars+horizontalGap*(numBars+1)+HORIZONTAL_PADDING);
		setHeight(rectangleHeight*barHeight+verticalGap*(barHeight+1)+VERTICAL_PADDING);
		setColorVals();
		context.setFill(backgroundColor);
		context.fillRect(0, 0, getWidth(), getHeight());
		for(int bar_index = 0; bar_index < numBars; ++bar_index) {
			if(heights[bar_index] >= barHeight)
				heights[bar_index] = barHeight-1;
			int x_val = rectangleWidth*bar_index+horizontalGap*(bar_index+1)+HORIZONTAL_PADDING/2;
			for(int rectangle_index = 1; rectangle_index <= heights[bar_index]; ++rectangle_index) {
				int y_val = (int) getHeight() - (rectangleHeight*rectangle_index+verticalGap*(rectangle_index+1)+VERTICAL_PADDING/2);
				context.setFill(colorVals[rectangle_index]);
				context.fillRect(x_val, y_val, rectangleWidth, rectangleHeight);
			}
		}
	}
	
}
