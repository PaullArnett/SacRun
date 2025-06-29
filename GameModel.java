package com.csus.csc133;
import java.lang.Math;
import java.util.Arrays;
import java.util.Observable;
import java.util.Random;
import java.util.Vector;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import com.codename1.ui.Dialog;
import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.geom.Point;
import com.csus.csc133.GameObjectCollection.Iterator;

public class GameModel extends Observable {
	private int gameWidth;
	private int gameHeight;
	private double gameTime;
	private double elapsedTime;
	public int tickLength = 20;
	public double tickMult = (double) tickLength / 1000;
	public boolean activeLecture;
	public String activeLectureName;
	public double lectureTime;
	private StudentPlayer player;
	Random random = new Random();  //used for creating random integers
	private GameObjectCollection objectCollection;
	
	public void setGameWidth(int width) {
	    gameWidth = width;
	}
	public void setGameHeight(int height) {
	    gameHeight = height;
	}
	public double getGameTime() {
	    return gameTime;
	}
	public boolean getActiveLecture() {
	    return activeLecture;
	}
	public String getActiveLectureName() {
	    return activeLectureName;
	}
	public double getLectureTime() {
	    return lectureTime;
	}
	public StudentPlayer getThePlayer() {
	    return player.getPlayer();
	}
	public GameObjectCollection getCollection() {
		return objectCollection;
	}
	public void change(String s) {
		setChanged();
		notifyObservers(s);
	}
	
	public interface ISelectable{
		public void setSelected(boolean b);
		public boolean isSelected();
	}
	
	abstract class GameObject implements ISelectable {
		//coordinates of object
		private int size;
		private int[] color = {0,0,0};
		private boolean isSelected;
		Vector<GameObject> recentCollisions = new Vector<>();
		
		Transform translateForm = Transform.makeIdentity();
		Transform rotateForm = Transform.makeIdentity();
		private Transform myScale = Transform.makeIdentity();
		
		public int[]  pX,pY;
	
		public GameObject(){
			pX = new int[] {-size/2, -size/2, size/2, size/2};
			pY = new int[] {size/2, -size/2, -size/2, size/2};
			translateForm.translate(random.nextInt(gameWidth), random.nextInt(gameHeight));
		}
		public void draw (Graphics g, int screenX, int screenY) {

		}
		public void applyTranslation(Graphics g) {
			Transform gXform = Transform.makeIdentity();
			g.getTransform(gXform);
			
			gXform.translate(translateForm.getTranslateX(), translateForm.getTranslateY());
			
			g.setTransform(gXform);
		}
		public void applyRotation(Graphics g) {
			Transform gXform = Transform.makeIdentity();
			g.getTransform(gXform);
			
			gXform.concatenate(rotateForm);
			
			g.setTransform(gXform);
		}
		public void applyScale(Graphics g) {
			Transform gXform = Transform.makeIdentity();
			g.getTransform(gXform);
			
			gXform.concatenate(myScale);
			
			g.setTransform(gXform);
		}
		
		//methods for getting and setting variables
		public double getX() {
		    return translateForm.getTranslateX();
		}

		public double getY() {
		    return translateForm.getTranslateY();
		}
		public void setXY(float x, float y) {
			translateForm.setTranslation(x, y);  
		}
			
		public int[] getColor() {
		    return this.color;
		}
		public void setColor(int[] color) {
		    this.color = color;
		}
		public int getSize() {
		    return this.size;
		}
		public void setSize(int size) {
		    this.size = size;
		}
		public boolean isSelected() {
			return isSelected;
		}
		public void setSelected(boolean b) {
			isSelected = b;
		}
		public void handleCollide(Student s){
			//empty but will be overridden
		}
	}	
	
	abstract class Facility extends GameObject {
		//constructor to set specific coordinates for facilities when initiating
		public Facility() {
			super();
			setSize(90);
		}
		public void draw (Graphics g, int screenX, int screenY) {
			float x = -this.getSize() / 2;
			float y = -this.getSize() / 2;
					
			Transform oldXform = Transform.makeIdentity();
			g.getTransform(oldXform);
			
			applyTranslation(g);
			applyRotation(g);	
			applyScale(g);
			g.fillRect((int) x, (int) y, this.getSize(), this.getSize());
			
			if (this.isSelected()) {
				g.setColor(ColorUtil.rgb(255,0,0));
				g.drawRect(-(this.getSize()/2), -(this.getSize()/2), this.getSize(), this.getSize());
			}
			
			g.setTransform(oldXform);
		}
		//base toString for facilities which returns class name and position
		public String toString() {
			return getClass().getSimpleName() + ", pos (" + getX() + "," + getY() + "), Size: " + getSize();
		}
		
