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
	private final boolean smooth = true;
	private final boolean allowneg = false;
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

	@Override
	public Node getNode() {
		return display;
	}
	
}
