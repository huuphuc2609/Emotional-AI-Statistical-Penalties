/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Emotions;

import com.google.gson.Gson;

/**
 *
 * @author Phuc
 */
public class EStateAction {
    
    public EState s;
    public EAction a;
    
    public EStateAction(EState sv, EAction av)
    {
        s = sv;
        a = av;
    }
    
    public String toString()
    {
        return new Gson().toJson(this);
    }
    
    public boolean equals(EStateAction esa)
    {        
        if(this.s.equals(esa.s) && this.a.equals(esa.a))
            return true;
        return false;
    }
}
