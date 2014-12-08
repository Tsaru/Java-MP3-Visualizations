/**
 * This is a class that is suppose to capture audio via 
 * a Microphone or mixer and create a line as way to do this.
 * The record button on the visualizationInterface is suppose 
 * to respond to a button click by using the array in this class
 * to record what the person sings and save it in a file that the
 * singer can then listen to if wanted.
 * 
 * @author-Cullen Vaughn
 */

import java.io.ByteArrayOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class SpectrumMicrophone {
	TargetDataLine line;
	AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
	 boolean stopped;
	 SpectrumMicrophone(){
	DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
	if (!AudioSystem.isLineSupported(info)) {
	    System.err.println("An audio system error has occured, Audio System is not line supported");

	}
	// Obtain and open the line.
	try {
	    line = (TargetDataLine) AudioSystem.getLine(info);
		line.open(format);
		
	} catch (LineUnavailableException ex) {
	    System.err.println("The line here is not available");
	}

	// Assume that the TargetDataLine, line, has already
	// been obtained and opened.
	ByteArrayOutputStream out  = new ByteArrayOutputStream();
	int numBytesRead;
	byte[] data = new byte[line.getBufferSize() / 5];

	// Begin audio capture.
	line.start();

		
	while (!stopped) {
	    // Read the next chunk of data from the TargetDataLine.
	    numBytesRead =  line.read(data, 0, data.length);
	    // Save this chunk of data.
	    out.write(data, 0, numBytesRead);}
	
	 }
	
	 
	 //update method that displays the status of the line and closes the line if the line has stopped
	public void update(LineEvent status){
		LineEvent.Type type = status.getType();
		if(type == LineEvent.Type.OPEN){
			System.out.println("OPEN");
		}
		else if(type == LineEvent.Type.CLOSE){
			System.out.println("CLOSE");
			System.exit(0);
		}
		if(type == LineEvent.Type.START){
			System.out.println("START");
		}
		if(type == LineEvent.Type.STOP){
			System.out.println("STOP");
			line.close();
		}
	}

}
