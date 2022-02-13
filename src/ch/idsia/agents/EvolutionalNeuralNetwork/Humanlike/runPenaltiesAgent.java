/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Humanlike;

import ch.idsia.agents.EvolutionalNeuralNetwork.CustomNetwork;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EAllActions;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EFeature;
import ch.idsia.agents.EvolutionalNeuralNetwork.Genome;
import ch.idsia.agents.EvolutionalNeuralNetwork.Test.EvaluationEnvironment.MyBasicTask;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;
import com.google.gson.Gson;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.TreeSet;
import javax.swing.JFrame;

/**
 *
 * @author Phuc
 * run Emotional AI trained using human data
 */
public class runPenaltiesAgent {
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
        
        
        //final String argsString = "-le off -lb off";
        //final MarioAIOptions marioAIOptions = new MarioAIOptions(argsString);
        final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
        double overallFitness = 0;
        CustomNetwork net = new CustomNetwork();
        //FullyConnectedEvoWeight net = new FullyConnectedEvoWeight();
        net.setPopulation(100);
        //net.setnInput(53);
        net.setnOutput(6);
        //Genome inputGenome = readGenomeFromFile("400June_Check_44_50"); //Normal Good
        //Genome inputGenome = readGenomeFromFile("June_Check_S_50"); //Normal Good
        //Genome inputGenome = readGenomeFromFile("100June_Check_11_50"); //Normal Good
        //Genome inputGenome = readGenomeFromFile("200June_CountFaster_333_50"); //Normal Good
        //Genome inputGenome = readGenomeFromFile("CheckError_50"); //Normal Good
        //Genome inputGenome = readGenomeFromFile("TestInput2_July7_50"); //Normal Good
        Genome inputGenome = readGenomeFromFile("Sep27_TuningStandWeight_7_50"); //Normal Good
        
        final PenaltiesAgent agent = new PenaltiesAgent();
        agent.use10frame = true;
        agent.isShiftRight = false;
        agent.useEnemiesInfo = false;
        agent.numUsedFrame = 200;

        if(agent.use10frame)
            net.setnInput(24);
        else
            net.setnInput(18);

        
        agent.setnInput(7);
        agent.nn = net;
        
        marioAIOptions.setReceptiveFieldHeight(19);
        marioAIOptions.setReceptiveFieldWidth(19);
       
        System.out.println(agent.nn.listGens.size());
        marioAIOptions.setAgent(agent);    
        
        marioAIOptions.setFPS(GlobalOptions.MaxFPS);
        marioAIOptions.setVisualization(false);
        marioAIOptions.setLevelRandSeed(4);
        marioAIOptions.setLevelDifficulty(0);
        //marioAIOptions.setEnemies("off");
        final BasicTask basicTask = new BasicTask(marioAIOptions);
        //final MyBasicTask basicTask = new MyBasicTask(marioAIOptions);
        
                
        //Genome inputGenome = readGenomeFromFile("NewTrain21_03_2017_50"); //Normal Good
        //Genome inputGenome = readGenomeFromFile("NewTrainNewInput21_03_2017_50"); //Normal Good
        //Genome inputGenome = readGenomeFromFile("NewTrainNewInputST22_03_2017_50"); //Normal Good
        //inputGenome = readGenomeFromFile("NewTrainNewInputST24_03_2017_50"); //Normal Good
        //inputGenome = readGenomeFromFile("NewTrainNewInputST27_03_2017_50"); //Normal Good
        //inputGenome = readGenomeFromFile("NewTrainNewInput5levelsSTTTT27_03_2017_50"); //Normal Good
        ////inputGenome = readGenomeFromFile("NewTrainNewInput5levelsSTTTT27_03_2017_50"); //Normal Good
        //inputGenome = readGenomeFromFile("NewTrainNewInputContSTTT27_03_2017_50"); //Normal Good
        //inputGenome = readGenomeFromFile("Lev3_28_03_2017_50"); //Normal Good
        //inputGenome = readGenomeFromFile("Lev33"); //Normal Good
        ////inputGenome = readGenomeFromFile("Lev33");
        //inputGenome = readGenomeFromFile("Lev4S_28_03_2017_50"); //Normal Good
        ////inputGenome = readGenomeFromFile("Lev1_28_03_2017_50"); //Normal Good
        //inputGenome = readGenomeFromFile("GPU_Test_3_04_17_201750");
        //inputGenome = readGenomeFromFile("GPU_Test_04_17_201750");
        ////inputGenome = readGenomeFromFile("June_Check_11_50");
        int seed = 0; //0, 1, 2
        //Visualize structure
        VisualizeFrame mainFrame = new VisualizeFrame(inputGenome);
        //mainFrame.paint(mainFrame.getGraphics());
        //Genome inputGenome = readGenomeFromFile("AdvNatSim50");
        //Genome inputGenome = readGenomeFromFile("AdvFit10lev50");
        //Genome inputGenome = readGenomeFromFile("Only0.03");
        //inputGenome = readGenomeFromFile("EmotionalTestBestFitBothNoData02_100"); // 1
        //inputGenome = readGenomeFromFile("EmotionalTestBestNatBothNoData00_100"); // 1
        
