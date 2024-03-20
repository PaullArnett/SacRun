package com.csus.csc133;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;

public class SacRun extends Form{

	private GameModel gm;
	
	public SacRun(){
		gm = new GameModel();
		A2();
		gm.init();
	}

	private void A2() {
		ViewMessage viewMessage = new ViewMessage();
		ViewMap viewMap = new ViewMap();
		ViewStatus viewStatus = new ViewStatus();
		Controls controls = new Controls(gm);
		Toolbar toolbar = this.getToolbar();
		
		gm.addObserver(viewStatus);
		gm.addObserver(viewMessage);
		gm.addObserver(viewMap);
		
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
}
