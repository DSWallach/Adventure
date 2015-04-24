import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.math.*;
import javalib.tester.*;
import javalib.funworld.*;
import javalib.colors.*;
import javalib.worldimages.*;
import java.io.*;
interface LoE{
  public boolean enemyHere();
  public int num();
  public Enemy getEnemy();
  public Posn enemyLoc();
  public LoE getNext();
  public LoE setFirst(Enemy enemy);
  public LoE setEnemy(Enemy enemy);
  public LoE setNext(LoE next);
  public LoE damage(Enemy enemy);
  public WorldImage listDraw(WorldImage base);
}

class noEnemy implements LoE{
  
  noEnemy(){}
  
  public boolean enemyHere(){
    return false;
  }
  public int num(){
    return 0;
  }
  public Posn enemyLoc(){
    return new Posn(-1,-1);
  }
  public Enemy getEnemy(){
    return null;
  }
  public LoE getNext(){
    return null;
  }
  public LoE setFirst(Enemy enemy){
    return new EnemyNode (enemy, new noEnemy());
  }
  public LoE setEnemy(Enemy enemy){
    return new EnemyNode (enemy, new noEnemy());
  }
  public LoE setNext(LoE next){
    return next;
  }
  public LoE damage(Enemy enemy){
    return this;
  }
  public WorldImage listDraw(WorldImage base){
    return base;
  }
}
class EnemyNode implements LoE{
  public Enemy e;
  public LoE n;
  
  EnemyNode(Enemy e, LoE n){
    this.e = e;
    this.n = n;
  }
  public boolean enemyHere(){
    return true;
  }
  public int num(){
    return 1 + this.n.num();
  }
  public Posn enemyLoc(){
    return new Posn (this.e.center.x, this.e.center.y);
  }
  public Enemy getEnemy(){
    return e;
  }
  public LoE getNext(){
    if (n.enemyHere()){
    return n;
    } else {
      return new noEnemy();
    }
  }
  public LoE setFirst(Enemy enemy){
    return new EnemyNode(enemy, this);
  }
  public LoE setEnemy(Enemy enemy){
    return new EnemyNode(enemy, this.n);
  }
  public LoE setNext(LoE next){
    return new EnemyNode(this.e, next);
  }
  public LoE damage(Enemy enemy){
    if(this.e==enemy){
      if (this.e.health > 1){
      return this.setEnemy (new Enemy(this.e.center,
				      this.e.type,
				      this.e.facing,
				      this.e.shooting,
				      this.e.health-1));
      } else {
	return n;
      }
    } else {
      return new EnemyNode (this.e, this.n.damage(enemy));
    }
  }			 
  public WorldImage listDraw(WorldImage base){
    WorldImage Image = new OverlayImages(base, this.getEnemy().enemyDraw());
    return this.n.listDraw(Image);
  }
}      
class Enemy {
  
  Posn center;
  String type;
  String facing;
  int shooting;
  int health;
  
  Enemy(Posn center, String type, String facing, int shooting, int health){
    this.center = center;
    this.type = type;
    this.facing = facing;
    this.shooting = shooting;
    this.health = health;
  }
  public FromFileImage enemyDraw(){
    // System.out.println ("Printing enemy"+this.center.x);
    if (type=="ET"){
      if (this.facing.equals("right")){
	if (this.shooting==1){
	  return new FromFileImage (this.center,"ETShooting.PNG");
	} else {
	  return new FromFileImage (this.center,"ETStanding.PNG");
	}
      } else {
	if (this.shooting==1){
	  return new FromFileImage (this.center,"LETShooting.PNG");
	} else {
	  return new FromFileImage (this.center,"LETStanding.PNG");
	}
      }
    } else {
      if (this.facing.equals("right")){
	if (this.shooting==1){
	  return new FromFileImage (this.center,"ETShooting.PNG");
	} else {
	  return new FromFileImage (this.center,"ETStanding.PNG");
	}
      } else {
	if (this.shooting==1){
	  return new FromFileImage (this.center,"LETShooting.PNG");
	} else {
	  return new FromFileImage (this.center,"LETStanding.PNG");
	}
      }
    }
  }
}

