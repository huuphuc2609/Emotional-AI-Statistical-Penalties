/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Phuc
 */
public class Species {
    int id;
    int staleness;
    double topFitness;
    Genome topGen;
    List<Genome> listGen;
    
    public Species()
    {
        id = -1;
        staleness = 0;
        topFitness = 0;
        topGen = null;
        listGen = new ArrayList<>();
    }
    
    public Species(int idx)
    {
        id = idx;
        staleness = 0;
        topFitness = 0;
        topGen = null;
        listGen = new ArrayList<>();
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int idx)
    {
        id = idx;
    }
    
    public int getNumberOfGen()
    {
        return listGen.size();
    }
}
