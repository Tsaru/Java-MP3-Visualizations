/*
 * Author: Eden Doonan
 * E-Mail: doonanem@clarkson.edu
 * Date: 11/18/2014
 */

/**
 * 
 * This class compresses an array of magnitudes corresponding to frequency bands
 * into an array of values ranging from 0 to 1, based on the actual magnitude
 * divided by the maximum magnitude.
 * 
 * This class tracks minimums and maximums of a set of magnitudes for a
 * particular song. If you call the static functions, this functionality is
 * disabled.
 * 
 * The given array of magnitudes is compressed logarithmically. By default,
 * with 128 bands in the magnitudes array, there are 2 frequency bands in the
 * first compression, 3 in the second, 5 in the third, etc.
 * 
 * @author Eden Doonan
 */
public class FrequencyCompressor {
	// The array of minium and maximum values for an array of magnitudes
	private float[] minimums, maximums;
	
	// The maximum value of a particular magnitude and
	// The denominator which determines the distribution of frequency bars per bucket
	private int maxVolume, logDenominator;

	/**
	 * Sets the internal maximum volume and defaults the log denominator to 3.
	 * 
	 * @param maxVolume
	 * 			maxVolume: the value to which the internal maximum volume will be set.
	 */
	FrequencyCompressor(int maxVolume) {
		this(maxVolume, 3);
	}
	
	/**
	 * Sets the internal maximum volume and the log denominator.
	 * 
	 * @param maxVolume the value to which the internal maximum volume will be set.
	 * @param logDenominator the value to be used in determining the distribution of bands of the compressed array.
	 */
	FrequencyCompressor(int maxVolume, int logDenominator) {
		this.maxVolume = maxVolume;
		this.logDenominator = logDenominator;
	}
	
	/**
	 * Sets the maximum volume.
	 * 
	 * @param maxVolume
	 * 			maxVolume: the value to which the maximum volume will be set.
	 */
	public void setMaxVolume(int maxVolume) {
		this.maxVolume = maxVolume;
	}
	
	/**
	 * Sets the log denominator value. It is used in the formula:
	 * (size of compressed array) / (ln((length of the magnitudes array/logDenominator) + 1)) * (ln((bands processed)/logDenominator + 1))
	 * where each integer passed marks the end of a compressed chunk of bands.
	 * 
	 * @param logDenominator the value to which the log denominator will be set.
	 */
	public void setLogDenominator(int logDenominator) {
		this.logDenominator = logDenominator;
	}
	
	/**
	 * Gives you the value of maxVolume.
	 * @return the integer maxVolume.
	 */
	public int getMaxVolume() {
		return maxVolume;
	}

	/**
	 * Gives you the value of logDenominator.
	 * @return the integer logDenominator.
	 */
	public int getLogDenominator() {
		return logDenominator;
	}
	
	/**
	 * Compresses the given array magnitudes to an array of percentages which is numBuckets in length.
	 * 
	 * @param magnitudes the array to be compressed.
	 * @param numBuckets the length of the compressed array.
	 * @param maxVolume the value used to determine the percentages. It uses the formula:
	 * 					particular magnitude / maxVolume
	 * @param logDenominator the value used to determine the distribution of bands in the compressed array.
	 * @return a float[] containing the percentages of the maxVolume attained in each chunk of bands.
	 */
	
	public static float[] compressHeights(float[] magnitudes, int numBuckets, int maxVolume, int logDenominator) {
		double a = numBuckets/Math.log((double) magnitudes.length/(double) logDenominator + 1);
		double sum = 0;
		int bands_this_bucket = 0;
		int buckets_processed = 0;
		int bands_processed = 0;
		float[] buckets = new float[numBuckets];
		for(int i = 0; i < magnitudes.length; ++i) {
			sum += ( (double) magnitudes[bands_processed]/(double) maxVolume);
			bands_this_bucket++;
			bands_processed++;
			if(a*Math.log((double) bands_processed/(double) logDenominator + 1)-buckets_processed >= 1) {
				buckets[buckets_processed] = (float) sum /(float) bands_this_bucket;
				bands_this_bucket = 0;
				sum = 0;
				buckets_processed++;
			}
		}
		if(buckets_processed != numBuckets)
			buckets[buckets_processed] = (float) sum /(float) bands_this_bucket;
		return buckets;
		
	}
	
