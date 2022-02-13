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

package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EAction;
import ch.idsia.benchmark.mario.engine.Replayer;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, sergey.karakovskiy@gmail.com
 * Date: Oct 9, 2010
 * Time: 2:08:47 AM
 * Package: ch.idsia.agents.controllers
 */
public class ReplayAgent implements Agent
{
private Replayer replayer;
private String name;

byte[][] levelScene;
byte[][] enemies;
byte[][] mergedObservation;
double[][] inputObservation;
double[][] inputEnemiesObservation;

public List<EAction> listActs = new ArrayList<>();


public ReplayAgent(String name)
{
    setName("Replay<" + name + ">");
}

//this method should return mario state and position array
//byte[] TODO: fix comment

public void setReplayer(Replayer replayer)
{
    this.replayer = replayer;
}

public boolean[] getAction()
{
//    for(int i = 0; i < mergedObservation.length; ++i)
//    {
//        for(int j = 0; j < mergedObservation[i].length; ++j)
//        {
//            System.out.print(mergedObservation[i][j] + " ");
//        }
//        System.out.println("");
//    }
    //handle the "Out of time" case
    boolean[] action = null;
    try
    {
        action = replayer.readAction();
        listActs.add(new EAction(action));
    } catch (IOException e)
    {
        System.err.println("[Mario AI Exception] : ReplayAgent is not able to read next action");
        e.printStackTrace();
    }
    return action;
}

public void integrateObservation(final Environment environment)
{   

//    levelScene = environment.getLevelSceneObservationZ(1);
//    enemies = environment.getEnemiesObservationZ(0);
//    mergedObservation = environment.getMergedObservationZZ(1, 0);
//                
//       
//        // It also possible to use direct methods from Environment interface.
//        //
//        
//        
//        int SizeInputObs = 7;
//        inputObservation = new double[SizeInputObs][SizeInputObs];
//        inputEnemiesObservation = new double[SizeInputObs][SizeInputObs];
//        int z1, z2;
//        z1 = 0;
//        z2 = 0;
//        for(int i = 0; i < mergedObservation.length; ++i)
//        {
//            for(int j = 0; j < mergedObservation[0].length; ++j)
//            {
//                if(i > (mergedObservation.length - SizeInputObs)/2 - 1
//                        && j > (mergedObservation.length - SizeInputObs)/2 - 1
//                        && i < mergedObservation.length - (mergedObservation.length - SizeInputObs-1)/2 - 1 
//                        && j < mergedObservation.length - (mergedObservation.length - SizeInputObs-1)/2 - 1)
//                {
//                    inputObservation[z1][z2] = mergedObservation[i][j];
//                    inputEnemiesObservation[z1][z2] = enemies[i][j];
//                    z2++;
//                    if(z2 == SizeInputObs)
//                    {
//                        z1++;
//                        z2 = 0;
//                    }
//                }
//            }
//        }
}

public void giveIntermediateReward(final float intermediateReward)
{}

public void reset()
{}

public void setObservationDetails(final int rfWidth, final int rfHeight, final int egoRow, final int egoCol)
{}

public String getName()
{
    return name;
}

public void setName(final String name)
{
    this.name = name;
}
}
