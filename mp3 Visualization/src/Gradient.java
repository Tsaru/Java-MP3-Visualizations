import java.util.Random;
import javafx.scene.paint.Color;

/*
 * Author: Eden Doonan
 * E-Mail: doonanem@clarkson.edu
 * Date: 11/18/2014
 */
/**
 * This class contains a set of static functions for building gradient color
 * arrays. It provides two main functionalities:
 * 
 * randomColor functions generate one or more random colors
 * 
 * buildGradient functions return a Color[] variable holding a gradient.
 * 
 * @author Eden Doonan
 *
 */

public class Gradient {
	
	/**
	 * The most basic randomColor function.
	 * @return Returns a random color.
	 */
	public static Color randomColor() {
		Random randGen = new Random(System.currentTimeMillis());
		return new Color((double) Math.abs(randGen.nextInt()%256)/255.0,
						 (double) Math.abs(randGen.nextInt()%256)/255.0,
						 (double) Math.abs(randGen.nextInt()%256)/255.0, 1.0);
	}
	
	/**
	 * Generates a color that is at least a given offset from every color in an array of colors.
	 * 
	 * @param currentColors The array the random color will be compared to.
	 * @param minTotalOffset The minimum offset from every color in the array.
	 * @return a random Color value.
	 */
	public static Color randomColor(Color[] currentColors, int minTotalOffset) {
		int maxOffset = minTotalOffset + 1;
		int r = 0, g = 0, b = 0;
		Random randGen = new Random(System.currentTimeMillis());
		while(maxOffset < minTotalOffset) {
			maxOffset = 0;
			r = Math.abs(randGen.nextInt()%256);
			g = Math.abs(randGen.nextInt()%256);
			b = Math.abs(randGen.nextInt()%256);
			for(int i = 0; i < currentColors.length; ++i) {
				int thisOffset = Math.abs((int) ( (double) currentColors[i].getRed()*255.0) - r) +
		  		  		  		 Math.abs((int) ( (double) currentColors[i].getGreen()*255.0) - g) +
		  		  		  		 Math.abs((int) ( (double) currentColors[i].getBlue()*255.0) - b);
				if(thisOffset > maxOffset)
					maxOffset = thisOffset;
			}
		}
		return new Color((double)r/255.0, (double)g/255.0, (double)b/255.0, 1.0);
	}
	
	/**
	 * Generates a color that is at least a given offset from a given color.
	 * 
	 * @param currentColor The color the random color will be compared to.
	 * @param minTotalOffset The minimum offset the random color must have from the given color.
	 * @return A random Color value.
	 */
	public static Color randomColor(Color currentColor, int minTotalOffset) {
		int offset = 0;
		int r = 0, g = 0, b = 0;
		Random randGen = new Random(System.currentTimeMillis());
		while(offset < minTotalOffset) {
			r = Math.abs(randGen.nextInt()%256);
			g = Math.abs(randGen.nextInt()%256);
			b = Math.abs(randGen.nextInt()%256);
			offset = Math.abs((int) ( (double) currentColor.getRed()*255.0) - r) +
		  		  	 Math.abs((int) ( (double) currentColor.getGreen()*255.0) - g) +
		  		  	 Math.abs((int) ( (double) currentColor.getBlue()*255.0) - b);
		}
		return new Color((double)r/255.0, (double)g/255.0, (double)b/255.0, 1.0);
		
	}
	
	/**
	 * Generates a Color[] variable of a given length and fills it with random Color values;
	 * 
	 * @param numColors The length of the Color[] variable;
	 * @return A Color[] variable of length numColors filled with random Color values;
	 */
	public static Color[] randomColors(int numColors) {
		Color[] colors = new Color[numColors];
		for(int i = 0; i < numColors; ++i) {
			colors[i] = randomColor();
		}
		return colors;
	}
	
