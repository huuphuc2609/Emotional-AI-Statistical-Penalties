/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Emotions;

import ch.idsia.agents.EvolutionalNeuralNetwork.CustomNetwork;
import ch.idsia.agents.EvolutionalNeuralNetwork.Genome;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 *
 * @author Phuc
 * Training Emotional AI using trace position only
 */
public class EmotionalEvolTest {
       
    public static void main(String[] args) {
        
        long startTime = System.currentTimeMillis();
        final String argsString = "-trace on";
        //final String argsString = "-le off -lb off";
        final MarioAIOptions marioAIOptions = new MarioAIOptions(argsString);
        //final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
        marioAIOptions.setLevelRandSeed(0);
        double overallFitness = -10000;
        double bestFitness = 0;
        //CustomNetwork net = new CustomNetwork();
        CustomNetwork net = new CustomNetwork();
        
        net.setPopulation(100);
        net.setMaximumHiddenNode(10);
        net.setStepSize(0.1);
        
        net.setMutationChance(1.0);
        net.setNodeMutationChance(0.5);
        net.setAddConnectionChance(0.5);
        
        net.setConnectionWeightPerturbationChance(0.9);
        net.setBiasMutationChance(0.5);
               
        net.setnInput(27);
        net.setnOutput(6);
        net.initialize();
        
        final EmotionalAgent agent = new EmotionalAgent();
        agent.nn = net;
        agent.setnInput(5);
        agent.initializeEmotionUnit();
        
        //NEATAgent.debug = true;
        marioAIOptions.setReceptiveFieldHeight(5);
        marioAIOptions.setReceptiveFieldWidth(5);
       
        System.out.println(agent.nn.listGens.size());
        marioAIOptions.setAgent(agent);
        
        marioAIOptions.setFPS(GlobalOptions.MaxFPS);
        marioAIOptions.setVisualization(false);
        marioAIOptions.setLevelRandSeed(0);
        
        final BasicTask basicTask = new BasicTask(marioAIOptions);
        
        //****************Read hashmap from file****************
        HashMap<EState, EAction> humanTrack = new HashMap<>();
        try
        {
            FileInputStream infile = new FileInputStream("hashmapHumanPlay");
            InputStream inbuffer = new BufferedInputStream(infile);
            ObjectInput input = new ObjectInputStream(inbuffer);
            try
            {
                humanTrack = (HashMap<EState,EAction>)input.readObject();
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
        List<EState> listHS = new ArrayList<EState>(humanTrack.keySet());
        List<EAction> listHA = new ArrayList<EAction>(humanTrack.values());
        //System.out.println(listHS.size());
        
        //****************Read trace from human data****************
        List<double[]> humanTrace = new ArrayList<>();
        try
        {
            FileReader fr = new FileReader("trace.txt");
            BufferedReader br = new BufferedReader(fr);
            for(String line; (line = br.readLine()) != null; ) 
            {
                // process the line.
                double[] val = new double[2];
                String[] tmp = line.split(" ");
                val[0] = Double.parseDouble(tmp[0]);
                val[1] = Double.parseDouble(tmp[1]);
                humanTrace.add(val);
            }
            br.close();
            fr.close();
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
        
//        for(int i = 0; i < humanTrace.size(); ++i)
//        {
//            System.out.println(humanTrace.get(i)[0] + " " + humanTrace.get(i)[1]);
//        }
        int loop = 0;
        int level = 1;
        int numOfChild = 10;
        while(loop < 2500)
        {
//            //*************************Fully advanced option*************************
//            for(int j = 0; j < agent.nn.listGens.size(); ++j)
//            {
//                agent.nn.setActiveGen(j);
//                agent.nn.activatedGen.fitness = 0;
//                agent.agentTrace.clear();
//                //Reset the number of spike
//                agent.numOfFearSpike = 0;
//                    basicTask.runSingleEpisode(1);
//                    
//                    agent.nn.activatedGen.fitness += basicTask.getEvaluationInfo().computeDistancePassed()*0.001;
//                    double traceError = 0.0;
//                    for(int i = 0; i < agent.agentTrace.size(); ++i)
//                    {
//                        if(i >= humanTrace.size())
//                            break;
//                        double distance = Math.abs(Math.sqrt((humanTrace.get(i)[0] - agent.agentTrace.get(i)[0])*(humanTrace.get(i)[0] - agent.agentTrace.get(i)[0])
//                         + (humanTrace.get(i)[1] - agent.agentTrace.get(i)[1])*(humanTrace.get(i)[1] - agent.agentTrace.get(i)[1])));
////                        if(distance > 32)
////                            agent.nn.activatedGen.fitness-=0.6;
////                        else if( distance <= 32)
////                            agent.nn.activatedGen.fitness+=1;
//                        if(distance <= 200)
//                            agent.nn.activatedGen.fitness+=1;
//                        else
//                            agent.nn.activatedGen.fitness-=10;
//                    }
//                    
////                    while(agent.nn.activatedGen.fitness < oldFitness)
////                    {
////                        ((NEATNetwork)agent.nn).mutation(agent.nn.activatedGen);
////                        basicTask.runSingleEpisode(1);
////                        agent.nn.activatedGen.fitness = basicTask.getEvaluationInfo().computeWeightedFitness();
////                    }
//            }
//            //System.out.println("");
//            //System.out.println("");
//            //new Generations
            
            //*************************Evol advanced option*************************
            double alpha = 0.1;
            double beta = 0.05;
            double wfit = 0.1;
            List<Genome> listNew = new ArrayList<>();
            //Random shuffle           
            Collections.shuffle(agent.nn.listGens);
            for(int i = 0; i < agent.nn.listGens.size(); i+=2)
            {
                //Pairing
                Genome p1 = agent.nn.listGens.get(i);
                Genome p2 = agent.nn.listGens.get(i+1);
                
                //****************P1****************
                agent.nn.activatedGen = p1;
                agent.nn.activatedGen.fitness = 0;
                agent.agentTrace.clear();
                agent.track.clear();
                for(int m = 0; m < level; m++)
                {
                    marioAIOptions.setLevelRandSeed(m);
                    basicTask.runSingleEpisode(1);
                    //agent.nn.activatedGen.fitness += basicTask.getEvaluationInfo().computeWeightedFitness();
                    agent.nn.activatedGen.fitness += basicTask.getEvaluationInfo().computeWeightedFitness()*wfit;
                    double traceError = 0.0;
                    for(int t = 0; t < agent.agentTrace.size(); ++t)
                    {
                        if(t >= humanTrace.size())
                            break;
                        double distance = Math.abs(Math.sqrt((humanTrace.get(t)[0] - agent.agentTrace.get(t)[0])*(humanTrace.get(t)[0] - agent.agentTrace.get(t)[0])
                         + (humanTrace.get(t)[1] - agent.agentTrace.get(t)[1])*(humanTrace.get(t)[1] - agent.agentTrace.get(t)[1])));
                        agent.nn.activatedGen.fitness += measureDistance(distance);
                    }
                    List<EState> listAS = new ArrayList<>(agent.track.keySet());
                    List<EAction> listAA = new ArrayList<>(agent.track.values());
                    agent.nn.activatedGen.fitness += measureStateAction(listHS, listAS, listHA, listAA);
                    agent.nn.activatedGen.fitness += measureState(humanTrack, agent.track, listAS);
                }
                agent.nn.activatedGen.fitness = agent.nn.activatedGen.fitness/level;
                
                //****************P2****************
                agent.nn.activatedGen = p2;
                agent.nn.activatedGen.fitness = 0;
                agent.agentTrace.clear();
                agent.track.clear();
                for(int m = 0; m < level; m++)
                {
                    marioAIOptions.setLevelRandSeed(m);
                    basicTask.runSingleEpisode(1);
                    //agent.nn.activatedGen.fitness += basicTask.getEvaluationInfo().computeWeightedFitness();
                    agent.nn.activatedGen.fitness += basicTask.getEvaluationInfo().computeWeightedFitness()*wfit;
                    double traceError = 0.0;
                    for(int t = 0; t < agent.agentTrace.size(); ++t)
                    {
                        if(t >= humanTrace.size())
                            break;
                        double distance = Math.abs(Math.sqrt((humanTrace.get(t)[0] - agent.agentTrace.get(t)[0])*(humanTrace.get(t)[0] - agent.agentTrace.get(t)[0])
                         + (humanTrace.get(t)[1] - agent.agentTrace.get(t)[1])*(humanTrace.get(t)[1] - agent.agentTrace.get(t)[1])));
                        agent.nn.activatedGen.fitness += measureDistance(distance);
                    }
                    List<EState> listAS = new ArrayList<>(agent.track.keySet());
                    List<EAction> listAA = new ArrayList<>(agent.track.values());
                    agent.nn.activatedGen.fitness += measureStateAction(listHS, listAS, listHA, listAA);
                    agent.nn.activatedGen.fitness += measureState(humanTrack, agent.track, listAS);
                }
                agent.nn.activatedGen.fitness = agent.nn.activatedGen.fitness/level;
                
                
                List<Genome> child1 = new ArrayList<>();
                List<Genome> child2 = new ArrayList<>();
                
                //****************P1's children****************
                for(int j = 0; j < numOfChild; ++j)
                {
                    Genome newGen = new Genome(p1);
                    //((CustomNetwork)agent.nn).mutation(newGen);
                    ((CustomNetwork)agent.nn).mutationWithRemove(newGen);
                    child1.add(newGen);
                    agent.nn.activatedGen = newGen;
                    agent.nn.activatedGen.fitness = 0;
                    agent.agentTrace.clear();
                    agent.track.clear();
                    for(int m = 0; m < level; m++)
                    {
                        marioAIOptions.setLevelRandSeed(m);
                        basicTask.runSingleEpisode(1);
                        //agent.nn.activatedGen.fitness += basicTask.getEvaluationInfo().computeWeightedFitness();
                        agent.nn.activatedGen.fitness += basicTask.getEvaluationInfo().computeWeightedFitness()*wfit;
                        double traceError = 0.0;
                        for(int t = 0; t < agent.agentTrace.size(); ++t)
                        {
                            if(t >= humanTrace.size())
                                break;
                            double distance = Math.abs(Math.sqrt((humanTrace.get(t)[0] - agent.agentTrace.get(t)[0])*(humanTrace.get(t)[0] - agent.agentTrace.get(t)[0])
                             + (humanTrace.get(t)[1] - agent.agentTrace.get(t)[1])*(humanTrace.get(t)[1] - agent.agentTrace.get(t)[1])));
                            agent.nn.activatedGen.fitness += measureDistance(distance);
                        }
                        List<EState> listAS = new ArrayList<>(agent.track.keySet());
                        List<EAction> listAA = new ArrayList<>(agent.track.values());
                        agent.nn.activatedGen.fitness += measureStateAction(listHS, listAS, listHA, listAA);
                        agent.nn.activatedGen.fitness += measureState(humanTrack, agent.track, listAS);
                    }
                    agent.nn.activatedGen.fitness = agent.nn.activatedGen.fitness/level;
                }
                //****************P2's children****************
                for(int j = 0; j < numOfChild; ++j)
                {
                    Genome newGen = new Genome(p2);
                    //((CustomNetwork)agent.nn).mutation(newGen);
                    ((CustomNetwork)agent.nn).mutationWithRemove(newGen);
                    child2.add(newGen);
                    agent.nn.activatedGen = newGen;
                    agent.nn.activatedGen.fitness = 0;
                    agent.agentTrace.clear();
                    agent.track.clear();
                    for(int m = 0; m < level; m++)
                    {
                        marioAIOptions.setLevelRandSeed(m);
                        basicTask.runSingleEpisode(1);
                        //agent.nn.activatedGen.fitness += basicTask.getEvaluationInfo().computeWeightedFitness();
                        agent.nn.activatedGen.fitness += basicTask.getEvaluationInfo().computeWeightedFitness()*wfit;
                        double traceError = 0.0;
                        for(int t = 0; t < agent.agentTrace.size(); ++t)
                        {
                            if(t >= humanTrace.size())
                                break;
                            double distance = Math.abs(Math.sqrt((humanTrace.get(t)[0] - agent.agentTrace.get(t)[0])*(humanTrace.get(t)[0] - agent.agentTrace.get(t)[0])
                             + (humanTrace.get(t)[1] - agent.agentTrace.get(t)[1])*(humanTrace.get(t)[1] - agent.agentTrace.get(t)[1])));
                            agent.nn.activatedGen.fitness += measureDistance(distance);
                        }
                        List<EState> listAS = new ArrayList<>(agent.track.keySet());
                        List<EAction> listAA = new ArrayList<>(agent.track.values());
                        agent.nn.activatedGen.fitness += measureStateAction(listHS, listAS, listHA, listAA);
                        agent.nn.activatedGen.fitness += measureState(humanTrack, agent.track, listAS);
                    }
                    agent.nn.activatedGen.fitness = agent.nn.activatedGen.fitness/level;
                }

                for(int j = 0; j < child1.size(); ++j)
                {
                    if(child1.get(j).fitness > p1.fitness)
                        p1 = child1.get(j);
                }
                for(int j = 0; j < child2.size(); ++j)
                {
                    if(child2.get(j).fitness > p2.fitness)
                        p2 = child2.get(j);
                }
                listNew.add(new Genome(p1));
                listNew.add(new Genome(p2));
                child1.clear();
                child2.clear();
            }
            
            agent.nn.listGens.clear();
            agent.nn.listGens.addAll(listNew);
            
            //if(agent.nn.getOverallFitness() > overallFitness)
            {
                //Save best genome
                double fitness = -10000;
                Genome crbest = null;
                for(int i = 0; i < agent.nn.listGens.size(); ++i)
                {
                    if(agent.nn.listGens.get(i).fitness >= fitness)
                    {
                        fitness = agent.nn.listGens.get(i).fitness;
                        crbest = new Genome(agent.nn.listGens.get(i));
                    }
                }
                try
                {
                    FileOutputStream outfile = new FileOutputStream("EmotionalTest");
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
                bestFitness = agent.nn.getBestFitness();
                
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                
                if(crbest != null)
                {
                    System.out.print(overallFitness + " " + bestFitness + " -- " + dateFormat.format(date));
                    System.out.println(" Nodes: " + crbest.listHiddenNodes.size() + " connections: " + crbest.listConnections.size() + " bias connection: " + crbest.listBiasConnections.size());
                }
                else
                    System.out.println(overallFitness + " " + bestFitness + " -- " + dateFormat.format(date));
                if(basicTask.isFinished() && bestFitness > 800000)
                    break;
            }
            
            //System.out.println("overallFitness: " + overallFitness);
            //System.out.println("agent.nn.overallFitness: " + agent.nn.overallFitness);
            //agent.nn.nextGeneration();
//            System.out.println("Number of connection: ");
//            for(int i = 0; i < agent.nn.distincGens.size(); ++i)
//            {
//                System.out.println(agent.nn.distincGens.get(i).getInfo());
//            }
//            System.out.println("");
            loop++;
        }
        
        long finishTime = System.currentTimeMillis() - startTime;
        System.out.println("Time: " + finishTime + " ms");
        
        //Select the best genome
        double fitness = 0;
        Genome bestGen = null;
        for(int i = 0; i < agent.nn.listGens.size(); ++i)
        {
            if(agent.nn.listGens.get(i).fitness > fitness)
            {
                System.out.println("Best gen: " + agent.nn.listGens.get(i).fitness);
                fitness = agent.nn.listGens.get(i).fitness;
                bestGen = new Genome(agent.nn.listGens.get(i));
            }
        }
        //Write best genome to file
        try
        {
            FileOutputStream outfile = new FileOutputStream("EmotionalEvolution");
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
        //Read genome from file
        Genome inputGenome = null;
        try
        {
            FileInputStream infile = new FileInputStream("EmotionalEvolution");
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
        
        marioAIOptions.setFPS(24);
        marioAIOptions.setVisualization(true);
        agent.nn.listGens.clear();
        agent.nn.listGens.add(inputGenome);
        System.out.println(agent.nn.listGens.size());
        agent.nn.setActiveGen(0);
        basicTask.runSingleEpisode(1);
        agent.nn.activatedGen.fitness = basicTask.getEvaluationInfo().computeWeightedFitness();
        
//        for (int i = 0; i < 10; ++i) //Level difficult from 0 to 10
//        {
//            int seed = i;
//            do
//            {
//                for(int j = 0; j < agent.nn.listGens.size(); ++j)
//                {
//                    agent.nn.setActiveGen(j);
//
//                        basicTask.runSingleEpisode(1);
//                        //System.out.println(basicTask.getEnvironment().getEvaluationInfoAsString());
//                        System.out.print(agent.nn.activatedGen.identity + ": ");
//                        System.out.println(basicTask.getEvaluationInfo().computeWeightedFitness());
//                        agent.nn.activatedGen.fitness = basicTask.getEvaluationInfo().computeWeightedFitness();
//                }
//                //new Generations
//                
//                
//            } while (basicTask.getEvaluationInfo().computeWeightedFitness() < 1000);
//        }
        
    }
    
    public static double measureDistance(double distance)
    {
        if (distance <= 100)
            return 1;
        else if(distance <= 200)
            return 0.1;
        else if(distance <= 300)
            return 0.075;
        else if(distance <= 400)
            return 0.05;
        else
            return -0.005;
    }
    
    public static double measureStateAction(List<EState> hs, List<EState> as, List<EAction> ha, List<EAction> aa)
    {
        double result = 0;
        
        
        return result;
    }
    
    public static double measureState(HashMap<EState,EAction> a, HashMap<EState,EAction> b, List<EState> c)
    {
        double result = 0;
        for(int i = 0; i < c.size(); ++i)
        {
            EState e = c.get(i);
            EAction hp = a.get(e);
            EAction aip = a.get(e);
            if(hp != null && aip != null)
            {
                if(hp.equals(aip))
                    result++;
            }
        }
        return result;
    }
}
