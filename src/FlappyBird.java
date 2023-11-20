
/** 
* Flappy Bird is a recreation of the popular mobile game created by Dong Nguyen.
* NOTE: All graphics and sound effects were not crundeated by us; however, all of the code was written by us.
* 
* @Author Micah Stairs, William Fiset
* @Version 2.0
* March 13, 2014
*/

import acm.graphics.*;
import acm.program.*;
import java.awt.event.*;

public class FlappyBird extends GraphicsProgram {

	final static int SCREEN_WIDTH = 288, SCREEN_HEIGHT = 512, GROUND_LEVEL = 400, PIPE_WIDTH = 52,
			BIRD1_X_START = 68, BIRD2_X_START = 68;

	Bird player1, player2; // The bird that the user controls
	FileHandler highScoreFile; // Used to save the high score to a file

	static int currentMode = 0; // 0 = Get Ready, 1 = Playing, 2 = Falling, 3 = Game Over
	static int score1, score2, total;
	boolean isNight = true; // true = night, false = day
	int scoreChange = 0; // needed to determin if night should change

	// award for the space between pipes
	// center of the space between pipes
	int[] pipeSpaceCenter1 = new int[4];
	int[] pipeSpaceCenter2 = new int[4];
	int[] distance1 = new int[4];
	int[] distance2 = new int[4];
	int[] topOfMiddlePipe = new int[4], bottomOfMiddlePipe = new int[4],
			bottomOfTopPipe =new int[4], topOfBottomPipe = new int[4];

	// space between pipes
	int pipeSpace = 0;
	int pipeCounter = 0;
	double totalPipeScore = 0;

	// min and max for the space between pipes
	int min = 150, max = 249;
	double scoreInterval = 0;

	@Override
	public void init() {

		setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

		// Loads images and sets their locations
		Data.init();
		initializeDigits(true);

		// Instantiate Bird
		player1 = new Bird(FlappyBird.BIRD1_X_START, 240);
		player2 = new Bird(FlappyBird.BIRD2_X_START, 240);

		// Creates a new fileHanlder to deal with saving to a file
		highScoreFile = new FileHandler();

		// Sets
		resetPipes();

		// randomize night
		isNight = (Math.random() < 0.5);
		changeNight();

		// Adds starting images to screen
		add(Data.backgroundNight);
		add(Data.backgroundDay);
		Data.backgroundDay.setLocation(0, 0);
		Data.backgroundNight.setLocation(0, 0);

		add(Data.backgroundNight2);
		add(Data.backgroundDay2);
		Data.backgroundDay2.setLocation(SCREEN_WIDTH, 0);
		Data.backgroundNight2.setLocation(SCREEN_WIDTH, 0);

		add(Data.backgroundNight3);
		add(Data.backgroundDay3);
		Data.backgroundDay3.setLocation(SCREEN_WIDTH * 2, 0);
		Data.backgroundNight3.setLocation(SCREEN_WIDTH * 2, 0);

		for (int i = 0; i < 4; i++) {
			// Adds pipes to screen
			add(Data.pipeTopDay[i]);
			add(Data.pipeBottomDay[i]);
			add(Data.pipeMiddleDay[i]);

			add(Data.pipeTopNight[i]);
			add(Data.pipeBottomNight[i]);
			add(Data.pipeMiddleNight[i]);
		}
		// add(Data.getReady);
		total = score1 + score2;
		if (total > (scoreInterval * 100)) { // if score is greater than 100
			add(Data.birdLogo);
		}
		//add(Data.instructions);
		add(Data.scoreboard);
		add(Data.player2Up);
		add(Data.player1Up);

		// Initializes the images for the running total digits so that it's not null
		// (Places it out of view)
		for (int n = 0; n < 10; n++) {
			// loop thru 3 scores
			for (int i = 0; i < 3; i++) {
				Data.scoreDigits[i][n] = new GImage(Data.bigNums[0].getImage());
				Data.scoreDigits[i][n].setLocation(-100, 0);
				add(Data.scoreDigits[i][n]);
			}
		}

		// Draw the initial "0"
		drawScore();

		// Set up keyboard and mouse
		addMouseListeners();
		addKeyListeners();

	}

	private void handlePipeCollision(Bird player) {
		if (player.pipeCollision()) {
			Music.playSound("Music/falling.wav");
			player.downwardSpeed = Math.min(0, player.downwardSpeed);
			currentMode = 2;
			endRound();
		}
	}

