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

//convert input.gif -transparent white output.gif  

// Lenovo Reference number 214878350

class Background{

  String stage;
  
  Background(String stage){
    this.stage = stage;
  }
  
  public FromFileImage draw(){
    
    FromFileImage Stage1Image = new FromFileImage (new Posn(350,125), "Stage1.PNG");
    FromFileImage Stage2Image = new FromFileImage (new Posn(350,125), "Stage2.PNG");
    FromFileImage StageRImage = new FromFileImage (new Posn(350,125), "StageR.PNG");
    FromFileImage Stage3Image = new FromFileImage (new Posn(350,125), "Stage3.PNG");
    FromFileImage Stage4Image = new FromFileImage (new Posn(350,125), "Stage4.PNG");
    FromFileImage StageJImage = new FromFileImage (new Posn(350,125), "StageJ.PNG");
    
    if (this.stage.equals("Stage1")){
      return Stage1Image;
    } else if (this.stage.equals("Stage2")){
      return Stage2Image;
    } else if (this.stage.equals("StageR")){
      return StageRImage;
    } else if (this.stage.equals("Stage3")){
      return Stage3Image;
    } else if (this.stage.equals("Stage4")){
      return Stage4Image;
    } else {
      return StageJImage;
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
    return new Posn (this.e.center().x, this.e.center().y);
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
      if (this.e.health() > 1){
	if (this.e.type().equals("ET")){
	  return this.setEnemy (new ET(this.e.center(),
					"ET",
				       this.e.facing(),
				       this.e.shooting(),
				       this.e.health()-1));
	} else {
	  return this.setEnemy (new DrRacket(this.e.center(),
					     this.e.floating(),
					     this.e.facing(),
					     this.e.shooting(),
					     this.e.health()-1));
	}
      } else {
	return n;
      }
    } else {
      return new EnemyNode(this.e, this.n.damage(enemy));
    }
  }
  public LoE attack(Enemy enemy, String dir){
    if(enemy.type().equals("DrRacket")){
      if (this.e==enemy){
	return new EnemyNode(new DrRacket(this.e.center(),
					  this.e.floating(),
					  this.e.facing(),
					  this.e.shooting(),
					  this.e.health()));
	  } else {
	return new EnemyNode(e, n.attack(enemy, dir));
      }			
    } else if(this.e==enemy){
      if (this.getEnemy().shooting > 0) {
	if (dir.equals("right")){
	  return new EnemyNode(new ET(this.e.center,
					 "right",
				      this.e.shooting()-1,
				      this.e.health()), this.n.attack(enemy, "right"));
	} else {
	  return new EnemyNode(new ET(this.e.center(),
					 "left",
				      this.e.shooting()-1,
				      this.e.health()), this.n.attack(enemy, "left"));
	}
      }
    }
    return new EnemyNode(new ET(this.e.center(),
				this.e.facing(),
				   15,
				this.e.health()), this.n.attack(enemy, dir));
      
  }
  public WorldImage listDraw(WorldImage base){
    WorldImage Image = new OverlayImages(base, this.getEnemy().enemyDraw());
    return this.n.listDraw(Image);
  }
}
interface Enemy {
  public Posn center();
  public String type();
  public int health();
  public int shooting();
  public String facing();
  public FromFileImage enemyDraw();
}
class ET implements Enemy {
  
  Posn center;
  String facing;
  int shooting;
  int health;
  
