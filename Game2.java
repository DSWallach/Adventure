import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import tester.*;
import javalib.funworld.*;
import javalib.colors.*;
import javalib.worldimages.*;
import java.io.*;

class Weapon{
  Posn center;
  String type;
  String facing;
  Weapon(Posn center, String type,String facing){
    this.center = center;
    this.type = type;
    this.facing = facing;
  }
  public FromFileImage weaponDraw(){
    if(this.type.equals("null")){
      return new FromFileImage (new Posn (10000,10000), "BusterShot.PNG");
    } else {
      return new FromFileImage (this.center,"BusterShot.PNG");
    }
  }
  public Weapon moveWeapon(){
    if (this.facing.equals("right")){
      return new Weapon(new Posn(this.center.x + 10,this.center.y),
			this.type,this.facing);
    } else {
      return new Weapon(new Posn(this.center.x - 10,this.center.y),
			this.type,this.facing);
    }
  }
}
class Player{
    
  Posn center;
  String facing;
  int running;
  int shooting;
  int jumping;

  Player(Posn center, String facing, int running, int shooting, int jumping){
    this.center = center;
    this.facing = facing;
    this.running = running;
    this.shooting = shooting;
    this.jumping = jumping;
  }
  
  // Generates an image of the player's character
  public FromFileImage playerDraw(){
    if (this.facing.equals("right")){
      if (this.shooting==1){
	if (this.running==1){
	  return new FromFileImage (this.center,"Shooting1.PNG");
	} else if (this.running==2 || this.running==4){
	  return new FromFileImage (this.center,"Shooting2.PNG");
	} else if (this.running==3){
	  return new FromFileImage (this.center,"Shooting3.PNG");
	} else {
	  return new FromFileImage (this.center,"Shooting.PNG");
	}
      } else {
	if (this.running==1){
	  return new FromFileImage (this.center,"Running1.PNG");
	} else if (this.running==2 || this.running==4){
	  return new FromFileImage (this.center,"Running2.PNG");
	} else if (this.running==3){
	  return new FromFileImage (this.center,"Running3.PNG");
	} else {
	  return new FromFileImage (this.center,"Standing.PNG");
	}
      }
    } else {
      if (this.shooting==1){
	if (this.running==1){
	  return new FromFileImage (this.center,"LShooting1.PNG");
	} else if (this.running==2 || this.running==4){
	  return new FromFileImage (this.center,"LShooting2.PNG");
	} else if (this.running==3){
	  return new FromFileImage (this.center,"LShooting3.PNG");
	} else {
	  return new FromFileImage (this.center,"LShooting.PNG");
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
  }
  public Player jump(){
    if (this.facing.equals("right")){
      if (this.jumping>5){
	return new Player (new Posn(this.center.x+(2*(1+this.running)),
				    this.center.y-10),
			   this.facing,
			   this.running,
			   this.shooting,
			   this.jumping-1);
      } else {
	return new Player (new Posn(this.center.x+(2*(1+this.running)),
				    this.center.y+10),
			   this.facing,
			   this.running,
			   this.shooting,
			   this.jumping-1);
      }
    } else {
      if (this.jumping>5){
	return new Player (new Posn(this.center.x-(2*(1+this.running)),
				    this.center.y-10),
			   this.facing,
			   this.running,
			   this.shooting,
			   this.jumping-1);
      } else {
	return new Player (new Posn(this.center.x-(2*(1+this.running)),
				    this.center.y+10),
			   this.facing,
			   this.running,
			   this.shooting,
			   this.jumping-1);
      }
    }
  }
  // Moves the player's based which arrow key is pressed
  public Player movePlayer(String ke){
    if (this.running==4){
      this.running=0;
    }
    if (ke.equals("right") && this.jumping==0){
      return new Player(new Posn(this.center.x + 10, this.center.y),
			"right",
			this.running+1,
		        0,
			this.jumping);
    } else if (ke.equals("left") && this.jumping==0){
      return new Player(new Posn(this.center.x - 10, this.center.y),
			"left",
			this.running+1,
			0,
			this.jumping);
    } else if (ke.equals("up") && this.jumping==0){
      return new Player(this.center,
			this.facing,
			this.running,
			this.shooting,
			10);
    } else if (ke.equals("s")){
      return new Player(new Posn(this.center.x, this.center.y),
			this.facing,
			this.running,
			1,
			this.jumping);
    } else {
      return new Player(this.center,
			this.facing,0,
			0,
			this.jumping);
    }
  }      
}
class Game2 extends World {
    
  static int width = 1000;
  static int height = 400;
  Player player;
  Weapon weapon;
  WorldImage gameArena = new RectangleImage(new Posn((this.width / 2),
						     (this.height / 2)),
					    this.width,
					    this.height,
					    new White());
    
  public Game2 (Player player, Weapon weapon){
    this.player = player;
    this.weapon = weapon;
      }
  // Controls what happens in the game when a key is pressed
  public World onKeyEvent(String ke){
    // If the key "x" is pressed the game world ends
    if (ke.equals("x")){
      return this.endOfWorld("Aidos");
    } else if (ke.equals("s")){
      if (this.player.facing.equals("right")){
	return new Game2 (this.player.movePlayer(ke),
			  new Weapon(new Posn(this.player.center.x+4,this.player.center.y+7),
				     "Buster","right"));
      } else {
	return new Game2 (this.player.movePlayer(ke),
			  new Weapon(new Posn(this.player.center.x-4,this.player.center.y+7),
				     "Buster","left"));
      }
    } else {
      return new Game2 (this.player.movePlayer(ke),this.weapon.moveWeapon());
    } 
  }
  // Controls what happens on each tick of the game world
  public World onTick(){
    if (this.player.jumping==0){
      return new Game2(new Player (this.player.center,
				   this.player.facing,
				   this.player.running,
				   this.player.shooting,
				   this.player.jumping),
		       this.weapon.moveWeapon());
    } else {
      return new Game2(new Player (this.player.center,
				   this.player.facing,
				   this.player.running,
				   this.player.shooting,
				   this.player.jumping).jump(),
		       this.weapon.moveWeapon());
    }
  }
  // Overlays the images of each of the game objects 
  public WorldImage makeImage(){
    return  new OverlayImages(new OverlayImages(	     
			      new OverlayImages(
						new OverlayImages(this.gameArena,
				  new RectangleImage(new Posn(500,390),
						     1000, 20, new Black())),
						this.weapon.weaponDraw()),
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
    Game2 G = new Game2(new Player (new Posn (50,360),"right",0,0,0),
			new Weapon(new Posn (100,100),"null","right"));
    G.bigBang(width, height, 0.1);
  }
}

