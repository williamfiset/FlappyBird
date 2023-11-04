
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

public abstract class Data{

		public static BufferedImage fullImage = null;

		public static GImage background, ground,
					     birdFlat, birdUp, birdDown, birdDead,
					     getReady, gameOver, instructions, scoreboard, replayButton,
					     bronzeMedal, silverMedal, goldMedal, platinumMedal, new_;

		public static GImage[] pipeTop = new  GImage[4], pipeBottom = new GImage[4],
				medNums = new  GImage[10], bigNums = new GImage[10],  
				scoreDigits = new GImage[10], scoreBoardDigits = new GImage[20];
				
		public static GImage[][] pipeDigits = new GImage[4][10];
	    
	  /** Loads and sets the location of all the images **/
		public static void init(){

			Data.loadImages();
			Data.setLocations();

		}
		
		/** Loads all the images from the sprite sheet **/
		public static void loadImages(){
			
			// Import sprite-sheet
			try { fullImage = ImageIO.read(new File("Images/flappyBirdGraphics.png"));	}
			catch (IOException e) {	e.printStackTrace(); }
			
			// Background
			Data.background = makeImage(fullImage, 0, 0, 288, 512);

			// Pipes
			for(int i = 0; i < 4; i++){
				Data.pipeTop[i] = makeImage(fullImage, 112, 646, 164, 965);
				Data.pipeBottom[i] = makeImage(fullImage, 168, 646, 220, 965);
				for (int n = 0; n < 10; n++){
				Data.pipeDigits[i][n] = makeImage(fullImage, 274, 612, 288, 632);
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
			Data.birdUp = makeImage(fullImage, 6, 982, 39, 1005);
			Data.birdFlat = makeImage(fullImage, 62, 982, 95, 1005);
			Data.birdDown = makeImage(fullImage, 118, 982, 151, 1005);
			Data.birdDead = makeImage(fullImage, 297, 983, 327, 1020);

			// Misc.
			Data.getReady = makeImage(fullImage, 584, 116, 780, 180);
			Data.instructions = makeImage(fullImage, 584, 175, 700, 283);
			Data.gameOver = makeImage(fullImage, 780, 116, 990, 180);
			Data.replayButton = makeImage(fullImage, 705, 234, 816, 303);

			// Score Board
			Data.scoreboard = makeImage(fullImage, 0, 515, 237, 643);
			Data.bronzeMedal = makeImage(fullImage, 224, 954, 268, 998);
			Data.silverMedal = makeImage(fullImage, 224, 906, 268, 950);
			Data.goldMedal = makeImage(fullImage, 242, 564, 286, 608);
			Data.platinumMedal = makeImage(fullImage, 242, 516, 286, 560);
			Data.new_ = makeImage(fullImage, 224, 1002, 256, 1016);

		}
		
		/** Set the location of images **/
		private static void setLocations(){
			
			// Foreground
			Data.ground.setLocation(0, 400);
			
			// Bird
			Data.birdFlat.setLocation(-100, 0);
			Data.birdDown.setLocation(-100, 0);
			Data.birdUp.setLocation(-100, 0);
			Data.birdDead.setLocation(70, 371);
			
			// Misc.
			Data.getReady.setLocation(45, 130);
			Data.instructions.setLocation(85, 210);
			Data.gameOver.setLocation(40, 130);
			Data.replayButton.setLocation(85, 330);

			// Score Board
			Data.scoreboard.setLocation(25, 195);
			Data.bronzeMedal.setLocation(57, 240);
			Data.silverMedal.setLocation(57, 240);
			Data.goldMedal.setLocation(57, 240);
			Data.platinumMedal.setLocation(57, 240);

		}
		
		/** Used to help get the sub-images from the sprite-sheet **/
		protected static GImage makeImage(BufferedImage i, int xStart, int yStart, int xEnd, int yEnd){
			return new GImage(i.getSubimage(xStart, yStart, xEnd - xStart, yEnd - yStart));
		}

}