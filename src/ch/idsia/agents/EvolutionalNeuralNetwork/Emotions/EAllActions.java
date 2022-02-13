/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Emotions;

import java.util.HashMap;

/**
 *
 * @author Phuc
 */
public class EAllActions {
    
    public int ALLLEFT = 0;
    public int ALLRIGHT = 0;
    public int ALLRUN = 0;
    
    //0, 1 & 2 buttons : 22 actions
    public int Stand = 0;

    public int Left = 0;
    public int LR = 0;
    public int LD = 0;
    public int LJ = 0;
    public int LS = 0;
    public int LU = 0;

    public int Right = 0;
    public int RD = 0;
    public int RJ = 0;
    public int RS = 0;
    public int RU = 0;
    
    public int Down = 0;
    public int DJ = 0;
    public int DS = 0;
    public int DU = 0;
   
    public int Jump = 0;
    public int JS = 0;
    public int JU = 0;
    
    public int Speed = 0;
    public int SU = 0;
    
    public int Up = 0;
    
    //3 buttons: 20 actions
    public int LRD = 0;
    public int LRJ = 0;
    public int LRS = 0;
    public int LRU = 0;
    public int LDJ = 0;
    public int LDS = 0;
    public int LDU = 0;
    public int LJS = 0;
    public int LJU = 0;
    public int LSU = 0;
    
    public int RDJ = 0;
    public int RDS = 0;
    public int RDU = 0;
    public int RJS = 0;
    public int RJU = 0;
    public int RSU = 0;
    
    public int DJS = 0;
    public int DJU = 0;
    public int DSU = 0;
    
    public int JSU = 0;
    
    //4 buttons: 15 actions
    public int LRDJ = 0;
    public int LRDS = 0;
    public int LRDU = 0;
    public int LRJS = 0;
    public int LRJU = 0;
    public int LRSU = 0;
    public int LDJS = 0;
    public int LDJU = 0;
    public int LDSU = 0;
    public int LJSU = 0;
    
    public int RDJS = 0;
    public int RDJU = 0;
    public int RDSU = 0;
    public int RJSU = 0;
    
    public int DJSU = 0;
    
    //5 buttons: 6 actions
    public int LRDJS = 0;
    public int LRDJU = 0;
    public int LRDSU = 0;
    public int LRJSU = 0;
    public int LDJSU = 0;
    public int RDJSU = 0;
    //6 buttons: 1 actions
    public int LRDJSU = 0;
    
    public EAllActions(){};

    public EAllActions(EAllActions inAct) 
    {
        ALLLEFT = inAct.ALLLEFT;
        ALLRIGHT = inAct.ALLRIGHT;
        ALLRUN = inAct.ALLRUN;
        
        Stand = inAct.Stand;

        Left = inAct.Left;
        LR = inAct.LR;
        LD = inAct.LD;
        LJ = inAct.LJ;
        LS = inAct.LS;
        LU = inAct.LU;

        Right = inAct.Right;
        RD = inAct.RD;
        RJ = inAct.RJ;
        RS = inAct.RS;
        RU = inAct.RU;

        Down = inAct.Down;
        DJ = inAct.DJ;
        DS = inAct.DS;
        DU = inAct.DU;

        Jump = inAct.Jump;
        JS = inAct.JS;
        JU = inAct.JU;

        Speed = inAct.Speed;
        SU = inAct.SU;

        Up = inAct.Up;

        LRD = inAct.LRD;
        LRJ = inAct.LRJ;
        LRS = inAct.LRS;
        LRU = inAct.LRU;
        LDJ = inAct.LDJ;
        LDS = inAct.LDS;
        LDU = inAct.LDU;
        LJS = inAct.LJS;
        LJU = inAct.LJU;
        LSU = inAct.LSU;

        RDJ = inAct.RDJ;
        RDS = inAct.RDS;
        RDU = inAct.RDU;
        RJS = inAct.RJS;
        RJU = inAct.RJU;
        RSU = inAct.RSU;

        DJS = inAct.DJS;
        DJU = inAct.DJU;
        DSU = inAct.DSU;

        JSU = inAct.JSU;

        LRDJ = inAct.LRDJ;
        LRDS = inAct.LRDS;
        LRDU = inAct.LRDU;
        LRJS = inAct.LRJS;
        LRJU = inAct.LRJU;
        LRSU = inAct.LRSU;
        LDJS = inAct.LRJS;
        LDJU = inAct.LDJU;
        LDSU = inAct.LDSU;
        LJSU = inAct.LJSU;

        RDJS = inAct.RDJS;
        RDJU = inAct.RDJU;
        RDSU = inAct.RDSU;
        RJSU = inAct.RJSU;

        DJSU = inAct.DJSU;

        LRDJS = inAct.LRDJS;
        LRDJU = inAct.LRDJU;
        LRDSU = inAct.LRDSU;
        LRJSU = inAct.LRJSU;
        LDJSU = inAct.LDJSU;
        RDJSU = inAct.RDJSU;
 
        LRDJSU = inAct.LRDJSU;
    }
    