	private void moveAndWarpBackground(GImage background, GImage nextBackground) {
		background.move(-5, 0);
		if (background.getX() <= -SCREEN_WIDTH) {
			background.setLocation(nextBackground.getX() + SCREEN_WIDTH - 10, 0);
		}
	}
	
	public void moveBackground() {
		moveAndWarpBackground(Data.backgroundDay, Data.backgroundDay3);
		moveAndWarpBackground(Data.backgroundDay2, Data.backgroundDay);
		moveAndWarpBackground(Data.backgroundDay3, Data.backgroundDay2);
	
		moveAndWarpBackground(Data.backgroundNight, Data.backgroundNight3);
		moveAndWarpBackground(Data.backgroundNight2, Data.backgroundNight);
		moveAndWarpBackground(Data.backgroundNight3, Data.backgroundNight2);
	}

	/** run Contains the Game Loop **/
	@Override
	public void run() {
		while (true) {

			// currentMode: 0 = Get Ready, 1 = Playing, 2 = Falling, 3 = Game Over

			if (FlappyBird.currentMode == 1 || FlappyBird.currentMode == 2) {

				player1.fly();
				player2.fly();

				// Checks if player1 you hit the ground
				if (player1.getY() > SCREEN_HEIGHT - 30) {
					player1.setY(15);
					player1.downwardSpeed = 0;
				}
				// Checks if player 2you hit the ground
				if (player2.getY() > SCREEN_HEIGHT - 30) {
					player2.setY(15);
					player2.downwardSpeed = 0;
				}
			}

			// Playing
			if (FlappyBird.currentMode == 1) {

				movePipes();

				handlePipeCollision(player1);
				// handlePipeCollision(player2);

			}
			moveBackground();

			// Draw the bird with his flappy little wings
			if (FlappyBird.currentMode < 3)
				player1.draw(this, 1);
			player2.draw(this, 2);
			// This controls the speed of the game
			pause(40);

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
			respondToUserInput(1);

	}

	/**
	 * If user clicks the mouse, it checks to see if the replay button was pressed,
	 * and if not, then it invokes respondToUserInput()
	 **/
	@Override
	public void mousePressed(MouseEvent mouse) {
		// Checks to see if click is on the white & green 'start' image
		if (FlappyBird.currentMode == 3) {
			// Assuming replayButton is a GImage or similar object with getX, getY, getWidth, and getHeight methods
			if (mouse.getX() > Data.replayButton.getX() && 
			    mouse.getX() < Data.replayButton.getX() + Data.replayButton.getWidth() && 
				mouse.getY() > Data.replayButton.getY() && 
				mouse.getY() < Data.replayButton.getY() + Data.replayButton.getHeight()) {
				replayGame();
			}
			return;
		}
		respondToUserInput(2);
	}

	/** Responds to user input by invoking flapWing() or changing the game state **/
	public void respondToUserInput(int player) {
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
			if (player == 1)
				player1.capHeight();
			else if (player == 2)
				player2.capHeight();
			Music.playSound("Music/flap.wav");
			// if (player1.getY() > 0){
			// 	player1.downwardSpeed = -10;
			// 	//set to bottom of screen
			// 	player1.setY(SCREEN_HEIGHT - 30);
			// }
		}

	}

	private void calculateScore(Bird player, int scoreIndex, int i) {
		int[] scores = { score1, score2 };
		int[] distances1 = { distance1[i], distance2[i] };
		int[] distances2 = { distance2[i], distance1[i] };

		if (player.getY() < topOfMiddlePipe[i] + 55.5) {
			scores[scoreIndex - 1] += 100 - distances1[scoreIndex - 1];
		} else if (player.getY() > bottomOfMiddlePipe[i] - 55.5) {
			scores[scoreIndex - 1] += 100 - distances2[scoreIndex - 1];
		}

		// Update the original score variables
		score1 = scores[0];
		score2 = scores[1];
	}

