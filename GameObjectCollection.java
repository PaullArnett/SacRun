package com.csus.csc133;

import java.util.ArrayList;
import com.csus.csc133.GameModel.GameObject;



public class GameObjectCollection {
	private ArrayList<GameObject> gameObjects;
	
	public GameObjectCollection() {
		gameObjects = new ArrayList<GameObject>();
	}
	public void addGameObject(GameObject o){
		getGameObjects().add(o);
	}

    public ArrayList<GameObject> getGameObjects() {
		return gameObjects;
	}

	public class Iterator {
        private int currentObject;

        public Iterator() {
            currentObject = 0;
        }

        public boolean hasNext() {
            return currentObject < getGameObjects().size();
        }

        public GameObject getNext() {
            if (hasNext()) {
                GameObject nextObject = getGameObjects().get(currentObject);
                currentObject++;
                return nextObject;
            }
            return null; 
        }
    }
	public Iterator createIterator() {
		return new Iterator();
	}
}
