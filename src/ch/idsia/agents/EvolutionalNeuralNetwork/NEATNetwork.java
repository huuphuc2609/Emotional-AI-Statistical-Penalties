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
public class NEATNetwork extends EvolNetwork {
          
    private int numSpecies;
    private double c1;
    private double c2;
    private double c3;
    private double sigma; // compability threshold;
    
    public List<Species> listSpecies;
    
    public NEATNetwork()
    {
        super();
        numSpecies = 0;
        c1 = 0;
        c2 = 0;
        c3 = 0;
        listSpecies = new ArrayList<>();
    }
    
    public void setSpecies(int numVal, double c1Val, double c2Val, double c3Val, double sigmaVal)
    {
        numSpecies = numVal;
        c1 = c1Val;
        c2 = c2Val;
        c3 = c3Val;
        sigma = sigmaVal;
    }
    
    @Override
    public void setActiveGen(int i) {
        activatedGen = listGens.get(i);
        if(NEATAgent.debug)
        {
            activatedGen.showGenInfo();
            System.out.println("");
        }
    }
    
    public boolean mutateAddNewNormalConnection(Genome g)
    {
        Random rand = new Random();
        int idxFrom = -2;
        int idxTo = -2;
        int coin;

        Layer fromL = Layer.NONE;
        Layer toL = Layer.NONE;

        Connection c = null;

        //Randomly choose a from layer
        coin = rand.nextInt(2);
        if(coin == 0)
            fromL = Layer.INPUT;
        else
            fromL = Layer.HIDDEN;
        //Randomly choose a to layer
        coin = rand.nextInt(2);
        if(coin == 0)
            toL = Layer.HIDDEN;
        else
            toL = Layer.OUTPUT;
        //Randomly choose a node index for from layer
        if(fromL == Layer.INPUT)
        {
            idxFrom = rand.nextInt(getnInput());
        }
        else
        {
            if(g.listHiddenNodes.isEmpty())
                return false;
            idxFrom = rand.nextInt(g.listHiddenNodes.size());
        }
        //Randomly choose a node index for to layer
        if(toL == Layer.OUTPUT)
        {
            idxTo = rand.nextInt(getnOutput());
        }
        else
        {
            if(g.listHiddenNodes.isEmpty())
                return false;
            idxTo = rand.nextInt(g.listHiddenNodes.size());
        }

        //Mutate normal connection
        if(idxFrom != -2 && idxTo != -2)
        {
            c = new Connection(fromL, toL, idxFrom, idxTo, rand.nextDouble()*2-1, getInnovationNumber());
            c = checkDistinct(c);
            return g.addConnection(c);
        }
        return false;
    }
    
    public void mutateAddNewBiasConnection(Genome g)
    {
        Random rand = new Random();
        int coin;
        int idxTo = -2;
        Layer fromL = null;
        Layer toL = null;
        Connection c = null;
        //Randomly choose a from layer
        coin = rand.nextInt(2);
        if(coin == 0)
        {
            fromL = Layer.INPUT;
            toL = Layer.HIDDEN;
        }
        else
        {
            fromL = Layer.HIDDEN;
            toL = Layer.OUTPUT;
        }
        if(g.listHiddenNodes.isEmpty())
            return;
        if(fromL.equals(Layer.INPUT))
            idxTo = rand.nextInt(g.listHiddenNodes.size());
        else if(fromL.equals(Layer.HIDDEN))
            idxTo = rand.nextInt(g.listOutputNodes.size());
        c = new Connection(fromL, toL, -1, idxTo, rand.nextDouble()*2-1, getInnovationNumber());
        c = checkDistinct(c);
        g.addConnection(c);
    }
    
    public void mutateChangeNormalConnectionWeight(Genome g)
    {
        Random rand = new Random();
        if(g.listConnections.size() > 0)
        {
            for(Connection c : g.listConnections)
            {
//                if(rand.nextDouble() < getConnectionWeightPerturbationChance())
//                    c.w = c.w + (rand.nextDouble()*2-1)*getStepSize();
//                else
//                    c.w = rand.nextDouble()*2-1;
                if(rand.nextDouble() < getConnectionWeightPerturbationChance())
                    c.w += rand.nextGaussian()*getStepSize();
                else
                    c.w = rand.nextGaussian();
            }
        }
    }
    
