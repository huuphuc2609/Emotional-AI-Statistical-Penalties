/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Emotions;

import ch.idsia.agents.EvolutionalNeuralNetwork.EvolNetwork;
import ch.idsia.agents.controllers.human.HumanKeyboardAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author Phuc
 */
public class EmotionalHumanAgent extends HumanKeyboardAgent {
    
    public EvolNetwork nn;
    private boolean[] Action = null;
    private String Name = "HumanKeyboardAgent";

    private int nInput = 0;
    private int nOutput = 0;
    
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
    
    int tick = 0;
    
    //Emotional part
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
    private double[][] inputObservation;
    private double[][] inputEnemiesObservation;
    
    public boolean isShiftRight;
    public boolean useEnemiesInfo;
    
    public EmotionalHumanAgent()
    {
        this.reset();
    //        RegisterableAgent.registerAgent(this);
        
//        emotionalChart = new DynamicLineAndTimeSeriesChart("Emotion monitor");
//        emotionalChart.setChartName("Fear Emotion");
//        emotionalChart.setxAxisName("Tick");
//        emotionalChart.setyAxisName("vout");
//        emotionalChart.setRangeYAxis(-0.1, 2.0);
//        emotionalChart.setShowTime(1000.0);
//        emotionalChart.initializeChart();
//        emotionalChart.pack();
//        RefineryUtilities.centerFrameOnScreen(emotionalChart);
//        emotionalChart.setVisible(true);
        emotionalChart = new DynamicLineAndTimeSeriesChart("Emotion monitor");
        emotionalChart.setChartName("Happy Emotion");
        emotionalChart.setxAxisName("Tick");
        emotionalChart.setyAxisName("vout");
        emotionalChart.setRangeYAxis(-0.1, 2.0);
        emotionalChart.setShowTime(1000.0);
        emotionalChart.initializeChart();
        emotionalChart.pack();
        RefineryUtilities.centerFrameOnScreen(emotionalChart);
        emotionalChart.setVisible(true);
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
        System.out.println("Coin gained: " + environment.getEvaluationInfo().coinsGained);
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
        initFearWeights7x7();
        initHappyWeights7x7();
        initCuriosityWeights7x7();
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
        
        System.out.println("FEAR");
        for(int i = 0; i < nInput; i++)
        {
            for(int j = 0; j < nInput; j++)
            {
                System.out.print(fear.connections[i][j] + " ");
            }
            System.out.println("");
        }
        
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
        
        System.out.println("HAPPY");
        for(int i = 0; i < nInput; i++)
        {
            for(int j = 0; j < nInput; j++)
            {
                System.out.print(happy.connections[i][j] + " ");
            }
            System.out.println("");
        }
        
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
        
        System.out.println("CURIOSITY");
        for(int i = 0; i < nInput; i++)
        {
            for(int j = 0; j < nInput; j++)
            {
                System.out.print(curiosity.connections[i][j] + " ");
            }
            System.out.println("");
        }
        
    }

