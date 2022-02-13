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
public class Connection implements Serializable {
    
    public int inno;
    public int idxFromNode;
    public int idxToNode;
    public Layer fromLayer;
    public Layer toLayer;
    public double w;
    public boolean isDisable;
    
    public Connection() {
        idxFromNode = -2;
        idxToNode = -2;
        w = 0;
        isDisable = false;
        inno = -1;
    }
    
    public Connection(Connection c)
    {
        inno = c.inno;
        fromLayer = c.fromLayer;
        toLayer = c.toLayer;
        idxFromNode = c.idxFromNode;
        idxToNode = c.idxToNode;
        w = c.w;
        isDisable = c.isDisable;
    }
    
    public Connection(Layer from, Layer to, int inIndex, int outIndex, double weight, int innovation) {
        fromLayer = from;
        toLayer = to;
        idxFromNode = inIndex;
        idxToNode = outIndex;
        w = weight;
        isDisable = false;
        inno = innovation;
    }
    
    public void copyInfo(Connection c)
    {
        inno = c.inno;
        fromLayer = c.fromLayer;
        toLayer = c.toLayer;
        idxFromNode = c.idxFromNode;
        idxToNode = c.idxToNode;
        w = c.w;
        isDisable = c.isDisable;
    }
    
    public boolean equals(Connection c) {
        if(idxFromNode == c.idxFromNode
            && idxToNode == c.idxToNode
            && fromLayer.equals(c.fromLayer)
            && toLayer.equals(c.toLayer))
            return true;
        return false;
    }
    
    public String getInfo() 
    {
        String result = "";
        result += "Inno: " + inno;
        result += " From layer: " + fromLayer + " to layer: " + toLayer;
        result += " From node: " + idxFromNode + " to node: " + idxToNode;
        result += " weight: " + w + " IsDisable: " + isDisable;
        return result;
    }
}
