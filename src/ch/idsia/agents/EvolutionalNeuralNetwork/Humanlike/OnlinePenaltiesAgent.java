/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Humanlike;

import ch.idsia.agents.Agent;
import ch.idsia.agents.EvolutionalNeuralNetwork.Connection;
import ch.idsia.agents.EvolutionalNeuralNetwork.CustomNetwork;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.DynamicLineAndTimeSeriesChart;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EAction;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EState;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EStateAction;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EmotionalPerceptron;
import ch.idsia.agents.EvolutionalNeuralNetwork.Genome;
import ch.idsia.agents.EvolutionalNeuralNetwork.Layer;
import ch.idsia.agents.EvolutionalNeuralNetwork.Node;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author Phuc
 */
public class OnlinePenaltiesAgent extends BasicMarioAIAgent implements Agent {

    public CustomNetwork nn;
    public static boolean debug = false;
    protected boolean Action[] = new boolean[Environment.numberOfKeys];
    protected String name = "OnlinePenaltiesAgent";
    
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
    
    //Emotional part
    public boolean useEmotion = false;
    public boolean isShiftRight = false;
    public boolean useEnemiesInfo = false;
    public boolean isTraining = false;
    
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
    
    
    EAction prevAct = null;
    
    public double averageDistClosestEnemy = 0;
    
    int oldIsOnGround = 0;
    
    double instantReward = 0;    
    
    Genome oldGen;
    Genome currentGen;
    
    public void resetInformation()
    {
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
        tick = 0;
        LeftRight = 0;
        IllegalAction = 0;
    }
    
    
    public OnlinePenaltiesAgent() {
        super("PenaltiesAgent");
        
        emotionalChart = new DynamicLineAndTimeSeriesChart("Emotion monitor");
        emotionalChart.setChartName("Fear Emotion");
        emotionalChart.setxAxisName("Tick");
        emotionalChart.setyAxisName("vout");
        emotionalChart.setRangeYAxis(-0.1, 2.0);
        emotionalChart.setShowTime(1000.0);
        emotionalChart.initializeChart();
        emotionalChart.pack();
        RefineryUtilities.centerFrameOnScreen(emotionalChart);
        emotionalChart.setVisible(false);
        
        currentGen = new Genome();
    }
    
