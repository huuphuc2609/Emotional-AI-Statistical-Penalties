/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Emotions;

import ch.idsia.agents.Agent;
import ch.idsia.agents.EvolutionalNeuralNetwork.EvolNetwork;
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
public class EmotionalAgent extends BasicMarioAIAgent implements Agent {

    public EvolNetwork nn;
    public static boolean debug = false;
    protected boolean Action[] = new boolean[Environment.numberOfKeys];
    protected String name = "EmotionalAgent";
    
    private int nInput = 0;
    private int nOutput = 0;

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
    
    //Emotional part
    public boolean useEmotion = false;
    public boolean isShiftRight = false;
    public boolean useEnemiesInfo = false;
    
    public int oldNumOffPresenceEnemies = 0;
    public int numOfPresentEnemies = 0;

    public EmotionalPerceptron neutral;
    public EmotionalPerceptron happy;
    public EmotionalPerceptron curiosity;
    public EmotionalPerceptron fear;
    
    public List<double[]> agentTrace = new ArrayList<>();
    public HashMap<EState, EAction> track = new HashMap<>();
    public EState oldState = null;
    public EAction oldAction = null;
    public List<EStateAction> seqStateAction = new ArrayList<>();
    
    final DynamicLineAndTimeSeriesChart emotionalChart;
    
    int tick = 0;
  
    
    //Some penalty of strange behavior
    public int numOfJump = 0;
    int oldIsOnGround = 0;
    
    
    public EmotionalAgent() {
        super("EmotionalAgent");
        
        emotionalChart = new DynamicLineAndTimeSeriesChart("Emotion monitor");
        emotionalChart.setChartName("Fear Emotion");
        emotionalChart.setxAxisName("Tick");
        emotionalChart.setyAxisName("vout");
        emotionalChart.setRangeYAxis(-0.1, 2.0);
        emotionalChart.setShowTime(1000.0);
        emotionalChart.initializeChart();
        emotionalChart.pack();
        RefineryUtilities.centerFrameOnScreen(emotionalChart);
        //emotionalChart.setVisible(true);
    }
    
