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
public class EmotionalAdvancedHumanAgent extends HumanKeyboardAgent {
    
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
    
    public EmotionalAdvancedHumanAgent()
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
        //emotionalChart.setVisible(true);
    }
    
    //This function is called after number of input is set.
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
        initFearWeights();
        initHappyWeights();
        initCuriosityWeights();
    }
    
    public void initFearWeights()
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
    
    public void initHappyWeights()
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
    
    public void initCuriosityWeights()
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
        boolean[] action = new boolean[6];
        double[][] sen = inputObservation;
        double[] input;
        double[] originInput;
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
        for(int i = 0; i < sen.length; ++i)
        {
            for(int j = 0; j < sen[i].length; ++j)
            {
                input[idx] = sen[i][j];
            }
        }
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
        
        
        
//        System.out.println("Enemies info: ");
//        for(int i = 0; i < enemiesFloatPos.length; ++i)
//        {
//            System.out.println(enemiesFloatPos[i] + " ");
//        }
//        System.out.println("Stored info: " + input[9] + " " + input[10] + " " + input[11] + " " + input[12] + " " + input[13] + " " + input[14]);
        
        
        EAction ha = new EAction(Action);
        EState hs;
        hs = new EState(input,enemiesFloatPos,ha,tick);        

        track.put(hs, ha);
        
        //Tracking
        double[] aTrace = new double[2];
        aTrace[0] = marioFloatPos[0]; //x
        aTrace[1] = marioFloatPos[1]; //y
        agentTrace.add(aTrace);
        tick++;
        EStateAction sa = new EStateAction(hs,ha);
        seqStateAction.add(sa);
        
        //Chart
        //emotionalChart.updateXYSeries(fear.vout, tick);
        //emotionalChart.updateXYSeries(happy.vout, tick);
        //emotionalChart.updateXYSeries(curiosity.vout, tick);
        
        return Action;
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
        }
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
