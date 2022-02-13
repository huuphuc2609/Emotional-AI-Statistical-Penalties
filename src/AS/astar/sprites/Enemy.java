package AS.astar.sprites;

import AS.astar.LevelScene;
import static java.lang.Thread.sleep;

public class Enemy extends Sprite implements Cloneable
{
    public static final int ENEMY_RED_KOOPA = 0;
    public static final int ENEMY_GREEN_KOOPA = 1;
    public static final int ENEMY_GOOMBA = 2;
    public static final int ENEMY_SPIKY = 3;
    public static final int ENEMY_FLOWER = 4;
    public static final int POSITION_WAVE_GOOMBA = 7;

    public float runTime;
    public boolean onGround = false;
    

    int width = 4;
    int height = 24;

    public float yaa = 1;
    //private LevelScene levelScene;
    public int facing;
    public int deadTime = 0;
    public boolean flyDeath = false;

    public boolean avoidCliffs = true;
    private int type;

    public boolean winged = true;
    public int wingTime = 0;
    
    //private boolean firstMove = true;
    
    public float yaw = 1;
    
    public boolean noFireballDeath;
    
    //Modifed by Luong - Default value of gravity for enemies.
    public float creaturesGravity = 1;

    public Enemy(LevelScene world, float xValue, float yValue, int dir, int type, boolean winged, int mapX, int mapY)
    {
        byte k = KIND_UNDEF;
        switch (type)
        {
            case ENEMY_RED_KOOPA:
                k = (byte) (82 + ((winged) ? 15 : 0));
                break;
            case ENEMY_GREEN_KOOPA:
                k = (byte) (81 + ((winged) ? 15 : 0));
                break;
            case ENEMY_GOOMBA:
                k = (byte) (80 + ((winged) ? 15 : 0));
                break;
            case ENEMY_FLOWER:
                k = (byte) (91);
                break;
            case ENEMY_SPIKY:
                k = (byte) (93 + ((winged) ? 6 : 0));
                break;
            case POSITION_WAVE_GOOMBA:
                k = (byte) (98);
                break;
        }
        kind = k;
        this.type = type;
        this.winged = winged;
        this.x = xValue;
        this.y = yValue;
        this.mapX = mapX;
        this.mapY = mapY;

        this.world = world;
        yaa = creaturesGravity * 2;
        yaw = creaturesGravity == 1 ? 1 : 0.3f * creaturesGravity;
        //move();

        avoidCliffs = type == Enemy.ENEMY_RED_KOOPA;
        
        noFireballDeath = type == Enemy.ENEMY_SPIKY;

        if (type > 1) height = 12;
        facing = dir;
        if (facing == 0) facing = 1;
        //this.xa *= 0.85;
    }

    @Override
    public void collideCheck()
    {
        if (deadTime != 0)
        {
            return;
        }

        float xMarioD = world.mario.x - this.x;
        float yMarioD = world.mario.y - this.y;
        
        if (xMarioD > -width*2-4 && xMarioD < width*2+4)
        {
            if (yMarioD > -height && yMarioD < world.mario.height)
            {
                if ((kind != KIND_SPIKY && kind != KIND_SPIKY_WINGED && kind != KIND_ENEMY_FLOWER) && world.mario.ya > 0 && yMarioD <= 0 && (!world.mario.onGround || !world.mario.wasOnGround))
                {
                    world.mario.stomp(this);
                    if (winged)
                    {
                        winged = false;
                        ya = 0;
                    }
                    else
                    {
                        if (spriteTemplate != null) spriteTemplate.isDead = true;
                        deadTime = 10;
                        winged = false;

                        if (kind == KIND_RED_KOOPA || kind == KIND_RED_KOOPA_WINGED)
                        {
                            spriteContext.addSprite(new Shell(world, x, y, 0));
                        } else if (kind == KIND_GREEN_KOOPA || kind == KIND_GREEN_KOOPA_WINGED)
                        {
                            spriteContext.addSprite(new Shell(world, x, y, 1));
                        }
                    }
                    ++LevelScene.killedCreaturesTotal;
                    ++LevelScene.killedCreaturesByStomp;
                }
                else
                {
                    world.mario.getHurt();
                }
            }
        }
    }