    public void mutateDisableConnection(Genome g)
    {
        Random rand = new Random();
        if(g.listConnections.size() > 0)
        {
            for(Connection c : g.listConnections)
            {
                if(rand.nextDouble() < getDisableMutationChance())
                {
                    c.isDisable = true;
                }
                else 
                {
                    c.isDisable = false;
                }
            }
        }
        if(g.listBiasConnections.size() > 0)
        {
            for(Connection c : g.listBiasConnections)
            {
                if(rand.nextDouble() < getDisableMutationChance())
                {
                    c.isDisable = true;
                }
                else 
                {
                    c.isDisable = false;
                }
            }
        }
    }
    
    public void mutateChangeBiasConnectionWeight(Genome g)
    {
        Random rand = new Random();
        if(g.listBiasConnections.size() > 0)
        {
            for(Connection c : g.listBiasConnections)
            {
//                if(rand.nextDouble() < getConnectionWeightPerturbationChance())
//                    c.w = c.w + (rand.nextDouble()*2-1)*getStepSize();
//                else
//                    c.w = rand.nextDouble()*2-1;
                if(rand.nextDouble() < getConnectionWeightPerturbationChance())
                    c.w += rand.nextGaussian()*getStepSize();
                else
                    c.w = rand.nextGaussian();
            }
        }
    }
    
    public void mutateAddNodes(Genome g)
    {
        if(g.listConnections.size() > 0)
        {
            Random rand = new Random();
            //********Get the largest missing id in the list of hidden nodes********
            int largestIdx = -1;
            for(Node tmp : g.listHiddenNodes)
            {
                if(tmp.index > largestIdx)
                    largestIdx = tmp.index;
            }
            largestIdx++;
            if(NEATAgent.debug)
                System.out.println("Check index: " + largestIdx + "/" + g.listHiddenNodes.size());
            //**********************************************************************
            Connection tmpC = g.listConnections.get(rand.nextInt(g.listConnections.size()));
            //New node
            Node n = new Node(Layer.HIDDEN, largestIdx, getInnovationNumber());
            //Create 2 connections of this new node (in connection and out connection)
            Connection connectToN = null;
            connectToN = new Connection(tmpC.fromLayer, Layer.HIDDEN, tmpC.idxFromNode, largestIdx, 1, getInnovationNumber());
            connectToN = checkDistinct(connectToN);
            Connection connectFormN = null;

            connectFormN = new Connection(Layer.HIDDEN, tmpC.toLayer, largestIdx, tmpC.idxToNode, tmpC.w, getInnovationNumber());
            connectFormN = checkDistinct(connectFormN);
            boolean check = g.mutateAddNode(n, connectToN, connectFormN);
            if(check)
                tmpC.isDisable = true;
        }
    }
    
    public void mutation(Genome g)
    {
        Random rand = new Random();
        
        if(rand.nextDouble() < getNodeMutationChance()
                && g.listHiddenNodes.size() < getMaximumHiddenNode())
        {
            //System.out.println("MutateChance:" + getNodeMutationChance() + " --- ");
            mutateAddNodes(g);
        }
        if(rand.nextDouble() < getAddConnectionChance())
        {
            mutateAddNewNormalConnection(g);
        }
        else
            mutateChangeNormalConnectionWeight(g);
        
        //Mutate change status of connection
        mutateDisableConnection(g);
        
        if(rand.nextDouble() < getBiasMutationChance())
        {
            mutateAddNewBiasConnection(g);
            mutateChangeBiasConnectionWeight(g);
        }
    }
    
    public void crossover(List<Genome> inputList, List<Genome> outputList)
    {
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
        tmpGen.identity = outputList.size();
        outputList.add(tmpGen);
        tmpGen = new Genome(b);
        tmpGen.identity = outputList.size();
        outputList.add(tmpGen);
        
//        Genome mutatedGen = new Genome(a);
//        mutation(mutatedGen);
//        mutatedGen.identity = outputList.size();
//        outputList.add(mutatedGen);
//        mutatedGen = new Genome(b);
//        mutation(mutatedGen);
//        mutatedGen.identity = outputList.size();
//        outputList.add(mutatedGen);
        
        //System.out.println("Best Gen: " + a.fitness);
        
        //System.out.println("CROSSOVER: Remove " + listGens.get(worstIdx).fitness + " ------ at (" + idP1 + ") " + a.fitness + " x " + " at (" + idP2 + ") " + b.fitness);
        //1st mate
        //Genome newGen = Genome.crossOver(a, b, crossOverPercent);
        if(rand.nextDouble() < getCrossOverChance())
        {
            //1st mate
            Genome newGen = Genome.crossOverNEAT(a, b, getEnableMutationChance(), getDisableMutationChance());
            newGen.identity = outputList.size();
            if(NEATAgent.debug)
                if(newGen != null)
                    System.out.println("NEW GEN CREATED!!!!");
            outputList.add(newGen);
            
//            //2nd mate
//            //newGen = Genome.crossOver(b, a, crossOverPercent);
//            newGen = Genome.crossOverNEAT(b, a, getEnableMutationChance(), getDisableMutationChance());
//            newGen.identity = outputList.size();
//            if(NEATAgent.debug)
//                if(newGen != null)
//                    System.out.println("NEW GEN CREATED!!!!");
//            outputList.add(newGen);
        }
    }
    
