/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Humanlike;

/**
 *
 * @author Phuc
 */
public class NNPoint {
    public int x;
    public int y;
    public int NodeID;
    public NNPoint(int a, int b, int id)
    {
        x = a;
        y = b;
        NodeID = id;
    }
}
