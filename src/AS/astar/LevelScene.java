package AS.astar;

import ch.idsia.benchmark.mario.engine.GlobalOptions;
import static ch.idsia.benchmark.mario.engine.LevelScene.cellSize;
import java.util.ArrayList;
import java.util.List;

import AS.astar.level.Level;
import AS.astar.level.SpriteTemplate;
import AS.astar.sprites.BulletBill;
import AS.astar.sprites.Enemy;
import AS.astar.sprites.FireFlower;
import AS.astar.sprites.Fireball;
import AS.astar.sprites.FlowerEnemy;
import AS.astar.sprites.Mario;
import AS.astar.sprites.Mushroom;
import AS.astar.sprites.Shell;
import AS.astar.sprites.Sparkle;
import AS.astar.sprites.Sprite;
import AS.astar.sprites.SpriteContext;
import AS.astar.sprites.WaveGoomba;

// This is the class containing an entire world state.
// It is taken from the Mario physics engine and has been adapted somewhat to
// support cloning, and add level-features and enemies on the fly

public class LevelScene implements SpriteContext, Cloneable
{
    public List<Sprite> sprites = new ArrayList<Sprite>();
    private List<Sprite> spritesToAdd = new ArrayList<Sprite>();
    private List<Sprite> spritesToRemove = new ArrayList<Sprite>();

    public Level level;
    public Mario mario;
    
    public float xCam, yCam, xCamO, yCamO; // left-over from rendering, can probably be removed.
    public int tickCount;
    
    public int verbose = 0; // set to > 0 for debugging output

    public boolean paused = true;
    public int startTime = 0;
    public int timeLeft;
//    private int width;
//    private int height;
    
    // Some features that can be used to tweak the plan (unused for now)
    public int enemiesJumpedOn = 0;
    public int enemiesKilled = 0;
    public int coinsCollected = 0;
    public int powerUpsCollected = 0;
    public int otherTricks = 0;

    private int timeLimit = 200;
    
    public static int killedCreaturesTotal;
    public static int killedCreaturesByFireBall;
    public static int killedCreaturesByStomp;
    public static int killedCreaturesByShell;
    
    private int greenMushroomMode = 0;
    
    /* Modified */
    public static final int CANNON_MUZZLE = -82;
    public static final int CANNON_TRUNK = -80;
    public static final int COIN_ANIM = 2;
    public static final int BREAKABLE_BRICK = -20; // original = -20
    public static final int UNBREAKABLE_BRICK = -22; //a rock with animated question mark // original = -22
    public static final int BRICK = -24;           //a rock with animated question mark
    public static final int FLOWER_POT = -90;
    public static final int BORDER_CANNOT_PASS_THROUGH = -127; // original = 60
    public static final int BORDER_HILL = -62;
    public static final int FLOWER_POT_OR_CANNON = -85;
    public static final int LADDER = 61;
    public static final int TOP_OF_LADDER = 61;
    public static final int PRINCESS = 5;
    public byte GeneralizeI(byte el){
         switch (el)
        {
             case -24:
             case -20:
             case -22:
             case -60:
             case -90:
                 return BORDER_CANNOT_PASS_THROUGH;
        }
        return el;
    }
    
    public static List<Sprite> cloneList(List<Sprite> list) throws CloneNotSupportedException {
        List<Sprite> clone = new ArrayList<Sprite>(list.size());
        for(Sprite item: list) clone.add((Sprite) item.clone());
        return clone;
    }
    
    @Override protected Object clone() throws CloneNotSupportedException 
    {
    	LevelScene c = (LevelScene) super.clone();
    	c.mario = (Mario) this.mario.clone();
    	c.level = (Level) this.level.clone();
    	c.mario.world = c;
    	
    	List<Sprite> clone = new ArrayList<Sprite>(this.sprites.size());
        for(Sprite item: this.sprites) 
        {
        	if (item == mario)
        	{
        		clone.add(c.mario);
        	}
        	else
        	{
        		Sprite s = (Sprite) item.clone();
        		if (s.kind == Sprite.KIND_SHELL && ((Shell) s).carried && c.mario.carried != null)
        			c.mario.carried = s;
        		s.world = c;
        		clone.add(s);
        	}
        }
        c.sprites = clone;
    	return c;
    }

