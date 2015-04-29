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

class Background{

  String stage;
  
  Background(String stage){
    this.stage = stage;
  }
  
  public FromFileImage draw(){
    
    FromFileImage Stage1Image = new FromFileImage (new Posn(350,125), "Stage1.PNG");
    FromFileImage Stage2Image = new FromFileImage (new Posn(350,125), "Stage2.PNG");
    FromFileImage StageRImage = new FromFileImage (new Posn(350,125), "StageR.PNG");
    
    if (this.stage.equals("Stage1")){
      return Stage1Image;
    } else if (this.stage.equals("Stage2")){
      return Stage2Image;
    } else if (this.stage.equals("StageR")){
      return StageRImage;
    } else if (this.stage.equals("Stage3")){
      return Stage1Image;
    } else if (this.stage.equals("Stage4")){
      return Stage1Image;
    } else {
      return StageRImage;
    }
  }
}
    

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
  public LoE attack(Enemy enemy, String dir);
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
  public LoE attack(Enemy enemy, String dir){
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
    return n;
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
      return new EnemyNode(this.e, this.n.damage(enemy));
    }
  }
  public LoE attack(Enemy enemy, String dir){
    if(this.e==enemy){
      if (dir.equals("right")){	if (this.getEnemy().shooting > 0) {
	  return new EnemyNode(new Enemy(this.e.center,
					 this.e.type,
					 "right",
					 this.e.shooting-1,
					 this.e.health), this.n.attack(enemy, "right"));
	} else {
	  return new EnemyNode(new Enemy(this.e.center,
					 this.e.type,
					 "right",
					 10,
					 this.e.health),this.n);
	}
      } else {
	if (this.getEnemy().shooting > 0) {
	  return new EnemyNode(new Enemy(this.e.center,
					 this.e.type,
					 "left",
					 this.e.shooting-1,
					 this.e.health), this.n.attack(enemy, "left"));
	} else {
	  return new EnemyNode(new Enemy(this.e.center,
					 this.e.type,
					 "left",
					 10,
					 this.e.health),this.n);
	}
      }
    } else {
      return new EnemyNode(new Enemy(this.e.center,
				     this.e.type,
				     this.e.facing,
				     this.e.shooting-1,
				     this.e.health), this.n.attack(enemy, dir));
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
    if (this.facing.equals("right")){
      if (this.shooting>0){
	return new FromFileImage (this.center,"ETShooting.PNG");
      } else {
	return new FromFileImage (this.center,"ETStanding.PNG");
      }
    } else {
      if (this.shooting>0){
	return new FromFileImage (this.center,"LETShooting.PNG");
      } else {
	return new FromFileImage (this.center,"LETStanding.PNG");
      }
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
  public LoW cutTail(Weapon w);
  public LoW eShoot(Posn e, String dir,String type);
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
    return null; //throw RuntimeException("No Weapon Here!");
  }
  public LoW move(){
    return this;
  }
  public LoW getNext(){
    return this;
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
  public LoW cutTail(Weapon w){
    return this;
  }
  public LoW eShoot(Posn e, String dir, String type){
    if (dir=="right"){
      return new WeaponNode (new Weapon (false,
					 new Posn (e.x+5,
						   e.y+7),
					 type,
					 dir), this);
    } else {
      return new WeaponNode (new Weapon (false,
					 new Posn (e.x-5,
						   e.y+7),
					 type,
					 dir), this);
    }
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
    return new WeaponNode (this.w.moveWeapon(), this.n.move());
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
      return new WeaponNode(this.w,this.n.remove(w));
    }
  }
  public LoW cutTail(Weapon w){
    if (this.w==w){
      return new noWeapon();
    } else {
      return new WeaponNode(this.w,this.n.cutTail(w));
    }
  }
  public LoW eShoot (Posn e, String dir, String type){
    if (dir=="right"){
      return new WeaponNode (new Weapon (false,
					 new Posn (e.x+5,
						   e.y+7),
					 type,
					 dir), this);
    } else {
      return new WeaponNode (new Weapon (false,
					 new Posn (e.x-5,
						   e.y+7),
					 type,
					 dir), this);
    }
  }
  public WorldImage listDraw(WorldImage base){
    WorldImage Image = new OverlayImages(base, this.getWeapon().weaponDraw());
    return this.n.listDraw(Image);
  }
}      
class Weapon{
  
  boolean friendly;
  Posn center;
  String type;
  String facing;
  
  Weapon(Boolean friendly, Posn center, String type, String facing){
    this.friendly = friendly;
    this.center = center;
    this.type = type;
    this.facing = facing;
  }
  
  public FromFileImage weaponDraw(){
    if (this.friendly){
      if(this.type.equals("buster")){
	return new FromFileImage (this.center, "BusterShot.PNG");
      } else {
	return new FromFileImage (this.center,"BusterShot.PNG");
      }
    } else {
      if(this.type.equals("null")){
	return new FromFileImage (new Posn (10000,10000), "Eweapon.PNG");
      } else {
	return new FromFileImage (this.center,"EWeapon.PNG");
      }
    }
  }
  public Weapon moveWeapon(){
    if (this.facing.equals("right")){
      return new Weapon(this.friendly,
			new Posn(this.center.x + 10,this.center.y),
			this.type,this.facing);
    } else {
      return new Weapon(this.friendly,
			new Posn(this.center.x - 10,this.center.y),
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
  static LoP Stage1Platforms =
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
  int count = 0;

  String stage;  
  Player player;
  LoW low;
  LoE loe;
  LoP lop;
  Background back;

  FromFileImage blank  = new FromFileImage (new Posn(0,0), "blank.png");
 
  public Game2 (String Stage,
		Player player,
		LoW low,
		LoE loe,
		LoP lop,
		Background back){
    
    this.stage = stage;
    this.player = player;
    this.low = low;
    this.loe = loe;
    this.lop = lop;
    this.back = back;
    
  }
 
  
  // Controls what happens in the game when a key is pressed
  public World onKeyEvent(String ke){
    // If the key "x" is pressed the game world ends
    if (ke.equals("x")){
      return this;
    } else if (ke.equals("s")){
      if (this.player.facing.equals("right")){
	return new Game2 (this.stage,
			  this.player.move(ke),
			  new WeaponNode(new Weapon(true,
						    new Posn(this.player.center.x+5,
							     this.player.center.y+7),
						    "Buster","right"), this.low),
			  this.loe,
			  this.lop,
			  this.back).gravity().hit();
      } else {
	return new Game2 (this.stage,
			  this.player.move(ke),
			  new WeaponNode(new Weapon(true,
						    new Posn(this.player.center.x-5,
							     this.player.center.y+7),
						    "Buster","left"), this.low),
			  this.loe,
			  this.lop,
			  this.back).gravity().hit();
      }
    } else {
      return new Game2 (this.stage,
			this.player.move(ke),
			this.low.move(),
			this.loe,
			this.lop,
			this.back).gravity().hit();
    } 
  }
  // Controls what happens on each tick of the game world
  public World onTick(){
    if (this.player.center.x>650 && count==0){
      count++;
      return new Game2("Stage2",
		       new Player (new Posn (50,100),"right",0,0,0,10),
		       new WeaponNode(new Weapon(true,
						 new Posn(100,100),
						 "buster","right"),
				      new noWeapon()),
		       new EnemyNode(new Enemy (new Posn (300,200),
						"left","ET",0,2),
				     new EnemyNode(new Enemy (new Posn (500, 200),
							      "left","ET",0,2),
						   new noEnemy())),
		       Stage1Platforms,
		       new Background("Stage2"));
    } else if (this.player.center.x>650 && count==1){
      count++;
      return new Game2("StageR",
		       new Player (new Posn (50,100),"right",0,0,0,10),
		       new WeaponNode(new Weapon(true,
						 new Posn(100,100),
						 "buster","right"),
				      new noWeapon()),
		       new EnemyNode(new Enemy (new Posn (300,200),
						"left","ET",0,2),
				     new EnemyNode(new Enemy (new Posn (500, 200),
							      "left","ET",0,2),
						   new noEnemy())),
		       Stage1Platforms,
		       new Background("StageR"));
    } else {
      if (this.low.num()>100){
	return new Game2(this.stage,
			 this.player,
			 this.low,
			 this.loe,
			 this.lop,
			 this.back).LoWClean().hit().AI();
      } else if (this.player.jumping==0){
	return new Game2(this.stage,
			 this.player,
			 this.low.move(),
			 this.loe,
			 this.lop,
			 this.back).gravity().hit().AI();
      } else {
	return new Game2(this.stage,
			 this.player.jump(),
			 this.low.move(),
			 this.loe,
			 this.lop,
			 this.back).hit().AI();
      }
    }
  }
  // Overlays the images of each of the game objects 
  public WorldImage makeImage(){
    return  new OverlayImages(
			      new OverlayImages(
						new OverlayImages(
								  new OverlayImages(
										    new OverlayImages(blank,
												      this.back.draw()),
										    this.loe.listDraw(blank)),
								  this.player.draw()),
						this.low.listDraw(blank)),
			      new TextImage(new Posn(300, 20), "Player health " +
					    this.player.health,Color.red));
  }
  // Determines under what conditions the game world ends
  public WorldEnd worldEnds(){
    if (this.player.center.y>height){
      return new WorldEnd(true,this.makeImage());
    } else if (this.player.health==0){
      return new WorldEnd(true,this.makeImage());
    } else {
      // Otherwise don't end the game
      return new WorldEnd(false, this.makeImage());
    }
  }
  public Game2 gravity(){
    if (!(0==this.lop.platformHere(this.player.center.x))){
      if (this.player.center.y < this.lop.platformHere(this.player.center.x)-20){
	return new Game2(this.stage,
			 new Player(new Posn (this.player.center.x,this.player.center.y+10),
				    this.player.facing,
				    this.player.running,
				    this.player.shooting,
				    this.player.jumping,
				    this.player.health),
			 this.low,
			 this.loe,
			 this.lop,
			 this.back);
      } else {
	return this;
      }
    } else {
      return new Game2(this.stage,
		       new Player(new Posn (this.player.center.x,this.player.center.y+10),
				  this.player.facing,
				  this.player.running,
				  this.player.shooting,
				  this.player.jumping,
				  this.player.health),
		       this.low,
		       this.loe,
		       this.lop,
		       this.back);
    }
  }
  
  public Game2 AI(){
    LoE currEnemy = this.loe;
    Posn eLoc = currEnemy.enemyLoc();
    Posn pLoc = this.player.center;
    for (int i = 0; i<this.loe.num();i++){
      if (Math.abs(pLoc.y - eLoc.y)<60){
	if (0 < (pLoc.x - eLoc.x) &&
	    (pLoc.x - eLoc.x) < 80){
	  if (currEnemy.getEnemy().shooting>0){
	    return new Game2(this.stage,
			     this.player,
			     this.low,
			     this.loe.attack(currEnemy.getEnemy(),"right"),
			     this.lop,
			     this.back);
	  } else {
	    return new Game2(this.stage,
			     this.player,
			     this.low.eShoot(eLoc,"right",currEnemy.getEnemy().type),
			     this.loe.attack(currEnemy.getEnemy(),"right"),
			     this.lop,
			     this.back);
	  }
	} else if (-80 < (pLoc.x - eLoc.x) &&
		   (pLoc.x - eLoc.x) < 0){
	  if (currEnemy.getEnemy().shooting>0){
	    return new Game2(this.stage,
			     this.player,
			     this.low,
			     this.loe.attack(currEnemy.getEnemy(),"left"),
			     this.lop,
			     this.back);
	  } else {
	    return new Game2(this.stage,
			     this.player,
			     this.low.eShoot(eLoc,"left",currEnemy.getEnemy().type),
			     this.loe.attack(currEnemy.getEnemy(),"left"),
			     this.lop,
			     this.back);
	  }
	}
      }
      currEnemy = currEnemy.getNext();
      eLoc = currEnemy.enemyLoc();
      pLoc = this.player.center;
    }
    return this;
  }

  public Game2 LoWClean(){
    LoW curr = this.low;
    Posn wLoc = curr.weaponLoc();
    int count = this.low.num();
    for (int i=0; i<count;i++){
      if (width < 0 || width < wLoc.x){
	return new Game2(this.stage,
			 this.player,
			 this.low.cutTail(curr.getWeapon()),
			 this.loe,
			 this.lop,
			 this.back);
      }
      count = this.low.num();
      curr = curr.getNext();
      wLoc = curr.weaponLoc();
    }
    return this;
  }
    
  public Game2 hit(){
    
    Posn hit = new Posn (100, 100);
    LoE currEnemy = this.loe;
    LoW currWeapon = this.low;
    Posn pLoc = this.player.center;
    Posn wLoc = currWeapon.weaponLoc();
    Posn eLoc = currEnemy.enemyLoc();
    
    for (int i=0;i<this.low.num();i++){
      if (!this.low.getWeapon().friendly){
	
	pLoc = this.player.center;
	hit = new Posn(Math.abs(wLoc.x-pLoc.x),
		       Math.abs(wLoc.y-pLoc.y));
	if (hit.x < 15 && hit.y < 20){
	  return new Game2(this.stage,
			   new Player(this.player.center,
				      this.player.facing,
				      this.player.running,
				      this.player.shooting,
				      this.player.jumping,
				      this.player.health-1),
			   this.low.remove(currWeapon.getWeapon()),
			   this.loe,
			   this.lop,
			   this.back);
	}
      } else {
      	
	currEnemy = this.loe;
	eLoc = currEnemy.enemyLoc();
      
	for (int j=0;j<this.loe.num();j++){
	
	  hit = new Posn(Math.abs(wLoc.x-eLoc.x),
			 Math.abs(wLoc.y-eLoc.y));
	  
	  if (hit.x < 20 && hit.y < 20){
	    return new Game2(this.stage,
			     this.player,
			     this.low.remove(currWeapon.getWeapon()),
			     this.loe.damage(currEnemy.getEnemy()),
			     this.lop,
			     this.back);
	  }
	}
      }
      currWeapon = currWeapon.getNext();
      wLoc = currWeapon.weaponLoc();
    }
    return this;
  }
  // Defines the initial setup of the game world and begins the game
  public static void main(String args[]){



    Game2 Stage1 = new Game2("Stage1",
			     new Player (new Posn (50,100),"right",0,0,0,10),
			     new noWeapon(),
			     new EnemyNode(new Enemy (new Posn (300,190),
						      "left","ET",0,2),
					   new EnemyNode(new Enemy (new Posn (100, 159),
								    "left","ET",0,2),
							 new noEnemy())),
			     Stage1Platforms,
			     new Background("Stage1"));
    Stage1.bigBang(width, height, 0.1);
  }
}

