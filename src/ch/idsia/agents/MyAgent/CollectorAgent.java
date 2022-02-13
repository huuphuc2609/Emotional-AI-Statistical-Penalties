/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.MyAgent;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.human.HumanKeyboardAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Phuc
 */
public class CollectorAgent extends HumanKeyboardAgent implements Agent
{
    protected boolean Action[] = new boolean[Environment.numberOfKeys];
    protected String name = "CollectorAgent";

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
    
    FileWriter fw = null;
    
    public CollectorAgent() {
        this.reset();
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
        mergedObservation = environment.getMergedObservationZZ(0, 0);

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
    
    public void setOutputFile(FileWriter outputFile)
    {
        fw = outputFile;
    }
    /*------------------------------------LevelScene and Actions----------------------------------------*/
    public boolean[] getAction()
    {
        Action = super.getAction();
        //float[] enemiesPos = Env.getEnemiesFloatPos();
        //byte[][] mergedObservation = Env.getLevelSceneObservationZ(1);
        String trackContent = "";
        try
        {
            boolean hasAction = false;
            for(int i = 0; i < Action.length; ++i)
            {
                if(Action[i])
                {
                    hasAction = true;
                    break;
                }
            }
            if(hasAction)
            {
                //Scene
                //System.out.print(levelScene.length);
                //System.out.print("x");
                //System.out.println(levelScene[0].length);
                
                for(int i = 0; i < levelScene.length; ++i)
                {
                    for(int j = 0; j < levelScene[i].length; ++j)
                    { 
                        System.out.print(String.valueOf(levelScene[i][j]) + " ");
                    }
                    System.out.println("");
                }
                System.out.println("");
                int distanceToTheNearestItemsX = 0;
                int distanceToTheNearestItemsY = 0;
                double distanceToTheNearestItems = 0;
                List<Integer> listX = new ArrayList<>();
                List<Integer> listY = new ArrayList<>();
                List<Double> distances = new ArrayList<>();
                for(int i = 0; i < enemies.length; ++i)
                {
                    for(int j = 0; j < enemies[i].length; ++j)
                    {
                        if(enemies[i][j] == 2 || enemies[i][j] == 3)
                        {
                            //Mario at [9][9] and [8][9]
                            double distance = Math.sqrt(1.0*((i-9)*(i-9)+(j-9)*(j-9)));
                            distances.add(distance);
                            listX.add(i-9);
                            listY.add(j-9);
                        }
                    }
                }
                //Scan list and pick the nearest items then set value to the input variable.
                if(listX.isEmpty())
                {
                    distanceToTheNearestItemsX = 0;
                    distanceToTheNearestItemsY = 0;
                }
                else
                {
                    int min = 0;
                    for(int i = 1; i < distances.size(); ++i)
                    {
                        if(distances.get(min) > distances.get(i))
                            min = i;
                    }
                    distanceToTheNearestItemsX = listX.get(min);
                    distanceToTheNearestItemsY = listY.get(min);
                }
                listX.clear();
                listY.clear();
                distances.clear();
//                System.out.println("Enemies float pos");
//                for(int i = 0; i < enemiesFloatPos.length; ++i)
//                {
//                    System.out.print(String.valueOf(enemiesFloatPos[i]) + " ");
//                
//                }
                System.out.println("Nearest: " + String.valueOf(distanceToTheNearestItems) + " " + distanceToTheNearestItemsX + " " + distanceToTheNearestItemsY);
                
                for(int i = 0; i < mergedObservation.length; ++i)
                {
                    for(int j = 0; j < mergedObservation[i].length; ++j)
                    { 
                        //System.out.print(String.valueOf(levelScene[i][j]) + ",");
                        //trackContent += String.valueOf(mergedObservation[i][j]) + ",";
                        //fw.write(String.valueOf(mergedObservation[i][j]) + ",");
                        trackContent += String.valueOf(mergedObservation[i][j]) + ",";
                    }
                }
                for(int i = 0; i < Action.length; ++i)
                {
                    //System.out.print(String.valueOf(Action[i]));
                    //fw.write(String.valueOf(Action[i]));
                    if(Action[i])
                    {
                        //System.out.print(String.valueOf(1));
                        //fw.write(String.valueOf(1));
                        //trackContent += i;
                        trackContent += "1";
                    }
                    else
                    {
                        //System.out.print(String.valueOf(0));
                        //fw.write(String.valueOf(0));
                        //trackContent += -i;
                        trackContent += "0";
                    }
                    if(i < Action.length-1)
                    {
                        //System.out.print(String.valueOf(","));
                        //fw.write(String.valueOf(","));
                        //trackContent += ",";
                        trackContent += ",";
                    }
                }
                //System.out.println("");
                //fw.write("\r\n");
                trackContent += "\r\n";
            }
            fw.write(trackContent);
        }
        catch(Exception ex)
        {
            //System.out.println(ex.getMessage());
        }
        
        return Action;
    }
    /*------------------------------------------------------------------------------------------------*/
//    public boolean[] getAction()
//    {
//        Action = super.getAction();
//        //Enemies float pos
////        for(int i = 0; i < enemiesFloatPos.length; ++i)
////        {
////            System.out.print(enemiesFloatPos[i] + " ");
////        }
//        //Is on ground
//        System.out.println("Is on ground: " + isMarioOnGround);
//        
//        //EgoPos
//        System.out.println("Mario pos: " + (int)marioFloatPos[0]/16 + ", " + (int)marioFloatPos[1]/16 + " - " + marioFloatPos[0] + ", " + marioFloatPos[1]);
//        
//        return Action;
//    }
}
