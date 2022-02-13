/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Emotions;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Phuc
 */
public class HumanData {
    
    public int numberOfJump = 0;
    public int numberOfRun = 0;
    public int numberOfLeft = 0;
    public int numberOfRight = 0;
    
    public int killedByFire = 0;
    public int killedByStomp = 0;
    public int killedByShell = 0;
    public int totalKilled = 0;
    public int coinGained = 0;
    
    public int timeLeft = 0;
    public int timeSpent = 0;
    public int finishTick = 0;
    
    public String id;
    public List<EStateAction> seqStateAction = new ArrayList<>();
    
    
    public HumanData()
    {
        id = "";
        numberOfJump = 0;
        numberOfRun = 0;
        seqStateAction.clear();
    }
    
    public HumanData(String name, List<EStateAction> seq)
    {
        id = name;
        double oldIsOnGround = -1;
        for(int i = 0; i < seq.size(); ++i)
        {
            seqStateAction.add(seq.get(i));
            if(seq.get(i).s.dataVector[1] != oldIsOnGround)
            {
                numberOfJump++;
            }
            oldIsOnGround = seq.get(i).s.dataVector[1];
            if(seq.get(i).a.action[4])
                numberOfRun++;
            if(seq.get(i).a.action[0])
                numberOfLeft++;
            if(seq.get(i).a.action[1])
                numberOfRight++;
        }
        finishTick = seq.size();
//        public static final int KEY_LEFT = 0;
//        public static final int KEY_RIGHT = 1;
//        public static final int KEY_DOWN = 2;
//        public static final int KEY_JUMP = 3;
//        public static final int KEY_SPEED = 4;
//        public static final int KEY_UP = 5;
    }
    
    @Override
    public String toString()
    {
        return new Gson().toJson(this);
    }
}
