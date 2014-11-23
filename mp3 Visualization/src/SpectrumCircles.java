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
		private final boolean smooth = true;
		private final int NUMPOINTS = 120;
		private final int RADIUS = 50;
		private final int CLICK_ADJUSTMENT = 25;
		private ArrayList<Point> centers;
		Canvas display;
		FrequencyCompressor compressor;

		SpectrumCircles(){
    		System.out.println("New Spectrum Circles");
        	maxVolume = 100;
        	maxheight = 100;
        	display = new Canvas();
    		compressor = new FrequencyCompressor(maxVolume);
    		isbottomColorShifting = false;
        	width = 500;
        	height = 500;
        	centers = new ArrayList<Point>();
        	display.setHeight(height);
        	display.setWidth(width);
    		topColor = Color.GOLD;
    		bottomColor = Color.BLUE;
    		backgroundColor = Color.BLACK;
    		colorShiftVals = Gradient.buildRandomGradient(topColor, 210, COLOR_SHIFT_SPEED);
        }
        
        public void addCircle(int x, int y, Color col){
        	Point p = new Point(x-RADIUS-CLICK_ADJUSTMENT,y-RADIUS-CLICK_ADJUSTMENT);
        	centers.add(p);
        }
        
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
    		int[] heights = new int[NUMPOINTS];
    		for(int i=0;i<NUMPOINTS;i++){
    			heights[i] = (int) ((float) buckets[i]*maxheight);
    		}
    		return heights;
    	}
        
        public void Update(double timestamp, double duration, float[] magnitudes, float[] phases){
    		final GraphicsContext context = display.getGraphicsContext2D();
    		display.setWidth(width);
    		display.setHeight(height);
    		context.setFill(backgroundColor);
    		context.fillRect(0, 0, display.getWidth(), display.getHeight());
    		display.addEventHandler(MouseEvent.MOUSE_DRAGGED, 
    			       new EventHandler<MouseEvent>() {
    			           @Override
    			           public void handle(MouseEvent e) {
    			        	   int index = insideCircle(e.getX(), e.getY());
    			        	   if(index >= 0){
    			        		   centers.remove(index);
    			        	   }
    			        	   addCircle((int)e.getX(),(int)e.getY(),Color.AQUA);
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
 			        	   addCircle((int)e.getX(),(int)e.getY(),Color.AQUA);
 			           }
 			       });
        	int size = centers.size();
        	timeSinceColorUpdate += duration;
    		if(timeSinceColorUpdate >= COLOR_UPDATE_FREQUENCY) {
    			timeSinceColorUpdate = 0;
    			incrementColors();
    		}
    		Color[] colorVals = Gradient.buildGradient(bottomColor, topColor, height);
    		int[] heights = processHeights(magnitudes, timestamp);
        	for(int i = 0; i < size; i++){
        		int xcent = ((int) centers.get(i).getX());
        		int ycent = ((int) centers.get(i).getY());
        		double[] ypos = new double[NUMPOINTS];
        		double[] xpos = new double[NUMPOINTS];
        		if(smooth){
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
        		}else{
		    		for(int j=0;j<NUMPOINTS;j++) {
						ypos[j] = ycent+(heights[j]*Math.sin((360/NUMPOINTS)*j)+RADIUS+CLICK_ADJUSTMENT);
						xpos[j] = xcent+(heights[j]*Math.cos((360/NUMPOINTS)*j)+RADIUS+CLICK_ADJUSTMENT);
						if(j == 0){
						}else{
							context.setStroke(colorVals[i]);
							context.strokeLine(xpos[j-1],ypos[j-1],xpos[j],ypos[i]);
						}
					}
	    			context.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.1, true, CycleMethod.REPEAT,new Stop(0.0, topColor),new Stop(1.0, bottomColor)));
	    			context.fillPolygon(xpos,ypos,NUMPOINTS);
        		}
        		context.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.1, true, CycleMethod.REPEAT,new Stop(0.0, topColor),new Stop(1.0, bottomColor)));
        		context.fillOval((xcent + 50), (ycent + 50), RADIUS, RADIUS);
        	}
        }
        
    	@Override
    	public Node getNode() {
    		return display;
    	}
}
