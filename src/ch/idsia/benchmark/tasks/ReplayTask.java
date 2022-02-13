/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  Neither the name of the Mario AI nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ch.idsia.benchmark.tasks;

import ch.idsia.agents.Agent;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EAction;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EFeature;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.HumanData;
import ch.idsia.agents.controllers.ReplayAgent;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.mario.engine.Replayer;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;
import ch.idsia.tools.MarioAIOptions;
import ch.idsia.tools.ReplayerOptions;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, sergey.karakovskiy@gmail.com
 * Date: Oct 9, 2010
 * Time: 7:17:49 PM
 * Package: ch.idsia.benchmark.tasks
 */
public class ReplayTask implements Task
{
protected final static Environment environment = MarioEnvironment.getInstance();
private ReplayAgent agent;
private String name = getClass().getSimpleName();
private Replayer replayer;

public int numberOfEncouteringEnemies = 0;
    
public EFeature fea = new EFeature();
public int count = 0;
public EAction prevAct = null;
protected int[] marioState = null;
protected boolean isMarioOnGround;
int oldIsOnGround = 0; 

public ReplayTask()
{}

public void playOneFile(final MarioAIOptions options)
{
    ReplayerOptions.Interval interval = replayer.getNextIntervalInMarioseconds();
    if (interval == null)
    {
        interval = new ReplayerOptions.Interval(2, replayer.actionsFileSize());
    }
  
    while (!environment.isLevelFinished())
    {
        if (environment.getTimeSpent() == interval.from) //TODO: Comment this piece
            GlobalOptions.isVisualization = true;
        else if (environment.getTimeSpent() == interval.to)
        {
            GlobalOptions.isVisualization = false;
            interval = replayer.getNextIntervalInMarioseconds();
        }
        
        //**********Calculation of information***********ADDED BY LUONG**************
        byte[][] enemiesObs = environment.getEnemiesObservationZ(0);
        //for(int i = 0; i < enemiesObs.length; ++i)
        //{
            if(enemiesObs[8][8] != 0
            || enemiesObs[8][9] != 0
            || enemiesObs[8][10] != 0
            || enemiesObs[9][8] != 0
            || enemiesObs[9][10] != 0
            || enemiesObs[10][8] != 0
            || enemiesObs[10][9] != 0
            || enemiesObs[10][10] != 0)
                numberOfEncouteringEnemies++;
        //}
        //END*******Calculation of information***********ADDED BY LUONG**************
        //System.out.println(numberOfEncouteringEnemies);
        environment.tick();
        if (!GlobalOptions.isGameplayStopped)
        {
            
            boolean[] action = agent.getAction();
            if(action != null)
            {
                EAction act = new EAction(action);
                //Count actions
                fea.allAct.increaseCounter(act);
                fea.increaseIllegalAction(act);
                if(prevAct != null)
                {
                    if(!prevAct.equals(act))
                        fea.ChangeActions++;
                }
                
                if(environment.isMarioOnGround())
                    fea.isOnGround++;
                
                
                environment.performAction(action);
                prevAct = act;
                marioState = environment.getMarioState();
                isMarioOnGround = marioState[2] == 1;
                int isOnGround;
                if(isMarioOnGround)
                    isOnGround = 1;
                else
                    isOnGround = 0;
                if(oldIsOnGround == 1 && isOnGround != 1)
                {
                    fea.numJump++;
                }
                oldIsOnGround = (int)isOnGround;
                
                byte[][] mergedObservation = environment.getMergedObservationZZ(1,0);
                if(mergedObservation[8][10] < 0 ||
                mergedObservation[9][10] < 0 ||
                mergedObservation[8][11] < 0 ||
                mergedObservation[9][11] < 0)
                    fea.infrontObj++;
                
            }
        }
        count++;
        if (interval == null)
            break;
    }
    fea.coinGained = environment.getEvaluationInfo().coinsGained;
    fea.totalKilled = environment.getEvaluationInfo().killsTotal;
    fea.killByStomp = environment.getEvaluationInfo().killsByStomp;
    fea.killByFire = environment.getEvaluationInfo().killsByFire;
    fea.killByShell = environment.getEvaluationInfo().killsByShell;
    fea.timeSpent = environment.getEvaluationInfo().timeSpent;
    
    
    fea.printValues();
   
//    System.out.println(" " + environment.getEvaluationInfo().mushroomsDevoured + " " +
//            environment.getEvaluationInfo().flowersDevoured + " " + 
//            count + " " +
//            environment.getEvaluationInfo().totalNumberOfCoins + " " +
//            environment.getEvaluationInfo().totalNumberOfCreatures + " " +
//            environment.getEvaluationInfo().totalNumberOfMushrooms + " " +
//            environment.getEvaluationInfo().distancePassedPhys
//                    );
    System.out.print(environment.getEvaluationInfo().totalNumberOfCoins + " ");
    System.out.print(environment.getEvaluationInfo().totalNumberOfCreatures);
    System.out.println("");
}

public int evaluate(final Agent controller)
{
    return 0;
}

public void setOptionsAndReset(final MarioAIOptions options)
{}

public void setOptionsAndReset(final String options)
{
    //To change body of implemented methods use File | Settings | File Templates.
}

public void doEpisodes(final int amount, final boolean verbose, final int repetitionsOfSingleEpisode)
{}

public void startReplay()
{
    try
    {
        agent = new ReplayAgent("Replay agent");
        MarioAIOptions options = new MarioAIOptions();
        while (replayer.openNextReplayFile())
        {
            replayer.openFile("options");
            String strOptions = (String) replayer.readObject();
            options.setArgs(strOptions);
            //TODO: reset; resetAndSetArgs;
            options.setVisualization(true);
            options.setRecordFile("off");
            options.setFPS(30);
            agent.setName(options.getAgent().getName());
            options.setAgent(agent);
            agent.reset();
            agent.setReplayer(replayer);

            environment.setReplayer(replayer);
            environment.reset(options);
            GlobalOptions.isVisualization = false;

            replayer.openFile("actions.act");

            playOneFile(options);

            GlobalOptions.isVisualization = false;
//            replayer.closeFile();
            replayer.closeReplayFile();
        }
    } catch (IOException e)
    {
        e.printStackTrace();
    } catch (Exception e)
    {
        e.printStackTrace();
    }
    
    //Print actions list of a replay
    
//    for(int i = 0; i < agent.listActs.size(); ++i)
//    {
//        System.out.print(agent.listActs.get(i).toInt() + " ");
//    }
//    System.out.println("");
}

public boolean isFinished()
{
    return false;
}

public void reset(String replayOptions)
{
    replayer = new Replayer(replayOptions);
    GlobalOptions.isReplaying = true;
}

public void reset()
{}

public String getName()
{
    return name;
}

public void printStatistics()
{
    //To change body of implemented methods use File | Settings | File Templates.
}

public Environment getEnvironment()
{
    return environment;
}
}
