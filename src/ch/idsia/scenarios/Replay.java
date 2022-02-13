/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
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

package ch.idsia.scenarios;

import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EFeature;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.HumanData;
import ch.idsia.agents.EvolutionalNeuralNetwork.Humanlike.PenaltiesAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.tasks.ReplayTask;
import ch.idsia.tools.MarioAIOptions;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, sergey.karakovskiy@gmail.com
 * Date: Oct 9, 2010
 * Time: 8:00:13 PM
 * Package: ch.idsia.scenarios
 */
public class Replay
{
    public static void main(String[] args)
    {
//        System.out.println("IsOnGround STAND DOWN RIGHT LEFT JUMP SPEED R+J L+J R+S L+S R+J+S L+J+S ChangeActions COIN Jumps TimeSpent TimeLeft FinishTick TotalKills SeqSize Status");
//        for(int i = 0; i < 5; ++i)
//        {
//            runReplayFile("ReplayTung_0_"+i+"_0");
//        }
        String[] playerName = new String[5];
        playerName[0] = "Luong";
        playerName[1] = "Duy";
        playerName[2] = "Vu";
        playerName[3] = "Sila";
        playerName[4] = "Tung";
        String replayName = "";
        
        //EFeature.printTitle();
        //System.out.println(" Mushroom Flower Seq TotalCoin TotalEnemy TotalMushroom Score");
        final PenaltiesAgent agent = new PenaltiesAgent();
        agent.fea.printTitle();
        System.out.println(" TotalCoins TotalEnemies");
        //System.out.println(" Seq ");
        for(int i = 0; i < playerName.length; ++i)
        {
            for(int j = 0; j < 5; ++j)
            {
                runReplayFile("Replay"+playerName[i]+""+"_0_"+j+"_0");
            }
        }
        System.exit(0);
    }
    
    public static void runReplayFile(String filename)
    {
        final MarioAIOptions cmdLineOptions = new MarioAIOptions(filename);
        final ReplayTask replayTask = new ReplayTask();
        replayTask.reset(filename);
        cmdLineOptions.setVisualization(false);
        cmdLineOptions.setFPS(30);
        //Start replay
        replayTask.startReplay();
        //Print stats
        //printStatistics(replayTask.getEnvironment());
    }

    
    public static void printStatistics(Environment env)
    {
        //System.out.println(env.getEvaluationInfoAsString());
        //HumanData tmpDT = new HumanData("Stats", null);
        HumanData tmpDT = new HumanData();
                tmpDT.coinGained = env.getEvaluationInfo().coinsGained;
                tmpDT.killedByFire = env.getEvaluationInfo().killsByFire;
                tmpDT.killedByStomp = env.getEvaluationInfo().killsByStomp;
                tmpDT.killedByShell = env.getEvaluationInfo().killsByShell;
                tmpDT.totalKilled = env.getEvaluationInfo().killsTotal;
                tmpDT.timeSpent = env.getEvaluationInfo().timeSpent;
                tmpDT.timeLeft = env.getEvaluationInfo().timeLeft;
        System.out.println();
    }
}
