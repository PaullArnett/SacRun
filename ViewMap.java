package com.csus.csc133;
import java.util.Observable;
import java.util.Observer;
import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Container;
import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Border;
import com.csus.csc133.GameModel.*;
import com.csus.csc133.GameObjectCollection.Iterator;

public class ViewMap extends Container implements Observer{
	GameModel gm;
	//TransformedShape[] obj;
	int screenWidth;
	int screenHeight;
	int viewL;
	int viewB;
	int viewWidth;
	int viewHeight;
	float mult =1;
	
	int originalViewWidth;
	int originalViewHeight;
	
	private boolean isPinch = false;
	private boolean isDragged = false;
	private int pPrevDragLocX = -1;
	private int pPrevDragLocY = -1;
	Transform VTM, W2ND, ND2D;
	
    public ViewMap() {
		updateVTM();
		this.getStyle().setBorder(Border.createLineBorder(2, ColorUtil.rgb(255,0,0)));
		this.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
    }
    //Initializes starting width/height
    public void setWH(int w, int h) {
    	originalViewWidth = viewWidth = screenWidth = w;
    	originalViewHeight = viewHeight = screenHeight = h;
    	updateVTM();
    }
    
    //applies changes to VTM
    public void updateVTM() {
		VTM = Transform.makeIdentity();
		W2ND = Transform.makeScale(1.0f/viewWidth, 1.0f/viewHeight);
		W2ND.translate(-viewL, viewB);
		ND2D = Transform.makeTranslation(0, 0);
		ND2D.scale(screenWidth, screenHeight);
		VTM.concatenate(ND2D);
		VTM.concatenate(W2ND);
	}
    
    //each tick objects are re painted in there new positions
	public void paint(Graphics g) {
		super.paint(g);
		Iterator iterator = gm.getCollection().createIterator();
		
		Transform xForm = Transform.makeIdentity();
		xForm.translate(getX(),getY());

		updateVTM();
		xForm.concatenate(VTM);
		 
		g.setTransform(xForm);
		while (iterator.hasNext()) {
			GameObject next = iterator.getNext();
	        int x = (int)(next.getX());
	        int y = (int)(next.getY());
	        int size = next.getSize();
	        
			if (next instanceof LectureHall){
				g.setColor(ColorUtil.BLUE);
				g.drawString(((LectureHall)next).getName(), x-(size/2), y-(size/2) + size + 5);
				//"!" above active lecture
				if (gm.activeLectureName != null) {
					if (((LectureHall)next).getName().equals(gm.activeLectureName)) {
						g.drawString("!", x - 7, y - 40 -(size/2));
					}
				}
				next.draw(g, getParent().getAbsoluteX(),getParent().getAbsoluteY());
			}
			else {
				next.draw(g, getParent().getAbsoluteX(),getParent().getAbsoluteY());
			}
			//if object is selected, draw a red square around them

			g.setColor(ColorUtil.BLACK);
			g.drawRect(5, 5, this.getWidth()-10, this.getHeight()-10);
		}
		g.resetAffine();
	}
    public void update(Observable observable, Object o) {
    	gm = (GameModel) observable;
    	repaint();
    }
    public void checkPointer(int pointerX, int pointerY, boolean changePosition) {
		Iterator iterator2 = gm.getCollection().createIterator();
		System.out.println((1.0f/viewWidth)*screenWidth);
		while(iterator2.hasNext()) {
			//first check if user is changing position of object and move it accordingly
			GameObject next = iterator2.getNext();
			if (changePosition && next.isSelected()) {
				next.setXY((pointerX - getX())/(1.0f/viewWidth)/screenWidth + viewL, (pointerY- getY())/(1.0f/viewHeight)/screenHeight - viewB - 115);
				System.out.println(pointerX);
				next.setSelected(false);
				
			}
			//checking if the click was inside the bounds of each object -- the 116 being added is the size of the toolbar
			else if (pointerX >= (next.getX()- (next.getSize()/2) - viewL)*(1.0f/viewWidth)*screenWidth + getX()  && pointerX <= (next.getX() + next.getSize()- (next.getSize()/2)-viewL)*(1.0f/viewWidth)*screenWidth + getX() && pointerY >= (next.getY()  + 115- (next.getSize()/2)+viewB)*(1.0f/viewHeight)*screenHeight+ getY() && pointerY <= (next.getY() + next.getSize()  + 115- (next.getSize()/2)+viewB)*(1.0f/viewHeight)*screenHeight+ getY()) {
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
	
	//Zoom and Pan methods
	public void zoom(float factor) {
		factor = 1 +(1- factor)/10; //flips stretch and pinch and reduces intensity of zoom
		float newWidth = viewWidth * (factor);
		float newHeight = viewHeight * (factor);
		if(newWidth < 600 || newHeight < 600 ||	newWidth > 3000 || newHeight > 3000 ) return;
		viewL += (viewWidth - newWidth)/2;
		viewB -= (viewHeight - newHeight)/2;
		viewWidth = (int) newWidth;
		viewHeight = (int) newHeight;
		this.repaint();

	}
	public void panHorizontal(double delta) {
		viewL += delta;
		this.repaint();
	}
	public void panVertical(double delta) {
		viewB += delta;
		this.repaint();
	}

	protected boolean pinch(float scale){
		isPinch = true;
		super.pinch(scale);
		zoom(scale);
		return true;
	}

	//Mouse input
	@Override
	public void pointerPressed(int x, int y){
		pPrevDragLocX = x;
		pPrevDragLocY = y;
		
	}
	
	public void pointerReleased(int x, int y){
		if(isPinch || isDragged) {
			isDragged = false;
			isPinch = false;
			return;
		}
		repaint();
	}
	
	public void pointerDragged(int x, int y){
		isDragged = true;
		double dx = pPrevDragLocX - x;
		double dy = pPrevDragLocY - y;
		dx *= (viewWidth / (float)screenWidth);
		dy *= -(viewHeight / (float)screenHeight);
		panHorizontal(dx);
		panVertical(dy);
		pPrevDragLocX = x;
		pPrevDragLocY = y;
		repaint();
	}

}