import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import tester.*;
import javalib.funworld.*;
import javalib.colors.*;
import javalib.worldimages.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

class loadMegaman extends Component{
  BufferedImage img;
  public void paint(Graphics g){
    g.drawImage(img, 0, 0, null);
  }
  public loadMegaman(){
    try {
      img = ImageIO.read(new File("Megaman_sprite.PNG"));
    } catch (IOException e) {
    }
  }
  public Dimension resize(){
    if (img==null){
      return new Dimension(100,100);
    } else {
      return new Dimension(img.getWidth(null),img.getHeight(null));
    }
  }
}
class Player{
    
  Posn center;
  boolean running;
  int shooting;

  Player(Posn center, boolean running, int shooting){
    this.center = center;
    this.running = running;
    this.shooting = shooting;
  }
  // Generates an image of the player's block
  public WorldImage playerImage(){
    if(this.running){
      
      JFrame f = new JFrame("Load Image Sample");

      f.addWindowListener(new WindowAdapter(){
	  public void windowClosing(WindowEvent e) {
	    System.exit(0);
	  }
	});
      f.add(new loadMegaman());
      f.pack();
      f.setVisible(true);
    } else {
      
      JFrame f = new JFrame("Load Image Sample");

      f.addWindowListener(new WindowAdapter(){
	  public void windowClosing(WindowEvent e) {
	    System.exit(0);
	  }
	});
      f.add(new loadMegaman());
      f.pack();
      f.setVisible(true);
    }
  }
  // Moves the player's block based which arrow key is pressed
  public Player movePlayer(String ke){
    if (ke.equals("right")){
      return new Player(new Posn(this.center.x + 20, this.center.y),
			this.running,
			this.shooting);
    } else if (ke.equals("left")){
      return new Player(new Posn(this.center.x - 20, this.center.y),
			this.running,
			this.shooting);
    } else {   
      return this;
    }
  }
}
class Game2 extends World {
    
  static int width = 420;
  static int height = 600;
  Player player;
  WorldImage gameArena = new RectangleImage(new Posn((this.width / 2),
						     (this.height / 2)),
					    this.width,
					    this.height,
					    new Blue());
    
  public Game2 (Player player){
    this.player = player;
  }
  // Controls what happens in the game when a key is pressed
  public World onKeyEvent(String ke){
    // If the key "x" is pressed the game world ends
    if (ke.equals("x")){
      return this.endOfWorld("Aidos");
    } else {
      // If any other key is pressed feed that key into .moveBlock()
      // and run it on the current player block
      return new Game2 (this.player.movePlayer(ke));
    }
  }
  // Controls what happens on each tick of the game world
  public World onTick(){
    return new Game2(this.player);
  }
  // Overlays the images of each of the game objects 
  public WorldImage makeImage(){
    return new OverlayImages(
			     new OverlayImages(	     
					       new OverlayImages(this.gameArena,
								 // This rectangle provides a visual for the
								 // ground everywhere other than the goal
								 new RectangleImage(new Posn(210,590), 420, 20, new Black())),
					       this.player.playerImage()),
			     new TextImage(new Posn(300, 20), "Your score is " + this.score,Color.red)); 
  }
  // Determines under what conditions the game world ends
  public WorldEnd worldEnds(){
    // If the width of any of the platforms is greater than or equal to that
    // of the game arena end the game
    if (P1.width >= width || P2.width >= width || P3.width >= width){
      return 
	new WorldEnd(true,
		     new OverlayImages(this.makeImage(),
				       new TextImage(new Posn(210, 300),
						     "GAME OVER: A Platform Grew Too Wide", 
						     Color.red)));
    } else {
      // Otherwise don't end the game
      return new WorldEnd(false, this.makeImage());
    }
  }
  // Defines the initial setup of the game world and begins the game
  public static void main(String args[]){
    Game2 G = new Game2();
    G.bigBang(width, height, 0.2);
  }
}

