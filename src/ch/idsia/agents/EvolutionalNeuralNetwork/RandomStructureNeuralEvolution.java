/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork;

import ch.idsia.agents.EvolutionalNeuralNetwork.Agent.NEATAgent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Phuc
 */
public class RandomStructureNeuralEvolution extends EvolNetwork {
    
    public void mutateAddConnection(Genome g)
    {
        Random rand = new Random();
        
        int idxFrom = -1;
        int idxTo = -1;
        
        int coin = rand.nextInt(2);
        Layer inputL = Layer.NONE;
        Layer outputL = Layer.NONE;
        if(coin == 0)
            inputL = Layer.INPUT;
        if(coin == 1)
            inputL = Layer.HIDDEN;
        coin = rand.nextInt(2);
        if(coin == 0)
            outputL = Layer.HIDDEN;
        if(coin == 1)
            outputL = Layer.OUTPUT;
        if(inputL == Layer.INPUT)
        {
            idxFrom = rand.nextInt(getnInput());
        }
        else
        {
            for(int i = 0; i < g.listHiddenNodes.size(); ++i)
            {
                coin = rand.nextInt(2);
                if(coin == 0)
                {
                    idxFrom = g.listHiddenNodes.get(i).index;
                    break;
                }
            }
            idxFrom = g.listHiddenNodes.get(0).index;
        }
        
        if(outputL == Layer.OUTPUT)
        {
            idxTo = rand.nextInt(getnOutput());
        }
        else
        {
            for(int i = 0; i < g.listHiddenNodes.size(); ++i)
            {
                coin = rand.nextInt(2);
                if(coin == 0)
                {
                    idxTo = g.listHiddenNodes.get(i).index;
                    break;
                }
            }
            idxTo = g.listHiddenNodes.get(0).index;
        }
        
        Connection c = new Connection(inputL, outputL, idxFrom, idxTo, rand.nextDouble()*2-1, getInnovationNumber());
        if(!checkDistincGen(c))
        {
            c.inno = getInnovationNumber();
            distincGens.add(c);
            setInnovationNumber(getInnovationNumber()+1);
        }
        g.addConnectionRandom(c);
        
        //For bias unit
        idxTo = rand.nextInt(g.listHiddenNodes.size());
        c = new Connection(Layer.INPUT, Layer.HIDDEN, -1, idxTo, rand.nextDouble()*2-1, getInnovationNumber());
        if(!checkDistincGen(c))
        {
            c.inno = getInnovationNumber();
            distincGens.add(c);
            setInnovationNumber(getInnovationNumber()+1);
        }
        g.addConnectionRandom(c);
        idxTo = rand.nextInt(getnOutput());
        c = new Connection(Layer.HIDDEN, Layer.OUTPUT, -1, idxTo, rand.nextDouble()*2-1, getInnovationNumber());
        if(!checkDistincGen(c))
        {
            c.inno = getInnovationNumber();
            distincGens.add(c);
            setInnovationNumber(getInnovationNumber()+1);
        }
        g.addConnectionRandom(c);
        idxTo = rand.nextInt(getnOutput());
        c = new Connection(Layer.INPUT, Layer.OUTPUT, -1, idxTo, rand.nextDouble()*2-1, getInnovationNumber());
        if(!checkDistincGen(c))
        {
            c.inno = getInnovationNumber();
            distincGens.add(c);
            setInnovationNumber(getInnovationNumber()+1);
        }
        g.addConnectionRandom(c);
    }
    
