/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.QL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Phuc
 */
public class Brain {
    
    public double lr = 0.05;
    public double discount = 0.95;
    public HashMap<QLState,QLActionValue> map = new HashMap<>();
    
    public Brain()
    {
        lr = 0.05;
        discount = 0.04;
        lr = 0.2;
        discount = 10;
    }
    
    public void recordState(double[] s)
    {
        QLState state = new QLState(s);
        QLActionValue qav = new QLActionValue();
        
        if(!map.containsKey(state))
        {
            map.put(state,qav);
        }
        
    }
    
    public boolean[] getAnAction(QLState s)
    {
        boolean[] result;
        QLActionValue qav = map.get(s);
        
        if(qav != null)
        {
            double maxValue = -10000;
            int idxMax = -1;
            for(int i = 0; i < qav.qvalues.length; ++i)
            {
                if(qav.qvalues[i] > maxValue)
                {
                    idxMax = i;
                    maxValue = qav.qvalues[i];
                }
            }

            result = qav.actions[idxMax];
        }
        else
        {
            result = getRandomAction();
        }
        return result;
    }
    
    public boolean[] getRandomAction()
    {
        Random r = new Random();
        QLActionValue qav = (QLActionValue)map.values().toArray()[r.nextInt(map.size())];
        boolean[] result = qav.actions[2];
        return result;
    }
    
    public void update(QLState oldS, QLState inS, double reward, boolean[] oldA, boolean[] anA)
    {
        QLActionValue oldQ = map.get(oldS);
        QLActionValue inQ = map.get(inS);
        
        oldQ.qvalues[oldQ.getActionIndex(oldA)] += lr *(reward - discount + inQ.qvalues[inQ.getActionIndex(anA)] - oldQ.qvalues[oldQ.getActionIndex(oldA)]);
        map.put(oldS,oldQ);
    }
    
}