	/** Moves the pipes to the left, warping to the right side if needed **/
	public void movePipes() {

		for (int i = 0; i < 4; i++) {

			// Move pipes
			Data.pipeBottomDay[i].move(-4, 0);
			Data.pipeTopDay[i].move(-4, 0);
			Data.pipeMiddleDay[i].move(-4, 0);

			Data.pipeBottomNight[i].move(-4, 0);
			Data.pipeTopNight[i].move(-4, 0);
			Data.pipeMiddleNight[i].move(-4, 0);

			// Draw the score for the next pipe
			drawPipeScore(i, (int) Data.pipeBottomDay[i].getX() + (PIPE_WIDTH / 2) - 6);

			// Move pipe digits
			Data.pipeDigits1[i][0].move(-4, 0);
			Data.pipeDigits1[i][1].move(-4, 0);
			Data.pipeDigits2[i][0].move(-4, 0);
			Data.pipeDigits2[i][1].move(-4, 0);

			if (Data.pipeBottomDay[i].getX() == BIRD1_X_START + 2) {

				// award score for each pipe that you pass
				calculateScore(player1, 1, i);
				calculateScore(player2, 2, i);

				// update average score between pipes
				pipeCounter += 2;
				totalPipeScore += distance1[i] + distance2[i];
				scoreInterval = totalPipeScore / pipeCounter;

				total = score1 + score2; // total score
				drawScore();
				// calls isNight every 250 points
				if ((int) total / 500 > scoreChange) {
					scoreChange = (int) (total) / 500;
					changeNight();
				}
				Music.playSound("Music/coinSound.wav");
			}

			// Re-spawns the pipe if they have already slid across the screen
			if (Data.pipeBottomDay[i].getX() < -(SCREEN_WIDTH / 2) - (PIPE_WIDTH / 2)) {
				respawnPipe(Data.pipeTopDay[i], Data.pipeBottomDay[i], Data.pipeMiddleDay[i]);
				respawnPipe(Data.pipeTopNight[i], Data.pipeBottomNight[i], Data.pipeMiddleNight[i]);
				// Move pipe digits
				randomizePipes(i);
				findPipeCenters(i);
			}

		}

	}

	private void respawnPipe(GImage pipeTop, GImage pipeBottom, GImage pipeMiddle) {
		double locationX = SCREEN_WIDTH + (SCREEN_WIDTH / 2) - (PIPE_WIDTH / 2);
		double locationYTop = -118;
		double locationYBottom = (GROUND_LEVEL / 2);
		double locationYMiddle = (GROUND_LEVEL / 2) - 118;

		pipeTop.setLocation(locationX, locationYTop);
		pipeBottom.setLocation(locationX, locationYBottom);
		pipeMiddle.setLocation(locationX, locationYMiddle);
	}

private void changeVisibility(boolean isVisible, GImage... images) {
    for (GImage image : images) {
        if (image != null) {
            image.setVisible(isVisible);
        }
    }
}

	// change night function
	public void changeNight() {
		isNight = !isNight;

		// Change background
		changeVisibility(isNight, Data.backgroundDay, Data.backgroundDay2, Data.backgroundDay3);
		changeVisibility(!isNight, Data.backgroundNight, Data.backgroundNight2, Data.backgroundNight3);

		for (int i = 0; i < 4; i++) {
			// Change pipes
			changeVisibility(isNight, Data.pipeTopDay[i], Data.pipeBottomDay[i], Data.pipeMiddleDay[i]);
			changeVisibility(!isNight, Data.pipeTopNight[i], Data.pipeBottomNight[i], Data.pipeMiddleNight[i]);
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
			// create middle pipe between top and bottom
			Data.pipeMiddleDay[i].setLocation(SCREEN_WIDTH * 2 + i * (SCREEN_WIDTH / 2) - (PIPE_WIDTH / 2),
					(GROUND_LEVEL / 2) - 118);

			Data.pipeTopNight[i].setLocation(SCREEN_WIDTH * 2 + i * (SCREEN_WIDTH / 2) - (PIPE_WIDTH / 2), -118);
			Data.pipeBottomNight[i].setLocation(SCREEN_WIDTH * 2 + i * (SCREEN_WIDTH / 2) - (PIPE_WIDTH / 2),
					(GROUND_LEVEL / 2));
			// create middle pipe between top and bottom
			Data.pipeMiddleNight[i].setLocation(SCREEN_WIDTH * 2 + i * (SCREEN_WIDTH / 2) - (PIPE_WIDTH / 2),
					(GROUND_LEVEL / 2) - 118);

			randomizePipes(i);
			findPipeCenters(i);
		}
	}

