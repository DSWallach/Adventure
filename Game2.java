import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.math.*;
import tester.*;
import javalib.funworld.*;
import javalib.colors.*;
import javalib.worldimages.*;
import java.io.*;

// convert input.gif -transparent white output.gif  

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
				       this.e.facing(),
				       this.e.shooting(),
				       this.e.health()-1));
	} else if (this.e.type().equals("DrRacket")){	  
	  return this.setEnemy (new DrRacket(this.e.center(),
					     this.e.floating(),
					     this.e.facing(),
					     this.e.shooting(),
					     this.e.health()-1));
	} else if (this.e.type().equals("JMin")){
	  return this.setEnemy(new JMin(this.e.center(),
					this.e.facing(),
					this.e.shooting(),
					this.e.health()-1));
	} else {
	  return this.setEnemy(new J(this.e.center(),
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
    if(enemy.type().equals("DrRacket") && this.e==enemy){
      if (this.e.shooting()>10){
	return new EnemyNode(new DrRacket(this.e.center(),
					  this.e.floating(),
					  this.e.facing(),
					  0,
					  this.e.health()),
			     new noEnemy());
      } else if (dir.equals("right")){
	return new EnemyNode(new DrRacket(this.e.center(),
					  this.e.floating(),
					  "right",
					  this.e.shooting()+1,
					  this.e.health()),
			     this.n);
      } else {
	return new EnemyNode(new DrRacket(this.e.center(),
					  this.e.floating(),
					  "left",
					  this.e.shooting()+1,
					  this.e.health()),
			     this.n);
      }
    } else if(this.e==enemy){
      if (this.getEnemy().shooting() > 0) {
	if (dir.equals("right")){
	  return new EnemyNode(new ET(this.e.center(),
				      "right",
				      this.e.shooting()-1,
				      this.e.health()),
			       this.n.attack(enemy, "right"));
	} else {
	  return new EnemyNode(new ET(this.e.center(),
				      "left",
				      this.e.shooting()-1,
				      this.e.health()),
			       this.n.attack(enemy, "left"));
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
  public boolean floating();
  public Enemy move(String dir);
  public int health();
  public int shooting();
  public String facing();
  public OverlayImages enemyDraw();
}
class JMin implements Enemy {

  FromFileImage blank  = new FromFileImage (new Posn(0,0), "blank.png");
  
  Posn center;
  String facing;
  int shooting;
  int health;

  JMin(Posn center, String facing, int shooting, int health){
    this.center = center;
    this.facing = facing;
    this.shooting = shooting;
    this.health = health;
  }
  public Posn center(){
    return this.center;
  }
  public String type(){
    return "JMin";
  }
  public boolean floating(){
    return false;
  }
  public Enemy move(String dir){
    if (this.shooting==11){
      return new JMin(this.center,
		      this.facing,
		      0,
		      this.health).move(dir);
    } else if (dir.equals("right")){
      return new JMin(new Posn (this.center.x + 7, this.center.y),
		      "right",
		      this.shooting+1,
		      this.health);
    } else if (dir.equals("left")){
      return new JMin(new Posn (this.center.x - 7, this.center.y),
		      "left",
		      this.shooting+1,
		      this.health);
    } else {
      return this;
    }
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
  public OverlayImages enemyDraw(){
    if (this.facing.equals("right")){
      if (this.shooting<6){
	return new OverlayImages(blank, new FromFileImage (this.center, "JMin.PNG"));
      } else {
	return new OverlayImages(blank, new FromFileImage (this.center, "JMin2.PNG"));
      }
    } else {
      if (this.shooting<6){
	return new OverlayImages(blank, new FromFileImage (this.center, "LJMin.PNG"));
      } else {
	return new OverlayImages(blank, new FromFileImage (this.center, "LJmin2.PNG"));
      }
    }
  }
}
class ET implements Enemy {

  FromFileImage blank  = new FromFileImage (new Posn(0,0), "blank.png");
  
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
  public boolean floating(){
    return false;
  }
  public String type(){
    return "ET";
  }
  public Enemy move(String dir){
    return this;
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
  public OverlayImages enemyDraw(){
      if (this.facing.equals("right")){
	if (this.shooting>0){
	  return new OverlayImages(blank, new FromFileImage (this.center,"ETShooting.PNG"));
	} else {
	  return new OverlayImages(blank, new FromFileImage (this.center,"ETStanding.PNG"));
	}
      } else {
	if (this.shooting>0){
	  return new OverlayImages(blank, new FromFileImage (this.center,"LETShooting.PNG"));
	} else {
	  return new OverlayImages(blank, new FromFileImage (this.center,"LETStanding.PNG"));
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
    this.floating = floating;
    this.facing = facing;
    this.shooting = shooting;
    this.health = health;
  }
  public boolean floating(){
    return this.floating;
  }
  public Posn center(){
    return this.center;
  }
  public String type(){
    return "DrRacket";
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
  public Enemy move(String dir){
    int dif = 3;
    
    if (dir.equals("float") && this.center.y > 25){
      return new DrRacket(new Posn(this.center.x, this.center.y-dif),
			  true,
			  this.facing,
			  this.shooting+1,
			  this.health);
    } else if (dir.equals("land") && this.center.y < 225){
    return new DrRacket(new Posn(this.center.x, this.center.y+dif),
			false,
			this.facing,
			  this.shooting+1,
			  this.health);
  } else if (dir.equals("up") && this.center.y > 25){
      return new DrRacket(new Posn(this.center.x, this.center.y-dif),
			  this.floating,
			  this.facing,
			  this.shooting,
			  this.health);
} else if (dir.equals("down") && this.center.y < 225){
      return new DrRacket(new Posn(this.center.x, this.center.y+dif),
			  this.floating,
			  this.facing,
			  this.shooting,
			  this.health);
    } else if (dir.equals("right") && this.center.x < 625){
      return new DrRacket(new Posn(this.center.x+dif, this.center.y),
			  this.floating,
			  this.facing,
			  this.shooting,
			  this.health);
    } else if (dir.equals("left") && this.center.x > 25){
      return new DrRacket(new Posn(this.center.x-dif, this.center.y),
			  this.floating,
			  this.facing,
			  this.shooting,
			  this.health);
    } else {
      return this;
    }
  }			 	
  public OverlayImages enemyDraw(){
    OverlayImages healthBar = new OverlayImages(new RectangleImage(new Posn(675, 125),
								   25, 220, Color.black),
						new RectangleImage(new Posn(675, 60),
								   15, this.health*4, 
								   Color.red));
    if (this.facing.equals("right")){
      if (this.shooting>1){
	if (this.floating){
	  return new OverlayImages(new FromFileImage(this.center, "DrRacketFireball.PNG"),
				   healthBar);
	} else {
	return new OverlayImages(new FromFileImage(this.center, "DrRacketShooting.PNG"),
				 healthBar);
	}
      } else {
      return new OverlayImages(new FromFileImage(this.center,"DrRacketFloating.PNG"),
			       healthBar);
      }
    } else {
      if (this.shooting>1){
	if (this.floating){
	  return new OverlayImages(new FromFileImage(this.center, "LDrRacketFireball.PNG"),
				   healthBar);
	} else {
	  return new OverlayImages(new FromFileImage(this.center, "LDrRacketShooting.PNG"),
				   healthBar);
	}
      } else {
	return new OverlayImages(new FromFileImage(this.center,"LDrRacketFloating.PNG"),
				 healthBar);
      }
    }
  }
}
class J implements Enemy {

  Posn center;
  Boolean floating;
  String facing;
  int shooting;
  int health;
  
  J (Posn center, Boolean floating, String facing, int shooting, int health){
    this.center = center;
    this.floating = floating;
    this.facing = facing;
    this.shooting = shooting;
    this.health = health;
  }
  public Posn center(){
    return this.center;
  }
  public String type(){
    return "J";
  }
  public boolean floating(){
    return this.floating;
  }
  public Enemy move(String dir){
    if (dir.equals("up") && this.center.y > 68){
      return new J(new Posn(this.center.x, this.center.y-5),
			  this.floating,
			  this.facing,
			  this.shooting,
			  this.health);
    } else if (dir.equals("down") && this.center.y < 182){
      return new J(new Posn(this.center.x, this.center.y+5),
			  this.floating,
			  this.facing,
			  this.shooting+1,
			  this.health);
    } else if (dir.equals("right") && this.center.x < 670){
      return new J(new Posn(this.center.x+5, this.center.y),
			  this.floating,
			  this.facing,
			  this.shooting+1,
			  this.health);
    } else if (dir.equals("left") && this.center.x > 30){
      return new J(new Posn(this.center.x-5, this.center.y),
			  this.floating,
			  this.facing,
			  this.shooting+1,
			  this.health);
    } else {
      return this;
    }
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
  public OverlayImages enemyDraw(){
    return new OverlayImages(new OverlayImages(new FromFileImage(this.center, "JMech.PNG"),
					       new RectangleImage(new Posn(675, 60),
								  25, 115, Color.black)),
			     new RectangleImage(new Posn(675, 60),
						15, this.health*2, 
						Color.red));
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
  public LoW eShoot(Posn e, Posn aim, String dir, String type);
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
    return null; 
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
  public LoW eShoot(Posn e, Posn aim, String dir, String type){
    if (type.equals("Racketball")){
      return new WeaponNode (new Weapon (false,
					 new Posn (e.x+37, e.y-26),
					 new Posn (aim.x - (e.x-24), aim.y - (e.y+54)),
					 type,
					 dir), this);
    } else {
      return eShoot(e, dir, type);
    }
  }
  public LoW eShoot(Posn e, String dir, String type){
    if (type.equals("ET")){
      if (dir.equals("right")){
	return new WeaponNode (new Weapon (false,
					   new Posn (e.x+5,e.y+7),
					   new Posn (0,0),
					   type,
					   dir), this);
      } else {
	return new WeaponNode (new Weapon (false,
					   new Posn (e.x-5,e.y+7),
					   new Posn (0,0),
					   type,
					   dir), this);
      }
    } else if (type.equals("Fireball")){
      if (dir.equals("right")){
	return new WeaponNode (new Weapon (false,
					   new Posn(e.x+5,e.y+5),
					   new Posn (0,0),
					   type,
					   dir), this);
      } else {
	return new WeaponNode (new Weapon (false,
					   new Posn(e.x-5,e.y+5),
					   new Posn (0,0),
					   type,
					   dir), this);
      }
    } else if (type.equals("Jball")){
      if (dir.equals("right")){
	return new WeaponNode (new Weapon (false,
					   new Posn(e.x+24,e.y+54),
					   new Posn (0,0),
					   type,
					   dir), this);
      } else {
	return new WeaponNode (new Weapon (false,
					   new Posn(e.x-24,e.y+54),
					   new Posn (0,0),
					   type,
					   dir), this);
      }
    } else if (type.equals("RacketBall")){
      return new WeaponNode (new Weapon (false,
					 new Posn (e.x-24, e.y+56),
					 new Posn (0,0),
					 type,
					 dir), this);
    } else {
      if (dir.equals("right")){
	return new WeaponNode (new Weapon (false,
					   new Posn (e.x+37,e.y-26),
					   new Posn (0,0),
					   type,
					   dir), this);
      } else {
	return new WeaponNode (new Weapon (false,
					   new Posn (e.x-37,e.y-26),
					   new Posn (0,0),
					   type,
					   dir), this);
      }
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
  public LoW eShoot(Posn e, Posn aim, String dir, String type){
    if (type.equals("Racketball")){
      return new WeaponNode (new Weapon (false,
					 new Posn (e.x-24, e.y+54),
					 new Posn (aim.x - (e.x-24), aim.y - (e.y+54)),
					 type,
					 dir), this);
    } else {
      return eShoot(e, dir, type);
    }
  }
  public LoW eShoot (Posn e, String dir, String type){
    if (type.equals("ET")){
      if (dir.equals("right")){
	return new WeaponNode (new Weapon (false,
					   new Posn (e.x+5,e.y+7),
					   new Posn (0,0),
					   type,
					   dir), this);
      } else {
	return new WeaponNode (new Weapon (false,
					   new Posn (e.x-5,e.y+7),
					   new Posn (0,0),
					   type,
					   dir), this);
      }
    } else if (type.equals("Fireball")){
      if (dir.equals("right")){
	return new WeaponNode (new Weapon (false,
					   new Posn(e.x+5,e.y+5),
					   new Posn (0,0),
					   type,
					   dir), this);
      } else {
	return new WeaponNode (new Weapon (false,
					   new Posn(e.x-5,e.y+5),
					   new Posn (0,0),
					   type,
					   dir), this);
      }
    } else if (type.equals("Jball")){
       if (dir.equals("right")){
	return new WeaponNode (new Weapon (false,
					   new Posn(e.x+24,e.y+54),
					   new Posn (0,0),
					   type,
					   dir), this);
      } else {
	return new WeaponNode (new Weapon (false,
					   new Posn(e.x-24,e.y+54),
					   new Posn (0,0),
					   type,
					   dir), this);
      }
    } else {
      if (dir.equals("right")){
	return new WeaponNode (new Weapon (false,
					   new Posn (e.x+37,e.y-26),
					   new Posn (0,0),
					   type,
					   dir), this);
      } else {
	return new WeaponNode (new Weapon (false,
					   new Posn (e.x-37,e.y-26),
					   new Posn (0,0),
					   type,
					   dir), this);
      }
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
  Posn dir;
  String type;
  String facing;
  
  Weapon(Boolean friendly, Posn center, Posn dir,String type, String facing){
    this.friendly = friendly;
    this.center = center;
    this.dir = dir;
    this.type = type;
    this.facing = facing;
  }
  
  public FromFileImage weaponDraw(){
    if (this.friendly){
      return new FromFileImage (this.center,"BusterShot.PNG");
    } else {
      if (this.type.equals("Racketball")){
	return new FromFileImage(this.center, "Racketball.PNG");
      } else if (this.type.equals("Lambda")){
	if (this.facing.equals("right")){
	  return new FromFileImage(this.center, "Lambda.PNG");
	} else {
	  return new FromFileImage(this.center, "LLambda.PNG");
	}
      } else if (this.type.equals("ET")){
	return new FromFileImage (this.center, "Eweapon.PNG");
      } else if (this.type.equals("Fireball")){
	if (this.facing.equals("right")){
	  return new FromFileImage (this.center,"Fireball.PNG");
	} else {
	  return new FromFileImage (this.center,"LFireball.PNG");
	}
      } else if (this.type.equals("Jball")){
	if (this.facing.equals("right")){
	  return new FromFileImage (this.center,"Jball.PNG");
	} else {
	  return new FromFileImage (this.center,"LJball.PNG");
	}
      } else {
	return new FromFileImage(this.center,"blank.PNG");
      }
    }
  }
  public Weapon moveWeapon(){
    if (this.type.equals("Racketball")){
      	return new Weapon(this.friendly,
			  new Posn(this.center.x+(this.dir.x/20),
				   this.center.y+(this.dir.y/20)),
			  this.dir,
			  this.type,
			  this.facing);
    } else if (this.type.equals("Fireball")){
      if (this.facing.equals("right")){
	return new Weapon(this.friendly,
			  new Posn(this.center.x+15,
				   this.center.y+10),
			  this.dir,
			  this.type,
			  this.facing);
      } else {
	return new Weapon(this.friendly,
			  new Posn(this.center.x-15,
				   this.center.y+10),
			  this.dir,
			  this.type,
			  this.facing);
      }
    }  else if (this.type.equals("Jball")){
      if (this.facing.equals("right")){
	return new Weapon(this.friendly,
			  new Posn(this.center.x+15,
				   this.center.y+10),
			  this.dir,
			  this.type,
			  this.facing);
      } else {
	return new Weapon(this.friendly,
			  new Posn(this.center.x-15,
				   this.center.y+10),
			  this.dir,
			  this.type,
			  this.facing);
      }
    } else {
      if (this.facing.equals("right")){
	return new Weapon(this.friendly,
			  new Posn(this.center.x + 10,this.center.y),
			  this.dir,
			  this.type,this.facing);
      } else {
	return new Weapon(this.friendly,
			  new Posn(this.center.x - 10,this.center.y),
			  this.dir,
			  this.type,this.facing);
      }
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
    if (ke.equals("right") && this.center.x < 700){
      return new Player(new Posn(this.center.x + 10, this.center.y),
			"right",
			this.running+1,
			0,
			this.jumping,
			this.health);
    } else if (ke.equals("left") && this.center.x > 0){
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
    } else if (ke.equals("down")){
      return new Player(this.center,
			this.facing,
			0,
			0,
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
  public int heightHere(int x, int y);
  public boolean platformHere(int x, int y);
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
  public int heightHere(int x, int y){
    return 250;
  }
  public boolean platformHere(int x, int y){
    return false;
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
  public int heightHere(int x, int y){
    if (this.platformHere(x, y)){
      return this.p.height.x;
    } else {
      return this.getNext().heightHere(x, y);
    }
  }
  public boolean platformHere(int x, int y){
    if (this.p.width.x < x && x < this.p.width.y && this.p.height.x < y && this.p.height.y > y){
      return true;
    } else {
      return this.getNext().platformHere(x,y);
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
  Posn height;
  Posn width;
  Platform(Posn height, Posn width){
    this.height = height;
    this.width = width;
  }
}
class Game2 extends World {
  
  static int width = 700;
  static int height = 250;
  static PlatformNode Stage1Platforms =
    new PlatformNode(new Platform(new Posn(147,167),
				  new Posn(0,86)),
    new PlatformNode(new Platform(new Posn(179,199),
				  new Posn(86,151)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(151,313)),
    new PlatformNode(new Platform(new Posn(226,246),
				  new Posn(313,379)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(379,541)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(574,700)),
		     new noPlatform()))))));
  
  static PlatformNode Stage2Platforms =
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(0,45)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(78,139)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(173,234)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(268,329)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(363,425)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(458,519)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(553,700)),
		     new noPlatform())))))));
  
  static PlatformNode StageRPlatforms =
    new PlatformNode(new Platform(new Posn(117,137),
				  new Posn(0,43)),
    new PlatformNode(new Platform(new Posn(149,169),
				  new Posn(28,75)),
    new PlatformNode(new Platform(new Posn(181,201),
				  new Posn(60,139)),
    new PlatformNode(new Platform(new Posn(213,233),
				  new Posn(124,635)),
    new PlatformNode(new Platform(new Posn(181,201),
				  new Posn(623,670)),
    new PlatformNode(new Platform(new Posn(117,137),
				  new Posn(652,700)),
		     new noPlatform()))))));
  
  static PlatformNode Stage3Platforms =
    new PlatformNode(new Platform(new Posn(145,165),
				  new Posn(0,53)),
    new PlatformNode(new Platform(new Posn(115,135),
				  new Posn(53,150)),
    new PlatformNode(new Platform(new Posn(178,198),
				  new Posn(199,278)),
    new PlatformNode(new Platform(new Posn(130,150),
				  new Posn(311,378)),
    new PlatformNode(new Platform(new Posn(82,102),
				  new Posn(406,454)),
    new PlatformNode(new Platform(new Posn(194,214),
				  new Posn(406,439)),
    new PlatformNode(new Platform(new Posn(194,214),
				  new Posn(471,502)),
    new PlatformNode(new Platform(new Posn(165,185),
				  new Posn(535,566)),
    new PlatformNode(new Platform(new Posn(178,198),
				  new Posn(583,700)),
		     new noPlatform())))))))));
  static PlatformNode Stage4Platforms =
    new PlatformNode(new Platform(new Posn(178,198),
				  new Posn (0,700)),
		     new noPlatform());
  static PlatformNode StageJPlatforms =
    new PlatformNode(new Platform(new Posn(178,198),
				  new Posn(0,22)),
    new PlatformNode(new Platform(new Posn(103,123),
				  new Posn(82,179)),
    new PlatformNode(new Platform(new Posn(103,123),
				  new Posn(523,620)),
    new PlatformNode(new Platform(new Posn(150,170),
				  new Posn(556,620)),
    new PlatformNode(new Platform(new Posn(150,170),
				  new Posn(82,146)),
    new PlatformNode(new Platform(new Posn(120,140),
				  new Posn(82,115)),
    new PlatformNode(new Platform(new Posn(120,140),
				  new Posn(587,620)),
    new PlatformNode(new Platform(new Posn(210,230),
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
    if (ke.equals("s")){
      if (this.player.facing.equals("right")){
	return new Game2 (this.player.move(ke),
			  new WeaponNode(new Weapon(true,
						    new Posn(this.player.center.x+5,
							     this.player.center.y+7),
						    new Posn (0,0),
						    "Buster","right"), this.low).move(),
			  this.loe,
			  this.lop,
			  this.back).gravity().hit();
      } else {
	return new Game2 (this.player.move(ke),
			  new WeaponNode(new Weapon(true,
						    new Posn(this.player.center.x-5,
							     this.player.center.y+7),
						    new Posn (0,0),
						    "Buster","left"), this.low).move(),
			  this.loe,
			  this.lop,
			  this.back).gravity().hit();
      }
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
      return new Game2(new Player (new Posn (10,190),"right",0,0,0,this.player.health),
		       new noWeapon(),
		       new EnemyNode(new ET (new Posn (300,190),
						"left",0,5),
		       new EnemyNode(new ET (new Posn (500, 190),
						"left",0,5),
				     new noEnemy())),
		       Stage2Platforms,
		       new Background("Stage2"));
    } else if (this.player.center.x>690 && this.back.stage.equals("Stage2")){
      return new Game2(new Player (new Posn (10,97),"right",0,0,0,this.player.health),
		       new noWeapon(),
		       new EnemyNode(new DrRacket(new Posn(350,125),
					    false,
					    "left",
					    0,
					    25), new noEnemy()),
		       StageRPlatforms,
		       new Background("StageR"));
    } else if (this.player.center.x>690 && this.back.stage.equals("StageR")){
      return new Game2(new Player (new Posn (20,135),"right",0,0,0,this.player.health),
		       new noWeapon(),
		       new EnemyNode(new ET (new Posn (251,158),
						"left",0,5),
		       new EnemyNode(new ET (new Posn (485,174),
						"left",0,5),
		       new EnemyNode(new ET (new Posn(550,142),
					     "left",0,5),
		       new EnemyNode(new ET (new Posn(610,158),
					     "left",0,5),						   
				     new noEnemy())))),
		       Stage3Platforms,
		       new Background("Stage3"));
      
    } else if (this.player.center.x>690 && this.back.stage.equals("Stage3")){
      return  new Game2(new Player (new Posn (20,158),"right",0,0,0,this.player.health),
			      new noWeapon(),
			      new noEnemy(),
			      Stage4Platforms,
			      new Background("Stage4"));

    } else if (this.player.center.x>690 && this.back.stage.equals("Stage4")){
      return new Game2(new Player (new Posn (10,158),"right",0,0,0,this.player.health),
			     new noWeapon(),
			     new EnemyNode(new J(new Posn(550,75),
							false,
							"left",
							0,
							50), new noEnemy()),
			     StageJPlatforms,
			     new Background("StageJ"));
    } else {
      return new Game2(this.player,
		       this.low.move(),
		       this.loe,
		       this.lop,
		       this.back).gravity().AI().hit();
    }
  }
  
// Overlays the images of each of the game objects 
  public WorldImage makeImage(){
    return  new OverlayImages(
		    new OverlayImages(
			new OverlayImages(
			    new OverlayImages(
				new OverlayImages(
				    new OverlayImages(blank,
						      this.back.draw()),
				    this.loe.listDraw(blank)),
				this.player.draw()),
			    this.low.listDraw(blank)),
			new RectangleImage(new Posn(25, 60),
					   25, 85, Color.black)),
		    new RectangleImage(new Posn(25, 60),
				       15, this.player.health*3, 
				       Color.blue));
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
    } else if (this.lop.platformHere(this.player.center.x, this.player.center.y+20)){
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
    } else {
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
    }
  }
  public Game2 RacketAI(){
    LoE currEnemy = this.loe;
    Posn eLoc = currEnemy.enemyLoc();
    Posn pLoc = this.player.center;
    Random newRandom = new Random();
    int rand = newRandom.nextInt(100);
    if (currEnemy.getEnemy().shooting()<1 && rand < 50 ){
      return new Game2(this.player,
		       this.low,
		       this.loe.setEnemy(currEnemy.getEnemy().move("float")),
		       this.lop,
		       this.back).LoWClean();
    } else if (currEnemy.getEnemy().shooting()<1){
      return new Game2(this.player,
		       this.low,
		       this.loe.setEnemy(currEnemy.getEnemy().move("land")),
		       this.lop,
		       this.back);    
    } else if (currEnemy.getEnemy().floating()){
       if (currEnemy.getEnemy().center().y>this.player.center.y-100){
	return new Game2(this.player,
			 this.low,
			 this.loe.setEnemy(currEnemy.getEnemy().move("up")),
			 this.lop,
			 this.back);
      } else {
	if (currEnemy.getEnemy().center().x>pLoc.x &&
	    (currEnemy.getEnemy().shooting() % 2 == 0)){
	  return new Game2(this.player,
			   this.low.eShoot(eLoc,"left","Fireball"),
			   this.loe.attack(currEnemy.getEnemy(),
					   "left"),
			   this.lop,
			   this.back);
	} else if (currEnemy.getEnemy().shooting() % 2 == 0) {
	  return new Game2(this.player,
			   this.low.eShoot(eLoc,"right","Fireball"),
			   this.loe.attack(currEnemy.getEnemy(),
					   "right"),
			   this.lop,
			   this.back);
	} else {
	  return new Game2(this.player,
			   this.low,
			   this.loe.attack(currEnemy.getEnemy(),
					   currEnemy.getEnemy().facing()),
			   this.lop,
			   this.back);
	}
      }
    } else {
      if (currEnemy.getEnemy().center().y<this.player.center.y-25){
	return new Game2(this.player,
			 this.low,
			 this.loe.setEnemy(currEnemy.getEnemy().move("down")),
			 this.lop,
			 this.back);
      } else if (currEnemy.getEnemy().center().y>this.player.center.y){
	return new Game2(this.player,
			 this.low,
			 this.loe.setEnemy(currEnemy.getEnemy().move("up")),
			 this.lop,
			 this.back);
      } else {
	if (currEnemy.getEnemy().center().x>pLoc.x &&
	    (currEnemy.getEnemy().shooting() % 2 == 0)){
	  return new Game2(this.player,
			   this.low.eShoot(eLoc,"left","Lambda"),
			   this.loe.attack(currEnemy.getEnemy(),
					   "left"),
			   this.lop,
			   this.back);
	} else if (currEnemy.getEnemy().shooting() % 2 == 0) {
	  return new Game2(this.player,
			   this.low.eShoot(eLoc,"right","Lambda"),
			   this.loe.attack(currEnemy.getEnemy(),
					   "right"),
			   this.lop,
			   this.back);
	} else {
	  return new Game2(this.player,
			   this.low,
			   this.loe.attack(currEnemy.getEnemy(),
					   currEnemy.getEnemy().facing()),
			   this.lop,
			   this.back);
	}
      }
    }
  }
  public Game2 JAI(){
 
    Enemy currEnemy = this.loe.getEnemy();
    Posn eLoc = this.loe.enemyLoc();
    Posn pLoc = this.player.center;
    Random newRandom = new Random();
    int rand = newRandom.nextInt(100);
    if (currEnemy.shooting()==75 && currEnemy.center().x > 550){
      return new Game2 (this.player,
			this.low,
			this.loe.setEnemy(new J(currEnemy.center(),
						currEnemy.floating(),
						currEnemy.facing(),
						0,
						currEnemy.health())),
			this.lop,
			this.back);
    } else if (currEnemy.center().x < 100 || currEnemy.shooting()==75){
      return new Game2(this.player,
		       this.low,
		       this.loe.setEnemy(new J(new Posn (currEnemy.center().x+5,
							 currEnemy.center().y),
						currEnemy.floating(),
						currEnemy.facing(),
						75,
						currEnemy.health())),
		       this.lop,
		       this.back);
    } else if (currEnemy.shooting()<1){
      if (rand < 20){
	return new Game2(this.player,
			 this.low,
			 this.loe.setEnemy(new J(currEnemy.center(),
						 currEnemy.floating(),
						 currEnemy.facing(),
						 75,
						 currEnemy.health())).setFirst(new JMin(new Posn(680,179),
											"left",
											0,
											2)),
			 this.lop,
			 this.back);
      } else if (rand < 60){
	return new Game2(this.player,
			 this.low,
			 this.loe.setEnemy(new J(currEnemy.center(),
						 true,
						 currEnemy.facing(),
						 currEnemy.shooting()+1,
						 currEnemy.health())),
			 this.lop,
			 this.back);
      } else {
	return new Game2(this.player,
			 this.low,
			 this.loe.setEnemy(new J(currEnemy.center(),
						 false,
						 currEnemy.facing(),
						 currEnemy.shooting()+1,
						 currEnemy.health())),
			 this.lop,
			 this.back);
      }
    }  else if (currEnemy.floating()){
      if (currEnemy.shooting() % 2 == 0){
	return new Game2(this.player,
			 this.low.eShoot(eLoc,"left","Jball"),
			 this.loe.setEnemy(currEnemy.move("left")),
			 this.lop,
			 this.back);

      } else {
	return new Game2(this.player,
			 this.low,
			 this.loe.setEnemy(currEnemy.move(currEnemy.facing())),
			 this.lop,
			 this.back);
      }      
    } else {
      if (currEnemy.shooting() % 10 == 0){
	return new Game2(this.player,
			 this.low.eShoot(eLoc, pLoc, "left","Racketball"),
			 this.loe.setEnemy(new J(currEnemy.center(),
						 false,
						 currEnemy.facing(),
						 currEnemy.shooting()+1,
						 currEnemy.health())),
			 this.lop,
			 this.back);
      } else {
	return new Game2(this.player,
			 this.low,
			 this.loe.setEnemy(new J(currEnemy.center(),
						 currEnemy.floating(),
						 currEnemy.facing(),
						 currEnemy.shooting()+1,
						 currEnemy.health())),
			 this.lop,
			 this.back);
      }      
    }
  }
  public Game2 AI(){
    LoE currEnemy = this.loe;
    Posn eLoc = currEnemy.enemyLoc();
    Posn pLoc = this.player.center;
    for (int i = 0; i<this.loe.num();i++){
      if (currEnemy.getEnemy().type().equals("DrRacket")){
	return this.RacketAI();
      } else if (currEnemy.getEnemy().type().equals("J")){
	return this.JAI();
      } else if (currEnemy.getEnemy().type().equals("JMin")){
	if(eLoc.x < 100 && currEnemy.getEnemy().facing().equals("left")){
	  return new Game2(this.player,
			   this.low,
			   this.loe.setEnemy(currEnemy.getEnemy().move("right")),
			   this.lop,
			   this.back);
	} else if (eLoc.x > 650 && currEnemy.getEnemy().facing().equals("right")){
	  	  return new Game2(this.player,
			   this.low,
			   this.loe.setEnemy(currEnemy.getEnemy().move("left")),
				   this.lop,
				   this.back);
	} else {
	  return new Game2(this.player,
			   this.low,
			   this.loe.setEnemy(currEnemy.getEnemy().move(currEnemy.getEnemy().facing())),
			   this.lop,
			   this.back);
	}
      } else {
	if (Math.abs(pLoc.y - eLoc.y)<50){
	  if (0 < (pLoc.x - eLoc.x) &&
	      (pLoc.x - eLoc.x) < 160){
	    if (currEnemy.getEnemy().shooting()>0){
	      return new Game2(this.player,
			       this.low,
			       this.loe.attack(currEnemy.getEnemy(),"right"),
			       this.lop,
			       this.back);
	    } else {
	      return new Game2(this.player,
			       this.low.eShoot(eLoc,"right",currEnemy.getEnemy().type()),
			       this.loe.attack(currEnemy.getEnemy(),"right"),
			       this.lop,
			       this.back);
	    }
	  } else if (-160 < (pLoc.x - eLoc.x) &&
		     (pLoc.x - eLoc.x) < 0){
	    if (currEnemy.getEnemy().shooting()>0){
	      return new Game2(this.player,
			       this.low,
			       this.loe.attack(currEnemy.getEnemy(),"left"),
			       this.lop,
			       this.back);
	    } else {
	      return new Game2(this.player,
			       this.low.eShoot(eLoc,"left",currEnemy.getEnemy().type()),
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

    for (int i=0;i<this.low.num();i++){
      if (!this.low.getWeapon().friendly){	
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
	  if (Math.abs(eLoc.x-pLoc.x) < 15 && Math.abs(eLoc.y-pLoc.y) < 20){
	    return new Game2(new Player(this.player.center,
					this.player.facing,
					this.player.running,
					this.player.shooting,
					this.player.jumping,
					this.player.health-1),
			     this.low,
			     this.loe,
			     this.lop,
			     this.back);
	  } else {
	    hit = new Posn(Math.abs(wLoc.x-eLoc.x),
			   Math.abs(wLoc.y-eLoc.y));

	    if (currEnemy.getEnemy().type().equals("ET")
		&& (hit.x < 15 && hit.y < 20)){
	      return new Game2(this.player,
			       this.low.remove(currWeapon.getWeapon()),
			       this.loe.damage(currEnemy.getEnemy()),
			       this.lop,
			       this.back);

	    } else if (currEnemy.getEnemy().type().equals("DrRacket")
		       && (hit.x < 15 && hit.y < 50)) {
	      return new Game2(this.player,
			       this.low.remove(currWeapon.getWeapon()),
			       this.loe.damage(currEnemy.getEnemy()),
			       this.lop,
			       this.back);
	    } else if (currEnemy.getEnemy().type().equals("J")
		       && (hit.x < 15 && hit.y < 50)) {
	      return new Game2(this.player,
			       this.low.remove(currWeapon.getWeapon()),
			       this.loe.damage(currEnemy.getEnemy()),
			       this.lop,
			       this.back);
	    } else if (currEnemy.getEnemy().type().equals("JMin")
		       && (hit.x < 15 && hit.y < 25)) {
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
      }
      currWeapon = currWeapon.getNext();
      wLoc = currWeapon.weaponLoc();
    }
    return this;
  }
  // Defines the initial setup of the game world and begins the game
  public static void main(String args[]){
    
    Game2 Stage1 = new Game2(new Player (new Posn (40,127),"right",0,0,0,25),
			     new noWeapon(),
			     new EnemyNode(new ET (new Posn (300,190),
						      "left",0,5),
			     new EnemyNode(new ET (new Posn (450, 190),
						      "left",0,5),
					   new noEnemy())),
			     Stage1Platforms,
			     new Background("Stage1"));
    
    Stage1.bigBang(width, height, 0.05);
  }
}

class AdventureTest{
  // int's for the width and height of the Game World
  static int width = 700;
  static int height = 250;

  // Lists of platforms for each of the stages
  static PlatformNode Stage1Platforms =
    new PlatformNode(new Platform(new Posn(147,167),
				  new Posn(0,86)),
    new PlatformNode(new Platform(new Posn(179,199),
				  new Posn(86, 151)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(151,313)),
    new PlatformNode(new Platform(new Posn(226,246),
				  new Posn(313,379)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(379, 541)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(574, 700)),
		     new noPlatform()))))));
  
  static PlatformNode Stage2Platforms =
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(0,45)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(78,139)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(173,234)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(268,329)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(363,425)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn (458,519)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(553,700)),
		     new noPlatform())))))));
  
  static PlatformNode StageRPlatforms =
    new PlatformNode(new Platform(new Posn(117,137),
				  new Posn(0,43)),
    new PlatformNode(new Platform(new Posn(149,169),
				  new Posn(28,75)),
    new PlatformNode(new Platform(new Posn(181,201),
				  new Posn(60,139)),
    new PlatformNode(new Platform(new Posn(213,233),
				  new Posn(124,635)),
    new PlatformNode(new Platform(new Posn(181,201),
				  new Posn(623,670)),
    new PlatformNode(new Platform(new Posn(117,137),
				  new Posn(652,700)),
		     new noPlatform()))))));
  
  static PlatformNode Stage3Platforms =
    new PlatformNode(new Platform(new Posn(145,165),
				  new Posn(0,53)),
    new PlatformNode(new Platform(new Posn(115,135),
				  new Posn(53,150)),
    new PlatformNode(new Platform(new Posn(178,198),
				  new Posn(199,278)),
    new PlatformNode(new Platform(new Posn(130,150),
				  new Posn(311,378)),
    new PlatformNode(new Platform(new Posn(82,102),
				  new Posn(406,454)),
    new PlatformNode(new Platform(new Posn(194,214),
				  new Posn(406,439)),
    new PlatformNode(new Platform(new Posn(194,214),
				  new Posn(471,502)),
    new PlatformNode(new Platform(new Posn(165,185),
				  new Posn(535,566)),
    new PlatformNode(new Platform(new Posn(178,198),
				  new Posn(583,700)),
		     new noPlatform())))))))));
  static PlatformNode Stage4Platforms =
    new PlatformNode(new Platform(new Posn(178,198),
				  new Posn (0,700)),
		     new noPlatform());
  static PlatformNode StageJPlatforms =
    new PlatformNode(new Platform(new Posn(178,198),
				  new Posn(0,22)),
    new PlatformNode(new Platform(new Posn(103,123),
				  new Posn(82,179)),
    new PlatformNode(new Platform(new Posn(103,123),
				  new Posn(523,620)),
    new PlatformNode(new Platform(new Posn(150,170),
				  new Posn(556,620)),
    new PlatformNode(new Platform(new Posn(150,170),
				  new Posn(82,146)),
    new PlatformNode(new Platform(new Posn(120,140),
				  new Posn(82,115)),
    new PlatformNode(new Platform(new Posn(120,140),
				  new Posn(587,620)),
    new PlatformNode(new Platform(new Posn(210,230),
				  new Posn(0,700)),
		     new noPlatform()))))))));

  // The six stages in the game

  Game2 Stage1 = new Game2(new Player (new Posn (40,132),"right",0,0,0,25),
			     new noWeapon(),
			     new EnemyNode(new ET (new Posn (300,190),
						      "left",0,5),
			     new EnemyNode(new ET (new Posn (450, 190),
						      "left",0,5),
					   new noEnemy())),
			     Stage1Platforms,
			     new Background("Stage1"));
  
  Game2 Stage2 = new Game2(new Player (new Posn (10,190),"right",0,0,0,25),
		       new noWeapon(),
		       new EnemyNode(new ET (new Posn (300,190),
						"left",0,5),
		       new EnemyNode(new ET (new Posn (500, 190),
						"left",0,5),
				     new noEnemy())),
		       Stage2Platforms,
		       new Background("Stage2"));
  
  Game2 StageR = new Game2(new Player (new Posn (10,97),"right",0,0,0,25),
		       new noWeapon(),
		       new EnemyNode(new DrRacket(new Posn(350,125),
					    false,
					    "left",
					    0,
					    25), new noEnemy()),
		       StageRPlatforms,
		       new Background("StageR"));
  
  Game2 Stage3 = new Game2(new Player (new Posn (20,135),"right",0,0,0,25),
		       new noWeapon(),
		       new EnemyNode(new ET (new Posn (251,158),
						"left",0,5),
		       new EnemyNode(new ET (new Posn (485,174),
						"left",0,5),
		       new EnemyNode(new ET (new Posn(550,142),
					     "left",0,5),
		       new EnemyNode(new ET (new Posn(610,158),
					     "left",0,5),						   
				     new noEnemy())))),
		       Stage3Platforms,
		       new Background("Stage3"));
  
  Game2 Stage4 = new Game2(new Player (new Posn (20,158),"right",0,0,0,25),
			      new noWeapon(),
			      new noEnemy(),
			      Stage4Platforms,
			      new Background("Stage4"));

  Game2 StageJ = new Game2(new Player (new Posn (10,158),"right",0,0,0,25),
			   new noWeapon(),
			   new EnemyNode(new J(new Posn(550,75),
					       false,
					       "left",
					       0,
					       50), new noEnemy()),
			   StageJPlatforms,
			   new Background("StageJ"));
  Player StartingPlayer = new Player (new Posn (40,132),"right",0,0,0,25);

  boolean testPlayer(Tester t){
    String[] stringArray;
    stringArray = new String[5];
    stringArray[0] = "s";
    stringArray[1] = "up";
    stringArray[2] = "down";
    stringArray[3] = "left";
    stringArray[4] = "right";
    boolean passed = false;
    
    for (int i = 0; i < 300; i++){

      Random randomInput = new Random();
      int randInput = randomInput.nextInt(5);
      String input = stringArray[randInput];
      Random randomX = new Random();
      int randX = randomX.nextInt(701);
      Random randomY = new Random();
      int randY = randomY.nextInt(251);
      Random randomJ = new Random();
      int randJ = randomJ.nextInt(2);
      
      if (input.equals("right")){
	if (randX == 700){
	  passed = t.checkExpect(new Player (new Posn (randX,randY),"right",0,0,randJ,25).move(input),
				 new Player (new Posn (randX,randY),"right",1,0,randJ,25),
				 "Test MovePlayer - Right");
	} else {
	  passed = t.checkExpect(new Player (new Posn (randX,randY),"right",0,0,randJ,25).move(input),
				 new Player (new Posn (randX+10,randY),"right",1,0,randJ,25),
				 "Test MovePlayer - Right");
	}
      } else if (input.equals("left")){
	if (randX == 0){
	  passed = t.checkExpect(new Player (new Posn (randX,randY),"right",0,0,randJ,25).move(input),
				 new Player (new Posn (randX,randY),"right",1,0,randJ,25),
				 "Test MovePlayer - Left");
	} else {
	  passed = t.checkExpect(new Player (new Posn (randX,randY),"right",0,0,randJ,25).move(input),
				 new Player (new Posn (randX-10,randY),"left",1,0,randJ,25),
				 "Test MovePlayer - Left");
	}
      } else if (input.equals("up")){
	if (randJ == 0){
	  passed = t.checkExpect(new Player (new Posn (randX,randY),"right",0,0,randJ,25).move("up"),
				 new Player (new Posn (randX,randY),"right",0,0,randJ+1,25),
				 "Test MovePlayer - Jump");
	} else {
	  passed = t.checkExpect(new Player (new Posn (randX,randY),"right",0,0,randJ,25).move("up"),
				 new Player (new Posn (randX,randY),"right",0,0,randJ,25),
				 "Test MovePlayer - Jump While Jumping");
	}
      } else if (input.equals("down")){
	passed = t.checkExpect(new Player (new Posn (randX,randY),"right",1,0,0,25).move("down"),
			       new Player (new Posn (randX,randY),"right",0,0,0,25),
			       "Test MovePlayer - Holster weapon and Stand Still");
      } else {
	if (randJ == 0){
	  passed = t.checkExpect(new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   new PlatformNode(new Platform(new Posn(randY-30,randY+40),
									 new Posn(randX-20,randX+20)),
							    new noPlatform()),
					   new Background("Stage1")).onKeyEvent("s"),
				 new Game2(new Player (new Posn (randX,randY),"right",0,1,0,25),
					   new WeaponNode(new Weapon(true,
								     new Posn(randX+5,randY+7),
								     new Posn (0,0),
								     "Buster","right"), new noWeapon()).move(),
					   new noEnemy(),
					   new PlatformNode(new Platform(new Posn(randY-30,randY+40),
									 new Posn(randX-20,randX+20)),
							    new noPlatform()),
					   new Background("Stage1")),
				 "Test Player Shooting - Shooting Right");
	} else {
	  passed = t.checkExpect(new Game2(new Player (new Posn (randX,randY),"left",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   new PlatformNode(new Platform(new Posn(randY-30,randY+40),
									 new Posn(randX-20,randX+20)),
							    new noPlatform()),
					   new Background("Stage1")).onKeyEvent("s"),
				 new Game2(new Player (new Posn (randX,randY),"left",0,1,0,25),
					   new WeaponNode(new Weapon(true,
								     new Posn(randX-5,randY+7),
								     new Posn (0,0),
								     "Buster","left"), new noWeapon()).move(),
					   new noEnemy(),
					   new PlatformNode(new Platform(new Posn(randY-30,randY+40),
									 new Posn(randX-20,randX+20)),
							    new noPlatform()),
					   new Background("Stage1")),
				 "Test Player Shooting - Shooting Left");
	}
      }
    }
    return passed;
  }
  boolean testGravity(Tester t){
    boolean passed = false;
    for (int i = 0; i < 300; i++){
      
      Random randomX = new Random();
      int randX = randomX.nextInt(701);
      Random randomY = new Random();
      int randY = randomY.nextInt(251);
      Random randomPX = new Random();
      int randPX = randomPX.nextInt(701);
      Random randomPY = new Random();
      int randPY = randomPY.nextInt(251);
      Random random = new Random();
      int rand = random.nextInt(100);
      int randJ;
      if (rand < 50){
	Random randomJ = new Random();
	randJ = randomJ.nextInt(30);
      } else {
	randJ = 0;
      }
      if (randJ == 0 && randPX-20 < randX && randX < randPX+20 && randPY-10 < randY+20 && randY+20 < randPY+10){
	passed = t.checkExpect( new Game2(new Player (new Posn (randX,randY),"left",0,0,randJ,25),
					  new noWeapon(),
					  new noEnemy(),
					  new PlatformNode(new Platform(new Posn(randPY-10,randPY+10),
									new Posn(randPX-20,randPX+20)),
							   new noPlatform()),
					  new Background("Stage1")).gravity(),
				new Game2(new Player (new Posn (randX,randY),"left",0,0,randJ,25),
					  new noWeapon(),
					  new noEnemy(),
					  new PlatformNode(new Platform(new Posn(randPY-10,randPY+10),
									new Posn(randPX-20,randPX+20)),
							   new noPlatform()),
					  new Background("Stage1")),
				"Test Gravity() - Not Jumping, On a Platform");
      } else if (0 < randJ && randJ < 15){
	passed = t.checkExpect( new Game2(new Player (new Posn (randX,randY),"left",0,0,randJ,25),
					  new noWeapon(),
					  new noEnemy(),
					  new PlatformNode(new Platform(new Posn(randPY-10,randPY+10),
									new Posn(randPX-20,randPX+20)),
							   new noPlatform()),
					  new Background("Stage1")).gravity(),
				new Game2(new Player (new Posn (randX,randY-5),"left",0,0,randJ+1,25),
					  new noWeapon(),
					  new noEnemy(),
					  new PlatformNode(new Platform(new Posn(randPY-10,randPY+10),
									new Posn(randPX-20,randPX+20)),
							   new noPlatform()),
					  new Background("Stage1")),
				"Test Gravity() - Jumping, On a Platform");
      } else if (randPX-20 < randX && randX < randPX+20 && randPY-10 < randY+20 && randY+20 < randPY+10){
	passed = t.checkExpect( new Game2(new Player (new Posn (randX,randY),"left",0,0,randJ,25),
					  new noWeapon(),
					  new noEnemy(),
					  new PlatformNode(new Platform(new Posn(randPY-10,randPY+10),
									new Posn(randPX-20,randPX+20)),
							   new noPlatform()),
					  new Background("Stage1")).gravity(),
				new Game2(new Player (new Posn (randX,randY),"left",0,0,0,25),
					  new noWeapon(),
					  new noEnemy(),
					  new PlatformNode(new Platform(new Posn(randPY-10,randPY+10),
									new Posn(randPX-20,randPX+20)),
							   new noPlatform()),
					  new Background("Stage1")),
				"Test Gravity() - Falling, On a Platform");
      } else {
	passed = t.checkExpect( new Game2(new Player (new Posn (randX,randY),"left",0,0,randJ,25),
					  new noWeapon(),
					  new noEnemy(),
					  new PlatformNode(new Platform(new Posn(randPY-10,randPY+10),
									new Posn(randPX-20,randPX+20)),
							   new noPlatform()),
					  new Background("Stage1")).gravity(),
				new Game2(new Player (new Posn (randX,randY+5),"left",0,0,randJ,25),
					  new noWeapon(),
					  new noEnemy(),
					  new PlatformNode(new Platform(new Posn(randPY-10,randPY+10),
									new Posn(randPX-20,randPX+20)),
							   new noPlatform()),
					  new Background("Stage1")),
				"Test Gravity() - Falling, Not on a Platform");
      }
    }
    return passed;
  }
  // Runs three hundred tests of the worldEnds() method checking if the world should end
  boolean testWorldEnds(Tester t){
    boolean passed = false;
    for (int i = 0; i < 300; i++){

      Random randomW = new Random();
      int randW = randomW.nextInt(2);
      Random randomX = new Random();
      int randX = randomX.nextInt(701);
      Random randomY = new Random();
      int randY = randomY.nextInt(251);
      Random randomH = new Random();
      int randH = randomH.nextInt(26);
      if (randW == 0){
	passed = t.checkExpect( Stage1.worldEnds(),
				new WorldEnd(false, Stage1.makeImage()),
				"Test WorldEnd() - World Doesn't end");
      } else if (randW == 1){
	if (randY > 220){
	  passed = t.checkExpect( new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					    new noWeapon(),
					    new noEnemy(),
					    Stage3Platforms,
					    new Background("Stage3")).worldEnds(),
				  new WorldEnd(true,
					       new Game2(new Player (new Posn (randX,randY),"right",0,0,0,0),
							 new noWeapon(),
							 new noEnemy(),
							 Stage3Platforms,
							 new Background("Stage3")).makeImage()),
				  "Test WorldEnds() - Falling onto Spikes");
	} else {
	  passed = t.checkExpect( new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					    new noWeapon(),
					    new noEnemy(),
					    Stage3Platforms,
					    new Background("Stage3")).worldEnds(),
				  new WorldEnd(false,
					       new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
							 new noWeapon(),
							 new noEnemy(),
							 Stage3Platforms,
							 new Background("Stage3")).makeImage()),
				  "Test WorldEnds() - Did not fall onto spikes");
	}
      } else {
	if (randH == 0){
	  passed = t.checkExpect( new Game2(new Player (new Posn (20,135),"right",0,0,0,randH),
					    new noWeapon(),
					    new noEnemy(),
					    Stage3Platforms,
					    new Background("Stage3")).worldEnds(),
				  new WorldEnd(true,
					       new Game2(new Player (new Posn (20,135),"right",0,0,0,0),
							 new noWeapon(),
							 new noEnemy(),
							 Stage3Platforms,
							 new Background("Stage3")).makeImage()),
				  "Test WorldEnds() - Player has zero health");
	} else {
	  passed = t.checkExpect( new Game2(new Player (new Posn (20,135),"right",0,0,0,randH),
					    new noWeapon(),
					    new noEnemy(),
					    Stage3Platforms,
					    new Background("Stage3")).worldEnds(),
				  new WorldEnd(false,
					       new Game2(new Player (new Posn (randX,randY),"right",0,0,0,randH),
							 new noWeapon(),
							 new noEnemy(),
							 Stage3Platforms,
							 new Background("Stage3")).makeImage()),
				  "Test WorldEnds() - Player has at least one  health");
	}
      }
    }
    return passed;
  }
  // Runs three hundred tests of the hit() method checking if either the player or an enemy should be damaged
  boolean testHitDetection(Tester t){
    boolean passed = false;
    for (int i = 0; i < 300; i++){

      Random randomW = new Random();
      int randW = randomW.nextInt(2);
      Random randomWX = new Random();
      int randWX = randomWX.nextInt(701);
      Random randomWY = new Random();
      int randWY = randomWY.nextInt(251);
      Random randomX = new Random();
      int randX = randomX.nextInt(701);
      Random randomY = new Random();
      int randY = randomX.nextInt(251);
      if (randW == 0){
	passed = t.checkExpect(Stage1.hit(),
			       Stage1,
			       "Test Hit Detection - No Hit");
      } else if (randW == 0) {
	if (Math.abs(randX-randWX) < 15 && Math.abs(randY - randWY) < 20){
	  passed = t.checkExpect(new Game2(new Player (new Posn (40,132),"right",0,0,0,25),
					   new WeaponNode(new Weapon(true,
								     new Posn(randWX,randWY),
								     new Posn (0,0),
								     "Buster","right"), new noWeapon()),
					   new EnemyNode(new ET (new Posn (randX,randY),
								 "left",0,5),
							 new EnemyNode(new ET (new Posn (450, 190),
									       "left",0,5),
								       new noEnemy())),
					   Stage1Platforms,
					   new Background("Stage1")).hit(),
				 new Game2(new Player (new Posn (40,132),"right",0,0,0,25),
					   new noWeapon(),
					   new EnemyNode(new ET (new Posn (randX,randY),
								 "left",0,4),
							 new EnemyNode(new ET (new Posn (450, 190),
									       "left",0,5),
								       new noEnemy())),
					   Stage1Platforms,
					   new Background("Stage1")),
				 "Test Hit Detection - Enemy Hit");
	} else {
	  passed = t.checkExpect(new Game2(new Player (new Posn (40,132),"right",0,0,0,25),
					   new WeaponNode(new Weapon(true,
								     new Posn(randWX,randWY),
								     new Posn (0,0),
								     "Buster","right"), new noWeapon()),
					   new EnemyNode(new ET (new Posn (randX,randY),
								 "left",0,5),
							 new EnemyNode(new ET (new Posn (450, 190),
									       "left",0,5),
								       new noEnemy())),
					   Stage1Platforms,
					   new Background("Stage1")).hit(),
				 new Game2(new Player (new Posn (40,132),"right",0,0,0,25),
					   new WeaponNode(new Weapon(true,
								     new Posn(randWX,randWY),
								     new Posn (0,0),
								     "Buster","right"), new noWeapon()),
					   new EnemyNode(new ET (new Posn (randX,randY),
								 "left",0,5),
							 new EnemyNode(new ET (new Posn (450, 190),
									       "left",0,5),
								       new noEnemy())),
					   Stage1Platforms,
					   new Background("Stage1")),
				 "Test Hit Detection - Enemy Missed");

	}
      } else if (randW == 1){
	if (Math.abs(randWX-randX) < 15 && Math.abs(randWY - randY) < 20){
	  passed = t.checkExpect(new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new WeaponNode(new Weapon(false,
								     new Posn(randWX,randWY),
								     new Posn (0,0),
								     "ET","left"), new noWeapon()),
					   new noEnemy(),
					   Stage1Platforms,
					   new Background("Stage1")).hit(),
				 new Game2(new Player (new Posn (randX,randY),"right",0,0,0,24),
					   new noWeapon(),
					   new noEnemy(),
					   Stage1Platforms,
					   new Background("Stage1")),
				 "Test Hit Detection - Player Hit");
	} else {
	  passed = t.checkExpect(new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new WeaponNode(new Weapon(false,
								     new Posn(randWX,randWY),
								     new Posn (0,0),
								     "ET","left"), new noWeapon()),
					   new noEnemy(),
					   Stage1Platforms,
					   new Background("Stage1")).hit(),
				 new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new WeaponNode(new Weapon(false,
								     new Posn(randWX,randWY),
								     new Posn (0,0),
								     "ET","left"), new noWeapon()),
					   new noEnemy(),
					   Stage1Platforms,
					   new Background("Stage1")),
				 "Test Hit Detection - Player Missed");
	}
      } else {
	if (Math.abs(randX-randWX) < 15 && Math.abs(randY - randWY) < 20){
	  passed = t.checkExpect(new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new EnemyNode(new ET (new Posn (randWX,randWY),
								 "left",0,5),
							 new EnemyNode(new ET (new Posn (450, 190),
									       "left",0,5),
								       new noEnemy())),
					   Stage1Platforms,
					   new Background("Stage1")).hit(),
				 new Game2(new Player (new Posn (randX,randY),"right",0,0,0,24),
					   new noWeapon(),
					   new EnemyNode(new ET (new Posn (randWX,randWY),
								 "left",0,4),
							 new EnemyNode(new ET (new Posn (450, 190),
									       "left",0,5),
								       new noEnemy())),
					   Stage1Platforms,
					   new Background("Stage1")),
				 "Test Hit Detection - Player/Enemy Contact");
	} else {
	  passed = t.checkExpect(new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new EnemyNode(new ET (new Posn (randWX,randWY),
								 "left",0,5),
							 new EnemyNode(new ET (new Posn (450, 190),
									       "left",0,5),
								       new noEnemy())),
					   Stage1Platforms,
					   new Background("Stage1")).hit(),
				 new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new EnemyNode(new ET (new Posn (randWX,randWY),
								 "left",0,4),
							 new EnemyNode(new ET (new Posn (450, 190),
									       "left",0,5),
								       new noEnemy())),
					   Stage1Platforms,
					   new Background("Stage1")),
				 "Test Hit Detection - Player/Enemy No Contact");
	}
      }
    }
    return passed;
  }
  boolean testSwitchStage(Tester t){
    boolean passed = false;
    for (int i = 0; i < 500; i++){
      
      Random randomStage = new Random();
      int randStage = randomStage.nextInt(6);
      Random randomX = new Random();
      int randX = randomX.nextInt(701);
      Random randomY = new Random();
      int randY = randomX.nextInt(251);

      if (randStage == 0){
	if (randX > 690){
	  passed = t.checkExpect(new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   Stage1Platforms,
					   new Background("Stage1")).onTick(),
				 Stage2,
				 "Stage 1 - Switch");
	} else {
	  passed = t.checkExpect(new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   Stage1Platforms,
					   new Background("Stage1")).onTick(),
				 new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   Stage1Platforms,
					   new Background("Stage1")).gravity(),
				 "Stage 1 - Stay");
	}
      } else if (randStage == 1){
	if (randX > 690){
	  passed = t.checkExpect(new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   Stage2Platforms,
					   new Background("Stage2")).onTick(),
				 StageR,
				 "Stage 2 - Switch");
	} else {
	  passed = t.checkExpect(new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   Stage2Platforms,
					   new Background("Stage2")).onTick(),
				 new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   Stage2Platforms,
					   new Background("Stage2")).gravity(),
				 "Stage 2 - Stay");
	}
      } else if (randStage == 2){
	if (randX > 690){
	  passed = t.checkExpect(new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   StageRPlatforms,
					   new Background("StageR")).onTick(),
				 Stage3,
				 "Stage R - Switch");
	} else {
	  passed = t.checkExpect(new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   StageRPlatforms,
					   new Background("StageR")).onTick(),
				 new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   StageRPlatforms,
					   new Background("StageR")).gravity(),
				 "Stage R - Stay");
	}	
      } else if (randStage == 3){
	if (randX > 690){
	  passed = t.checkExpect(new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   Stage3Platforms,
					   new Background("Stage3")).onTick(),
				 Stage4,
				 "Stage 3 - Switch");
	} else {
	  passed = t.checkExpect(new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   Stage3Platforms,
					   new Background("Stage3")).onTick(),
				 new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   Stage3Platforms,
					   new Background("Stage3")).gravity(),
				 "Stage 3 - Stay");
	}
      } else if (randStage == 4){
	if (randX > 690){
	  passed = t.checkExpect(new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   Stage4Platforms,
					   new Background("Stage4")).onTick(),
				 StageJ,
				 "Stage 4 - Switch");
	} else {
	  passed = t.checkExpect(new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   Stage4Platforms,
					   new Background("Stage4")).onTick(),
				 new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   Stage4Platforms,
					   new Background("Stage4")).gravity(),
				 "Stage 4 - Stay");
	}
      } else {
	passed = t.checkExpect(new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   StageJPlatforms,
					   new Background("StageJ")).onTick(),
				 new Game2(new Player (new Posn (randX,randY),"right",0,0,0,25),
					   new noWeapon(),
					   new noEnemy(),
					   StageJPlatforms,
					   new Background("StageJ")).gravity(),
				 "Stage J - Stay");
      }     
    }
    return passed;
  }
  // Runs five hundred tests with random input and a randomly chosen stage
  boolean testRandomInput(Tester t){
    
    boolean passed = false;
    String[] stringArray;
    stringArray = new String[39];
    stringArray[0] = "a";
    stringArray[1] = "b";
    stringArray[2] = "c";
    stringArray[3] = "d";
    stringArray[4] = "e";
    stringArray[5] = "f";
    stringArray[6] = "g";
    stringArray[7] = "h";
    stringArray[8] = "i";
    stringArray[9] = "j";
    stringArray[10] = "k";
    stringArray[11] = "l";
    stringArray[12] = "m";
    stringArray[13] = "n";
    stringArray[14] = "o";
    stringArray[15] = "p";
    stringArray[16] = "q";
    stringArray[17] = "r";
    stringArray[18] = "s";
    stringArray[19] = "t";
    stringArray[20] = "u";
    stringArray[21] = "v";
    stringArray[22] = "w";
    stringArray[23] = "x";
    stringArray[24] = "y";
    stringArray[25] = "z";
    stringArray[26] = "up";
    stringArray[27] = "down";
    stringArray[28] = "left";
    stringArray[29] = "right";
    stringArray[30] = "1";
    stringArray[31] = "2";
    stringArray[32] = "3";
    stringArray[33] = "4";
    stringArray[34] = "5";
    stringArray[35] = "6";
    stringArray[36] = "7";
    stringArray[37] = "8";
    stringArray[38] = "9";

    for (int i = 0; i < 500; i++){
      
      Random randomInput = new Random();
      Random randomStage = new Random();
      int randInput = randomInput.nextInt(39);
      int randStage = randomStage.nextInt(6);
      String input = stringArray[randInput];

      if (randStage == 0){
	if (input.equals("s")){
	  passed = t.checkExpect(Stage1.onKeyEvent(input),
				 new Game2(new Player (new Posn (40,132),"right",0,1,0,25).move(input),
					   new WeaponNode(new Weapon(true,
								     new Posn(45,
									      139),
								     new Posn (0,0),
								     "Buster","right"), new noWeapon()).move(),
					   new EnemyNode(new ET (new Posn (300,190),
								 "left",0,5),
					   new EnemyNode(new ET (new Posn (450, 190),
								 "left",0,5),
							 new noEnemy())),
					   Stage1Platforms,
					   new Background("Stage1")).gravity().hit(),
				 "Test Random Input - Stage1. Input: " + input);

	} else {
	  passed = t.checkExpect(Stage1.onKeyEvent(input),
				 new Game2(new Player (new Posn (40,132),"right",0,0,0,25).move(input),
					   new noWeapon(),
					   new EnemyNode(new ET (new Posn (300,190),
								 "left",0,5),
					   new EnemyNode(new ET (new Posn (450, 190),
								 "left",0,5),
							 new noEnemy())),
					   Stage1Platforms,
					   new Background("Stage1")).gravity().hit(),
				 "Test Random Input - Stage1. Input: " + input);
	}
      } else if (randStage == 1){
	if (input.equals("s")){
	  passed = t.checkExpect(Stage2.onKeyEvent(input),
				 new Game2(new Player (new Posn (10,190),"right",0,0,0,25).move(input),
					   new WeaponNode(new Weapon(true,
								     new Posn(15,197),
								     new Posn (0,0),
								     "Buster","right"), new noWeapon()).move(),
					   new EnemyNode(new ET (new Posn (300,190),
								 "left",0,5),
					   new EnemyNode(new ET (new Posn (500, 190),
								 "left",0,5),
							 new noEnemy())),
					   Stage2Platforms,
					   new Background("Stage2")).gravity().hit(),
				 "Test Random Input - Stage 2. Input: " + input);
	} else {
	  passed = t.checkExpect(Stage2.onKeyEvent(input),
				 new Game2(new Player (new Posn (10,190),"right",0,0,0,25).move(input),
					   new noWeapon(),
					   new EnemyNode(new ET (new Posn (300,190),
								 "left",0,5),
					   new EnemyNode(new ET (new Posn (500, 190),
								 "left",0,5),
							 new noEnemy())),
					   Stage2Platforms,
					   new Background("Stage2")).gravity().hit(),
				 "Test Random Input - Stage 2. Input: " + input);
	}
      } else if (randStage == 2){
	if (input.equals("s")){
	  passed = t.checkExpect(StageR.onKeyEvent(input),
				 new Game2(new Player (new Posn (10,97),"right",0,0,0,25).move(input),
					   new WeaponNode(new Weapon(true,
								     new Posn(15,104),
								     new Posn (0,0),
								     "Buster","right"), new noWeapon()).move(),
					   new EnemyNode(new DrRacket(new Posn(350,125),
								      false,
								      "left",
								      0,
								      25), new noEnemy()),
					   StageRPlatforms,
					   new Background("StageR")).gravity().hit(),
				 "Test Random Input - Stage R. Input: " + input);
	} else {
	  passed = t.checkExpect(StageR.onKeyEvent(input),
				 new Game2(new Player (new Posn (10,97),"right",0,0,0,25).move(input),
					   new noWeapon(),
					   new EnemyNode(new DrRacket(new Posn(350,125),
								      false,
								      "left",
								      0,
								      25), new noEnemy()),
					   StageRPlatforms,
					   new Background("StageR")).gravity().hit(),
				 "Test Random Input - Stage R. Input: " + input);
	}
      } else if (randStage == 3){
	if (input.equals("s")){
	  passed = t.checkExpect(Stage3.onKeyEvent(input),
				 new Game2(new Player (new Posn (20,135),"right",0,0,0,25).move(input),
					   new WeaponNode(new Weapon(true,
								     new Posn(25,142),
								     new Posn (0,0),
								     "Buster","right"), new noWeapon()).move(),
					   new EnemyNode(new ET (new Posn (251,158),
								 "left",0,5),
					   new EnemyNode(new ET (new Posn (485,174),
								 "left",0,5),
					   new EnemyNode(new ET (new Posn(550,142),
								 "left",0,5),
					   new EnemyNode(new ET (new Posn(610,158),
								 "left",0,5),						   
							 new noEnemy())))),
					   Stage3Platforms,
					   new Background("Stage3")).gravity().hit(),
				 "Test Random Input - Stage 3. Input: " + input);
	} else {
	  passed = t.checkExpect(Stage3.onKeyEvent(input),
				 new Game2(new Player (new Posn (20,135),"right",0,0,0,25).move(input),
					   new noWeapon(),
					   new EnemyNode(new ET (new Posn (251,158),
								 "left",0,5),
					   new EnemyNode(new ET (new Posn (485,174),
								 "left",0,5),
					   new EnemyNode(new ET (new Posn(550,142),
								 "left",0,5),
					   new EnemyNode(new ET (new Posn(610,158),
								 "left",0,5),						   
							 new noEnemy())))),
					   Stage3Platforms,
					   new Background("Stage3")).gravity().hit(),
				 "Test Random Input - Stage 3. Input: " + input);
	}
      } else if (randStage == 4){
	if (input.equals("s")){
	  passed = t.checkExpect(Stage4.onKeyEvent(input),
				 new Game2(new Player (new Posn (20,158),"right",0,0,0,25).move(input),
					   new WeaponNode(new Weapon(true,
								     new Posn(25,165),
								     new Posn (0,0),
								     "Buster","right"), new noWeapon()).move(),
					   new noEnemy(),
					   Stage4Platforms,
					   new Background("Stage4")).gravity().hit(),
				 "Test Random Input - Stage 4. Input: " + input);
	} else {
	  passed = t.checkExpect(Stage4.onKeyEvent(input),
				 new Game2(new Player (new Posn (20,158),"right",0,0,0,25).move(input),
					   new noWeapon(),
					   new noEnemy(),
					   Stage4Platforms,
					   new Background("Stage4")).gravity().hit(),
				 "Test Random Input - Stage 4. Input: " + input);
	}
      } else {
	if (input.equals("s")){
	  passed = t.checkExpect(StageJ.onKeyEvent(input),
				 new Game2(new Player (new Posn (10,158),"right",0,0,0,25).move(input),
					   new WeaponNode(new Weapon(true,
								     new Posn(15,165),
								     new Posn (0,0),
								     "Buster","right"), new noWeapon()).move(),
					   new EnemyNode(new J(new Posn(550,75),
							       false,
							       "left",
							       0,
							       50), new noEnemy()),
					   StageJPlatforms,
					   new Background("StageJ")).gravity().hit(),
				 "Test Random Input - Stage J. Input: " + input);
	} else {
	  passed = t.checkExpect(StageJ.onKeyEvent(input),
				 new Game2(new Player (new Posn (10,158),"right",0,0,0,25).move(input),
					   new noWeapon(),
					   new EnemyNode(new J(new Posn(550,75),
							       false,
							       "left",
							       0,
							       50), new noEnemy()),
					   StageJPlatforms,
					   new Background("StageJ")).gravity().hit(),
				 "Test Random Input - Stage J. Input: " + input);
	}
      }
    }
    return passed;
  }
  /*
  boolean testRandomOnTick(Tester t){
    
    String[] stringArray;
    stringArray = new String[5];
    stringArray[0] = "s";
    stringArray[1] = "up";
    stringArray[2] = "down";
    stringArray[3] = "left";
    stringArray[4] = "right";
    boolean passed = false;
    Game2 currGame;
    Game2 prevGame;
    
    for (int j = 0; j < 50; j++){
      
      Random randomStage = new Random();
      int randStage = randomStage.nextInt(6);
      Random randomX = new Random();
      int randX = randomX.nextInt(701);
      Random randomY = new Random();
      int randY = randomX.nextInt(251);

      if (randStage == 0){
	currGame = Stage1;
	for (int i = 0; i < 50; i++){
	  
	  Random randomInput = new Random();
	  int randInput = randomInput.nextInt(5);
	  String input = stringArray[randInput];
	  Random randomInt = new Random();
	  int randInt = randomX.nextInt(2);
	  prevGame = currGame;
	  
	  if (randInt==0){
	    currGame = (Game2)currGame.onTick();
	    passed = t.checkExpect(prevGame.onTick(),
				   currGame,
				   "Test onTick - Stage1 on tick " + i);
	  } else {
	    currGame = (Game2)currGame.onKeyEvent(input);
	    passed = t.checkExpect(prevGame.onKeyEvent(input),
				   currGame,
				   "Test onTick - Stage1 on key event " + i);
	  }				 
	}
      } else if (randStage == 1){
	currGame = Stage2;
	for (int i = 0; i < 50; i++){
	  
	  Random randomInput = new Random();
	  int randInput = randomInput.nextInt(5);
	  String input = stringArray[randInput];
	  Random randomInt = new Random();
	  int randInt = randomX.nextInt(2);
	  prevGame = currGame;
	  
	  if (randInt==0){
	    currGame = (Game2)currGame.onTick();
	    passed = t.checkExpect(prevGame.onTick(),
				   currGame,
				   "Test onTick - Stage2 on tick " + i);
	  } else {
	    currGame = (Game2)currGame.onKeyEvent(input);
	    passed = t.checkExpect(prevGame.onKeyEvent(input),
				   currGame,
				   "Test onTick - Stage2 on key event " + i);
	  }				 
	}
      } else if (randStage == 2){
	currGame = StageR;
	for (int i = 0; i < 50; i++){
	  
	  Random randomInput = new Random();
	  int randInput = randomInput.nextInt(5);
	  String input = stringArray[randInput];
	  Random randomInt = new Random();
	  int randInt = randomX.nextInt(2);
	  prevGame = currGame;
	  
	  if (randInt==0){
	    currGame = (Game2)currGame.onTick();
	    passed = t.checkExpect(prevGame.onTick(),
				   currGame,
				   "Test onTick - StageR on tick " + i);
	  } else {
	    currGame = (Game2)currGame.onKeyEvent(input);
	    passed = t.checkExpect(prevGame.onKeyEvent(input),
				   currGame,
				   "Test onTick - StageR on key event " + i);
	  }				 
	}
      } else if (randStage == 3){
	currGame = Stage3;
	for (int i = 0; i < 50; i++){
	  
	  Random randomInput = new Random();
	  int randInput = randomInput.nextInt(5);
	  String input = stringArray[randInput];
	  Random randomInt = new Random();
	  int randInt = randomX.nextInt(2);
	  prevGame = currGame;
	  
	  if (randInt==0){
	    currGame = (Game2)currGame.onTick();
	    passed = t.checkExpect(prevGame.onTick(),
				   currGame,
				   "Test onTick - Stage3 on tick " + i);
	  } else {
	    currGame = (Game2)currGame.onKeyEvent(input);
	    passed = t.checkExpect(prevGame.onKeyEvent(input),
				   currGame,
				   "Test onTick - Stage3 on key event " + i);
	  }				 
	}
      } else if (randStage == 4){
	currGame = Stage4;
	for (int i = 0; i < 50; i++){
	  
	  Random randomInput = new Random();
	  int randInput = randomInput.nextInt(5);
	  String input = stringArray[randInput];
	  Random randomInt = new Random();
	  int randInt = randomX.nextInt(2);
	  prevGame = currGame;
	  
	  if (randInt==0){
	    currGame = (Game2)currGame.onTick();
	    passed = t.checkExpect(prevGame.onTick(),
				   currGame,
				   "Test onTick - Stage4 on tick " + i);
	  } else {
	    currGame = (Game2)currGame.onKeyEvent(input);
	    passed = t.checkExpect(prevGame.onKeyEvent(input),
				   currGame,
				   "Test onTick - Stage4 on key event " + i);
	  }				 
	}
      } else {
	currGame = StageJ;
	for (int i = 0; i < 50; i++){
	  
	  Random randomInput = new Random();
	  int randInput = randomInput.nextInt(5);
	  String input = stringArray[randInput];
	  Random randomInt = new Random();
	  int randInt = randomX.nextInt(2);
	  prevGame = currGame;
	  
	  if (randInt==0){
	    currGame = (Game2)currGame.onTick();
	    passed = t.checkExpect(prevGame.onTick(),
				   currGame,
				   "Test onTick - StageJ on tick " + i);
	  } else {
	    currGame = (Game2)currGame.onKeyEvent(input);
	    passed = t.checkExpect(prevGame.onKeyEvent(input),
				   currGame,
				   "Test onTick - StageJ on key event " + i);
	  }				 
	}
      }     
    }
    return passed;
  }*/
  public static void main(String[] args){
    AdventureTest testAll = new AdventureTest();
    Tester.runReport(testAll, false, false);
  }
}