		//empty functions for lecture hall to be able to use facilities as an array
		public void startNewLecture() {
		}
		public void lectureTick() {
		}
	}
	
	class LectureHall extends Facility {
		private String name;
		private Lecture lecture;
		
		//constructor which includes name of lecture hall
		public LectureHall(String name) {
			super();
			this.name = name;
		}

		//if there is an active lecture then decrease its time by 1
		public void lectureTick() {
			if(elapsedTime % 100 == 0) {
				if(this.lecture != null) {
					lectureTime = Math.round((lectureTime - .1) * 10.0)/10.0;
					//if time is now 0 then end lecture and player is absent
					if (lectureTime <= 0) {
						player.getPlayer().absent();
						endLecture();
					}
				}
				else if(!activeLecture && random.nextInt(100) == 0){
						startNewLecture();
				}
			}
			//if no other lecture halls has an active lecture then 10% to start new one

		}
		public void startNewLecture() {
			lecture = new Lecture();
			activeLecture = true;
			activeLectureName = name;
		}
		public void endLecture() {
			lecture = null;
			activeLecture = false;
			activeLectureName = null;
		}
		//if player collides then end any active lecture
		public void handleCollide(Student s){
			if (s instanceof StudentPlayer && lecture != null) {
				endLecture();
			}
			s.turn(180);
		}
		//first checks if there is a lecture for the lecture hall and if so return its subject
		public String getSubject() {
			if (lecture != null) {
				return lecture.getSubject();
			}
			else {
				return null;
			}
		}
		public String getName() {
			return name;
		}
		public String toString() {
			return getClass().getSimpleName() + " " + name + ", pos (" + getX() + "," + getY() + "), Size: " + getSize() + ", Current Lecture: " + getSubject();
		}
	}
	//lecture class which keeps track of its subject and the timeRemaining until player is absent
	class Lecture {
		private String subject;
		String[] subjects = {"Math", "Science", "English", "History"};
		
		public Lecture() {
			subject = subjects[random.nextInt(4)];
			lectureTime = 20;
		}
		public String getSubject() {
			return subject;
		}
	}
	//Restroom class which sets a students water intake to 0 if collided with
	class Restroom extends Facility {
		public Restroom() {
			super();
			setSize(70);
		}
		public void draw (Graphics g, int screenX, int screenY) {
			g.setColor(ColorUtil.GREEN);
			super.draw(g, screenX, screenY);
		}
		public void handleCollide(Student s){
			if (s instanceof StudentPlayer) {
				s.useRestroom();
			}
			s.turn(180);
		}
	}
	//WaterDispenser class resets hydration of students to the default level if collided with
	class WaterDispenser extends Facility {
		public WaterDispenser() {
			super();
			setSize(50);
		}
		public void handleCollide(Student s){
			if (s instanceof StudentPlayer) {
				s.drink();
			}
			s.turn(180);
		}
		public void draw (Graphics g, int screenX, int screenY) {
			float x = -this.getSize() / 2;
			float y = -this.getSize() / 2;
			g.setColor(ColorUtil.BLUE);
					
			Transform oldXform = Transform.makeIdentity();
			g.getTransform(oldXform);
			
			applyTranslation(g);
			applyRotation(g);	
			g.fillArc((int) x, (int) y, this.getSize(), this.getSize(), 0, 360);
			
			if (this.isSelected()) {
				g.setColor(ColorUtil.rgb(255,0,0));
				g.drawRect(-(this.getSize()/2), -(this.getSize()/2), this.getSize(), this.getSize());
			}
			
			g.setTransform(oldXform);
		}
	}
	//interface for all move-able objects
	interface IMoveable {
		public void move();
		public void handleCollide(Student s);
	}
	abstract class Student extends GameObject implements IMoveable {
		public static final double DEFAULT_SPEED = 125;
		private static final double DEFAULT_TALKIVELEVEL = 2;
		private static final double DEFAULT_HYDRATION = 200.0;
		public double speed = DEFAULT_SPEED;
		private double talkiveLevel = DEFAULT_TALKIVELEVEL;
		private double head;
		private double timeRemaining;
		private double hydration = DEFAULT_HYDRATION;
		private double waterIntake;
		private double sweatingRate = 3;
		private boolean needTurn;
		
