package com.csus.csc133;
import java.io.InputStream;
import com.codename1.media.Media;
import com.codename1.media.MediaManager;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.util.UITimer;
import com.codename1.ui.events.ActionEvent;

public class SacRun extends Form implements Runnable{
    //Variables that need to be easily accessible
	private GameModel gm = new GameModel();
	char key = ' ';
	UITimer turnTimer = new UITimer(() -> { handleInput(key); });
	UITimer timer = new UITimer(this);
	boolean changePosition = false;
	Controls controls = new Controls(gm, this);
	Sound background = new Sound("bgMusic.mp3");
	
	ViewMap viewMap = new ViewMap();
	
	public SacRun(){
		A4();
		background.run();
		gm.init();
		viewMap.setWH(viewMap.getWidth(), viewMap.getHeight());
		timer.schedule(gm.tickLength, true, this);
	}
	
	public void run() {
		gm.gameTick();
	}
	
	private void A4() {
		ViewMessage viewMessage = new ViewMessage();
		ViewStatus viewStatus = new ViewStatus();
		
		Toolbar toolbar = this.getToolbar();
		gm.addObserver(viewStatus);
		gm.addObserver(viewMessage);
		gm.addObserver(viewMap);
		
		//whenever pointer is pressed call checkPointer
		this.addPointerPressedListener((evt) -> { 
			int pointerX = evt.getX();
		    int pointerY = evt.getY();
		    viewMap.checkPointer(pointerX, pointerY, changePosition);
		    changePosition = false;
		});
		
		//Commands for tool bar
		NonMovementCommand aboutB = new NonMovementCommand("About", gm, this);
		NonMovementCommand strategiesB = new NonMovementCommand("Change Strategies", gm, this);
        NonMovementCommand exitB = new NonMovementCommand("Exit", gm, this);
		
		//Creating side menu
		toolbar.addMaterialCommandToSideMenu("Change Strategies", FontImage.MATERIAL_SETTINGS,strategiesB);
        toolbar.addMaterialCommandToSideMenu("About", FontImage.MATERIAL_INFO, aboutB);
        toolbar.addMaterialCommandToSideMenu("Exit", FontImage.MATERIAL_EXIT_TO_APP, exitB);    

        //top right buttons
        this.getToolbar().addMaterialCommandToRightBar("About", ' ', aboutB);

        //layout of each container
		this.setLayout(new BorderLayout());
		this.add(BorderLayout.WEST, controls);
	    this.add(BorderLayout.EAST, viewStatus);
	    this.add(BorderLayout.CENTER, viewMap);
	    this.add(BorderLayout.SOUTH, viewMessage);
        this.show();

        //s1.play();
       
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
                turnTimer.schedule(50, true, this); //turns smoothly if button held
                break;
            case 'd':
                handleInput('d');
                key = 'd';
                turnTimer.schedule(50, true, this);
                break;
        }
    }
    //stops turning once key released
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
	//stops timer in order to pause game
	public void pauseGame() {
		timer.cancel();
		controls.getButton(5).setText("Play");
		gm.change("Game is Paused");
		background.pause();
	}
	//continues timer to keep playing
	public void playGame() {
		timer.schedule(gm.tickLength, true, this);
		controls.getButton(5).setText("Pause");
		gm.change("Game Continues");
		background.play();
	}
	public void changePosition() {
		this.changePosition = true;
	}
}