    /*
        This function create the next generation by performing crossover and mutation.
    */
    public void nextGeneration()
    {
        List<Genome> newGeneration = new ArrayList<>();
        
//        for(int i = 0; i < listGens.size(); ++i)
//        {
//            System.out.println(i + " " + listGens.get(i).fitness);
//        }
        
        //SORT GENOME INCREASING FITNESS
        sortByFitness(listGens, false);        
        newGeneration.add(new Genome(listGens.get(0)));
        //REPRODUCTION
        adjustingFitness(listGens);
        seperateSpecies(listGens);
        for(int i = 0; i < listSpecies.size(); ++i)
        {
            if(listSpecies.get(i).listGen.isEmpty())
            {
                listSpecies.remove(i);
                i--;
            }
        }
        
        ////Need to reselect the topGen.
        for(Species sp : listSpecies)
        {
            for(Genome gn : sp.listGen)
            {
                if(gn.fitness > sp.topFitness)
                {
                    sp.topFitness = gn.fitness;
                    sp.topGen = gn;
                }
            }
        }
        
        //System.out.println("Species size: " + listSpecies.size());
        //Remove half bottom of species
        //Rank Globally
        //Remove stale species
        removeStaleSpecies();
        //Rank Globally
        //Calculate average fitness of each species
        calculateSpeciesAverageFitness(listSpecies);
        //Remove weak species
        //System.out.println("numSpecies: " + listSpecies.size());
        if(listSpecies.size() < numSpecies)
        {
            //Remove dominant species
            int maxOrganisms = getPopulation()/numSpecies;
            for(int i = 0; i < listSpecies.size(); ++i)
            {
                if(listSpecies.get(i).listGen.size() > maxOrganisms)
                {
                    sortByFitness(listSpecies.get(i).listGen, false);
                    while(listSpecies.get(i).listGen.size() > maxOrganisms/2)
                    {
                        listSpecies.get(i).listGen.remove(listSpecies.get(i).listGen.size()-1);
                    }
                }
            }
            
        }
        //Perform crossover + mutate and remove weak genomes in each species
//            //CROSSOVER
//        if(newGeneration.size() < getPopulation())
//            for(int i = 0; i < listSpecies.size(); ++i)
//            {
//                if(newGeneration.size() >= getPopulation())
//                    break;
//                if(listSpecies.get(i).listGen.size() > 1)
//                {
//                    crossover(listSpecies.get(i).listGen, newGeneration);
//                }
//                listSpecies.get(i).staleness++;
//            }
//        
//        //Add top gens from survived species
//        if(newGeneration.size() < getPopulation())
//        {
//            for(Species sp : listSpecies)
//            {
//                if(sp.listGen.size() > 0)
//                {
//                    newGeneration.add(new Genome(sp.topGen));
//                    if(newGeneration.size() >= getPopulation())
//                        break;
//                }
//            }
//        }
        
        for(int i = 0; i < listSpecies.size(); ++i)
        {
            //System.out.println("Before reproduce: " + listSpecies.get(i).listGen.size());
            reproduce(listSpecies.get(i));
            //System.out.println("After reproduce: " + listSpecies.get(i).listGen.size());
            listSpecies.get(i).staleness++;
            newGeneration.addAll(listSpecies.get(i).listGen);
        }
        //System.out.println(newGeneration.size());
        //Create new random gens to fill up the population.
        while(newGeneration.size() < getPopulation())
        {
            Genome gen = new Genome();
            gen.identity = newGeneration.size();
            for(int j = 0; j < getnInput(); ++j)
            {
                gen.newNode(Layer.INPUT, j, -1);
            }
            for(int j = 0; j < getnOutput(); ++j)
            {
                gen.newNode(Layer.OUTPUT, j, -1);
            }
            mutation(gen);
            newGeneration.add(gen);
        }
//        for(int i = 1; i < newGeneration.size(); ++i)
//        {
//            mutation(newGeneration.get(i));
//        }
        
        listGens.clear();
        listGens = newGeneration;
        for(int i = 0; i < listSpecies.size(); ++i)
        {
            listSpecies.get(i).listGen.clear();
        }
        //System.out.println("ListGens: " + listGens.size());
    }
    
