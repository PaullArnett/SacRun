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
    //each tick objects are re painted in there new positions
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
				//"!" about active lecture
				if (gm.activeLectureName != null) {
					if (((LectureHall)next).getName().equals(gm.activeLectureName)) {
						g.drawString("!", x + (size/2) - 7, y - 40);
					}
				}
			}
			if (next instanceof Restroom){
				g.setColor(ColorUtil.GREEN);
				g.fillRect(x, y, size, size);
			}
			if (next instanceof WaterDispenser) {
				g.setColor(ColorUtil.BLUE);
				g.fillArc(x, y, size, size, 0, 360);
			}
			if(next instanceof Student) {
				int[] xPoints = {x, x+size, x+(size/2)}; // X-coordinates 
			    int[] yPoints = {y+size, y+size, y};// Y-coordinates
			    int[] color = next.getColor();
			    g.setColor(ColorUtil.rgb(color[0], color[1], color[2]));
			    
			    if(next instanceof StudentPlayer) {
			    	g.fillPolygon(xPoints, yPoints, 3);
			    }
			    else {
			    	g.drawPolygon(xPoints, yPoints, 3);
			    }
			}
			//if object is selected, draw a red square around them
			if (next.isSelected()) {
				g.setColor(ColorUtil.rgb(255,0,0));
				g.drawRect(x, y, size, size);
			}
		}
	}
    public void update(Observable observable, Object o) {
    	gm = (GameModel) observable;
    	repaint();
    }
    public void checkPointer(int pointerX, int pointerY, boolean changePosition) {
		Iterator iterator2 = gm.getCollection().createIterator();
		while(iterator2.hasNext()) {
			//first check if user is changing position of object and move it accordingly
			GameObject next = iterator2.getNext();
			if (changePosition && next.isSelected()) {
				next.setX(pointerX - getX());
				next.setY(pointerY - getY() - 115);
				next.setSelected(false);
			}
			//checking if the click was inside the bounds of each object -- the 116 being added is the size of the toolbar
			else if (pointerX >= next.getX() + getX() && pointerX <= next.getX() + next.getSize() + getX() && pointerY >= next.getY() + getY() + 115 && pointerY <= next.getY() + next.getSize() + getY() + 115) {
				next.setSelected(true);
				gm.change("Selected: " + next.getClass().getSimpleName());
			}
			//else if the pointer is within the viewMap then unselect the object
			else if (pointerX >= getX() && pointerY >= getY()) {
				next.setSelected(false);
			}
			gm.change(null); //if paused, objects will still be re-drawn
		}
    }

}