	/**
	 * Generates a Color[] variable of a given length and fills it with random Color values,
	 * each of which is at least a minimum offset from the previous value.
	 * 
	 * @param numColors The length of the Color[] variable;
	 * @param minTotalOffset The offset from the previous Color value each new color value must have.
	 * @return  A Color[] variable of length numColors filled with random Color values;
	 */
	public static Color[] randomColors(int numColors, int minTotalOffset) {
		Color[] colors = new Color[numColors];
		colors[0] = randomColor();
		for(int i = 1; i < numColors; ++i) {
			colors[i] = randomColor(colors[i-1], minTotalOffset);
		}
		return colors;
	}
	
	/**
	 * Adds random colors to a given array of colors.
	 * 
	 * @param currentColors The array of colors to be added to.
	 * @param numColors The number of colors to add to the array.
	 * @return A Color[] variable that is filled with the given Color values followed by random Color values;
	 */
	public static Color[] addRandomColors(Color[] currentColors, int numColors) {
		Color[] colors = new Color[currentColors.length+numColors];
		for(int i = 0; i < currentColors.length; ++i) {
			colors[i] = currentColors[i];
		}
		for(int i = currentColors.length; i < colors.length; ++i) {
			colors[i] = randomColor();
		}
		return colors;
	}
	
	/**
	 * Adds random colors to a given array of colors, with each new color being a minimum offset from the previous color.
	 * 
	 * @param currentColors The array of colors to be added to.
	 * @param numColors The number of colors to add to the array.
	 * @param minTotalOffset The minimum offset each new Color must have from the previous Color.
	 * @return A Color[] variable that is filled with the given Color values followed by random Color values;
	 */
	public static Color[] addRandomColors(Color[] currentColors, int numColors, int minTotalOffset) {
		Color[] colors = new Color[currentColors.length+numColors];
		for(int i = 0; i < currentColors.length; ++i) {
			colors[i] = currentColors[i];
		}
		for(int i = currentColors.length; i < colors.length; ++i) {
			colors[i] = randomColor(colors[i-1], minTotalOffset);
		}
		return colors;
	}
	
	/**
	 * Builds a gradient given a Color[] value and the number of steps to be taken between each pair of Colors.
	 * 
	 * @param colors The colors between which a gradient will be generated. Must be at least 2 Colors long.
	 * @param steps The number of steps between each pair of Color values. It's length must be one less than that of colors.
	 * @return A Color[] value with gradients between each given Color value.
	 */
	public static Color[] buildGradient(Color[] colors, int[] steps) {
		if(colors.length != steps.length+1)
			return colors;
		if(colors.length < 2)
			return colors;
		int totalSteps = 1;
		for(int i = 0; i < steps.length; ++i)
			totalSteps += steps[i];
		Color[] gradient = new Color[totalSteps];
		
		int colorStepIndex = 0;
		int stepsTaken = 0;
		double currentR = colors[0].getRed();
		double currentG = colors[0].getGreen();
		double currentB = colors[0].getBlue();
		double rDifference = colors[1].getRed()-currentR;
		double gDifference = colors[1].getGreen()-currentG;
		double bDifference = colors[1].getBlue()-currentB;
		for(int i = 0; i < totalSteps; ++i) {
			gradient[i] = new Color(currentR+rDifference/(double)steps[colorStepIndex]*(i-stepsTaken),
		 				 			currentG+gDifference/(double)steps[colorStepIndex]*(i-stepsTaken),
		 				 			currentB+bDifference/(double)steps[colorStepIndex]*(i-stepsTaken),
		 				 			1.0);

			if(i-stepsTaken == steps[colorStepIndex] && colorStepIndex < colors.length-2) {
				System.out.println(colorStepIndex);
				stepsTaken += steps[colorStepIndex];
				colorStepIndex++;
				currentR = colors[colorStepIndex].getRed();
				currentG = colors[colorStepIndex].getGreen();
				currentB = colors[colorStepIndex].getBlue();
				rDifference = colors[colorStepIndex+1].getRed()-currentR;
				gDifference = colors[colorStepIndex+1].getGreen()-currentG;
				bDifference = colors[colorStepIndex+1].getBlue()-currentB;
			}	
		}
		return gradient;
	}
	