    // Update internal level representation to what we get from the API.
    // includes some gap detection code
    public void printLevel(byte[][] data)
    {
        for(int i = 0; i < data.length; ++i)
        {
            for(int j = 0; j < data[i].length; ++j)
            {
                System.out.print(data[i][j] + " ");
            }
            System.out.println("");
        }
    }
    
    public boolean setLevelScene(byte[][] data)
    {      
        int HalfObsWidth = 9;
        int HalfObsHeight = 9;

        int MarioXInMap = (int)mario.x/16;
        int MarioYInMap = (int)mario.y/16;
        
        //**************************Debug pos****************************
//        System.out.print("--------------MarioRealState: ");
//        System.out.print("x: " + mario.x + " y: " + mario.y);
//        System.out.println("------------------------------");
//        //printLevel(data);
//        System.out.println("**************************************************");
        
        boolean gapAtLast = true;
        boolean gapAtSecondLast = true;
        int lastEventX = 0;
        int[] heights = new int[18];
        for(int i = 0; i < heights.length; i++)
        	heights[i] = 0;
        
        int gapBorderHeight = 0;
        int gapBorderMinusOneHeight = 0;
        int gapBorderMinusTwoHeight = 0;
        
        for (int y = MarioYInMap - HalfObsHeight, obsX = 0; y < MarioYInMap + HalfObsHeight; y++, obsX++)
        {
            for (int x = MarioXInMap - HalfObsWidth, obsY = 0; x < MarioXInMap + HalfObsWidth; x++, obsY++)
            {
                if (x >=0 && x <= level.xExit && y >= 0 && y < level.height)
                {
                	/*----------------modification--------------*/
                        byte datum;
//                      if(data[obsX][obsY] == -60 || data[obsX][obsY] == -20 || data[obsX][obsY] == -22 || data[obsX][obsY] == -90)
//                          datum = -127;
//                      else
                            datum = GeneralizeI(data[obsX][obsY]);
//                	System.out.print(datum);
//                      datum = data[obsX][obsY];

                       /*-----------end of modification------------*/
                        
                 	if (datum != 0 && datum != BORDER_CANNOT_PASS_THROUGH && datum != 1 && obsY > lastEventX)
                	{
                 		lastEventX = obsY;
                	}
                 	if (datum != 0 && datum != 1)
                	{
                		if (heights[obsY] == 0)
                		{
                			heights[obsY] = y;
                		}
                	}
                 	
                	// cannon detection: if there's a one-block long hill, it's a cannon!
                 	// i think this is not required anymore, because we get the cannon data straight from the API.
                	if (x == MarioXInMap + HalfObsWidth - 3 &&
                			datum != 0 && y > 5)
                	{

                		if (gapBorderMinusTwoHeight == 0)
                			gapBorderMinusTwoHeight = y;
                	}
                	if (x == MarioXInMap + HalfObsWidth - 2 &&
                			datum != 0 && y > 5)
                	{
                		if (gapBorderMinusOneHeight == 0)
                			gapBorderMinusOneHeight = y;
                		gapAtSecondLast = false;
                	}
                	if (x == MarioXInMap + HalfObsWidth - 1 &&
                			datum != 0 && y > 5)
                	{

                		if (gapBorderHeight == 0)
                			gapBorderHeight = y;
                		gapAtLast = false;
                	}
                    
                        
   
                    if (datum != 1 && level.getBlock(x, y) != 14) 
                   	level.setBlock(x, y, datum);
                }
                //System.out.print(level.getBlock(x, y)+ " ");
            }
            //System.out.println("");
        }
        
//        for (int y = MarioYInMap - HalfObsHeight, obsX = 0; y < MarioYInMap + HalfObsHeight; y++, obsX++)
//        {
//            for (int x = MarioXInMap - HalfObsWidth, obsY = 0; x < MarioXInMap + HalfObsWidth; x++, obsY++)
//            {
//                //System.out.print(data[obsX][obsY] + " ");
//                System.out.print(level.getBlock(x, y)+ " ");
//            }
//            System.out.println("");
//        }
//        System.out.println("");
//        System.out.println("------------------------------");
        if (gapBorderHeight == gapBorderMinusTwoHeight && gapBorderMinusOneHeight < gapBorderHeight)
        {
        	// found a cannon!
        	//System.out.println("Got a cannon!");
            
        	level.setBlock(MarioXInMap + HalfObsWidth - 2,gapBorderMinusOneHeight, (byte)14);
        }
        if (gapAtLast && !gapAtSecondLast)
        {
        	// found a gap. 
        	int holeWidth = 4;

    		// make the gap wider before we see the end to allow ample time for the 
        	// planner to jump over.
        	for(int i = 0; i < holeWidth; i++)
        	{
                    for(int j = 0; j < 15; j++)
                    {
                            level.setBlock(MarioXInMap + HalfObsWidth + i, j, (byte) 0);
                    }
                    level.isGap[MarioXInMap + HalfObsWidth + i] = true;
                    level.gapHeight[MarioXInMap + HalfObsWidth + i] = gapBorderMinusOneHeight;
        	}
        	for(int j = gapBorderMinusOneHeight; j < 16; j++)
        	{
        		level.setBlock(MarioXInMap + HalfObsWidth + holeWidth, gapBorderMinusOneHeight, (byte) 4);
        	}
        	return true;
        }
    	return false;
    }
    
   
    public void init()
    {
        //Modify by Luong
        Level.loadBehaviors();
        
        Sprite.spriteContext = this;
        sprites.clear();

        mario = new Mario(this);
        sprites.add(mario);
        startTime = 1;

        timeLeft = timeLimit*15;

        tickCount = 1;
    }
    