    public void setEmoChart(boolean flag)
    {
        emotionalChart.setVisible(flag);
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
        
        //instantReward = environment.getEvaluationInfo().computeWeightedFitness();
        instantReward = environment.getEvaluationInfo().distancePassedPhys;
        //System.out.println(instantReward);
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
        
        //Init genome
        currentGen.identity = 0;
        System.out.println(nn.getnInput());
        for(int j = 0; j < nn.getnInput(); ++j)
        {
            currentGen.newNode(Layer.INPUT, j, -1);
        }
        for(int j = 0; j < nn.getnOutput(); ++j)
        {
            currentGen.newNode(Layer.OUTPUT, j, -1);
        }
        currentGen.eta = 0.1;       
//        oldGen = new Genome(currentGen);
        nn.activatedGen = currentGen;
        
        //FullyInit
        for(int i = 0; i < 5; ++i)
        {
            Node newNode = new Node();
            newNode.layer = Layer.HIDDEN;
            newNode.index = i;
            currentGen.listHiddenNodes.add(newNode);
        }
        
        Random r = new Random();
        for(int i = 0; i < nn.getnInput(); ++i)
        {
            for(int j = 0; j < 5; ++j)
            {
                Connection c = new Connection(Layer.INPUT, Layer.HIDDEN, i, j, r.nextDouble(), -1);
                currentGen.listConnections.add(c);
            }
        }
        
        for(int i = 0; i < 5; ++i)
        {
            for(int j = 0; j < nn.getnOutput(); ++j)
            {
                Connection c = new Connection(Layer.HIDDEN, Layer.OUTPUT, i, j, r.nextDouble(), -1);
                currentGen.listConnections.add(c);
            }
        }
        //Bias connection
        for(int j = 0; j < nn.getnOutput(); ++j)
        {
            Connection bc = new Connection(Layer.HIDDEN, Layer.OUTPUT, -1, j, r.nextDouble(), -1);
            currentGen.listBiasConnections.add(bc);
        }
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
            input = new double[18];
        else
            input = new double[15];
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
//        for(int i = 0; i < sen.length; ++i)
//        {
//            for(int j = 0; j < sen[i].length; ++j)
//            {
//                input[idx] = sen[i][j];
//            }
//        }
        //Mario status
        input[0] = isJumpable;
        input[1] = isOnGround;
        input[2] = isShootable;
        input[3] = marioMode;
        
        //Front obstacles
        if(mergedObservation[8][10] < 0 ||
                mergedObservation[9][10] < 0 ||
                mergedObservation[8][11] < 0 ||
                mergedObservation[9][11] < 0)
        {
            input[4] = 1;
        }
        else
        {
            input[4] = 0;
        }
        
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
        input[5] = numCellFromTheGround;
        
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
        if(isGapInfront)
            input[6] = 1;
        else
            input[6] = 0;
        
        //Mario float pos
        input[7] = marioFloatPos[0];
        input[8] = marioFloatPos[1];
        
        if(enemiesFloatPos.length == 3)
        {
            input[9] = enemiesFloatPos[0]; //Type of 1st enemy
            input[10] = Math.sqrt(enemiesFloatPos[1]*enemiesFloatPos[1] + enemiesFloatPos[2]*enemiesFloatPos[2]); //Distance
            input[11] = Math.tan(enemiesFloatPos[2]/enemiesFloatPos[1]); //Angle
            if(Double.isNaN(input[11]))
            {
                input[11] = 0;
            }
            input[12] = 0; //Type of 1st enemy
            input[13] = 0; //Distance
            input[14] = 0; //Angle
            averageDistClosestEnemy+=input[10];
        }
        else if(enemiesFloatPos.length > 3)
        {
            //Get 2 nearest enemies
            double len = 10000;
            int idx1 = -1;
            int idx2 = -1;
            for(int i = 0; i < enemiesFloatPos.length; i+=3)
            {
                if(Math.abs(enemiesFloatPos[i+1] - marioFloatPos[0]) < len)
                {
                    len = enemiesFloatPos[i+1];
                    idx1 = i;
                }
            }
            len = 10000;
            for(int i = 0; i < enemiesFloatPos.length; i+=3)
            {
                if(Math.abs(enemiesFloatPos[i+1] - marioFloatPos[0]) < len && i != idx1);
                {
                    len = enemiesFloatPos[i+1];
                    idx2 = i;
                }
            }
            input[9] = enemiesFloatPos[idx1]; //Type of 1st enemy
            input[10] = Math.sqrt(enemiesFloatPos[idx1+1]*enemiesFloatPos[idx1+1] + enemiesFloatPos[idx1+2]*enemiesFloatPos[idx1+2]); //Distance
            input[11] = Math.tan(enemiesFloatPos[idx1+2]/enemiesFloatPos[idx1+1]); //Angle
            if(Double.isNaN(input[11]))
            {
                input[11] = 0;
            }

            input[12] = enemiesFloatPos[idx2]; //Type of 1st enemy
            input[13] = Math.sqrt(enemiesFloatPos[idx2+1]*enemiesFloatPos[idx2+1] + enemiesFloatPos[idx2+2]*enemiesFloatPos[idx2+2]); //Distance
            input[14] = Math.tan(enemiesFloatPos[idx2+2]/enemiesFloatPos[idx2+1]); //Angle
            if(Double.isNaN(input[14]))
            {
                input[14] = 0;
            }
            
            averageDistClosestEnemy+=input[10];
        }
        else
        {
            input[9] = 0; //Type of 1st enemy
            input[10] = 0; //Distance
            input[11] = 0; //Angle

            input[12] = 0; //Type of 1st enemy
            input[13] = 0; //Distance
            input[14] = 0; //Angle
        }
        
        
        
        //*****************Spike new enemies appear*****************
//        Presence of enemies
//        int newE = 0;
//        
//        numOfPresentEnemies = enemiesFloatPos.length/3;
//        if(numOfPresentEnemies != oldNumOffPresenceEnemies)
//        {
//            if(numOfPresentEnemies > oldNumOffPresenceEnemies)
//            {
//                //Produce a spike
//                newE = 1;
//            }
//            oldNumOffPresenceEnemies = numOfPresentEnemies;
//        }
        
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
        
        //*****************EMOTIONS*********************
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
            input[15] = fear.vout; //FEAR
            input[16] = happy.vout; //HAPPY
            input[17] = curiosity.vout; //CURIOSITY
        }
        
        
//        if(useEmotion)
//        {
//            if(fear.isExcited)
//            {
//                input[15] = 1;
//            }
//            else
//            {
//                input[15] = 0;
//            }
//            
//            if(happy.isExcited)
//            {
//                input[16] = 1;
//            }
//            else
//            {
//                input[16] = 0;
//            }
//            
//            if(curiosity.isExcited)
//            {
//                input[17] = 1;
//            }
//            else
//            {
//                input[17] = 0;
//            }
//        }
        
