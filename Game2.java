import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import tester.*;
import javalib.funworld.*;
import javalib.colors.*;
import javalib.worldimages.*;
import java.io.*;


class Player{
    
  Posn center;
  String facing;
  int running;
  int shooting;

  Player(Posn center, String facing, int running, int shooting){
    this.center = center;
    this.facing = facing;
    this.running = running;
    this.shooting = shooting;
  }
  
  // Generates an image of the player's character
  public FromFileImage playerDraw(){
    if (this.facing.equals("right")){
	if (this.running==1){
	  return new FromFileImage (this.center,"Running1.PNG");
	} else if (this.running==2 || this.running==4){
	  return new FromFileImage (this.center,"Running2.PNG");
	} else if (this.running==3){
	  return new FromFileImage (this.center,"Running3.PNG");
	} else {
	  return new FromFileImage (this.center,"Standing.PNG");
	}
      } else {
      if (this.running==1){
	return new FromFileImage (this.center,"LRunning1.PNG");
      } else if (this.running==2 || this.running==4){
	return new FromFileImage (this.center,"LRunning2.PNG");
      } else if (this.running==3){
	return new FromFileImage (this.center,"LRunning3.PNG");
      } else {
	return new FromFileImage (this.center,"LStanding.PNG");
      }
    }
  }
      
  // Moves the player's block based which arrow key is pressed
  public Player movePlayer(String ke){
    if (this.running==4){
      this.running=0;
    }
    if (ke.equals("right")){
      return new Player(new Posn(this.center.x + 10, this.center.y),
			"right",
			this.running+1,
			this.shooting);
    } else if (ke.equals("left")){
      return new Player(new Posn(this.center.x - 10, this.center.y),
			"left",
			this.running+1,
			this.shooting);
    } else {
      return new Player(this.center,this.facing,0,0);
    }
  }      
}
class Game2 extends World {
    
  static int width = 1000;
  static int height = 400;
  Player player;
  WorldImage gameArena = new RectangleImage(new Posn((this.width / 2),
						     (this.height / 2)),
					    this.width,
					    this.height,
					    new White());
    
  public Game2 (Player player){
    this.player = player;
  }
  // Controls what happens in the game when a key is pressed
  public World onKeyEvent(String ke){
    // If the key "x" is pressed the game world ends
    if (ke.equals("x")){
      return this.endOfWorld("Aidos");
    } else if (ke.equals("left")||ke.equals("right")) {
      return new Game2 (this.player.movePlayer(ke));
    } else {
      return new Game2(new Player (this.player.center,
				   this.player.facing,
				   0,0));
    }
  }
  // Controls what happens on each tick of the game world
  public World onTick(){
    return new Game2(this.player);
  }
  // Overlays the images of each of the game objects 
  public WorldImage makeImage(){
    return  new OverlayImages(	     
		new OverlayImages(
		    new OverlayImages(this.gameArena,
				  new RectangleImage(new Posn(500,390),
						     1000, 20, new Black())),
		    this.player.playerDraw()),
	     new TextImage(new Posn(300, 20), "Running is " + this.player.running,Color.red)); 
  }
  // Determines under what conditions the game world ends
  public WorldEnd worldEnds(){
    if (false){
      return 
	new WorldEnd(true,this.makeImage());
    } else {
      // Otherwise don't end the game
      return new WorldEnd(false, this.makeImage());
    }
  }
  // Defines the initial setup of the game world and begins the game
  public static void main(String args[]){
    Game2 G = new Game2(new Player (new Posn (50,365),"right",0,0));
    G.bigBang(width, height, 0.2);
  }
}

