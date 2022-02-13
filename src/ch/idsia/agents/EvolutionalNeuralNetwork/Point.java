/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork;

/**
 *
 * @author Phuc
 */
public class Point {
    
    public double val;
    public int x;
    public int y;
    public double floatX;
    public double floatY;
    
    public Point()
    {
        val = 0;
        x = 0;
        y = 0;
        floatX = 0;
        floatY = 0;
    }
    
    public Point(double vVal, int xVal, int yVal, double floatXVal, double floatYVal)
    {
        val = vVal;
        x = xVal;
        y = yVal;
        floatX = floatXVal;
        floatY = floatYVal;
    }
}
