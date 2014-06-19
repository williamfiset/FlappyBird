
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

	protected int downwardSpeed = 0, hoverCounter = 0, x, y;
	private int animationCounter = 0;
	boolean hoverDirectionUp = true;

	public Bird(int startingX, int startingY){
		
		// Creates a thin invisible rectangle on top of the bird as it flys for collision detection
		birdRect = new GRectangle(x, y , (int) Data.birdFlat.getWidth(), (int) Data.birdFlat.getHeight());

		this.x = startingX;
		this.y = startingY;

	}

	/** Checks for collision between the pipes and the bird **/
	public boolean pipeCollision(){

		for (GImage pipeImage : Data.pipeTop)
			if(birdRect.intersects( new GRectangle(pipeImage.getBounds())))
				return true;

		for (GImage pipeImage : Data.pipeBottom)
			if(birdRect.intersects( new GRectangle(pipeImage.getBounds())))
				return true;

		return false;
	}

	/** Draw bird on screen **/
	public void draw(GraphicsProgram window){
		
		// Resets the location for all bird images
		Data.birdDown.setLocation(FlappyBird.BIRD_X_START, getY() + hoverCounter);
		Data.birdFlat.setLocation(FlappyBird.BIRD_X_START, getY() + hoverCounter);
		Data.birdUp.setLocation(FlappyBird.BIRD_X_START, getY() + hoverCounter);
		
		if(FlappyBird.currentMode != 2){
			
			// Proceeds to the next image in the animation
			if(animationCounter % 2 == 0)
				animateBird(animationCounter/2, window);
	
			animationCounter = (animationCounter + 1) % 8;
			
		}
		
	}

	/** Makes the bird move downwards **/
	public void fly(){

		hoverBird();

		// Move Flappy Bird
		downwardSpeed -= 1*FlappyBird.currentMode;
		this.setY( this.getY() - downwardSpeed );

		// Set the new location of the invisible rectangle under the bird, used for collision detection
		birdRect.setLocation(FlappyBird.BIRD_X_START, getY() + hoverCounter);
		
 	}

	/** Makes sure that the bird doesn't go off screen **/
	public void capHeight(){
		
		if(getY() + hoverCounter > -16)
			downwardSpeed = 10;

	}

	/** Animates the Flappy bird **/
	protected void animateBird(int index, GraphicsProgram window){
		
		if(index == 0){
			window.add(Data.birdFlat);
			window.remove(Data.birdUp);
		}
		else if(index == 1){
			window.add(Data.birdDown);
			window.remove(Data.birdFlat);
		}
		else if(index == 2){
			window.add(Data.birdFlat);
			window.remove(Data.birdDown);
		}
		else{
			window.add(Data.birdUp);
			window.remove(Data.birdFlat);
		}
		
	}

	/** Makes the bird appear to hover up and down **/
	protected void hoverBird(){

		if(hoverDirectionUp){
			hoverCounter--;
			if(hoverCounter == -5)
				hoverDirectionUp = false;
		}
		else{
			hoverCounter++;
			if(hoverCounter == 5)
				hoverDirectionUp = true;
		}

	}

	public void setY(int y){ this.y = y; }
	public void setX(int x){ this.x = x; }
	public int getX(){ return this.x; }
	public int getY(){ return this.y; }

}
