
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
	GImage currentImage;

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

	public void updateBirdRect(int player) {
		double newWidth = this.birdSize(player) * 1.5;
		double newHeight = this.birdSize(player);
		birdRect.setSize(newWidth, newHeight);
		birdRect.setLocation(this.getX(), this.getY());
	}

	public double birdSize(int player){
		
		//scale bird size based on Y location

		double scalingFactor = 7; // Adjust this value to make the bird scale slower
		double maxSize = 20.0; // Adjust this value to set the maximum size
		
		double birdHeight = Math.min((this.getY() / scalingFactor), maxSize);
		
		double birdWidth = birdHeight * 1.5; // Maintain the original proportions
		
		if(player == 1){
			Data.player1Down.setSize(birdWidth, birdHeight);
			Data.player1Flat.setSize(birdWidth, birdHeight);
			Data.player1Up.setSize(birdWidth, birdHeight);
		} else if (player == 2){
			Data.player2Down.setSize(birdWidth, birdHeight);
			Data.player2Flat.setSize(birdWidth, birdHeight);
			Data.player2Up.setSize(birdWidth, birdHeight);
		}

		return birdHeight;
	}

	/** Draw bird on screen **/
	public void draw(GraphicsProgram window, int player){
		
		// Resets the location for all bird images
		if (player == 1) {
			// Resets the location for player 1 bird images
			Data.player1Down.setLocation(FlappyBird.BIRD1_X_START, this.getY());
			Data.player1Flat.setLocation(FlappyBird.BIRD1_X_START, this.getY());
			Data.player1Up.setLocation(FlappyBird.BIRD1_X_START, this.getY());
			birdSize(1);
			updateBirdRect(1);
		} else if (player == 2) {
			// Resets the location for player 2 bird images
			Data.player2Down.setLocation(FlappyBird.BIRD2_X_START, this.getY());
			Data.player2Flat.setLocation(FlappyBird.BIRD2_X_START, this.getY());
			Data.player2Up.setLocation(FlappyBird.BIRD2_X_START, this.getY());
			birdSize(2);
			updateBirdRect(2);
		}
		
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
		this.downwardSpeed -= 1;
		this.setY( this.getY() - this.downwardSpeed );
		
 	}

	/** Makes sure that the bird doesn't go off screen **/
	public void capHeight(){
		
		// cap at top of screen
		if(this.getY() > 20)
			this.downwardSpeed = 10;

	}

	

	/** Animates the Flappy bird **/
	protected void animateBird(int index, GraphicsProgram window){
		if (FlappyBird.currentMode != 3){
		if(index == 0){
			//Day
			window.add(Data.player1Flat);
			window.remove(Data.player1Up);
			//Night
			window.add(Data.player2Flat);
			window.remove(Data.player2Up);
		}
		else if(index == 1){
			//Day
			window.add(Data.player1Down);
			window.remove(Data.player1Flat);
			//Night
			window.add(Data.player2Down);
			window.remove(Data.player2Flat);

		}
		else if(index == 2){
			//Day
			window.add(Data.player1Flat);
			window.remove(Data.player1Down);
			//Night
			window.add(Data.player2Flat);
			window.remove(Data.player2Down);
		}
		else{
			//Day
			window.add(Data.player1Up);
			window.remove(Data.player1Flat);
			//Night
			window.add(Data.player2Up);
			window.remove(Data.player2Flat);
		}
		//remove all bird images
	}else {
		remove(Data.player1Up);
		remove(Data.player1Down);
		remove(Data.player1Flat);
		remove(Data.player2Up);
		remove(Data.player2Down);
		remove(Data.player2Flat);
	}
}

	public void setY(int y){ this.y = y; }
	public void setX(int x){ this.x = x; }
	public int getX(){ return this.x; }
	public int getY(){ return this.y; }

    public void scale(double d) {
    }

}
