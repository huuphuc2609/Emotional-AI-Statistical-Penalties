/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Emotions;


import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Phuc
 * Collecting human data
 */
public class AnalyzingHumanData {
    
    public static void main(String[] args) {
        
//        "Stand"
//        "Left"
//        "LeftRight"
//        "LeftDown"
//        "LeftJump"
//        "LeftSpeed"
//        "LeftUp"
//        "Right"
//        "RightDown"
//        "RightJump"
//        "RightSpeed"
//        "RightUp"
//        "Down"
//        "DownJump"
//        "DownSpeed"
//        "DownUp"
//        "Jump"
//        "JumpSpeed"
//        "JumpUp"
//        "Speed"
//        "SpeedUp"
//        "Up"
//        "LeftRightDown"
//        "LeftRightJump"
//        "LeftRightSpeed"
//        "LeftRightUp"
//        "LeftDownJump"
//        "LeftDownSpeed"
//        "LeftDownUp"
//        "LeftJumpSpeed"
//        "LeftJumpUp"
//        "LeftSpeedUp"
//        "RightDownJump"
//        "RightDownSpeed"
//        "RightDownUp"
//        "RightJumpSpeed"
//        "RightJumpUp"
//        "RightSpeedUp"
//        "DownJumpSpeed"
//        "DownJumpUp"
//        "DownSpeedUp"
//        "JumpSpeedUp"
//        "LeftRightDownJump"
//        "LeftRightDownSpeed"
//        "LeftRightDownUp"
//        "LeftRightJumpSpeed"
//        "LeftRightJumpUp"               
//        "LeftRightSpeedUp"
//        "LeftDownJumpSpeed"
//        "LeftDownJumpUp"
//        "LeftDownSpeedUp"            
//        "LeftJumpSpeedUp"
//        "RightDownJumpSpeed"
//        "RightDownJumpUp"
//        "RightDownSpeedUp"
//        "RightJumpSpeedUp"
//        "DownJumpSpeedUp"
//        "LeftRightDownJumpSpeed"
//        "LeftRightDownJumpUp"
//        "LeftRightDownSpeedUp"
//        "LeftRightJumpSpeedUp"
//        "LeftDownJumpSpeedUp"
//        "RightDownJumpSpeedUp"
//        "LeftRightDownJumpSpeedUp"

//        System.out.println("Convert to int: ");
//        EAction tmpAct = new EAction("Stand");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("Left");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftRight");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftDown");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftJump");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftSpeed");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("Right");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("RightDown");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("RightJump");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("RightSpeed");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("RightUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("Down");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("DownJump");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("DownSpeed");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("DownUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("Jump");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("JumpSpeed");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("JumpUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("Speed");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("SpeedUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("Up");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftRightDown");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftRightJump");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftRightSpeed");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftRightUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftDownJump");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftDownSpeed");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftDownUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftJumpSpeed");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftJumpUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftSpeedUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("RightDownJump");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("RightDownSpeed");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("RightDownUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("RightJumpSpeed");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("RightJumpUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("RightSpeedUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("DownJumpSpeed");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("DownJumpUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("DownSpeedUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("JumpSpeedUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftRightDownJump");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftRightDownSpeed");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftRightDownUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftRightJumpSpeed");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftRightJumpUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftRightSpeedUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftDownJumpSpeed");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftDownJumpUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftDownSpeedUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftJumpSpeedUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("RightDownJumpSpeed");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("RightDownJumpUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("RightDownSpeedUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("RightJumpSpeedUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("DownJumpSpeedUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftRightDownJumpSpeed");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftRightDownJumpUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftRightDownSpeedUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftRightJumpSpeedUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftDownJumpSpeedUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("RightDownJumpSpeedUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
//        tmpAct = new EAction("LeftRightDownJumpSpeedUp");
//        System.out.println(EAction.convertBoolToInt(tmpAct.action));
        
        
        List<HumanData> lHD = new ArrayList<>();       
        readHumanData(lHD);
        //System.out.println("No ID RIGHT LEFT JUMP RUN R+J L+J R+R L+R R+J+R L+J+R ChangeActions COIN Jumps TimeSpent TimeLeft FinishTick TotalKills SeqSize AverageMinDistClosestEne mindistene");
        //System.out.println("No ID IllegalAct OnGround RightLeft STAND DOWN RIGHT LEFT JUMP RUN R+J L+J R+S L+S R+J+S L+J+S ChangeActions COIN Jumps TimeSpent TimeLeft FinishTick TotalKills SeqSize Status TotalCoin TotalEnemies");
        EAllActions.printTitle();
        System.out.println(" Seq");
        for(int i = 0; i < lHD.size(); ++i)
        {
            EFeature feature = new EFeature();
            
            int ChangeActions = 0;
            HumanData tmp = lHD.get(i);
            int numJumpButton = 0;
            int numOfJump = 0;
            EAction prevAction = null;
            
            tmp.numberOfRight = 0;
            tmp.numberOfLeft = 0;
            tmp.numberOfJump = 0;
            tmp.numberOfRun = 0;
            
            int illegalAct = 0;
            EAllActions allActs = new EAllActions();
            for(int j = 0; j < tmp.seqStateAction.size(); ++j)
            {
//                if(tmp.seqStateAction.get(j).s.dataVector[1] == 1)
//                    onGround++;
//                if(tmp.seqStateAction.get(j).a.action[3])
//                    numJumpButton++;
//                tmp.seqStateAction.get(j).a.action[2] = false;
//                tmp.seqStateAction.get(j).a.action[5] = false;
                
                allActs.increaseCounter(tmp.seqStateAction.get(j).a);
  
                            
//                if (i == lHD.size()-1)
//                {
//                    if(tmp.seqStateAction.get(j).a.action[1])
//                        for(int z = 0; z < 6; ++z)
//                        {
//                            if(tmp.seqStateAction.get(j).a.action[z])
//                                System.out.print("true" + " ");
//                            else
//                                System.out.print("false" + " ");
//                        }
//                    System.out.println("");
//                }
                //Combined actions
//                if(tmp.seqStateAction.get(j).a.action[0] && tmp.seqStateAction.get(j).a.action[3])
//                    LJ++;
//                if(tmp.seqStateAction.get(j).a.action[1] && tmp.seqStateAction.get(j).a.action[3])
//                    RJ++;
//                if(tmp.seqStateAction.get(j).a.action[0] && tmp.seqStateAction.get(j).a.action[4])
//                    LR++;
//                if(tmp.seqStateAction.get(j).a.action[1] && tmp.seqStateAction.get(j).a.action[4])
//                    RR++;
//                if(tmp.seqStateAction.get(j).a.action[0] && tmp.seqStateAction.get(j).a.action[3] && tmp.seqStateAction.get(j).a.action[4])
//                    LJR++;
//                if(tmp.seqStateAction.get(j).a.action[1] && tmp.seqStateAction.get(j).a.action[3] && tmp.seqStateAction.get(j).a.action[4])
//                    RJR++;
           
//                if(prevAction != null)
//                {
//                    if(!prevAction.equals(tmp.seqStateAction.get(j).a))
//                        ChangeActions++;
//                    if(prevAction.action[3] == false && tmp.seqStateAction.get(j).a.action[3] == true)
//                        numOfJump++;
//                }
//                prevAction = tmp.seqStateAction.get(j).a;
//                
//                int countTrue = 0;
//                for(int k = 0; k < tmp.seqStateAction.get(j).a.action.length; ++k)
//                {
//                    if(tmp.seqStateAction.get(j).a.action[k])
//                        countTrue++;
//                }
//                if(countTrue > 3)
//                    illegalAct++;
            }
            allActs.printValues();
            System.out.println(tmp.seqStateAction.size());
            
//            System.out.print(i + " " + tmp.id + " ");
//            System.out.print(illegalAct + " ");
//            
//            System.out.print(ChangeActions + " ");
//            System.out.print(tmp.coinGained + " ");
//            System.out.print(tmp.timeSpent + " ");
//            System.out.print(tmp.timeLeft + " ");
//            System.out.print(tmp.totalKilled + " ");
            double averageMinDistance = 0;
            for(int j = 0; j < tmp.seqStateAction.size(); ++j)
            {
                averageMinDistance += tmp.seqStateAction.get(j).s.dataVector[10];
            }
            averageMinDistance/=tmp.seqStateAction.size();
            //System.out.print(averageMinDistance + " ");
            
            double sum_dist_enemy = 0;
            double min_dis = 100000;
            for(int j = 0; j < tmp.seqStateAction.size(); ++j)
            {
                for(int k = 0; k < tmp.seqStateAction.get(j).s.enemies.length; k+=3)
                {
                    sum_dist_enemy = Math.sqrt((tmp.seqStateAction.get(j).s.enemies[k+1]-tmp.seqStateAction.get(j).s.dataVector[7])*(tmp.seqStateAction.get(j).s.enemies[k+1]-tmp.seqStateAction.get(j).s.dataVector[7])
                            +(tmp.seqStateAction.get(j).s.enemies[k+2]-tmp.seqStateAction.get(j).s.dataVector[8])*(tmp.seqStateAction.get(j).s.enemies[k+2]-tmp.seqStateAction.get(j).s.dataVector[8]));
                    if(sum_dist_enemy < min_dis)
                        min_dis = sum_dist_enemy;
                }
            }
            sum_dist_enemy = sum_dist_enemy/tmp.seqStateAction.size();
            //System.out.println(min_dis);
        }
        
//        System.out.println();
//        
//        System.out.println("Test");
//        Random testRand = new Random(1);
//        for(int i = 0; i < 10; ++i)
//        {
//            System.out.println(testRand.nextInt(10));
//        }
//        System.out.println("Test1");
//        Random testRand1 = new Random(1);
//        for(int i = 0; i < 10; ++i)
//        {
//            System.out.println(testRand1.nextInt(10));
//        }
//        System.out.println("Test2");
//        Random testRand2 = new Random(1);
//        for(int i = 0; i < 10; ++i)
//        {
//            System.out.println(testRand2.nextDouble());
//        }
    }
    
    public static void readHumanData(List<HumanData> lHD)
    {
        int noH = 5; //Number of human players
//        int noDif = 2;
//        int noLev = 61;
        int noDif = 0;
        int noLev = 5;
        String[] playerName = new String[noH];
        playerName[0] = "Luong";
        playerName[1] = "Duy";
        playerName[2] = "Vu";
        playerName[3] = "Sila";
        playerName[4] = "Tung";
//        playerName[0] = "LuongSpeed";
//        playerName[1] = "DuySpeed";
//        playerName[2] = "VuSpeed";
//        playerName[3] = "SilaSpeed";
//        playerName[4] = "TungSpeed";
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
                dif = 0;
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
}
