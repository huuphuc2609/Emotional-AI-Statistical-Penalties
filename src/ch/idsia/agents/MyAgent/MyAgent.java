/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.MyAgent;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Phuc
 */
public class MyAgent extends BasicMarioAIAgent implements Agent
{
    MultilayerNeuralNetwork nn;
    boolean[] oldret;
    List jump;
    public MyAgent()
    {
        super("MyAgent");
        int npInput = 324;
        int npHidden = 20;
        int npOutput = 6;
        double lr = 0.01;
        double th = 0.4;
        
        jump = new ArrayList<>();
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
                    DataRow row = new DataRow(npInput, npOutput);
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
                }
                idx++;
            }
            fileReader.close();
        }
        catch(IOException ex)
        {
            System.out.println(ex.getMessage());
        }
        
        for(int i = 0; i < patterns.size(); ++i)
        {
            for(int j = 0; j < npOutput; ++j)
            {
                patterns.get(i).lables[j] = labels[i][j];
            }
        }
        Collections.shuffle(patterns);
        //Run ANN
        nn = new MultilayerNeuralNetwork(npInput, npHidden, npOutput, 2);
        nn.setLearningRate(lr);
        nn.setThreshold(th);
        nn.train(patterns);
        //reset();
    }

    private Random R = null;

    public void reset()
    {
        // Dummy reset, of course, but meet formalities!
    }

    public boolean[] getAction()
    {
        boolean[] ret;
        oldret = new boolean[6];
        byte[][] sen = mergedObservation;
        double[] input = new double[19*19];
        int idx = 0;
        for(int i = 0; i < sen.length; ++i)
        {
            for(int j = 0; j < sen[i].length; ++j)
            {
                input[idx] = sen[i][j]*1.0;
                idx++;
            }
        }
        ret = nn.test(input);
        if(ret[Mario.KEY_JUMP] == true)
        {
            jump.add(new Object());
        }
        if(jump.size() == 20)
        {
            ret[Mario.KEY_JUMP] = false;
            jump.clear();
        }
        return ret;
    }
}
