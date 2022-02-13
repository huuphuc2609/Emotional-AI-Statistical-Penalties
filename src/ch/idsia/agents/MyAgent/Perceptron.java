/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.MyAgent;

/**
 *
 * @author Phuc
 */
public class Perceptron {
    public double a; // Sum of input values.
    public double z; // Output value after pass through the activation function.
    public double error; // Error of this perceptron.
    public double[] w; // List of input weights.
    
    // Constructor
    Perceptron(int nInput)
    {
        a = 0;
        z = 0;
        error = 0d;
        w = new double[nInput];
    }
}
