package ch.idsia.agents.EvolutionalNeuralNetwork;

import ch.idsia.evolution.Evolvable;
import ch.idsia.agents.Agent;
import ch.idsia.agents.AgentsPool;
import ch.idsia.agents.learning.SimpleMLPAgent;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.benchmark.tasks.LearningTask;
import ch.idsia.benchmark.tasks.MultiSeedProgressTask;
import ch.idsia.benchmark.tasks.ProgressTask;
import ch.idsia.benchmark.tasks.Task;
import ch.idsia.evolution.ea.ES;
import ch.idsia.tools.MarioAIOptions;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 4, 2009
 * Time: 4:33:25 PM
 */
public class EvolveIncrementally {

    final static int generations = 100;
    final static int populationSize = 100;


    public static void main(String[] args) {
        
        final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
        Evolvable initial = new SimpleMLPAgent();

        AgentsPool.addAgent((Agent)initial);
        for (int difficulty = 0; difficulty < 11; difficulty++)
        {
            System.out.println("New EvolveIncrementally phase with difficulty = " + difficulty + " started.");
            marioAIOptions.setLevelDifficulty(difficulty);
            marioAIOptions.setFPS(100);
            marioAIOptions.setVisualization(false);
            //Task task = new ProgressTask(marioAIOptions);
            ProgressTask task = new ProgressTask(marioAIOptions);
            ES es = new ES (task, initial, populationSize);
            System.out.println("Evolving " + initial + " with task " + task);
            for (int gen = 0; gen < generations; gen++) {
                es.nextGeneration();
                double bestResult = es.getBestFitnesses()[0];
                System.out.println("Generation " + gen + " best " + bestResult);
                marioAIOptions.setVisualization(gen % 5 == 0 || bestResult > 4000);
                //marioAIOptions.setMaxFPS(true);
                Agent a = (Agent) es.getBests()[0];
                a.setName(((Agent)initial).getName() + gen);
//                AgentsPool.addAgent(a);
//                AgentsPool.setCurrentAgent(a);
                double result = task.evaluate(a);
                marioAIOptions.setVisualization(false);
                //marioAIOptions.setMaxFPS(true);
                //Easy.save (es.getBests()[0], "evolved.xml");
                if (result > 4000) {
                    initial = es.getBests()[0];
                    break; // Go to next difficulty.
                }
            }
        }
        System.exit(0);
    }
}