  ET(Posn center, String facing, int shooting, int health){
    this.center = center;
    this.facing = facing;
    this.shooting = shooting;
    this.health = health;
  }
  public Posn center(){
    return this.center;
  }
  public String type(){
    return "ET";
  }
  public int health(){
    return this.health;
  }
  public int shooting(){
    return this.shooting;
  }
  public String facing(){
    return this.facing;
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
class DrRacket implements Enemy {
  Posn center;
  boolean floating;
  String facing;
  int shooting;
  int health;
  
  DrRacket(Posn center, boolean floating, String facing, int shooting, int health){
    this.center = center;
    this.type = type;
    this.facing = facing;
    this.shooting = shooting;
    this.health = health;
  }
  public Posn center(){
    return this.center;
  }
  public String type(){
    return "DrRacket";
  }
  public int health(){
    return this.health();
  }
  public int shooting(){
    return this.shooting;
  }
  public String facing(){
    return this.facing;
  }
  public FromFileImage enemyDraw(){
      if (this.facing.equals("right")){
	if (this.shooting>0){
	  return new FromFileImage(this.center, "DrRacketShooting.PNG");
	} else {
	  return new FromFIleImage(this.center,"DrRacketFloating.PNG");
	}
      } else {
	if (this.shooting>0){
	  return new FromFileImage(this.center, "DrRacketShooting.PNG");
	} else {
	  return new FromFIleImage(this.center,"DrRacketFloating.PNG");
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
      if (this.health==0){
	return new FromFileImage (this.center,"Death.PNG");
      } else {
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
      }
    } else {
      if (this.health==0){
	return new FromFileImage (this.center,"LDeath.PNG");
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
  }
  // Moves the player's based which arrow key is pressed
  public Player move(String ke){
    if (this.running==4){
      this.running=0;
    }
    if (ke.equals("right")){
      return new Player(new Posn(this.center.x + 10, this.center.y),
			"right",
			this.running+1,
			0,
			this.jumping,
			this.health);
    } else if (ke.equals("left")){
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
			1,
			this.health);
    } else if (ke.equals("s")){
      return new Player(this.center,
			this.facing,
			this.running,
			1,
			this.jumping,
			this.health);
    } else {
      return new Player(this.center,
			this.facing,
			this.running,
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
interface LoP{
  public boolean platformHere();
  public int num();
  public int platformHere(int h);
  public Platform getPlatform();
  public LoP getNext();
}

class noPlatform implements LoP{
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
  public LoP getNext(){
    return null;
  }
}
class PlatformNode implements LoP{
  public Platform p;
  public LoP n;
  
  PlatformNode(Platform p, LoP n){
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
  public LoP getNext(){
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
  static PlatformNode Stage1Platforms =
    new PlatformNode(new Platform(147,
				  new Posn(0,86)),
    new PlatformNode(new Platform(179,
				  new Posn(86, 151)),
    new PlatformNode(new Platform(210,
				  new Posn(151,313)),
    new PlatformNode(new Platform(226,
				  new Posn(313,379)),
    new PlatformNode(new Platform(210,
				  new Posn (379, 541)),
    new PlatformNode(new Platform (210,
				   new Posn(574, 700)),
		     new noPlatform()))))));
  
  static PlatformNode Stage2Platforms =
    new PlatformNode(new Platform(210,
				  new Posn(0,45)),
    new PlatformNode(new Platform(210,
				  new Posn(78,139)),
    new PlatformNode(new Platform(210,
				  new Posn(173,234)),
    new PlatformNode(new Platform(210,
				  new Posn(268,329)),
    new PlatformNode(new Platform(210,
				  new Posn(363,425)),
    new PlatformNode(new Platform(210,
				  new Posn (458,519)),
    new PlatformNode(new Platform(210,
				  new Posn(553,700)),
		     new noPlatform())))))));
  
  static PlatformNode StageRPlatforms =
    new PlatformNode(new Platform(117,
				  new Posn(0,43)),
    new PlatformNode(new Platform(149,
				  new Posn(28,75)),
    new PlatformNode(new Platform(181,
				  new Posn(60,139)),
    new PlatformNode(new Platform(213,
				  new Posn(124,635)),
    new PlatformNode(new Platform(181,
				  new Posn(623,670)),
    new PlatformNode(new Platform(117,
				  new Posn(652,700)),
		     new noPlatform()))))));
  
  static PlatformNode Stage3Platforms =
    new PlatformNode(new Platform(145,
				  new Posn(0,53)),
    new PlatformNode(new Platform(115,
				  new Posn(53,150)),
    new PlatformNode(new Platform(178,
				  new Posn(199,278)),
    new PlatformNode(new Platform(130,
				  new Posn(311,378)),
    new PlatformNode(new Platform(82,
				  new Posn(406,454)),
    new PlatformNode(new Platform(194,
				  new Posn(406,439)),
    new PlatformNode(new Platform(194,
				  new Posn(471,502)),
    new PlatformNode(new Platform(165,
				  new Posn(535,566)),
    new PlatformNode(new Platform(178,
				  new Posn(583,700)),
		     new noPlatform())))))))));
  static PlatformNode Stage4Platforms =
    new PlatformNode(new Platform(178,
				  new Posn (0,700)),
		     new noPlatform());
  static PlatformNode StageJPlatforms =
    new PlatformNode(new Platform(178,
				  new Posn(0,22)),
    new PlatformNode(new Platform(103,
				  new Posn(82,179)),
    new PlatformNode(new Platform(103,
				  new Posn(523,620)),
    new PlatformNode(new Platform(150,
				  new Posn(556,620)),
    new PlatformNode(new Platform(150,
				  new Posn(82,146)),
    new PlatformNode(new Platform(120,
				  new Posn(82,115)),
    new PlatformNode(new Platform(120,
				  new Posn(587,620)),
    new PlatformNode(new Platform(210,
				  new Posn(0,700)),
		     new noPlatform()))))))));
							  
  int count = 0;

  Player player;
  LoW low;
  LoE loe;
  LoP lop;
  Background back;

  FromFileImage blank  = new FromFileImage (new Posn(0,0), "blank.png");
 
  public Game2 (Player player,
		LoW low,
		LoE loe,
		LoP lop,
		Background back){
    
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
	return new Game2 (this.player.move(ke),
			  new WeaponNode(new Weapon(true,
						    new Posn(this.player.center.x+5,
							     this.player.center.y+7),
						    "Buster","right"), this.low),
			  this.loe,
			  this.lop,
			  this.back).gravity().hit();
      } else {
	return new Game2 (this.player.move(ke),
			  new WeaponNode(new Weapon(true,
						    new Posn(this.player.center.x-5,
							     this.player.center.y+7),
						    "Buster","left"), this.low),
			  this.loe,
			  this.lop,
			  this.back).gravity().hit();
      }
    } else if(this.player.jumping==0 && ke.equals("up")){
      return new Game2(this.player.move(ke),
		       this.low.move(),
		       this.loe,
		       this.lop,
		       this.back).gravity().hit();
    } else {
      return new Game2 (this.player.move(ke),
			this.low.move(),
			this.loe,
			this.lop,
			this.back).gravity().hit();
    } 
  }
  // Controls what happens on each tick of the game world
  public World onTick(){
    if (this.player.center.x>690 && this.back.stage.equals("Stage1")){
      count++;
      return new Game2(new Player (new Posn (10,190),"right",0,0,0,10),
		       new noWeapon(),
		       new EnemyNode(new Enemy (new Posn (300,190),
						"left","ET",0,2),
		       new EnemyNode(new Enemy (new Posn (500, 190),
						"left","ET",0,2),
				     new noEnemy())),
		       Stage2Platforms,
		       new Background("Stage2"));
    } else if (this.player.center.x>690 && this.back.stage.equals("Stage2")){
      count++;
      return new Game2(new Player (new Posn (10,97),"right",0,0,0,10),
		       new noWeapon(),
		       new noEnemy(),
		       StageRPlatforms,
		       new Background("StageR"));
    } else if (this.player.center.x>690 && this.back.stage.equals("StageR")){
      count++;
      return new Game2(new Player (new Posn (20,145),"right",0,0,0,10),
		       new noWeapon(),
		       new EnemyNode(new Enemy (new Posn (350,120),
						"left","ET",0,2),
		       new EnemyNode(new Enemy (new Posn (485,184),
						"left","ET",0,2),
		       new EnemyNode(new Enemy (new Posn(155,540),
						"left","ET",0,2),
				     new noEnemy()))),
		       Stage3Platforms,
		       new Background("Stage3"));
    } else if (this.player.center.x>690 && this.back.stage.equals("Stage3")){
      count++;
      return new Game2(new Player (new Posn (10,178),"right",0,0,0,10),
		       new noWeapon(),
		       new EnemyNode(new Enemy (new Posn (350,120),
						"left","ET",0,2),
		       new EnemyNode(new Enemy (new Posn (500, 200),
						"left","ET",0,2),
				     new noEnemy())),
		       Stage4Platforms,
		       new Background("Stage4"));      
    } else if (this.player.center.x>690 && this.back.stage.equals("Stage4")){
      count++;
      return new Game2(new Player (new Posn (10,178),"right",0,0,0,10),
		       new noWeapon(),
		       new noEnemy(),
		       StageJPlatforms,
		       new Background("StageJ"));
    } else {
      return new Game2(this.player,
		       this.low.move(),
		       this.loe,
		       this.lop,
		       this.back).hit().AI().gravity();
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
    if (this.player.center.y>height-30){
      return new WorldEnd(true,new Game2(new Player(new Posn (this.player.center.x,
							      this.player.center.y),
						    this.player.facing,
						    this.player.running,
						    this.player.shooting,
						    this.player.jumping,
						    0),
					 this.low,
					 this.loe,
					 this.lop,
					 this.back).makeImage());
    } else if (this.player.health==0){
      return new WorldEnd(true,this.makeImage());
    } else {
      // Otherwise don't end the game
      return new WorldEnd(false, this.makeImage());
    }
  }
  public Game2 gravity(){
    if (0 < this.player.jumping && this.player.jumping < 15){
      return new Game2(new Player(new Posn (this.player.center.x,this.player.center.y-5),
				  this.player.facing,
				  this.player.running,
				  this.player.shooting,

				  this.player.jumping+1,
				  this.player.health),
		       this.low,
		       this.loe,
		       this.lop,
		       this.back);
    } else if (!(0==this.lop.platformHere(this.player.center.x))){
      if (this.player.center.y < this.lop.platformHere(this.player.center.x)-20){
	return new Game2(new Player(new Posn (this.player.center.x,this.player.center.y+5),
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
	return new Game2(new Player(this.player.center,
				    this.player.facing,
				    this.player.running,
				    this.player.shooting,
				    0,
				    this.player.health),
			 this.low,
			 this.loe,
			 this.lop,
			 this.back);
      }
    } else {
      return new Game2(new Player(new Posn (this.player.center.x,this.player.center.y+2),
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
  public Game2 RacketAI(){
    LoE currEnemy = this.loe;
    Posn eLoc = currEnemy.enemyLoc();
    Posn pLoc = this.player.center;
    Random newRandom = new Random();
    int rand = newRandom.nextInt(100);
    if (currEnemy.getEnemy().floating && currEnemy.getEnemy().center.y<125){
      return new Game2(this.player,
		       this.low,
		       this.loe.move(currEnemy.getEnemy(), new Posn(0, -5)),
		       this.lop,
		       this.back);
    }
    if (currEnemy.getEnemy().shooting<1 && rand < 50){
      return new Game2(this.player,
		       this.low,
		       this.loe,
		       this.lop,
		       this.back);
		       
		       
    } else if (currEnemy.getEnemy().shooting<1){
      return new Game2(this.player,
		       this.low,
		       this.loe,
		       this.lop,
		       this.back);
    } else if (currEnemy.getEnemy().center.y>=125){
      return new Game2(this.player,
		       this.low.eShoot(eLoc,"right",currEnemy.getEnemy().type),
		       this.loe.attack(currEnemy.getEnemy(),"right"),
		       this.lop,
		       this.back);
    } else {
      return new Game2(this.player,
		       this.low.eShoot(eLoc,"right",currEnemy.getEnemy().type),
		       this.loe.attack(currEnemy.getEnemy(),"right"),
		       this.lop,
		       this.back);
    }
  }
  public Game2 AI(){
    LoE currEnemy = this.loe;
    Posn eLoc = currEnemy.enemyLoc();
    Posn pLoc = this.player.center;
    for (int i = 0; i<this.loe.num();i++){
      if (currEnemy.type.equals("DrRacket")){
	return this.RacketAI();
      } else {
	if (Math.abs(pLoc.y - eLoc.y)<50){
	  if (0 < (pLoc.x - eLoc.x) &&
	      (pLoc.x - eLoc.x) < 160){
	    if (currEnemy.getEnemy().shooting>0){
	      return new Game2(this.player,
			       this.low,
			       this.loe.attack(currEnemy.getEnemy(),"right"),
			       this.lop,
			       this.back);
	    } else {
	      return new Game2(this.player,
			       this.low.eShoot(eLoc,"right",currEnemy.getEnemy().type),
			       this.loe.attack(currEnemy.getEnemy(),"right"),
			       this.lop,
			       this.back);
	    }
	  } else if (-160 < (pLoc.x - eLoc.x) &&
		     (pLoc.x - eLoc.x) < 0){
	    if (currEnemy.getEnemy().shooting>0){
	      return new Game2(this.player,
			       this.low,
			       this.loe.attack(currEnemy.getEnemy(),"left"),
			       this.lop,
			       this.back);
	    } else {
	      return new Game2(this.player,
			       this.low.eShoot(eLoc,"left",currEnemy.getEnemy().type),
			       this.loe.attack(currEnemy.getEnemy(),"left"),
			       this.lop,
			       this.back);
	    }
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
	return new Game2(this.player,
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

    //    for (int i=0;i<this.loe.num();i++){
       
      for (int i=0;i<this.low.num();i++){
      if (!this.low.getWeapon().friendly){
	
	pLoc = this.player.center;
	hit = new Posn(Math.abs(wLoc.x-pLoc.x),
		       Math.abs(wLoc.y-pLoc.y));
	if (hit.x < 15 && hit.y < 20){
	  return new Game2(new Player(this.player.center,
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
	    return new Game2(this.player,
			     this.low.remove(currWeapon.getWeapon()),
			     this.loe.damage(currEnemy.getEnemy()),
			     this.lop,
			     this.back);
	  }
	  currEnemy = currEnemy.getNext();
	  eLoc = currEnemy.enemyLoc();
	}
	}
      currWeapon = currWeapon.getNext();
      wLoc = currWeapon.weaponLoc();
    }
    return this;
  }
  // Defines the initial setup of the game world and begins the game
  public static void main(String args[]){
    
    Game2 Stage1 = new Game2(new Player (new Posn (40,127),"right",0,0,0,10),
			     new noWeapon(),
			     new EnemyNode(new Enemy (new Posn (300,190),
						      "left","ET",0,2),
			     new EnemyNode(new Enemy (new Posn (450, 190),
						      "left","ET",0,2),
					   new noEnemy())),
			     Stage1Platforms,
			     new Background("Stage1"));
    
    Stage1.bigBang(width, height, 0.05);
  }
}