    public void assignData(EAllActions inAct)
    {
        ALLLEFT = inAct.ALLLEFT;
        ALLRIGHT = inAct.ALLRIGHT;
        ALLRUN = inAct.ALLRUN;
        
        Stand = inAct.Stand;

        Left = inAct.Left;
        LR = inAct.LR;
        LD = inAct.LD;
        LJ = inAct.LJ;
        LS = inAct.LS;
        LU = inAct.LU;

        Right = inAct.Right;
        RD = inAct.RD;
        RJ = inAct.RJ;
        RS = inAct.RS;
        RU = inAct.RU;

        Down = inAct.Down;
        DJ = inAct.DJ;
        DS = inAct.DS;
        DU = inAct.DU;

        Jump = inAct.Jump;
        JS = inAct.JS;
        JU = inAct.JU;

        Speed = inAct.Speed;
        SU = inAct.SU;

        Up = inAct.Up;

        LRD = inAct.LRD;
        LRJ = inAct.LRJ;
        LRS = inAct.LRS;
        LRU = inAct.LRU;
        LDJ = inAct.LDJ;
        LDS = inAct.LDS;
        LDU = inAct.LDU;
        LJS = inAct.LJS;
        LJU = inAct.LJU;
        LSU = inAct.LSU;

        RDJ = inAct.RDJ;
        RDS = inAct.RDS;
        RDU = inAct.RDU;
        RJS = inAct.RJS;
        RJU = inAct.RJU;
        RSU = inAct.RSU;

        DJS = inAct.DJS;
        DJU = inAct.DJU;
        DSU = inAct.DSU;

        JSU = inAct.JSU;

        LRDJ = inAct.LRDJ;
        LRDS = inAct.LRDS;
        LRDU = inAct.LRDU;
        LRJS = inAct.LRJS;
        LRJU = inAct.LRJU;
        LRSU = inAct.LRSU;
        LDJS = inAct.LRJS;
        LDJU = inAct.LDJU;
        LDSU = inAct.LDSU;
        LJSU = inAct.LJSU;

        RDJS = inAct.RDJS;
        RDJU = inAct.RDJU;
        RDSU = inAct.RDSU;
        RJSU = inAct.RJSU;

        DJSU = inAct.DJSU;

        LRDJS = inAct.LRDJS;
        LRDJU = inAct.LRDJU;
        LRDSU = inAct.LRDSU;
        LRJSU = inAct.LRJSU;
        LDJSU = inAct.LDJSU;
        RDJSU = inAct.RDJSU;
 
        LRDJSU = inAct.LRDJSU;
    }
    
    public void resetCounter()
    {
        ALLLEFT = 0;
        ALLRIGHT = 0;
        ALLRUN = 0;
        
        Stand = 0;

        Left = 0;
        LR = 0;
        LD = 0;
        LJ = 0;
        LS = 0;
        LU = 0;

        Right = 0;
        RD = 0;
        RJ = 0;
        RS = 0;
        RU = 0;

        Down = 0;
        DJ = 0;
        DS = 0;
        DU = 0;

        Jump = 0;
        JS = 0;
        JU = 0;

        Speed = 0;
        SU = 0;

        Up = 0;

        //3 buttons: 20 actions
        LRD = 0;
        LRJ = 0;
        LRS = 0;
        LRU = 0;
        LDJ = 0;
        LDS = 0;
        LDU = 0;
        LJS = 0;
        LJU = 0;
        LSU = 0;

        RDJ = 0;
        RDS = 0;
        RDU = 0;
        RJS = 0;
        RJU = 0;
        RSU = 0;

        DJS = 0;
        DJU = 0;
        DSU = 0;

        JSU = 0;

        //4 buttons: 15 actions
        LRDJ = 0;
        LRDS = 0;
        LRDU = 0;
        LRJS = 0;
        LRJU = 0;
        LRSU = 0;
        LDJS = 0;
        LDJU = 0;
        LDSU = 0;
        LJSU = 0;

        RDJS = 0;
        RDJU = 0;
        RDSU = 0;
        RJSU = 0;

        DJSU = 0;

        //5 buttons: 6 actions
        LRDJS = 0;
        LRDJU = 0;
        LRDSU = 0;
        LRJSU = 0;
        LDJSU = 0;
        RDJSU = 0;
        //6 buttons: 1 actions
        LRDJSU = 0;
    }
    
