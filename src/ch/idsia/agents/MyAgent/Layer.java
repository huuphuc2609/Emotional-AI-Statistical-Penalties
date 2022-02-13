/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.MyAgent;

import java.util.Random;

/**
 *
 * @author Phuc
 */
public class Layer 
{
    Perceptron[] unit;
    double bias;
    
    Layer(int m, int n)
    {
        Random generator = new Random();
        unit = new Perceptron[n];
        for(int i = 0; i < unit.length; ++i)
        {
            unit[i] = new Perceptron(m+1);
            for(int j = 0; j < m; ++j)
            {
                unit[i].w[j] = generator.nextDouble() * 2 - 1;
            }
        }
        bias = 1.0;
    }
}
