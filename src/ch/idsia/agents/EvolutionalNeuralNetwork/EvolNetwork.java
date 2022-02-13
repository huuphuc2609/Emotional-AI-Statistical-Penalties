/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork;

import ch.idsia.agents.EvolutionalNeuralNetwork.Agent.NEATAgent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Phuc
 */
public class EvolNetwork {
    
    private int nInput;
    private int nOutput;
    int genCount = 0;
    
    private int liveTime;
    private int population;
    private int numSpecies;
    
    private int generation;
    private int innovationNumber;
    private int currentIdentity;
    
    private int maximumHiddenNode;
    
    private double overallFitness;
    private double overallNaturalness;
    
    private double stepSize;
    
    private int deltaDisjoint;
    private int deltaWeights;
    private double deltaThreshold;
    
    private double mutationChance;
    
    private double addConnectionChance;
    private double connectionWeightPerturbationChance;
    
    private double crossOverChance;
    
    private double nodeMutationChance;
    private double biasMutationChance;
    
    private double DisableMutationChance;
    private double EnableMutationChance;
    
    public Genome activatedGen;
    public List<Connection> distincGens;
    public List<Genome> listGens;
    
    public EvolNetwork() {
        nInput = -1;
        nOutput = -1;
        liveTime = 0;
        population = 0;
        generation = 0;
        numSpecies = 1;
        innovationNumber = 0;
        currentIdentity = -1;
        
        maximumHiddenNode = 0;
        
        overallFitness = 0;
        
        stepSize = 1;
                
        deltaThreshold = 1;
        
        mutationChance = 0;
        
        addConnectionChance = 0.8;
        connectionWeightPerturbationChance = 0.9; //10% assigned new random value.
        crossOverChance = 0.5;
        nodeMutationChance = 0.5;
        biasMutationChance = 0.5;
        
        DisableMutationChance = 0.4;
        EnableMutationChance = 0.2;
        
        activatedGen = null;
        distincGens = new ArrayList<>();
        listGens = new ArrayList<>();
    }
    
    public EvolNetwork(EvolNetwork enw) {
        nInput = enw.nInput;
        nOutput = enw.nOutput;
        liveTime = enw.liveTime;
        population = enw.population;
        generation = enw.generation;
        numSpecies = enw.numSpecies;
        innovationNumber = enw.innovationNumber;
        currentIdentity = enw.currentIdentity;
        
        maximumHiddenNode = enw.maximumHiddenNode;
        
        overallFitness = enw.overallFitness;
        
        stepSize = enw.stepSize;
                
        deltaThreshold = enw.deltaThreshold;
        
        mutationChance = enw.mutationChance;
        
        addConnectionChance = enw.addConnectionChance;
        connectionWeightPerturbationChance = enw.connectionWeightPerturbationChance; //10% assigned new random value.
        crossOverChance = enw.crossOverChance;
        nodeMutationChance = enw.nodeMutationChance;
        biasMutationChance = enw.biasMutationChance;
        
        DisableMutationChance = enw.DisableMutationChance;
        EnableMutationChance = enw.EnableMutationChance;
        
        activatedGen = enw.activatedGen;
        distincGens = new ArrayList<>();
        distincGens.addAll(enw.distincGens);
        listGens = new ArrayList<>();
        listGens.addAll(enw.listGens);
    }
    
    public double getOverallFitness()
    {
        overallFitness = 0;
        for(Genome tmp : listGens)
        {
            overallFitness += tmp.fitness;
        }
        overallFitness = overallFitness/listGens.size();
//        for(Genome tmp : listGens)
//        {
//            if(tmp.fitness >= overallFitness)
//                overallFitness = tmp.fitness;
//        }
        return overallFitness;
    }
    
    public double getBestFitness()
    {
        double result = -100000;
        for(Genome tmp : listGens)
        {
            if(tmp.fitness >= result)
                result = tmp.fitness;
        }
        return result;
    }
    
    public double getOverallNaturalness()
    {
        overallNaturalness = 0;
        for(Genome tmp : listGens)
        {
            overallNaturalness += tmp.naturalness;
        }
        overallNaturalness = overallNaturalness/listGens.size();
//        for(Genome tmp : listGens)
//        {
//            if(tmp.fitness >= overallFitness)
//                overallFitness = tmp.fitness;
//        }
        return overallNaturalness;
    }
    
    public double getBestNaturalness()
    {
        double result = 0;
        for(Genome tmp : listGens)
        {
            if(tmp.naturalness >= result)
                result = tmp.naturalness;
        }
        return result;
    }
    
