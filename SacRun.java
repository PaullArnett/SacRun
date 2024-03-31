package com.csus.csc133;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.util.UITimer;
import com.csus.csc133.GameModel.GameObject;
import com.csus.csc133.GameObjectCollection.Iterator;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.*;

public class SacRun extends Form implements Runnable, ActionListener {

	private GameModel gm;
	char key = ' ';
	UITimer turnTimer = new UITimer(() -> { handleInput(key); });
	
	public SacRun(){
		gm = new GameModel();
		UITimer timer = new UITimer(this);
		timer.schedule(gm.tickLength, true, this);
		A3();
		gm.init();

	}
	public void run() {
		gm.gameTick();
	}
	private void A3() {
		ViewMessage viewMessage = new ViewMessage();
		ViewMap viewMap = new ViewMap();
		ViewStatus viewStatus = new ViewStatus();
		Controls controls = new Controls(gm, this);
		Toolbar toolbar = this.getToolbar();
		
		gm.addObserver(viewStatus);
		gm.addObserver(viewMessage);
		gm.addObserver(viewMap);
		
		this.addPointerPressedListener((evt) -> { 
			int pointerX = evt.getX();
		    int pointerY = evt.getY();
		    viewMap.checkPointer(pointerX, pointerY);
		});
		
		//Commands for toolbar
		NonMovementCommand aboutB = new NonMovementCommand("About", gm);
		NonMovementCommand strategiesB = new NonMovementCommand("Change Strategies", gm);
        NonMovementCommand exitB = new NonMovementCommand("Exit", gm);
        NonMovementCommand lectureHallB = new NonMovementCommand("Lecture Hall", gm);
		
		//Creating side menu
		toolbar.addMaterialCommandToSideMenu("Change Strategies", FontImage.MATERIAL_SETTINGS,strategiesB);
        toolbar.addMaterialCommandToSideMenu("About", FontImage.MATERIAL_INFO, aboutB);
        toolbar.addMaterialCommandToSideMenu("Exit", FontImage.MATERIAL_EXIT_TO_APP, exitB);    

        //top right buttons
        this.getToolbar().addMaterialCommandToRightBar("Lecture Hall", ' ', lectureHallB);
        this.getToolbar().addMaterialCommandToRightBar("About", ' ', aboutB);

        //layout of each container
		this.setLayout(new BorderLayout());
		this.add(BorderLayout.WEST, controls);
	    this.add(BorderLayout.EAST, viewStatus);
	    this.add(BorderLayout.CENTER, viewMap);
	    this.add(BorderLayout.SOUTH, viewMessage);
        this.show();
        
        gm.setGameWidth(viewMap.getWidth());
        gm.setGameHeight(viewMap.getHeight());
	}
	
	public void actionPerformed(ActionEvent evt) {
        // Handle action events
    }
    public void keyPressed(int keyCode) {
		
        switch (keyCode) {
            case 'w':
                handleInput('w');
                break;
            case 's':
                handleInput('s');
                break;
            case 'a':
                handleInput('a');
                key = 'a';
                turnTimer.schedule(50, true, this);
                break;
            case 'd':
                handleInput('d');
                key = 'd';
                turnTimer.schedule(50, true, this);
                break;
        }
    }
	public void keyReleased(int keyCode) {
		switch(keyCode) {
		case 'a':
                turnTimer.cancel();
                break;
        case 'd':
        	 turnTimer.cancel();
             break;
		}
	}
	public void handleInput(char key) {
		gm.playerMovement(key);
	}
}
