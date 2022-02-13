/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.QL;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.environments.Environment;

/**
 *
 * @author Phuc
 */
public class QLearningAgent extends BasicMarioAIAgent implements Agent {
    
    public Brain br = new Brain();
    private double oldX = 0;
    QLState oldState;
    boolean[] oldAction = new boolean[Environment.numberOfKeys];
    double QLintermediateReward = 0;
    double fitness = 0;
    public boolean isLearning = true;
    
    protected boolean Action[] = new boolean[Environment.numberOfKeys];
    protected String name = "QLAgent";

    /*final*/
    protected byte[][] levelScene;
    /*final */
    protected byte[][] enemies;
    protected byte[][] mergedObservation;

    protected float[] marioFloatPos = null;
    protected float[] enemiesFloatPos = null;

    protected int[] marioState = null;

    protected int marioStatus;
    protected int marioMode;
    protected boolean isMarioOnGround;
    protected boolean isMarioAbleToJump;
    protected boolean isMarioAbleToShoot;
    protected boolean isMarioCarrying;
    protected int getKillsTotal;
    protected int getKillsByFire;
    protected int getKillsByStomp;
    protected int getKillsByShell;

    protected int receptiveFieldWidth;
    protected int receptiveFieldHeight;
    protected int marioEgoRow;
    protected int marioEgoCol;

    // values of these variables could be changed during the Agent-Environment interaction.
    // Use them to get more detailed or less detailed description of the level.
    // for information see documentation for the benchmark <link: marioai.org/marioaibenchmark/zLevels
    int zLevelScene = 1;
    int zLevelEnemies = 0;
    
    public double instantReward = 0;
    private int receptiveFieldSize = 0;
    
    public QLearningAgent() {
        super("QLearningAgent");
    }
    
    @Override
    public void reset()
    {

    }
    
    @Override
    public void giveIntermediateReward(float intermediateReward)
    {
        QLintermediateReward = intermediateReward;
    }
    
    @Override
    public void setObservationDetails(final int rfWidth, final int rfHeight, final int egoRow, final int egoCol){
        receptiveFieldWidth = rfWidth;
        receptiveFieldHeight = rfHeight;

        marioEgoRow = egoRow;
        marioEgoCol = egoCol;
    }
    
    public void integrateObservation(Environment environment)
    {
        levelScene = environment.getLevelSceneObservationZ(zLevelScene);
        enemies = environment.getEnemiesObservationZ(zLevelEnemies);
        mergedObservation = environment.getMergedObservationZZ(1, 0);
        
//        //Generalized observation.
//        for(int i = 0; i < mergedObservation.length; ++i)
//        {
//            for(int j = 0; j < mergedObservation[i].length; ++j)
//            {
//                if(mergedObservation[i][j] < 0)
//                {
//                    mergedObservation[i][j] = -100;
//                }
//            }
//        }
        
        this.marioFloatPos = environment.getMarioFloatPos();
        this.enemiesFloatPos = environment.getEnemiesFloatPos();
        this.marioState = environment.getMarioState();

        receptiveFieldWidth = environment.getReceptiveFieldWidth();
        receptiveFieldHeight = environment.getReceptiveFieldHeight();

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
        
        int coin = environment.getEvaluationInfo().coinsGained;
        instantReward = marioFloatPos[0] - oldX - 1;
        //System.out.println(instantReward);
    }

    public boolean[] getAction()
    {
        boolean[] action = new boolean[6];
        byte[][] sen = mergedObservation;
        //byte[][] sen = levelScene;
        //int[] input = new int[5*5+4];
        double[] input = new double[receptiveFieldSize*receptiveFieldSize];
        int idx = 0;
        
        double averageDistClosestEnemy = 0;
        
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
        
        //Input handle
        for(int i = 0; i < sen.length; ++i)
        {
            for(int j = 0; j < sen[i].length; ++j)
            {
                //System.out.print(sen[i][j] + "\t");
                input[idx] = sen[i][j];
                idx++;
            }
            //System.out.println("");
        }

        
        if(isLearning)
        {
            double reward = 0;

            //Return the action
            //action = br.generateAction(input,action,reward);

            //State recognition

            QLState inputState = new QLState(input);

            br.recordState(input);

            action = br.getAnAction(inputState);

            if(oldState != null)
            {

                reward = instantReward;
                if (marioStatus == 0)
                    reward -= 100;
                reward += (inputState.s[inputState.s.length-1] - oldState.s[oldState.s.length-1]) * 0.9;

                br.update(oldState, inputState, reward, oldAction, action);
            }

            oldX = marioFloatPos[0];
            oldState = inputState;
            oldAction =  action;

    //        for(int i = 0; i < action.length; ++i)
    //        {
    //            oldAction[i] = action[i];
    //        }

            oldAction = action;
        }
        else
        {
            QLState inputState = new QLState(input);
            action = br.getAnAction(inputState);
        }
//        if(action == null)
//        {
//            action = br.getRandomAction();
//        }
        //System.out.println(br.map.size());
        return action;
    }

    void setReceptiveFieldSize(int fieldSize) {
        this.receptiveFieldSize = fieldSize;
    }
}