    @Override
    public void reset()
    {
        agentTrace.clear();
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
                
        this.marioFloatPos = environment.getMarioFloatPos();
        this.enemiesFloatPos = environment.getEnemiesFloatPos();
        this.marioState = environment.getMarioState();

        receptiveFieldWidth = environment.getReceptiveFieldWidth();
        receptiveFieldHeight = environment.getReceptiveFieldHeight();

        marioEgoRow = environment.getMarioEgoPos()[0];
        marioEgoCol = environment.getMarioEgoPos()[1];
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
    
    public void initializeEmotionUnit()
    {
        neutral = new EmotionalPerceptron();
        happy = new EmotionalPerceptron();
        curiosity = new EmotionalPerceptron();
        fear = new EmotionalPerceptron();
        neutral.setInput(nInput);
        happy.setInput(nInput);
        curiosity.setInput(nInput);
        fear.setInput(nInput);
        neutral.initializeConnection();
        happy.initializeConnection();
        curiosity.initializeConnection();
        fear.initializeConnection();
        initFearWeights5x5();
        initHappyWeights5x5();
        initCuriosityWeights5x5();
    }
    
    public void initFearWeights5x5()
    {
        fear.decay = 0.9;
        fear.vmax = 0.5;
        fear.vmin = 0.2;
        int ego = nInput/2;
        
        double max = 0.4;
        double inteval = 0.1;
        //Close positions
        fear.setWeightAt(ego, ego, max);
        fear.setWeightAt(ego-1, ego, max);
        fear.setWeightAt(ego, ego+1, max);
        fear.setWeightAt(ego, ego-1, max);
        fear.setWeightAt(ego-1, ego, max);
        fear.setWeightAt(ego-1, ego+1, max);
        fear.setWeightAt(ego-1, ego-1, max);
        fear.setWeightAt(ego-2, ego, max);
        fear.setWeightAt(ego-2, ego+1, max);
        fear.setWeightAt(ego-2, ego-1, max);
        //Near position
        fear.setWeightAt(ego, ego+2, max-inteval*1);
        fear.setWeightAt(ego, ego-2, max-inteval*1);
        fear.setWeightAt(ego-1, ego+2, max-inteval*1);
        fear.setWeightAt(ego-1, ego-2, max-inteval*1);
        fear.setWeightAt(ego-2, ego+2, max-inteval*1);
        fear.setWeightAt(ego-2, ego-2, max-inteval*1);
        
//        for(int i = 0; i < nInput; i++)
//        {
//            for(int j = 0; j < nInput; j++)
//            {
//                System.out.print(fear.connections[i][j] + " ");
//            }
//            System.out.println("");
//        }
        
    }
    
    public void initFearWeights7x7()
    {
        fear.decay = 0.9;
        fear.vmax = 0.5;
        fear.vmin = 0.2;
        int ego = nInput/2;
        
        double max = 0.4;
        double inteval = 0.1;
        //Close positions
        fear.setWeightAt(ego, ego, max);
        fear.setWeightAt(ego-1, ego, max);
        fear.setWeightAt(ego, ego+1, max);
        fear.setWeightAt(ego, ego-1, max);
        fear.setWeightAt(ego-1, ego, max);
        fear.setWeightAt(ego-1, ego+1, max);
        fear.setWeightAt(ego-1, ego-1, max);
        fear.setWeightAt(ego-2, ego, max);
        fear.setWeightAt(ego-2, ego+1, max);
        fear.setWeightAt(ego-2, ego-1, max);
        //Near position
        fear.setWeightAt(ego, ego+2, max-inteval*1);
        fear.setWeightAt(ego, ego-2, max-inteval*1);
        fear.setWeightAt(ego-1, ego+2, max-inteval*1);
        fear.setWeightAt(ego-1, ego-2, max-inteval*1);
        fear.setWeightAt(ego-2, ego+2, max-inteval*1);
        fear.setWeightAt(ego-2, ego-2, max-inteval*1);
        fear.setWeightAt(ego-3, ego, max-inteval*1);
        fear.setWeightAt(ego-3, ego+1, max-inteval*1);
        fear.setWeightAt(ego-3, ego-1, max-inteval*1);
        //Far position
        fear.setWeightAt(ego, ego+3, max-inteval*2);
        fear.setWeightAt(ego, ego-3, max-inteval*2);
        fear.setWeightAt(ego-1, ego+3, max-inteval*2);
        fear.setWeightAt(ego-1, ego-3, max-inteval*2);
        fear.setWeightAt(ego-2, ego+3, max-inteval*2);
        fear.setWeightAt(ego-2, ego-3, max-inteval*2);        
        fear.setWeightAt(ego-3, ego+2, max-inteval*2);
        fear.setWeightAt(ego-3, ego-2, max-inteval*2);
        fear.setWeightAt(ego-3, ego+3, max-inteval*3);
        fear.setWeightAt(ego-3, ego-3, max-inteval*3);
        
//        for(int i = 0; i < nInput; i++)
//        {
//            for(int j = 0; j < nInput; j++)
//            {
//                System.out.print(fear.connections[i][j] + " ");
//            }
//            System.out.println("");
//        }
        
    }
    
    public void initHappyWeights5x5()
    {
        happy.decay = 0.9;
        happy.vmax = 0.5;
        happy.vmin = 0.2;
        int ego = nInput/2;
        
        double max = 0.2;
        double inteval = 0.1;
        //Close positions
        happy.setWeightAt(ego, ego, max);
        happy.setWeightAt(ego, ego+1, max);
        happy.setWeightAt(ego, ego-1, max);
        happy.setWeightAt(ego-1, ego, max);
        happy.setWeightAt(ego-1, ego+1, max);
        happy.setWeightAt(ego-1, ego-1, max);
        happy.setWeightAt(ego+1, ego, max);
        happy.setWeightAt(ego+1, ego+1, max);
        happy.setWeightAt(ego+1, ego-1, max);
        //Near position
        happy.setWeightAt(ego, ego+2, max-inteval*1);
        happy.setWeightAt(ego, ego-2, max-inteval*1);
        happy.setWeightAt(ego-2, ego, max-inteval*1);
        happy.setWeightAt(ego+2, ego, max-inteval*1);
        happy.setWeightAt(ego-1, ego+2, max-inteval*1);
        happy.setWeightAt(ego-1, ego-2, max-inteval*1);
        happy.setWeightAt(ego+1, ego+2, max-inteval*1);
        happy.setWeightAt(ego+1, ego-2, max-inteval*1);
        happy.setWeightAt(ego-2, ego+1, max-inteval*1);
        happy.setWeightAt(ego-2, ego-1, max-inteval*1);
        happy.setWeightAt(ego+2, ego+1, max-inteval*1);
        happy.setWeightAt(ego+2, ego-1, max-inteval*1);
        happy.setWeightAt(ego-2, ego+2, max-inteval*1);
        happy.setWeightAt(ego-2, ego-2, max-inteval*1);
        happy.setWeightAt(ego+2, ego+2, max-inteval*1);
        happy.setWeightAt(ego+2, ego-2, max-inteval*1);
        
//        for(int i = 0; i < nInput; i++)
//        {
//            for(int j = 0; j < nInput; j++)
//            {
//                System.out.print(happy.connections[i][j] + " ");
//            }
//            System.out.println("");
//        }
        
    }
    
    public void initHappyWeights7x7()
    {
        happy.decay = 0.9;
        happy.vmax = 0.5;
        happy.vmin = 0.2;
        int ego = nInput/2;
        
        double max = 0.2;
        double inteval = 0.1;
        //Close positions
        happy.setWeightAt(ego, ego, max);
        happy.setWeightAt(ego, ego+1, max);
        happy.setWeightAt(ego, ego-1, max);
        happy.setWeightAt(ego-1, ego, max);
        happy.setWeightAt(ego-1, ego+1, max);
        happy.setWeightAt(ego-1, ego-1, max);
        happy.setWeightAt(ego+1, ego, max);
        happy.setWeightAt(ego+1, ego+1, max);
        happy.setWeightAt(ego+1, ego-1, max);
        //Near position
        happy.setWeightAt(ego, ego+2, max-inteval*1);
        happy.setWeightAt(ego, ego-2, max-inteval*1);
        happy.setWeightAt(ego-2, ego, max-inteval*1);
        happy.setWeightAt(ego+2, ego, max-inteval*1);
        happy.setWeightAt(ego-1, ego+2, max-inteval*1);
        happy.setWeightAt(ego-1, ego-2, max-inteval*1);
        happy.setWeightAt(ego+1, ego+2, max-inteval*1);
        happy.setWeightAt(ego+1, ego-2, max-inteval*1);
        happy.setWeightAt(ego-2, ego+1, max-inteval*1);
        happy.setWeightAt(ego-2, ego-1, max-inteval*1);
        happy.setWeightAt(ego+2, ego+1, max-inteval*1);
        happy.setWeightAt(ego+2, ego-1, max-inteval*1);
        happy.setWeightAt(ego-2, ego+2, max-inteval*1);
        happy.setWeightAt(ego-2, ego-2, max-inteval*1);
        happy.setWeightAt(ego+2, ego+2, max-inteval*1);
        happy.setWeightAt(ego+2, ego-2, max-inteval*1);
        //Far position
        happy.setWeightAt(ego, ego+3, max-inteval*2);
        happy.setWeightAt(ego, ego-3, max-inteval*2);
        happy.setWeightAt(ego-3, ego, max-inteval*2);
        happy.setWeightAt(ego+3, ego, max-inteval*2);
        happy.setWeightAt(ego-1, ego+3, max-inteval*2);
        happy.setWeightAt(ego-1, ego-3, max-inteval*2);
        happy.setWeightAt(ego+1, ego+3, max-inteval*2);
        happy.setWeightAt(ego+1, ego-3, max-inteval*2);
        
        happy.setWeightAt(ego-2, ego+3, max-inteval*2);
        happy.setWeightAt(ego-2, ego-3, max-inteval*2);
        happy.setWeightAt(ego+2, ego+3, max-inteval*2);
        happy.setWeightAt(ego+2, ego-3, max-inteval*2);
        
        happy.setWeightAt(ego-3, ego+3, max-inteval*2);
        happy.setWeightAt(ego-3, ego-3, max-inteval*2);
        happy.setWeightAt(ego+3, ego+3, max-inteval*2);
        happy.setWeightAt(ego+3, ego-3, max-inteval*2);
        
        happy.setWeightAt(ego-3, ego+1, max-inteval*2);
        happy.setWeightAt(ego-3, ego-1, max-inteval*2);
        happy.setWeightAt(ego+3, ego+1, max-inteval*2);
        happy.setWeightAt(ego+3, ego-1, max-inteval*2);
        happy.setWeightAt(ego-3, ego+2, max-inteval*2);
        happy.setWeightAt(ego-3, ego-2, max-inteval*2);
        happy.setWeightAt(ego+3, ego+2, max-inteval*2);
        happy.setWeightAt(ego+3, ego-2, max-inteval*2);
        
//        for(int i = 0; i < nInput; i++)
//        {
//            for(int j = 0; j < nInput; j++)
//            {
//                System.out.print(happy.connections[i][j] + " ");
//            }
//            System.out.println("");
//        }
        
    }
    
    public void initCuriosityWeights5x5()
    {
        curiosity.decay = 0.9;
        curiosity.vmax = 0.5;
        curiosity.vmin = 0.2;
        int ego = nInput/2;
        
        double max = 0.15;
        double inteval = 0.05;
        //Close positions
        curiosity.setWeightAt(ego, ego, max);
        curiosity.setWeightAt(ego, ego+1, max);
        curiosity.setWeightAt(ego, ego-1, max);
        curiosity.setWeightAt(ego-1, ego, max);
        curiosity.setWeightAt(ego-1, ego+1, max);
        curiosity.setWeightAt(ego-1, ego-1, max);
        curiosity.setWeightAt(ego+1, ego, max);
        curiosity.setWeightAt(ego+1, ego+1, max);
        curiosity.setWeightAt(ego+1, ego-1, max);
        //Near position
        curiosity.setWeightAt(ego, ego+2, max-inteval*1);
        curiosity.setWeightAt(ego, ego-2, max-inteval*1);
        curiosity.setWeightAt(ego-2, ego, max-inteval*1);
        curiosity.setWeightAt(ego+2, ego, max-inteval*1);
        curiosity.setWeightAt(ego-1, ego+2, max-inteval*1);
        curiosity.setWeightAt(ego-1, ego-2, max-inteval*1);
        curiosity.setWeightAt(ego+1, ego+2, max-inteval*1);
        curiosity.setWeightAt(ego+1, ego-2, max-inteval*1);
        curiosity.setWeightAt(ego-2, ego+1, max-inteval*1);
        curiosity.setWeightAt(ego-2, ego-1, max-inteval*1);
        curiosity.setWeightAt(ego+2, ego+1, max-inteval*1);
        curiosity.setWeightAt(ego+2, ego-1, max-inteval*1);
        curiosity.setWeightAt(ego-2, ego+2, max-inteval*1);
        curiosity.setWeightAt(ego-2, ego-2, max-inteval*1);
        curiosity.setWeightAt(ego+2, ego+2, max-inteval*1);
        curiosity.setWeightAt(ego+2, ego-2, max-inteval*1);
        
//        for(int i = 0; i < nInput; i++)
//        {
//            for(int j = 0; j < nInput; j++)
//            {
//                System.out.print(curiosity.connections[i][j] + " ");
//            }
//            System.out.println("");
//        }
        
    }
    
    public void initCuriosityWeights7x7()
    {
        curiosity.decay = 0.9;
        curiosity.vmax = 0.5;
        curiosity.vmin = 0.2;
        int ego = nInput/2;
        
        double max = 0.15;
        double inteval = 0.05;
        //Close positions
        curiosity.setWeightAt(ego, ego, max);
        curiosity.setWeightAt(ego, ego+1, max);
        curiosity.setWeightAt(ego, ego-1, max);
        curiosity.setWeightAt(ego-1, ego, max);
        curiosity.setWeightAt(ego-1, ego+1, max);
        curiosity.setWeightAt(ego-1, ego-1, max);
        curiosity.setWeightAt(ego+1, ego, max);
        curiosity.setWeightAt(ego+1, ego+1, max);
        curiosity.setWeightAt(ego+1, ego-1, max);
        //Near position
        curiosity.setWeightAt(ego, ego+2, max-inteval*1);
        curiosity.setWeightAt(ego, ego-2, max-inteval*1);
        curiosity.setWeightAt(ego-2, ego, max-inteval*1);
        curiosity.setWeightAt(ego+2, ego, max-inteval*1);
        curiosity.setWeightAt(ego-1, ego+2, max-inteval*1);
        curiosity.setWeightAt(ego-1, ego-2, max-inteval*1);
        curiosity.setWeightAt(ego+1, ego+2, max-inteval*1);
        curiosity.setWeightAt(ego+1, ego-2, max-inteval*1);
        curiosity.setWeightAt(ego-2, ego+1, max-inteval*1);
        curiosity.setWeightAt(ego-2, ego-1, max-inteval*1);
        curiosity.setWeightAt(ego+2, ego+1, max-inteval*1);
        curiosity.setWeightAt(ego+2, ego-1, max-inteval*1);
        curiosity.setWeightAt(ego-2, ego+2, max-inteval*1);
        curiosity.setWeightAt(ego-2, ego-2, max-inteval*1);
        curiosity.setWeightAt(ego+2, ego+2, max-inteval*1);
        curiosity.setWeightAt(ego+2, ego-2, max-inteval*1);
        //Far position
        curiosity.setWeightAt(ego, ego+3, max-inteval*2);
        curiosity.setWeightAt(ego, ego-3, max-inteval*2);
        curiosity.setWeightAt(ego-3, ego, max-inteval*2);
        curiosity.setWeightAt(ego+3, ego, max-inteval*2);
        curiosity.setWeightAt(ego-1, ego+3, max-inteval*2);
        curiosity.setWeightAt(ego-1, ego-3, max-inteval*2);
        curiosity.setWeightAt(ego+1, ego+3, max-inteval*2);
        curiosity.setWeightAt(ego+1, ego-3, max-inteval*2);
        
        curiosity.setWeightAt(ego-2, ego+3, max-inteval*2);
        curiosity.setWeightAt(ego-2, ego-3, max-inteval*2);
        curiosity.setWeightAt(ego+2, ego+3, max-inteval*2);
        curiosity.setWeightAt(ego+2, ego-3, max-inteval*2);
        
        curiosity.setWeightAt(ego-3, ego+3, max-inteval*2);
        curiosity.setWeightAt(ego-3, ego-3, max-inteval*2);
        curiosity.setWeightAt(ego+3, ego+3, max-inteval*2);
        curiosity.setWeightAt(ego+3, ego-3, max-inteval*2);
        
        curiosity.setWeightAt(ego-3, ego+1, max-inteval*2);
        curiosity.setWeightAt(ego-3, ego-1, max-inteval*2);
        curiosity.setWeightAt(ego+3, ego+1, max-inteval*2);
        curiosity.setWeightAt(ego+3, ego-1, max-inteval*2);
        curiosity.setWeightAt(ego-3, ego+2, max-inteval*2);
        curiosity.setWeightAt(ego-3, ego-2, max-inteval*2);
        curiosity.setWeightAt(ego+3, ego+2, max-inteval*2);
        curiosity.setWeightAt(ego+3, ego-2, max-inteval*2);
        
//        for(int i = 0; i < nInput; i++)
//        {
//            for(int j = 0; j < nInput; j++)
//            {
//                System.out.print(curiosity.connections[i][j] + " ");
//            }
//            System.out.println("");
//        }
        
    }

    public boolean[] getAction()
    {
        boolean[] action = new boolean[6];
        double[][] sen = inputObservation;
        double[] input;
        double[] originInput;
        if(useEmotion)
        {
            input = new double[nInput*nInput+10+3];
            originInput = new double[nInput*nInput+10+3];
        }
        else
        {
            input = new double[nInput*nInput+10];
            originInput = new double[nInput*nInput+10];
        }
        int idx = 0;
        
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
        for(int i = 0; i < sen.length; ++i)
        {
            for(int j = 0; j < sen[i].length; ++j)
            {
                originInput[idx] = sen[i][j];
                input[idx] = sen[i][j];
                //System.out.print(sen[i][j] + "\t");
                
//                //Generalized observation.
//                //LevelScene
//                //BreakableBlock = -20
//                //UnbreakableBlock = -22
//                //Brick = -24
//                //FlowerPot = -90
//                //BorderCannotPass - -60
//                //BorderHill = -62
//                //FlowerPotOrCanon = -85
//                //Princess = 5
//                switch(sen[i][j])
//                {
//                    case -22:
//                    case -90:
//                    case -60:
//                    case -85:
//                        input[idx] = -1.0;
//                        break;
//                    case -24:
//                        input[idx] = -0.7;
//                        break;
//                    case -20:
//                        input[idx] = -0.3;
//                        break;
//                    case -62:
//                        input[idx] = -0.1;
//                        break;
//                    //For enemies
//                    case 80:
//                        input[idx] = 1.0;
//                        break;
//                    case 2:
//                        input[idx] = 0.5;
//                        break;
//                    default:
//                        input[idx] = 0;
//                        break;
//                }
                idx++;
            }
            //System.out.println("");
        }
        input[sen.length+sen[0].length] = isJumpable;
        input[sen.length+sen[0].length+1] = isOnGround;
        input[sen.length+sen[0].length+2] = isShootable;
        input[sen.length+sen[0].length+3] = marioMode;
        if(isShiftRight)
        {
            if(sen[3][1] < 0)
                input[sen.length+sen[0].length+4] = 1;
            else
                input[sen.length+sen[0].length+4] = 0;
        }
        else
        {
            if(sen[3][4] < 0)
                input[sen.length+sen[0].length+4] = 1;
            else
                input[sen.length+sen[0].length+4] = 0;
        }
        originInput[sen.length+sen[0].length] = isJumpable;
        originInput[sen.length+sen[0].length+1] = isOnGround;
        originInput[sen.length+sen[0].length+2] = isShootable;
        originInput[sen.length+sen[0].length+3] = marioMode;
        originInput[sen.length+sen[0].length+4] = input[sen.length+sen[0].length+4];
        
        //*****************Information of Enemies*****************
        if(useEnemiesInfo)
        {
            if(enemiesFloatPos.length == 3)
            {
                input[sen.length+sen[0].length+5] = enemiesFloatPos[0]; //Type of 1st enemy
                input[sen.length+sen[0].length+6] = enemiesFloatPos[1]*enemiesFloatPos[1] + enemiesFloatPos[2]*enemiesFloatPos[2]; //Distance
                input[sen.length+sen[0].length+7] = Math.tan(enemiesFloatPos[2]/enemiesFloatPos[1]); //Angle
                if(Double.isNaN(input[sen.length+sen[0].length+7]))
                {
                    input[sen.length+sen[0].length+7] = 0;
                }

                input[sen.length+sen[0].length+8] = 0; //Type of 1st enemy
                input[sen.length+sen[0].length+9] = 0; //Distance
                input[sen.length+sen[0].length+10] = 0; //Angle
            }
            else if(enemiesFloatPos.length > 3)
            {
                //Get 2 nearest enemies
                double len = 10000;
                int idx1 = -1;
                int idx2 = -1;
                for(int i = 0; i < enemiesFloatPos.length; i+=3)
                {
                    if(enemiesFloatPos[i+1] < len)
                    {
                        len = enemiesFloatPos[i+1];
                        idx1 = i;
                    }
                }
                len = 10000;
                for(int i = 0; i < enemiesFloatPos.length; i+=3)
                {
                    if(enemiesFloatPos[i+1] < len && i != idx1);
                    {
                        len = enemiesFloatPos[i+1];
                        idx2 = i;
                    }
                }
                input[sen.length+sen[0].length+5] = enemiesFloatPos[idx1]; //Type of 1st enemy
                input[sen.length+sen[0].length+6] = enemiesFloatPos[idx1+1]*enemiesFloatPos[idx1+1] + enemiesFloatPos[idx1+2]*enemiesFloatPos[idx1+2]; //Distance
                input[sen.length+sen[0].length+7] = Math.tan(enemiesFloatPos[idx1+2]/enemiesFloatPos[idx1+1]); //Angle
                if(Double.isNaN(input[sen.length+sen[0].length+7]))
                {
                    input[sen.length+sen[0].length+7] = 0;
                }

                input[sen.length+sen[0].length+8] = enemiesFloatPos[idx2]; //Type of 1st enemy
                input[sen.length+sen[0].length+9] = enemiesFloatPos[idx2+1]*enemiesFloatPos[idx2+1] + enemiesFloatPos[idx2+2]*enemiesFloatPos[idx2+2]; //Distance
                input[sen.length+sen[0].length+10] = Math.tan(enemiesFloatPos[idx2+2]/enemiesFloatPos[idx2+1]); //Angle
                if(Double.isNaN(input[sen.length+sen[0].length+10]))
                {
                    input[sen.length+sen[0].length+10] = 0;
                }
            }
            else
            {
                input[sen.length+sen[0].length+5] = 0; //Type of 1st enemy
                input[sen.length+sen[0].length+6] = 0; //Distance
                input[sen.length+sen[0].length+7] = 0; //Angle

                input[sen.length+sen[0].length+8] = 0; //Type of 1st enemy
                input[sen.length+sen[0].length+9] = 0; //Distance
                input[sen.length+sen[0].length+10] = 0; //Angle
            }
        }
        else
        {
                input[sen.length+sen[0].length+5] = 0; //Type of 1st enemy
                input[sen.length+sen[0].length+6] = 0; //Distance
                input[sen.length+sen[0].length+7] = 0; //Angle

                input[sen.length+sen[0].length+8] = 0; //Type of 1st enemy
                input[sen.length+sen[0].length+9] = 0; //Distance
                input[sen.length+sen[0].length+10] = 0; //Angle
        }
        
        
        
        //*****************Spike new enemies appear*****************
        //Presence of enemies
        int newE = 0;
        
        numOfPresentEnemies = enemiesFloatPos.length/3;
        if(numOfPresentEnemies != oldNumOffPresenceEnemies)
        {
            if(numOfPresentEnemies > oldNumOffPresenceEnemies)
            {
                //Produce a spike
                newE = 1;
            }
            oldNumOffPresenceEnemies = numOfPresentEnemies;
        }
        
        //Presence of enemies
        int[][] enemyScene = new int[inputEnemiesObservation.length][inputEnemiesObservation.length];
        for(int i = 0; i < inputEnemiesObservation.length; ++i)
        {
            for(int j = 0; j < inputEnemiesObservation[i].length; ++j)
            {
                if(inputEnemiesObservation[i][j] > 0 && inputEnemiesObservation[i][j] != 25)
                    enemyScene[i][j] = 1;
                else
                    enemyScene[i][j] = 0;
            }
        }
        //Presence of coins/blocks
        int[][] coinScene = new int[sen.length][sen.length];
        int[][] blockScene = new int[sen.length][sen.length];
        for(int i = 0; i < sen.length; ++i)
        {
            for(int j = 0; j < sen[i].length; ++j)
            {
                //Coins
                if(sen[i][j] == 2)
                    coinScene[i][j] = 1;
                else
                    coinScene[i][j] = 0;
                //Blocks
                if(sen[i][j] == -24)
                    blockScene[i][j] = 1;
                else
                    blockScene[i][j] = 0;
            }
        }
        
        fear.calcVin(enemyScene);
        
        fear.vout = (fear.vout + fear.vin*0.4)*fear.decay;
        
        fear.calcVin(enemyScene);
        fear.vout = (fear.vout + fear.vin*0.4)*fear.decay;
        double fearSpike = 0;
        if(fear.vout >= fear.vmax)
            fearSpike = 1;
        else if (fear.vout <= fear.vmin)
            fearSpike = 0;
        if(fear.vout < 0)
            fear.vout = 0;        
        if(fearSpike == 1)
        {
            //System.out.println("+.+");
            fear.isExcited = true;
        }
        else
        {
            //System.out.println("-.-");
            fear.isExcited = false;
        }
        
        happy.calcVin(coinScene);
        happy.vout = (happy.vout + happy.vin*0.4)*happy.decay;
        double happySpike = 0;
        if(happy.vout >= happy.vmax)
            happySpike = 1;
        else if (happy.vout <= happy.vmin)
            happySpike = 0;
        if(happy.vout < 0)
            happy.vout = 0;        
        if(happySpike == 1)
        {
            //System.out.println("^.^");
            happy.isExcited = true;
        }
        else
        {
            //System.out.println("-.-");
            happy.isExcited = false;
        }
        
        
        curiosity.calcVin(blockScene);
        curiosity.vout = (curiosity.vout + curiosity.vin*0.4)*curiosity.decay;
        double curiositySpike = 0;
        if(curiosity.vout >= curiosity.vmax)
            curiositySpike = 1;
        else if (curiosity.vout <= curiosity.vmin)
            curiositySpike = 0;
        if(curiosity.vout < 0)
            curiosity.vout = 0;        
        if(curiositySpike == 1)
        {
            //System.out.println("^.^");
            curiosity.isExcited = true;
        }
        else
        {
            //System.out.println("-.-");
            curiosity.isExcited = false;
        }
        
        
        if(useEmotion)
        {
            if(fear.isExcited)
            {
                input[sen.length+sen[0].length+11] = 1;
                originInput[sen.length+sen[0].length+11] = 1;
            }
            else
            {
                input[sen.length+sen[0].length+11] = 0;
                originInput[sen.length+sen[0].length+11] = 0;
            }
            
            if(happy.isExcited)
            {
                input[sen.length+sen[0].length+12] = 1;
                originInput[sen.length+sen[0].length+12] = 1;
            }
            else
            {
                input[sen.length+sen[0].length+12] = 0;
                originInput[sen.length+sen[0].length+12] = 0;
            }
            
            if(curiosity.isExcited)
            {
                input[sen.length+sen[0].length+13] = 1;
                originInput[sen.length+sen[0].length+13] = 1;
            }
            else
            {
                input[sen.length+sen[0].length+13] = 0;
                originInput[sen.length+sen[0].length+13] = 0;
            }
        }
        
//        originInput[sen.length+sen[0].length+4] = fear.vout;
//        originInput[sen.length+sen[0].length+5] = happy.vout;
//        originInput[sen.length+sen[0].length+6] = curiosity.vout;
        //System.out.println("");System.out.println("");
        action = nn.generateAction(input);

        EAction aa = new EAction(Action);
        EAction aolda;
        if(oldAction == null)
        {
            aolda = new EAction();
        }
        else
        {
            aolda = new EAction(oldAction);
        }
        EState as;
        as = new EState(input,aa,tick);

        track.put(as, aa);
        seqStateAction.add(new EStateAction(new EState(as), new EAction(aa)));
        oldState = new EState(as);
        oldAction = new EAction(aa);
        
        //Tracking
        double[] aTrace = new double[2];
        aTrace[0] = marioFloatPos[0]; //x
        aTrace[1] = marioFloatPos[1]; //y
        agentTrace.add(aTrace);
        
        //Detect number of JumpAction
        if(oldIsOnGround == 1 && isOnGround != 1)
        {
            numOfJump++;
        }
        oldIsOnGround = (int)isOnGround;
        //action[4] = false;
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