    public boolean[] getAction()
    {
        //Collecting data
        byte[][] sen = mergedObservation;
        //double[] input = new double[getnInput()*getnInput()+4+6];
        double[] input = new double[7*7+4+6];
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
        for(int i = 0; i < inputObservation.length; ++i)
        {
            for(int j = 0; j < inputObservation[i].length; ++j)
            {
                if(i != 0 && i != 6 && j != 0 && j != 1)
                {
                //System.out.print(sen[i][j] + "\t");
                    input[idx] = inputObservation[i][j];
                    idx++;
                }
            }
            //System.out.println("");
        }
        
        
        
        
        //*************************Sensor of the advanced Mario*************************
        
        for(int i = 0; i < mergedObservation.length; ++i)
        {
            for(int j = 0; j < mergedObservation.length; ++j)
            {
                System.out.print(mergedObservation[i][j] + " ");
            }
            System.out.println("");
        }
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
        System.out.println("Num cell from the ground: " + numCellFromTheGround);
        
        boolean isGapInfront = true;
        for(int i = 10; i < mergedObservation.length; ++i)
        {
            if(mergedObservation[i][11] != 0 && mergedObservation[i][12] != 0)
            {
                isGapInfront = false;
                break;
            }
        }
        if(isGapInfront)
            System.out.println("Gap in front: TRUE");
        else
            System.out.println("Gap in front: FALSE");
        //Mario information
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
        
        System.out.println("Mario Float pos:");
        for(int i = 0; i < marioFloatPos.length; ++i)
        {
            System.out.print(marioFloatPos[i] + " ");
        }
        System.out.println("");
               
        
        //Enemies information
        System.out.println("Mario: " + marioFloatPos[0] + " " + marioFloatPos[1]);
        System.out.println("Enemies: ");
        for(int i = 0; i < enemiesFloatPos.length; ++i)
        {
            System.out.print(enemiesFloatPos[i] + " ");
        }
        System.out.println("");
        
        //***************Print distance to closest coin***************
        double dvF = 90000;
        for(int i = 0; i < levelScene.length; ++i)
        {
            for(int j = 0; j < levelScene[i].length; ++j)
            {
                if(levelScene[i][j] == 2)
                {
                    System.out.println("M: " + marioEgoRow + " " + marioEgoCol);
                    System.out.println("D to Coin i: " + (i - marioEgoRow) + " j: " + (j - marioEgoCol));
                    double dvX = ((16.0*(Math.abs(i - marioEgoRow))+marioFloatPos[0]) - marioFloatPos[0]);
                    double dvY = ((16.0*(Math.abs(j - marioEgoCol))+marioFloatPos[1]) - marioFloatPos[1]);
                    double dv = Math.sqrt(dvX + dvY);
                    if(dvF > dv)
                        dvF = dv;
                }
            }
        }
        System.out.println("Value=" + dvF + " mario: " + marioFloatPos[0]);
        
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
        
        
        EAction ha = new EAction(Action);
        EAction holda;
        if(oldAction == null)
        {
            holda = new EAction();
        }
        else
        {
            holda = new EAction(oldAction);
        }
        EState hs;
        hs = new EState(input,ha,tick);

        track.put(hs, ha);
        
        oldState = new EState(hs);
        oldAction = new EAction(ha);
        
        
        
        //Spike new enemies appear.
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
        int[][] enemyScene = new int[enemies.length][enemies.length];
        for(int i = 0; i < enemies.length; ++i)
        {
            for(int j = 0; j < enemies[i].length; ++j)
            {
                if(enemies[i][j] > 0 && enemies[i][j] != 25 && i != 0 && i != 6 && j != 0 && j != 1)
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
                if(i != 0 && i != 6 && j != 0 && j != 1)
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
                    //System.out.print(levelScene[i][j] + " ");
                }
                else
                {
                    coinScene[i][j] = 0;
                    blockScene[i][j] = 0;
                }
            }
            //System.out.println("");
        }
        //System.out.println("");
        
        fear.calcVin(enemyScene);
        fear.vout = (fear.vout + fear.vin*0.4)*fear.decay;
//        double fearSpike = 0;
//        if(fear.vout >= fear.vmax)
//            fearSpike = 1;
//        else if (fear.vout <= fear.vmin)
//            fearSpike = 0;
//        if(fear.vout < 0)
//            fear.vout = 0;        
//        if(fearSpike == 1)
//        {
//            //System.out.println("+.+");
//            fear.isExcited = true;
//        }
//        else
//        {
//            //System.out.println("-.-");
//            fear.isExcited = false;
//        }
//        
        happy.calcVin(coinScene);
        happy.vout = (happy.vout + happy.vin*0.4)*happy.decay;
//        double happySpike = 0;
//        if(happy.vout >= happy.vmax)
//            happySpike = 1;
//        else if (happy.vout <= happy.vmin)
//            happySpike = 0;
//        if(happy.vout < 0)
//            happy.vout = 0;        
//        if(happySpike == 1)
//        {
//            //System.out.println("^.^");
//            happy.isExcited = true;
//        }
//        else
//        {
//            //System.out.println("-.-");
//            happy.isExcited = false;
//        }
//        
        curiosity.calcVin(blockScene);
        curiosity.vout = (curiosity.vout + curiosity.vin*0.4)*curiosity.decay;
//        double curiositySpike = 0;
//        if(curiosity.vout >= curiosity.vmax)
//            curiositySpike = 1;
//        else if (curiosity.vout <= curiosity.vmin)
//            curiositySpike = 0;
//        if(curiosity.vout < 0)
//            curiosity.vout = 0;        
//        if(curiositySpike == 1)
//        {
//            //System.out.println("^.^");
//            curiosity.isExcited = true;
//        }
//        else
//        {
//            //System.out.println("-.-");
//            curiosity.isExcited = false;
//        }
        
