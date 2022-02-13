/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Humanlike;

import ch.idsia.agents.Agent;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.DynamicLineAndTimeSeriesChart;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EAction;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EFeature;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EState;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EStateAction;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EmotionalPerceptron;
import ch.idsia.agents.EvolutionalNeuralNetwork.EvolNetwork;
import ch.idsia.agents.EvolutionalNeuralNetwork.MarioAIANNInput;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author Phuc
 */
public class PenaltiesAgent extends BasicMarioAIAgent implements Agent {
    
    public List<EAction> listActs = new ArrayList<>();
    public boolean runTest = false;
    
    public EvolNetwork nn;
    public static boolean debug = false;
    protected boolean Action[] = new boolean[Environment.numberOfKeys];
    protected String name = "StatisticalPenaltiesAgent";
    
    private int nInput = 0;
    private int nOutput = 0;
    int countI = 0;
    /*final*/
    protected byte[][] levelScene;
    /*final */
    protected byte[][] enemies;
    protected byte[][] mergedObservation;
    protected double[][] inputObservation;
    protected double[][] inputEnemiesObservation;

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
    
    //Flag part
    public boolean isShiftRight = false;
    public boolean useEnemiesInfo = false;
    public boolean isTraining = false;
    
    public int oldNumOffPresenceEnemies = 0;
    public int numOfPresentEnemies = 0;

    
    public List<double[]> agentTrace = new ArrayList<>();
    public HashMap<EState, EAction> track = new HashMap<>();
    public EState oldState = null;
    public EAction oldAction = null;
    public List<EStateAction> seqStateAction = new ArrayList<>();
    
    
    int tick = 0;
  
    
    //Some penalty of strange behavior
    public int numOfJump = 0;
    public EFeature fea = new EFeature();
    
    
    EAction prevAct = null;
    
    int oldIsOnGround = 0; 
    
    int currentGameTime = 201;
    int prevTime = 0;
    double posAtOneSecondBefore = 32;
    
    public boolean use10frame = true;
    public List<EAction> list10frame = new ArrayList<>();
    
    int[] previousNumActs = new int[6];
    
    public int numUsedFrame;
    
    //Instance reward Sep 21st, 2017.
    public double iReward = 0;
    public int collectedCoin = 0;
    public int stuckTime = 0;
    public double prevPos = 0;
    public double prvDistanceToTheNearestCoin = 9000;
    public double nearestPosToTheCoin = 9000;
    
    public void resetInformation()
    {
        prvDistanceToTheNearestCoin = 9000;
        prevPos = 0;
        stuckTime = 0;
        iReward = 0;
        collectedCoin = 0;
        nearestPosToTheCoin = 9000;
        agentTrace.clear();
        track.clear();
        seqStateAction.clear();
        numOfJump = 0;
        tick = 0;
        fea.resetCounter();
        previousNumActs[0] = 0;
        previousNumActs[1] = 0;
        previousNumActs[2] = 0;
        previousNumActs[3] = 0;
        previousNumActs[4] = 0;
        previousNumActs[5] = 0;
        oldIsOnGround = 0;
        currentGameTime = 201;
        prevTime = 0;
        posAtOneSecondBefore = 32;
        prevAct = null;
        list10frame.clear();
        isMarioAbleToJump = false;
        isMarioAbleToShoot = false;
        isMarioCarrying = false;
        isMarioOnGround = false;      
        levelScene = null;
        enemies = null;
        mergedObservation = null;
        inputObservation = null;
        inputEnemiesObservation = null;

    }
        
    public PenaltiesAgent() {
        super("PenaltiesAgent");
        fea = new EFeature();
               
        numOfJump = 0;
        tick = 0;
        previousNumActs[0] = 0;
        previousNumActs[1] = 0;
        previousNumActs[2] = 0;
        previousNumActs[3] = 0;
        previousNumActs[4] = 0;
        previousNumActs[5] = 0;
        oldIsOnGround = 0;
        currentGameTime = 201;
        prevTime = 0;
        posAtOneSecondBefore = 32;
        prevAct = null;
        list10frame.clear();
    }
    
    @Override
    public void reset()
    {
        agentTrace.clear();
        resetInformation();
    }
    
