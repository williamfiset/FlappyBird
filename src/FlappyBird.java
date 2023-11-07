
/** 
* Flappy Bird is a recreation of the popular mobile game created by Dong Nguyen.
* NOTE: All graphics and sound effects were not created by us; however, all of the code was written by us.
* 
* @Author Micah Stairs, William Fiset
* @Version 2.0
* March 13, 2014
*/

import acm.graphics.*;
import acm.program.*;
import java.awt.event.*;

public class FlappyBird extends GraphicsProgram {

	final static int SCREEN_WIDTH = 288, SCREEN_HEIGHT = 512, GROUND_LEVEL = 400, PIPE_WIDTH = 52, BIRD_X_START = 68;

	Bird bird;
	FileHandler highScoreFile;

	static int currentMode = 0; // 0 = Get Ready, 1 = Playing, 2 = Falling, 3 = Game Over
	static int score = 0;
	boolean isNight = true;
	int scoreChange = 0;

	// award for the space between pipes
	int[] pipeSpaceAward = {0,0,0,0};
	// center of the space between pipes
	int[] pipeSpaceCenter = {0,0,0,0};
	// space between pipes
	int pipeSpace = 0;
	
	// min and max for the space between pipes
	int min = 38, max = 137;
	int scoreInterval = (max + min) / 2;

	@Override
	public void init() {

		setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

		// Loads images and sets their locations
		Data.init();
		initializeDigits(true);

		// Instantiate Bird
		bird = new Bird(FlappyBird.BIRD_X_START, 240);

		// Creates a new fileHanlder to deal with saving to a file
		highScoreFile = new FileHandler();

		// Sets
		resetPipes();

		//randomize night 
		isNight = (Math.random() < 0.5);
		changeNight();

		// Adds starting images to screen
		add(Data.backgroundNight);
		add(Data.backgroundDay);


		for (int i = 0; i < 4; i++) {
			// Adds pipes to screen
			add(Data.pipeTopDay[i]);
			add(Data.pipeBottomDay[i]);
			add(Data.pipeTopNight[i]);
			add(Data.pipeBottomNight[i]);
		}
		add(Data.ground);
		add(Data.getReady);
		if (score >= scoreInterval * 100){
			add(Data.birdLogo);
		}
		add(Data.instructions);
		add(Data.birdUpNight);
		add(Data.birdUpDay);

		// Initializes the images for the running total digits so that it's not null
		// (Places it out of view)
		for (int i = 0; i < 10; i++) {
			Data.scoreDigits[i] = new GImage(Data.bigNums[0].getImage());
			Data.scoreDigits[i].setLocation(-100, 0);
			add(Data.scoreDigits[i]);
		}

		// Draw the initial "0"
		drawScore();

		// Set up keyboard and mouse
		addMouseListeners();
		addKeyListeners();

	}

	/** run Contains the Game Loop **/
	@Override
	public void run() {
		int groundOffset = 0;
		while (true) {

			// currentMode: 0 = Get Ready, 1 = Playing, 2 = Falling, 3 = Game Over

			if (FlappyBird.currentMode == 1 || FlappyBird.currentMode == 2) {

				bird.fly();

				// Checks if you hit the ground
				if (bird.getY() + bird.hoverCounter > FlappyBird.GROUND_LEVEL - Data.birdFlatDay.getHeight()) {
					Music.playSound("Music/falling.wav");
					bird.downwardSpeed = 0;
					endRound();
				}
			}

			// Playing
			if (FlappyBird.currentMode == 1) {

				movePipes();

				// Checks if you hit a pipe
				if (bird.pipeCollision()) {
					Music.playSound("Music/falling.wav");
					bird.downwardSpeed = Math.min(0, bird.downwardSpeed);
					currentMode = 2;
				}

			}

			// Animate the foreground
			if (FlappyBird.currentMode < 2) {

				Data.ground.setLocation(-groundOffset, FlappyBird.GROUND_LEVEL);
				groundOffset = (groundOffset + 4) % 24;

			}

			// Draw the bird with his flappy little wings
			if (FlappyBird.currentMode < 3)
				bird.draw(this);

			// This controls the speed of the game
			pause(bird.getY() / 4);

		}
	}

	/**
	 * If user presses either the space or w, the program invokes
	 * respondToUserInput()
	 **/
	@Override
	public void keyPressed(KeyEvent key) {

		char character = key.getKeyChar();

		if (character == ' ' || character == 'w')
			respondToUserInput();

	}

