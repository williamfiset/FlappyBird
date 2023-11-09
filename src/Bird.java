
/**
* Bird - This class creates functionality for a bird to fly and to have collision detection with pipes.
* 
* @Author Micah Stairs, William Fiset
* March 13, 2014
**/

import acm.graphics.*;
import acm.program.*;

public class Bird extends FlappyBird{

	GRectangle birdRect;

	protected int downwardSpeed = 0, x, y;
	private int animationCounter = 0;
	boolean hoverDirectionUp = true;

	public Bird(int startingX, int startingY){

		this.x = startingX;
		this.y = startingY;
		// Creates a thin invisible rectangle on top of the bird as it flys for collision detection
		birdRect = new GRectangle(x, y, 25, 30);

	}

	/** Checks for collision between the pipes and the bird **/
	public boolean pipeCollision(){

		for (GImage pipeImage : Data.pipeTopDay)
			if(birdRect.intersects( new GRectangle(pipeImage.getBounds())))
				return true;

		for (GImage pipeImage : Data.pipeBottomDay)
			if(birdRect.intersects( new GRectangle(pipeImage.getBounds())))
				return true;
		
		for (GImage pipeImage : Data.pipeMiddleDay)
			if(birdRect.intersects( new GRectangle(pipeImage.getBounds())))
				return true;

		return false;
	}

	public void updateBirdRect() {
		double newWidth = birdSize() * 1.5;
		double newHeight = birdSize();
		birdRect.setSize(newWidth, newHeight);
		birdRect.setLocation(getX(), getY());
	}

	public double birdSize(){
		
		//scale bird size based on Y location

		double scalingFactor = 7; // Adjust this value to make the bird scale slower
		double maxSize = 20.0; // Adjust this value to set the maximum size
		
		double birdHeight = Math.min((getY() / scalingFactor), maxSize);
		
		double birdWidth = birdHeight * 1.5; // Maintain the original proportions
		
		Data.birdDownDay.setSize(birdWidth, birdHeight);
		Data.birdFlatDay.setSize(birdWidth, birdHeight);
		Data.birdUpDay.setSize(birdWidth, birdHeight);
		//night
		Data.birdDownNight.setSize(birdWidth, birdHeight);
		Data.birdFlatNight.setSize(birdWidth, birdHeight);
		Data.birdUpNight.setSize(birdWidth, birdHeight);

		return birdHeight;
	}

	/** Draw bird on screen **/
	public void draw(GraphicsProgram window){
		
		// Resets the location for all bird images
		//Day
		Data.birdDownDay.setLocation(FlappyBird.BIRD_X_START, getY());
		Data.birdFlatDay.setLocation(FlappyBird.BIRD_X_START, getY());
		Data.birdUpDay.setLocation(FlappyBird.BIRD_X_START, getY());		
		//Night
		Data.birdDownNight.setLocation(FlappyBird.BIRD_X_START, getY());
		Data.birdFlatNight.setLocation(FlappyBird.BIRD_X_START, getY());
		Data.birdUpNight.setLocation(FlappyBird.BIRD_X_START, getY());

		birdSize();
		updateBirdRect();

		if(FlappyBird.currentMode != 2){
			
			// Proceeds to the next image in the animation
			if(animationCounter % 2 == 0)
				animateBird(animationCounter/2, window);
	
			animationCounter = (animationCounter + 1) % 8;
			
		}
		
	}

	/** Makes the bird move downwards **/
	public void fly(){

		// Move Flappy Bird
		downwardSpeed -= 1;
		this.setY( this.getY() - downwardSpeed );
		
 	}

	/** Makes sure that the bird doesn't go off screen **/
	public void capHeight(){
		
		// cap at top of screen
		if(getY() > 20)
			downwardSpeed = 10;

	}

	/** Animates the Flappy bird **/
	protected void animateBird(int index, GraphicsProgram window){
		
		if(index == 0){
			//Day
			window.add(Data.birdFlatDay);
			window.remove(Data.birdUpDay);
			//Night
			window.add(Data.birdFlatNight);
			window.remove(Data.birdUpNight);
		}
		else if(index == 1){
			//Day
			window.add(Data.birdDownDay);
			window.remove(Data.birdFlatDay);
			//Night
			window.add(Data.birdDownNight);
			window.remove(Data.birdFlatNight);

		}
		else if(index == 2){
			//Day
			window.add(Data.birdFlatDay);
			window.remove(Data.birdDownDay);
			//Night
			window.add(Data.birdFlatNight);
			window.remove(Data.birdDownNight);
		}
		else{
			//Day
			window.add(Data.birdUpDay);
			window.remove(Data.birdFlatDay);
			//Night
			window.add(Data.birdUpNight);
			window.remove(Data.birdFlatNight);
		}
		
	}

	public void setY(int y){ this.y = y; }
	public void setX(int x){ this.x = x; }
	public int getX(){ return this.x; }
	public int getY(){ return this.y; }

    public void scale(double d) {
    }

}