        //Tracking
        double[] aTrace = new double[2];
        aTrace[0] = marioFloatPos[0]; //x
        aTrace[1] = marioFloatPos[1]; //y
        agentTrace.add(aTrace);
        tick++;
        EStateAction sa = new EStateAction(hs,ha);
        seqStateAction.add(sa);
        
        //Chart
        emotionalChart.updateXYSeries1(fear.vout, tick);
        emotionalChart.updateXYSeries2(happy.vout, tick);
        emotionalChart.updateXYSeries3(curiosity.vout, tick);
        
        return Action;
    }

    public void giveIntermediateReward(float intermediateReward)
    {

    }

    public void reset()
    {
        // Just check you keyboard. Especially arrow buttons and 'A' and 'S'!
        Action = new boolean[Environment.numberOfKeys];
    }

    public void setObservationDetails(final int rfWidth, final int rfHeight, final int egoRow, final int egoCol)
    {}

//    public boolean[] getAction(Environment observation)
//    {
//        //float[] enemiesPos = observation.getEnemiesFloatPos();
//        
//        byte[][] sen = mergedObservation;
//        double[] input = new double[getnInput()*getnInput()+4];
//        int idx = 0;
//        
//        //Get jumbable status
//        double isJumpable = 0;
//        double isOnGround = 0;
//        if(isMarioAbleToJump)
//            isJumpable = 1;
//        if(isMarioOnGround)
//            isOnGround = 1;
//        
//        for(int i = 0; i < sen.length; ++i)
//        {
//            for(int j = 0; j < sen[i].length; ++j)
//            {
//                //System.out.print(sen[i][j] + "\t");
//                input[idx] = sen[i][j];
//                idx++;
//            }
//            //System.out.println("");
//        }
//        input[sen.length+sen[0].length] = isJumpable;
//        input[sen.length+sen[0].length+1] = isOnGround;
//        input[sen.length+sen[0].length+2] = marioFloatPos[0];
//        input[sen.length+sen[0].length+3] = marioFloatPos[1];
//        
//        EState hs;
//        if(oldState.equals(null))
//        {
//             hs = new EState(input, tick);
//        }
//        else
//        {
//            hs = new EState(input,tick,oldState);
//        }
//        EAction ha = new EAction(Action);
//        track.put(hs, ha);
//        
//        oldState = hs;
//        return Action;
//    }

    public String getName() { return Name; }

    public void setName(String name) { Name = name; }


    public void keyPressed(KeyEvent e)
    {
        toggleKey(e.getKeyCode(), true);
    }

    public void keyReleased(KeyEvent e)
    {
        toggleKey(e.getKeyCode(), false);
    }


    private void toggleKey(int keyCode, boolean isPressed)
    {
        switch (keyCode)
        {
            case KeyEvent.VK_LEFT:
                Action[Mario.KEY_LEFT] = isPressed;
                break;
            case KeyEvent.VK_RIGHT:
                Action[Mario.KEY_RIGHT] = isPressed;
                break;
            case KeyEvent.VK_DOWN:
                Action[Mario.KEY_DOWN] = isPressed;
                break;
            case KeyEvent.VK_UP:
                Action[Mario.KEY_UP] = isPressed;
                break;

            case KeyEvent.VK_S:
                Action[Mario.KEY_JUMP] = isPressed;
                break;
            case KeyEvent.VK_A:
                Action[Mario.KEY_SPEED] = isPressed;
                break;
        }
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
