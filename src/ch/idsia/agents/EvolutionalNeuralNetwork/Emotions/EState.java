/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Emotions;

import com.google.gson.Gson;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Phuc
 */
public class EState {
    
    public double[] dataVector = null;
    public double[] enemies = null;
    public EAction currentAction = null;
    public int tick = -1;
    
    public EState(EState es)
    {
        dataVector = new double[es.dataVector.length];
        for(int i = 0; i < dataVector.length; ++i)
        {
            dataVector[i] = es.dataVector[i];
        }
        currentAction = new EAction(es.currentAction);
        tick = es.tick;
    }
    
    public EState(double[] data)
    {
        dataVector = new double[data.length];
        for(int i = 0; i < data.length; ++i)
        {
            dataVector[i] = data[i];
        }
    }
    
    public EState(double[] data, EAction curA, int time)
    {
        dataVector = new double[data.length];
        for(int i = 0; i < data.length; ++i)
        {
            dataVector[i] = data[i];
        }
        currentAction = new EAction(curA);
        tick = time;
    }
    
    public EState(double[] data, float[] ene, EAction curA, int time)
    {
        dataVector = new double[data.length];
        enemies = new double[ene.length];
        for(int i = 0; i < ene.length; ++i)
        {
            enemies[i] = ene[i];
        }
        for(int i = 0; i < data.length; ++i)
        {
            dataVector[i] = data[i];
        }
        currentAction = new EAction(curA);
        tick = time;
    }
    
    public EState(double[] data, int time)
    {
        dataVector = new double[data.length];
        for(int i = 0; i < data.length; ++i)
        {
            dataVector[i] = data[i];
        }
        tick = time;
    }
    
    @Override
    public String toString()
    {
        return new Gson().toJson(this);
    }

    @Override
    public int hashCode() {
        //int hash = 7 + 59 * Arrays.hashCode(dataVector) + currentAction.hashCode();
        int hash = 7 + 59 * Arrays.hashCode(dataVector);
//        if(oldDataVector != null)
//            hash += oldDataVector.hashCode();
//        if(oldAction != null)
//            hash += oldAction.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        boolean debug = false;
        if (obj == null) {
            if(debug)
                System.out.println("Null Object");
            return false;
        }
        if (getClass() != obj.getClass()) {
            if(debug)
                System.out.println("Class incompatible");
            return false;
        }
        final EState other = (EState) obj;
        if (!Arrays.equals(this.dataVector, other.dataVector)) {
            if(debug)
                System.out.println("Different data");
            return false;
        }
//        if (!Objects.equals(this.currentAction, other.currentAction)) {
//            if(debug)
//                System.out.println("Different actions");
//            return false;
//        }
//        if(!Objects.equals(this.oldAction, other.oldAction))
//        {
//            if(debug)
//                System.out.println("Different old actions");
//            return false;
//        }
        if(debug)
            System.out.println("Same state!!!");
        return true;
    }
}
