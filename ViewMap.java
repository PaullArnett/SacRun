package com.csus.csc133;
import java.util.Observable;
import java.util.Observer;
import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Container;
import com.codename1.ui.Graphics;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Border;
import com.csus.csc133.GameModel.*;
import com.csus.csc133.GameObjectCollection.Iterator;

public class ViewMap extends Container implements Observer{
	GameModel gm;
	
    public ViewMap() {
		this.getStyle().setBorder(Border.createLineBorder(2, ColorUtil.rgb(255,0,0)));
		this.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
    }
	public void paint(Graphics g) {
		super.paint(g);
		Iterator iterator = gm.getCollection().createIterator();
		
		while (iterator.hasNext()) {
			GameObject next = iterator.getNext();
	        int x = (int)(getX() + next.getX());
	        int y = (int)(getY() + next.getY());
	        int size = next.getSize();
	        
			if (next instanceof LectureHall){
				g.setColor(ColorUtil.BLUE);
				g.fillRect(x, y, size, size);
				g.drawString(((LectureHall)next).getName(), x, y + size + 5);
			}
			if (next instanceof Restroom){
				g.setColor(ColorUtil.GREEN);
				g.fillRect(x, y, size, size);
			}
			if (next instanceof WaterDispenser) {
				g.setColor(ColorUtil.BLUE);
				g.fillArc(x, y, size, size, 0, 360);
			}
			if(next instanceof Student && !(next instanceof StudentPlayer)) {
				int[] xPoints = {x, x+size, x+(size/2)}; // X-coordinates 
			    int[] yPoints = {y+size, y+size, y};// Y-coordinates
			    g.setColor(ColorUtil.BLACK);
			    g.drawPolygon(xPoints, yPoints, 3);
			}
			if(next instanceof StudentPlayer) {
				int[] xPoints = {x, x+size, x+(size/2)};  
				int[] yPoints = {y+size, y+size, y};
			    g.setColor(ColorUtil.BLACK);
			    g.fillPolygon(xPoints, yPoints, 3);
			}
			System.out.println(next.toString());
		}
	}
    public void update(Observable observable, Object o) {
    	gm = (GameModel) observable;
    	repaint();
    	/*//prints out all info
		System.out.println("Time: " + gm.getGameTime());
		while (iterator.hasNext()) {
			GameObject next = iterator.getNext();
			if (next instanceof LectureHall){
				paint();
			}
			System.out.println(iterator.getNext().toString());
		}
		System.out.println("Game Width: " + this.getWidth());
		System.out.println("Game Height: " + this.getHeight());
        */
    }

}