    public void move()
    {
        wingTime++;
        if (deadTime > 0)
        {
            deadTime--;

            if (deadTime == 0)
            {
                deadTime = 1;
                for (int i = 0; i < 8; i++)
                {
                    world.addSprite(new Sparkle((int) (x + Math.random() * 16 - 8) + 4, (int) (y - Math.random() * 8) + 4, (float) (Math.random() * 2 - 1), (float) Math.random() * -1, 0, 1, 5));
                }
                spriteContext.removeSprite(this);
            }

            if (flyDeath)
            {
                x += xa;
                y += ya;
                ya *= 0.95;
                ya += 1;
            }
            return;
        }


        float sideWaysSpeed = 1.75f;
        //        float sideWaysSpeed = onGround ? 2.5f : 1.2f;

        if (xa > 2)
        {
            facing = 1;
        }
        if (xa < -2)
        {
            facing = -1;
        }

        xa = facing * sideWaysSpeed;

        runTime += (Math.abs(xa)) + 5;

        if (!move(xa, 0)) facing = -facing;
        
        onGround = false;
        //System.out.println("Sim Enemy Kind " + kind + " before Y move Pos: " + x + " " + y + " a: " + xa + " " + ya );
        move(0, ya);
        //System.out.println("Sim Enemy Kind " + kind + " before Y move Pos: " + x + " " + y + " a: " + xa + " " + ya );
        
//        if (firstMove)
//        {
//        	onGround = true;
//        	firstMove = false;
//        }
        
        //System.out.println("Fake Enemy movement: Pos: "+x+" "+y+" Type: "+ kind + " Rasterized pos: " + mapX + " " + mapY);
        ya *= winged ? 0.95f : 0.85f;
        if (onGround)
        {
            xa *= (GROUND_INERTIA + windScale(windCoeff, facing) + iceScale(iceCoeff));
        }
        else
        {
            xa *= (AIR_INERTIA + windScale(windCoeff, facing) + iceScale(iceCoeff));
        }

        if (!onGround)
        {
            if (winged)
            {
                ya += 0.6f * yaw;
            }
            else
            {
                ya += yaa;
            }
        }
        else if (winged)
        {
            ya = -10;
            //System.out.println("Enemy jumps!");
        }
    }

    public boolean move(float xa, float ya)
    {
        while (xa > 8)
        {
            if (!move(8, 0)) return false;
            xa -= 8;
        }
        while (xa < -8)
        {
            if (!move(-8, 0)) return false;
            xa += 8;
        }
        while (ya > 8)
        {
            if (!move(0, 8)) return false;
            ya -= 8;
        }
        while (ya < -8)
        {
            if (!move(0, -8)) return false;
            ya += 8;
        }

        boolean collide = false;
        if (ya > 0)
        {
            if (isBlocking(x + xa - width, y + ya, xa, 0)) collide = true;
            else if (isBlocking(x + xa + width, y + ya, xa, 0)) collide = true;
            else if (isBlocking(x + xa - width, y + ya + 1, xa, ya)) collide = true;
            else if (isBlocking(x + xa + width, y + ya + 1, xa, ya)) collide = true;
        }
        if (ya < 0)
        {
            if (isBlocking(x + xa, y + ya - height, xa, ya)) collide = true;
            else if (collide || isBlocking(x + xa - width, y + ya - height, xa, ya)) collide = true;
            else if (collide || isBlocking(x + xa + width, y + ya - height, xa, ya)) collide = true;
        }
        if (xa > 0)
        {
            if (isBlocking(x + xa + width, y + ya - height, xa, ya)) collide = true;
            if (isBlocking(x + xa + width, y + ya - height / 2, xa, ya)) collide = true;
            if (isBlocking(x + xa + width, y + ya, xa, ya)) collide = true;

            if (avoidCliffs && onGround && !world.level.isBlocking((int) ((x + xa + width) / 16), (int) ((y) / 16 + 1), xa, 1)) collide = true;
        }
        if (xa < 0)
        {
            if (isBlocking(x + xa - width, y + ya - height, xa, ya)) collide = true;
            if (isBlocking(x + xa - width, y + ya - height / 2, xa, ya)) collide = true;
            if (isBlocking(x + xa - width, y + ya, xa, ya)) collide = true;

            if (avoidCliffs && onGround && !world.level.isBlocking((int) ((x + xa - width) / 16), (int) ((y) / 16 + 1), xa, 1)) collide = true;
        }

        if (collide)
        {
            if (xa < 0)
            {
                x = (int) ((x - width) / 16) * 16 + width;
                this.xa = 0;
            }
            if (xa > 0)
            {
                x = (int) ((x + width) / 16 + 1) * 16 - width - 1;
                this.xa = 0;
            }
            if (ya < 0)
            {
                y = (int) ((y - height) / 16) * 16 + height;
                this.ya = 0;
            }
            if (ya > 0)
            {
                y = (int) (y / 16 + 1) * 16 - 1;
                onGround = true;
            }
            return false;
        }
        else
        {
            x += xa;
            y += ya;
            return true;
        }
    }

