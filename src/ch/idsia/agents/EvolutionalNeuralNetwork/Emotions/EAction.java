/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Emotions;

import com.google.gson.Gson;

/**
 *
 * @author Phuc
 */
public class EAction {
    
    public boolean[] action = null;
    
    public EAction()
    {
        action = new boolean[]{false, false, false, false, false, false};
    }
    
    public EAction(boolean[] a)
    {
        action = new boolean[a.length];
        for(int i = 0; i < a.length; ++i)
        {
            action[i] = a[i];
        }
    }
    
    public EAction(EAction a)
    {
        action = new boolean[a.action.length];
        for(int i = 0; i < a.action.length; ++i)
        {
            action[i] = a.action[i];
        }
    }
    
    public EAction(String s)
    {
        switch(s)
        {
            case "Stand":
                action = new boolean[]{false, false, false, false, false, false};
                break;
            
            case "Left":
                action = new boolean[]{true, false, false, false, false, false};
                break;
            case "LeftRight":
                action = new boolean[]{true, true, false, false, false, false};
                break;
            case "LeftDown":
                action = new boolean[]{true, false, true, false, false, false};
                break;
            case "LeftJump":
                action = new boolean[]{true, false, false, true, false, false};
                break;
            case "LeftSpeed":
                action = new boolean[]{true, false, false, false, true, false};
                break;
            case "LeftUp":
                action = new boolean[]{true, false, false, false, false, true};
                break;
                
            case "Right":
                action = new boolean[]{false, true, false, false, false, false};
                break;
            case "RightDown":
                action = new boolean[]{false, true, true, false, false, false};
                break;
            case "RightJump":
                action = new boolean[]{false, true, false, true, false, false};
                break;
            case "RightSpeed":
                action = new boolean[]{false, true, false, false, true, false};
                break;
            case "RightUp":
                action = new boolean[]{false, true, false, false, false, true};
                break;
            
                
            case "Down":
                action = new boolean[]{false, false, true, false, false, false};
                break;
            case "DownJump":
                action = new boolean[]{false, false, true, true, false, false};
                break;
            case "DownSpeed":
                action = new boolean[]{false, false, true, false, true, false};
                break;
            case "DownUp":
                action = new boolean[]{false, false, true, false, false, true};
                break;
            
            case "Jump":
                action = new boolean[]{false, false, false, true, false, false};
                break;
            case "JumpSpeed":
                action = new boolean[]{false, false, false, true, true, false};
                break;
            case "JumpUp":
                action = new boolean[]{false, false, false, true, false, true};
                break;

            case "Speed":
                action = new boolean[]{false, false, false, false, true, false};
                break;
            case "SpeedUp":
                action = new boolean[]{false, false, false, false, true, true};
                break;
            
            case "Up":
                action = new boolean[]{false, false, false, false, false, true};
                break;
            
            //3 buttons
            case "LeftRightDown":
                action = new boolean[]{true, true, true, false, false, false};
                break;
            case "LeftRightJump":
                action = new boolean[]{true, true, false, true, false, false};
                break;
            case "LeftRightSpeed":
                action = new boolean[]{true, true, false, false, true, false};
                break;
            case "LeftRightUp":
                action = new boolean[]{true, true, false, false, false, true};
                break;
            case "LeftDownJump":
                action = new boolean[]{true, false, true, true, false, false};
                break;
            case "LeftDownSpeed":
                action = new boolean[]{true, false, true, false, true, false};
                break;
            case "LeftDownUp":
                action = new boolean[]{true, false, true, false, false, true};
                break;
            case "LeftJumpSpeed":
                action = new boolean[]{true, false, false, true, true, false};
                break;    
            case "LeftJumpUp":
                action = new boolean[]{true, false, false, true, false, true};
                break;
            case "LeftSpeedUp":
                action = new boolean[]{true, false, false, false, true, true};
                break;
                
            case "RightDownJump":
                action = new boolean[]{false, true, true, true, false, false};
                break;
            case "RightDownSpeed":
                action = new boolean[]{false, true, true, false, true, false};
                break;
            case "RightDownUp":
                action = new boolean[]{false, true, true, false, false, true};
                break;
            case "RightJumpSpeed":
                action = new boolean[]{false, true, false, true, true, false};
                break;
            case "RightJumpUp":
                action = new boolean[]{false, true, false, true, false, true};
                break;
            case "RightSpeedUp":
                action = new boolean[]{false, true, false, false, true, true};
                break;
            
            case "DownJumpSpeed":
                action = new boolean[]{false, false, true, true, true, false};
                break;   
            case "DownJumpUp":
                action = new boolean[]{false, false, true, true, false, true};
                break;
            case "DownSpeedUp":
                action = new boolean[]{false, false, true, false, true, true};
                break;
                
            case "JumpSpeedUp":
                action = new boolean[]{false, false, false, true, true, true};
                break;
                
            //4 buttons
            case "LeftRightDownJump":
                action = new boolean[]{true, true, true, true, false, false};
                break;
            case "LeftRightDownSpeed":
                action = new boolean[]{true, true, true, false, true, false};
                break;
            case "LeftRightDownUp":
                action = new boolean[]{true, true, true, false, false, true};
                break;
                
            case "LeftRightJumpSpeed":
                action = new boolean[]{true, true, false, true, true, false};
                break;
            case "LeftRightJumpUp":
                action = new boolean[]{true, true, false, true, false, true};
                break;
                
            case "LeftRightSpeedUp":
                action = new boolean[]{true, true, false, false, true, true};
                break;
                
            case "LeftDownJumpSpeed":
                action = new boolean[]{true, false, true, true, true, false};
                break;
            case "LeftDownJumpUp":
                action = new boolean[]{true, false, true, true, false, true};
                break;
            case "LeftDownSpeedUp":
                action = new boolean[]{true, false, true, false, true, true};
                break;
                
            case "LeftJumpSpeedUp":
                action = new boolean[]{true, false, false, true, true, true};
                break;

            case "RightDownJumpSpeed":
                action = new boolean[]{false, true, true, true, true, false};
                break;
            case "RightDownJumpUp":
                action = new boolean[]{false, true, true, true, false, true};
                break;
            case "RightDownSpeedUp":
                action = new boolean[]{false, true, true, false, true, true};
                break;
            case "RightJumpSpeedUp":
                action = new boolean[]{false, true, false, true, true, true};
                break;
                
            case "DownJumpSpeedUp":
                action = new boolean[]{false, false, true, true, true, true};
                break;
            
            //5 buttons
            case "LeftRightDownJumpSpeed":
                action = new boolean[]{true, true, true, true, true, false};
                break;
            case "LeftRightDownJumpUp":
                action = new boolean[]{true, true, true, true, false, true};
                break;
            case "LeftRightDownSpeedUp":
                action = new boolean[]{true, true, true, false, true, true};
                break;
            case "LeftRightJumpSpeedUp":
                action = new boolean[]{true, true, false, true, true, true};
                break;
            case "LeftDownJumpSpeedUp":
                action = new boolean[]{true, false, true, true, true, true};
                break;
            case "RightDownJumpSpeedUp":
                action = new boolean[]{false, true, true, true, true, true};
                break;
            
            //6 buttons
            case "LeftRightDownJumpSpeedUp":
                action = new boolean[]{true, true, true, true, true, true};
                break;
                
            default:
                System.out.println("Error action!");
        }
    }

    public void destruct()
    {
        action = null;
    }
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        return this.equals((EAction) obj);
//    }
       
    public boolean equals(EAction e)
    {
        if(e.toInt() == this.toInt())
        {
                return false;
        }
        return true;
    }
    
    
    @Override
    public String toString()
    {
        return new Gson().toJson(this);
    }
    
    public int toInt()
    {
        int result = 0;
        for (int i = 0; i < action.length; ++i) {
            result = (result << 1) + (action[i] ? 1 : 0);
        }
        return result;
    }
    
    public static int convertBoolToInt(boolean[] bool)
    {
        int result = 0;
        for (int i = 0; i < bool.length; ++i) {
            result = (result << 1) + (bool[i] ? 1 : 0);
        }
        return result;
    }
}