class EWeapon{
  Posn center;
  String type;
  String facing;
  
  EWeapon(Posn center, String type,String facing){
    this.center = center;
    this.type = type;
    this.facing = facing;
  }
  public FromFileImage eWeaponDraw(){
    if(this.type.equals("null")){
      return new FromFileImage (new Posn (10000,10000), "Eweapon.PNG");
    } else {
      return new FromFileImage (this.center,"EWeapon.PNG");
    }
  }
  public EWeapon moveEWeapon(){
    if (this.facing.equals("right")){
      return new EWeapon(new Posn(this.center.x + 10,this.center.y),
			 this.type,this.facing);
    } else {
      return new EWeapon(new Posn(this.center.x - 10,this.center.y),
			 this.type,this.facing);
    }
  }
}
interface LoW{
  public boolean weaponHere();
  public int num();
  public Posn weaponLoc();
  public Weapon getWeapon();
  public LoW move();
  public LoW getNext();
  public LoW setWeapon(Weapon w);
  public LoW setNext(LoW n);
  public LoW remove(Weapon w);
  public WorldImage listDraw(WorldImage base);
}

class noWeapon implements LoW{
  noWeapon(){}
  public boolean weaponHere(){
    return false;
  }
  public int num(){
    return 0;
  }
  public Posn weaponLoc(){
    return new Posn(-1,-1);
  }
  public Weapon getWeapon(){
    return null;
  }
  public LoW move(){
    return null;
  }
  public LoW getNext(){
    return null;
  }
  public LoW setWeapon(Weapon w){
    return new WeaponNode(w, new noWeapon());
  }
  public LoW setNext(LoW n){
    return n;
  }
  public LoW remove(Weapon w){
    return this;
  }
  public WorldImage listDraw(WorldImage base){
    return base;
  }
}
class WeaponNode implements LoW{
  public Weapon w;
  public LoW n;
  