    @Override
    public void setObservationDetails(final int rfWidth, final int rfHeight, final int egoRow, final int egoCol){
        receptiveFieldWidth = rfWidth;
        receptiveFieldHeight = rfHeight;

        marioEgoRow = egoRow;
        marioEgoCol = egoCol;
    }
    
    public double normalize(double in, double max, double min)
    {
        return (in-min)/(max-min);
    }
    
    public void integrateObservation(Environment environment)
    {
        levelScene = environment.getLevelSceneObservationZ(zLevelScene);
        enemies = environment.getEnemiesObservationZ(zLevelEnemies);
        mergedObservation = environment.getMergedObservationZZ(1, 0);
                
        this.marioFloatPos = environment.getMarioFloatPos();
        this.enemiesFloatPos = environment.getEnemiesFloatPos();
        this.marioState = environment.getMarioState();

        receptiveFieldWidth = environment.getReceptiveFieldWidth();
        receptiveFieldHeight = environment.getReceptiveFieldHeight();

        marioEgoRow = environment.getMarioEgoPos()[0];
        marioEgoCol = environment.getMarioEgoPos()[1];
        
        //If one more coin is collected
        if(environment.getEvaluationInfo().coinsGained > collectedCoin)
            iReward += 100;
        collectedCoin = environment.getEvaluationInfo().coinsGained;
        
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
        
        int SizeInputObs = 7;
        inputObservation = new double[SizeInputObs][SizeInputObs];
        inputEnemiesObservation = new double[SizeInputObs][SizeInputObs];
        int z1, z2;
        z1 = 0;
        z2 = 0;
        for(int i = 0; i < mergedObservation.length; ++i)
        {
            for(int j = 0; j < mergedObservation[0].length; ++j)
            {
                if(isShiftRight)
                {
                    if(i > (mergedObservation.length - SizeInputObs)/2 - 1
                            && j > (mergedObservation.length - SizeInputObs)/2 + 1
                            && i < mergedObservation.length - (mergedObservation.length - SizeInputObs-1)/2 - 1 
                            && j < mergedObservation.length - (mergedObservation.length - SizeInputObs-1)/2 + 1)
                    {
                        inputObservation[z1][z2] = mergedObservation[i][j];
                        inputEnemiesObservation[z1][z2] = enemies[i][j];
                        z2++;
                        if(z2 == SizeInputObs)
                        {
                            z1++;
                            z2 = 0;
                        }
                    }
                }
                else
                {
                    if(i > (mergedObservation.length - SizeInputObs)/2 - 1
                            && j > (mergedObservation.length - SizeInputObs)/2 - 1
                            && i < mergedObservation.length - (mergedObservation.length - SizeInputObs-1)/2 - 1 
                            && j < mergedObservation.length - (mergedObservation.length - SizeInputObs-1)/2 - 1)
                    {
                        inputObservation[z1][z2] = mergedObservation[i][j];
                        inputEnemiesObservation[z1][z2] = enemies[i][j];
                        z2++;
                        if(z2 == SizeInputObs)
                        {
                            z1++;
                            z2 = 0;
                        }
                    }
                }
            }
        }
        
        currentGameTime = environment.getTimeLeft();
        //System.out.println(marioFloatPos[0] +" --- "+ marioFloatPos[1]);
        //System.out.println("Creatures: " + ((MarioEnvironment)environment).getLevel());
//        for(int i = 0; i < ((MarioEnvironment)environment).getLevel().map.length; ++i)
//        {
//            for(int j = 0; j < ((MarioEnvironment)environment).getLevel().map[i].length; ++j)
//            {
//                System.out.print(((MarioEnvironment)environment).getLevel().map[i][j] + " ");
//            }
//            System.out.println("");
//        }
        
        
//        for(int i = 0; i < inputObservation.length; ++i)
//        {
//            for(int j = 0; j < inputObservation.length; ++j)
//            {
//                System.out.print(inputObservation[i][j] + " ");
//            }
//            System.out.println("");
//        }
//        System.out.println("");
    }
    
 
    public boolean[] getAction()
    {
       //Must < 40 ms
        //System.out.println("New fea: " + currentGameTime + " ---- " + prevTime);
        //System.out.println("New fea: " + posAtOneSecondBefore);
        //long s1 = System.nanoTime();
        
        MarioAIANNInput inputObj = new MarioAIANNInput();
        if(use10frame)
            inputObj.setNumberOfInput(26);
        else
            inputObj.setNumberOfInput(20);
        
        double[] input;

        //Get pos at one second before
        if(Math.abs(currentGameTime - prevTime) == 1)
        {
            posAtOneSecondBefore = marioFloatPos[0];
        }
                
        //Get jumbable status
        inputObj.setJumpable(isMarioAbleToJump);
        inputObj.setIsOnGround(isMarioOnGround);
        inputObj.setIsShootable(isMarioAbleToShoot);

//        for(int i = 0; i < sen.length; ++i)
//        {
//            for(int j = 0; j < sen[i].length; ++j)
//            {
//                input[idx] = sen[i][j];
//            }
//        }

        
        //Front obstacles
        inputObj.set4CellsInFront(mergedObservation);

        //Distance from the ground
        int numCellFromTheGround = 0;
        for(int i = 10; i < mergedObservation.length; ++i)
        {
            if(mergedObservation[i][9] >= 0)
            {
                numCellFromTheGround++;
            }
            else
            {
                break;
            }
        }
        inputObj.setNumCellFromTheGround(numCellFromTheGround);
        
        //Front gap
        boolean isGapInfront = true;
        for(int i = 10; i < mergedObservation.length; ++i)
        {
            if(mergedObservation[i][11] != 0 && mergedObservation[i][12] != 0)
            {
                isGapInfront = false;
                break;
            }
        }
        inputObj.setIsGapInfront(isGapInfront);
        
        //Mario float pos
        inputObj.setMarioFloatPos(marioFloatPos);
        //Enemies float pos
        inputObj.setEnemiesFloatPos(enemiesFloatPos);
        //Nearest item
        inputObj.setDistanceToNearestItem(enemies);
        //Get input as array
        input = inputObj.getInputAsDoubleArray();
        if(input[4] == 1)
            fea.infrontObj++;
            
        boolean[] action = nn.generateAction(input);
//        if(action[0] && action[1])
//            action[1] = false;
//        long testS2 = System.nanoTime();
//        double S3 = (1.0*(testS2 - testS1))/1000/1000;
//        String rs = String.format("%.2f", S3);
//        System.out.println("ANN time: " + rs);
        //long s1 = System.nanoTime();
        if(use10frame)
        {
            for(int k = 0; k < action.length; ++k)
            {
                if(action[k])
                {
                    switch(k)
                    {
                        case 0: previousNumActs[0]++; break;
                        case 1: previousNumActs[1]++; break;
                        case 2: previousNumActs[2]++; break;
                        case 3: previousNumActs[3]++; break;
                        case 4: previousNumActs[4]++; break;
                        case 5: previousNumActs[5]++; break;
                    }
                }
            }
            inputObj.setNumActs(previousNumActs);
            
            if(list10frame.size() == numUsedFrame)
            {
                for(int k = 0; k < list10frame.get(0).action.length; ++k)
                {
                    if(list10frame.get(0).action[k])
                    {
                        switch(k)
                        {
                            case 0: previousNumActs[0]--; break;
                            case 1: previousNumActs[1]--; break;
                            case 2: previousNumActs[2]--; break;
                            case 3: previousNumActs[3]--; break;
                            case 4: previousNumActs[4]--; break;
                            case 5: previousNumActs[5]--; break;
                        }
                    }
                }
                list10frame.remove(0);
            }
            list10frame.add(new EAction(action));
        }

        EAction aa = new EAction(action);
        //Store actions
        if(runTest)
        {
            listActs.add(new EAction(aa));
        }
        
        
        //as = new EState(input,aa,tick);
        //as = new EState(input,aa,0);
        //track.put(as, aa);
        //seqStateAction.add(new EStateAction(new EState(as), new EAction(aa)));
        
        //Tracking
        //double[] aTrace = new double[2];
        //aTrace[0] = marioFloatPos[0]; //x
        //aTrace[1] = marioFloatPos[1]; //y
        //agentTrace.add(aTrace);
        
        //Detect number of JumpAction
        int isOnGround;
        if(isMarioOnGround)
            isOnGround = 1;
        else
            isOnGround = 0;
        if(oldIsOnGround == 1 && isOnGround != 1)
        {
            numOfJump++;
            fea.numJump++;
        }
        oldIsOnGround = (int)isOnGround;
                       
        fea.allAct.increaseCounter(aa);
        fea.increaseIllegalAction(aa);
        
        if(isMarioOnGround)
            fea.isOnGround++;

        if(prevAct != null)
        {
            if(!prevAct.equals(aa))
            {
                fea.ChangeActions++;
            }
        }
        prevAct = aa;
        
        tick++;
        prevTime = currentGameTime;
        
//        if(action[0])
//        {
//            action[1] = false;
//        }
//        else if(action[1])
//        {
//            action[0] = false;
//        }
        
//        long s2 = System.nanoTime();
//        double rs = (1.0*(s2 - s1))/1000000;
//        System.out.println("Preparing: " + String.format("%.2f", rs));
        //System.out.println("Size PrevAct: " + list10frame.size());
        //System.out.println("TestPrevActs: " +  previousNumActs[0] + " " + previousNumActs[1] + " " + previousNumActs[2] + " " + previousNumActs[3] + " " + previousNumActs[4] + " " + previousNumActs[5]);
        
        
        //***************Print distance to closest coin***************
        double distanceToCoinIfExist = 9000;
        for(int i = 0; i < levelScene.length; ++i)
        {
            for(int j = 0; j < levelScene[i].length; ++j)
            {
                if(levelScene[i][j] == 2) //If there is a coin
                {
                    //System.out.println("M: " + marioEgoRow + " " + marioEgoCol);
                    //System.out.println("D to Coin i: " + (i - marioEgoRow) + " j: " + (j - marioEgoCol));
                    double dvX = ((16.0*((i - marioEgoRow))+marioFloatPos[0]) - marioFloatPos[0]);
                    double dvY = ((16.0*((j - marioEgoCol))+marioFloatPos[1]) - marioFloatPos[1]);
                    double distance = Math.sqrt(dvX + dvY);
                    if(distanceToCoinIfExist > distance)
                        distanceToCoinIfExist = distance;
                    //Get the nearest distance to the coin
                    if(nearestPosToTheCoin > distanceToCoinIfExist)
                    {
                        nearestPosToTheCoin = distanceToCoinIfExist;
                    }
                }
            }
        }
        //System.out.println("Value=" + dvF);
        double instanceReward = 0;
        if(nearestPosToTheCoin == 9000) // If there is no coin in the observation
        {
            //dvF = marioFloatPos[0]*0.00000001;
            //iReward += dvF;
            prvDistanceToTheNearestCoin = 9000;
            instanceReward = 0;
        }
        else if(nearestPosToTheCoin != 0 && nearestPosToTheCoin != 9000) // If there is a coin and we have a distance
        {
            if(nearestPosToTheCoin <= prvDistanceToTheNearestCoin)
                instanceReward += 1.0/nearestPosToTheCoin;
            prvDistanceToTheNearestCoin = nearestPosToTheCoin;
        }
        else if (nearestPosToTheCoin == 0)
        {
            prvDistanceToTheNearestCoin = 9000;
        }
        
        if((int)prevPos != (int)marioFloatPos[0])
        {
            prevPos = marioFloatPos[0];
            stuckTime = 0;
        }
        else
        {
            stuckTime++;
            if(PenaltiesAgent.debug)
                System.out.println("Stuck");
            instanceReward = 0;
        }
        if(stuckTime > 120) //If 120 frames passed, then give some penalty.
        {
            //instanceReward = -stuckTime;
        }
        iReward += instanceReward;
        if(PenaltiesAgent.debug)
        {
            System.out.println("Prev Pos:" + prevPos + " " + marioFloatPos[0]);
            System.out.println("instanceR = " + instanceReward + " Reward = " + iReward);
        }
        
        
        
        return action;
    }
    
    /**
     * @return the nInput
     */
    public int getnInput() {
        return nInput;
    }

    /**
     * @param nInput the nInput to set
     */
    public void setnInput(int nInput) {
        this.nInput = nInput;
    }

    /**
     * @return the nOutput
     */
    public int getnOutput() {
        return nOutput;
    }

    /**
     * @param nOutput the nOutput to set
     */
    public void setnOutput(int nOutput) {
        this.nOutput = nOutput;
    }
}
