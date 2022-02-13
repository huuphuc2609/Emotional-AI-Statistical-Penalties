/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.StatisticalPenaltiesAgent;

import ch.idsia.agents.EvolutionalNeuralNetwork.CustomNetwork;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EAction;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EFeature;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EState;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EStateAction;
import static ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EmotionalHumanEvolTest.writeGenomeToFile;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.HumanData;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.MonitorChart;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.PenaltyMonitorChart;
import ch.idsia.agents.EvolutionalNeuralNetwork.Humanlike.OnlinePenaltiesAgent;
import ch.idsia.agents.EvolutionalNeuralNetwork.Genome;
import ch.idsia.agents.EvolutionalNeuralNetwork.Humanlike.PenaltiesAgent;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author Phuc
 */
public class StatisticalPenaltiesAgentTest {
    
    public static int seedLevel = 0;
    
    public static void main(String[] args) {
        //VisualizeFrame mainFrame = new VisualizeFrame();
        boolean debugInside = false;
        String nameFit = "Sep27_TuningStandWeight_7_"; //STT and STTTT
        //String nameFit = "ExperimentFitEmo01_";
        //String nameNat = "ExperimentNatSim00_";
        String trainLog = "";
    //****************Read Human Data****************
        List<HashMap<EState,EAction>> listHashMap = new ArrayList<>();
        List<List<double[]>> listTrace = new ArrayList<>();
        List<List<EStateAction>> listSA = new ArrayList<>();
        List<HumanData> lHD = new ArrayList<>();
        readHumanData(lHD);
        for(int i = 0; i < lHD.size(); ++i)
        {
            System.out.println(lHD.get(i).id + ": " + lHD.get(i).numberOfJump + " " + lHD.get(i).numberOfRun + " " + lHD.get(i).seqStateAction.size());
        }
//        
//        readHumanDataOld(listHashMap, listTrace, listSA);
//        
//        System.out.println("HashMap/Trace: "+listHashMap.size() + "/" + listTrace.size() + "/" + listSA.size());
        
        int nInput = 15;
        int nHidden = 20;
        int nOutput = 6;
        
        double overallFitness = -10000;
        double bestFitness = -10000;
        double overallNaturalness = -10000;
        double bestNaturalness = -10000;
        
        //Genome.randSeed = 2;
        CustomNetwork net = new CustomNetwork();
        net.randSeed = 0;
        net.isFixedRandomSeed = false;
        Genome.isFixedRandomSeed = false;
        
        //FullyConnectedEvoWeight net = new FullyConnectedEvoWeight();
        int popSize = 50;
        net.setPopulation(popSize);
        net.setMaximumHiddenNode(20);
        net.setStepSize(0.01);
        net.setMutationChance(1.0);
        net.setNodeMutationChance(0.5);
        net.setAddConnectionChance(0.5);
        net.setConnectionWeightPerturbationChance(0.9);
        net.setBiasMutationChance(1.0);
        //net.setnInput(15);
        net.setnOutput(6);
        
        
        //Create monitor chart
        MonitorChart monitorChart;
        monitorChart = new MonitorChart(nameFit+String.valueOf(popSize));
        monitorChart.setChartName(nameFit+String.valueOf(popSize));
        monitorChart.setxAxisName("Iteration");
        monitorChart.setyAxisName("Value");
        monitorChart.initializeChart();
        monitorChart.pack();
        RefineryUtilities.centerFrameOnScreen(monitorChart);
        monitorChart.setVisible(true);
        
        //Create penalty chart
        PenaltyMonitorChart penaltyChart;
        penaltyChart = new PenaltyMonitorChart(nameFit+String.valueOf(popSize));
        penaltyChart.setChartName(nameFit+String.valueOf(popSize));
        penaltyChart.setxAxisName("Penalty Features");
        penaltyChart.setyAxisName("Value");
        penaltyChart.initializeChart();
        penaltyChart.pack();
        RefineryUtilities.centerFrameOnScreen(penaltyChart);
        penaltyChart.setVisible(true);
        
      
        //Game settings
        final PenaltiesAgent agent = new PenaltiesAgent();
        //Set input
        agent.nn = net;
        agent.setnInput(7);
        agent.use10frame = true;
        agent.isShiftRight = false;
        agent.useEnemiesInfo = false;
        agent.isTraining = true;
        agent.numUsedFrame = 200;
        
        int numOfInput = 0;

        if(agent.use10frame)
            numOfInput = 26;
        else
            numOfInput = 20;

        net.setnInput(numOfInput);
        
        net.initialize();
        
//        marioAIOptions.setReceptiveFieldHeight(19);
//        marioAIOptions.setReceptiveFieldWidth(19);
//        marioAIOptions.setFPS(30);
//        marioAIOptions.setVisualization(true);
//        marioAIOptions.setAgent(agent);
//        //marioAIOptions.setLevelRandSeed(1);
//        final BasicTask basicTask = new BasicTask(marioAIOptions);
//        agent.nn.listGens.clear();
//        agent.nn.listGens.add(gen);
//        agent.nn.setActiveGen(0);
        final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
        
        marioAIOptions.setReceptiveFieldHeight(19);
        marioAIOptions.setReceptiveFieldWidth(19);
       
        System.out.println(agent.nn.listGens.size());
        marioAIOptions.setAgent(agent);
        
        marioAIOptions.setFPS(GlobalOptions.MaxFPS);
        marioAIOptions.setVisualization(false);
        marioAIOptions.setLevelRandSeed(0);
        final BasicTask basicTask = new BasicTask(marioAIOptions);
        
        
        //GA
        System.out.println("Learning...");
        int loop = 0;
        int numOfChild = 10;
        long startTT = System.currentTimeMillis();
        while(loop < 5000)
        {   
            //*************************Evol advanced option*************************
            List<Genome> listNew = new ArrayList<>();
            //Random shuffle
            Collections.shuffle(agent.nn.listGens, new Random(0));
            for(int i = 0; i < agent.nn.listGens.size(); i+=2)
            {
                //Pairing
                Genome p1 = new Genome(agent.nn.listGens.get(i));
                Genome p2 = new Genome(agent.nn.listGens.get(i+1));
//                //if(loop == 0)
//                {
//                    //****************P1****************
//                    evaluateGenome(p1, agent, marioAIOptions, basicTask, lHD);
//                    //evaluateGenomeOld(p1, agent, marioAIOptions, basicTask, listSA);
//                    //****************P2****************
//                    evaluateGenome(p2, agent, marioAIOptions, basicTask, lHD);
//                    //evaluateGenomeOld(p2, agent, marioAIOptions, basicTask, listSA);
//                }
                Genome dummy1 = p1;
                Genome dummy2 = p2;

                List<Genome> child1 = new ArrayList<>();
                List<Genome> child2 = new ArrayList<>();
                
                //****************P1's children****************
                for(int j = 0; j < numOfChild; ++j)
                {
                    Genome newGen = new Genome(dummy1);
                    //((CustomNetwork)agent.nn).mutation(newGen);
                    ((CustomNetwork)agent.nn).mutationWithRemove(newGen);
                    ((CustomNetwork)agent.nn).mutateAllWeights(newGen);
                    newGen.fitness = evaluateGenome(newGen, agent, marioAIOptions, basicTask, lHD);
                    //agent.list10frame.clear();
                    //basicTask.reset();
//                    if(debugInside)
//                    {
//                        EFeature feaA = new EFeature(newGen.fea);
//                        double checkA = newGen.fitness;
//                        double checkA1 = newGen.score;
//                        double checkA2 = newGen.naturalness;
//                        String checkA3 = basicTask.getEnvironment().getEvaluationInfoAsString();
//                        double checkB = evaluateGenome(newGen, agent, marioAIOptions, basicTask, lHD);
//                        basicTask.reset();
//                        EFeature feaB = new EFeature(newGen.fea);
//                        double checkB1 = newGen.score;
//                        double checkB2 = newGen.naturalness;
//                        String checkB3 = basicTask.getEnvironment().getEvaluationInfoAsString();
//                        double checkC = evaluateGenome(newGen, agent, marioAIOptions, basicTask, lHD);
//                        basicTask.reset();
//                        double checkC1 = newGen.score;
//                        double checkC2 = newGen.naturalness;
//                        String checkC3 = basicTask.getEnvironment().getEvaluationInfoAsString();
//                        EFeature feaC = new EFeature(newGen.fea);
//                        if(checkA != checkB)
//                        {
//                            System.out.println("INSIDE ERROR!!! " + checkA + " " + checkB);
//                            System.out.println(checkA1 + " " + checkB1);
//                            System.out.println(checkA2 + " " + checkB2);
//                            
//                            if(feaA != feaB)
//                            {
//                                EFeature.printTitle();
//                                System.out.println("");
//                                feaA.printValues();
//                                System.out.println("");
//                                feaB.printValues();
//                                System.out.println("");
//                                feaC.printValues();
//                                System.out.println("");
//                                System.out.println(checkA3);
//                                System.out.println(checkB3);
//                                System.out.println(checkC3);
//                            }
//                        }
//                    }
                    child1.add(newGen);
//                    if(newGen.listHiddenNodes.size() > 0 && newGen.fitness > 1000)
//                    {
//                        doingBackPropagation(patterns, newGen);
//                    }
                    
                }
                //****************P2's children****************
                for(int j = 0; j < numOfChild; ++j)
                {
                    Genome newGen = new Genome(dummy2);
                    //((CustomNetwork)agent.nn).mutation(newGen);
                    ((CustomNetwork)agent.nn).mutationWithRemove(newGen);
                    ((CustomNetwork)agent.nn).mutateAllWeights(newGen);
                    newGen.fitness = evaluateGenome(newGen, agent, marioAIOptions, basicTask, lHD);
                    //agent.list10frame.clear();
                    //basicTask.reset();
//                    if(debugInside)
//                    {
//                        EFeature feaA = new EFeature(newGen.fea);
//                        double checkA = newGen.fitness;
//                        double checkA1 = newGen.score;
//                        double checkA2 = newGen.naturalness;
//                        String checkA3 = basicTask.getEnvironment().getEvaluationInfoAsString();
//                        basicTask.reset();
//                        double checkB = evaluateGenome(newGen, agent, marioAIOptions, basicTask, lHD);
//                        EFeature feaB = new EFeature(newGen.fea);
//                        double checkB1 = newGen.score;
//                        double checkB2 = newGen.naturalness;
//                        String checkB3 = basicTask.getEnvironment().getEvaluationInfoAsString();
//                        basicTask.reset();
//                        double checkC = evaluateGenome(newGen, agent, marioAIOptions, basicTask, lHD);
//                        double checkC1 = newGen.score;
//                        double checkC2 = newGen.naturalness;
//                        String checkC3 = basicTask.getEnvironment().getEvaluationInfoAsString();
//                        EFeature feaC = new EFeature(newGen.fea);
//                        if(checkA != checkB)
//                        {
//                            System.out.println("INSIDE ERROR!!! " + checkA + " " + checkB);
//                            System.out.println(checkA1 + " " + checkB1);
//                            System.out.println(checkA2 + " " + checkB2);
//                            
//                            if(feaA != feaB)
//                            {
//                                EFeature.printTitle();
//                                System.out.println("");
//                                feaA.printValues();
//                                System.out.println("");
//                                feaB.printValues();
//                                System.out.println("");
//                                feaC.printValues();
//                                System.out.println("");
//                                System.out.println(checkA3);
//                                System.out.println(checkB3);
//                                System.out.println(checkC3);
//                            }
//                        }
//                    }
                    child2.add(newGen);
//                    if(newGen.listHiddenNodes.size() > 0 && newGen.fitness > 1000)
//                    {
//                        doingBackPropagation(patterns, newGen);
//                    }
                    
                }
                
                int p1i = -1;
                for(int j = 0; j < child1.size(); ++j)
                {
//                    if(child1.get(j).fitness >= p1.fitness && child1.get(j).naturalness >= p1.naturalness
//                            || child1.get(j).fitness >= p1.fitness && child1.get(j).naturalness == p1.naturalness
//                            || child1.get(j).fitness == p1.fitness && child1.get(j).naturalness >= p1.naturalness)
                    if(child1.get(j).fitness > p1.fitness)
                    //if(child1.get(j).naturalness >= p1.naturalness && child1.get(j).fitness >= p1.fitness)
                    {
                        p1 = child1.get(j);
                        p1i = j;
                    }
                }
                for(int j = 0; j < child2.size(); ++j)
                {
                    //if(p1i != j)
                    {
//                        if(child1.get(j).fitness >= p2.fitness && child1.get(j).naturalness >= p2.naturalness
//                            || child1.get(j).fitness >= p2.fitness && child1.get(j).naturalness == p2.naturalness
//                                || child1.get(j).fitness == p2.fitness && child1.get(j).naturalness >= p2.naturalness)
                        if(child2.get(j).fitness > p2.fitness)
                        //if(child1.get(j).naturalness >= p2.naturalness && child1.get(j).fitness >= p2.fitness)
                        {
                            p2 = child2.get(j);
                        }
                    }
                }
                listNew.add(new Genome(p1));
                listNew.add(new Genome(p2));
                
                dummy1.destruct();
                dummy2.destruct();
                for(int k = 0; k < child1.size(); ++k)
                {
                    child1.get(k).destruct();
                }
                for(int k = 0; k < child2.size(); ++k)
                {
                    child2.get(k).destruct();
                }
                child1.clear();
                child2.clear();
            }
            //agent.nn.listGens.clear();
            for(int k = 0; k < agent.nn.listGens.size(); ++k)
            {
                agent.nn.listGens.get(k).destruct();
            }
            agent.nn.listGens.clear();
//            for(int k = 0; k < listNew.size(); ++k)
//            {
//                //agent.nn.listGens.add(new Genome(listNew.get(k)));
//                agent.nn.listGens.add(listNew.get(k));
//            }
            agent.nn.listGens.addAll(listNew);
            listNew.clear();
            //agent.nn.setActiveGen(0);
            //if(agent.nn.getOverallFitness() > overallFitness)
            {
                //Save best genome
                Genome crbestFit = null;
                
                //Genome crbestNat = null;
                crbestFit = new Genome(agent.nn.listGens.get(0));
//                System.out.println("=======Checking========");
                for(int i = 0; i < agent.nn.listGens.size(); ++i)
                {
                    if(agent.nn.listGens.get(i).fitness > crbestFit.fitness)
                    {
                        crbestFit = new Genome(agent.nn.listGens.get(i));
                    }
                    
//                    System.out.print("FIT1: " + agent.nn.listGens.get(i).fitness + " SCORE: " + agent.nn.listGens.get(i).score + " PEN: " + agent.nn.listGens.get(i).naturalness); 
//                    System.out.println(" Pen: " + agent.nn.listGens.get(i).penStand + " " + agent.nn.listGens.get(i).penALLLEFT  + " " + agent.nn.listGens.get(i).penIllegalAction + " " + agent.nn.listGens.get(i).penChangeAction + " " + agent.nn.listGens.get(i).penCoinGained + " " + agent.nn.listGens.get(i).penTotalKill + " " + agent.nn.listGens.get(i).penOnGround + " " + agent.nn.listGens.get(i).penTimeSpent + " ");
//                    agent.nn.listGens.get(i).naturalness = simCal(agent.nn.listGens.get(i));
//                    System.out.print("FIT2: " + agent.nn.listGens.get(i).fitness + " SCORE: " + agent.nn.listGens.get(i).score + " PEN: " + agent.nn.listGens.get(i).naturalness); 
//                    System.out.println(" Pen: " + agent.nn.listGens.get(i).penStand + " " + agent.nn.listGens.get(i).penALLLEFT  + " " + agent.nn.listGens.get(i).penIllegalAction + " " + agent.nn.listGens.get(i).penChangeAction + " " + agent.nn.listGens.get(i).penCoinGained + " " + agent.nn.listGens.get(i).penTotalKill + " " + agent.nn.listGens.get(i).penOnGround + " " + agent.nn.listGens.get(i).penTimeSpent + " ");
                }
//                System.out.println("=======CheckingEnd========");
                
                //Calculate and show statistical
                
//                if(debugInside)
//                {
//                    Genome testGen = new Genome(crbestFit);
//                    testGen.fitness =  evaluateGenome(testGen, agent, marioAIOptions, basicTask, lHD);
//                    if(testGen.fitness != crbestFit.fitness)
//                    {
//                        System.out.println("ERROR!!! Different fitness. " + "Test vs Current = " + testGen.fitness + " " + crbestFit.fitness);
//                    }
//                }
//                writeGenomeToFile(crbestFit,"1LevAdvFitSim02"+String.valueOf(popSize));
//                if(crbestNat != null)
//                {
//                    writeGenomeToFile(crbestNat,"1LevAdvNatSim02"+String.valueOf(popSize));
//                }
//                else
//                    crbestNat = crbestFit;
//                String nameFit = "ExperimentFit09_";
//                //String nameFit = "ExperimentFitEmo01_";
//                String nameNat = "ExperimentNat09_";
                 
                
                writeGenomeToFile(crbestFit,nameFit+String.valueOf(popSize));
                if(loop == 100)
                {
                    writeGenomeToFile(crbestFit,"100" + nameFit+String.valueOf(popSize));
                }
                if(loop == 500)
                {
                    writeGenomeToFile(crbestFit,"500" + nameFit+String.valueOf(popSize));
                }
                if(loop == 990)
                {
                    writeGenomeToFile(crbestFit,"990" + nameFit+String.valueOf(popSize));
                }
//                if(crbestNat != null)
//                {
//                    writeGenomeToFile(crbestNat,nameNat+String.valueOf(popSize));
//                }
//                else
//                    crbestNat = crbestFit;
            
                overallFitness = agent.nn.getOverallFitness();
                bestFitness = agent.nn.getBestFitness();
                overallNaturalness = agent.nn.getOverallNaturalness();
                bestNaturalness = agent.nn.getBestNaturalness();
                
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                
                //Update monitor chart
                //monitorChart.updateXYSeries1(crbestFit.score, loop);
                monitorChart.updateXYSeries2(crbestFit.naturalness, loop);
                double[] penVals = new double[10];
                penVals[0] = crbestFit.penStand;
                penVals[1] = crbestFit.penALLLEFT;
                penVals[2] = crbestFit.penIllegalAction;
                penVals[3] = crbestFit.penChangeAction;
                penVals[4] = crbestFit.penCoinGained;
                penVals[5] = crbestFit.penTotalKill;
                penVals[6] = crbestFit.penOnGround;
                penVals[7] = crbestFit.penTimeSpent;
                penVals[8] = crbestFit.penJump;
                penVals[9] = crbestFit.penRUNTIME;
                penaltyChart.updateXYSeriesN(penVals[0],loop,1);
                penaltyChart.updateXYSeriesN(penVals[1],loop,2);
                penaltyChart.updateXYSeriesN(penVals[2],loop,3);
                penaltyChart.updateXYSeriesN(penVals[3],loop,4);
                penaltyChart.updateXYSeriesN(penVals[4],loop,5);
                penaltyChart.updateXYSeriesN(penVals[5],loop,6);
                penaltyChart.updateXYSeriesN(penVals[6],loop,7);
                penaltyChart.updateXYSeriesN(penVals[7],loop,8);
                penaltyChart.updateXYSeriesN(penVals[8],loop,9);
                penaltyChart.updateXYSeriesN(penVals[9],loop,10);
                //monitorChart.updateXYSeries3(crbestFit.fitness, loop);
                
                if(crbestFit != null)
                {
//                    //crbestFit.showGenInfo();
//                    mainFrame.removeAll();
//                    mainFrame.setGenome(crbestFit);
//                    //mainFrame.paint(mainFrame.getGraphics());
//                    mainFrame.revalidate();
//                    mainFrame.repaint();
                    trainLog += "Iteration#" + loop + ": " +
                            "FIT: " + crbestFit.fitness + " SCORE: " + crbestFit.score + " PEN: " + crbestFit.naturalness + " AVGFIT: " + overallFitness + " AVGPEN: " + overallNaturalness + " " +
                            "Pen: " + crbestFit.penStand + " " + crbestFit.penALLLEFT  + " " + crbestFit.penIllegalAction + " " + crbestFit.penChangeAction + " " + crbestFit.penCoinGained + " " + crbestFit.penTotalKill + " " + crbestFit.penOnGround + " " + crbestFit.penTimeSpent + " " +
                            "Actions: " + crbestFit.fea.allAct.returnPrintValues() +
                            " IllegalAction:" + crbestFit.fea.IllegalActions +
                            " OnGround:" + crbestFit.fea.isOnGround +
                            " ChangeAct:" + crbestFit.fea.ChangeActions +
                            " Coin:" + crbestFit.fea.coinGained +
                            " Kills:" + crbestFit.fea.totalKilled +
                            " Mushroom:" + crbestFit.fea.mushroom + 
                            " Flower:" + crbestFit.fea.flower +
                            " SpentTime:" + crbestFit.fea.timeSpent + 
                            " NumJump:" + crbestFit.fea.numJump + 
                            " FrontObj:" + crbestFit.fea.infrontObj + 
                            " Nodes: " + crbestFit.listHiddenNodes.size() + " connections: " + crbestFit.listConnections.size() + " bias connection: " + crbestFit.listBiasConnections.size()+ " " + dateFormat.format(date) + " Time: " + ((System.currentTimeMillis() - startTT)/1000) + "s" + "\r\n";
                    System.out.print("Iteration#" + loop + ": ");
//                    if(crbestFit.fitness > 2000)
//                        System.out.print("1 ");
//                    else
//                        System.out.print("0 ");
                    System.out.print("FIT: " + crbestFit.fitness + " SCORE: " + crbestFit.score + " PEN: " + crbestFit.naturalness + " AVGFIT: " + overallFitness + " AVGPEN: " + overallNaturalness + " "); 
                    System.out.print("Pen: " + crbestFit.penStand + " " + crbestFit.penALLLEFT  + " " + crbestFit.penIllegalAction + " " + crbestFit.penChangeAction + " " + crbestFit.penCoinGained + " " + crbestFit.penTotalKill + " " + crbestFit.penOnGround + " " + crbestFit.penTimeSpent + " ");
                    System.out.print("Actions:" + crbestFit.fea.allAct.returnPrintValues() +
                            " IllegalAction:" + crbestFit.fea.IllegalActions +
                            " OnGround:" + crbestFit.fea.isOnGround +
                            " ChangeAct:" + crbestFit.fea.ChangeActions +
                            " Coin:" + crbestFit.fea.coinGained +
                            " Kills:" + crbestFit.fea.totalKilled +
                            " Mushroom:" + crbestFit.fea.mushroom + 
                            " Flower:" + crbestFit.fea.flower + 
                            " SpentTime:" + crbestFit.fea.timeSpent +
                            " NumJump:" + crbestFit.fea.numJump + 
                            " FrontObj:" + crbestFit.fea.infrontObj + " " + dateFormat.format(date));
                    System.out.println(" Nodes: " + crbestFit.listHiddenNodes.size() + " connections: " + crbestFit.listConnections.size() + " bias connection: " + crbestFit.listBiasConnections.size() + " Time: " + ((System.currentTimeMillis() - startTT)/1000) + "s");
                    writeLogToFile(trainLog, nameFit+String.valueOf(popSize)+"_log");
                    //System.out.print("Fitness: " + overallFitness + " " + bestFitness + " Error: " + overallNaturalness + " " + bestNaturalness + " -- " + dateFormat.format(date));
                    //System.out.println(" FITNESS: Jump=" + crbestFit.jumpTimes + " " + crbestFit.fitness + " " + crbestFit.score + " " + crbestFit.naturalness + " Nodes: " + crbestFit.listHiddenNodes.size() + " connections: " + crbestFit.listConnections.size() + " bias connection: " + crbestFit.listBiasConnections.size());
                    
                    //System.out.println(" NATURALNESS: Jump=" + crbestNat.jumpTimes + " " + crbestNat.fitness+"/"+crbestNat.naturalness + " Nodes: " + crbestNat.listHiddenNodes.size() + " connections: " + crbestNat.listConnections.size() + " bias connection: " + crbestNat.listBiasConnections.size());
                    //System.out.print("Iteration#" + loop + ": ");
                    //System.out.print("Fitness: " + overallFitness + " " + bestFitness + " Naturalness: " + overallNaturalness + " " + bestNaturalness + " -- " + dateFormat.format(date));
                    //System.out.print("Fitness: " + overallFitness + " " + bestFitness + " Naturalness: " + overallNaturalness + " " + bestNaturalness + " -- " + dateFormat.format(date) + " simDist: " + crbestFit.simDistEnemy);
                    //System.out.println(" FITNESS: Jump=" + crbestFit.jumpTimes + " " + crbestFit.fitness+"/"+crbestFit.naturalness + " Nodes: " + crbestFit.listHiddenNodes.size() + " connections: " + crbestFit.listConnections.size() + " bias connection: " + crbestFit.listBiasConnections.size());
                    
                    crbestFit.destruct();
                }
//                else
//                    System.out.println(overallFitness + " " + bestFitness + " " + overallNaturalness + " " + bestNaturalness + " -- " + dateFormat.format(date));
            }
//            seedLevel++;
//            if(seedLevel > 4)
//                seedLevel = 0;
            loop++;
        }
        System.out.println("Training Completed!!!");
        //Creating hashmap of action
        //LEFT RIGHT DOWN JUMP SPEED UP
//        boolean[] act1 = new boolean[]{true,false,false,false,false,false}; // LEFT
//        boolean[] act2 = new boolean[]{false,true,false,false,false,false}; // RIGHT
//        boolean[] act3 = new boolean[]{false,false,true,false,false,false}; // DOWN
//        boolean[] act4 = new boolean[]{false,false,false,true,false,false}; // JUMP
//        boolean[] act5 = new boolean[]{false,false,false,false,true,false}; // SPEED
//        boolean[] act6 = new boolean[]{false,false,false,false,false,true}; // UP
//        
//        boolean[] act7 = new boolean[]{true,false,false,true,false,false}; // LEFT + JUMP
//        boolean[] act8 = new boolean[]{true,false,false,true,true,false}; // LEFT + JUMP + SPEED
//        
//        boolean[] act9 = new boolean[]{false,true,false,true,false,false}; // RIGHT + JUMP
//        boolean[] act10 = new boolean[]{false,true,false,true,true,false}; // RIGHT + JUMP + SPEED
//        
//        boolean[] act11 = new boolean[]{true,false,false,false,true,false}; // LEFT + SPEED
//        boolean[] act12 = new boolean[]{false,true,false,false,true,false}; // RIGHT + SPEED
//        boolean[] act13 = new boolean[]{false,false,false,true,true,false}; // JUMP + SPEED
    }
    