    public void resetMario()
    {
        mario = new Mario(this);
        sprites.add(mario);
    }

    public int fireballsOnScreen = 0;

    List<Shell> shellsToCheck = new ArrayList<Shell>();

    public void checkShellCollide(Shell shell)
    {
        shellsToCheck.add(shell);
    }

    List<Fireball> fireballsToCheck = new ArrayList<Fireball>();

    public void checkFireballCollide(Fireball fireball)
    {
        fireballsToCheck.add(fireball);
    }
   
    public void tick()
    {
        if (GlobalOptions.isGameplayStopped)
            return;
        
        timeLeft--;
        if (timeLeft == 0)
            mario.die();
        xCamO = xCam;
        yCamO = yCam;

        if (startTime > 0)
        {
            startTime++;
        }

        float targetXCam = mario.x - 160;

        xCam = targetXCam;

        if (xCam < 0) xCam = 0;
        if (xCam > level.width * cellSize - GlobalOptions.VISUAL_COMPONENT_WIDTH)
            xCam = level.width * cellSize - GlobalOptions.VISUAL_COMPONENT_WIDTH;

        fireballsOnScreen = 0;

        for (Sprite sprite : sprites)
        {
            if (sprite != mario)
            {
                float xd = sprite.x - xCam;
                float yd = sprite.y - yCam;
                if (xd < -64 || xd > GlobalOptions.VISUAL_COMPONENT_WIDTH + 64 || yd < -64 || yd > GlobalOptions.VISUAL_COMPONENT_HEIGHT + 64)
                {
                    removeSprite(sprite);
                } else
                {
                    if (sprite instanceof Fireball)
                        fireballsOnScreen++;
                }
            }
        }
        if (paused)
        {
            for (Sprite sprite : sprites)
            {
                if (sprite == mario)
                {
                    sprite.tick();
                }
                else
                {
                    sprite.tickNoMove();
                }
            }
        }
        else
        {
            tickCount++;
            level.tick();

        //            boolean hasShotCannon = false;
        //            int xCannon = 0;

            for (int x = (int) xCam / cellSize - 1; x <= (int) (xCam + GlobalOptions.VISUAL_COMPONENT_WIDTH) / cellSize + 1; x++)
                for (int y = (int) yCam / cellSize - 1; y <= (int) (yCam + GlobalOptions.VISUAL_COMPONENT_HEIGHT) / cellSize + 1; y++)
                {
                    int dir = 0;

                    if (x * cellSize + 8 > mario.x + cellSize) dir = -1;
                    if (x * cellSize + 8 < mario.x - cellSize) dir = 1;

                    SpriteTemplate st = level.getSpriteTemplate(x, y);

//                    if (st != null)
//                    {
//        //                        if (st.getType() == Sprite.KIND_SPIKY)
//        //                        {
//        //                            System.out.println("here");
//        //                        }
//
//                        if (st.lastVisibleTick != tickCount - 1)
//                        {
//                            if (st.sprite == null || !sprites.contains(st.sprite))
//                                st.spawn(this, x, y, dir);
//                        }
//
//                        st.lastVisibleTick = tickCount;
//                    }

                    if (dir != 0)
                    {
                        byte b = level.getBlock(x, y);
                        //System.out.println(b + " " + Level.TILE_BEHAVIORS[b & 0xff]);
                        if (((Level.TILE_BEHAVIORS[b & 0xff]) & Level.BIT_ANIMATED) > 0)
                        {
                            if ((b % cellSize) / 4 == 3 && b / cellSize == 0)
                            {
                                if ((tickCount - x * 2) % 100 == 0)
                                {
        //                                    xCannon = x;
                                    for (int i = 0; i < 8; i++)
                                    {
                                        addSprite(new Sparkle(x * cellSize + 8, y * cellSize + (int) (Math.random() * cellSize), (float) Math.random() * dir, 0, 0, 1, 5));
                                    }
                                    addSprite(new BulletBill(this, x * cellSize + 8 + dir * 8, y * cellSize + 15, dir));

        //                                    hasShotCannon = true;
                                }
                            }
                        }
                    }
                }

            for (Sprite sprite : sprites)
                sprite.tick();

            byte levelElement = level.getBlock(mario.mapX, mario.mapY);

            if (levelElement == (byte) (13 + 3 * 16) || levelElement == (byte) (13 + 5 * 16))
            {
                if (levelElement == (byte) (13 + 5 * 16))
                    mario.setOnTopOfLadder(true);
                else
                    mario.setInLadderZone(true);
            } else if (mario.isInLadderZone())
            {
                mario.setInLadderZone(false);
            }

            for (Sprite sprite : sprites)
            {
                sprite.collideCheck();
            }

            for (Shell shell : shellsToCheck)
            {
                for (Sprite sprite : sprites)
                {
                    if (sprite != shell && !shell.dead)
                    {
                        if (sprite.shellCollideCheck(shell))
                        {
                            if (mario.carried == shell && !shell.dead)
                            {
                                mario.carried = null;
                                //mario.setRacoon(false);
                                //System.out.println("sprite = " + sprite);
                                shell.die();
                                ++this.killedCreaturesTotal;
                            }
                        }
                    }
                }
            }
            shellsToCheck.clear();

            for (Fireball fireball : fireballsToCheck)
                for (Sprite sprite : sprites)
                    if (sprite != fireball && !fireball.dead)
                        if (sprite.fireballCollideCheck(fireball))
                            fireball.die();
            fireballsToCheck.clear();
        }

        sprites.addAll(0, spritesToAdd);
        sprites.removeAll(spritesToRemove);
        spritesToAdd.clear();
        spritesToRemove.clear();
    }
  
