package ch.idsia.agents.EvolutionalNeuralNetwork.Emotions;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Phuc
 */
public class EmotionalPerceptron {
    
    public double vmax = 0;
    public double vmin = 0;
    public double decay = 0;
    
    public double vin = 0;
    public double vout = 0;
    
    public int input = 0;
    
    public double[][] connections;
    
    public boolean isExcited = false;
    
    public EmotionalPerceptron()
    {
        
    }
    
    public void reset()
    {
        vin = 0;
        vout = 0;
        isExcited = false;
    }
    
    public void initializeConnection()
    {
        connections = new double[input][];
        for(int i = 0; i < input; ++i)
        {
            connections[i] = new double[input];
            for(int j = 0; j < input; ++j)
            {
                connections[i][j] = 0;
            }
        }
    }
    
    public void setWeightAt(int i, int j, double val)
    {
        connections[i][j] = val;
    }
    
    public double calcVin(int[][] inputScene)
    {
        double sum = 0;
        for(int i = 0; i < inputScene.length; ++i)
        {
            for(int j = 0; j < inputScene.length; ++j)
            {
                sum += inputScene[i][j]*connections[i][j];
            }
        }
        vin = sum;
        return vin;
    }
    
    public double sigmoid(double x) 
    {
        return (1.0 / ( 1.0 + Math.exp(-1.0*x)));
    }
    
    public double tanh(double x)
    {
        return Math.tanh(x);
    }

    /**
     * @return the input
     */
    public int getInput() {
        return input;
    }

    /**
     * @param input the input to set
     */
    public void setInput(int input) {
        this.input = input;
    }
    
}