		//constructor for student that sets coordinates between (200,200) - (800,600) and degrees 0-359
		public Student() {
			super();
			setHead(random.nextInt(359));
			setColor(new int[]{255,0,0});  //red
			setSize(random.nextInt(21)+40);  //size of 40-60
			
			pX = new int[] {0, -this.getSize()/2, this.getSize()/2};
			pY = new int[] {this.getSize()/2+10, -this.getSize()/2, -this.getSize()/2};

		}
		public void draw (Graphics g, int screenX, int screenY) {
			int[] color = this.getColor();
	    	g.setColor(ColorUtil.rgb(color[0], color[1], color[2]));
	    	
			Transform xForm = Transform.makeIdentity(); 
			Transform oldXform = Transform.makeIdentity();

			//get parent's transformation
			g.getTransform(xForm);
			oldXform = xForm.copy();
			
			//transform them
			xForm.translate(screenX, screenY);
			xForm.concatenate(translateForm);
			xForm.concatenate(rotateForm);
			xForm.translate(-screenX, -screenY);
			
			//apply the transformation for the next draw (push stack)
			g.setTransform(xForm);
			
			if(this instanceof StudentPlayer) {
				g.fillPolygon(pX, pY, 3);
			}
			else if(this instanceof Student){
				g.drawPolygon(pX, pY, 3);
			}
			if (this.isSelected()) {
				g.setColor(ColorUtil.rgb(255,0,0));
				g.drawRect(-(this.getSize()/2), -(this.getSize()/2), this.getSize(), this.getSize());
			}
			
			//set it back to old one (pop stack)
			g.setTransform(oldXform);
		}
		public void move() {	
			if (timeRemaining <= 0) { //if not talking with another student then move
				double xNext = translateForm.getTranslateX();
				double yNext = translateForm.getTranslateY();
				//if player were to go outside bounds of map then they are placed at nearest edge and turned around
				if (xNext > gameWidth) {
					xNext = gameWidth;
					needTurn = true;
				}
				if (xNext < 0) {
					xNext = 0;
					needTurn = true;
				}
				if (yNext > gameHeight) {
					yNext = gameHeight;
					needTurn = true;
				}
				if (yNext < 0) {
					yNext = 0;
					needTurn = true;
				}
				if (needTurn) {
					turn(180);
					needTurn = false;
				}
				float xf = (float)(Math.cos(Math.toRadians(90 - head)) * speed * tickMult);
				float yf = (float)(Math.sin(Math.toRadians(90 - head)) * speed * tickMult);
				translateForm.translate(xf, yf);
			}


			
			if(elapsedTime % 1000 == 0)
			{
				hydration = hydration - sweatingRate; //hydration and timeRemaining drops each elapsed second
				
				if (timeRemaining > 0) {
					timeRemaining -= 1;
				}
			}
			if(timeRemaining > 0) {
				setColor(new int[] {255,192,203}); //pink
			}
			else {
				setColor(new int[] {255,0,0});  //red
			}
		}
		
