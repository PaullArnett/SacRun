package com.csus.csc133;
import com.codename1.ui.CN;
import com.codename1.ui.Command;
import com.codename1.ui.Dialog;
import com.codename1.ui.events.ActionEvent;

class NonMovementCommand extends Command {
	private GameModel gm;
		public NonMovementCommand(String command, GameModel gm) {
			super(command);
			this.gm = gm;
		}
		public void actionPerformed(ActionEvent e) {
			switch(getCommandName()) {

				case "Lecture Hall": handleInput('1'); break;
				case "Restroom": handleInput('2'); break;
				case "Water Dispenser": handleInput('3'); break;
				case "Change Strategies": handleInput('c'); break;
				case "Student": handleInput('t'); break;
				case "Next Frame": handleInput('f'); break;
				case "About": handleInput('i'); break;
				case "Exit": handleInput('e'); break;
			}
		}
		public void handleInput(char key) {
			if (key == 't') {
				StudentDialog studentDialog = new StudentDialog(gm);
				studentDialog.show();
			}
			else if (key == 'i') {
	            Dialog.show("A2", "Paul Arnett Spring 2024", "Confirm", null);
			}
			else if (key == 'e') {
				boolean exit = Dialog.show("Confirm", "Are you sure you want to exit?", "Yes", "No");
				if (exit) {
					CN.exitApplication();
				}
			}
			else {
				gm.commands(key);
			}
		}
	}