
/**
*	Data - Loads images from a sprite-sheet and sets their location on the screen if applicable.
* 
* @Author Micah Stairs, William Fiset
* March 13, 2014
**/

import acm.graphics.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public abstract class Data {

	public static BufferedImage fullImage = null;

	public static GImage ground, birdLogo, birdMedal,
			player1Flat, player1Up, player1Down, player1Dead,
			player2Flat, player2Up, player2Down, player2Dead,
			backgroundDay, backgroundNight,
			getReady, gameOver, instructions, scoreboard, replayButton,
			bronzeMedal, silverMedal, goldMedal, platinumMedal, new_;

	public static GImage[] pipeTopDay = new GImage[4], pipeBottomDay = new GImage[4],
			pipeTopNight = new GImage[4], pipeBottomNight = new GImage[4],
			medNums = new GImage[10], bigNums = new GImage[10],
			scoreBoardDigits = new GImage[20],
			pipeMiddleDay = new GImage[4], pipeMiddleNight = new GImage[4];

	public static GImage[][] pipeDigits1 = new GImage[4][10],
			pipeDigits2 = new GImage[4][10], scoreDigits = new GImage[4][10];

	/** Loads and sets the location of all the images **/
	public static void init() {

		Data.loadImages();
		Data.setLocations();

	}

	/** Loads all the images from the sprite sheet **/
	public static void loadImages() {

		// Import sprite-sheet
		try {
			fullImage = ImageIO.read(new File("Images/flappyBirdGraphics.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Background
		Data.backgroundDay = makeImage(fullImage, 0, 0, 288, 512);
		Data.backgroundNight = makeImage(fullImage, 292, 0, 580, 512);

		// Pipes
		for (int i = 0; i < 4; i++) {
			// Green pipes (for day mode)
			Data.pipeTopNight[i] = makeImage(fullImage, 112, 646, 164, 965);
			Data.pipeBottomNight[i] = makeImage(fullImage, 168, 646, 220, 965);
			Data.pipeMiddleNight[i] = makeImage(fullImage, 300, 869, 352, 980);

			// Red pipes on the sprite image to the far left of the image
			Data.pipeTopDay[i] = makeImage(fullImage, 0, 646, 52, 965);
			Data.pipeBottomDay[i] = makeImage(fullImage, 56, 644, 108, 963);
			Data.pipeMiddleDay[i] = makeImage(fullImage, 358, 869, 410, 980);

			// Score Digits
			for (int n = 0; n < 10; n++) {
				Data.scoreDigits[i][n] = makeImage(fullImage, 274, 612, 288, 632);
				Data.pipeDigits1[i][n] = makeImage(fullImage, 274, 612, 288, 632);
				Data.pipeDigits2[i][n] = makeImage(fullImage, 274, 612, 288, 632);
			}
		}

		// Score Board
		Data.medNums[0] = makeImage(fullImage, 274, 612, 288, 632);
		Data.medNums[1] = makeImage(fullImage, 278, 954, 288, 974);
		Data.medNums[2] = makeImage(fullImage, 274, 978, 288, 998);
		Data.medNums[3] = makeImage(fullImage, 262, 1002, 276, 1022);
		Data.medNums[4] = makeImage(fullImage, 1004, 0, 1018, 20);
		Data.medNums[5] = makeImage(fullImage, 1004, 24, 1018, 44);
		Data.medNums[6] = makeImage(fullImage, 1010, 52, 1024, 72);
		Data.medNums[7] = makeImage(fullImage, 1010, 84, 1024, 104);
		Data.medNums[8] = makeImage(fullImage, 586, 484, 600, 504);
		Data.medNums[9] = makeImage(fullImage, 622, 412, 636, 432);

		// Running Total
		Data.bigNums[0] = makeImage(fullImage, 992, 120, 1016, 156);
		Data.bigNums[1] = makeImage(fullImage, 272, 910, 288, 946);
		Data.bigNums[2] = makeImage(fullImage, 584, 320, 608, 356);
		Data.bigNums[3] = makeImage(fullImage, 612, 320, 636, 356);
		Data.bigNums[4] = makeImage(fullImage, 640, 320, 664, 356);
		Data.bigNums[5] = makeImage(fullImage, 668, 320, 692, 356);
		Data.bigNums[6] = makeImage(fullImage, 584, 368, 608, 404);
		Data.bigNums[7] = makeImage(fullImage, 612, 368, 636, 404);
		Data.bigNums[8] = makeImage(fullImage, 640, 368, 664, 404);
		Data.bigNums[9] = makeImage(fullImage, 668, 368, 692, 404);

		// Foreground
		Data.ground = makeImage(fullImage, 584, 0, 919, 111);

		// Bird Parts
		// (red for the player 1)
		Data.player1Up = makeImage(fullImage, 230, 762, 263, 785);
		Data.player1Flat = makeImage(fullImage, 230, 814, 263, 837);
		Data.player1Down = makeImage(fullImage, 230, 866, 263, 889);
		Data.player1Dead = makeImage(fullImage, 367, 982, 397, 1020);
		// (green for the player 2)
		Data.player2Up = makeImage(fullImage, 6, 983, 39, 1006);
		Data.player2Flat = makeImage(fullImage, 62, 983, 95, 1006);
		Data.player2Down = makeImage(fullImage, 118, 983, 151, 1006);
		Data.player2Dead = makeImage(fullImage, 297, 982, 327, 1020);

		// Misc.
		Data.getReady = makeImage(fullImage, 584, 116, 780, 180);
		Data.instructions = makeImage(fullImage, 584, 175, 700, 283);
		Data.gameOver = makeImage(fullImage, 780, 116, 990, 180);
		Data.replayButton = makeImage(fullImage, 705, 234, 816, 303);
		Data.birdLogo = makeImage(fullImage, 708, 300, 840, 775);

		// Score Board
		Data.scoreboard = makeImage(fullImage, 0, 515, 237, 643);
		Data.bronzeMedal = makeImage(fullImage, 224, 954, 268, 998);
		Data.silverMedal = makeImage(fullImage, 224, 906, 268, 950);
		Data.goldMedal = makeImage(fullImage, 242, 564, 286, 608);
		Data.platinumMedal = makeImage(fullImage, 242, 516, 286, 560);
		Data.new_ = makeImage(fullImage, 224, 1002, 256, 1016);
		// make birdmedal 1/3 the zie of birdlogo
		Data.birdMedal = makeImage(fullImage, 708, 300, 840, 775);
		Data.birdMedal.setSize(Data.birdMedal.getWidth() / 3, Data.birdMedal.getHeight() / 3);
	}

	/** Set the location of images **/
	private static void setLocations() {

		// Foreground
		Data.ground.setLocation(0, 400);

		// Bird Parts
		// Player 1
		Data.player1Flat.setLocation(-100, 0);
		Data.player1Down.setLocation(-100, 0);
		Data.player1Up.setLocation(-100, 0);
		Data.player2Dead.setLocation(FlappyBird.BIRD1_X_START, 371);
		// Player 2
		Data.player2Flat.setLocation(-100, 0);
		Data.player2Down.setLocation(-100, 0);
		Data.player2Up.setLocation(-100, 0);
		Data.player2Dead.setLocation(FlappyBird.BIRD2_X_START, 371);

		// Misc.
		Data.getReady.setLocation(45, 170);
		Data.instructions.setLocation(80, 234);
		Data.birdLogo.setLocation(55, 60);
		Data.gameOver.setLocation(40, 100);
		Data.replayButton.setLocation(85, 330);

		// Score Board
		Data.scoreboard.setLocation(25, 195);
		Data.bronzeMedal.setLocation(57, 240);
		Data.silverMedal.setLocation(57, 240);
		Data.goldMedal.setLocation(57, 240);
		Data.platinumMedal.setLocation(57, 240);
		Data.birdMedal.setLocation(55, 245);
	}

	/** Used to help get the sub-images from the sprite-sheet **/
	protected static GImage makeImage(BufferedImage i, int xStart, int yStart, int xEnd, int yEnd) {
		return new GImage(i.getSubimage(xStart, yStart, xEnd - xStart, yEnd - yStart));
	}

}