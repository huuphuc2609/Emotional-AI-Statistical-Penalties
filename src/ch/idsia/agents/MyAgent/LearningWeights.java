/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.MyAgent;

import ch.idsia.agents.MyAgent.DataRow;
import ch.idsia.agents.MyAgent.MultilayerNeuralNetwork;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Phuc
 */
public class LearningWeights {
    
    public static void main(String[] args)
    {
        int npInput = 324;
        int npHidden = 10;
        int npOutput = 6;
        
        List<DataRow> patterns = new ArrayList<>();
        try
        {
            String filename = "data2.csv";
            FileReader fileReader = new FileReader(new File(filename));

            BufferedReader br = new BufferedReader(fileReader);

            String line = null;
            String[] temp = null;
            // if no more lines the readLine() returns null
            while ((line = br.readLine()) != null) {
                 // reading lines until the end of the file
                temp = line.split(",");
                if(temp.length >= npInput)
                {
                    DataRow row = new DataRow(npInput,npOutput);
                    for(int i = 0; i < npInput; ++i)
                    {
                        row.data[i] = Double.parseDouble(temp[i]);
                    }
                    patterns.add(row);
                }
            }
            fileReader.close();
        }
        catch(IOException ex)
        {
            System.out.println(ex.getMessage());
        }
        Collections.shuffle(patterns);
        
        double[][] labels = new double[patterns.size()][];
        try
        {
            String filename = "lables2.csv";
            FileReader fileReader = new FileReader(new File(filename));

            BufferedReader br = new BufferedReader(fileReader);

            String line = null;
            String[] temp = null;
            int idx = 0;
            // if no more lines the readLine() returns null
            while ((line = br.readLine()) != null) {
                 // reading lines until the end of the file
                temp = line.split(",");
                if(temp.length >= npOutput)
                {
                    labels[idx] = new double[6];
                    for(int i = 0; i < npOutput; ++i)
                    {
                        labels[idx][i] = Double.parseDouble(temp[i]);
                    }
                    idx++;
                }   
            }
            fileReader.close();
        }
        catch(IOException ex)
        {
            System.out.println(ex.getMessage());
        }
        
        
        //Run ANN
        MultilayerNeuralNetwork nn = new MultilayerNeuralNetwork(npInput, npHidden, npOutput, 1);
        nn.setLearningRate(0.1);
        nn.setThreshold(0.02);
        nn.train(patterns);
        nn.test(new double[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,0,0,0,0,0,0,-24,-24,-24,-24,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,-24,-24,0,-24,-24,-24,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-24,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-24,-24,-24,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0,-24,0,0,-24,0,-24,-24,0,80,-60,-60,-60,-60,-60,-24,0,0,0,-24,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,-60,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
    }
    
}
