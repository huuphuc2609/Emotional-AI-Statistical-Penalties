/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Phuc
 */
public class MarioAIANNInput {
    
    boolean isJumpable;
    boolean isOnGround;
    boolean isShootable;
    boolean isGapInfront;
    
    private int marioMode;
    private int posAtOneSecondBefore;
    private int numCellFromTheGround;
    private int numberOfInput;
    
    double MarioFloatX;
    double MarioFloatY;
    
    double E1_Type;
    double E1_Distance;
    double E1_Angle;
    
    double E2_Type;
    double E2_Distance;
    double E2_Angle;
    
    double distanceToTheNearestItemsX;
    double distanceToTheNearestItemsY;
    
    double distanceToTheNearestCoinX;
    double distanceToTheNearestCoinY;
    
    int[] fourCellsInFront = new int[4];
    
    //New input features (number of actions)
    int[] previousNumActs = new int[6];
    
    
    
    //Init object
    public MarioAIANNInput()
    {
        isJumpable = false;
        isOnGround = false;
        isShootable = false;
        isGapInfront = false;
        marioMode = 0;
        numberOfInput = 0;
        posAtOneSecondBefore = 0;
        numCellFromTheGround = 0;
        
        MarioFloatX = 0;
        MarioFloatY = 0;
        E1_Type = 0;
        E1_Distance = 0;
        E1_Angle = 0;
        E2_Type = 0;
        E2_Distance = 0;
        E2_Angle = 0;
        
        distanceToTheNearestItemsX = 0;
        distanceToTheNearestItemsY = 0;
        
        distanceToTheNearestCoinX = 0;
        distanceToTheNearestCoinY = 0;
        
        for(int i = 0; i < previousNumActs.length; ++i)
        {
            previousNumActs[i] = 0;
        }
    }
    
    //Get input as a double array
    public double[] getInputAsDoubleArray()
    {
        double[] result = new double[getNumberOfInput()];
        
        if(isJumpable)
            result[0] = 1;
        if(isOnGround)
            result[1] = 1;
        if(isShootable)
            result[2] = 1;
        result[3] = getMarioMode();
        
        if(fourCellsInFront[0] < 0 ||
                fourCellsInFront[1] < 0 ||
                fourCellsInFront[2] < 0 ||
                fourCellsInFront[3] < 0)
        {
            result[4] = 1;
        }
        else
        {
            result[4] = 0;
        }
        
        result[5] = getNumCellFromTheGround();
        
        if(isGapInfront)
            result[6] = 1;
        else
            result[6] = 0;
        
        result[7] = MarioFloatX;
        result[8] = MarioFloatY;
        
        result[9] = E1_Type; //Type of 1st enemy
        result[10] = E1_Distance; //Distance
        result[11] = E1_Angle; //Angle

        result[12] = E2_Type; //Type of 1st enemy
        result[13] = E2_Distance; //Distance
        result[14] = E2_Angle; //Angle
        
        result[15] = getPosAtOneSecondBefore();
        
        result[7] = Math.round(result[7]);
        result[8] = Math.round(result[8]);
        result[9] = Math.round(result[9]);
        result[10] = Math.round(result[10]);
        result[11] = Math.round(result[11]);
        result[12] = Math.round(result[12]);
        result[13] = Math.round(result[13]);
        result[14] = Math.round(result[14]);
        //input[15] = posAtOneSecondBefore;
        result[15] = Math.round(posAtOneSecondBefore);
        
        result[16] = previousNumActs[0];
        result[17] = previousNumActs[1];
        result[18] = previousNumActs[2];
        result[19] = previousNumActs[3];
        result[20] = previousNumActs[4];
        result[21] = previousNumActs[5];
        
        result[22] = distanceToTheNearestItemsX;
        result[23] = distanceToTheNearestItemsY;
        
        result[24] = distanceToTheNearestCoinX;
        result[25] = distanceToTheNearestCoinY;
        
        return result;
    }
    
    //GET_SET methods
    public void set4CellsInFront(byte[][] mergedObservation)
    {
        fourCellsInFront[0] = mergedObservation[8][10];
        fourCellsInFront[1] = mergedObservation[9][10];
        fourCellsInFront[2] = mergedObservation[8][11];
        fourCellsInFront[3] = mergedObservation[9][11];
    }
    
    public void setMarioFloatPos(float[] marioFloatPos)
    {
        MarioFloatX = marioFloatPos[0];
        MarioFloatY = marioFloatPos[1];
    }
    