    public void reproduce(Species sp)
    {
        int maxOrganism = sp.listGen.size();
        Random rand = new Random();
        List<Genome> halftop = new ArrayList<>();
        if(sp.listGen.size() > 1)
            for(int i = 0; i < sp.listGen.size()/2; ++i)
            {
                halftop.add(new Genome(sp.listGen.get(i)));
            }
//        System.out.println("Halftop1 size:" + halftop.size());
        //System.out.println("SpeciesListGen1 size:" + sp.listGen.size());
        //Crossover
        if(rand.nextDouble() < getCrossOverChance() && halftop.size() > 1)
        {
            sp.listGen.clear();
            crossover(halftop, sp.listGen);
        }
        //Mutation
//        System.out.println("Halftop2 size:" + halftop.size());
        //System.out.println("SpeciesListGen2 size:" + sp.listGen.size());
        if(sp.listGen.size() < maxOrganism)
        {
            for(int i = 1; i < sp.listGen.size(); ++i)
            {
                //if(rand.nextDouble() < getMutationChance())
                {
                    Genome tmp = new Genome(sp.listGen.get(i));
                    mutation(tmp);
                    sp.listGen.add(tmp);
                    if(sp.listGen.size() >= maxOrganism)
                        break;
                }
            }
        }
        else
        {
            for(int i = 1; i < sp.listGen.size(); ++i)
            {
                mutation(sp.listGen.get(i));
            }
        }
    }
    
    public void seperateSpecies(List<Genome> lis)
    {
        if(listSpecies == null)
        {
            List<Species> listSpecies = new ArrayList<>();
        }
        for(int i = 0; i < lis.size(); ++i)
        {
            // If there is no species
            if(listSpecies.isEmpty())
            {
                Species newSpecies = new Species(0);
                newSpecies.listGen.add(lis.get(i));
                newSpecies.topGen = lis.get(i);
                listSpecies.add(newSpecies);
            }
            else
            {
                boolean isNewSpecies = true;
                for(int j = 0; j < listSpecies.size(); ++j)
                {
                    //Calculate compability between two gens.
                    double tmpSigma = calCompability(lis.get(i), listSpecies.get(j).topGen);
                    //System.out.println("Compability: " + tmpSigma);
                    //If same species
                    if(tmpSigma < sigma)
                    {
                        //Put into the same species
                        listSpecies.get(j).listGen.add(lis.get(i));
                        isNewSpecies = false;
                        break;
                    }
                }
                //If this genome is a new species
                if(isNewSpecies)
                {
                    Species newSpecies = new Species(listSpecies.size());
                    newSpecies.listGen.add(lis.get(i));
                    newSpecies.topGen = lis.get(i);
                    listSpecies.add(newSpecies);
                }
            }
        }
    }
    
    public void removeStaleSpecies()
    {
        for(int i = 0; i < listSpecies.size(); ++i)
        {
            if(listSpecies.get(i).staleness > getLiveTime())
            {
                listSpecies.remove(i);
                i--;
            }
        }
    }
    
