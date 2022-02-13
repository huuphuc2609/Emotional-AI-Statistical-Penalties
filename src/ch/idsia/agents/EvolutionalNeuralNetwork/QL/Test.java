/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.QL;

import ch.idsia.agents.EvolutionalNeuralNetwork.*;
import ch.idsia.agents.controllers.ForwardAgent;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;
import com.google.gson.Gson;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Phuc
 */
public class Test {
       
    public static void main(String[] args) {
        
        //final String argsString = "-fps 1000";
        //final String argsString = "-le off -lb off";
        //final MarioAIOptions marioAIOptions = new MarioAIOptions(argsString);
        final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
      
        final QLearningAgent agent = new QLearningAgent();
        //ForwardAgent agent = new ForwardAgent();
        //NEATAgent.debug = true;
        int fieldSize = 7;
        marioAIOptions.setReceptiveFieldHeight(fieldSize);
        marioAIOptions.setReceptiveFieldWidth(fieldSize);
        agent.setReceptiveFieldSize(fieldSize);
        marioAIOptions.setAgent(agent);
        
        marioAIOptions.setFPS(100);
        //marioAIOptions.setVisualization(true);
        marioAIOptions.setLevelRandSeed(0);
        marioAIOptions.setLevelDifficulty(1);
        
        final BasicTask basicTask = new BasicTask(marioAIOptions);
        marioAIOptions.setVisualization(false);
        
        if(true)
        {
            double oldfitness = 0;
            int loop = 0;
            while(loop < 1000)
            {
                for(int i = 0; i < 1; ++i)
                {
                    marioAIOptions.setLevelDifficulty(i);
                    for(int j = 0; j < 10; ++j)
                    {
                        marioAIOptions.setLevelRandSeed(j);
                        
                        //while(loop < 100 || oldfitness < 9000)

                        basicTask.runSingleEpisode(1);
                        agent.fitness = basicTask.getEvaluationInfo().computeWeightedFitness();
                        //System.out.println(agent.fitness + " " + loop);
//                        if(agent.fitness > 4000)
//                        {
//                            marioAIOptions.setFPS(30);
//                            marioAIOptions.setVisualization(true);
//                            agent.isLearning = true;
//                            marioAIOptions.setLevelRandSeed(0);
//                            basicTask.runSingleEpisode(1);
//                            marioAIOptions.setVisualization(true);
//                            marioAIOptions.setFPS(30);
//                            agent.isLearning = true;
//                        }
                        if(agent.fitness > oldfitness)
                        {
                            oldfitness = agent.fitness;
                            //Write hashmap to file
                            writeQMapToFile(agent.br.map,"QLMAP");
                            //break;
                        }
                        loop++;
                    }
                }
                
            }
        }
        
        marioAIOptions.setVisualization(true);
        marioAIOptions.setFPS(30);
        agent.isLearning = false;
        agent.br.map.clear();
        //Read hashmap from file
        agent.br.map = readQMapFromFile("QLMAP");
        
        for(int i = 0; i < 10; ++i)
        {
            marioAIOptions.setLevelRandSeed(i);    
            basicTask.runSingleEpisode(1);
        }
        
        System.out.println(basicTask.getEvaluationInfo().computeWeightedFitness());
    }
    
    public static HashMap<QLState,QLActionValue> readQMapFromFile(String filename)
    {
        HashMap<QLState,QLActionValue> result = new HashMap<>();
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = br.readLine();
            while(line != null)
            {
                String[] record = line.split("###");
                QLState tmpS = new Gson().fromJson(record[0], QLState.class);
                QLActionValue tmpAV = new Gson().fromJson(record[1], QLActionValue.class);
                result.put(tmpS, tmpAV);
                line = br.readLine();
            }
            br.close();
        }
        catch(IOException ex)
        {
            System.out.println(ex.getMessage());
        }
        
        
        return result;
    }
    
    public static void writeQMapToFile(HashMap<QLState,QLActionValue> map, String filename)
    {
        try
            {
                PrintWriter outfile = new PrintWriter(filename);
                for(QLState qls : map.keySet())
                    {
                        outfile.write(qls.toString() + "###" + map.get(qls).toString()+ "\r\n");
                    }
                outfile.close();
            }
            catch(IOException e)
            {
                System.out.println(e.getMessage());
            }
    }
}
