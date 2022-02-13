/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Emotions;

import ch.idsia.agents.EvolutionalNeuralNetwork.Genome;
import java.util.List;

/**
 *
 * @author Phuc
 */
public class EPenaltyStatistical {
    double bestPenStand;
    double bestPenAllLeft;
    double bestPenOnGround;
    double bestPenChangAction;
    double bestPenCoinGained;
    double bestPenTotalKill;
    double besttIllegalAction;
    double bestPenTimeSpent;
    
    double avgPenStand;
    double avgPenAllLeft;
    double avgPenOnGround;
    double avgPenChangAction;
    double avgPenCoinGained;
    double avgPenTotalKill;
    double avgtIllegalAction;
    double avgPenTimeSpent;
    
    double stdPenStand;
    double stdPenAllLeft;
    double stdPenOnGround;
    double stdPenChangAction;
    double stdPenCoinGained;
    double stdPenTotalKill;
    double stdtIllegalAction;
    double stdPenTimeSpent;
    
    public void calcAvg(List<Genome> lisGens)
    {
        for(int i = 0; i < lisGens.size(); ++i)
        {
            avgPenStand += lisGens.get(i).penStand;
            avgPenAllLeft += lisGens.get(i).penALLLEFT;
            avgPenOnGround += lisGens.get(i).penOnGround;
            avgPenChangAction += lisGens.get(i).penChangeAction;
            avgPenCoinGained += lisGens.get(i).penCoinGained;
            avgPenTotalKill += lisGens.get(i).penTotalKill;
            avgtIllegalAction += lisGens.get(i).penIllegalAction;
            avgPenTimeSpent += lisGens.get(i).penTimeSpent;
        }
        
        avgPenStand /= lisGens.size();
        avgPenAllLeft /= lisGens.size();
        avgPenOnGround /= lisGens.size();
        avgPenChangAction /= lisGens.size();
        avgPenCoinGained /= lisGens.size();
        avgPenTotalKill /= lisGens.size();
        avgtIllegalAction /= lisGens.size();
        avgPenTimeSpent /= lisGens.size();
    }
    
    public void calcStd(List<Genome> lisGens)
    {
        double sumPenStand = 0;
        double sumPenAllLeft = 0;
        double sumPenOnGround = 0;
        double sumPenChangAction = 0;
        double sumPenCoinGained = 0;
        double sumPenTotalKill = 0;
        double sumtIllegalAction = 0;
        double sumPenTimeSpent = 0;
        for(int i = 0; i < lisGens.size(); ++i)
        {
            sumPenStand += (lisGens.get(i).penStand - avgPenStand)*(lisGens.get(i).penStand - avgPenStand);
            sumPenAllLeft += (lisGens.get(i).penALLLEFT - avgPenAllLeft)*(lisGens.get(i).penALLLEFT - avgPenAllLeft);
            sumPenOnGround += (lisGens.get(i).penOnGround - avgPenOnGround)*(lisGens.get(i).penOnGround - avgPenOnGround);
            sumPenChangAction += (lisGens.get(i).penChangeAction - avgPenChangAction)*(lisGens.get(i).penChangeAction - avgPenChangAction);
            sumPenCoinGained += (lisGens.get(i).penCoinGained - avgPenCoinGained)*(lisGens.get(i).penCoinGained - avgPenCoinGained);
            sumPenTotalKill += (lisGens.get(i).penTotalKill - avgPenTotalKill)*(lisGens.get(i).penTotalKill - avgPenTotalKill);
            sumtIllegalAction += (lisGens.get(i).penIllegalAction - avgtIllegalAction)*(lisGens.get(i).penIllegalAction - avgtIllegalAction);
            sumPenTimeSpent += (lisGens.get(i).penTimeSpent - avgPenTimeSpent)*(lisGens.get(i).penTimeSpent - avgPenTimeSpent);
        }
        
        stdPenStand = Math.sqrt(sumPenStand/lisGens.size());
        stdPenAllLeft = Math.sqrt(sumPenAllLeft/lisGens.size());
        stdPenOnGround = Math.sqrt(sumPenOnGround/lisGens.size());
        stdPenChangAction = Math.sqrt(sumPenChangAction/lisGens.size());
        stdPenCoinGained = Math.sqrt(sumPenCoinGained/lisGens.size());
        stdPenTotalKill = Math.sqrt(sumPenTotalKill/lisGens.size());
        stdtIllegalAction = Math.sqrt(sumtIllegalAction/lisGens.size());
        stdPenTimeSpent = Math.sqrt(sumPenTimeSpent/lisGens.size());
    }
}