	/**
	 * Builds a gradient given a Color[] value and the maximum each step can move for any given channel.
	 * 
	 * @param colors The colors between which a gradient will be generated. Must be at least 2 Colors long.
	 * @param maxShift The maximum change of any given channel for each step in the gradient.
	 * @return A Color[] value with gradients between each given Color value.
	 */
	public static Color[] buildGradient(Color[] colors, double maxShift) {
		
		if(colors.length < 2) {
			return colors;
		}
		int[] steps = new int[colors.length-1];
		for(int i = 0; i < steps.length; ++i) {
			double maxDifference = Math.abs(colors[i].getRed()-colors[i+1].getRed());
			if(maxDifference < Math.abs(colors[i].getGreen()-colors[i+1].getGreen()))
				maxDifference = Math.abs(colors[i].getGreen()-colors[i+1].getGreen());
			if(maxDifference < Math.abs(colors[i].getBlue()-colors[i+1].getBlue()))
				maxDifference = Math.abs(colors[i].getBlue()-colors[i+1].getBlue());
			steps[i] = (int)(((maxDifference*255.0)/(double)maxShift)+.5);
		}	
		return buildGradient(colors, steps);
	}
	
	public static Color[] buildGradient(Color color1, Color color2, double maxShift) {
		Color[] colors = new Color[2];
		colors[0] = color1;
		colors[1] = color2;
		return buildGradient(colors, maxShift);
	}
	
	public static Color[] buildGradient(Color color1, Color color2, int steps) {
		Color[] colors = new Color[2];
		colors[0] = color1;
		colors[1] = color2;
		int[] stepArray = new int[1];
		stepArray[0] = steps;
		return buildGradient(colors, stepArray);
	}
	
	/**
	 * Builds a random gradient of a given length with a given maximum shift for any color channel.
	 * 
	 * @param numColors The number of colors in the gradient.
	 * @param maxShift The maximum change of any given channel for each step in the gradient.
	 * @return A Color[] value with gradients between random Color values.
	 */
	public static Color[] buildRandomGradient(int numColors, double maxShift) {
		return buildGradient(randomColors(numColors), maxShift);
	}
	
	/**
	 * Builds a gradient given a Color[] value and the number of steps to be taken between each pair of Colors.
	 * 
	 * @param numColors The number of colors in the gradient.
	 * @param steps The number of steps between each pair of Color values. It's length must be one less than that of colors.
	 * @return A Color[] value with gradients between random Color values.
	 */
	public static Color[] buildRandomGradient(int numColors, int[] steps) {
		return buildGradient(randomColors(numColors), steps);
	}
	
	/**
	 * Builds a random gradient of a given length where each original color
	 * will be at least a given offset from the previous color, and with a
	 * given maximum shift for any color channel during the gradient creation.
	 * 
	 * @param numColors The number of colors in the gradient.
	 * @param minShift The minimum shift between each color from which the gradient will be generated.
	 * @param maxShift The maximum change of any given channel for each step in the gradient.
	 * @return A Color[] value with gradients between random Color values.
	 */
	public static Color[] buildRandomGradient(int numColors, int minShift, double maxShift) {
		return buildGradient(randomColors(numColors, minShift), maxShift);
	}

	/**
	 * Builds a random gradient of a given length where each original color
	 * will be at least a given offset from the previous color, and with a
	 * given number of steps to be taken between each pair of Colors.
	 * 
	 * @param numColors The number of colors in the gradient.
	 * @param minShift The minimum shift between each color from which the gradient will be generated.
	 * @param steps The number of steps between each pair of Color values. It's length must be one less than that of colors.
	 * @return A Color[] value with gradients between random Color values.
	 */
	public static Color[] buildRandomGradient(int numColors, int minShift, int[] steps) {
		return buildGradient(randomColors(numColors, minShift), steps);
	}
	
	/**
	 * Builds a two color gradient with a given start color and a given
	 * maximum shift for any color channel during the gradient creation.
	 * 
	 * @param startColor The color used to start the gradient.
	 * @param maxShift The maximum change of any given channel for each step in the gradient.
	 * @return A Color[] value with a gradient between a given Color value and a random color value.
	 */
	public static Color[] buildRandomGradient(Color startColor, double maxShift) {
		Color[] colors = new Color[2];
		colors[0] = startColor;
		colors[1] = randomColor();
		return buildGradient(colors, maxShift);
	}
	
