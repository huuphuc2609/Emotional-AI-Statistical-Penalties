/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.QL;

import com.google.gson.Gson;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Phuc
 */
public class QLState {
        
    public double[] s;
    
    public QLState(double[] input)
    {
        s = new double[input.length];
        for(int i = 0; i < input.length; ++i)
        {
            s[i] = input[i];
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Arrays.hashCode(this.s);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QLState other = (QLState) obj;
        if (!Arrays.equals(this.s, other.s)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return new Gson().toJson(this);
    }
}