	/**
	 * If user clicks the mouse, it checks to see if the replay button was pressed,
	 * and if not, then it invokes respondToUserInput()
	 **/
	@Override
	public void mousePressed(MouseEvent mouse) {

		// Checks to see if click is on the white & green 'start' image
		if (FlappyBird.currentMode == 3) {
			if (mouse.getX() > 89 && mouse.getX() < 191 && mouse.getY() > 333 && mouse.getY() < 389)
				replayGame();
			return;
		}

		respondToUserInput();

	}

	/** Responds to user input by invoking flapWing() or changing the game state **/
	public void respondToUserInput() {
		// If the current mode is "Get Ready", it begins the game
		if (FlappyBird.currentMode == 0) {
			FlappyBird.currentMode = 1;
			remove(Data.getReady);
			remove(Data.instructions);
			remove(Data.birdLogo);

		}
		// If the current mode is "Playing", the flapping sound effect is played and it
		// ensures that the bird isn't above the top of the screen
		else if (FlappyBird.currentMode == 1) {
			bird.capHeight();
			Music.playSound("Music/flap.wav");
		}

	}

	/** Moves the pipes to the left, warping to the right side if needed **/
	public void movePipes() {
		for (int i = 0; i < 4; i++) {

			// Move pipes
			Data.pipeBottomDay[i].move(-4, 0);
			Data.pipeTopDay[i].move(-4, 0);
			Data.pipeBottomNight[i].move(-4, 0);
			Data.pipeTopNight[i].move(-4, 0);

			// Draw the score for the next pipe
			drawPipeScore(i, (int) Data.pipeBottomDay[i].getX() + (PIPE_WIDTH / 2) - 6);
			
			// Move pipe digits
				Data.pipeDigits[i][0].move(-4, 0);
				Data.pipeDigits[i][1].move(-4, 0);
			
			if (Data.pipeBottomDay[i].getX() == BIRD_X_START + 2) {
				// award points for each pipe that you pass
				 score = score + pipeSpaceAward[i];
				drawScore();

				// calls isNight every 250 points
				if ((int) score / 250 > scoreChange) {
					scoreChange = (int)score / 250;
					changeNight();
				}
				Music.playSound("Music/coinSound.wav");
			}

			// Re-spawns the pipe if they have already slid across the screen
			if (Data.pipeBottomDay[i].getX() < -(SCREEN_WIDTH / 2) - (PIPE_WIDTH / 2)) {
				Data.pipeTopDay[i].setLocation(SCREEN_WIDTH + (SCREEN_WIDTH / 2) - (PIPE_WIDTH / 2), -118);
				Data.pipeBottomDay[i].setLocation(SCREEN_WIDTH + (SCREEN_WIDTH / 2) - (PIPE_WIDTH / 2),
				(GROUND_LEVEL / 2));
				Data.pipeTopNight[i].setLocation(SCREEN_WIDTH + (SCREEN_WIDTH / 2) - (PIPE_WIDTH / 2), -118);
				Data.pipeBottomNight[i].setLocation(SCREEN_WIDTH + (SCREEN_WIDTH / 2) - (PIPE_WIDTH / 2),
				(GROUND_LEVEL / 2));
			// Move pipe digits
				randomizePipes(i);
			}

		}

	}

	//change night function
	public void changeNight() {
		isNight = !isNight;
		// Change bird
			//Day
			Data.birdUpDay.setVisible(isNight);
			Data.birdDownDay.setVisible(isNight);
			Data.birdFlatDay.setVisible(isNight);
			Data.birdDeadDay.setVisible(isNight);
			//Night
			Data.birdUpNight.setVisible(!isNight);
			Data.birdDownNight.setVisible(!isNight);
			Data.birdFlatNight.setVisible(!isNight);
			Data.birdDeadNight.setVisible(!isNight);


		Data.backgroundDay.setVisible(isNight);
		Data.backgroundNight.setVisible(!isNight);

		for (int i = 0; i < 4; i++) {

			// Change pipes
			//Day
			Data.pipeTopDay[i].setVisible(isNight);
			Data.pipeBottomDay[i].setVisible(isNight);
			//Night
			Data.pipeTopNight[i].setVisible(!isNight);
			Data.pipeBottomNight[i].setVisible(!isNight);

		}
	}

	/** Resets all 4 set of pipes to their starting locations **/
	public void resetPipes() {

		// Reset pipes
		for (int i = 0; i < 4; i++) {

			// Move pipes
			Data.pipeTopDay[i].setLocation(SCREEN_WIDTH * 2 + i * (SCREEN_WIDTH / 2) - (PIPE_WIDTH / 2), -118);
			Data.pipeBottomDay[i].setLocation(SCREEN_WIDTH * 2 + i * (SCREEN_WIDTH / 2) - (PIPE_WIDTH / 2),
					(GROUND_LEVEL / 2));
			Data.pipeTopNight[i].setLocation(SCREEN_WIDTH * 2 + i * (SCREEN_WIDTH / 2) - (PIPE_WIDTH / 2), -118);
			Data.pipeBottomNight[i].setLocation(SCREEN_WIDTH * 2 + i * (SCREEN_WIDTH / 2) - (PIPE_WIDTH / 2),
					(GROUND_LEVEL / 2));
			randomizePipes(i);
		}
	}