    public void mutateAddNodes(Genome g)
    {
        Random rand = new Random();
        int randFrom;
        int randTo;
        int largestIdx = -1;
        for(int i = 0; i < g.listHiddenNodes.size(); ++i)
        {
            if(g.listHiddenNodes.get(i).index > largestIdx)
                largestIdx = g.listHiddenNodes.get(i).index;
        }
        largestIdx++;
        if(NEATAgent.debug)
            System.out.println("Check index: " + largestIdx + "/" + g.listHiddenNodes.size());
        Node n = new Node(Layer.HIDDEN, largestIdx, getInnovationNumber());
        
        Connection connectToN = null;
        randFrom = rand.nextInt(getnInput());
        connectToN = new Connection(Layer.INPUT, Layer.HIDDEN, randFrom, largestIdx, 1, getInnovationNumber());
        //if(!checkDistincGen(connectToN))
        {
            connectToN.inno = getInnovationNumber();
            setInnovationNumber(getInnovationNumber()+1);
            distincGens.add(connectToN);
        }
        
        Connection connectFormN = null;

        randTo = rand.nextInt(getnOutput());
        double curW = g.getWeightOfConnection(Layer.INPUT, Layer.OUTPUT, randFrom, randTo);
        if(curW == -999)
            curW = rand.nextDouble() * 2 - 1;
        connectFormN = new Connection(Layer.HIDDEN, Layer.OUTPUT, largestIdx, randTo, curW, getInnovationNumber());
        //if(!checkDistincGen(connectFormN))
        {
            connectFormN.inno = getInnovationNumber();
            setInnovationNumber(getInnovationNumber()+1);
            distincGens.add(connectFormN);
        }
        
        g.mutateAddNodeRandom(n, connectToN, connectFormN);
    }
       
    public void Mutation(Genome g)
    {
        if(g.listHiddenNodes.size() < getMaximumHiddenNode())
            mutateAddNodes(g);
        mutateAddConnection(g);
//        mutateAddNewNormalConnection(g);
//        mutateChangeNormalConnectionWeight(g);
//
//        mutateAddNewBiasConnection(g);
//        mutateChangeBiasConnectionWeight(g);
    }
    
