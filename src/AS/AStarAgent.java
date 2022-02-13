package AS;

/* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */ 

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.environments.Environment;

import AS.astar.AStarSimulator;
import AS.astar.sprites.Mario;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EAction;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EFeature;

public class AStarAgent extends BasicMarioAIAgent implements Agent
{
    private AStarSimulator sim;
    private float lastX = 0;
    private float lastY = 0;
    
    //Some penalty of strange behavior
    public int numOfJump = 0;
    public int RightButton = 0;
    public int LeftButton = 0;
    public int RunButton = 0;
    public int JumpButton = 0;
    
    public int Stand = 0;
    public int RJ = 0;
    public int LJ = 0;
    public int RS = 0;
    public int LS = 0;
    public int RJS = 0;
    public int LJS = 0;
    public int Kills = 0;
    public int changeActions = 0;
    public int encounterEnemies = 0;
    public int OnGround = 0;
    
    //Illegal actions
    public int LeftRight = 0;
    public int UpDown = 0;
    public int IllegalAction = 0;
    public int tick;
    
    EAction prevAct = null;
    int oldIsOnGround = 0;
    
    public EFeature fea = new EFeature();
    
    public AStarAgent(String s) {
        super(s);
        name = "AStarAgent";
        //sim = new AStarSimulator();
    }
    
    @Override
    public void reset()
    {
        action = new boolean[Environment.numberOfKeys];
        sim = new AStarSimulator();
        fea.resetCounter();
    }

    public void resetInfo()
    {
        tick = 0;
        numOfJump = 0;
        RightButton = 0;
        LeftButton = 0;
        RunButton = 0;
        JumpButton = 0;
        numOfJump = 0;

        Stand = 0;
        RJ = 0;
        LJ = 0;
        RS = 0;
        LS = 0;
        RJS = 0;
        LJS = 0;
        changeActions = 0;
        encounterEnemies = 0;
        OnGround = 0;
        LeftRight = 0;
        IllegalAction = 0;
    }
    
//    public AGENT_TYPE getType()
//    {
//        return Agent.AGENT_TYPE.AI;
//    }
    