        //inputGenome = readGenomeFromFile("EmotionalTestBestFitBothNoDataSpecialEvaluation100");
        
        //inputGenome = readGenomeFromFile("EmotionalTestBestNatBoth50");z
        //inputGenome = readGenomeFromFile("EmotionalTestBestNatBoth100");
        //inputGenome = readGenomeFromFile("EmotionalTestBestFitBoth100");
        //inputGenome = readGenomeFromFile("EmotionalTestBestFitBoth50");
        //inputGenome = readGenomeFromFile("TRAINED//EmotionalTestBestFitBothNoData100");
        
        marioAIOptions.setFPS(29);
        marioAIOptions.setVisualization(true);
        //marioAIOptions.setLevelRandSeed(1);
        PenaltiesAgent.debug = true;
        agent.nn.listGens.clear();
        agent.nn.listGens.add(inputGenome);
        System.out.println(agent.nn.listGens.size());
        agent.nn.setActiveGen(0);
        agent.nn.activatedGen.showGenInfo();
        marioAIOptions.setLevelDifficulty(0);
        marioAIOptions.setFPS(30);
        int lev = 1;  //30 - 90
        agent.fea.printTitle();
        System.out.println(" Seq ");
        
        for(int i = 0; i < lev; ++i)
        {
            agent.listActs.clear();
            agent.runTest = false;
            marioAIOptions.setLevelRandSeed(i); //0,2,5
            basicTask.runSingleEpisode(1);
            
            agent.fea.totalKilled = basicTask.getEvaluationInfo().killsTotal;
            agent.fea.timeSpent = basicTask.getEvaluationInfo().timeSpent;
            agent.fea.coinGained = basicTask.getEvaluationInfo().coinsGained;
            agent.fea.killByFire = basicTask.getEvaluationInfo().killsByFire;
            agent.fea.killByStomp = basicTask.getEvaluationInfo().killsByStomp;
            agent.fea.killByShell = basicTask.getEvaluationInfo().killsByShell;
            
            
            //agent.fea.printValues();
            //System.out.println(agent.seqStateAction.size());
            //agent.resetInformation();
            //agent.nn.activatedGen.resetInfo();
            
            agent.seqStateAction.clear();
            System.out.println("Size Act: " + agent.listActs.size());
            for(int k = 0; k < agent.listActs.size(); ++k)
            {
                System.out.print(agent.listActs.get(k).toInt() + " ");
            }
            
            //System.out.println(i + " "  + basicTask.getEnvironment().getEvaluationInfo().totalNumberOfCoins + " " + basicTask.getEnvironment().getEvaluationInfo().totalNumberOfCreatures);
            //System.out.print(basicTask.getEnvironment().getEvaluationInfo().distancePassedPhys);
            //System.out.println("");
        }
            
        
        
//////        System.out.println("IllegalAction LeftRight IsOnGround STAND RIGHT LEFT JUMP RUN R+J L+J R+R L+R R+J+R L+J+R ChangeActions COIN Jumps TimeSpent TimeLeft FinishTick TotalKills SeqSize Encounter Score");
//////        for(int i = 0; i < lev; ++i)
//////        {
//////            marioAIOptions.setLevelRandSeed(i);
//////            basicTask.runSingleEpisode(1);
//////            //Show info agent        
//////            System.out.print(agent.IllegalAction + " ");
//////            System.out.print(agent.LeftRight + " ");
//////            System.out.print(agent.OnGround + " ");
//////            System.out.print(agent.Stand + " ");
//////            System.out.print(agent.RightButton + " ");
//////            System.out.print(agent.LeftButton + " ");
//////            System.out.print(agent.JumpButton + " ");
//////            System.out.print(agent.RunButton + " ");
//////            System.out.print(agent.RJ + " ");
//////            System.out.print(agent.LJ + " ");
//////            System.out.print(agent.RS + " ");
//////            System.out.print(agent.LS + " ");
//////            System.out.print(agent.RJS + " ");
//////            System.out.print(agent.LJS + " ");
//////            System.out.print(agent.changeActions + " ");
//////            System.out.print(basicTask.getEvaluationInfo().coinsGained + " ");
//////            System.out.print(agent.numOfJump + " ");
//////            System.out.print(basicTask.getEvaluationInfo().timeSpent + " ");
//////            System.out.print(basicTask.getEvaluationInfo().timeLeft + " ");
//////            System.out.print(agent.tick + " ");
//////            System.out.print(basicTask.getEvaluationInfo().killsTotal + " ");
//////            System.out.print(agent.seqStateAction.size() + " ");
//////            System.out.print(agent.encounterEnemies + " ");
//////            System.out.println(basicTask.getEvaluationInfo().distancePassedPhys);
//////            agent.resetInformation();
//////            agent.nn.activatedGen.resetInfo();
//////            agent.seqStateAction.clear();
//////        }
//////        agent.nn.activatedGen.fitness = basicTask.getEvaluationInfo().computeWeightedFitness();
//////        System.out.println(basicTask.getEnvironment().getEvaluationInfoAsString());
//////        System.out.println("GENOME STRUCTURE!!!!!!!");

        
        
//        for(int i = 0; i < agent.agentTrace.size(); ++i)
//        {
//            System.out.println(agent.agentTrace.get(i)[0] + " " + agent.agentTrace.get(i)[1]);
//        }
        
//        //Show info agent        
//        System.out.println("STAND RIGHT LEFT JUMP RUN R+J L+J R+R L+R R+J+R L+J+R ChangeActions COIN Jumps TimeSpent TimeLeft FinishTick TotalKills SeqSize Encounter");
//        System.out.print(agent.Stand + " ");
//        System.out.print(agent.RightButton + " ");
//        System.out.print(agent.LeftButton + " ");
//        System.out.print(agent.JumpButton + " ");
//        System.out.print(agent.RunButton + " ");
//        System.out.print(agent.RJ + " ");
//        System.out.print(agent.LJ + " ");
//        System.out.print(agent.RS + " ");
//        System.out.print(agent.LS + " ");
//        System.out.print(agent.RJS + " ");
//        System.out.print(agent.LJS + " ");
//        System.out.print(agent.changeActions + " ");
//        System.out.print(basicTask.getEvaluationInfo().coinsGained + " ");
//        System.out.print(agent.numOfJump + " ");
//        System.out.print(basicTask.getEvaluationInfo().timeSpent + " ");
//        System.out.print(basicTask.getEvaluationInfo().timeLeft + " ");
//        System.out.print(agent.tick + " ");
//        System.out.print(basicTask.getEvaluationInfo().killsTotal + " ");
//        System.out.print(agent.seqStateAction.size() + " ");
//        System.out.print(agent.encounterEnemies + " ");
        System.out.println(basicTask.getEnvironment().getEvaluationInfoAsString());
        agent.nn.activatedGen.score = basicTask.getEvaluationInfo().computeDistancePassed() + basicTask.getEvaluationInfo().marioStatus*16 + basicTask.getEvaluationInfo().mushroomsDevoured*500 + basicTask.getEvaluationInfo().flowersDevoured*500;
        System.out.println("Score: " + agent.nn.activatedGen.score);
        EFeature.printTitle();
        System.out.println("");
        agent.fea.printValues();
        System.out.println("");
        System.out.println("Number of frontObj: " + agent.fea.infrontObj);
        System.out.println("Number of jumps: " + agent.fea.numJump);
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
    
    
}
