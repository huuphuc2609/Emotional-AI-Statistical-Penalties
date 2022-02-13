/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork;

import ch.idsia.agents.EvolutionalNeuralNetwork.Agent.RuleBasedAgent;
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
public class runRuleBasedAgent {
    public static void main(String[] args) {
        final String argsString = "-le off";
        final MarioAIOptions marioAIOptions = new MarioAIOptions(argsString);
        //final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
        double overallFitness = 0;

        final RuleBasedAgent agent = new RuleBasedAgent();
        
        marioAIOptions.setReceptiveFieldHeight(9);
        marioAIOptions.setReceptiveFieldWidth(9);        

        marioAIOptions.setAgent(agent);    
        
        marioAIOptions.setFPS(GlobalOptions.MaxFPS);
        marioAIOptions.setVisualization(false);
        marioAIOptions.setLevelRandSeed(1);
        final BasicTask basicTask = new BasicTask(marioAIOptions);
        
        
        marioAIOptions.setFPS(25);
        marioAIOptions.setVisualization(true);
        //marioAIOptions.setLevelRandSeed(1);
        basicTask.runSingleEpisode(1);
        System.out.println(basicTask.getEnvironment().getEvaluationInfoAsString());
    }
}