    public static void readHumanData(List<HumanData> lHD)
    {
        int noH = 5; //Number of human players
//        int noDif = 2;
//        int noLev = 61;
        int noDif = 1;
        int noLev = 1;
        String[] playerName = new String[noH];
        playerName[0] = "Luong";
        playerName[1] = "Duy";
        playerName[2] = "Vu";
        playerName[3] = "Sila";
        playerName[4] = "Tung";
        for(int n = 0; n < noH; ++n)
        {
            String name = playerName[n];
            //String folderName = playerName[n] + "_Dif0_Lev1"; //Dif lev
            String folderName = playerName[n]; //Dif lev
            String currentDir = "";
            try {
                currentDir = new java.io.File( "." ).getCanonicalPath();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            String folder = currentDir + "\\HumanTraceData\\"+folderName+"\\";

            for(int dif = 0; dif < noDif; ++dif)
            {
                for(int lev = 0; lev < noLev; ++lev)
                {
                    for(int times = 0; times < 1; ++times)
                    {
                        //****************Read human data******************
                        try
                        {
                            FileReader fr = new FileReader(folder+"seqActionHumanPlay"+playerName[n]+"_"+dif+"_"+lev+"_"+times);
                            BufferedReader br = new BufferedReader(fr);
                            for(String line; (line = br.readLine()) != null; ) 
                            {
                                // process the line.
                                HumanData hmdt = (HumanData) new Gson().fromJson(line,HumanData.class);
                                lHD.add(hmdt);
                            }
                            br.close();
                            fr.close();
                        }
                        catch(IOException e)
                        {
                            System.out.println(e.getMessage());
                        }


                        System.out.println("Human data: " + lHD.size());
                    }
                }
            }
        }
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
    
    public static double evaluateGenome(Genome g, PenaltiesAgent agent, MarioAIOptions option, BasicTask task, List<HumanData> listHD)
    {   
        int dif = 1;
        int level = 1;
        double result = 0;
        agent.nn.activatedGen = g;
        agent.nn.activatedGen.fitness = 0;
        agent.nn.activatedGen.score = 0;
        agent.nn.activatedGen.naturalness = 0;
        //agent.nn.activatedGen.simDistEnemy = 0;
        agent.nn.activatedGen.resetInfo();
        for(int n = 0; n < dif; ++n)
        {
            option.setLevelDifficulty(n);
            for(int m = 0; m < level; m++)
            {
                agent.resetInformation();
                option.setLevelRandSeed(m);
                //option.setLevelRandSeed(1);
                task.runSingleEpisode(1);
                //agent.nn.activatedGen.fitness += basicTask.getEvaluationInfo().computeWeightedFitness();
                //agent.nn.activatedGen.fitness += task.getEvaluationInfo().computeWeightedFitness();
                //agent.nn.activatedGen.fitness += task.getEvaluationInfo().computeDistancePassed()
                                                //+ task.getEvaluationInfo().mushroomsDevoured*100
                                                //- task.getEvaluationInfo().collisionsWithCreatures*100
                                                //+ task.getEvaluationInfo().hiddenBlocksFound*100
                                                //+ task.getEvaluationInfo().marioMode*16
                                                //+ task.getEvaluationInfo().marioStatus*100
                                                //+ task.getEvaluationInfo().coinsGained*16;
                        ;
                
                agent.nn.activatedGen.jumpTimes = agent.numOfJump;
                //agent.nn.activatedGen.fitness -= agent.nn.activatedGen.jumpTimes*5;
                //agent.averageDistClosestEnemy/=agent.seqStateAction.size();
                
                //agent.nn.activatedGen.fitness -= agent.nn.activatedGen.jumpTimes*50;
                //int timeSpent = task.getEvaluationInfo().timeSpent;
                //int numJumps = agent.numOfJump;
                //int numCoins = task.getEvaluationInfo().coinsGained;
                //int numKills = task.getEvaluationInfo().killsTotal;
                agent.nn.activatedGen.score = task.getEvaluationInfo().computeDistancePassed()*0.005 + task.getEvaluationInfo().marioStatus*16 + task.getEvaluationInfo().mushroomsDevoured*500 + task.getEvaluationInfo().flowersDevoured*500 + agent.iReward;
                //agent.nn.activatedGen.score += agent.fea.allAct.ALLLEFT*0.1 + agent.fea.allAct.Stand*0.1 + task.getEvaluationInfo().distancePassedPhys + task.getEvaluationInfo().marioStatus*16 + task.getEvaluationInfo().mushroomsDevoured*500 + task.getEvaluationInfo().flowersDevoured*500;
                //agent.nn.activatedGen.score += task.getEvaluationInfo().marioStatus*16 + task.getEvaluationInfo().mushroomsDevoured*500 + task.getEvaluationInfo().flowersDevoured*500;
                //agent.nn.activatedGen.score += task.getEvaluationInfo().computeWeightedFitness();
                int[] otherInfo = new int[4];
                otherInfo[0] = agent.numOfJump;
                otherInfo[1] = task.getEvaluationInfo().coinsGained;
                otherInfo[2] = task.getEvaluationInfo().killsTotal;
                otherInfo[3] = task.getEvaluationInfo().timeSpent;
                agent.nn.activatedGen.fea.totalKilled = otherInfo[2];
                int[] levelInfo = new int[2];
                levelInfo[0] = task.getEvaluationInfo().totalNumberOfCoins;
                levelInfo[1] = task.getEvaluationInfo().totalNumberOfCreatures;
                
                agent.fea.coinGained = task.getEvaluationInfo().coinsGained;
                agent.fea.totalKilled = task.getEvaluationInfo().killsTotal;
                agent.fea.timeSpent = task.getEvaluationInfo().timeSpent;
                agent.fea.mushroom = task.getEvaluationInfo().mushroomsDevoured;
                agent.fea.flower = task.getEvaluationInfo().flowersDevoured;
//                List<HumanData> selecteData = new ArrayList<>();
//                for(int k = 0; k < listHD.size(); k+=10)
//                {
//                    
//                }
                
                

                //Actions
                agent.nn.activatedGen.fea.assignData(agent.fea);
                
//                agent.nn.activatedGen.fea.coinGained += task.getEvaluationInfo().coinsGained;
//                agent.nn.activatedGen.fea.totalKilled += task.getEvaluationInfo().killsTotal;
//                agent.nn.activatedGen.fea.timeSpent += task.getEvaluationInfo().timeSpent;
                //agent.nn.activatedGen.fea.printValues();
                //System.out.println();
                agent.nn.activatedGen.naturalness = simCal(agent.nn.activatedGen);
                //agent.nn.activatedGen.naturalness = 0;
            }
 
            
//            agent.nn.activatedGen.Stand = agent.Stand;
//            agent.nn.activatedGen.Right = agent.RightButton;
//            agent.nn.activatedGen.Left = agent.LeftButton;
//            agent.nn.activatedGen.Speed = agent.RunButton;
//            agent.nn.activatedGen.Jump = agent.JumpButton;
//            agent.nn.activatedGen.RJ = agent.RJ;
//            agent.nn.activatedGen.LJ = agent.LJ;
//            agent.nn.activatedGen.RS = agent.RS;
//            agent.nn.activatedGen.LS = agent.LS;
//            agent.nn.activatedGen.RJS = agent.RJS;
//            agent.nn.activatedGen.LJS = agent.LJS;
//            agent.nn.activatedGen.Coin = task.getEvaluationInfo().coinsGained;
//            agent.nn.activatedGen.Kills = task.getEvaluationInfo().killsTotal;
//            agent.nn.activatedGen.ChangeAction = agent.changeActions;
//
//            agent.nn.activatedGen.LeftRight = agent.LeftRight;
//            agent.nn.activatedGen.UpDown = agent.UpDown;
//            agent.nn.activatedGen.score = agent.nn.activatedGen.fitness;
            //agent.nn.activatedGen.naturalness *= 0.2;
            
            
            //else
            //    agent.nn.activatedGen.fitness = agent.nn.activatedGen.fitness*0.5;
        }
            //agent.nn.activatedGen.score = agent.nn.activatedGen.score/(level*dif);
            //agent.nn.activatedGen.fitness = agent.nn.activatedGen.fitness/level;
            //ent.nn.activatedGen.naturalness = agent.nn.activatedGen.naturalness/(level*dif);
//            
//            agent.nn.activatedGen.fea.allAct.Stand/=(level*dif);
//            agent.nn.activatedGen.fea.coinGained/=(level*dif);
//            agent.nn.activatedGen.fea.totalKilled/=(level*dif);
//            agent.nn.activatedGen.fea.ChangeActions/=(level*dif);
//            
//            agent.nn.activatedGen.fea.isOnGround/=(level*dif);
//            agent.nn.activatedGen.fea.IllegalActions/=(level*dif);
//            agent.nn.activatedGen.fea.timeSpent/=(level*dif);            
            //if(agent.nn.activatedGen.fitness > 2000)
            {
                //agent.nn.activatedGen.naturalness = simCalPositionBased(agent.agentTrace, listSeqs);
                //agent.nn.activatedGen.naturalness += (12000 - simCalNumberOfButtonBased(agent.seqStateAction,listSeqs))*0.08;
                
                agent.nn.activatedGen.fitness = agent.nn.activatedGen.score - agent.nn.activatedGen.naturalness;
                
//                if(agent.nn.activatedGen.fitness < agent.nn.activatedGen.oldfiness)
//                {
//                    if(!task.getEnvironment().getEvaluationInfoAsString().contains("Time out!") && !task.getEnvironment().getEvaluationInfoAsString().contains("Collision"))
//                    {
//                        System.out.println("DETECT ONE LOWER FITNESS!!!");
//                        System.out.println(task.getEnvironment().getEvaluationInfoAsString());
//                    }
//                }
                
                agent.nn.activatedGen.oldfiness = agent.nn.activatedGen.fitness;
                result = agent.nn.activatedGen.fitness;
                //agent.nn.activatedGen = null;
                agent.list10frame.clear();
            }
        return result;
    }
        
    public static double simCal(Genome ai)
    {
        double result = 0;
        double penaltySTAND = 0;
        double penaltyALLLEFT = 0;
        double penaltyOnGround = 0;
        double penaltyChangeAction = 0;
        double penaltyCOIN = 0;
        double penaltyKILL = 0;
        double penaltyIllegalAction = 0;  
        double penaltyTIMESPENT = 0;  
        double penaltyJUMP = 0;
        double penaltyRUNTIME = 0;
        double penaltyFrontObj = 0;
        
        double pStand = ai.fea.allAct.Stand*1.0/3000;
        double limitStand = 0.08127;
        double limitStandDev = 0.0574;
        
        double pALLLEFT = ai.fea.allAct.ALLLEFT*1.0/3000;
        double limitALLLEFT = 0.03923;
        double limitALLLEFTDev = 0.0276;
        
        double pIllegalActions = ai.fea.IllegalActions*1.0/3000;
        double limitIllegalActions = 0.03505;
        double limitIllegalActionsDev = 0.0356;
        
        double pChangeActions = ai.fea.ChangeActions*1.0/3000;
        double limitChangeActions = 0.33275;
        double limitChangeActionsDev = 0.1405;
        
        double pCoinGained = ai.fea.coinGained*1.0/285; //275
        double limitCoinGained = 0.31291;
        double limitCoinGainedDev = 0.1231;
        
        double pTotalKilled = ai.fea.totalKilled*1.0/40; //46
        double limitTotalKilled = 0.33826;
        double limitTotalKilledDev = 0.1912;
        
        double pOnGround = ai.fea.isOnGround*1.0/3000;
        double limitOnGround = 0.21645;
        double limitOnGroundDev = 0.1052;
        
        double pTimeSpent = ai.fea.timeSpent*1.0/200;
        double limitTimeSpent = 0.42940;
        double limitTimeSpentDev = 0.1789;
        
        double pJump = ai.fea.numJump*1.0/200;
        double limitJump = 0.0397;
        double limitJumpDev = 0.01704;
        
        double pRUNTIME = (ai.fea.allAct.ALLRUN*1.0/ai.fea.timeSpent)/7.5;
        double limitRUNTIME = 0.9959;
        double limitRUNTIMEDev = 0.5378;
        
        double pFrontObj = ai.fea.infrontObj*1.0/3000;
        double limitFrontObj = 0.0787;
        double limitFrontObjDev = 0.0539;
;
        
        //New Feature Sim     
        
        penaltySTAND = calPenalty(pStand,limitStand,limitStandDev);
        penaltyALLLEFT = calPenalty(pALLLEFT,limitALLLEFT,limitALLLEFTDev);
        penaltyIllegalAction = calPenalty(pIllegalActions,limitIllegalActions,limitIllegalActionsDev);
        penaltyChangeAction = calPenalty(pChangeActions,limitChangeActions,limitChangeActionsDev);
        penaltyCOIN = calPenalty(pCoinGained,limitCoinGained,limitCoinGainedDev);
        penaltyKILL = calPenalty(pTotalKilled,limitTotalKilled,limitTotalKilledDev);
        penaltyOnGround = calPenalty(pOnGround,limitOnGround,limitOnGroundDev);
        penaltyTIMESPENT = calPenalty(pTimeSpent,limitTimeSpent,limitTimeSpentDev);
        penaltyJUMP = calPenalty(pJump, limitJump, limitJumpDev);
        penaltyRUNTIME = calPenalty(pRUNTIME, limitRUNTIME, limitRUNTIMEDev);
        penaltyFrontObj = calPenalty(pFrontObj, limitFrontObj, limitFrontObjDev);
                
        double PENALTY =  
                penaltySTAND * 0 //3
                + penaltyALLLEFT * 0 //20
                + penaltyOnGround * 0 //0.75
                + penaltyChangeAction * 0 //1
                + penaltyCOIN * 0 //1 
                + penaltyKILL * 0 //1
                + penaltyIllegalAction * 0 //10
                + penaltyTIMESPENT * 0 //10000 //0.75
                + penaltyJUMP * 10000 //50000
                + penaltyRUNTIME * 0 //15000
                + penaltyFrontObj * 10000;
        
        //Others actions pen
//        double pRight = ai.fea.allAct.Right/3000;
//        double pRJ = ai.fea.allAct.RJ/3000;
//        double pRS = ai.fea.allAct.RS/3000;
//        double pJump = ai.fea.allAct.Jump/3000;
//        double pJS = ai.fea.allAct.JS/3000;
//        double pSpeed = ai.fea.allAct.Speed/3000;
//        double pRJS = ai.fea.allAct.RJS/3000;
//        
////        PENALTY += calPenalty(pRight, 0.04617, 0.04804) * 6
////                + calPenalty(pRJ, 0.02138, 0.02200) * 15
////                + calPenalty(pRS, 0.03736, 0.03284) * 10
////                + calPenalty(pJump, 0.01048, 0.00890) * 30
////                + calPenalty(pJS, 0.01817, 0.01906) * 16
////                + calPenalty(pSpeed, 0.04786, 0.04700) * 7.5
////                + calPenalty(pRJS, 0.03656, 0.03009) * 8.3;
//        
//        PENALTY += calPenalty(pRight, 0.04617, 0.04804)
//                + calPenalty(pRJ, 0.02138, 0.02200)
//                + calPenalty(pRS, 0.03736, 0.03284)
//                + calPenalty(pJump, 0.01048, 0.00890)
//                + calPenalty(pJS, 0.01817, 0.01906)
//                + calPenalty(pSpeed, 0.04786, 0.04700)
//                + calPenalty(pRJS, 0.03656, 0.03009);
//        
//        double pRD = ai.fea.allAct.RD/3000;      
//        double pRU = ai.fea.allAct.RU/3000;
//        double pDown = ai.fea.allAct.Down/3000;
//        double pDJ = ai.fea.allAct.DJ/3000;
//        double pDS = ai.fea.allAct.DS/3000;
//        double pDU = ai.fea.allAct.DU/3000;
//        double pJU = ai.fea.allAct.JU/3000;
//        double pSU = ai.fea.allAct.SU/3000;
//        double pUp = ai.fea.allAct.Up/3000;
//        double pRDJ = ai.fea.allAct.RDJ/3000;
//        double pRDS = ai.fea.allAct.RDS/3000;
//        double pRDU = ai.fea.allAct.RDU/3000;
//        double pRJU = ai.fea.allAct.RJU/3000;
//        double pRSU = ai.fea.allAct.RSU/3000;
//        double pDJS = ai.fea.allAct.DJS/3000;
//        double pDJU = ai.fea.allAct.DJU/3000;
//        double pDSU = ai.fea.allAct.DSU/3000;
//        double pJSU = ai.fea.allAct.JSU/3000;
//        double pRDJS = ai.fea.allAct.RDJS/3000;
//        double pRDJU = ai.fea.allAct.RDJU/3000;
//        double pRDSU = ai.fea.allAct.RDSU/3000;
//        double pRJSU = ai.fea.allAct.RJSU/3000;
//        double pDJSU = ai.fea.allAct.DJSU/3000;
//        double pRDJSU = ai.fea.allAct.RDJSU/3000;
//
//        
//        PENALTY += pRD + pRU + pDown + pDJ + pDS + pDU + pJU + pSU + pUp + pRDJ + pRDS + pRDU + pRJU + pRSU + pDJS + pDJU + pDSU + pJSU + pRDJS + pRDJU + pRDSU + pRJSU + pDJSU + pRDJSU;
//        

        //result = Math.sqrt(PENALTY*500);
        //result = PENALTY/39;
        result = PENALTY;
        
        //System.out.println("Debug: " + penaltySTAND + " " + penaltyALLLEFT + " " + penaltyOnGround + " " + penaltyChangeAction + " " + penaltyCOIN + " " + penaltyKILL + " " + penaltyIllegalAction + " " + penaltyTIMESPENT);
        
        ai.penStand = penaltySTAND;
        ai.penALLLEFT = penaltyALLLEFT;
        ai.penOnGround = penaltyOnGround;
        ai.penChangeAction = penaltyChangeAction;
        ai.penCoinGained = penaltyCOIN;
        ai.penTotalKill = penaltyKILL;
        ai.penIllegalAction = penaltyIllegalAction;
        ai.penTimeSpent = penaltyTIMESPENT;
        ai.penJump = penaltyJUMP;
        ai.penRUNTIME = penaltyRUNTIME;
        
        return result;
    }
    
    public static double calPenalty(double value, double threshold, double dev)
    {
        dev = dev/3;
        double result;
        if(value > threshold + dev)
            result = value - (threshold + dev);
        else if(value < threshold - dev)
            result = threshold - dev - value;
        else
            result = 0;
        return result*result;
    }
}
