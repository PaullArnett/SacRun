package com.csus.csc133;
import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Button;
import com.codename1.ui.Container;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Border;

class Controls extends Container{
	Button[] buttons = new Button[7];
		
	    public Controls(GameModel gm, SacRun sacRun) {
	    	//making button array to manipulate them easier
	    	//creating buttons for movement
	    	buttons[0] = new Button("Move");
	    	buttons[1] = new Button("Stop");
	    	buttons[2] = new Button("Turn Left");
	    	buttons[3] = new Button("Turn Right");
	    	//creating buttons for non-movement
	    	buttons[4] = new Button("Change Strategies");
	    	//buttons[5] = new Button("Lecture Hall");
	    	//buttons[6] = new Button("Restroom");
	    	//buttons[7] = new Button("Water Dispenser");
	    	buttons[5] = new Button("Pause");
	    	buttons[6] = new Button("Change Position");
	    	
	        // Set commands for movement buttons
			buttons[0].setCommand(new MovementCommand("Move", gm));
			buttons[1].setCommand(new MovementCommand("Stop", gm));
			buttons[2].setCommand(new MovementCommand("Turn Left", gm));
			buttons[3].setCommand(new MovementCommand("Turn Right", gm));
	        // Set commands for movement buttons
			buttons[4].setCommand(new NonMovementCommand("Change Strategies", gm, sacRun));
			//buttons[5].setCommand(new NonMovementCommand("Lecture Hall", gm, sacRun));
			//buttons[6].setCommand(new NonMovementCommand("Restroom", gm, sacRun));
			//buttons[7].setCommand(new NonMovementCommand("Water Dispenser", gm, sacRun));
			buttons[5].setCommand(new NonMovementCommand("Pause", gm, sacRun));
			buttons[6].setCommand(new NonMovementCommand("Change Position", gm, sacRun));
		
			for(int i = 0; i < buttons.length; i++) {
				buttons[i].setPreferredH(120);
				buttons[i].getStyle().setBgTransparency(255);
				buttons[i].getStyle().setBgColor(ColorUtil.rgb(150,75,0));
				buttons[i].getStyle().setFgColor(ColorUtil.rgb(255,255,255));
				buttons[i].getStyle().setBorder(Border.createLineBorder(2));
		        this.add(buttons[i]);
			}
			this.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
	    } 
	    public Button getButton(int i) {
	    	return buttons[i];
	    }
	}
	