		//reset to default hydration and add what was gained to the waterIntake
	    public void drink() {
	    	double intake = DEFAULT_HYDRATION - this.hydration;
	    	this.waterIntake = this.waterIntake + intake;
	    	this.hydration = DEFAULT_HYDRATION;
	    }
	    //waterIntake set to 0
	    public void useRestroom() {
	    	this.waterIntake = 0;
	    }
		//head is the direction the student is facing in degrees (1-359)
	    public double getHead() {
	        return head;
	    }
	    public void setHead(double deg) {
	    	double rotate = head - deg;
	    	rotateForm.rotate((float)Math.toRadians(rotate),0,0);
	    	head = deg;
	    }
	    //adds input to current head value and ensures value is between 1-359
	    public void turn(double deg) {
	    	head = head + deg;
	    	while (head > 359) {
	    		head = head - 360;
	    	}
	    	while (head < 0) {
	    		head = head + 360;
	    	}
	    	rotateForm.rotate(-(float)Math.toRadians(deg),0,0);
	    }
	    //talkiveLevel is how long they will talk if collided with
	    public double getTalkiveLevel() {
	    	return talkiveLevel;
	    }
	    //set method using multiplier
	    public void setTalkiveLevel(double talkMult) {
	    	talkiveLevel = talkiveLevel * talkMult;
	    }
	    public double getSpeed() {
	    	return speed;
	    }
	    //set method using multiplier
	    public void setSpeed(double speedMult) {
	    	speed = DEFAULT_SPEED * speedMult;
	    }
	    //set method using multiplier
	    public void setSweatingRate(double sweatMult) {
	    	sweatingRate = sweatingRate * sweatMult;
	    }
	    public double getHydration() {
	    	return hydration;
	    }
	    public double getWaterIntake() {
	    	return waterIntake;
	    }
	    public double getTimeRemaining() {
	    	return timeRemaining;
	    }
		public void handleCollide(Student s){
			//if student is not StudentNonstop (because StudentNonstop ignores collisions with other students)
			if (!(s instanceof StudentNonstop)) {
				//timeRemaing for both students becomes the value of the highest talkiveLevel
				double max = Math.max(this.talkiveLevel, s.talkiveLevel);
				this.timeRemaining = max;
				s.timeRemaining = max;
			}
		}
		//toString method for printing out game data
		public String toString() {
			return getClass().getSimpleName() + ", pos (" + getX() + "," + getY() + "), head:" + head + ", speed:" + speed + ", hydration:" + hydration + 
					", talkiveLevel:" + talkiveLevel + ", timeRemain:" + timeRemaining + ", Size: " + getSize() + ", Color: " + Arrays.toString(getColor());
		}
	}
	//Singleton design
	class StudentPlayer extends Student{
		private StudentPlayer thePlayer;
		private int absenceTime;
		//overriding constructor to set specific coordinates
		private StudentPlayer() {
			//middle of map facing north
			super();
			translateForm.setTranslation(gameWidth/2, gameHeight/2);
			setHead(180);
			setSpeed(0);
			setSize(55);
		}
		public StudentPlayer getPlayer() {
	        if (thePlayer == null) {
	        	thePlayer = new StudentPlayer();
	        }
	        return thePlayer;
	    }
		public void absent() {
			getPlayer().absenceTime++;
		}
		public int getAbsence() {
			return getPlayer().absenceTime;
		}
		//adding to toString to include more info for Player
		public String toString() {
			return super.toString() + ", Absence: " + getPlayer().absenceTime + ", WaterIntake: " + getPlayer().getWaterIntake();
		}
	}
	class StudentStrategy extends Student{
		private IStrategy moveStrategy;
		private String strategyName;
		
		public StudentStrategy(){
			super();
			setStrategy(random.nextInt(3));
		}
		public void move(){
			moveStrategy.apply();
			super.move();
		}
		
		public void setStrategy(int type) {
			switch(type) {
				case 0: moveStrategy = new confusedStrategy(this); strategyName = "Confused Strategy"; break;
				case 1: moveStrategy = new verticalStrategy(this); strategyName = "Vertical Strategy"; break;
				case 2: moveStrategy = new horizontalStrategy(this); strategyName = "Horizontal Strategy"; break;
			}
			moveStrategy.apply();

		}
		public String getStrategyName(){
			return strategyName;
		}

		
		public String toString() {
			return super.toString() + " " + strategyName;
		}
	}
	public interface IStrategy{
		public void apply();
	}
	public class confusedStrategy implements IStrategy{
		private StudentStrategy parent;
		public confusedStrategy(StudentStrategy parent) {
	        this.parent = parent;
	    }
		public void apply() {
			if (elapsedTime % 1000 == 0) {
				this.parent.setHead(random.nextInt(360));
			}
		}
	}
	public class verticalStrategy implements IStrategy{
		private StudentStrategy parent;
		public verticalStrategy(StudentStrategy parent) {
	        this.parent = parent;
	    }
		public void apply() {
			//round head value to either 90 or 270
			if (parent.getHead() >= 270 && parent.getHead() <= 90) {
				parent.setHead(0);
			}else {
				parent.setHead(180);
			}
		}
	}
	public class horizontalStrategy implements IStrategy{
		private StudentStrategy parent;
		public horizontalStrategy(StudentStrategy parent) {
	        this.parent = parent;
	    }
		public void apply() {
			if (parent.getHead() >= 0 && parent.getHead() <= 180) {
				parent.setHead(90);
			}else {
				parent.setHead(270);
			}
		}
	}
	