    /*
        This function create the next generation by performing crossover and mutation.
    */
    public void nextGeneration()
    {
        //Kill the bad genome after some specific round
//        genCount++;
//        if(genCount > getLiveTime())
//        {
//            double fitness = 999999;
//            int badGen = -1;
//            int identity = -1;
//            for(int i = 0; i < listGens.size(); ++i)
//            {
//                if(listGens.get(i).fitness < fitness)
//                {
//                    badGen = i;
//                    identity = listGens.get(i).identity;
//                }
//            }
//            listGens.remove(badGen);
//            //Create new gen;
//            Genome gen = new Genome();
//            gen.identity = identity;
//            for(int j = 0; j < getnInput(); ++j)
//            {
//                gen.newNode(Layer.INPUT, j, -1);
//            }
//            for(int j = 0; j < getnOutput(); ++j)
//            {
//                gen.newNode(Layer.OUTPUT, j, -1);
//            }
//            gen.newNode(Layer.HIDDEN, 0, getInnovationNumber());
//            listGens.add(gen);
//            genCount = 0;
//        }
        
        List<Genome> newGeneration = new ArrayList<>();
        //CROSSOVER
        Random rand = new Random();
        //Mating of 2 best parents
        Genome a = null;
        Genome b = null;
        double fitness = -1;
        //Print checkGens
        if(NEATAgent.debug)
            for(int i = 0; i < listGens.size(); ++i)
            {
                System.out.println("Gen at index" + i + ": " + listGens.get(i).fitness);            
            }
        
        int idP1 = -1;
        int idP2 = -1;
        //Select the first parent
        fitness = -1;
        for(int i = 0; i < listGens.size(); ++i)
        {
            if(listGens.get(i).fitness > fitness)
            {
                a = listGens.get(i);
                fitness = a.fitness;
                idP1 = i;
            }
        }
       
        //Select the second parent
        fitness = -1;
        for(int i = 0; i < listGens.size(); ++i)
        {
            if(listGens.get(i).fitness > fitness && !listGens.get(i).equals(a))
            {
                b = listGens.get(i);
                fitness = b.fitness;
                idP2 = i;
            }
        }
        
        if(a == null)
        {
            if(NEATAgent.debug)
                System.out.println("A null!");
        }
        if(b == null)
        {
            if(NEATAgent.debug)
                System.out.println("B null!");
        }
        
        //Copy the best genome (Selection)
        Genome tmpGen = new Genome(a);
        tmpGen.identity = newGeneration.size();
        newGeneration.add(tmpGen);
        tmpGen = new Genome(b);
        tmpGen.identity = newGeneration.size();
        newGeneration.add(tmpGen);
        Genome mutatedGen = new Genome(a);
        Mutation(mutatedGen);
        mutatedGen.identity = newGeneration.size();
        newGeneration.add(mutatedGen);
        mutatedGen = new Genome(b);
        Mutation(mutatedGen);
        mutatedGen.identity = newGeneration.size();
        newGeneration.add(mutatedGen);
        
        //System.out.println("Best Gen: " + a.fitness);
        
        //System.out.println("CROSSOVER: Remove " + listGens.get(worstIdx).fitness + " ------ at (" + idP1 + ") " + a.fitness + " x " + " at (" + idP2 + ") " + b.fitness);
        //1st mate
        //Genome newGen = Genome.crossOver(a, b, crossOverPercent);
//        if(rand.nextDouble() < getCrossOverChance())
//        {
//            //1st mate
//            Genome newGen = Genome.crossOver(a, b);
//            newGen.identity = newGeneration.size();
//            if(NEATAgent.debug)
//                if(newGen != null)
//                    System.out.println("NEW GEN CREATED!!!!");
//            newGeneration.add(newGen);
//            
//            //2nd mate
//            //newGen = Genome.crossOver(b, a, crossOverPercent);
//            newGen = Genome.crossOver(b, a);
//            newGen.identity = newGeneration.size();
//            if(NEATAgent.debug)
//                if(newGen != null)
//                    System.out.println("NEW GEN CREATED!!!!");
//            newGeneration.add(newGen);
//        }
        //Mutation the rest
        while(newGeneration.size() < listGens.size())
        {
            //Genome bestG = new Genome(listGens.get(rand.nextInt(listGens.size())));
            Genome bestG = new Genome(a);
            bestG.identity = newGeneration.size();
//            for(int i = 0; i < bestG.listHiddenNodes.size(); ++i)
//            {
//                if(bestG.listHiddenNodes.get(i).z == 0)
//                {
//                    bestG.listHiddenNodes.remove(i);
//                    i--;
//                }
//            }
            for(int i = 0; i < bestG.listConnections.size(); ++i)
            {
                if(bestG.listConnections.get(i).isDisable &&
                        bestG.listConnections.get(i).w == 0)
                {
                    bestG.listConnections.remove(i);
                    i--;
                }
                Mutation(bestG);
            }
            for(int i = 0; i < bestG.listBiasConnections.size(); ++i)
            {
                if(bestG.listBiasConnections.get(i).isDisable &&
                        bestG.listBiasConnections.get(i).w == 0)
                {
                    bestG.listBiasConnections.remove(i);
                    i--;
                }
                Mutation(bestG);
            }
            Mutation(bestG);
            newGeneration.add(bestG);
            
//            //Create new gen;
//            Genome gen = new Genome();
//            gen.identity = newGeneration.size();
//            for(int j = 0; j < getnInput(); ++j)
//            {
//                gen.newNode(Layer.INPUT, j, -1);
//            }
//            for(int j = 0; j < getnOutput(); ++j)
//            {
//                gen.newNode(Layer.OUTPUT, j, -1);
//            }
//            gen.newNode(Layer.HIDDEN, 0, getInnovationNumber());
//            newGeneration.add(gen);
        }
        
        listGens.clear();
        listGens = newGeneration;
    }
    
    //Generate current activated genome
    public boolean[] generateAction(double[] testSample)
    {
        boolean[] result = new boolean[6];
        activatedGen.setInput(testSample);
        activatedGen.feedForward();
        activatedGen.obtainOutput(result);
        return result;
    }   
}
