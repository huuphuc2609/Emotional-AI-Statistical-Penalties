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

package ch.idsia.agents.MyAgent;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.ForwardJumpingAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.benchmark.tasks.MarioCustomSystemOfValues;
import ch.idsia.tools.MarioAIOptions;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey at idsia dot ch Date: Mar 17, 2010 Time: 8:28:00 AM
 * Package: ch.idsia.scenarios
 */
public final class CollectData
{
public static void main(String[] args)
{
    final CollectorAgent a = new CollectorAgent();
    final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
    
    try
    {
//        FileWriter fw = new FileWriter("tracking.txt");
//        a.setOutputFile(fw);
        marioAIOptions.setAgent(a);
    
        //Set observation size
        marioAIOptions.setReceptiveFieldHeight(19);
        marioAIOptions.setReceptiveFieldWidth(19);

        final BasicTask basicTask = new BasicTask(marioAIOptions);
        final List<String> listTrack = new ArrayList<>();
        
        //marioAIOptions.setVisualization(true);
    
    //        basicTask.reset(marioAIOptions);
        final MarioCustomSystemOfValues m = new MarioCustomSystemOfValues();
    //        basicTask.runSingleEpisode();
        // run 1 episode with same options, each time giving output of Evaluation info.
        // verbose = false
        for(int i = 0; i < 5; ++i)
        {
            long startTime = System.currentTimeMillis();
            marioAIOptions.setLevelRandSeed(i);
            marioAIOptions.setLevelDifficulty(0);
            marioAIOptions.setFPS(25);
            //basicTask.doEpisodes(1, false, 1);
            basicTask.runSingleEpisode(1);
            System.out.println("\nEvaluationInfo: \n" + basicTask.getEnvironment().getEvaluationInfoAsString());
            System.out.println("\nCustom : \n" + basicTask.getEnvironment().getEvaluationInfo().computeWeightedFitness(m));
            
            long duration = (int)(System.currentTimeMillis() - startTime);
            System.out.println("Time: " + duration);
        }
//        track.stop();
//        for(int n = 0; n < listTrack.size(); ++n)
//        {
//            fw.write(listTrack.get(n));
//            fw.write("\r\n");
//        }
//        fw.close();
    }
    catch(Exception ex)
    {}
    System.exit(0);
    }
}