	/** Randomizes the given set of pipes **/
	public void randomizePipes(int i) {
		// generate random number between 25 and 100
		pipeSpace = (int) (Math.random() * (max - min + 1)) + min;
		// saves the award for the space between pipes
		pipeSpaceAward[i] = (max - pipeSpace);
		int randomAltitude = (int) (Math.random() * (GROUND_LEVEL / 2)) - 101;

		// moves the pipes to the new location
		Data.pipeTopDay[i].move(0, randomAltitude - pipeSpace);
		Data.pipeBottomDay[i].move(0, randomAltitude + pipeSpace);
		Data.pipeTopNight[i].move(0, randomAltitude - pipeSpace);
		Data.pipeBottomNight[i].move(0, randomAltitude + pipeSpace);

		// bottom of the top pipe saved to variable
		int bottomOfTopPipe = (int) Data.pipeTopDay[i].getY() + 320;
		// top of the bottom pipe saved to variable
		int topOfBottomPipe = (int) Data.pipeBottomDay[i].getY();
		// center of the space between the pipes	
		pipeSpaceCenter[i] = (bottomOfTopPipe + topOfBottomPipe) / 2;		

		// draws the score for the next pipe
	}

	/** Displays the graphics for the end of a round **/
	public void endRound() {

		// Remove elements from screen
		for (int i = 0; i < 4; i++) {
			for (int n = 0; n < 10; n++)
			remove(Data.pipeDigits[i][n]);

		}
	
		scoreChange = 0;
		//randomize night 

		FlappyBird.currentMode = 3;

		// Bird Day
		remove(Data.birdUpDay);
		remove(Data.birdDownDay);
		remove(Data.birdFlatDay);
		add(Data.birdDeadDay);
		Data.birdDeadDay.setSize(36.7 * 1.5, 36.7);


		// Bird Night
		remove(Data.birdUpNight);
		remove(Data.birdDownNight);
		remove(Data.birdFlatNight);
		add(Data.birdDeadNight);
		Data.birdDeadNight.setSize(36.7 * 1.5, 36.7);

		// Foreground
		add(Data.ground);

		// Other
		add(Data.gameOver);
		add(Data.scoreboard);
		add(Data.replayButton);

		// Award medal if applicable

		// Determines the interval for each medal
		if (score >= scoreInterval * 100)
			add(Data.birdMedal);
		else if (score >= scoreInterval * 40)
			add(Data.platinumMedal);
		else if (score >= scoreInterval * 30)
			add(Data.goldMedal);
		else if (score >= scoreInterval * 20)
			add(Data.silverMedal);
		else if (score >= scoreInterval * 10)
			add(Data.bronzeMedal);

		// Retrieve high score from file
		String strHighScore = highScoreFile.getHighScore();
		int highScore = Integer.parseInt(strHighScore);

		// Sets the users score to zero for trying to cheat
		if (highScoreFile.fileHasBeenManipulated()) {
			drawBoardScore(1, score);
			Data.new_.setLocation(-100, 0);
		}

		// Update HighScore
		else if (score > highScore) {
			highScoreFile.updateHighScore(Integer.toString(score));
			drawBoardScore(1, score);
			Data.new_.setLocation(164, 256);;
			// Draw old high score
		} else {
			drawBoardScore(1, highScore);
			Data.new_.setLocation(-100, 0);
		}

		add(Data.new_);
		drawBoardScore(0, score);

	}

	/** Resets game graphics **/
	public void replayGame() {
	
		// Remove elements from screen
		remove(Data.replayButton);
		remove(Data.gameOver);
		remove(Data.birdDeadDay);
		remove(Data.birdDeadNight);
		changeNight();

		// Adjust Variables
		bird.setY(240);
		bird.downwardSpeed = 0;
		bird.hoverCounter = 0;

		// Reset mode to "Get Ready"
		FlappyBird.currentMode = 0;

		// Game setup
		resetPipes();
		add(Data.getReady);
		if (score >= scoreInterval * 100){
			add(Data.birdLogo);
		}
		add(Data.instructions);

		// Remove elements from screen
		if (score >= scoreInterval * 100)
			remove(Data.birdMedal);
		else if (score >= scoreInterval * 40)
			remove(Data.platinumMedal);
		else if (score >= scoreInterval * 30)
			remove(Data.goldMedal);
		else if (score >= scoreInterval * 20)
			remove(Data.silverMedal);
		else if (score >= scoreInterval * 10)
			remove(Data.bronzeMedal);
		remove(Data.scoreboard);
		remove(Data.new_);

		// Reset score
		score = 0;
		initializeDigits(false);

		// Draw initial '0'
		drawScore();
	}

