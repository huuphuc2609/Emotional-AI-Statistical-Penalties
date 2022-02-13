/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Emotions;

/**
 *
 * @author Phuc
 */
public class EFeature {
    public EAllActions allAct;
    public int IllegalActions;
    public int ChangeActions;
    
    public int coinGained;
    
    public int totalKilled;
    public int killByStomp;
    public int killByFire;
    public int killByShell;
    
    public int mushroom;
    public int flower;
    
    public int isOnGround;
    
    public int timeSpent; //Max time is 200s
    public int numJump;
    
    public int infrontObj;
    
    public EFeature()
    {
        allAct = new EAllActions();
        IllegalActions = 0;
        ChangeActions = 0;
        coinGained = 0;
        totalKilled = 0;
        killByStomp = 0;
        killByFire = 0;
        killByShell = 0;
        isOnGround = 0;
        timeSpent = 0;
        numJump = 0;
        infrontObj = 0;
    }
    
    public EFeature(EFeature inFea)
    {
        allAct = new EAllActions(inFea.allAct);
        IllegalActions = inFea.IllegalActions;
        ChangeActions = inFea.ChangeActions;
        coinGained = inFea.coinGained;
        totalKilled = inFea.totalKilled;
        killByStomp = inFea.killByStomp;
        killByFire = inFea.killByFire;
        killByShell = inFea.killByShell;
        isOnGround = inFea.isOnGround;
        timeSpent = inFea.timeSpent;
        mushroom = inFea.mushroom;
        flower = inFea.flower;
        numJump = inFea.numJump;
        infrontObj = inFea.infrontObj;
    }
    
    public void resetCounter()
    {
        allAct.resetCounter();
        IllegalActions = 0;
        ChangeActions = 0;
        coinGained = 0;
        totalKilled = 0;
        killByStomp = 0;
        killByFire = 0;
        killByShell = 0;
        isOnGround = 0;
        timeSpent = 0;
        mushroom = 0;
        flower = 0;
        numJump = 0;
        infrontObj = 0;
    }
    
    public void assignData(EFeature inFea)
    {
        allAct.assignData(inFea.allAct);
        IllegalActions = inFea.IllegalActions;
        ChangeActions = inFea.ChangeActions;
        coinGained = inFea.coinGained;
        totalKilled = inFea.totalKilled;
        killByStomp = inFea.killByStomp;
        killByFire = inFea.killByFire;
        killByShell = inFea.killByShell;
        isOnGround = inFea.isOnGround;
        timeSpent = inFea.timeSpent;
        mushroom = inFea.mushroom;
        flower = inFea.flower;
        numJump = inFea.numJump;
        infrontObj = inFea.infrontObj;
    }
    
    public static void printTitle()
    {
        System.out.print("AllLEFT AllRIGHT AllRUN Stand Left LR LD LJ LS LU Right RD RJ RS RU Down DJ DS"
                + " DU Jump JS JU Speed SU Up LRD LRJ LRS LRU LDJ LDS LDU LJS"
                + " LJU LSU RDJ RDS RDU RJS RJU RSU DJS DJU DSU JSU LRDJ LRDS LRDU"
                + " LRJS LRJU LRSU LDJS LDJU LDSU LJSU RDJS RDJU RDSU RJSU DJSU"
                + " LRDJS LRDJU LRDSU LRJSU LDJSU RDJSU LRDJSU NumJump frontObj IllegalAction"
                + " ChangeAction CoinGained TotalKill KillByStomp KillByFire"
                + " KillByShell OnGround TimeSpent");
    }
    
    public void printValues()
    {
        allAct.printValues();
        System.out.print(
            numJump + " " +
            infrontObj + " " +
            IllegalActions + " " +
            ChangeActions + " " +
            coinGained + " " +
            totalKilled + " " +
            killByStomp + " " +
            killByFire + " " +
            killByShell + " " +
            isOnGround + " " +
            timeSpent + " "
        );
    }
    
    public boolean isIllegalAction(EAction inAct)
    {
        int count = 0;
        for(int i = 0; i < inAct.action.length; ++i)
        {
            if(inAct.action[i])
                count++;
        }
        if((inAct.action[0] && inAct.action[1]) ||
                (inAct.action[2] && inAct.action[5]) ||
                (count > 3))
            return true;
        return false;
    }
    
    public void increaseIllegalAction(EAction inAct)
    {
        if(isIllegalAction(inAct))
            this.IllegalActions++;
    }
    
    public boolean equals(EFeature inFea)
    {
        boolean result = false;
        
        if(allAct.equals(inFea.allAct))
        {
            if(IllegalActions == inFea.IllegalActions &&
            ChangeActions == inFea.ChangeActions &&
            coinGained == inFea.coinGained &&
            totalKilled == inFea.totalKilled &&
            killByStomp == inFea.killByStomp &&
            killByFire == inFea.killByFire &&
            killByShell == inFea.killByShell &&
            isOnGround == inFea.isOnGround &&
            timeSpent == inFea.timeSpent)
                result = true;
        }
        
        return result;
    }
}
