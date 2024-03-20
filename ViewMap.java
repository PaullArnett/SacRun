package com.csus.csc133;
import java.util.Observable;
import java.util.Observer;
import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Container;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Border;
import com.csus.csc133.GameObjectCollection.Iterator;

public class ViewMap extends Container implements Observer{

	
    public ViewMap() {
		this.getStyle().setBorder(Border.createLineBorder(2, ColorUtil.rgb(255,0,0)));
		this.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
    }
    public void update(Observable observable, Object o) {
    	GameModel gm = (GameModel) observable;
    	Iterator iterator = gm.getCollection().createIterator();
    	
		System.out.println("Time: " + gm.getGameTime()); //prints the game time
		//prints all info of each student
		while (iterator.hasNext()) {
			System.out.println(iterator.getNext().toString());
		}
		System.out.println("Game Width: " + this.getWidth());
		System.out.println("Game Height: " + this.getHeight());
    }
}