	// find the center spaces between the pipes:
	public void findPipeCenters(int i) {

		// y location OF bottom of top pipe or top of screen which ever is lower
		bottomOfTopPipe[i] = (int) Math.max(Data.pipeTopDay[i].getY() + 320, 0);

		// y location OF top of bottom pipe or the ground whichever is higher
		topOfBottomPipe[i] = (int) Math.min(Data.pipeBottomDay[i].getY(), SCREEN_HEIGHT);

		// y of top of middle pipe
		topOfMiddlePipe[i] = (int) Data.pipeMiddleDay[i].getY();
		// y location of bottom of middle pipe
		bottomOfMiddlePipe[i] = (int) Data.pipeMiddleDay[i].getY() + 111;
		// center of space between bottomOfTopPipe and topOfMiddlePipe

		pipeSpaceCenter1[i] = (bottomOfTopPipe[i] + topOfMiddlePipe[i]) / 2;
		// center of space between bottomOfMiddlePipe and topOfBottomPipe
		pipeSpaceCenter2[i] = (bottomOfMiddlePipe[i] + topOfBottomPipe[i]) / 2;
		// distance between bottomOfTopPipe and topOfMiddlePipe
		distance1[i] = (topOfMiddlePipe[i] - bottomOfTopPipe[i]) / 2;
		// distance between bottomOfMiddlePipe and topOfBottomPipe
		distance2[i] = (topOfBottomPipe[i] - bottomOfMiddlePipe[i]) / 2;

	}

	/** Randomizes the given set of pipes **/
	public void randomizePipes(int i) {
		// generate random number between 25 and 100
		pipeSpace = (int) (Math.random() * (max - min + 1)) + min;
		// saves the award for the space between pipes
		int randomAltitude = (int) (Math.random() * (GROUND_LEVEL / 2)) - 101;

		// moves the pipes to the new location
		Data.pipeTopDay[i].move(0, randomAltitude - pipeSpace);
		Data.pipeBottomDay[i].move(0, randomAltitude + pipeSpace);
		// create middle pipe centered exactly in the middle of the top and bottom
		Data.pipeMiddleDay[i].move(0, randomAltitude + 56);

		Data.pipeTopNight[i].move(0, randomAltitude - pipeSpace);
		Data.pipeBottomNight[i].move(0, randomAltitude + pipeSpace);

		// create middle pipe centered exactly in the middle of the top and bottom
		Data.pipeMiddleNight[i].move(0, randomAltitude + 56);
	}

	// function to move birds off screen
	public void moveBirdsOffScreen() {
		// Player 1
		Data.player1Flat.setLocation(-100, 0);
		Data.player1Down.setLocation(-100, 0);
		Data.player1Up.setLocation(-100, 0);

		// //Player 2
		Data.player2Flat.setLocation(-100, 0);
		Data.player2Down.setLocation(-100, 0);
		Data.player2Up.setLocation(-100, 0);
	}

	/** Displays the graphics for the end of a round **/
	public void endRound() {

		// Remove elements from screen
		for (int i = 0; i < 4; i++) {
			for (int n = 0; n < 10; n++) {
				remove(Data.pipeDigits1[i][n]);
				remove(Data.pipeDigits2[i][n]);
			}
		}
		moveBirdsOffScreen();

		// randomize night

		FlappyBird.currentMode = 3;
		player1.animationCounter = 0;
		player2.animationCounter = 0;
		// remove the birds

		// Other
		add(Data.gameOver);
		add(Data.scoreboard);
		add(Data.replayButton);

		// Award medal if applicable

		// Determines the interval for each medal
		total = (score1 + score2); // total score
		if (total > scoreInterval * 100)
			add(Data.birdMedal);
		else if (total > scoreInterval * 40)
			add(Data.platinumMedal);
		else if (total > scoreInterval * 30)
			add(Data.goldMedal);
		else if (total > scoreInterval * 20)
			add(Data.silverMedal);
		else if (total > scoreInterval * 10)
			add(Data.bronzeMedal);

		// Retrieve high score from file
		String strHighScore = highScoreFile.getHighScore();
		int highScore = Integer.parseInt(strHighScore);

		// Sets the users score to zero for trying to cheat
		if (highScoreFile.fileHasBeenManipulated()) {
			drawBoardScore(1, highScore);
			Data.new_.setLocation(-100, 0);
		}

		// Update HighScore
		else if (total > highScore) {
			highScoreFile.updateHighScore(Integer.toString(total));
			drawBoardScore(1, total);
			Data.new_.setLocation(Data.backgroundDay.getWidth() + 20, 256);
			;
			// Draw old high score
		} else {
			drawBoardScore(1, highScore);
			Data.new_.setLocation(-100, 0);
		}

		add(Data.new_);
		drawBoardScore(0, total);

	}

