package com.csus.csc133;
import java.util.Observable;
import java.util.Observer;
import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.plaf.Border;

public class ViewMessage extends Container implements Observer{
	private Label message;
    public ViewMessage() {
    	message = new Label("Game Start!");
		this.getStyle().setBorder(Border.createLineBorder(2, ColorUtil.BLACK));
		this.add(message);
    }
    public void update(Observable observable, Object newMessage) {
    	if(newMessage instanceof String) {
    		message.setText((String)newMessage);
    		revalidate();
    	}
    }
}
 