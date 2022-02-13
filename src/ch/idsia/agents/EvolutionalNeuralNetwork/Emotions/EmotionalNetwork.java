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
public class EmotionalNetwork {
    
    private EmotionalPerceptron happy;
    private EmotionalPerceptron scared;
    private EmotionalPerceptron aggresive;
    private EmotionalPerceptron hurried;
    
    public EmotionalNetwork()
    {
        happy = new EmotionalPerceptron();
        scared = new EmotionalPerceptron();
        aggresive = new EmotionalPerceptron();
        hurried = new EmotionalPerceptron();
    }
    
    //Different code for each game
    public void processingInformation(double[][] obs)
    {
        //Scan the observations
        for(int i = 0; i < obs.length; ++i)
        {
            for(int j = 0; j < obs.length; ++j)
            {
                
            }
        }
        
    }
    
    public double getVoutHappy()
    {
        return happy.vout;
    }
    
    public double getVoutScared()
    {
        return scared.vout;
    }
    
    public double getVoutAggressive()
    {
        return aggresive.vout;
    }
    
    public double getVoutHurried()
    {
        return hurried.vout;
    }
    
    public void setHappy(double minVal, double maxVal, double decayVal)
    {
        happy.vmin = minVal;
        happy.vmax = maxVal;
        happy.decay = decayVal;
    }
    
    public void setScared(double minVal, double maxVal, double decayVal)
    {
        scared.vmin = minVal;
        scared.vmax = maxVal;
        scared.decay = decayVal;
    }
    
    public void setAggressive(double minVal, double maxVal, double decayVal)
    {
        aggresive.vmin = minVal;
        aggresive.vmax = maxVal;
        aggresive.decay = decayVal;
    }
    
    public void setHurried(double minVal, double maxVal, double decayVal)
    {
        hurried.vmin = minVal;
        hurried.vmax = maxVal;
        hurried.decay = decayVal;
    }
}
