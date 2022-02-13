/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.QL;

import com.google.gson.Gson;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ikeda-lab
 */
public class QLActionValue {
    
    public static boolean[] fire = {false, false, false, false, true, false};
    public static boolean[] left = {true, false, false, false, false, false};
    public static boolean[] right = {false, true, false, false, false, false};
    public static boolean[] leftSpeed = {true, false, false, false, true, false};
    public static boolean[] rightSpeed = {false, true, false, false, true, false};
    public static boolean[] jump = {false, false, false, true, false, false};
    public static boolean[] leftJump = {true, false, false, true, false, false};
    public static boolean[] rightJump = {false, true, false, true, false, false};
    public static boolean[] leftSpeedJump = {true, false, false, true, true, false};
    public static boolean[] rightSpeedJump = {false, true, false, true, true, false};
    
    public static boolean[][] actions = {fire,left,right,leftSpeed,rightSpeed,jump,
    leftJump,rightJump,leftSpeedJump,rightSpeedJump};
    
    public double[] qvalues = new double[actions.length];
    
    public QLActionValue()
    {
        for(int i = 0; i < qvalues.length; ++i)
        {
            qvalues[i] = 0;
        }
    }
    
    public int getActionIndex(boolean[] a)
    {
        int idx = -1;
        for(int i = 0; i < actions.length; ++i)
        {
            if(actions[i].equals(a))
                idx = i;
        }
        return idx;
    }
    
    @Override
    public String toString()
    {
        return new Gson().toJson(this);
    }
}
