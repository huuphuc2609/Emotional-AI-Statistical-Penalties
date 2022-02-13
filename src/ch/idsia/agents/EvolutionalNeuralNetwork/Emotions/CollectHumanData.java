/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Emotions;

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.benchmark.tasks.MarioCustomSystemOfValues;
import ch.idsia.tools.MarioAIOptions;
import com.google.gson.Gson;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Phuc
 * Collecting human data
 */
public class CollectHumanData {
    
    public static void main(String[] args) {
        
        //final String argsString = "-fps 1000";
        //final String argsString = "-le off -lb off";
        //final String argsString = "-trace on";
        //final MarioAIOptions marioAIOptions = new MarioAIOptions(argsString);
        final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
        //final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
        final BasicTask basicTask = new BasicTask(marioAIOptions);
        marioAIOptions.setVisualization(true);
        marioAIOptions.setLevelDifficulty(1);
        marioAIOptions.setLevelRandSeed(50);
        marioAIOptions.setFPS(30);
        marioAIOptions.setReceptiveFieldHeight(7);
        marioAIOptions.setReceptiveFieldWidth(7);
        
        EmotionalHumanAgent agent = new EmotionalHumanAgent();
        agent.setnInput(7);
        agent.initializeEmotionUnit();
        marioAIOptions.setAgent(agent);
        
    //        basicTask.reset(marioAIOptions);
        final MarioCustomSystemOfValues m = new MarioCustomSystemOfValues();
    //        basicTask.runSingleEpisode();
        // run 1 episode with same options, each time giving output of Evaluation info.
        // verbose = false
        
        //basicTask.doEpisodes(1, false, 1);
        String name = "HumanTest";
        String currentDir = "";
        try {
            currentDir = new java.io.File( "." ).getCanonicalPath();
        } catch (IOException ex) {
            Logger.getLogger(CollectHumanData.class.getName()).log(Level.SEVERE, null, ex);
        }
        String folder = currentDir + "\\HumanTraceData\\"+name+"\\";
        //Create folder for data
        File theDir = new File(folder);
        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + folder);
            boolean result = false;

            try{
                theDir.mkdirs();
                result = true;
            } 
            catch(SecurityException se){
                System.out.println(se.getMessage());
            }        
            if(result) {    
                System.out.println("DIR created");  
            }
        }
        
        for(int dif = 0; dif < 2; ++dif)
        {
            marioAIOptions.setLevelDifficulty(dif);
            //for(int lev = 0; lev < 61; lev+=15)
            for(int lev = 0; lev < 3; lev+=1)
            {
                for(int times = 0; times < 3; ++times)
                {
                    agent.track.clear();
                    agent.agentTrace.clear();
                    agent.seqStateAction.clear();
                    agent.oldState = null;
                    agent.oldAction = null;
                    marioAIOptions.setRecordFile(folder + "Replay" + name + "_" + dif + "_" + lev + "_" + times);
                    marioAIOptions.setLevelRandSeed(lev);
                    marioAIOptions.setLevelDifficulty(0);
                    marioAIOptions.setLevelRandSeed(50);
                    basicTask.runSingleEpisode(1);
                    //Write result to txt file
                    String eval = basicTask.getEnvironment().getEvaluationInfoAsString();
                    try
                    {
                        FileWriter fw = new FileWriter(folder + "Result" + name + "_" + dif + "_" + lev + "_" + times);
                        fw.write(eval);
                        fw.flush();
                        fw.close();
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }

                    //Write trace
                    String trackContent = "";
                    try
                    {
                        FileWriter fw = new FileWriter(folder+"trace" + name + "_" + dif + "_" + lev + "_" + times + ".txt");
                        for(int i = 0; i < agent.agentTrace.size(); ++i)
                        {
                            //System.out.println(agent.agentTrace.get(i)[0] + " " + (320 - agent.agentTrace.get(i)[1]));
                            trackContent += agent.agentTrace.get(i)[0] + " " + agent.agentTrace.get(i)[1];
                            trackContent += "\r\n";
                        }
                        fw.write(trackContent);
                        fw.flush();
                        fw.close();
                    }
                    catch(IOException ex)
                    {
                        System.out.println(ex.getMessage());
                    }

                    //Write track
                    try
                    {
                        PrintWriter outfile = new PrintWriter(folder+"hashmapHumanPlay"+ name + "_"+ dif + "_" + lev + "_" + times);
                        HashMap<EState,EAction> hmout = (HashMap<EState,EAction>) agent.track.clone();
                        System.out.println("Track hashmap: " + agent.track.size());
                        //System.out.println(hmout.size());
                        Thread.sleep(100);
                        for(EState es : hmout.keySet())
                        {
                            outfile.write(es.toString() + "###" + hmout.get(es).toString()+ "\r\n");
                        }
                        Thread.sleep(100);
                        outfile.flush();
                        outfile.close();   
                    }
                    catch(IOException | InterruptedException ex)
                    {
                        System.out.println(ex.getMessage());
                    }

                    //Write sequence of actions
                    try
                    {
                        PrintWriter outfile = new PrintWriter(folder+"seqActionHumanPlay"+ name + "_"+ dif + "_" + lev + "_" + times);
                        System.out.println("Seq action: " + agent.seqStateAction.size());
                        Thread.sleep(100);
                        for(EStateAction ea : agent.seqStateAction)
                        {
                            outfile.write(ea.toString() + "\r\n");
                        }
                        Thread.sleep(100);
                        outfile.flush();
                        outfile.close();
                    } catch (FileNotFoundException | InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        }
        System.exit(0);
    }
    
}