    @Override
    public boolean[] getAction() {
        // This is the main function that is called by the mario environment.
    	// we're supposed to compute and return an action in here.
    	
    	long startTime = System.currentTimeMillis();
    	
    	// everything with "verbose" in it is debug output. 
    	// Set Levelscene.verbose to a value greater than 0 to enable some debug output.
    	String s = "Fire";
    	if (!sim.levelScene.mario.fire)
    		s = "Large";
    	if (!sim.levelScene.mario.large)
    		s = "Small";
    	if (sim.levelScene.verbose > 0) System.out.println("Next action! Simulated Mariosize: " + s);

    	boolean[] ac = new boolean[6];
    	ac[Mario.KEY_RIGHT] = true;
    	ac[Mario.KEY_SPEED] = true;
        
        //Modified by Luong
        for (int i = 0; i < enemiesFloatPos.length; i += 3)
        {
            {
                enemiesFloatPos[i+1] = enemiesFloatPos[i+1] + marioFloatPos[0];
                enemiesFloatPos[i+2] = enemiesFloatPos[i+2] + marioFloatPos[1];
            }
        }
        
//*****************************************************************************
//        System.out.print("Real Mario: ");
//        for(int i = 0; i < marioFloatPos.length; i++)
//        {
//            System.out.print(marioFloatPos[i] + " ");
//        }
//        System.out.println("");
//        System.out.print("Real Enemies: ");
//        for(int i = 0; i < enemiesFloatPos.length; i+=3)
//        {
//            if(enemiesFloatPos[i] == 84)
//                System.out.print(enemiesFloatPos[i] + " " + enemiesFloatPos[i+1] + " " + enemiesFloatPos[i+2]);
//        }
//        System.out.println("");
//*****************************************************************************
        
        //Modified by Luong - Reset the mario sprite after its dead.
        if(sim.levelScene.mario.status == Mario.STATUS_DEAD)
        {
            sim.levelScene.resetMario();
        }
        
   	sim.levelScene.verbose = -100;
    	if (sim.levelScene.verbose > 2) System.out.println("Simulating using action: " + sim.printAction(action));
        
    	// Advance the simulator to the state of the "real" Mario state
    	sim.advanceStep(action);   
       
		// Handle desynchronisation of mario and the environment.
		if (sim.levelScene.mario.x != marioFloatPos[0] || sim.levelScene.mario.y != marioFloatPos[1])
		{
			// Stop planning when we reach the goal (just assume we're in the goal when we don't move)
			if (marioFloatPos[0] == lastX && marioFloatPos[1] == lastY)
				return ac;

			// Some debug output
			if (sim.levelScene.verbose > 0) System.out.println("INACURATEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE!");
			if (sim.levelScene.verbose > 0) System.out.println("Real: "+marioFloatPos[0]+" "+marioFloatPos[1]
			      + " Est: "+ sim.levelScene.mario.x + " " + sim.levelScene.mario.y +
			      " Diff: " + (marioFloatPos[0]- sim.levelScene.mario.x) + " " + (marioFloatPos[1]-sim.levelScene.mario.y));
			
			// Set the simulator mario to the real coordinates (x and y) and estimated speeds (xa and ya)
			sim.levelScene.mario.x = marioFloatPos[0];
			sim.levelScene.mario.xa = (marioFloatPos[0] - lastX) *0.89f;
			if (Math.abs(sim.levelScene.mario.y - marioFloatPos[1]) > 0.1f)
				sim.levelScene.mario.ya = (marioFloatPos[1] - lastY) * 0.85f;// + 3f;

			sim.levelScene.mario.y = marioFloatPos[1];
		}
                
                //Modified by Luong - Synchronized mode
		if(sim.levelScene.mario.getMode() != marioMode)
                {
                    if (marioMode == 2)
                        sim.levelScene.mario.setMode(Mario.MODE.MODE_FIRE);
                    else if (marioMode == 1)
                        sim.levelScene.mario.setMode(Mario.MODE.MODE_LARGE);
                    else
                        sim.levelScene.mario.setMode(Mario.MODE.MODE_SMALL);
                }
                
                //System.out.println(sim.levelScene.mario.getMode());
		// Update the internal world to the new information received
		sim.setLevelPart(levelScene, enemiesFloatPos);
                
		lastX = marioFloatPos[0];
		lastY = marioFloatPos[1];
//*****************************************************************************
//                System.out.print("Sim enemies: ");
//                for(int i = 0; i < sim.levelScene.sprites.size(); ++i)
//                {
//                    if(sim.levelScene.sprites.get(i).kind == 84 || sim.levelScene.sprites.get(i).kind == -31)
//                    {
//                        System.out.println(sim.levelScene.sprites.get(i).kind + " " + sim.levelScene.sprites.get(i).x + " " + sim.levelScene.sprites.get(i).y);
//                    }
//                }
//*****************************************************************************
                
		// This is the call to the simulator (where all the planning work takes place)
        action = sim.optimise();
        if (sim.levelScene.verbose > 1) System.out.println("Returning action: " + sim.printAction(action));
        // Some time budgeting, so that we do not go over 40 ms in average.
        sim.timeBudget += 39 - (int)(System.currentTimeMillis() - startTime);

        EAction act = new EAction(action);
        fea.allAct.increaseCounter(act);
        fea.increaseIllegalAction(act);
        
        if(isMarioOnGround)
            fea.isOnGround++;
        //Get jumbable status
        double isJumpable = 0;
        double isOnGround = 0;
        double isShootable = 0;
        if(isMarioAbleToJump)
            isJumpable = 1;
        if(isMarioOnGround)
            isOnGround = 1;
        if(isMarioAbleToShoot)
            isShootable = 1;
        //Detect number of JumpAction
        if(oldIsOnGround == 1 && isOnGround != 1)
        {
            numOfJump++;
        }
        oldIsOnGround = (int)isOnGround;
        if(isMarioOnGround)
            OnGround++;
        
        if(prevAct != null)
        {
            if(!prevAct.equals(act))
            {
                fea.ChangeActions++;
            }
        }
        prevAct = act;
        
        tick++;
        return action;
    }

    @Override
    public void integrateObservation(Environment environment)
    {
        levelScene = environment.getLevelSceneObservationZ(0);
        enemies = environment.getEnemiesObservationZ(0);
        mergedObservation = environment.getMergedObservationZZ(1, 0);

        this.marioFloatPos = environment.getMarioFloatPos();
        this.enemiesFloatPos = environment.getEnemiesFloatPos();
        this.marioState = environment.getMarioState();

        // It also possible to use direct methods from Environment interface.
        //
        marioStatus = marioState[0];
        marioMode = marioState[1];
        isMarioOnGround = marioState[2] == 1;
        isMarioAbleToJump = marioState[3] == 1;
        isMarioAbleToShoot = marioState[4] == 1;
        isMarioCarrying = marioState[5] == 1;
        getKillsTotal = marioState[6];
        getKillsByFire = marioState[7];
        getKillsByStomp = marioState[8];
        getKillsByShell = marioState[9];
    }
}