    public void increaseCounter(EAction inAct)
    {
        if(inAct.action[0])
            ALLLEFT++;
        if(inAct.action[1])
            ALLRIGHT++;
        if(inAct.action[4])
            ALLRUN++;
        int tmp = inAct.toInt();
        switch(tmp)
        {
            case 0: Stand++; break;
            case 1: Up++; break;
            case 2: Speed++; break;
            case 3: SU++; break;
            case 4: Jump++; break;
            case 5: JU++; break;
            case 6: JS++; break;
            case 7: JSU++; break;
            case 8: Down++; break;
            case 9: DU++; break;
            case 10: DS++; break;
            case 11: DSU++; break;
            case 12: DJ++; break;
            case 13: DJU++; break;
            case 14: DJS++; break;
            case 15: DJSU++; break;
            case 16: Right++; break;
            case 17: RU++; break;
            case 18: RS++; break;
            case 19: RSU++; break;
            case 20: RJ++; break;
            case 21: RJU++; break;
            case 22: RJS++; break;
            case 23: RJSU++; break;
            case 24: RD++; break;
            case 25: RDU++; break;
            case 26: RDS++; break;
            case 27: RDSU++; break;
            case 28: RDJ++; break;
            case 29: RDJU++; break;
            case 30: RDJS++; break;
            case 31: RDJSU++; break;
            case 32: Left++; break;
            case 33: LU++; break;
            case 34: LS++; break;
            case 35: LSU++; break;
            case 36: LJ++; break;
            case 37: LJU++; break;
            case 38: LJS++; break;
            case 39: LJSU++; break;
            case 40: LD++; break;
            case 41: LDU++; break;
            case 42: LDS++; break;
            case 43: LDSU++; break;
            case 44: LDJ++; break;
            case 45: LDJU++; break;
            case 46: LDJS++; break;
            case 47: LDJSU++; break;
            case 48: LR++; break;
            case 49: LRU++; break;
            case 50: LRS++; break;
            case 51: LRSU++; break;
            case 52: LRJ++; break;
            case 53: LRJU++; break;
            case 54: LRJS++; break;
            case 55: LRJSU++; break;
            case 56: LRD++; break;
            case 57: LRDU++; break;
            case 58: LRDS++; break;
            case 59: LRDSU++; break;
            case 60: LRDJ++; break;
            case 61: LRDJU++; break;
            case 62: LRDJS++; break;
            case 63: LRDJSU++; break;
        }
    }
    
    public static void printTitle()
    {
        System.out.print("AllLEFT AllRIGHT AllRUN Stand Left LR LD LJ LS LU Right RD RJ RS RU Down DJ DS"
                + " DU Jump JS JU Speed SU Up LRD LRJ LRS LRU LDJ LDS LDU LJS"
                + " LJU LSU RDJ RDS RDU RJS RJU RSU DJS DJU DSU JSU LRDJ LRDS LRDU"
                + " LRJS LRJU LRSU LDJS LDJU LDSU LJSU RDJS RDJU RDSU RJSU DJSU"
                + " LRDJS LRDJU LRDSU LRJSU LDJSU RDJSU LRDJSU");
    }
    
    public void printValues()
    {
        System.out.print(
            ALLLEFT + " " +
            ALLRIGHT + " " +
            ALLRUN + " " + 
            Stand + " " +
            Left + " " +
            LR + " " +
            LD + " " +
            LJ + " " +
            LS + " " +
            LU + " " +
            Right + " " +
            RD + " " +
            RJ + " " +
            RS + " " +
            RU + " " +
            Down + " " +
            DJ + " " +
            DS + " " +
            DU + " " +
            Jump + " " +
            JS + " " +
            JU + " " +
            Speed + " " +
            SU + " " +
            Up + " " +
            LRD + " " +
            LRJ + " " +
            LRS + " " +
            LRU + " " +
            LDJ + " " +
            LDS + " " +
            LDU + " " +
            LJS + " " +
            LJU + " " +
            LSU + " " +
            RDJ + " " +
            RDS + " " +
            RDU + " " +
            RJS + " " +
            RJU + " " +
            RSU + " " +
            DJS + " " +
            DJU + " " +
            DSU + " " +
            JSU + " " +
            LRDJ + " " +
            LRDS + " " +
            LRDU + " " +
            LRJS + " " +
            LRJU + " " +
            LRSU + " " +
            LDJS + " " +
            LDJU + " " +
            LDSU + " " +
            LJSU + " " +
            RDJS + " " +
            RDJU + " " +
            RDSU + " " +
            RJSU + " " +
            DJSU + " " +
            LRDJS + " " +
            LRDJU + " " +
            LRDSU + " " +
            LRJSU + " " +
            LDJSU + " " +
            RDJSU + " " +
            LRDJSU + " "
        ); 
    }
    
