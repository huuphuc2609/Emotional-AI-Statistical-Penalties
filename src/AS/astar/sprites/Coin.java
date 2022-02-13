/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AS.astar.sprites;

import AS.astar.LevelScene;

/**
 *
 * @author Phuc
 */
public class Coin extends Sprite {

    int width = 16;
    int height = 16;
    public int status; //0: available, -1: unreacable, 1:collected
    
    public Coin(LevelScene world, int xValue, int yValue, float realXValue, float realYValue){
        x = realXValue;
        y = realYValue;
        mapX = xValue;
        mapY = yValue;
        status = 0;
        this.world = world;
        kind = 1;
    }
    
    @Override
    public void move()
    {}
    
    @Override
    public void collideCheck()
    {
        float xMarioD = world.mario.x - this.x;
        float yMarioD = world.mario.y - this.y;
        
        if (xMarioD > -width*2-4 && xMarioD < width*2+4)
        {
            if (yMarioD > -height && yMarioD < world.mario.height)
            {
                world.coinsCollected++;
                //world.mario.devourCoin();
                status = 1;
            }
        }
    }
}
