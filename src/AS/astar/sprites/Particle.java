/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AS.astar.sprites;

import ch.idsia.benchmark.mario.engine.Art;

public class Particle extends Sprite
{
public int life;

public Particle(int x, int y, float xa, float ya)
{
    this(x, y, xa, ya, (int) (Math.random() * 2), 0);
}

public Particle(int x, int y, float xa, float ya, int xPic, int yPic)
{
    kind = KIND_PARTICLE;
    this.x = x;
    this.y = y;
    this.xa = xa;
    this.ya = ya;

    life = 10;
}

public void move()
{
    if (life-- < 0) Sprite.spriteContext.removeSprite(this);
    x += xa;
    y += ya;
    ya *= 0.95f;
    ya += 3;
}
}