	/** Resets game graphics **/
	public void replayGame() {
		// Remove elements from screen
		remove(Data.replayButton);
		remove(Data.gameOver);
		changeNight();

		// Adjust Variables
		player1.setY(240);
		player2.setY(240);

		player1.downwardSpeed = 0;
		player2.downwardSpeed = 0;

		// Reset mode to "Get Ready"
		FlappyBird.currentMode = 0;

		// Game setup
		resetPipes();
		add(Data.getReady);
		total = (score1 + score2); // total score
		if (total > scoreInterval * 100) {
			add(Data.birdLogo);
		}
		add(Data.instructions);

		// Remove elements from screen
		if (total > scoreInterval * 100)
			remove(Data.birdMedal);
		else if (total > scoreInterval * 40)
			remove(Data.platinumMedal);
		else if (total > scoreInterval * 30)
			remove(Data.goldMedal);
		else if (total > scoreInterval * 20)
			remove(Data.silverMedal);
		else if (total > scoreInterval * 10)
			remove(Data.bronzeMedal);
		remove(Data.scoreboard);
		remove(Data.new_);

		// Reset score
		score1 = 0; // player 1
		total = 0; // total
		score2 = 0; // player 2
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
		int startPoint = (int) Data.backgroundDay.getWidth() + 92;

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

	// function that draws the score for the next pipe spaces on the screen distance
	// for
	// the top and distance two for the bottom - it centers the dirrectly
	// in the middle of the space between the pipes (two diffrent numbers)
	// one for the two opening and one for teh bttom opening
	protected void drawPipeScore(int i, int x) {
		GImage[][][] pipeDigitsArray = { Data.pipeDigits1, Data.pipeDigits2 };
		int[] pipeSpaceCenterArray = { pipeSpaceCenter1[i], pipeSpaceCenter2[i] };
		int[] distances = { 100 - distance1[i], 100 - distance2[i] };

		for (int j = 0; j < 2; j++) {
			// Initialize variables
			int tempScore = distances[j];
			int widthScore = -1, digitCounter = 0;

			// Remove the previous score
			for (int n = 0; n < 2; n++) {
				remove(pipeDigitsArray[j][i][n]);
			}
			// Take the score one digit at a time (from right to left), and associate the
			// corresponding image of a number to that location in the array
			do {
				pipeDigitsArray[j][i][digitCounter] = new GImage(Data.medNums[tempScore % 10].getImage());
				widthScore += Data.medNums[tempScore % 10].getWidth() + 1;
				tempScore /= 10;
				digitCounter++;
			} while (tempScore > 0);

			// Draw the score on the scoreboard
			int startPoint = x - (widthScore / 2);
			// Draw the number on screen
			for (int n = 0; n < digitCounter; n++) {
				int index = digitCounter - n - 1;
				pipeDigitsArray[j][i][index].setLocation(startPoint + 8, pipeSpaceCenterArray[j] - 8);
				add(pipeDigitsArray[j][i][index]);
				startPoint += pipeDigitsArray[j][i][index].getWidth() + 1;
			}
		}
	}

	/** Draws your current score on the screen **/
	protected void drawScore() {
		// loop thru two scores
		int[] score = { score1, total, score2 };
		int[][] drawLocation = { { 85, SCREEN_WIDTH - 85, 85 }, { 425, 425, 447 } };
		for (int i = 0; i < 3; i++) {
			// Initialize variables
			int widthScore = -1, digitCounter = 0;

			// Remove the previous score
			for (int n = 0; n < 10; n++) {
				remove(Data.scoreDigits[i][n]);
			}
			// Take the score one digit at a time (from right to left), and associate the
			// corresponding image of a number to that location in the array
			do {
				if (i == 1) {
					Data.scoreDigits[i][digitCounter] = new GImage(Data.bigNums[score[i] % 10].getImage());
				} else {
					Data.scoreDigits[i][digitCounter] = new GImage(Data.medNums[score[i] % 10].getImage());
				}
				widthScore += Data.bigNums[score[i] % 10].getWidth() + 1;
				score[i] /= 10;
				digitCounter++;
			} while (score[i] > 0);

			// Draw the score on the scoreboard
			int startPoint = drawLocation[0][i] - (widthScore / 2);
			// Draw the number on screen
			for (int n = 0; n < digitCounter; n++) {
				int index = digitCounter - n - 1;
				Data.scoreDigits[i][index].setLocation(startPoint, drawLocation[1][i]);
				add(Data.scoreDigits[i][index]);
				startPoint += Data.scoreDigits[i][index].getWidth() + 1;
			}

		}

	}
}