	/**
	 * Builds a two color gradient with a given start color and a given
	 * number of steps to be taken between each pair of Colors.
	 * 
	 * @param startColor The color used to start the gradient.
	 * @param steps The number of steps between each pair of Color values. It's length must be one less than that of colors.
	 * @return A Color[] value with a gradient between a given Color value and a random color value.
	 */
	public static Color[] buildRandomGradient(Color startColor, int[] steps) {
		Color[] colors = new Color[2];
		colors[0] = startColor;
		colors[1] = randomColor();
		return buildGradient(colors, steps);
	}
	
	/**
	 * Builds a two color gradient with a given start color where the second
	 * color is randomly chosen but at least a given offset from the given
	 * Color. The gradient is generated with a given maximum shift for any
	 * color channel.
	 * 
	 * @param startColor The color used to start the gradient.
	 * @param minShift The minimum shift between each color from which the gradient will be generated.
	 * @param maxShift The maximum change of any given channel for each step in the gradient.
	 * @return A Color[] value with a gradient between a given Color value and a random color value.
	 */
	public static Color[] buildRandomGradient(Color startColor, int minShift, double maxShift) {
		Color[] colors = new Color[2];
		colors[0] = startColor;
		colors[1] = randomColor(startColor, minShift);
		return buildGradient(colors, maxShift);
	}

	/**
	 * Builds a two color gradient with a given start color where the second
	 * color is randomly chosen but at least a given offset from the given
	 * Color. The gradient is generated with a given number of steps to be
	 * taken between each pair of Colors.
	 * 
	 * @param startColor The color used to start the gradient.
	 * @param minShift The minimum shift between each color from which the gradient will be generated.
	 * @param steps The number of steps between each pair of Color values. It's length must be one less than that of colors.
	 * @return A Color[] value with a gradient between a given Color value and a random color value.
	 */
	public static Color[] buildRandomGradient(Color startColor, int minShift, int[] steps) {
		Color[] colors = new Color[2];
		colors[0] = startColor;
		colors[1] = randomColor(startColor, minShift);
		return buildGradient(colors, steps);
	}

	/**
	 * Builds a gradient with a given start color followed by a given
	 * number of random colors where each subsequent color is randomly
	 * chosen but at least a given offset from the previous color. The
	 * gradient is generated with a given maximum shift for any color
	 * channel.
	 * 
	 * @param startColor The color used to start the gradient.
	 * @param numColors The number of random colors that should follow the given start color.
	 * @param minShift The minimum shift between each color from which the gradient will be generated.
	 * @param maxShift The maximum change of any given channel for each step in the gradient.
	 * @return A Color[] value with a gradient between a given Color value and a random color value.
	 */
	public static Color[] buildRandomGradient(Color startColor, int numColors, int minShift, double maxShift) {
		Color[] colors = new Color[1];
		colors[0] = startColor;
		return buildGradient(addRandomColors(colors, numColors, minShift), maxShift);
	}

	/**
	 * Builds a gradient with a given start color followed by a given
	 * number of random colors where each subsequent color is randomly
	 * chosen but at least a given offset from the previous color.The
	 * gradient is generated with a given number of steps to be taken
	 * between each pair of Colors.
	 * 
	 * @param startColor The color used to start the gradient.
	 * @param numColors The number of random colors that should follow the given start color.
	 * @param minShift The minimum shift between each color from which the gradient will be generated.
	 * @param steps The number of steps between each pair of Color values. It's length must be one less than that of colors.
	 * @return A Color[] value with a gradient between a given Color value and a random color value.
	 */
	public static Color[] buildRandomGradient(Color startColor, int numColors, int minShift, int[] steps) {
		Color[] colors = new Color[1];
		colors[0] = startColor;
		return buildGradient(addRandomColors(colors, numColors, minShift), steps);
	}
}