    public void setEnemiesFloatPos(float[] enemiesFloatPos)
    {
        if(enemiesFloatPos.length == 3)
        {
            E1_Type = enemiesFloatPos[0]; //Type of 1st enemy
            E1_Distance = Math.sqrt(enemiesFloatPos[1]*enemiesFloatPos[1] + enemiesFloatPos[2]*enemiesFloatPos[2]); //Distance
            E1_Angle = Math.tan(enemiesFloatPos[2]/enemiesFloatPos[1]); //Angle
            if(Double.isNaN(E1_Angle))
            {
                E1_Angle = 0;
            }
            E2_Angle = 0; //Type of 2nd enemy
            E2_Distance = 0; //Distance
            E2_Angle = 0; //Angle
            //averageDistClosestEnemy+=input[10];
        }
        else if(enemiesFloatPos.length > 3)
        {
            //Get 2 nearest enemies
            double len = 10000;
            int idx1 = -1;
            int idx2 = -1;
            for(int i = 0; i < enemiesFloatPos.length; i+=3)
            {
                if(Math.abs(enemiesFloatPos[i+1] - MarioFloatX) < len)
                {
                    len = enemiesFloatPos[i+1];
                    idx1 = i;
                }
            }
            len = 10000;
            for(int i = 0; i < enemiesFloatPos.length; i+=3)
            {
                if(Math.abs(enemiesFloatPos[i+1] - MarioFloatX) < len && i != idx1);
                {
                    len = enemiesFloatPos[i+1];
                    idx2 = i;
                }
            }
            E1_Type = enemiesFloatPos[idx1]; //Type of 1st enemy
            E1_Distance = Math.sqrt(enemiesFloatPos[idx1+1]*enemiesFloatPos[idx1+1] + enemiesFloatPos[idx1+2]*enemiesFloatPos[idx1+2]); //Distance
            E1_Angle = Math.tan(enemiesFloatPos[idx1+2]/enemiesFloatPos[idx1+1]); //Angle
            if(Double.isNaN(E1_Angle))
            {
                E1_Angle = 0;
            }

            E2_Type = enemiesFloatPos[idx2]; //Type of 2nd enemy
            E2_Distance = Math.sqrt(enemiesFloatPos[idx2+1]*enemiesFloatPos[idx2+1] + enemiesFloatPos[idx2+2]*enemiesFloatPos[idx2+2]); //Distance
            E2_Angle = Math.tan(enemiesFloatPos[idx2+2]/enemiesFloatPos[idx2+1]); //Angle
            if(Double.isNaN(E2_Angle))
            {
                E2_Angle = 0;
            }
            
            //averageDistClosestEnemy+=input[10];
        }
        else
        {
            E1_Type = 0; //Type of 1st enemy
            E1_Distance = 0; //Distance
            E1_Angle = 0; //Angle

            E2_Type = 0; //Type of 2nd enemy
            E2_Distance = 0; //Distance
            E2_Angle = 0; //Angle
        }
    }
    
    public void setNumActs(int[] inPreviousNumActs) {
        previousNumActs[1] = inPreviousNumActs[1];
        previousNumActs[2] = inPreviousNumActs[2];
        previousNumActs[3] = inPreviousNumActs[3];
        previousNumActs[4] = inPreviousNumActs[4];
        previousNumActs[5] = inPreviousNumActs[5];
    }
    
    public void setDistanceToNearestItem(byte[][] enemiesScene)
    {
        List<Integer> listX = new ArrayList<>();
        List<Integer> listY = new ArrayList<>();
        List<Double> distances = new ArrayList<>();
        for(int i = 0; i < enemiesScene.length; ++i)
        {
            for(int j = 0; j < enemiesScene[i].length; ++j)
            {
                if(enemiesScene[i][j] == 2 || enemiesScene[i][j] == 3)
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
    }
    
    public void setDistanceToNearestCoin(byte[][] levelScene)
    {
        List<Integer> listX = new ArrayList<>();
        List<Integer> listY = new ArrayList<>();
        List<Double> distances = new ArrayList<>();
        for(int i = 0; i < levelScene.length; ++i)
        {
            for(int j = 0; j < levelScene[i].length; ++j)
            {
                if(levelScene[i][j] == 2)
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
            distanceToTheNearestCoinX = 0;
            distanceToTheNearestCoinY = 0;
        }
        else
        {
            int min = 0;
            for(int i = 1; i < distances.size(); ++i)
            {
                if(distances.get(min) > distances.get(i))
                    min = i;
            }
            distanceToTheNearestCoinX = listX.get(min);
            distanceToTheNearestCoinY = listY.get(min);
        }
        listX.clear();
        listY.clear();
        distances.clear();
    }
    
    public void setJumpable(boolean inVal)
    {
        isJumpable = inVal;
    }
    public boolean getJumable()
    {
        return isJumpable;
    }
    public void setIsOnGround(boolean inVal)
    {
        isOnGround = inVal;
    }
    public boolean getIsOnGround()
    {
        return isOnGround;
    }
    public void setIsShootable(boolean inVal)
    {
        isShootable = inVal;
    }
    public boolean getIsShootable()
    {
        return isShootable;
    }
    public void setIsGapInfront(boolean inVal)
    {
        isShootable = inVal;
    }
    public boolean getIsGapInfront()
    {
        return isGapInfront;
    }
    public int getMarioMode() {
        return marioMode;
    }
    public void setMarioMode(int marioMode) {
        this.marioMode = marioMode;
    }
    public int getPosAtOneSecondBefore() {
        return posAtOneSecondBefore;
    }
    public void setPosAtOneSecondBefore(int posAtOneSecondBefore) {
        this.posAtOneSecondBefore = posAtOneSecondBefore;
    }
    public int getNumCellFromTheGround() {
        return numCellFromTheGround;
    }
    public void setNumCellFromTheGround(int numCellFromTheGround) {
        this.numCellFromTheGround = numCellFromTheGround;
    }
    public int getNumberOfInput() {
        return numberOfInput;
    }
    public void setNumberOfInput(int numberOfInput) {
        this.numberOfInput = numberOfInput;
    }
    
    @Override
    public String toString()
    {
        return new Gson().toJson(this);
    }
}