        if(!isTraining)
        {
            emotionalChart.updateXYSeries1(fear.vout, tick);
            emotionalChart.updateXYSeries2(happy.vout, tick);
            emotionalChart.updateXYSeries3(curiosity.vout, tick);
        }
        //**************END EMOTIONS***********************
        
        //System.out.println(currentGen.listConnections.size());
//        currentGen.fitness = instantReward;
//        //System.out.println(currentGen.fitness);
//       
//        if(currentGen.fitness < oldGen.fitness)
//        {
//            Genome tmp = new Genome(oldGen);
//            currentGen = tmp;
//            nn.mutationWithRemove(currentGen);
//            nn.mutateChangeNormalConnectionWeight(currentGen);
//            nn.activatedGen = currentGen;
//        }
//            
//        oldGen = new Genome(currentGen);
        
        currentGen.newBPP(instantReward);
        currentGen.newUpdateWeightBackprobagation();
        System.out.println("Weight: " + currentGen.listConnections.get(0).w +
                " Error: " + currentGen.listOutputNodes.get(0).error +
                " Reward: " + instantReward);
        action = nn.generateAction(input);
        //action[2] = false;
        //action[5] = false;
        
//        if(action[1] && action[4])
//        {
//            action[1] = false;
//            action[4] = false;
//        }
        
        EAction aa = new EAction(action);
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
        //as = new EState(input,aa,tick);
        as = new EState(input,aa,0);
       
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
        
//        if(action[0])
//            LeftButton++;
//        if(action[1])
//            RightButton++;
//        if(action[3])
//            JumpButton++;
//        if(action[4])
//            RunButton++;
//        
//        if(action[0] && action[3])
//            LJ++;
//        if(action[1] && action[3])
//            RJ++;
//        if(action[0] && action[4])
//            LR++;
//        if(action[1] && action[4])
//            RR++;
//        if(action[0] && action[3] && action[4])
//            LJR++;
//        if(action[1] && action[3] && action[4])
//            RJR++;
        
        EAction act = new EAction(aa);
        if(act.equals(new EAction("stand")))
            Stand++;
        else if(act.equals(new EAction("right")))
            RightButton++;
        else if(act.equals(new EAction("left")))
            LeftButton++;
        else if(act.equals(new EAction("run")))
            RunButton++;
        else if(act.equals(new EAction("jump")))
            JumpButton++;
        else if(act.equals(new EAction("leftJump")))
            LJ++;
        else if(act.equals(new EAction("rightJump")))
            RJ++;
        else if(act.equals(new EAction("leftSpeed")))
            LS++;
        else if(act.equals(new EAction("rightSpeed")))
            RS++;
        else if(act.equals(new EAction("leftSpeedJump")))
            LJS++;
        else if(act.equals(new EAction("rightSpeedJump")))
            RJS++;
        
        if(isMarioOnGround)
            OnGround++;
        
        if(act.action[0] && act.action[1])
        {
            LeftRight++;
            IllegalAction++;
        }
        if(act.action[2] && act.action[5])
        {
            UpDown++;
            IllegalAction++;
        }
        int countTrue = 0;
        for(int i = 0; i < act.action.length; ++i)
        {
            if(act.action[i])
                countTrue++;
        }
        if(countTrue > 3)
        {
            IllegalAction++;
        }
        
        //System.out.println(RunButton);
        
        if(prevAct != null)
        {
            if(!prevAct.equals(aa))
            {
                changeActions++;
            }
        }
        prevAct = aa;
        
        //action[4] = false;
        //emotionalChart.updateXYSeries(fear.vout, tick);
        //tick++;
        //System.out.println(fear.vout);
        
        if(enemies[marioEgoRow-1][marioEgoCol-1] != 0
            || enemies[marioEgoRow-1][marioEgoCol] != 0
            || enemies[marioEgoRow-1][marioEgoCol+1] != 0
            || enemies[marioEgoRow][marioEgoCol-1] != 0
            || enemies[marioEgoRow][marioEgoCol+1] != 0
            || enemies[marioEgoRow+1][marioEgoCol-1] != 0
            || enemies[marioEgoRow+1][marioEgoCol] != 0
            || enemies[marioEgoRow+1][marioEgoCol+1] != 0)
                encounterEnemies++;
        
        tick++;

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
