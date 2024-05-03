package com.csus.csc133;
import java.io.InputStream;
import com.codename1.media.Media;
import com.codename1.media.MediaManager;
import com.codename1.ui.Display;

public class Sound implements Runnable{
	private Media m;

	public Sound(String fileName) {
		try {
	        InputStream is = Display.getInstance().getResourceAsStream(getClass(), "/" + fileName);
	        m = MediaManager.createMedia(is, "audio/mp3", this);
        } 
        catch (Exception err) {
        	err.printStackTrace();
        }
	}
	public void run() {
		m.setTime(0);
		m.play();
	}
	public void changeVolume(int vol) {
		m.setVolume(vol);
	}
	public void play() {
		m.setVolume(40);
		m.play();
	}
	public void pause() {
		m.pause();
	}
	
}