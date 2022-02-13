/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.StatisticalPenaltiesAgent;

import ch.idsia.agents.EvolutionalNeuralNetwork.Emotions.EAction;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Phuc
 */
public class testTime {
    public static void main(String[] args) {
        int a1 = 0;
        int a2 = 0;
        int a3 = 0;
        int a4 = 0;
        int a5 = 0;
        int a6 = 0;
        List<EAction> listAct = new ArrayList<>();
        for(int i = 0; i < 8; ++i)
        {
            EAction tmp0 = new EAction("LeftRightDownJumpSpeedUp");
            EAction tmp1 = new EAction("Stand");
            EAction tmp2 = new EAction("Left");         
            EAction tmp3 = new EAction("LeftRight");
            EAction tmp4 = new EAction("LeftDown");
            EAction tmp5 = new EAction("LeftJump");
            EAction tmp6 = new EAction("LeftSpeed");
            EAction tmp7 = new EAction("LeftUp");
            EAction tmp8 = new EAction("Right");
            EAction tmp9 = new EAction("RightDown");
            EAction tmp10 = new EAction("RightJump");
            EAction tmp11 = new EAction("RightSpeed");
            EAction tmp12 = new EAction("RightUp");
            EAction tmp13 = new EAction("Down");
            EAction tmp14 = new EAction("DownJump");
            EAction tmp15 = new EAction("DownSpeed");
            EAction tmp16 = new EAction("DownUp");           
            EAction tmp17 = new EAction("Jump");
            EAction tmp18 = new EAction("JumpSpeed");
            EAction tmp19 = new EAction("JumpUp");
            EAction tmp20 = new EAction("Speed");
            EAction tmp21 = new EAction("SpeedUp");           
            EAction tmp22 = new EAction("Up");
            EAction tmp23 = new EAction("LeftRightDown");
            EAction tmp24 = new EAction("LeftRightJump");
            EAction tmp25 = new EAction("LeftRightSpeed");
            EAction tmp26 = new EAction("LeftRightUp");
            EAction tmp27 = new EAction("LeftDownJump");
            EAction tmp28 = new EAction("LeftDownSpeed");
            EAction tmp29 = new EAction("LeftDownUp");
            EAction tmp30 = new EAction("LeftJumpSpeed"); 
            EAction tmp31 = new EAction("LeftJumpUp");
            EAction tmp32 = new EAction("LeftSpeedUp");
            EAction tmp33 = new EAction("RightDownJump");
            EAction tmp34 = new EAction("RightDownSpeed");
            EAction tmp35 = new EAction("RightDownUp");
            EAction tmp36 = new EAction("RightJumpSpeed");
            EAction tmp37 = new EAction("RightJumpUp");
            EAction tmp38 = new EAction("RightSpeedUp");
            EAction tmp39 = new EAction("DownJumpSpeed");
            EAction tmp40 = new EAction("DownJumpUp");
            EAction tmp41 = new EAction("DownSpeedUp");
            EAction tmp42 = new EAction("JumpSpeedUp");
            EAction tmp43 = new EAction("LeftRightDownJump");
            EAction tmp44 = new EAction("LeftRightDownSpeed");
            EAction tmp45 = new EAction("LeftRightDownUp");               
            EAction tmp46 = new EAction("LeftRightJumpSpeed");
            EAction tmp47 = new EAction("LeftRightJumpUp");                
            EAction tmp48 = new EAction("LeftRightSpeedUp");
            EAction tmp49 = new EAction("LeftDownJumpSpeed");
            EAction tmp50 = new EAction("LeftDownJumpUp");
            
            listAct.add(tmp0);
            listAct.add(tmp1);
            listAct.add(tmp2);
            listAct.add(tmp3);
            listAct.add(tmp4);
            listAct.add(tmp5);
            listAct.add(tmp6);
            listAct.add(tmp7);
            listAct.add(tmp8);
            listAct.add(tmp9);
            listAct.add(tmp10);
            listAct.add(tmp11);
            listAct.add(tmp12);
            listAct.add(tmp13);
            listAct.add(tmp14);
            listAct.add(tmp15);
            listAct.add(tmp16);
            listAct.add(tmp17);
            listAct.add(tmp18);
            listAct.add(tmp19);
            listAct.add(tmp20);
            listAct.add(tmp21);
            listAct.add(tmp22);
            listAct.add(tmp23);
            listAct.add(tmp24);
            listAct.add(tmp25);
            listAct.add(tmp26);
            listAct.add(tmp27);
            listAct.add(tmp28);
            listAct.add(tmp29);
            listAct.add(tmp30);
            listAct.add(tmp31);
            listAct.add(tmp32);
            listAct.add(tmp33);
            listAct.add(tmp34);
            listAct.add(tmp35);
            listAct.add(tmp36);
            listAct.add(tmp37);
            listAct.add(tmp38);
            listAct.add(tmp39);
            listAct.add(tmp40);
            listAct.add(tmp41);
            listAct.add(tmp42);
            listAct.add(tmp43);
            listAct.add(tmp44);
            listAct.add(tmp45);
            listAct.add(tmp46);
            listAct.add(tmp47);
            listAct.add(tmp48);
            listAct.add(tmp49);
            listAct.add(tmp50);
        }
        for(int z = 0; z < 1; ++z)
        {
            long start = System.nanoTime();
            Collections.shuffle(listAct);
            for(int i = 0; i < listAct.size(); ++i)
            {
                for(int j = 0; j < listAct.get(i).action.length; ++j)
                {
                    if(listAct.get(i).action[j])
                    {
                        switch(j)
                        {
                            case 0: a1++; break;
                            case 1: a2++; break;
                            case 2: a3++; break;
                            case 3: a4++; break;
                            case 4: a5++; break;
                            case 5: a6++; break;
                        }
                    }
                }
            }
            long stop = System.nanoTime();


            double dif = 1.0*(stop-start)/1000/1000;
            System.out.println(a1 + " " + a2 + " " + a3 + " " + a4 + " " + a5 + " " + a6);
            String result = String.format("%.2f",dif);
            System.out.println("Count time: " + result);
        }
    }
}
