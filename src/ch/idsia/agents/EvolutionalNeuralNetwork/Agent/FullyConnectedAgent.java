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

/**
 *
 * @author Phuc
 */
public class FullyConnectedAgent extends BasicMarioAIAgent implements Agent {

    public EvolNetwork nn;
    public static boolean debug = false;
    protected boolean Action[] = new boolean[Environment.numberOfKeys];
    protected String name = "FullyConnectedAgent";

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
    
    public FullyConnectedAgent() {
        super("FullyConnectedAgent");
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
        
        //Generalized observation.
        for(int i = 0; i < mergedObservation.length; ++i)
        {
            for(int j = 0; j < mergedObservation[i].length; ++j)
            {
                if(mergedObservation[i][j] < 0)
                {
                    mergedObservation[i][j] = -100;
                }
            }
        }
        
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
        double[] input = new double[nInput*nInput+2];
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
        //System.out.println("");System.out.println("");
        action = nn.generateAction(input);
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