	/**
	 * Compresses the given array magnitudes to an array of percentages which is numBuckets in length.
	 * It uses a default log denominator to determine the distribution of bands in the compressed array.
	 * 
	 * @param magnitudes the array to be compressed.
	 * @param numBuckets the length of the compressed array.
	 * @param maxVolume the value used to determine the percentages. It uses the formula:
	 * 						particular magnitude / maxVolume
	 * @return a float[] containing the percentages of the maxVolume attained in each chunk of bands.
	 */
	
	public static float[] compressHeights(float[] magnitudes, int numBuckets, int maxVolume) {
		return compressHeights(magnitudes, numBuckets, maxVolume, 3);
	}
	
	/**
	 * Compresses the given array of magnitudes to an array of percentages which is numBuckets in length.
	 * A maximum volume and logarithm denominator need to be set for this function to work.
	 * This function uses an adjusting system of minimums and maximums. Without it, certain bands will
	 * almost always be near 0.
	 * 
	 * @param magnitudes the array to be compressed.
	 * @param numBuckets the length of the compressed array.
	 * @param timestamp the timestamp given to spectrumDataUpdate which corresponds to the given array of magnitudes.
	 * @return a float[] containing the percentages of the maxVolume attained in each chunk of bands.
	 */
	public float[] compressHeights(float[] magnitudes, int numBuckets, double timestamp) {
		if(maxVolume < 1) {
			return compressHeights(magnitudes, numBuckets, 100, 3);
		}
		if(timestamp < .2) {
			return compressHeights(magnitudes, numBuckets, logDenominator);
		}
		updateMinMax(magnitudes);
		double a = numBuckets/Math.log((double) magnitudes.length/(double) logDenominator + 1);
		double sum = 0;
		int bands_this_bucket = 0;
		int buckets_processed = 0;
		int bands_processed = 0;
		float[] buckets = new float[numBuckets];
		for(int i = 0; i < magnitudes.length; ++i) {
			sum += ((double) (-1*magnitudes[bands_processed] - minimums[bands_processed]))/
				   ((double) (maximums[bands_processed] - minimums[bands_processed]));
			bands_this_bucket++;
			bands_processed++;
			if(a*Math.log((double) bands_processed/(double) logDenominator + 1)-buckets_processed >= 1) {
				buckets[buckets_processed] = (float) sum /(float) bands_this_bucket;
				bands_this_bucket = 0;
				sum = 0;
				buckets_processed++;
			}
		}
		if(buckets_processed != numBuckets)
			buckets[buckets_processed] = (float) sum /(float) bands_this_bucket;
		return buckets;
	}
	
	// creates and initializes the arrays float[] minimums and float[] maximums.
	private void initializeMinMax(float[] magnitudes) {
		minimums = new float[magnitudes.length];
		maximums = new float[magnitudes.length];
		for(int i = 0; i < magnitudes.length; ++i) {
			minimums[i] = -1*magnitudes[i];
			maximums[i] = -1*magnitudes[i];
		}
	}
	
	// updates the minimums and maximums arrays based on the provided array of magnitudes.
	private void updateMinMax(float[] magnitudes) {
		if(minimums == null || maximums == null)
			initializeMinMax(magnitudes);
		else if(minimums.length != magnitudes.length || maximums.length != magnitudes.length)
			initializeMinMax(magnitudes);
		else
			for(int i = 0; i < magnitudes.length; ++i) {
				if(-1*magnitudes[i] < minimums[i]) {
					minimums[i] = -1*magnitudes[i];
				}
				if(-1*magnitudes[i] > maximums[i]) {
					maximums[i] = -1*magnitudes[i];
				}
			}
	}
}
