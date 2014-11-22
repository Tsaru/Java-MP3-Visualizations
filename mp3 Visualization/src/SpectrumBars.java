import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class SpectrumBars extends Visualization {
	private final double COLOR_SHIFT_SPEED = 3;
	private final int VERTICAL_PADDING = 6;
	private final int HORIZONTAL_PADDING = 8;
	private final double COLOR_UPDATE_FREQUENCY = .1;
	
	private int numBars, barHeight, rectangleWidth, rectangleHeight, horizontalGap, verticalGap, colorShiftIndex;
	private Color bottomColor, topColor, backgroundColor;
	private Color[] colorShiftVals;
	private boolean isbottomColorShifting;
	private double timeSinceColorUpdate;
	Canvas display;
	FrequencyCompressor compressor;

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
		compressor = new FrequencyCompressor(volMax);
		isbottomColorShifting = false;
		maxVolume = volMax;
		numBars = bars;
		barHeight = height;
		rectangleWidth = rectWidth;
		rectangleHeight = rectHeight;
		horizontalGap = hGap;
		verticalGap = vGap;
		display.setWidth(rectangleWidth*numBars+horizontalGap*(numBars+1)+HORIZONTAL_PADDING);
		display.setHeight(rectangleHeight*barHeight+verticalGap*(barHeight+1)+VERTICAL_PADDING);
		bottomColor = Color.GOLD;
		topColor = Color.BLUE;
		backgroundColor = Color.BLACK;
		timeSinceColorUpdate = 0;
		colorShiftIndex = 0;
		colorShiftVals = Gradient.buildRandomGradient(topColor, 210, COLOR_SHIFT_SPEED);
		
	}
	
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
	
	private int[] processHeights(float[] magnitudes, double timestamp) {
		int[] heights = new int[numBars];
		float[] buckets = compressor.compressHeights(magnitudes, numBars, timestamp);
		for(int i = 0; i < numBars; ++i) {
			heights[i] = (int) ( (float) (buckets[i]* (float) barHeight));
		}
		return heights;
	}
	
	public void Update(double timestamp, double duration, float[] magnitudes, float[] phases) {
		GraphicsContext context = display.getGraphicsContext2D();
		display.setWidth(rectangleWidth*numBars+horizontalGap*(numBars+1)+HORIZONTAL_PADDING);
		display.setHeight(rectangleHeight*barHeight+verticalGap*(barHeight+1)+VERTICAL_PADDING);
		timeSinceColorUpdate += duration;
		if(timeSinceColorUpdate >= COLOR_UPDATE_FREQUENCY) {
			timeSinceColorUpdate = 0;
			incrementColors();
		}
		Color[] colorVals = Gradient.buildGradient(bottomColor, topColor, barHeight);;
		int[] heights = processHeights(magnitudes, timestamp);
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
