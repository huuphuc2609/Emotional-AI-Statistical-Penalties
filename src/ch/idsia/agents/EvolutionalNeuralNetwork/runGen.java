/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork;

import ch.idsia.agents.EvolutionalNeuralNetwork.Agent.NEATAgent;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Random;

/**
 *
 * @author Phuc
 */
public class runGen {
    public static void main(String[] args) {
                Random r = new Random();
//        System.out.println("Gaussian:");
//        for(int i = 0; i < 10; ++i)
//        {
//           System.out.println(r.nextGaussian());
//        }
//        System.out.println("Double:");
        for(int i = 0; i < 10; ++i)
        {
           //System.out.println(r.nextDouble()*4-2);
           System.out.println(r.nextDouble()*2-1);
        }
        
        
        final String argsString = "-le off -lb off";
        final MarioAIOptions marioAIOptions = new MarioAIOptions(argsString);
        //final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
        double overallFitness = 0;
        NEATNetwork net = new NEATNetwork();
        net.setGeneration(10);
        net.setPopulation(10);
        net.setnInput(27);
        net.setnOutput(6);
        net.initialize();
        final NEATAgent agent = new NEATAgent();
        agent.nn = net;
        
        marioAIOptions.setReceptiveFieldHeight(5);
        marioAIOptions.setReceptiveFieldWidth(5);
       
        System.out.println(agent.nn.listGens.size());
        marioAIOptions.setAgent(agent);    
        
        marioAIOptions.setFPS(GlobalOptions.MaxFPS);
        marioAIOptions.setVisualization(false);
        marioAIOptions.setLevelRandSeed(0);
        final BasicTask basicTask = new BasicTask(marioAIOptions);
        Genome inputGenome = null;
        try
        {
            FileInputStream infile = new FileInputStream("NEATEvolution");
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
        
        marioAIOptions.setFPS(30);
        marioAIOptions.setVisualization(true);
        //marioAIOptions.setLevelRandSeed(1);
        agent.nn.listGens.clear();
        agent.nn.listGens.add(inputGenome);
        System.out.println(agent.nn.listGens.size());
        agent.nn.setActiveGen(0);
        agent.nn.activatedGen.showGenInfo();
        basicTask.runSingleEpisode(1);
        agent.nn.activatedGen.fitness = basicTask.getEvaluationInfo().computeWeightedFitness();
        System.out.println(basicTask.getEnvironment().getEvaluationInfoAsString());
        
        System.out.println("GENOME STRUCTURE!!!!!!!");

    }
}
