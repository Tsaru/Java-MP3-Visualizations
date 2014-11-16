package application;

import java.util.Random;

import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.paint.Color;

public class SpectrumListener implements AudioSpectrumListener {

	//Declares the class SpectrumBars as the variable myDisplay
	SpectrumBars myDisplay;
	
	//Declaration of the variables and arrays of the class SpectrumListener
	private int freqMax;
	Color[] colorVals;
	int colorIndex;
	boolean colorForStart = true;
	
	//Class constructor that calls the SpectrumBars constructor and also sets other variabals
	SpectrumListener(int max) {
		myDisplay = new SpectrumBars();
		freqMax = max;
		generateColorVals();
	}
	
	//Method that returns the value of myDisplay
	public SpectrumBars getDisplay() {
		return myDisplay;
	}
	
	//Method that compares the difference between the RGB values and produces a random color based off of the results
	private Color randomColor(Color currentColor) {
		int totalDifference = 0;
		int r = 0, g = 0, b = 0;
		Random randGen = new Random(System.currentTimeMillis());
		while(totalDifference < 80) {
			r = Math.abs(randGen.nextInt()%256);
			g = Math.abs(randGen.nextInt()%256);
			b = Math.abs(randGen.nextInt()%256);
			totalDifference = Math.abs((int) ( (double) currentColor.getRed()*255.0) - r) +
					  		  Math.abs((int) ( (double) currentColor.getGreen()*255.0) - g) +
					  		  Math.abs((int) ( (double) currentColor.getBlue()*255.0) - b);
		}
		return new Color((double)r/255.0, (double)g/255.0, (double)b/255.0, 1.0);
	}
	
	
	//This method generates color values
	private void generateColorVals() {
		if(colorForStart)
			colorForStart = false;
		else
			colorForStart = true;
		colorIndex = 0;
		Color startColor, endColor;
		if(colorForStart)
			startColor = myDisplay.getStartColor();
		else
			startColor = myDisplay.getEndColor();
		endColor = randomColor(startColor);
		double rStart = startColor.getRed();
		double gStart = startColor.getGreen();
		double bStart = startColor.getBlue();
		double rDifference = endColor.getRed()-rStart;
		double gDifference = endColor.getGreen()-gStart;
		double bDifference = endColor.getBlue()-bStart;
		
		double maxDifference = Math.abs(rDifference);
		if(maxDifference < Math.abs(gDifference))
			maxDifference = Math.abs(gDifference);
		if(maxDifference < Math.abs(bDifference))
			maxDifference = Math.abs(bDifference);
		
		int numSteps = (int)(((maxDifference*255.0)/5.0)+.5);
		colorVals = new Color[numSteps];
		
		for(int i = 0; i < numSteps; ++i) {
			colorVals[i] = new Color(rStart+rDifference/(double)numSteps*i,
					 				 gStart+gDifference/(double)numSteps*i,
					 				 bStart+bDifference/(double)numSteps*i,
									 1.0);
		}
	}
	
	
	//Method that updates the spectrum data as the song file is being played
	@Override
	public void spectrumDataUpdate( double timestamp, double duration,
									float[] magnitudes, float[] phases) {
		System.out.println(colorIndex);
		if(colorForStart)
			myDisplay.setStartColor(colorVals[colorIndex]);
		else
			myDisplay.setEndColor(colorVals[colorIndex]);
		colorIndex += 1;
		if(colorIndex == colorVals.length)
			generateColorVals();
			
		float sum = 0;
		double current_freq = 250;
		int num_bars = 8;
		double band_width = (22050.0-250.0)/((double) magnitudes.length);
		int bar_count = 0;
		int[] heights = new int[num_bars];
		/*for(int i = 0; i < magnitudes.length; ++i) {
			current_freq += band_width;
			if(Math.log10(current_freq-250)/.62 - bar_count > 1) {
				System.out.println(timestamp);
				System.out.println(sum);
				System.out.println(Math.abs( (int) (((double) magnitudes.length/(double) num_bars)*60.0/sum)));
				System.out.println(" ");
				heights[bar_count] = Math.abs( (int) (((double) magnitudes.length/(double) num_bars)*60.0/sum))*myDisplay.getBarHeight();
				bar_count += 1;
				sum = magnitudes[i];
			}
			else {
				sum += magnitudes[i];
			}
		}
		if(bar_count < num_bars-1)
			heights[num_bars-1] = Math.abs( (int) (((double) magnitudes.length/(double) num_bars)*60.0/sum))*myDisplay.getBarHeight();
		*/
		System.out.println(timestamp);
		System.out.println(magnitudes.length);
		double max = ((((double) magnitudes.length/(double) num_bars)*freqMax)-600);
		int maxHeight = myDisplay.getBarHeight();
		for(int i = 0; i < magnitudes.length; ++i) {
			if(i+1 >= (bar_count+1)*(magnitudes.length/num_bars)) {
				double val = (double) (num_bars-bar_count-1)/num_bars*.8;
				sum = -1*sum-600;
				sum += (int)(max-sum)*val;
				//sum += val;
				//X^2/(1600*1600)+Y^2/(num_bars)=1;
				//Y = sqrt(num_bars*num_bars-(num_bars*num_bars)*X^2/1600^2)
				heights[bar_count] = (int) ( (double) Math.pow(sum/(max), 6)*maxHeight);
				//heights[bar_count] = (int) Math.sqrt(num_bars*num_bars-num_bars*num_bars*(sum-max)*(sum-max)/(max*max))
				//heights[bar_count] = (int) Math.sqrt(((double) heightSqrd ) - ((double) heightSqrd )*(sum)*(sum)/(max*max));
				System.out.format("bar %d is %f / %f evaluates to %d.", bar_count+1, sum, max, heights[bar_count]);
				//System.out.println(i+1);
				//System.out.println(sum);
				//System.out.println(heights[bar_count]);
				//System.out.println(sum/max);
				System.out.println(" "); 
				//heights[bar_count] = 1600-1*((int) ( (sum/(max))*((double) myDisplay.getBarHeight())));
				bar_count+=1;
				sum = magnitudes[i];
			}
			else sum += magnitudes[i];
		}
		System.out.print("BAR COUNT: ");
		System.out.println(bar_count);
		if(bar_count != num_bars)
			heights[bar_count] = -1*((int) ( (sum/(max-(num_bars-bar_count)*20+300))*((double) myDisplay.getBarHeight())));
		myDisplay.DrawSpectrumBars(heights);
	}

}