    @Override
    public void addSprite(Sprite sprite)
    {
        spritesToAdd.add(sprite);
        sprite.tick();
    }

    @Override
    public void removeSprite(Sprite sprite)
    {
        spritesToRemove.add(sprite);
    }

    public void bump(int x, int y, boolean canBreakBricks)
    {
        byte block = level.getBlock(x, y);

        if ((Level.TILE_BEHAVIORS[block & 0xff] & Level.BIT_BUMPABLE) > 0)
        {
            bumpInto(x, y - 1);
            level.setBlock(x, y, (byte) 4);
            //level.setBlockData(x, y, (byte) 4);

            if (((Level.TILE_BEHAVIORS[block & 0xff]) & Level.BIT_SPECIAL) > 0)
            {
                if (!mario.large)
                {
                    addSprite(new Mushroom(this, x * 16 + 8, y * 16 + 8));
                }
                else
                {
                    addSprite(new FireFlower(this, x * 16 + 8, y * 16 + 8));
                }
            }
            else
            {
                mario.getCoin();
                //addSprite(new CoinAnim(x, y));
            }
        }

        if ((Level.TILE_BEHAVIORS[block & 0xff] & Level.BIT_BREAKABLE) > 0)
        {
            bumpInto(x, y - 1);
            if (canBreakBricks)
            {
                level.setBlock(x, y, (byte) 0);
            }
            else
            {
                level.setBlockData(x, y, (byte) 4);
            }
        }
    }

