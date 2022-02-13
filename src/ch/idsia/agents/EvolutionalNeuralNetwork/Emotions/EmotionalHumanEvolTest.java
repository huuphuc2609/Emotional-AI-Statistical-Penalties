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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Phuc
 * Training Emotional AI using Human data
 */
public class EmotionalHumanEvolTest {
       
    public static void main(String[] args) throws InterruptedException {
        
        long startTime = System.currentTimeMillis();
        final String argsString = "-trace on";
        //final String argsString = "-le off -lb off";
        final MarioAIOptions marioAIOptions = new MarioAIOptions(argsString);
        //final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
        //marioAIOptions.setEnemies("off");
        marioAIOptions.setLevelRandSeed(0);
        double overallFitness = -10000;
        double bestFitness = 0;
        double overallNaturalness = 0;
        double bestNaturalness = 0;
        //CustomNetwork net = new CustomNetwork();
        CustomNetwork net = new CustomNetwork();
        
        int popSize = 50;
        net.setPopulation(popSize);
        net.setMaximumHiddenNode(10);
        net.setMaximumHiddenNode(50);
        net.setStepSize(0.1);
        
        net.setMutationChance(1.0);
        net.setNodeMutationChance(0.5);
        net.setAddConnectionChance(0.5);
        
        net.setConnectionWeightPerturbationChance(0.9);
        net.setBiasMutationChance(0.5);
               
        
        //net.setnInput(29);
        net.setnOutput(6);
        
        
        final EmotionalAgent agent = new EmotionalAgent();
        agent.useEmotion = false;
        agent.isShiftRight = false;
        agent.useEnemiesInfo = true;
        if(agent.useEmotion)
        {
            //net.setnInput(53+3);
            net.setnInput(53+3+6+1);
            //net.setnInput(29+3);
        }   
        else
        {
            //net.setnInput(53);
            net.setnInput(53+6+1);
            //net.setnInput(29);
        }
        net.initialize();
        agent.nn = net;
        
        agent.setnInput(7);
        //agent.setnInput(5);
        agent.initializeEmotionUnit();
        
        //NEATAgent.debug = true;
        //marioAIOptions.setReceptiveFieldHeight(7);
        //marioAIOptions.setReceptiveFieldWidth(7);
        
        marioAIOptions.setReceptiveFieldHeight(19);
        marioAIOptions.setReceptiveFieldWidth(19);
       
        System.out.println(agent.nn.listGens.size());
        marioAIOptions.setAgent(agent);
        
        marioAIOptions.setFPS(GlobalOptions.MaxFPS);
        marioAIOptions.setVisualization(false);
        marioAIOptions.setLevelRandSeed(0);
        
        final BasicTask basicTask = new BasicTask(marioAIOptions);
        
        List<HashMap<EState,EAction>> listHashMap = new ArrayList<>();
        List<List<double[]>> listTrace = new ArrayList<>();
        List<List<EStateAction>> listSA = new ArrayList<>();
        
        //****************Read Human Data****************
        int noH = 3; //Number of human players
//        int noDif = 2;
//        int noLev = 61;
        int noDif = 1;
        int noLev = 1;
        String[] playerName = new String[noH];
        playerName[0] = "Human1";
        playerName[1] = "Human2";
        playerName[2] = "Human3";
        //playerName[0] = "Phuc1";
        //playerName[1] = "Phuc2";
        //playerName[1] = "Duy";
        for(int n = 0; n < noH; ++n)
        {
            String name = playerName[n];
            String folderName = playerName[n] + "_Dif1_Lev1"; //Dif lev
            String currentDir = "";
            try {
                currentDir = new java.io.File( "." ).getCanonicalPath();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            String folder = currentDir + "\\HumanTraceData\\"+folderName+"\\";
            
            int dif = 1;
            int lev = 0;
            for(int times = 0; times < 3; ++times)
            {
                HashMap<EState, EAction> humanTrack = new HashMap<>();
                try
                {
                    //BufferedReader br = new BufferedReader(new FileReader(folder+"hashmapHumanPlay"+name+"_"+dif+"_"+lev+"_"+times));
                    BufferedReader br = new BufferedReader(new FileReader(folder+"hashmapHumanPlay"+name+"_"+dif+"_"+lev+"_"+times));
                    String line = br.readLine();
                    while(line != null)
                    {
                        String[] record = line.split("###");
                        EState tmpS = (EState) new Gson().fromJson(record[0], EState.class);
                        EAction tmpA = (EAction) new Gson().fromJson(record[1], EAction.class);
                        humanTrack.put(tmpS, tmpA);
                        line = br.readLine();
                    }
                    br.close();
                    //System.out.println("Human track: " + humanTrack.size());
                }
                catch(IOException ex)
                {
                    System.out.println(ex.getMessage());
                }

                //****************Read trace from human data****************
                List<double[]> humanTrace = new ArrayList<>();
                try
                {
                    FileReader fr = new FileReader(folder+"trace"+playerName[n]+"_"+dif+"_"+lev+"_"+times+".txt");
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

                //****************Read seq from human data******************
                List<EStateAction> stateAction = new ArrayList<>();
                try
                {
                    FileReader fr = new FileReader(folder+"seqActionHumanPlay"+playerName[n]+"_"+dif+"_"+lev+"_"+times);
                    BufferedReader br = new BufferedReader(fr);
                    for(String line; (line = br.readLine()) != null; ) 
                    {
                        // process the line.
                        EStateAction esa = (EStateAction) new Gson().fromJson(line,EStateAction.class);
                        stateAction.add(esa);
                    }
                    br.close();
                    fr.close();
                }
                catch(IOException e)
                {
                    System.out.println(e.getMessage());
                }

                listHashMap.add(humanTrack);
                listTrace.add(humanTrace);
                listSA.add(stateAction);
            }
//            for(int dif = 0; dif < noDif; ++dif)
//            {
//                //for(int lev = 0; lev < noLev; lev+=15)
//                for(int lev = 0; lev < noLev; lev+=1)
//                {                    
//                    for(int times = 0; times < 3; ++times)
//                    {
//                        //****************Read hashmap from file****************
//                        HashMap<EState, EAction> humanTrack = new HashMap<>();
//                        try
//                        {
//                            BufferedReader br = new BufferedReader(new FileReader(folder+"hashmapHumanPlay"+name+"_"+dif+"_"+lev+"_"+times));
//                            String line = br.readLine();
//                            while(line != null)
//                            {
//                                String[] record = line.split("###");
//                                EState tmpS = (EState) new Gson().fromJson(record[0], EState.class);
//                                EAction tmpA = (EAction) new Gson().fromJson(record[1], EAction.class);
//                                humanTrack.put(tmpS, tmpA);
//                                line = br.readLine();
//                            }
//                            br.close();
//                            //System.out.println("Human track: " + humanTrack.size());
//                        }
//                        catch(IOException ex)
//                        {
//                            System.out.println(ex.getMessage());
//                        }
//
//                        //****************Read trace from human data****************
//                        List<double[]> humanTrace = new ArrayList<>();
//                        try
//                        {
//                            FileReader fr = new FileReader(folder+"trace"+playerName[n]+"_"+dif+"_"+lev+"_"+times+".txt");
//                            BufferedReader br = new BufferedReader(fr);
//                            for(String line; (line = br.readLine()) != null; ) 
//                            {
//                                // process the line.
//                                double[] val = new double[2];
//                                String[] tmp = line.split(" ");
//                                val[0] = Double.parseDouble(tmp[0]);
//                                val[1] = Double.parseDouble(tmp[1]);
//                                humanTrace.add(val);
//                            }
//                            br.close();
//                            fr.close();
//                        }
//                        catch(IOException e)
//                        {
//                            System.out.println(e.getMessage());
//                        }
//
//                        //****************Read seq from human data******************
//                        List<EStateAction> stateAction = new ArrayList<>();
//                        try
//                        {
//                            FileReader fr = new FileReader(folder+"seqActionHumanPlay"+playerName[n]+"_"+dif+"_"+lev+"_"+times);
//                            BufferedReader br = new BufferedReader(fr);
//                            for(String line; (line = br.readLine()) != null; ) 
//                            {
//                                // process the line.
//                                EStateAction esa = (EStateAction) new Gson().fromJson(line,EStateAction.class);
//                                stateAction.add(esa);
//                            }
//                            br.close();
//                            fr.close();
//                        }
//                        catch(IOException e)
//                        {
//                            System.out.println(e.getMessage());
//                        }
//
//                        listHashMap.add(humanTrack);
//                        listTrace.add(humanTrace);
//                        listSA.add(stateAction);
//                    }
//                }
//            }
        }
        //System.out.println(listHS.size());
        System.out.println("HashMap/Trace: "+listHashMap.size() + "/" + listTrace.size() + "/" + listSA.size());
//        for(int i = 0; i < humanTrace.size(); ++i)
//        {
//            System.out.println(humanTrace.get(i)[0] + " " + humanTrace.get(i)[1]);
//        }
//        for(int i = 0; i < listHashMap.size(); ++i)
//        {
//            HashMap<EState, EAction> tmp = listHashMap.get(i);
//            List<EAction> lisA = new ArrayList<EAction>(tmp.values());
//            for(EAction a : lisA)
//            {
//                for(int j = 0; j < a.action.length; ++j)
//                {
//                    if(a.action[j])
//                        System.out.print("1");
//                    else
//                        System.out.print("0");
//                }
//                System.out.println("");
//            }
//        }
//        for(int i = 0; i < listSA.size(); ++i)
//        {
//            List<EStateAction> tmp = listSA.get(i);
//            for(EStateAction a : tmp)
//            {
//                System.out.println(a.toString());
//            }
//        }
        System.out.println("Learning...");
        int loop = 0;
        int numOfChild = 10;
        while(loop < 2500)
        {   
            //*************************Evol advanced option*************************
            List<Genome> listNew = new ArrayList<>();
            //Random shuffle           
            Collections.shuffle(agent.nn.listGens);
            for(int i = 0; i < agent.nn.listGens.size(); i+=2)
            {
                //Pairing
                Genome p1 = agent.nn.listGens.get(i);
                Genome p2 = agent.nn.listGens.get(i+1);
                
                //****************P1****************
                evaluateGenome(p1, agent, listHashMap, listTrace, listSA, marioAIOptions, basicTask);
                //****************P2****************
                evaluateGenome(p2, agent, listHashMap, listTrace, listSA, marioAIOptions, basicTask);
                
                List<Genome> child1 = new ArrayList<>();
                
                //****************P1's children****************
                for(int j = 0; j < numOfChild; ++j)
                {
                    Genome newGen = new Genome(p1);
                    //((CustomNetwork)agent.nn).mutation(newGen);
                    ((CustomNetwork)agent.nn).mutationWithRemove(newGen);
                    child1.add(newGen);
                    evaluateGenome(newGen, agent, listHashMap, listTrace, listSA, marioAIOptions, basicTask);
                }
                //****************P2's children****************
                for(int j = 0; j < numOfChild; ++j)
                {
                    Genome newGen = new Genome(p2);
                    //((CustomNetwork)agent.nn).mutation(newGen);
                    ((CustomNetwork)agent.nn).mutationWithRemove(newGen);
                    child1.add(newGen);
                    evaluateGenome(newGen, agent, listHashMap, listTrace, listSA, marioAIOptions, basicTask);
                }
                
                int p1i = -1;
                for(int j = 0; j < child1.size(); ++j)
                {
//                    if(child1.get(j).fitness >= p1.fitness && child1.get(j).naturalness >= p1.naturalness
//                            || child1.get(j).fitness >= p1.fitness && child1.get(j).naturalness == p1.naturalness
//                            || child1.get(j).fitness == p1.fitness && child1.get(j).naturalness >= p1.naturalness)
                    if(child1.get(j).fitness > p1.fitness)
                    //if(child1.get(j).naturalness > p1.naturalness)
                    {
                        p1 = child1.get(j);
                        p1i = j;
                    }
                }
                for(int j = 0; j < child1.size(); ++j)
                {
                    if(p1i != j)
                    {
//                        if(child1.get(j).fitness >= p2.fitness && child1.get(j).naturalness >= p2.naturalness
//                            || child1.get(j).fitness >= p2.fitness && child1.get(j).naturalness == p2.naturalness
//                                || child1.get(j).fitness == p2.fitness && child1.get(j).naturalness >= p2.naturalness)
                        if(child1.get(j).fitness > p2.fitness)
                        //if(child2.get(j).naturalness > p2.naturalness)
                        {
                            p2 = child1.get(j);
                        }
                    }
                }
                listNew.add(new Genome(p1));
                listNew.add(new Genome(p2));
                child1.clear();
                //child2.clear();
            }
            agent.nn.listGens.clear();
            agent.nn.listGens.addAll(listNew);
            
            //if(agent.nn.getOverallFitness() > overallFitness)
            {
                //Save best genome
                double fitness = -10000;
                double naturalness = 0;
                Genome crbestFit = null;
                Genome crbestNat = null;
                for(int i = 0; i < agent.nn.listGens.size(); ++i)
                {
                    if(agent.nn.listGens.get(i).fitness >= fitness)
                    {
                        fitness = agent.nn.listGens.get(i).fitness;
                        crbestFit = new Genome(agent.nn.listGens.get(i));
                    }
                    if(agent.nn.listGens.get(i).naturalness >= naturalness)
                    {
                        naturalness = agent.nn.listGens.get(i).naturalness;
                        crbestNat = new Genome(agent.nn.listGens.get(i));
                    }
                }
                
                writeGenomeToFile(crbestFit,"OldFeatureFit10levels_"+String.valueOf(popSize));
                writeGenomeToFile(crbestNat,"OldFeatureNat10levels_"+String.valueOf(popSize));
            
                overallFitness = agent.nn.getOverallFitness();
                bestFitness = agent.nn.getBestFitness();
                overallNaturalness = agent.nn.getOverallNaturalness();
                bestNaturalness = agent.nn.getBestNaturalness();
                
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                
                if(crbestFit != null)
                {
                    System.out.print("Iteration#" + loop + ": ");
                    System.out.print("Fitness: " + overallFitness + " " + bestFitness + " Naturalness: " + overallNaturalness + " " + bestNaturalness + " -- " + dateFormat.format(date));
                    System.out.print(" FITNESS: Jump=" + crbestFit.jumpTimes + " " + crbestFit.fitness+"/"+crbestFit.naturalness + " Nodes: " + crbestFit.listHiddenNodes.size() + " connections: " + crbestFit.listConnections.size() + " bias connection: " + crbestFit.listBiasConnections.size());
                    System.out.println(" NATURALNESS: Jump=" + crbestNat.jumpTimes + " " + crbestNat.fitness+"/"+crbestNat.naturalness + " Nodes: " + crbestNat.listHiddenNodes.size() + " connections: " + crbestNat.listConnections.size() + " bias connection: " + crbestNat.listBiasConnections.size());
                }
                else
                    System.out.println(overallFitness + " " + bestFitness + " " + overallNaturalness + " " + bestNaturalness + " -- " + dateFormat.format(date));
                if(basicTask.isFinished() && bestFitness > 800000)
                    break;
            }
            loop++;
        }
        
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
        int numFrame = 4;
        
        //System.out.println("Size HA:" + ha.size());
        //Sort lists
//        for(int i = 0; i < hs.size(); ++i)
//        {
//            System.out.println(hs.get(i).tick);
//        }
        
        //**********************************************************
//        EAction[] lisA = new EAction[numFrame];
//        EAction[] lisH = new EAction[numFrame];
//        for(int i = 0; i < aa.size()-numFrame; ++i)
//        {
//            //Get 1 seq from AI
//            for(int j = 0; j < numFrame; ++j)
//            {
//                lisA[j] = aa.get(j+i);
//                //System.out.print("AA: " + lisA[j]);
//            }
//            //System.out.println("");
//            for(int j = 0; j < ha.size()-numFrame; ++j)
//            {
//                //Get 1 seq from Human
//                for(int k = 0; k < numFrame; ++k)
//                {
//                    lisH[k] = ha.get(k+j);
//                    //System.out.print("HA: " + lisH[j]);
//                }
//                //Compare
//                boolean sameSeq = true;
//                for(int k = 0; k < lisA.length; ++k)
//                {
//                    if(!lisA[k].equals(lisH[k]))
//                    {
//                        sameSeq = false;
//                        //System.out.println("not same " + lisA[k] + " " + lisH[k]);
//                    }
//                }
//
//                if(sameSeq)
//                {
//                    result++;
//                }
//            }
//        }
        //System.out.println("Score: " + result);
        //**********************************************************
        
        EAction[] lisA = new EAction[numFrame];
        EAction[] lisH = new EAction[numFrame];
        String AI = "";
        String HM = "";
        
        for(int i = 0; i < aa.size(); ++i)
        {
            AI += aa.get(i).toString() + " ";
        }
        
        for(int i = 0; i < ha.size()-numFrame; ++i)
        {
            HM = "";
            //Get 1 seq from Human
            for(int j = 0; j < numFrame; ++j)
            {
                //lisH[k] = ha.get(k+j);
                HM += ha.get(j+i).toString() + " ";
                //System.out.print("HA: " + lisH[j]);
            }
            //Compare
            boolean sameSeq = false;

            if(AI.contains(HM))
            {
                sameSeq = true;
                //System.out.println("not same " + lisA[k] + " " + lisH[k]);
            }

            if(sameSeq)
            {
                result++;
            }
            
        }
        
        return result;
    }
    
    public static double measureState(HashMap<EState,EAction> human, HashMap<EState,EAction> ai, List<EState> aia)
    {
        double result = 0;
        //System.out.println(human.size());
        
        List<EState> lhe = new ArrayList<>();
        
        for(EState he : human.keySet())
        {
            //System.out.println(he.toString());
        }
        //System.out.println("***********************");
        
        for(int i = 0; i < aia.size(); ++i)
        {
            EState e = aia.get(i);
            //System.out.println("State " + e.toString());
            EAction hp = human.get(e);
            
            
            if(hp == null)
            {
                  //System.out.println("hp null");
            }
            EAction aip = ai.get(e);
            if(aip == null)
            {
                  //System.out.println("aip null");
            }
//            if(hp != null)
//                System.out.println(hp.toString() + " COMPARE ");
//            if(aip != null)
//                System.out.println(" COMPARE " + aip.toString());
            if(hp != null && aip != null)
            {
//                System.out.println(hp.toString() + " COMPARE " + aip.toString());
                if(hp.equals(aip))
                    result++;
            }
        }
        //System.out.println(result);
        return result;
    }
    
    public static double compareStateAndAction(List<EStateAction> lsa, EmotionalAgent agent)
    {
        double result = 0;
        
        
        
        for(EStateAction esa : lsa)
        {
            //double[][] inObs = new double[7][7];
            //Convert from data vector to matrix
            //Getting shifting 7x7
            //Adding rest info such as enemies....
            
            
            EAction aia = esa.a;
            EAction ha = new EAction(agent.nn.generateAction(esa.s.dataVector));
            if(ha.equals(aia))
                result++;
        }
        
//        int numStaAct = 5;
//        //Pick every N actions/states 
//        for(int i = 0; i < agent.seqStateAction.size() - numStaAct; i+=numStaAct)
//        {
//            EState[] chainStates = new EState[numStaAct];
//            EAction[] chainActions = new EAction[numStaAct];   
//            for(int j = 0; j < numStaAct; ++j)
//            {
//                chainActions[j] = agent.seqStateAction.get(i+j).a;
//                chainStates[j] = agent.seqStateAction.get(i+j).s; 
//            }
//            
//            List<EStateAction> chain = new ArrayList<>();
//            for(int j = 0; j < numStaAct; ++j)
//            {
//                chain.add(agent.seqStateAction.get(i+j));
//            }
//            
//            lsa.stream().forEach( (sa) -> {
//            
//                if(sa.)
//                
//            }
//            );
//            
//            for(int j = 0; j < lsa.size() - numStaAct; ++j)
//            {
//                boolean isMatch = true;
//                for(int k = 0; k < numStaAct; ++k)
//                {
//                    if(!chain.get(k).equals(lsa.get(j+k)))
//                    {
//                        isMatch = false;
//                        break;
//                    }
//                }
//                if(isMatch)
//                    result++;
//            }
//        }
        return result;
    }
    
    public static void writeGenomeToFile(Genome g, String filename)
    {
        try
        {
           PrintWriter outfile = new PrintWriter(filename);
           outfile.write(g.toString());
           outfile.close();
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    public static Genome readGenomeFromFile(String file)
    {
        Genome result = null;
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            result = new Gson().fromJson(line, Genome.class);
            br.close();
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
        return result;
    }
    
    public static void evaluateGenome(Genome g, EmotionalAgent agent, List<HashMap<EState,EAction>> lHS, List<List<double[]>> lT, List<List<EStateAction>> lSQ, MarioAIOptions option, BasicTask task)
    {   
        int level = 10;
        boolean HMtraceFlag = false;
        boolean HMtrackFlag = false;
        boolean CompareStateActionFlag = false;
        double wfit = 1;
        
        agent.nn.activatedGen = g;
        agent.nn.activatedGen.fitness = 0;
        agent.nn.activatedGen.naturalness = 0;
        option.setLevelDifficulty(1);
        for(int m = 0; m < level; m++)
        {
            agent.numOfJump = 0;
            agent.agentTrace.clear();
            agent.track.clear();
            agent.seqStateAction.clear();
            agent.fear.reset();
            agent.happy.reset();
            agent.curiosity.reset();
            option.setLevelRandSeed(m);
            task.runSingleEpisode(1);
            //agent.nn.activatedGen.fitness += basicTask.getEvaluationInfo().computeWeightedFitness();
            //agent.nn.activatedGen.fitness += task.getEvaluationInfo().computeWeightedFitness();
            agent.nn.activatedGen.fitness += task.getEvaluationInfo().computeDistancePassed()
                                            + task.getEvaluationInfo().coinsGained*10
                                            + task.getEvaluationInfo().mushroomsDevoured*100
                                            + task.getEvaluationInfo().killsByFire*10
                                            + task.getEvaluationInfo().killsByStomp*10
                                            - task.getEvaluationInfo().collisionsWithCreatures*100
                                            + task.getEvaluationInfo().hiddenBlocksFound*100
                                            + task.getEvaluationInfo().marioMode*16;
            if(task.getEvaluationInfo().marioStatus == 0)
                agent.nn.activatedGen.fitness -= 1;
            else if (task.getEvaluationInfo().marioStatus == 1)
                agent.nn.activatedGen.fitness += 100;
            agent.nn.activatedGen.jumpTimes = agent.numOfJump;
            agent.nn.activatedGen.fitness -= agent.nn.activatedGen.jumpTimes*15;
            //Trace fitness
            if(HMtraceFlag)
            {
                double track = 0;
                for(int i1 = 0; i1 < lT.size(); ++i1)
                {
                    double traceError = 0.0;
                    List<double[]> humanTrace = lT.get(i1);
                    for(int t = 0; t < agent.agentTrace.size(); ++t)
                    {
                        if(t >= humanTrace.size())
                            break;
                        double distance = Math.abs(Math.sqrt((humanTrace.get(t)[0] - agent.agentTrace.get(t)[0])*(humanTrace.get(t)[0] - agent.agentTrace.get(t)[0])
                         + (humanTrace.get(t)[1] - agent.agentTrace.get(t)[1])*(humanTrace.get(t)[1] - agent.agentTrace.get(t)[1])));
                        track += measureDistance(distance);
                    }
                }
                agent.nn.activatedGen.naturalness += track/lT.size();
            }
            //Human plays fitness
            if(HMtrackFlag)
            {
                double track = 0;                        
                for(int i1 = 0; i1 < lHS.size(); ++i1)
                {
                    HashMap<EState,EAction> tmpHumanHashmap = lHS.get(i1);
                    //System.out.println("Size human hashmap: " + lHS.size());
                    List<EState> listAS = new ArrayList<>(agent.track.keySet());
                    List<EAction> listAA = new ArrayList<>(agent.track.values());
                    List<EState> listHS = new ArrayList<>(tmpHumanHashmap.keySet());
                    List<EAction> listHA = new ArrayList<>(tmpHumanHashmap.values());
                    //track += measureStateAction(listHS, listAS, listHA, listAA)*0.1;
                    track += measureState(tmpHumanHashmap, agent.track, listAS)*0.01;
                }
                agent.nn.activatedGen.naturalness += track/lHS.size();
            }
            if(CompareStateActionFlag)
            {
                double track = 0;
                track = lSQ.stream().map((lSQ1) -> compareStateAndAction(lSQ1, agent)).reduce(track, (accumulator, _item) -> accumulator + _item);
                agent.nn.activatedGen.naturalness += track/lSQ.size();
            }
            //System.out.println("Track: " + agent.nn.activatedGen.naturalness);
            agent.nn.activatedGen.naturalness*=5;
        }
        agent.nn.activatedGen.fitness = agent.nn.activatedGen.fitness/level*wfit + agent.nn.activatedGen.naturalness/level;
        //agent.nn.activatedGen.fitness = agent.nn.activatedGen.naturalness;
    }
    
    public static void writeLogToFile(String log, String filename)
    {
        try
        {
           PrintWriter outfile = new PrintWriter(filename);
           outfile.write(log);
           outfile.close();
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    public double measureStrangeActionPenalty(EmotionalAgent agent)
    {
        return agent.numOfJump;
    }
}
