/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Emotions;

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;

/**
 *
 * @author Phuc
 */
public class EmotionTest {
    
    public static void main(String[] args) {
        
        //final String argsString = "-fps 1000";
        //final String argsString = "-le off -lb off";
        //final String argsString = "-trace on";
        //final MarioAIOptions marioAIOptions = new MarioAIOptions(argsString);
        final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
        //final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
        final BasicTask basicTask = new BasicTask(marioAIOptions);
        marioAIOptions.setVisualization(true);
        marioAIOptions.setLevelDifficulty(0);
        marioAIOptions.setLevelRandSeed(0);
        marioAIOptions.setFPS(30);
        marioAIOptions.setReceptiveFieldHeight(7);
        marioAIOptions.setReceptiveFieldWidth(7);
        
        EmotionalHumanAgent agent = new EmotionalHumanAgent();
        agent.setnInput(7);
        agent.initializeEmotionUnit();
        marioAIOptions.setAgent(agent);
        //marioAIOptions.setZLevelScene(5);
        
        agent.track.clear();
        agent.agentTrace.clear();
        agent.oldState = null;
        agent.oldAction = null;
        marioAIOptions.setLevelRandSeed(0);
        basicTask.runSingleEpisode(1);    
        
        
//        EAction a = new EAction("left");
//        System.out.println(a.toString());
        
//        List<HashMap<EState,EAction>> listHashMap = new ArrayList<>();
//        String currentDir = "";
//            try {
//                currentDir = new java.io.File( "." ).getCanonicalPath();
//            } catch (IOException ex) {
//                System.out.println(ex.getMessage());
//            }
//            String folder = currentDir + "\\HumanTraceData\\"+"LuongGson"+"\\";
//            
//            for(int dif = 0; dif < 1; ++dif)
//            {
//                //for(int lev = 0; lev < noLev; lev+=15)
//                for(int lev = 0; lev < 1; lev+=1)
//                {
//                    
//                    try
//                    {
//                        BufferedReader br = new BufferedReader(new FileReader(folder + "hashmapHumanPlay"+"LuongGson"+"_"+dif+"_"+lev));
//                        
//                        HashMap<EState, EAction> humanTrack = new HashMap<>();
//                        
//                        String line = br.readLine();
//                        while(line != null)
//                        {
//                            System.out.println(line);
//                            String[] record = line.split("###");
//                            EState tmpS = new Gson().fromJson(record[0], EState.class);
//                            EAction tmpA = new Gson().fromJson(record[1], EAction.class);
//                            humanTrack.put(tmpS, tmpA);
//
//                            listHashMap.add(humanTrack);
//                            line = br.readLine();
//                        }
//                        br.close();
//                    }
//                    catch(IOException ex)
//                    {
//                        System.out.println(ex.getMessage());
//                    }
//                }
//            }
//        
//        System.out.println(listHashMap.size());
        System.exit(0);
    }
    
}
