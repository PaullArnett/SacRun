package com.csus.csc133;

import com.codename1.ui.Button;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;

public class StudentDialog extends Dialog {
	
	public StudentDialog(GameModel gm){
		TextField textField = new TextField("", "Enter a number 0-9 for student collison");
		this.setLayout(new BorderLayout());

		Container containerB = new Container();
		
		Button okB = new Button("OK");
		Button cancelB = new Button("Cancel");
		
		containerB.add(okB);
		containerB.add(cancelB);
		
		okB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	String input = textField.getText();
            	try {
            		int num = Integer.parseInt(input);
            		if (num >= 0 && num <= 9) {
            			gm.pickStudent(num);
            			dispose();
            		}
            		else {
                        Dialog.show("Error", "Please enter a valid integer", "OK", null);
                        textField.clear();
            		}
            	}
            	catch(NumberFormatException e){
                    Dialog.show("Error", "Please enter a valid integer", "OK", null);
                    textField.clear();
            	}
            	
            }
        });
		
		cancelB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dispose();
            }
        });
		
		this.add(BorderLayout.CENTER, textField);
		this.add(BorderLayout.SOUTH, containerB);
	}
}