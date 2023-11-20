/** 
  * GameStarter - Starts the Flappy Bird application by putting the Applet into a JFrame.
  * 
  * @Author William Fiset, Micah Stairs
  * @Version 2.0
  * March 13, 2014
  */

import java.awt.*;
import javax.swing.*;

public class GameStarter{

    public static void main(String[] args) {
        
        FlappyBird flappyBirdApplet = new FlappyBird();
        JFrame appletFrame = new JFrame();

        // Must call init() method for Applet 
        flappyBirdApplet.init();

        // Set the layout of the JFrame to CardLayout
        appletFrame.setLayout(new CardLayout());

        // Add Applet to Frame and start
        appletFrame.add(flappyBirdApplet, "FlappyBird");
        flappyBirdApplet.start();

        // Set default Program values
        appletFrame.setResizable(false);
        appletFrame.setSize(FlappyBird.SCREEN_WIDTH, FlappyBird.SCREEN_HEIGHT);
        appletFrame.setVisible(true); 
        appletFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

        // Center Frame to the middle of screen on start
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        int screenStartX = screenDimension.width/2 - appletFrame.getSize().width/2;
        int screenStartY = screenDimension.height/2 - appletFrame.getSize().height/2;
        appletFrame.setLocation(screenStartX, screenStartY);
        
        // Add title
        appletFrame.setTitle("StrataBird - Wade");

    }
}