    public void initialize() {
        //Creating input and output nodes
        for(int i = 0; i < getPopulation(); ++i)
        {
            Genome gen = new Genome();
            gen.identity = i;
            for(int j = 0; j < getnInput(); ++j)
            {
                gen.newNode(Layer.INPUT, j, -1);
            }
            for(int j = 0; j < getnOutput(); ++j)
            {
                gen.newNode(Layer.OUTPUT, j, -1);
            }
            gen.eta = 0.01;
            listGens.add(gen);
        }
        
        //Randoming connections
        Random rand = new Random();
        for(int i = 0; i < listGens.size(); ++i)
        {
            Connection c = new Connection(Layer.INPUT, Layer.OUTPUT, rand.nextInt(getnInput()), rand.nextInt(getnOutput()), rand.nextDouble()*4-2, getInnovationNumber());
            c = checkDistinct(c);

            int coin = rand.nextInt(2);
            if(coin == 1)
                listGens.get(i).addConnection(c);
        }
        
//        for(int i = 0; i < getPopulation(); ++i)
//        {
//            listGens.get(i).showGenInfo();
//        }
    }
    
    public Genome initGenome()
    {
        Genome gen = new Genome();
        for(int j = 0; j < getnInput(); ++j)
        {
            gen.newNode(Layer.INPUT, j, -1);
        }
        for(int j = 0; j < getMaximumHiddenNode(); ++j)
        {
            gen.newNode(Layer.HIDDEN, j, -1);
        }
        for(int j = 0; j < getnOutput(); ++j)
        {
            gen.newNode(Layer.OUTPUT, j, -1);
        }
        
        return gen;
    }
    
    public Connection getConnectionFromDistinc(Connection c)
    {
        for(Connection tmp : distincGens)
        {
            if(c.equals(tmp))
                return new Connection(tmp);
        }
        return c;
    }
    
    public boolean checkDistincGen(Connection c)
    {
        boolean isExists = false;
        for(Connection tmp : distincGens)
        {
            if(tmp.equals(c))
            {
                isExists = true;
                break;
            }
        }
        return isExists;
    }
    
    public synchronized Connection checkDistinct(Connection c)
    {
        if(!checkDistincGen(c))
        {
            c.inno = getInnovationNumber();
            setInnovationNumber(getInnovationNumber() + 1);
            distincGens.add(new Connection(c));
        }
        else
        {
            c = getConnectionFromDistinc(c);
        }
        return c;
    }
    
    public void removeDistinct(Connection c)
    {
        for(int i = 0; i < distincGens.size(); ++i)
        {
            if(c.equals(distincGens.get(i)))
            {
                distincGens.remove(i);
                break;
            }   
        }
    }
    