	//StudentAngry talks twice as long
	class StudentAngry extends Student implements IMoveable {
		public StudentAngry() {
			super();
			setTalkiveLevel(2);
		}	
		//extending toString method
		public String toString() {
			return super.toString() + ", I am Angry!";
		}
	}
	//StudentBiking is 3 times as fast and sweats twice as much
	class StudentBiking extends Student implements IMoveable {
		public StudentBiking() {
			super();
			setSpeed(3);
			setSweatingRate(2);
		}	
	}
	//studentCar is 5 times as fast but only moves horizontal
	class StudentCar extends Student implements IMoveable {
		public StudentCar() {
			super();
			setSpeed(5);
			setSweatingRate(0);
			//round head value to either 90 or 270
			if (getHead() >= 0 && getHead() <= 179) {
				setHead(90);
			}else {
				setHead(270);
			}
		}	
	}
	//StudentConfused moves in random directions
	class StudentConfused extends Student implements IMoveable {
		//random head direction each time move is called
		public void move() {
			if (elapsedTime % 1000 == 0) {
				setHead(random.nextInt(359));
			}
			super.move();
		}
	}
	//StudentFriendly doesn't talk long
	class StudentFriendly extends Student implements IMoveable {
		public StudentFriendly() {
			super();
			setTalkiveLevel(.5); 
		}
	}
	//StudentHappy will randomly speed up
	class StudentHappy extends Student implements IMoveable {	
		public void move(){
			if (elapsedTime % 1000 == 0) {	
				if (this.speed != DEFAULT_SPEED) {
					this.speed = DEFAULT_SPEED;
				}
				if (random.nextInt(4) == 0) { //25% chance to speed up
					setSpeed(5);
				}
			}
			super.move();
		}
		//extended toString method
		public String toString() {
			return super.toString() + ", I am Happy!";
		}
	}
	//doesn't stop for talking when colliding with other student
	class StudentNonstop extends Student implements IMoveable {
		public void handleCollide(Student s){ 
			//override to cancel collision
		}
	}
	//StudentSleeping doesn't move or sweat
	class StudentSleeping extends Student implements IMoveable {
		public StudentSleeping() {
			super();
			setSweatingRate(0);
		}	
		public void move() { 
			//Override because StudentSleeping doesn't move
		}
		public String toString() {
			return super.toString() + ", zzzZZZ!";
		}
	}
	//StudentRunning has twice the speed and sweat rate
	class StudentRunning extends Student implements IMoveable {
		public StudentRunning() {
			super();
			setSweatingRate(2);
			setSpeed(2);
		}	
	}
	//initiates all the game objects
	public void init(){
		objectCollection = new GameObjectCollection();
		player = new StudentPlayer();
		
		objectCollection.addGameObject(player.getPlayer());
		objectCollection.addGameObject(new StudentStrategy());
		//1-2 of each student (excluding player and student strategy)
		for (int i = 0; i <= random.nextInt(2); i++) {
			objectCollection.addGameObject(new StudentAngry());
		}
		for (int i = 0; i <= random.nextInt(2); i++) { 
			objectCollection.addGameObject(new StudentBiking());
		}
		for (int i = 0; i <= random.nextInt(2); i++) { 
				objectCollection.addGameObject(new StudentCar());
		}
		for (int i = 0; i <= random.nextInt(2); i++) { 
			objectCollection.addGameObject(new StudentConfused());
		}
		for (int i = 0; i <= random.nextInt(2); i++) { 
			objectCollection.addGameObject(new StudentFriendly());
		}
		for (int i = 0; i <= random.nextInt(2); i++) { 
			objectCollection.addGameObject(new StudentHappy());
		}
		for (int i = 0; i <= random.nextInt(2); i++) { 
			objectCollection.addGameObject(new StudentNonstop());
		}
		for (int i = 0; i <= random.nextInt(2); i++) { 
			objectCollection.addGameObject(new StudentSleeping());
		}
		for (int i = 0; i <= random.nextInt(2); i++) { 
			objectCollection.addGameObject(new StudentRunning());
		}
		//creating 3 lecture halls
		objectCollection.addGameObject(new LectureHall("Amador"));
		objectCollection.addGameObject(new LectureHall("Shasta"));
		objectCollection.addGameObject(new LectureHall("Tahoe"));
		//2-4 WaterDispensers and Restrooms
		for (int i = 0; i <= random.nextInt(3)+1; i++) {
			objectCollection.addGameObject(new WaterDispenser());
		}
		for (int i = 0; i <= random.nextInt(3)+1; i++) {
			objectCollection.addGameObject(new Restroom());
		}
	    setChanged();
	    notifyObservers();
	}
	//Movement inputs
	public void playerMovement(char key) {
		String descr = null;
		if(key == 'w') {
			player.getPlayer().setSpeed(1.8); //player will move if game tick passes
			descr = "Player starts moving";
		}
		if(key == 's') {
			player.getPlayer().setSpeed(0); //player will NOT move if game tick passes
			descr = "Player stops moving";
			
		}
		if(key == 'a') {
			player.getPlayer().turn(10); // player turns left 15 degrees
			descr = "Player turns left";
		}
		if(key == 'd') {
			player.getPlayer().turn(-10); // player turns right 15 degrees
			descr = "Player turns right";
		}
	    setChanged();
	    notifyObservers(descr);
	}
	//Command inputs
	public void commands(char key) {
		Iterator iterator = objectCollection.createIterator();
		GameObject next = iterator.getNext();
		
		if(key == 'i') {
			//prints out creator
			System.out.println("Developer: Paul Arnett");
			setChanged();
			notifyObservers();
		}
		//randomly changes student strategy
		if(key == 'c') {
			while(iterator.hasNext()) {
				next = iterator.getNext();
				if (next instanceof StudentStrategy) {
					((StudentStrategy) next).setStrategy(random.nextInt(3));
					setChanged();
					notifyObservers("StudentStrategy changed to " + ((StudentStrategy) next).getStrategyName());
					break;
				}
			}
		}
	}
	public void gameTick() {
		elapsedTime += tickLength;
		Iterator iterator = objectCollection.createIterator();
		//all students use their move function
		while(iterator.hasNext()) {
			GameObject next = iterator.getNext();
			if (next instanceof IMoveable) {
				((IMoveable) next).move();
			}
			//all lecture halls use lectureTick
			if (next instanceof LectureHall) {
				((LectureHall) next).lectureTick();
			}
			Iterator iterator2 = objectCollection.createIterator();
			while(iterator2.hasNext()) {
				GameObject next2 = iterator2.getNext();
				if(checkCollision(next, next2)) {
					collision(next, next2);
				}
			}
		}
		//game over if hydration = 0 or water intake is over 199 or 3 absences occur
		if (player.getPlayer().getHydration() <= 0 | player.getPlayer().getWaterIntake() >= 200 | player.getPlayer().getAbsence() >= 3) {
			gameOver();
		}
		if (elapsedTime % 100 == 0) {
		gameTime = Math.round((gameTime + .1)*10.0)/10.0;
		}
		setChanged();
		notifyObservers();
	}
	public boolean checkCollision(GameObject obj1, GameObject obj2) {
		double obj1X = obj1.getX() - obj1.getSize();
		double obj1Y = obj1.getY() - obj1.getSize();
		double obj2X = obj2.getX() - obj1.getSize();
		double obj2Y = obj2.getY() - obj1.getSize();
		//checking if they overlap horizontally and vertically
		if(obj1 != obj2 && obj1X + obj1.getSize() >= obj2X && obj1X <= obj2X + obj2.getSize() && obj1Y + obj1.getSize() >= obj2Y && obj1Y <= obj2Y + obj2.getSize()) {
			return true;
		}
		else {
			if(obj1.recentCollisions.contains(obj2)) {
				obj1.recentCollisions.remove(obj2);
				obj2.recentCollisions.remove(obj1);
			}
			return false;
		}
	}
	public void collision(GameObject obj1, GameObject obj2) {
		if(obj1 instanceof Student) {
			if (!obj1.recentCollisions.contains(obj2)) {
				obj1.recentCollisions.add(obj2);
				obj2.recentCollisions.add(obj1);
				obj2.handleCollide((Student)obj1);
				setChanged();
				notifyObservers(obj1.getClass().getSimpleName() + " collided with " + obj2.getClass().getSimpleName() + "!");
			}
		}
		else if(obj2 instanceof Student) {
			if (!obj2.recentCollisions.contains(obj1)) {
				obj1.recentCollisions.add(obj2);
				obj2.recentCollisions.add(obj1);
				obj1.handleCollide((Student)obj2);
				setChanged();
				notifyObservers(obj2.getClass().getSimpleName() + " collided with " + obj1.getClass().getSimpleName() + "!");
			}
		}
	}
	//game over :(
	public void gameOver() {
		boolean exit = Dialog.show("GameOver", "Game Time: " + gameTime, "Exit Game", null);
		if(exit || !exit) {
        	CN.exitApplication();
		}
		setChanged();
		notifyObservers("Game Over");
	}
}