    public void bumpInto(int x, int y)
    {
        byte block = level.getBlock(x, y);
        if (((Level.TILE_BEHAVIORS[block & 0xff]) & Level.BIT_PICKUPABLE) > 0)
        {
            mario.getCoin();
            level.setBlock(x, y, (byte) 0);
        }

        for (Sprite sprite : sprites)
        {
            sprite.bumpCheck(x, y);
        }
    }

    // update the enemies to what we receive from the API
    // contains some simple matching code to match incoming enemy positions to internal
    // enemies. Also do some trajectory correction if we missjudged the position or speed.
	public boolean setEnemies(float[] enemies) 
	{
		boolean requireReplanning = false;
		List<Sprite> newSprites = new ArrayList<Sprite>();
		if (verbose > 1) System.out.println("Enemies: "+enemies.length);
		for (int i = 0; i < enemies.length; i += 3)
		{
			int kind = (int) enemies[i];
			float x = enemies[i+1];
			float y = enemies[i+2];

                        //Kind = NONE or FIRE FLOWER (old version)
			//if (kind == -1 || kind == 15)
			//	continue;
                        if (kind == 0 || kind == 3)
				continue;
	        int type = -1;
	        boolean winged = false;
                
	        switch(kind)
	        {
	        case(Sprite.KIND_BULLET_BILL): type = -2; break;
	        case(Sprite.KIND_GOOMBA): type = Enemy.ENEMY_GOOMBA; break;
	        case(Sprite.KIND_SHELL): type = Enemy.KIND_SHELL; break;
	        case(Sprite.KIND_GOOMBA_WINGED): type = Enemy.ENEMY_GOOMBA; winged = true; break;
	        case(Sprite.KIND_GREEN_KOOPA): type = Enemy.ENEMY_GREEN_KOOPA; break;
	        case(Sprite.KIND_GREEN_KOOPA_WINGED): type = Enemy.ENEMY_GREEN_KOOPA; winged = true; break;
	        case(Sprite.KIND_RED_KOOPA): type = Enemy.ENEMY_RED_KOOPA; break;
	        case(Sprite.KIND_RED_KOOPA_WINGED): type = Enemy.ENEMY_RED_KOOPA; winged = true; break;
	        case(Sprite.KIND_SPIKY): type = Enemy.ENEMY_SPIKY; break;
	        case(Sprite.KIND_SPIKY_WINGED): type = Enemy.ENEMY_SPIKY; winged = true; break;
	        case(Sprite.KIND_ENEMY_FLOWER): type = Enemy.ENEMY_FLOWER; break;
                case(Sprite.KIND_WAVE_GOOMBA): type = Enemy.POSITION_WAVE_GOOMBA; winged = true; break; //Added by Luong
	        }
                
                //System.out.println("Kind: " + kind + " Type: " + type);
                
	        if (!(type == -1 && kind == 0))
	        {
	        	if (verbose > 6) 
	        		System.out.println("Enemy received: Type: "+type+" (Kind: "+kind+") Pos: "+x+" "+y);
	        }
	        if (type == -1)
	        	continue;
	        
	        
	        // is there already an enemy here?
	        //float maxDelta = 2.01f * 1.75f;
                float maxDelta = 2.01f * 1.75f;
                boolean enemyFound = false;
	        for (Sprite sprite:sprites)
	        {
	        	if (sprite.kind == kind)
	        		if (verbose > 6) 
	        			System.out.println("Same kind Sprite found! pos: "+sprite.x + " " + sprite.y + " xa: " + sprite.xa);
	        	
	        	// check if object is of same kind and close enough
	        	if (sprite.kind == kind 
	        			&& Math.abs(sprite.x - x) < maxDelta 
	        			&& ((Math.abs(sprite.y - y) < maxDelta) || sprite.kind == Sprite.KIND_ENEMY_FLOWER))
	        	{
	        		if (Math.abs(sprite.x - x) > 0)
	        		{
	        			if (verbose > 6) 
	        				System.out.println("Enemy inaccurate! Diff: "+(sprite.x - x)+" "+(sprite.y - y));
	        			if (sprite.kind == Sprite.KIND_SHELL)
	        				((Shell) sprite).facing *= -1;
	        			else
	        				((Enemy) sprite).facing *= -1;
	        			requireReplanning = true;
		        		sprite.x = x;
	        		}
	        		if ((sprite.y - y) != 0 && sprite.kind == Sprite.KIND_ENEMY_FLOWER)
	        		{
	        			if (verbose > 6) 
	        				System.out.println("Flower inaccurate! Diff: "+(sprite.x - x)+" "+(sprite.y - y));
	        			((Enemy) sprite).ya = (y - sprite.lastAccurateY) * 0.89f;//+= sprite.y - y;
		        		sprite.y = y;
	        		}
	        		enemyFound = true;
	        	}
	        	
	        	if (sprite.kind == kind && 
	        			(sprite.x - x) == 0 && 
	        			(sprite.y - y) != 0 && 
	        			Math.abs(sprite.y - y) < 8 && 
	        			sprite.kind != Sprite.KIND_SHELL &&
	        			sprite.kind != Sprite.KIND_BULLET_BILL &&
	        			((Enemy) sprite).winged)
	        	{
	        		// x accurate but y wrong. flying thing
	        		if (verbose > 6) 
	        			System.out.println("Adjusting height!");
	        		
	        		sprite.ya = (y - sprite.lastAccurateY) * 0.95f + 0.6f; // / 0.89f;
	        			
	        		sprite.y = y;
	        		sprite.unknownYA = false;
	        		enemyFound = true;
	        		requireReplanning = true;
	        	}
	        	if (sprite.kind == kind && 
	        			(sprite.x - x) == 0 && 
	        			(sprite.y - y) != 0 && 
	        			Math.abs(sprite.y - y) <= 2 && 
	        			sprite.unknownYA &&
	        			sprite.lastAccurateY != 0)
	        	{
	        		// should be not winged, falling down a cliff
	        		if (verbose > 6) 
	        			System.out.println("Correcting unknown YA. lastAccY: "+ sprite.lastAccurateY);
	        		sprite.ya = (y - sprite.lastAccurateY) * 0.85f + 2; // / 0.89f; 	        		
	        		sprite.y = y;	        		

	        		sprite.unknownYA = false;
	        		enemyFound = true;
	        	}
	        	
	        	if (enemyFound)
	        	{
	        		newSprites.add(sprite);
		        	sprite.lastAccurateX = x;
		        	sprite.lastAccurateY = y;
	        		break;
	        	}
	        }
	        // didn't find a close enemy in our representation of the world,
	        // create a new one.
	        if (!enemyFound)
	        {
	        	if (verbose > 6) 
	        		System.out.println("Creating new Enemy.");
	        	requireReplanning = true;
	        	Sprite sprite;
	        	if (type == Enemy.ENEMY_FLOWER)
	        	{
	        		int flowerTileX = (int) x/16;
	        		int flowerTileY = (int) y/16;
	        		
			        //sprite = new FlowerEnemy(this, flowerTileX*16+15, (int) y, flowerTileX, flowerTileY, (int) y);
                                sprite = new FlowerEnemy(this, flowerTileX*16+15, y, flowerTileX, flowerTileY);
	        	}
	        	else if (kind == Sprite.KIND_BULLET_BILL)
	        	{
	        		if (verbose > 0) 
	        			System.out.println("Adding Bullet Bill!");
	        		int dir = -1;
                                sprite = new BulletBill(this, x, y, dir);
	        	}
                        else if (kind == Sprite.KIND_WAVE_GOOMBA) //Added by Luong
                        {
                                int dir = -1;
                                sprite = new WaveGoomba(this, (int)x, (int)y, dir, (int) x/16, (int) y/16);
                        }
	        	else
	        	{
	        		// Add new enemy to the system.
	        		//sprite = new Enemy(this, x, y, -1, type, winged, (int) x/16, (int) y/16);
                                sprite = new Enemy(this, x, y, -1, type, winged, (int) x/16, (int) y/16);
	        		sprite.xa = 2;
	        	}
	        
	        	sprite.lastAccurateX = x;
	        	sprite.lastAccurateY = y;
		        //sprite.spriteTemplate =  new SpriteTemplate(type, winged);
                        sprite.spriteTemplate =  new SpriteTemplate(kind);
		        newSprites.add(sprite);
	        }
		}
		newSprites.add(mario);
		
		// add fireballs
		for (Sprite sprite:sprites)
                {
			if (sprite.kind == Sprite.KIND_FIREBALL)
				newSprites.add(sprite);
                }
		sprites = newSprites;
		return requireReplanning;
	}
        
    public int getGreenMushroomMode()
    {
        return greenMushroomMode;
    }
}