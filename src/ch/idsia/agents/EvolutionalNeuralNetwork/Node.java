/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork;

import java.io.Serializable;

/**
 *
 * @author Phuc
 */
public class Node implements Serializable{
    
    public Layer layer;     //Layer which this node belong to
    public int index;       //Id of this node
    public double a;        //Sum of all inputs
    public double z;        //Output
    public double error;    //Error of this node
    public int inno;        //Innovation number
    
    public Node() {
        layer = Layer.NONE;
        index = -1;
        a = 0;
        z = 0;
        error = 0;
        inno = 0;
    }
    
    public Node(Layer lay, int idx, int in) {
        layer = lay;
        index = idx;
        inno = in;
        a = 0;
        z = 0;
        error = 0;
    }
    
    public Node(Node n)
    {
        layer = n.layer;
        index = n.index;
        inno = n.inno;
        a = n.a;
        z = n.z;
        error = n.error;
    }
    
    public boolean equals(Node n) {
        if(layer.equals(n.layer) 
            && index == n.index)
            return true;
        return false;
    }
    
    public static double sigmoid(double x) 
    {
        return (1.0 / ( 1.0 + Math.exp(-1.0*x)));
    }
    
    public static double tanh(double x)
    {
        return Math.tanh(x);
    }
    
    public static double derivative_sigmoid(double x)
    {
        return sigmoid(x)*(1.0-sigmoid(x));
    }
    
    public static double derivative_tanh(double x)
    {
        return 1-(Math.tanh(x)*Math.tanh(x));
    }
    
    public String getInfo()
    {
        String result = "";
        result = "Layer: " + layer + " Index: " + index + " Inno: " + inno +  " a: " + a + " z: " + z + " error: " + error;
        return result;
    }
}