	/**
	 * Initializes the images for the running total digits so that it's not null,
	 * placing them out of view
	 **/
	public void initializeDigits(boolean initialCall) {

		for (int i = 0; i < 20; i++) {

			if (!initialCall)
				remove(Data.scoreBoardDigits[i]);
			Data.scoreBoardDigits[i] = new GImage(Data.medNums[0].getImage());
			Data.scoreBoardDigits[i].setLocation(-100, 0);
			add(Data.scoreBoardDigits[i]);

		}

	}

	/**
	 * Draws a number on the score-board
	 * 
	 * @parameter mode: This helps distinguish between the finalScore and the
	 *            highScore. Mode for finalScore is 0, and mode for highScore is 1
	 * @parameter points: You must pass in the number that you want drawn
	 **/
	protected void drawBoardScore(int mode, int points) {

		// Initialize variables
		boolean drawing = true;
		int startPoint = 235;

		// Remove previous digit and add the new one, placing it out of view if
		// neccesary
		for (int n = 0; n < 10; n++) {

			// Remove previous digit, and load the new image, properly adjusting the start
			// point
			remove(Data.scoreBoardDigits[n + mode * 10]);

			Data.scoreBoardDigits[n + mode * 10] = new GImage(Data.medNums[points % 10].getImage());
			startPoint -= Data.scoreBoardDigits[n + mode * 10].getWidth();

			// Determines when we've finished processing the score and we don't want to draw
			// anymore digits
			if (points == 0)
				drawing = false;

			// Find the proper location
			if (drawing || n == 0)
				Data.scoreBoardDigits[n + mode * 10].setLocation(startPoint - n, 232 + mode * 42);
				else	
				Data.scoreBoardDigits[n + mode * 10].setLocation(-100, 0);

			// Add to screen
			add(Data.scoreBoardDigits[n + mode * 10]);

			// Cut off the last digit
			points /= 10;
		}

	}

	// draws the score for the next pipe on the screen
	protected void drawPipeScore(int i, int x) {
		// Initialize variables
		int tempScore = pipeSpaceAward[i], widthScore = -1, digitCounter = 0;

		// Remove the previous score
		for (int n = 0; n < 2; n++) {
			remove(Data.pipeDigits[i][n]);
		}
		// Take the score one digit at a time (from right to left), and associate the
		// corresponding image of a number to that location in the array
		do {
			Data.pipeDigits[i][digitCounter] = new GImage(Data.medNums[tempScore % 10].getImage());
			widthScore += Data.medNums[tempScore % 10].getWidth() + 1;
			tempScore /= 10;
			digitCounter++;
		} while (tempScore > 0);

		// Draw the score on the scoreboard
		int startPoint = x - (widthScore / 2);
		// Draw the number on screen
		for (int n = 0; n < digitCounter; n++) {
			int index = digitCounter - n - 1;
			Data.pipeDigits[i][index].setLocation(startPoint + 8, pipeSpaceCenter[i] - 10);
			add(Data.pipeDigits[i][index]);
			startPoint += Data.pipeDigits[i][index].getWidth() + 1;
		}

	}
	

	/** Draws your current score on the screen **/
	protected void drawScore() {

		// Initialize variables
		int tempScore = score, widthScore = -1, digitCounter = 0;

		// Remove the previous score
		for (int n = 0; n < 10; n++) {
			remove(Data.scoreDigits[n]);
		}
		// Take the score one digit at a time (from right to left), and associate the
		// corresponding image of a number to that location in the array
		do {
			Data.scoreDigits[digitCounter] = new GImage(Data.bigNums[tempScore % 10].getImage());
			widthScore += Data.bigNums[tempScore % 10].getWidth() + 1;
			tempScore /= 10;
			digitCounter++;
		} while (tempScore > 0);

		// Draw the score on the scoreboard
		int startPoint = (SCREEN_WIDTH / 2) - (widthScore / 2);
		// Draw the number on screen
		for (int n = 0; n < digitCounter; n++) {
			int index = digitCounter - n - 1;
			Data.scoreDigits[index].setLocation(startPoint, 15);
			add(Data.scoreDigits[index]);
			startPoint += Data.scoreDigits[index].getWidth() + 1;
		}

	}

}