    public String returnPrintValues()
    {
        return "" +
            ALLLEFT + " " +
            ALLRIGHT + " " +
            ALLRUN + " " +
            Stand + " " +
            Left + " " +
            LR + " " +
            LD + " " +
            LJ + " " +
            LS + " " +
            LU + " " +
            Right + " " +
            RD + " " +
            RJ + " " +
            RS + " " +
            RU + " " +
            Down + " " +
            DJ + " " +
            DS + " " +
            DU + " " +
            Jump + " " +
            JS + " " +
            JU + " " +
            Speed + " " +
            SU + " " +
            Up + " " +
            LRD + " " +
            LRJ + " " +
            LRS + " " +
            LRU + " " +
            LDJ + " " +
            LDS + " " +
            LDU + " " +
            LJS + " " +
            LJU + " " +
            LSU + " " +
            RDJ + " " +
            RDS + " " +
            RDU + " " +
            RJS + " " +
            RJU + " " +
            RSU + " " +
            DJS + " " +
            DJU + " " +
            DSU + " " +
            JSU + " " +
            LRDJ + " " +
            LRDS + " " +
            LRDU + " " +
            LRJS + " " +
            LRJU + " " +
            LRSU + " " +
            LDJS + " " +
            LDJU + " " +
            LDSU + " " +
            LJSU + " " +
            RDJS + " " +
            RDJU + " " +
            RDSU + " " +
            RJSU + " " +
            DJSU + " " +
            LRDJS + " " +
            LRDJU + " " +
            LRDSU + " " +
            LRJSU + " " +
            LDJSU + " " +
            RDJSU + " " +
            LRDJSU; 
    }
    
    public boolean equals(EAllActions inAllAct)
    {
        boolean result = false;
        
        if(ALLLEFT == inAllAct.ALLLEFT &&
        ALLRIGHT == inAllAct.ALLRIGHT &&
        ALLRUN == inAllAct.ALLRUN &&
                
        Stand == inAllAct.Stand &&

        Left == inAllAct.Left &&
        LR == inAllAct.LR &&
        LD == inAllAct.LD &&
        LJ == inAllAct.LJ &&
        LS == inAllAct.LS &&
        LU == inAllAct.LU &&

        Right == inAllAct.Right &&
        RD == inAllAct.RD &&
        RJ == inAllAct.RJ &&
        RS == inAllAct.RS &&
        RU == inAllAct.RU &&

        Down == inAllAct.Down &&
        DJ == inAllAct.DJ &&
        DS == inAllAct.DS &&
        DU == inAllAct.DU &&

        Jump == inAllAct.Jump &&
        JS == inAllAct.JS &&
        JU == inAllAct.JU &&

        Speed == inAllAct.Speed &&
        SU == inAllAct.SU &&

        Up == inAllAct.Up &&

        LRD == inAllAct.LRD &&
        LRJ == inAllAct.LRJ &&
        LRS == inAllAct.LRS &&
        LRU == inAllAct.LRU &&
        LDJ == inAllAct.LDJ &&
        LDS == inAllAct.LDS &&
        LDU == inAllAct.LDU &&
        LJS == inAllAct.LJS &&
        LJU == inAllAct.LJU &&
        LSU == inAllAct.LSU &&

        RDJ == inAllAct.RDJ &&
        RDS == inAllAct.RDS &&
        RDU == inAllAct.RDU &&
        RJS == inAllAct.RJS &&
        RJU == inAllAct.RJU &&
        RSU == inAllAct.RSU &&

        DJS == inAllAct.DJS &&
        DJU == inAllAct.DJU &&
        DSU == inAllAct.DSU &&

        JSU == inAllAct.JSU &&

        LRDJ == inAllAct.LRDJ &&
        LRDS == inAllAct.LRDS &&
        LRDU == inAllAct.LRDU &&
        LRJS == inAllAct.LRJS &&
        LRJU == inAllAct.LRJU &&
        LRSU == inAllAct.LRSU &&
        LDJS == inAllAct.LRJS &&
        LDJU == inAllAct.LDJU &&
        LDSU == inAllAct.LDSU &&
        LJSU == inAllAct.LJSU &&

        RDJS == inAllAct.RDJS &&
        RDJU == inAllAct.RDJU &&
        RDSU == inAllAct.RDSU &&
        RJSU == inAllAct.RJSU &&

        DJSU == inAllAct.DJSU &&

        LRDJS == inAllAct.LRDJS &&
        LRDJU == inAllAct.LRDJU &&
        LRDSU == inAllAct.LRDSU &&
        LRJSU == inAllAct.LRJSU &&
        LDJSU == inAllAct.LDJSU &&
        RDJSU == inAllAct.RDJSU &&
 
        LRDJSU == inAllAct.LRDJSU)
        {
            result = true;
        }
        
        
        return result;
    }
}
