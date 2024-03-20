package com.csus.csc133;
import java.util.Observable;
import java.util.Observer;
import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Border;

public class ViewStatus extends Container implements Observer{
	private Label lectureHallD = new Label("Lecture Hall");
	private Label lectureHallName = new Label("No class now");
	private Label lectureTimeD = new Label("Lecture Time Remaining");
	private Label lectureTime = new Label("0.0");
	private Label gameTimeD = new Label("Game Time");
	private Label gameTime = new Label("0.0");
	private Label absenceD = new Label("Absence");
	private Label absence = new Label("0");
	private Label hydrationD = new Label("Hydration");
	private Label hydration = new Label("100.0");
	private Label waterIntakeD = new Label("WaterIntake");
	private Label waterIntake = new Label("0.0");
	private Label holdD = new Label("Hold");
	private Label hold = new Label("0.0");
	
	
	
    public ViewStatus() {
		this.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
		//this.getStyle().setBorder(Border.createLineBorder(2, ColorUtil.rgb(0,0,0)));
		this.addAll(lectureHallD, lectureHallName, lectureTimeD, lectureTime, gameTimeD, gameTime, absenceD,
				absence,hydrationD, hydration, waterIntakeD, waterIntake, holdD, hold);
    }
    
    
    public void update(Observable observable, Object o) {
    	
    	GameModel gm = (GameModel) observable;
    	
    	if(gm.getActiveLecture()) {
    		lectureHallName.setText(gm.getActiveLectureName());
    		lectureTime.setText(String.valueOf(gm.getLectureTime()));
    	}
    	else {
    		lectureHallName.setText("No class now");
    		lectureTime.setText("0.0");
    	}
    	
    	gameTime.setText(String.valueOf(gm.getGameTime()));
    	absence.setText(String.valueOf(gm.getThePlayer().getAbsence()));
    	hydration.setText(String.valueOf(gm.getThePlayer().getHydration()));
    	waterIntake.setText(String.valueOf(gm.getThePlayer().getWaterIntake()));
    	hold.setText(String.valueOf(gm.getThePlayer().getTimeRemaining()));
    	
    	revalidate();
    }
}