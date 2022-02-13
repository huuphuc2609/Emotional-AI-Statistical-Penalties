/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork;

import ch.idsia.agents.EvolutionalNeuralNetwork.Agent.CustomEvolAgent;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author Phuc
 */
public class CustomTraining {
    public static void main(String[] args) {
        
        Random rand = new Random();
        
        for(int i = 0; i < 10; i++)
        {
            System.out.println(rand.nextGaussian());
        }
            
        
        
        //final String argsString = "-fps 1000";
        final String argsString = "-le off";
        final MarioAIOptions marioAIOptions = new MarioAIOptions(argsString);
        //final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
        marioAIOptions.setLevelRandSeed(5);
        double overallFitness = 0;
        double bestFitness = 0;
        CustomNetwork net = new CustomNetwork();
        net.setLiveTime(10000000);
        net.setGeneration(10);
        net.setPopulation(100);
        net.setSpecies(5, 1, 1, 0.4, 3);
        net.setMaximumHiddenNode(50);
        
        net.setnInput(27);
        net.setnOutput(6);
        net.initialize();
        
        final CustomEvolAgent agent = new CustomEvolAgent();
        agent.nn = net;
       // agent.debug = true;
        marioAIOptions.setReceptiveFieldHeight(5);
        marioAIOptions.setReceptiveFieldWidth(5);
       
        System.out.println(agent.nn.listGens.size());
        marioAIOptions.setAgent(agent);
        
        marioAIOptions.setFPS(GlobalOptions.MaxFPS);
        marioAIOptions.setVisualization(false);
        marioAIOptions.setLevelRandSeed(2);
        
        final BasicTask basicTask = new BasicTask(marioAIOptions);
        
        //Load genome
        Genome inputGenome = null;
        try
        {
            FileInputStream infile = new FileInputStream("currentBest");
            InputStream inbuffer = new BufferedInputStream(infile);
            ObjectInput input = new ObjectInputStream(inbuffer);
            try
            {
                inputGenome = (Genome)input.readObject();
            }
            catch (ClassNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
            finally
            {
                input.close();
            }
        }
        catch(IOException ex)
        {
            System.out.println(ex.getMessage());
        }
        //Put the loaded genome into the list and clone
        agent.nn.listGens.clear();
        while(agent.nn.listGens.size() < agent.nn.getPopulation())
        {
            Genome tmp = new Genome(inputGenome);
            tmp.fitness = 0;
            agent.nn.listGens.add(tmp);
        }
        System.out.println(agent.nn.listGens.size());
        
//        while(agent.nn.listGens.size() < agent.nn.getPopulation())
//        {
//            Genome gen = new Genome();
//            gen.identity = agent.nn.listGens.size();
//            for(int j = 0; j < agent.nn.getnInput(); ++j)
//            {
//                gen.newNode(Layer.INPUT, j, -1);
//            }
//            for(int j = 0; j < agent.nn.getnOutput(); ++j)
//            {
//                gen.newNode(Layer.OUTPUT, j, -1);
//            }
//            mutation(gen);
//            agent.nn.listGens.add(gen);
//        }
        
        
        //Training
        int loop = 0;
        int numLevels = 10;
        int numDifs = 2;
        while(true)
        {
            for(int j = 0; j < agent.nn.listGens.size(); ++j)
            {
                agent.nn.setActiveGen(j);
                agent.nn.activatedGen.fitness = 0;
                
                for(int dif = 0; dif < numDifs; ++dif)
                {
                    for(int seed = 0; seed < numLevels; ++seed)
                    {
                        marioAIOptions.setLevelRandSeed(seed);
                        marioAIOptions.setLevelDifficulty(dif);
                        //marioAIOptions.setLevelDifficulty(0);
                        basicTask.runSingleEpisode(1);
                        //System.out.println(basicTask.getEnvironment().getEvaluationInfoAsString());
                        //System.out.print("IDENTITY: " + agent.nn.activatedGen.identity + " fit: ");
                        //System.out.println(basicTask.getEvaluationInfo().computeWeightedFitness());
    //                    double oldFitness = agent.nn.activatedGen.fitness;
                        agent.nn.activatedGen.fitness += basicTask.getEvaluationInfo().computeWeightedFitness();
                        //agent.nn.activatedGen.fitness += basicTask.getEvaluationInfo().computeWeightedFitness()*0.01;
                        //agent.nn.activatedGen.fitness += basicTask.getEvaluationInfo().coinsGained*10;
                        //agent.nn.activatedGen.fitness = basicTask.getEvaluationInfo().distancePassedCells;
    //                    while(agent.nn.activatedGen.fitness < oldFitness)
    //                    {
    //                        ((NEATNetwork)agent.nn).mutation(agent.nn.activatedGen);
    //                        basicTask.runSingleEpisode(1);
    //                        agent.nn.activatedGen.fitness = basicTask.getEvaluationInfo().computeWeightedFitness();
    //                    }   
                    }
                }
                agent.nn.activatedGen.fitness = agent.nn.activatedGen.fitness/(numLevels*numDifs);
            }
            //System.out.println("");
            //System.out.println("");
            //new Generations
            
            if(agent.nn.getBestFitness() > bestFitness)
            {
                bestFitness = agent.nn.getBestFitness();
                //Write current best
                double fitness = 0;
                Genome crbest = null;
                for(int i = 0; i < agent.nn.listGens.size(); ++i)
                {
                    if(agent.nn.listGens.get(i).fitness > fitness)
                    {
                        fitness = agent.nn.listGens.get(i).fitness;
                        crbest = new Genome(agent.nn.listGens.get(i));
                    }
                }
                try
                {
                    FileOutputStream outfile = new FileOutputStream("bestOfBest");
                    OutputStream outbuffer = new BufferedOutputStream(outfile);
                    ObjectOutput output = new ObjectOutputStream(outbuffer);
                    try{
                        output.writeObject(crbest);
                    }
                    finally{
                        output.close();
                    }
                }
                catch(IOException e)
                {
                    System.out.println(e.getMessage());
                }
                
                overallFitness = agent.nn.getOverallFitness();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                //System.out.println(overallFitness + " " + bestFitness + " -- " + dateFormat.format(date));
                System.out.print(overallFitness + " " + bestFitness + " -- " + dateFormat.format(date));
                    System.out.println(" Nodes: " + crbest.listHiddenNodes.size() + " connections: " + crbest.listConnections.size() + " bias connection: " + crbest.listBiasConnections.size());
                if(basicTask.isFinished())
                    break;
            }
            
            //System.out.println("overallFitness: " + overallFitness);
            //System.out.println("agent.nn.overallFitness: " + agent.nn.overallFitness);
            agent.nn.nextGeneration();
//            System.out.println("Number of connection: ");
//            for(int i = 0; i < agent.nn.distincGens.size(); ++i)
//            {
//                System.out.println(agent.nn.distincGens.get(i).getInfo());
//            }
//            System.out.println("");
            loop++;
        }
        System.out.println("Finish");
        //Select the best genome
        double fitness = 0;
        Genome bestGen = null;
        for(int i = 0; i < agent.nn.listGens.size(); ++i)
        {
            if(agent.nn.listGens.get(i).fitness > fitness)
            {
                System.out.println("Best gen: " + agent.nn.listGens.get(i).fitness);
                fitness = agent.nn.listGens.get(i).fitness;
                bestGen = agent.nn.listGens.get(i);
            }
        }
        //Write best genome to file
        try
        {
            FileOutputStream outfile = new FileOutputStream("bestOfBest");
            OutputStream outbuffer = new BufferedOutputStream(outfile);
            ObjectOutput output = new ObjectOutputStream(outbuffer);
            try{
                output.writeObject(bestGen);
            }
            finally{
                output.close();
            }
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
    }    
}
