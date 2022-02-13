/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Agent;

import ch.idsia.agents.EvolutionalNeuralNetwork.*;
import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Phuc
 */
public class RuleBasedAgent extends BasicMarioAIAgent implements Agent {

    public static boolean debug = false;
    protected boolean Action[] = new boolean[Environment.numberOfKeys];
    protected boolean previousAction[] = new boolean[Environment.numberOfKeys];
    protected String name = "RuleBasedAgent";
    
    boolean flag = true;
    double focusX = 0;
    double focusY = 0;
    List<boolean[]> planner = new ArrayList<>();
    List<Point> listCoins = new ArrayList<>();

//    /*final*/
//    protected byte[][] levelScene;
//    /*final */
//    protected byte[][] enemies;
//    protected byte[][] mergedObservation;
//
//    protected float[] marioFloatPos = null;
//    protected float[] enemiesFloatPos = null;
//
//    protected int[] marioState = null;
//
//    protected int marioStatus;
//    protected int marioMode;
//    protected boolean isMarioOnGround;
//    protected boolean isMarioAbleToJump;
//    protected boolean isMarioAbleToShoot;
//    protected boolean isMarioCarrying;
//    protected int getKillsTotal;
//    protected int getKillsByFire;
//    protected int getKillsByStomp;
//    protected int getKillsByShell;
//
//    protected int receptiveFieldWidth;
//    protected int receptiveFieldHeight;
//    protected int marioEgoRow;
//    protected int marioEgoCol;

    // values of these variables could be changed during the Agent-Environment interaction.
    // Use them to get more detailed or less detailed description of the level.
    // for information see documentation for the benchmark <link: marioai.org/marioaibenchmark/zLevels
    int zLevelScene = 1;
    int zLevelEnemies = 0;
    
    public RuleBasedAgent() {
        super("RuleBasedAgent");
    }
    
    public void reset()
    {

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
    }