  WeaponNode(Weapon w, LoW n){
    this.w = w;
    this.n = n;
  }
  public boolean weaponHere(){
    return true;
  }
  public int num(){
    return 1 + this.n.num();
  }		   
  public Posn weaponLoc(){
    return new Posn (this.w.center.x,this.w.center.y);
  }
  public Weapon getWeapon(){
    return w;
  }
  public LoW move(){
    if (this.weaponHere()){
      if (this.n.weaponHere()){
	return new WeaponNode (this.w.moveWeapon(), this.n.move());
      } else {
	return new WeaponNode (this.w.moveWeapon(), new noWeapon());
      }
    } else {
      return this;
    }
  }
  public LoW getNext(){
    return n;
  }
  public LoW setWeapon(Weapon w){
    return new WeaponNode(w, this.n);
  }
  public LoW setNext(LoW n){
    return new WeaponNode(this.w, n);
  }
  public LoW remove(Weapon w){
    if(this.w==w){
      return this.getNext();
    } else {
      return this.getNext().remove(w);
    }
  }
  public WorldImage listDraw(WorldImage base){
    WorldImage Image = new OverlayImages(base, this.getWeapon().weaponDraw());
    return this.n.listDraw(Image);
  }
}      
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
    if(this.type.equals("buster")){
      return new FromFileImage (this.center, "BusterShot.PNG");
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
class Player {
    
  Posn center;
  String facing;
  int running;
  int shooting;
  int jumping;
  int health;

  Player(Posn center, String facing, int running, int shooting, int jumping, int health){
    this.center = center;
    this.facing = facing;
    this.running = running;
    this.shooting = shooting;
    this.jumping = jumping;
    this.health = health;
  }
  
  // Generates an image of the player's character
  public FromFileImage draw(){
    if (this.facing.equals("right")){
      if(this.jumping==0){
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
	  return new FromFileImage (this.center,"JumpShooting.PNG");
	} else {
	  return new FromFileImage (this.center,"Jumping.PNG");
	}
      }
    } else {
      if (this.jumping==0){
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
      } else {
	if (this.shooting==1){
	  return new FromFileImage (this.center,"LJumpShooting.PNG");
	} else {
	  return new FromFileImage (this.center,"LJumping.PNG");
	}
      }
    }
  }
  public Player jump(){
    if (this.facing.equals("right")){
      return new Player (new Posn(this.center.x+6,
				  this.center.y-10),
			 this.facing,
			 this.running,
			 this.shooting,
			 this.jumping-1,
			 this.health);
    } else {
      return new Player (new Posn(this.center.x-6,
				  this.center.y-10),
			 this.facing,
			 this.running,
			 this.shooting,
			 this.jumping-1,
			 this.health);
    }
  }
  // Moves the player's based which arrow key is pressed
  public Player move(String ke){
    if (this.running==4){
      this.running=0;
    }
    if (ke.equals("right") && this.jumping==0){
      return new Player(new Posn(this.center.x + 10, this.center.y),
			"right",
			this.running+1,
		        0,
			this.jumping,
			this.health);
    } else if (ke.equals("left") && this.jumping==0){
      return new Player(new Posn(this.center.x - 10, this.center.y),
			"left",
			this.running+1,
			0,
			this.jumping,
			this.health);
    } else if (ke.equals("up") && this.jumping==0){
      return new Player(this.center,
			this.facing,
			this.running,
			this.shooting,
			10,
			this.health);
    } else if (ke.equals("s")){
      return new Player(new Posn(this.center.x, this.center.y),
			this.facing,
			this.running,
			1,
			this.jumping,
			this.health);
    } else {
      return new Player(this.center,
			this.facing,0,
			0,
			this.jumping,
			this.health);
    }
  }
  public Player shoot(String ke){
    if (ke.equals("s")){
      return new Player(this.center,
			this.facing,
			this.running,
			1,
			this.jumping,
			this.health);
    } else {
      return this;
    }
  }
}
interface PlatformNode{
  public boolean platformHere();
  public int num();
  public int platformHere(int h);
  public Platform getPlatform();
  public PlatformNode getNext();
}

class noPlatform implements PlatformNode{
  noPlatform(){}
  public boolean platformHere(){
    return false;
  }
  public int num(){
    return 0;
  }
  public int platformHere(int h){
    return 0;
  }
  public Platform getPlatform(){
    return null;
  }
  public PlatformNode getNext(){
    return null;
  }
}
class LoP implements PlatformNode{
  public Platform p;
  public PlatformNode n;
  
  LoP(Platform p, PlatformNode n){
    this.p = p;
    this.n = n;
  }
  public boolean platformHere(){
    return true;
  }
  public int num(){
    return 1 + this.n.num();
  }
  public int platformHere(int x){
    if (this.p.width.x < x && x < this.p.width.y){
      return this.p.height;
    } else {
      return this.getNext().platformHere(x);
    }
  }
  public Platform getPlatform(){
    return p;
  }
  public PlatformNode getNext(){
    return n;
  }
}      
class Platform {
  Posn width;
  int height;
  Platform(int height, Posn width){
    this.height = height;
    this.width = width;
  }
}
class Game2 extends World {
    
  static int width = 700;
  static int height = 250;
  static int widthStage1 = 700;
  static int heightStage1 = 250;
  static FromFileImage blank  = new FromFileImage (new Posn(0,0), "blank.png");
  
  Player player;

  LoW low;
  LoE loe;
  LoP lop;
  
  EWeapon eweapon0;
  
  FromFileImage Stage1 = new FromFileImage (new Posn((widthStage1/2),
						     (heightStage1/2)),"Stage1.PNG");
    
