package com.csus.csc133;
import com.codename1.ui.Command;
import com.codename1.ui.events.ActionEvent;

class MovementCommand extends Command {
	private GameModel gm;
	public MovementCommand(String command, GameModel gm) {
		super(command);
		this.gm = gm;
	}
	public void actionPerformed(ActionEvent e) {
		switch(getCommandName()) {
			case "Move": handleInput('w'); break;
			case "Stop": handleInput('s'); break;
			case "Turn Left": handleInput('a'); break;
			case "Turn Right": handleInput('d'); break;
		}
	}
	public void handleInput(char key) {
		gm.playerMovement(key);
	}
}