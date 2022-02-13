/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Emotions;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;
import AS.AStarAgent;


/**
 *
 * @author Phuc
 */
public class TestAStar {
    public static void main(String[] args)
    {
        //final String argsString = "-vis on";
        //final String argsString = "-le off";
        //final String argsString = "-le off -lb on";
        //final MarioAIOptions marioAIOptions = new MarioAIOptions(argsString);
        //System.out.println(1.2f);
        final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
        marioAIOptions.setFPS(30);
        marioAIOptions.setVisualization(true);
        AStarAgent agent = new AStarAgent("A*");
        marioAIOptions.setAgent(agent);
        marioAIOptions.setReceptiveFieldHeight(18);
        marioAIOptions.setReceptiveFieldWidth(18);
        final BasicTask basicTask = new BasicTask(marioAIOptions);
        
//        //marioAIOptions.getLevelRandSeed();
//        //marioAIOptions.setFPS(1);
//        marioAIOptions.setLevelRandSeed(0);
//        marioAIOptions.setLevelDifficulty(100);
//        basicTask.runSingleEpisode(1);
//        System.out.println(basicTask.getEnvironment().getEvaluationInfoAsString());
        EAllActions.printTitle();
        System.out.println(" Seq");
        for(int i = 0; i < 5; ++i) {
            //System.out.println(marioAIOptions.getCreaturesGravity());
            marioAIOptions.setLevelRandSeed(i);
            marioAIOptions.setLevelDifficulty(0);
            marioAIOptions.setFPS(30);
            //System.out.println("Jump Power: " + marioAIOptions.getCreaturesGravity());
            //basicTask.setOptionsAndReset(marioAIOptions);
            basicTask.runSingleEpisode(1);
//            System.out.println(basicTask.getEnvironment().getEvaluationInfoAsString());
            agent.fea.printValues();
            agent.reset();
            System.out.print("seq ");
           System.out.println(basicTask.getEvaluationInfo().coinsGained + " " +
                basicTask.getEvaluationInfo().killsByFire + " " +
                basicTask.getEvaluationInfo().killsByStomp + " " +
                basicTask.getEvaluationInfo().killsByShell + " " +
                basicTask.getEvaluationInfo().killsTotal + " " +
                basicTask.getEvaluationInfo().timeSpent + " " +
                basicTask.getEvaluationInfo().timeLeft + " ");
        //}
 
        
       
        agent.resetInfo();
        }
            
            
            
        System.exit(0);

    }
}
