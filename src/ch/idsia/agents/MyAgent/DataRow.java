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
public class DataRow {
    
    double[] data;
    double[] lables;
    
    DataRow(int nInput, int nOutput)
    {
        data = new double[nInput];
        lables = new double[nOutput];
    }
}