  public Game2 (Player player,
		LoW low,
		LoE loe,
		EWeapon eweapon0,
		LoP lop){
    
    this.player = player;
    this.low = low;
    this.loe = loe;
    this.eweapon0 = eweapon0;
    this.lop = lop;
    
  }
 
  
  // Controls what happens in the game when a key is pressed
  public World onKeyEvent(String ke){
    // If the key "x" is pressed the game world ends
    if (ke.equals("x")){
      return this.hit();
    } else if (ke.equals("s")){
      if (this.player.facing.equals("right")){
	return new Game2 (this.player.move(ke),
			  new WeaponNode(new Weapon(new Posn(this.player.center.x+5,
							     this.player.center.y+7),
						    "Buster","right"), this.low),
			  this.loe,
			  this.eweapon0,
			  this.lop).gravity();
      } else {
	return new Game2 (this.player.move(ke),
			  new WeaponNode(new Weapon(new Posn(this.player.center.x-5,
							     this.player.center.y+7),
						    "Buster","left"), this.low),
			  this.loe,
			  this.eweapon0,
			  this.lop).gravity();
      }
    } else {
      return new Game2 (this.player.move(ke),
			this.low.move(),
			this.loe,
			this.eweapon0.moveEWeapon(),
			this.lop).gravity();
    } 
  }
  // Controls what happens on each tick of the game world
  public World onTick(){
    if (this.player.jumping==0){
      return new Game2(new Player (this.player.center,
				   this.player.facing,
				   this.player.running,
				   this.player.shooting,
				   this.player.jumping,
				   this.player.health),
		       this.low.move(),
		       this.loe,
		       this.eweapon0.moveEWeapon(),
		       this.lop).gravity().hit();
    } else {
      return new Game2(new Player (this.player.center,
				   this.player.facing,
				   this.player.running,
				   this.player.shooting,
				   this.player.jumping,
				   this.player.health).jump(),
		       this.low.move(),
		       this.loe,
		       this.eweapon0.moveEWeapon(),
		       this.lop).hit();
    }
  }
  // Overlays the images of each of the game objects 
  public WorldImage makeImage(){
    return  new OverlayImages(
		new OverlayImages(	     
		     new OverlayImages(
			 new OverlayImages(
			     new OverlayImages(this.Stage1,
				 this.loe.listDraw(blank)),
			     this.player.draw()),
			 this.low.listDraw(blank)),
		     this.eweapon0.eWeaponDraw()),
		new TextImage(new Posn(300, 20), "Player health " +
			      this.player.health,Color.red)); 
  }
  // Determines under what conditions the game world ends
  public WorldEnd worldEnds(){
    if (this.player.center.y>heightStage1){
      return new WorldEnd(true,this.makeImage());
    } else {
      // Otherwise don't end the game
      return new WorldEnd(false, this.makeImage());
    }
  }
  public Game2 gravity(){
    if (!(0==this.lop.platformHere(this.player.center.x))){
      if (this.player.center.y < this.lop.platformHere(this.player.center.x)-20){
	return new Game2(new Player(new Posn (this.player.center.x,this.player.center.y+10),
				    this.player.facing,
				    this.player.running,
				    this.player.shooting,
				    this.player.jumping,
				    this.player.health),
			 this.low,
			 this.loe,
			 this.eweapon0,
			 this.lop);
      } else {
	return this;
      }
    } else {
      return new Game2(new Player(new Posn (this.player.center.x,this.player.center.y+10),
				  this.player.facing,
				  this.player.running,
				  this.player.shooting,
				  this.player.jumping,
				  this.player.health),
		       this.low,
		       this.loe,
		       this.eweapon0,
		       this.lop);
    }
  }
  public Game2 hit(){
    Posn hit = new Posn (100, 100);
    LoE currEnemy = this.loe;
    LoW currWeapon = this.low;
    Posn wLoc = currWeapon.weaponLoc();
    Posn eLoc = currEnemy.enemyLoc();
    for (int i=0;i<this.loe.num();i++){
      currWeapon = this.low;
      wLoc = currWeapon.weaponLoc();
      for (int j=0;j<this.low.num();j++){
	//System.out.println("weapon loop");
	hit = new Posn(Math.abs(wLoc.x-eLoc.x),
		       Math.abs(wLoc.y-eLoc.y));
	if (hit.x < 20 && hit.y < 20){
	  System.out.println("Enemy hit");
	  return new Game2(this.player,
			   this.low.remove(currWeapon.getWeapon()),
			   this.loe.damage(currEnemy.getEnemy()),
			   this.eweapon0,
			   this.lop);
	} else {
	  currWeapon = currWeapon.getNext();
	}
      }
      //System.out.println("enemy loop");
      currEnemy = currEnemy.getNext();
      eLoc = currEnemy.enemyLoc();
    }
    return this;
  }
  // Defines the initial setup of the game world and begins the game
  public static void main(String args[]){
    LoP Stage1Platforms =
      new LoP(new Platform(147,
			   new Posn(0,86)),
	      new LoP(new Platform(179,
				   new Posn(86, 151)),
		      new LoP(new Platform(210,
					   new Posn(151,313)),
			      new LoP(new Platform(226,
						   new Posn(313,379)),
				      new LoP(new Platform(210,
							   new Posn (379, 541)),
					      new LoP (new Platform (210,
								     new Posn(574, 700)),
						       new noPlatform()))))));

    Game2 Stage1 = new Game2(new Player (new Posn (50,100),"right",0,0,0,10),
			     new WeaponNode(new Weapon(new Posn(100,100),
						       "buster","right"),
					    new noWeapon()),
			     new EnemyNode(new Enemy (new Posn (300,190),
						      "left","ET",0,2),
					   new EnemyNode(new Enemy (new Posn (100, 159),
								    "left","ET",0,2),
							 new noEnemy())),
			     new EWeapon(new Posn (100,100),
					 "null","left"),
			     Stage1Platforms);
    Game2 Stage2 = new Game2(new Player (new Posn (50,100),"right",0,0,0,10),
			     new WeaponNode(new Weapon(new Posn(100,100),
						       "buster","right"),
					    new noWeapon()),
			     new EnemyNode(new Enemy (new Posn (300,200),
						      "left","ET",0,0),
					   new EnemyNode(new Enemy (new Posn (500, 200),
								    "left","ET",0,0),
							 new noEnemy())),
			     new EWeapon(new Posn (100,100),
					 "null","left"),
			     Stage1Platforms);
    
    Stage1.bigBang(widthStage1, heightStage1, 0.1);
  }
}
/*
  class Adventure{
  public Game2 EnemyAI(Game2 g){
  if (g.player.center.x<g.enemy.cetner.x){
  return new Game2(g.player,
  g.weapon,
  new Enemy(g.enemry.center,
  "left",
  g.enemy.type,
  g.enemy.shooting,
  s.enemy.health),
  g.eweapon);
		       
  }
  public Enemy AI(Player p, Enemy e){
  if (this.enemy.shooting<=0 &&
  (Math.abs(this.enemy.center.y - this.player.center.y)<10) &&
  (Math.abs(this.enemy.center.x - this.player.center.x)<100)){
  return new Enemy (this.enemy.center,
  this.enemy.facing,
  this.enemy.type,
  10,
  this.enemy.health);
  } else {
  return new Enemy (this.enemy.center,
  this.enemy.facing,
  this.enemy.type,
  10,
  this.enemy.health);
  }
  }


    
  }
  public static void main(String args[]){

  Game2 Stage1 = new Game2(new Player (new Posn (50,200),"right",0,0,0),
  new Weapon(new Posn (100,100),"null","right"),
  new Enemy (new Posn (800,200),"left","ET",0,0),
  new EWeapon(new Posn (100,100),"null","left"));
  Stage1.bigBang(widthStage1, heightStage1, 0.1);
  }
  }
*/