    public double calCompability(Genome a, Genome b)
    {
        int nDisjoin = 0;
        int nExcess = 0;
        //Line up
        int maxInnoA = 0;
        int maxInnoB = 0;
        int maxInno = 0;
        int minInno = 100000;
        double result = 0;
        int N = 1;
        double W = 0;
        List<Connection> genA = new ArrayList<>();
        List<Connection> genB = new ArrayList<>();
        //get max inno of A and B
        if(a.listConnections.size() > 0 && b.listConnections.size() > 0)
        {
            for(Connection tmp : a.listConnections)
            {
                if(tmp.inno > maxInnoA)
                    maxInnoA = tmp.inno;
                if(tmp.inno < minInno)
                    minInno = tmp.inno;
            }
            for(Connection tmp : b.listConnections)
            {
                if(tmp.inno > maxInnoB)
                    maxInnoB = tmp.inno;
                if(tmp.inno < minInno)
                    minInno = tmp.inno;
            }
            
            if(maxInnoA > maxInnoB)
            {
                maxInno = maxInnoA;
            }
            else
            {
                maxInno = maxInnoB;
            }
            for(int i = minInno; i <= maxInno; ++i)
            {
                boolean exists = false;
                for(int j = 0; j < a.listConnections.size(); ++j)
                {
                    if(a.listConnections.get(j).inno == i)
                    {
                        genA.add(new Connection(a.listConnections.get(j)));
                        exists = true;
                        break;
                    }
                }
                if(!exists)
                    genA.add(null);
                exists = false;
                for(int j = 0; j < b.listConnections.size(); ++j)
                {
                    if(b.listConnections.get(j).inno == i)
                    {
                        genB.add(new Connection(b.listConnections.get(j)));
                        exists = true;
                        break;
                    }
                }
                if(!exists)
                    genB.add(null);
            }
            int coincident = 0;
            for(int i = 0; i < genA.size(); ++i)
            {
                if(genA.get(i) != null && genB.get(i) != null)
                {
                    W += Math.abs(genA.get(i).w - genB.get(i).w);
                    coincident++;
                }
                if(genA.get(i) != null && genB.get(i) == null)
                {
                    if(i <= minInno)
                        nDisjoin++;
                    else
                        nExcess++;
                }
                else if(genA.get(i) == null && genB.get(i) != null)
                {
                    if(i <= minInno)
                        nDisjoin++;
                    else
                        nExcess++;
                }
            }
            if(coincident > 0)
                W = W/coincident;
        }
        //System.out.println("Disjoin: " + nDisjoin + " Excess:" + nExcess + " !!!!!!!!!!!!!!!!!!!");
        //System.out.println("Disjoin: " + (c2*nDisjoin)/N + " Excess:" + (c1*nExcess)/N + " W:" + W + " !!!!!!!!!!!!!!!!!!!");
        if(a.listConnections.size() > 20)
            N = a.listConnections.size();
        result = (c1*nExcess)/N + (c2*nDisjoin)/N + c3*W;
        return result;
    }
    
    public void calculateSpeciesAverageFitness(List<Species> lis)
    {
        for(Species tmp : lis)
        {
            double sum = 0;
            for(Genome tmpGen : tmp.listGen)
            {
                sum += tmpGen.fitness;
            }
            sum = sum/tmp.listGen.size();
            if(sum > tmp.topFitness)
                tmp.topFitness = sum;
            else
                tmp.staleness++;
        }
    }
    
    public void adjustingFitness(List<Genome> lis)
    {
        double sum = 1;
        for(Genome tmp1 : lis)
        {
            for(Genome tmp2 : lis)
            {
                if(calCompability(tmp1, tmp2) > sigma)
                {
                    sum++;
                }
            }
            tmp1.fitness = tmp1.fitness/sum;
        }
    }
    
    public void sortByFitness(List<Genome> lis, boolean flag)
    {
        List<Genome> result = new ArrayList<>();
        if(!flag) //Decreasing fitness
        {
            while(lis.size() > 0)
            {
                Genome tmp = lis.get(0);
                int idx = 0;
                for(int i = 0; i < lis.size(); ++i)
                {
                    if(lis.get(i).fitness > tmp.fitness)
                    {
                        tmp = lis.get(i);
                        idx = i;
                    }
                }
                result.add(tmp);
                lis.remove(idx);
            }
        }
        else //Increasing fitness
        {
            while(lis.size() > 0)
            {
                Genome tmp = lis.get(0);
                int idx = 0;
                for(int i = 0; i < lis.size(); ++i)
                {
                    if(lis.get(i).fitness < tmp.fitness)
                    {
                        tmp = lis.get(i);
                        idx = i;
                    }
                }
                result.add(tmp);
                lis.remove(idx);
            }
        }
        lis.addAll(result);
    }
    
    public int getIdxOfWorstGen()
    {
        int result = -1;
        double fitness = listGens.get(0).fitness;
        for(int i = 0; i < listGens.size(); ++i)
        {
            if(fitness <= listGens.get(i).fitness)
            {
                fitness = listGens.get(i).fitness;
                result = i;
            }
        }
        return result;
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