    private boolean isBlocking(float _x, float _y, float xa, float ya)
    {
        int x = (int) (_x / 16);
        int y = (int) (_y / 16);
        if (x == (int) (this.x / 16) && y == (int) (this.y / 16)) return false;

        boolean blocking = world.level.isBlocking(x, y, xa, ya);

        return blocking;
    }

    public boolean shellCollideCheck(Shell shell)
    {
        if (deadTime != 0) return false;

        float xD = shell.x - x;
        float yD = shell.y - y;

        if (xD > -16 && xD < 16)
        {
            if (yD > -height && yD < shell.height)
            {
                xa = shell.facing * 2;
                ya = -5;
                flyDeath = true;
                if (spriteTemplate != null) spriteTemplate.isDead = true;
                deadTime = 100;
                winged = false;
                return true;
            }
        }
        return false;
    }

    public boolean fireballCollideCheck(Fireball fireball)
    {
        if (deadTime != 0) return false;

        float xD = fireball.x - x;
        float yD = fireball.y - y;

        if (xD > -16 && xD < 16)
        {
            if (yD > -height && yD < fireball.height)
            {
                if (noFireballDeath) return true;
                
                xa = fireball.facing * 2;
                ya = -5;
                flyDeath = true;
                if (spriteTemplate != null) spriteTemplate.isDead = true;
                deadTime = 100;
                winged = false;
                //System.out.println("FIREBALL hit enemy type " + this.type);
                return true;
            }
        }
        return false;
    }

    public void bumpCheck(int xTile, int yTile)
    {
        if (deadTime != 0) return;

        if (x + width > xTile * 16 && x - width < xTile * 16 + 16 && yTile == (int) ((y - 1) / 16))
        {
            xa = -world.mario.facing * 2;
            ya = -5;
            flyDeath = true;
            if (spriteTemplate != null) spriteTemplate.isDead = true;
            deadTime = 100;
            winged = false;
        }
    }

    /*
    public void render(Graphics og, float alpha)
    {
        if (winged)
        {
            int xPixel = (int) (xOld + (x - xOld) * alpha) - xPicO;
            int yPixel = (int) (yOld + (y - yOld) * alpha) - yPicO;

            if (type == Enemy.ENEMY_GREEN_KOOPA || type == Enemy.ENEMY_RED_KOOPA)
            {
            }
            else
            {
                xFlipPic = !xFlipPic;
                //og.drawImage(sheet[wingTime / 4 % 2][4], xPixel + (xFlipPic ? wPic : 0) + (xFlipPic ? 10 : -10), yPixel + (yFlipPic ? hPic : 0) - 8, xFlipPic ? -wPic : wPic, yFlipPic ? -hPic : hPic, null);
                xFlipPic = !xFlipPic;
            }
        }

        super.render(og, alpha);

        if (winged)
        {
            int xPixel = (int) (xOld + (x - xOld) * alpha) - xPicO;
            int yPixel = (int) (yOld + (y - yOld) * alpha) - yPicO;

            if (type == Enemy.ENEMY_GREEN_KOOPA || type == Enemy.ENEMY_RED_KOOPA)
            {
                og.drawImage(sheet[wingTime / 4 % 2][4], xPixel + (xFlipPic ? wPic : 0) + (xFlipPic ? 10 : -10), yPixel + (yFlipPic ? hPic : 0) - 10, xFlipPic ? -wPic : wPic, yFlipPic ? -hPic : hPic, null);
            }
            else
            {
                og.drawImage(sheet[wingTime / 4 % 2][4], xPixel + (xFlipPic ? wPic : 0) + (xFlipPic ? 10 : -10), yPixel + (yFlipPic ? hPic : 0) - 8, xFlipPic ? -wPic : wPic, yFlipPic ? -hPic : hPic, null);
            }
        }
    }*/
}