    public boolean[] getAction()
    {
        boolean[] action = new boolean[6];
        byte[][] sen = mergedObservation;
        double[] input = new double[9*9+2];
        int idx = 0;
        
        //Get jumbable status
        double isJumpable = 0;
        double isOnGround = 0;
        if(isMarioAbleToJump)
            isJumpable = 1;
        if(isMarioOnGround)
            isOnGround = 1;
        
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
        input[sen.length+sen[0].length] = isJumpable;
        input[sen.length+sen[0].length+1] = isOnGround;
        
        
        //Recognition of coins
        listCoins.clear();
        for(int i = 0; i < levelScene.length; ++i)
        {
            for(int j = 0; j < levelScene[i].length; ++j)
            {
                if(levelScene[i][j] == 2)
                {
                    double distanceX = (j - marioEgoRow)*16;
                    double distanceY = (i - marioEgoCol)*16;
//                    if(j < 3)
//                        distanceX = distanceX - 8;
//                    else if (j > 3)
//                        distanceX = distanceX - 8;
//                    if(i < 3)
//                        distanceY = distanceY - 8;
//                    else if (i > 3)
//                        distanceY = distanceY - 8;
                    distanceX = distanceX + marioFloatPos[0];
                    distanceY = distanceY + marioFloatPos[1];
                    Point coin = new Point(2,j,i,distanceX,distanceY);
                    //Check exitst coin
                    boolean exists = false;
                    for(int k = 0; k < listCoins.size(); ++k)
                    {
                        if(listCoins.get(k).x == coin.x && listCoins.get(k).y == coin.y)
                        {
                            exists = true;
                            break;
                        }
                    }
                    if(!exists)
                        listCoins.add(coin);
                }
            }
        }
        //Remove coins
        for(int i = 0; i < listCoins.size(); ++i)
        {
            if(levelScene[listCoins.get(i).y][listCoins.get(i).x] != 2
                    || Math.abs(listCoins.get(i).floatX - marioFloatPos[0]) < 8
                    && Math.abs(listCoins.get(i).floatY - marioFloatPos[1]) < 8)
            {
                listCoins.remove(i);
                i--;
            }
        }
        
        
        
//        action[1] = true;//Right
//        if(sen[2][3] > 2 || sen[1][3] > 2 || sen[0][3] > 2 || sen[1][4] > 2 || sen[0][4] > 2 || sen[2][2] > 2 || sen[3][3] > 2 && Math.abs(marioFloatPos[0] - enemiesFloatPos[2]) >= 4)
//        {
//            action[0] = true;
//            action[1] = false;
//        }
//        else if(sen[2][4] > 2 || sen[3][4] > 2)
//        { 
//            //action[4] = true;
//            action[3] = true;
//            //action[1] = false;
//            //action[0] = true;
//        }
//        if(sen[2][3] < 0)
//        {
//            action[3] = true;
//        }
//        if(previousAction[3] && isOnGround == 1)
//        {
//            action[3] = false;
//        }
        
//        if(flag)
//        {
//            boolean[] anAction = new boolean[6];
//            anAction[3] = true;
//            for(int i = 0; i < 9; ++i)
//            {
//                planner.add(anAction);
//            }
//            flag = false;
//        }
//        
//        
//        if(isMarioOnGround && isMarioAbleToJump)
//        {
//            if(planner.size() > 0)
//                action = planner.remove(0);
//        }
//        else if (previousAction[3])
//        {
//            if(planner.size() > 0)
//                action = planner.remove(0);
//        }
         
        //If there is no plan ahead
        if(planner.isEmpty())
        {
            if(listCoins.isEmpty())
            {
                boolean[] defaultAction = new boolean[6];
                defaultAction[1] = true;
                //Avoid obstacles a head
                if(sen[marioEgoRow-2][marioEgoCol+1] < 0 
                        || sen[marioEgoRow-1][marioEgoCol+1] < 0 
                        || sen[marioEgoRow][marioEgoCol+1] < 0 
                        || sen[marioEgoRow-2][marioEgoCol+2] < 0 
                        || sen[marioEgoRow-1][marioEgoCol+2] < 0 
                        || sen[marioEgoRow][marioEgoCol+2] < 0)
                    defaultAction[3] = true;
                planner.add(defaultAction);
            }
            //Chasing a coin
            if(!listCoins.isEmpty())
            {
                planner.clear();
                
                Point coin = listCoins.get(0);
                while(sen[coin.y][coin.x] != 2)
                {
                    //System.out.println(sen[coin.x][coin.y]);
                    coin = listCoins.get(0);
                }
                if(coin.floatX < marioFloatPos[0])
                {
                    boolean[] anAction = new boolean[6];
                    anAction[0] = true;
                    anAction[1] = false;
                    //Avoid obstacles a head
                    if(sen[marioEgoRow-2][marioEgoCol+1] < 0 
                        || sen[marioEgoRow-1][marioEgoCol+1] < 0 
                        || sen[marioEgoRow][marioEgoCol+1] < 0 
                        || sen[marioEgoRow-2][marioEgoCol+2] < 0 
                        || sen[marioEgoRow-1][marioEgoCol+2] < 0 
                        || sen[marioEgoRow][marioEgoCol+2] < 0)
                        anAction[3] = true;
                    planner.add(anAction);
                }
                else if (coin.floatX >= marioFloatPos[0])
                {
                    boolean[] anAction = new boolean[6];
                    anAction[0] = false;
                    anAction[1] = true;
                    //Avoid obstacles a head
                    if(sen[marioEgoRow-2][marioEgoCol+1] < 0 
                        || sen[marioEgoRow-1][marioEgoCol+1] < 0 
                        || sen[marioEgoRow][marioEgoCol+1] < 0 
                        || sen[marioEgoRow-2][marioEgoCol+2] < 0 
                        || sen[marioEgoRow-1][marioEgoCol+2] < 0 
                        || sen[marioEgoRow][marioEgoCol+2] < 0)
                        anAction[3] = true;
                    planner.add(anAction);
                }
                if(coin.floatY < marioFloatPos[1])
                {
                    boolean[] anAction = new boolean[6];
                        anAction[3] = true;
                    planner.add(anAction);
                }
            }
            
            
        }
        action = planner.remove(0);
        //Prevent holding jump button
        if(previousAction[3] && isMarioOnGround)
            action[3] = false;
        
        System.out.println("Number of coins: " + listCoins.size());
        for(int i = 0; i < listCoins.size(); ++i)
        {
            System.out.println("Coin " + i + " x=" + listCoins.get(i).x + " y=" + listCoins.get(i).y
            + " xFloat=" + listCoins.get(i).floatX + " yFloat=" + listCoins.get(i).floatY 
            + " Mario=(" + marioFloatPos[0] + "," + marioFloatPos[1] + ")" + "(" + marioFloatPos[0]/16 + "," + marioFloatPos[1]/16 + ")");
        }
//        for(int i = 0; i < 6; ++i)
//        {
//            if(action[i] == false)
//                System.out.print("0");
//            else
//                System.out.print("1");
//            if(i < 5)
//                System.out.print(",");
//        }
//        System.out.println("");
        //System.out.println(marioFloatPos[1]);
        //action[0] = true;//Left
        //action[1] = true;//Right
        //action[2] = true;//Down
        //action[3] = true;//Jump
        //action[4] = true;//Run & Shot
        //action[5] = true;//Up
        previousAction = action;
        return action;
    }
}