    public void nextGeneration()
    {
        
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
    
    public void setActiveGen(int i) {
        activatedGen = listGens.get(i);
        if(NEATAgent.debug)
        {
            activatedGen.showGenInfo();
            System.out.println("");
        }
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
    
    public void sortByFitness(List<Genome> lis, boolean flag)
    {
        List<Genome> result = new ArrayList<>();
        if(!flag) //Decreasing fitness
        {
            while(lis.size() > 0)
            {
                Genome tmp = new Genome(lis.get(0));
                int idx = 0;
                for(int i = 0; i < lis.size(); ++i)
                {
                    if(lis.get(i).fitness > tmp.fitness)
                    {
                        tmp = new Genome(lis.get(i));
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
                Genome tmp = new Genome(lis.get(0));
                int idx = 0;
                for(int i = 0; i < lis.size(); ++i)
                {
                    if(lis.get(i).fitness < tmp.fitness)
                    {
                        tmp = new Genome(lis.get(i));
                        idx = i;
                    }
                }
                result.add(tmp);
                lis.remove(idx);
            }
        }
        lis.addAll(result);
    }
    
    //**************************Getters & Setters*******************************

    /**
     * @return the nInput
     */
    public int getnInput() {
        return nInput;
    }

    /**
     * @param nInput the nInput to set
     */
    public void setnInput(int nInput) {
        this.nInput = nInput;
    }

    /**
     * @return the nOutput
     */
    public int getnOutput() {
        return nOutput;
    }

    /**
     * @param nOutput the nOutput to set
     */
    public void setnOutput(int nOutput) {
        this.nOutput = nOutput;
    }

    /**
     * @return the liveTime
     */
    public int getLiveTime() {
        return liveTime;
    }

    /**
     * @param liveTime the liveTime to set
     */
    public void setLiveTime(int liveTime) {
        this.liveTime = liveTime;
    }

    /**
     * @return the population
     */
    public int getPopulation() {
        return population;
    }

    /**
     * @param population the population to set
     */
    public void setPopulation(int population) {
        this.population = population;
    }

    /**
     * @return the generation
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * @param generation the generation to set
     */
    public void setGeneration(int generation) {
        this.generation = generation;
    }

    /**
     * @return the innovationNumber
     */
    public int getInnovationNumber() {
        return innovationNumber;
    }

    /**
     * @param innovationNumber the innovationNumber to set
     */
    public void setInnovationNumber(int innovationNumber) {
        this.innovationNumber = innovationNumber;
    }

    /**
     * @return the currentIdentity
     */
    public int getCurrentIdentity() {
        return currentIdentity;
    }

    /**
     * @param currentIdentity the currentIdentity to set
     */
    public void setCurrentIdentity(int currentIdentity) {
        this.currentIdentity = currentIdentity;
    }

    /**
     * @return the maximumHiddenNode
     */
    public int getMaximumHiddenNode() {
        return maximumHiddenNode;
    }

    /**
     * @param maximumHiddenNode the maximumHiddenNode to set
     */
    public void setMaximumHiddenNode(int maximumHiddenNode) {
        this.maximumHiddenNode = maximumHiddenNode;
    }

    /**
     * @param overallFitness the overallFitness to set
     */
    public void setOverallFitness(double overallFitness) {
        this.overallFitness = overallFitness;
    }

    /**
     * @return the deltaThreshold
     */
    public double getDeltaThreshold() {
        return deltaThreshold;
    }

    /**
     * @param deltaThreshold the deltaThreshold to set
     */
    public void setDeltaThreshold(double deltaThreshold) {
        this.deltaThreshold = deltaThreshold;
    }

    /**
     * @return the mutationChance
     */
    public double getMutationChance() {
        return mutationChance;
    }

    /**
     * @param mutationChance the mutationChance to set
     */
    public void setMutationChance(double mutationChance) {
        this.mutationChance = mutationChance;
    }
  
    /**
     * @return the connectionWeightPerturbationChance
     */
    public double getConnectionWeightPerturbationChance() {
        return connectionWeightPerturbationChance;
    }

    /**
     * @param connectionWeightPerturbationChance the connectionWeightPerturbationChance to set
     */
    public void setConnectionWeightPerturbationChance(double connectionWeightPerturbationChance) {
        this.connectionWeightPerturbationChance = connectionWeightPerturbationChance;
    }

    /**
     * @return the crossOverChance
     */
    public double getCrossOverChance() {
        return crossOverChance;
    }

    /**
     * @param crossOverChance the crossOverChance to set
     */
    public void setCrossOverChance(double crossOverChance) {
        this.crossOverChance = crossOverChance;
    }

    /**
     * @return the nodeMutationChance
     */
    public double getNodeMutationChance() {
        return nodeMutationChance;
    }

    /**
     * @param nodeMutationChance the nodeMutationChance to set
     */
    public void setNodeMutationChance(double nodeMutationChance) {
        this.nodeMutationChance = nodeMutationChance;
    }

    /**
     * @return the biasMutationChance
     */
    public double getBiasMutationChance() {
        return biasMutationChance;
    }

    /**
     * @param biasMutationChance the biasMutationChance to set
     */
    public void setBiasMutationChance(double biasMutationChance) {
        this.biasMutationChance = biasMutationChance;
    }

    /**
     * @return the DisableMutationChance
     */
    public double getDisableMutationChance() {
        return DisableMutationChance;
    }

    /**
     * @param DisableMutationChance the DisableMutationChance to set
     */
    public void setDisableMutationChance(double DisableMutationChance) {
        this.DisableMutationChance = DisableMutationChance;
    }

    /**
     * @return the EnableMutationChance
     */
    public double getEnableMutationChance() {
        return EnableMutationChance;
    }

    /**
     * @param EnableMutationChance the EnableMutationChance to set
     */
    public void setEnableMutationChance(double EnableMutationChance) {
        this.EnableMutationChance = EnableMutationChance;
    }

    /**
     * @return the stepSize
     */
    public double getStepSize() {
        return stepSize;
    }

    /**
     * @param stepSize the stepSize to set
     */
    public void setStepSize(double stepSize) {
        this.stepSize = stepSize;
    }

    /**
     * @return the addConnectionChance
     */
    public double getAddConnectionChance() {
        return addConnectionChance;
    }

    /**
     * @param addConnectionChance the addConnectionChance to set
     */
    public void setAddConnectionChance(double addConnectionChance) {
        this.addConnectionChance = addConnectionChance;
    }

} 
