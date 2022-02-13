/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork;

import ch.idsia.agents.EvolutionalNeuralNetwork.Agent.NEATAgent;
import com.google.gson.Gson;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Phuc
 */
public class MarioGenome {
   
    public int identity;
    public int currentInno;
    public int species;
    public int live;
    public double score;
    public double fitness;
    public double oldfiness;
    public double naturalness;
    public double totalError;
    public double eta;
    public static int randSeed;
    
    public Node biasInput;
    public Node biasHidden;
    
    public List<Node> listInputNodes;
    public List<Node> listHiddenNodes;
    public List<Node> listOutputNodes;
    public List<Connection> listConnections;
    public List<Connection> listBiasConnections;
    
    //Strange actions
    public int jumpTimes;
    public double simDistEnemy;
    
    //Actions
    public int Stand;
    public int Right;
    public int Left;
    public int Speed;
    public int Jump;
    public int RJ;
    public int LJ;
    public int RS;
    public int LS;
    public int RJS;
    public int LJS;
    public int ChangeAction;
    public int Coin;
    public int Kills;
    
    public int OnGround;
    public int LeftRight;
    public int UpDown;
    public int SpentTime;
    public int IllegalAction;
    
    public MarioGenome(){
        score = -100000;
        fitness = -100000;
        naturalness = -100000;
        oldfiness = -1;
        totalError = 0;
        eta = 0;
        currentInno = 0;
        species = -1;
        live = 0;
        biasInput = new Node(Layer.INPUT,-1,0);
        biasInput.z = 1;
        biasHidden = new Node(Layer.HIDDEN,-1,0);
        biasHidden.z = 1;
        listInputNodes = new ArrayList<>();
        listHiddenNodes = new ArrayList<>();
        listOutputNodes = new ArrayList<>();
        listConnections = new ArrayList<>();
        listBiasConnections = new ArrayList<>();
        
        jumpTimes = 0;
        simDistEnemy = 0;
        
        Stand = 0;
        Right = 0;
        Left = 0;
        Speed = 0;
        Jump = 0;
        RJ = 0;
        LJ = 0;
        RS = 0;
        LS = 0;
        RJS = 0;
        LJS = 0;
        ChangeAction = 0;
        Coin = 0;
        Kills = 0;
        
        OnGround = 0;
        LeftRight = 0;
        UpDown = 0;
        SpentTime = 0;
        IllegalAction = 0;
    }
        
    public MarioGenome(MarioGenome g){
        score = g.score;
        fitness = g.fitness;
        naturalness = g.naturalness;
        oldfiness = g.oldfiness;
        currentInno = g.currentInno;
        species = g.species;
        live = g.live;
        biasInput = g.biasInput;
        biasHidden = g.biasHidden;
        listInputNodes = new ArrayList<>();
        listHiddenNodes = new ArrayList<>();
        listOutputNodes = new ArrayList<>();
        listConnections = new ArrayList<>();
        listBiasConnections = new ArrayList<>();
        
        for(int i = 0; i < g.listInputNodes.size(); ++i)
        {
            listInputNodes.add(new Node(g.listInputNodes.get(i)));
        }
        for(int i = 0; i < g.listHiddenNodes.size(); ++i)
        {
            listHiddenNodes.add(new Node(g.listHiddenNodes.get(i)));
        }
        for(int i = 0; i < g.listOutputNodes.size(); ++i)
        {
            listOutputNodes.add(new Node(g.listOutputNodes.get(i)));
        }
        for(int i = 0; i < g.listConnections.size(); ++i)
        {
            listConnections.add(new Connection(g.listConnections.get(i)));
        }
        for(int i = 0; i < g.listBiasConnections.size(); ++i)
        {
            listBiasConnections.add(new Connection(g.listBiasConnections.get(i)));
        }
        
        jumpTimes = g.jumpTimes;
        simDistEnemy = g.simDistEnemy;
        
        Stand = g.Stand;
        Right = g.Right;
        Left = g.Left;
        Speed = g.Speed;
        Jump = g.Jump;
        RJ = g.RJ;
        LJ = g.LJ;
        RS = g.RS;
        LS = g.LS;
        RJS = g.RJS;
        LJS = g.LJS;
        ChangeAction = g.ChangeAction;
        Coin = g.Coin;
        Kills = g.Kills;
        
        OnGround = g.OnGround;
        LeftRight = g.LeftRight;
        UpDown = g.UpDown;
        SpentTime = g.SpentTime;
        IllegalAction = g.IllegalAction;
    }
    
    public void resetInfo()
    {
        Stand = 0;
        Right = 0;
        Left = 0;
        Speed = 0;
        Jump = 0;
        RJ = 0;
        LJ = 0;
        RS = 0;
        LS = 0;
        RJS = 0;
        LJS = 0;
        ChangeAction = 0;
        Coin = 0;
        Kills = 0;
        
        OnGround = 0;
        LeftRight = 0;
        UpDown = 0;
        SpentTime = 0;
        IllegalAction = 0;
    }
    
    public boolean checkExistedNode(Node n)
    {
        switch(n.layer)
        {
            case INPUT:
                for(int i = 0; i < listInputNodes.size(); ++i)
                {
                    if(listInputNodes.get(i).equals(n))
                        return true;
                }
                break;
            case HIDDEN:
                for(int i = 0; i < listHiddenNodes.size(); ++i)
                {
                    if(listHiddenNodes.get(i).equals(n))
                        return true;
                }
                break;
            case OUTPUT:
                for(int i = 0; i < listOutputNodes.size(); ++i)
                {
                    if(listOutputNodes.get(i).equals(n))
                        return true;
                }
                break;
        }
        if(n.equals(biasHidden))
            return true;
        else if(n.equals(biasInput))
            return true;
        return false;
    }
    
    public boolean checkExistedConnection(Connection c) {
        for(int i = 0; i < listConnections.size(); ++i)
        {
            if(listConnections.get(i).inno == c.inno)
                return true;
        }
        
        for(int i = 0; i < listBiasConnections.size(); ++i)
        {
            if(listBiasConnections.get(i).inno == c.inno)
                return true;
        }
        return false;
    }
    
    public void newNode(Layer layer, int idx, int innovationNumber) {
        Node node = new Node(layer, idx, innovationNumber);
        if(!checkExistedNode(node))
            switch(layer)
            {
                case INPUT:
                    listInputNodes.add(node);
                    break;
                case HIDDEN:
                    listHiddenNodes.add(node);
                    break;
                case OUTPUT:
                    listOutputNodes.add(node);
                    break;
                default:
                    break;
            }
        else
        {
            if(NEATAgent.debug)
                System.out.println("Error: Node already existed!");
        }
    }
    
    public void insertNode(Layer layer, Node n) {
        if(!checkExistedNode(n))
            switch(layer)
            {
                case INPUT:
                    listInputNodes.add(n);
                    break;
                case HIDDEN:
                    listHiddenNodes.add(n);
                    break;
                case OUTPUT:
                    listOutputNodes.add(n);
                    break;
                default:
                    break;
            }
        else
        {
            if(NEATAgent.debug)
                System.out.println("Error: Node already existed!");
        }
    }
    
    public void newConnection(Layer from, Layer to, int inIndex, int outIndex, double weight, int innovation) {
        Connection c = new Connection(from, to, inIndex, outIndex, weight, innovation);
        if(!checkExistedConnection(c))
            listConnections.add(c);
        else
        {
            if(NEATAgent.debug)
               System.out.println("Error: Connection already existed!");
        }
    }
    
    public void insertConnection(Connection c) {
        if(!checkExistedConnection(c))
            listConnections.add(c);
        else
        {
            if(NEATAgent.debug)
                System.out.println("Error: Connection already existed!");
        }
    }
    //*****************************CrossOver************************************
    public static Genome crossOver(Genome a, Genome b)
    {
        Genome newGen = new Genome(a);
        //Adding nodes
        for(int i = 0; i < b.listInputNodes.size(); ++i)
        {
            if(!newGen.checkExistedNode(b.listInputNodes.get(i)))
                newGen.listInputNodes.add(b.listInputNodes.get(i));
        }
        for(int i = 0; i < b.listHiddenNodes.size(); ++i)
        {
            if(!newGen.checkExistedNode(b.listHiddenNodes.get(i)))
                newGen.listHiddenNodes.add(b.listHiddenNodes.get(i));
        }
        for(int i = 0; i < b.listOutputNodes.size(); ++i)
        {
            if(!newGen.checkExistedNode(b.listOutputNodes.get(i)))
                newGen.listOutputNodes.add(b.listOutputNodes.get(i));
        }
        //Adding connections
        for(int i = 0; i < b.listConnections.size(); ++i)
        {
            Connection tmpC = b.listConnections.get(i);
            boolean existsConnection = false;
            for(int j = 0; j < newGen.listConnections.size(); ++j)
            {
                if(newGen.listConnections.get(j).equals(tmpC))
                {
                    existsConnection = true;
                }
            }
            if(!existsConnection)
                newGen.addConnectionRandom(tmpC);
        }
        for(int i = 0; i < b.listBiasConnections.size(); ++i)
        {
            Connection tmpC = b.listBiasConnections.get(i);
            boolean existsConnection = false;
            for(int j = 0; j < newGen.listBiasConnections.size(); ++j)
            {
                if(newGen.listBiasConnections.get(j).equals(tmpC))
                {
                    existsConnection = true;
                }
            }
            if(!existsConnection)
                newGen.addConnectionRandom(tmpC);
        }
        
        return newGen;
    }
    
    public static Genome crossOverNEAT(Genome a, Genome b, double enableChance, double disableChance)
    {
        Random rand = new Random(randSeed);
        Genome newGen = new Genome(a);
        int nDisjoin = 0;
        int nExcess = 0;
        //Adding nodes
        for(int i = 0; i < b.listInputNodes.size(); ++i)
        {
            if(!newGen.checkExistedNode(b.listInputNodes.get(i)))
                newGen.listInputNodes.add(b.listInputNodes.get(i));
        }
        for(int i = 0; i < b.listHiddenNodes.size(); ++i)
        {
            if(!newGen.checkExistedNode(b.listHiddenNodes.get(i)))
                newGen.listHiddenNodes.add(b.listHiddenNodes.get(i));
        }
        for(int i = 0; i < b.listOutputNodes.size(); ++i)
        {
            if(!newGen.checkExistedNode(b.listOutputNodes.get(i)))
                newGen.listOutputNodes.add(b.listOutputNodes.get(i));
        }
        
//        for(Node tmp : b.listInputNodes)
//        {
//            if(!newGen.checkExistedNode(tmp))
//                newGen.listInputNodes.add(new Node(tmp));
//        }
//        for(Node tmp : b.listHiddenNodes)
//        {
//            if(!newGen.checkExistedNode(tmp))
//                newGen.listHiddenNodes.add(new Node(tmp));
//        }
//        for(Node tmp : b.listOutputNodes)
//        {
//            if(!newGen.checkExistedNode(tmp))
//                newGen.listOutputNodes.add(new Node(tmp));
//        }        
        
        //Adding connections
//        for(int i = 0; i < b.listConnections.size(); ++i)
//        {
//            if(!newGen.checkExistedConnection(b.listConnections.get(i)))
//            {
//                newGen.listConnections.add(b.listConnections.get(i));
//            }
//        }
//        for(int i = 0; i < b.listBiasConnections.size(); ++i)
//        {
//            if(!newGen.checkExistedConnection(b.listBiasConnections.get(i)))
//            {
//                newGen.listBiasConnections.add(b.listBiasConnections.get(i));
//            }
//        }
        for(Connection tmp : b.listBiasConnections)
        {
            if(!newGen.checkExistedConnection(tmp))
                newGen.listBiasConnections.add(new Connection(tmp));
        }
        
        //Line up
        int maxInnoA = 0;
        int maxInnoB = 0;
        int maxInno = 0;
        int smallerInno = 0;
        int minInno = 100000;
        List<Connection> genA = new ArrayList<>();
        List<Connection> genB = new ArrayList<>();
        newGen.listConnections.clear();
        //get max inno of A and B
        if(a.listConnections.size() > 0 && b.listConnections.size() > 0)
        {
//            for(int i = 0; i < a.listConnections.size(); ++i)
//            {
//                if(a.listConnections.get(i).inno > maxInnoA)
//                    maxInnoA = a.listConnections.get(i).inno;
//                if(a.listConnections.get(i).inno < minInno)
//                    minInno = a.listConnections.get(i).inno;
//            }
//            for(int i = 0; i < b.listConnections.size(); ++i)
//            {
//                if(b.listConnections.get(i).inno > maxInnoB)
//                    maxInnoB = b.listConnections.get(i).inno;
//                if(b.listConnections.get(i).inno < minInno)
//                    minInno = b.listConnections.get(i).inno;
//            }
            for(Connection tmp : a.listConnections)
            {
                if(tmp.inno > maxInnoA)
                    maxInnoA = tmp.inno;
                if(tmp.inno < minInno)
                    minInno = tmp.inno;
            }
            for(Connection tmp : b.listConnections)
            {
                if(tmp.inno > maxInnoB)
                    maxInnoB = tmp.inno;
                if(tmp.inno < minInno)
                    minInno = tmp.inno;
            }
            
            if(maxInnoA > maxInnoB)
            {
                maxInno = maxInnoA;
                smallerInno = maxInnoB;
            }
            else
            {
                maxInno = maxInnoB;
                smallerInno = maxInnoA;
            }
            for(int i = minInno; i <= maxInno; ++i)
            {
                boolean exists = false;
                for(int j = 0; j < a.listConnections.size(); ++j)
                {
                    if(a.listConnections.get(j).inno == i)
                    {
                        genA.add(new Connection(a.listConnections.get(j)));
                        exists = true;
                        break;
                    }
                }
                if(!exists)
                    genA.add(null);
                exists = false;
                for(int j = 0; j < b.listConnections.size(); ++j)
                {
                    if(b.listConnections.get(j).inno == i)
                    {
                        genB.add(new Connection(b.listConnections.get(j)));
                        exists = true;
                        break;
                    }
                }
                if(!exists)
                    genB.add(null);
            }
            //System.out.println("Gens size: " + genA.size() + " " + genB.size() + " !!!!!!!!!!!!!!!!!!!");
            for(int i = 0; i < genA.size(); ++i)
            {
                if(genA.get(i) != null && genB.get(i) != null)
                {
                    if(rand.nextInt(2) == 1)
                        newGen.listConnections.add(new Connection(genB.get(i)));
                    else
                        newGen.listConnections.add(new Connection(genA.get(i)));
                }
                else if(genA.get(i) != null && genB.get(i) == null)
                {
                    if(i <= minInno)
                        nDisjoin++;
                    else
                        nExcess++;
                    newGen.listConnections.add(new Connection(genA.get(i)));
                }
                else if(genA.get(i) == null && genB.get(i) != null)
                {
                    if(i <= minInno)
                        nDisjoin++;
                    else
                        nExcess++;
                    newGen.listConnections.add(new Connection(genB.get(i)));
                }
            }
        }
        //Disable & Enable
//        for(int i = 0; i < newGen.listConnections.size(); ++i)
//        {
//            if(newGen.listConnections.get(i).isDisable)
//            {
//                if(rand.nextDouble() < enableChance)
//                {
//                    newGen.listConnections.get(i).isDisable = false;
//                }
//                else if(rand.nextDouble() < disableChance)
//                {
//                    newGen.listConnections.get(i).isDisable = true;
//                }
//            }
//        }
//        for(int i = 0; i < newGen.listBiasConnections.size(); ++i)
//        {
//            if(newGen.listBiasConnections.get(i).isDisable)
//            {
//                if(rand.nextDouble() < enableChance)
//                {
//                    newGen.listBiasConnections.get(i).isDisable = false;
//                }
//                else if(rand.nextDouble() < disableChance)
//                {
//                    newGen.listBiasConnections.get(i).isDisable = true;
//                }
//            }
//        }
        for(Connection tmp : newGen.listConnections)
        {
            if(tmp.isDisable)
            {
                if(rand.nextDouble() < enableChance)
                    tmp.isDisable = false;
                else if(rand.nextDouble() < disableChance)
                    tmp.isDisable = true;
            }
        }
        for(Connection tmp : newGen.listBiasConnections)
        {
            if(tmp.isDisable)
            {
                if(rand.nextDouble() < enableChance)
                    tmp.isDisable = false;
                else if(rand.nextDouble() < disableChance)
                    tmp.isDisable = true;
            }
        }
//        //Sort gens
//        for(int i = 0; i < newGen.listConnections.size(); ++i)
//        {
//            for(int j = 0; j < newGen.listConnections.size(); ++j)
//            {
//                if(i != j && newGen.listConnections.get(i).inno > newGen.listConnections.get(j).inno)
//                {
//                    Connection tmp = new Connection(newGen.listConnections.get(i));
//                    newGen.listConnections.get(i).copyInfo(newGen.listConnections.get(j));
//                    newGen.listConnections.get(j).copyInfo(tmp);
//                }
//            }
//        }
        return newGen;
    }
    
    //*****************************Mutation*************************************
    public void changeWeightOfConnection(Layer from, Layer to, int inIndex, int outIndex, double weight){
        Connection tmp;
        if(inIndex != -1)
        {
            for(int i = 0; i < listConnections.size(); ++i)
            {
                tmp = listConnections.get(i);
                if(tmp.fromLayer == from
                        && tmp.toLayer == to
                        && tmp.idxFromNode == inIndex
                        && tmp.idxToNode == outIndex)
                {
                    tmp.w = weight;
                    break;
                }
            }
        }
        else
        {
            for(int i = 0; i < listBiasConnections.size(); ++i)
            {
                tmp = listBiasConnections.get(i);
                if(tmp.fromLayer == from
                        && tmp.toLayer == to
                        && tmp.idxFromNode == inIndex
                        && tmp.idxToNode == outIndex)
                {
                    tmp.w = weight;
                    break;
                }
            }
        }
    }
    
    public double getWeightOfConnection(Layer from, Layer to, int inIndex, int outIndex){
        Connection tmp;
        if(inIndex != -1)
        {
            for(int i = 0; i < listConnections.size(); ++i)
            {
                tmp = listConnections.get(i);
                if(tmp.fromLayer == from
                        && tmp.toLayer == to
                        && tmp.idxFromNode == inIndex
                        && tmp.idxToNode == outIndex)
                {
                    return tmp.w;
                }
            }
        }
        else
        {
            for(int i = 0; i < listBiasConnections.size(); ++i)
            {
                tmp = listBiasConnections.get(i);
                if(tmp.fromLayer == from
                        && tmp.toLayer == to
                        && tmp.idxFromNode == inIndex
                        && tmp.idxToNode == outIndex)
                {
                    return tmp.w;
                }
            }
        }
        return -999;
    }
    
    public Connection getConnection(Layer fromL, Layer toL, Node inN, Node outN)
    {
        Connection result = null;
        if(inN.index != -1)
        {
            for(int i = 0; i < listConnections.size(); ++i)
            {
                Connection tmp = listConnections.get(i);
                if(tmp.fromLayer.equals(fromL) && tmp.toLayer.equals(toL)
                        && tmp.idxFromNode == inN.index && tmp.idxToNode == outN.index)
                {
                    result = tmp;
                    break;
                }
            }
        }
        else
        {
            for(int i = 0; i < listBiasConnections.size(); ++i)
            {
                Connection tmp = listBiasConnections.get(i);
                if(tmp.fromLayer.equals(fromL) && tmp.toLayer.equals(toL)
                        && tmp.idxFromNode == inN.index && tmp.idxToNode == outN.index)
                {
                    result = tmp;
                    break;
                }
            }
        }
        if(result == null)
        {
            if(NEATAgent.debug)
                System.out.println("Connection not found!");
        }
        return result;
    }
    
    public void disableConnection(Layer from, Layer to, int inIndex, int outIndex){
        Connection tmp;
        for(int i = 0; i < listConnections.size(); ++i)
        {
            tmp = listConnections.get(i);
            if(tmp.fromLayer == from
                    && tmp.toLayer == to
                    && tmp.idxFromNode == inIndex
                    && tmp.idxToNode == outIndex)
            {
                tmp.isDisable = true;
                break;
            }
        }
    }
    
    public void enableConnection(Layer from, Layer to, int inIndex, int outIndex){
        Connection tmp;
        for(int i = 0; i < listConnections.size(); ++i)
        {
            tmp = listConnections.get(i);
            if(tmp.fromLayer == from
                    && tmp.toLayer == to
                    && tmp.idxFromNode == inIndex
                    && tmp.idxToNode == outIndex)
            {
                tmp.isDisable = false;
                break;
            }
        }
    }
    
    public boolean addConnection(Connection c) {
        //Connection can not from output or to input unit
        if(c.fromLayer == Layer.OUTPUT)
        {
            if(NEATAgent.debug)
                System.out.println("Error: FROM must be other than OUTPUT layer.");
            return false;
        }
        if(c.toLayer == Layer.INPUT)
        {
            if(NEATAgent.debug)
                System.out.println("Error: TO must be other than INPUT layer.");
            return false;
        }
        //Get indices of fromNode and toNode
        int idxFrom = -2;
        int idxTo = -2;
        Node fromNode = null;
        Node toNode = null;
        if(c.idxFromNode != -1 && c.idxFromNode != -2)
        {
            switch(c.fromLayer)
            {
                case INPUT:
//                    for(Node tmp : listInputNodes)
//                    {
//                        if(tmp.index == c.idxFromNode)
//                        {
//                            idxFrom = c.idxFromNode;
//                            fromNode = tmp;
//                            break;
//                        }
//                    }
                    fromNode = listInputNodes.get(c.idxFromNode);
                    idxFrom = fromNode.index;
                    break;
                case HIDDEN:
//                    for(Node tmp : listHiddenNodes)
//                    {
//                        if(tmp.index == c.idxFromNode)
//                        {
//                            idxFrom = c.idxFromNode;
//                            fromNode = tmp;
//                            break;
//                        }
//                    }
                    fromNode = listHiddenNodes.get(c.idxFromNode);
                    idxFrom = fromNode.index;
                    break;
                case OUTPUT:
//                    for(Node tmp : listOutputNodes)
//                    {
//                        if(tmp.index == c.idxFromNode)
//                        {
//                            idxFrom = c.idxFromNode;
//                            fromNode = tmp;
//                            break;
//                        }
//                    }
                    fromNode = listOutputNodes.get(c.idxFromNode);
                    idxFrom = fromNode.index;
                    break;
            }
        }
        else if(c.idxFromNode == -1)
        {
            idxFrom = c.idxFromNode;
            if(c.fromLayer.equals(Layer.INPUT))
                fromNode = biasInput;
            else if(c.fromLayer.equals(Layer.HIDDEN))
                fromNode = biasHidden;
        }
        switch(c.toLayer)
        {
            case HIDDEN:
//                for(Node tmp : listHiddenNodes)
//                {
//                    if(tmp.index == c.idxToNode)
//                    {
//                        idxTo = c.idxToNode;
//                        toNode = tmp;
//                        break;
//                    }
//                }
                toNode = listHiddenNodes.get(c.idxToNode);
                idxTo = toNode.index;
                break;
            case OUTPUT:
//                for(Node tmp : listOutputNodes)
//                {
//                    if(tmp.index == c.idxToNode)
//                    {
//                        idxTo = c.idxToNode;
//                        toNode = tmp;
//                        break;
//                    }
//                }
                toNode = listOutputNodes.get(c.idxToNode);
                idxTo = toNode.index;
                break;
        }
        if(NEATAgent.debug)
            System.out.println("Test idxFrom: " + c.idxFromNode + " to: " + c.idxToNode + " layer: " + c.fromLayer + " to " + c.toLayer);
        if(idxFrom == -2)
        {
            if(NEATAgent.debug)
                System.out.println("FROM layer: No index found!");
            return false;
        }
        if(idxTo == -2)
        {
            if(NEATAgent.debug)
                System.out.println("TO layer: No index found!");
            return false;
        }
        
        //No recurrent connection
//        if(idxFrom == idxTo && c.fromLayer.equals(c.toLayer))
//            return false;
        
        if(!checkExistedConnection(c))
        {
            if(c.idxFromNode == -1)
                listBiasConnections.add(c);
            else
            {
                listConnections.add(c);
            }
            this.currentInno = c.inno;
            return true;
        }
//        else
//        {
//            Connection tmp = getConnection(c.fromLayer, c.toLayer, fromNode, toNode);
//            tmp.w = c.w;
//            System.out.println("AlterWeight!");
//        }
        return false;
    }
    public boolean addConnectionRandom(Connection c) {
        //Connection can not from output or to input unit
        if(c.fromLayer == Layer.OUTPUT)
        {
            if(NEATAgent.debug)
                System.out.println("Error: FROM must be other than OUTPUT layer.");
            return false;
        }
        if(c.toLayer == Layer.INPUT)
        {
            if(NEATAgent.debug)
                System.out.println("Error: TO must be other than INPUT layer.");
            return false;
        }
        //Get indices of fromNode and toNode
        int idxFrom = -2;
        int idxTo = -2;
        if(c.idxFromNode != -1)
        {
            switch(c.fromLayer)
            {
                case INPUT:
                    for(int i = 0; i < listInputNodes.size(); ++i)
                    {
                        if(listInputNodes.get(i).index == c.idxFromNode)
                        {
                            idxFrom = c.idxFromNode;
                            break;
                        }
                    }
                    break;
                case HIDDEN:
                    for(int i = 0; i < listHiddenNodes.size(); ++i)
                    {
                        if(listHiddenNodes.get(i).index == c.idxFromNode)
                        {
                            idxFrom = c.idxFromNode;
                            break;
                        }
                    }
                    break;
                case OUTPUT:
                    for(int i = 0; i < listOutputNodes.size(); ++i)
                    {
                        if(listOutputNodes.get(i).index == c.idxFromNode)
                        {
                            idxFrom = c.idxFromNode;
                            break;
                        }
                    }
                    break;
            }
        }
        else
        {
            idxFrom = c.idxFromNode;
        }
        switch(c.toLayer)
        {
            case HIDDEN:
                for(int i = 0; i < listHiddenNodes.size(); ++i)
                {
                    if(listHiddenNodes.get(i).index == c.idxToNode)
                    {
                        idxTo = c.idxToNode;
                        break;
                    }
                }
                break;
            case OUTPUT:
                for(int i = 0; i < listOutputNodes.size(); ++i)
                {
                    if(listOutputNodes.get(i).index == c.idxToNode)
                    {
                        idxTo = c.idxToNode;
                        break;
                    }
                }
                break;
        }
        if(NEATAgent.debug)
            System.out.println("Test idxFrom: " + c.idxFromNode);
        if(idxFrom == -2)
        {
            if(NEATAgent.debug)
                System.out.println("FROM layer: No index found!");
            return false;
        }
        if(idxTo == -2)
        {
            if(NEATAgent.debug)
                System.out.println("TO layer: No index found!");
            return false;
        }
        
        //No recurrent connection
        if(idxFrom == idxTo && c.fromLayer.equals(c.toLayer))
            return false;
        
        if(!checkExistedConnection(c))
        {
            if(c.idxFromNode == -1)
                listBiasConnections.add(c);
            else
                listConnections.add(c);
        }
        return true;
    }
    
    public boolean mutateAddNode(Node n, Connection inN, Connection outN) {
        
        //Adding the node.
        if(checkExistedNode(n))
        {
            //if(NEATAgent.debug)
                System.out.println("Node is already existed.");
            return false;
        }
        this.insertNode(n.layer, n);     

        boolean check = false;
        if(checkExistedConnection(inN))
        {
            if(NEATAgent.debug)
                System.out.println("IN connection is already existed.");
        }
        else
        {
            //Adding connections
            check = addConnection(inN);
        }
        if(checkExistedConnection(outN))
        {
            if(NEATAgent.debug)
                System.out.println("OUT connection is already existed.");
        }
        else
        {
            //Adding connections
            check = addConnection(outN);
        }
        
        //Disable the old connection which is splitted by the new node
        if(check)
            return true;
        return false;
    }
    
    public boolean mutateAddNodeRandom(Node n, Connection inN, Connection outN) {
        
        //Adding the node.
        if(checkExistedNode(n))
        {
            //if(NEATAgent.debug)
                System.out.println("Node is already existed.");
            return false;
        }
        this.insertNode(n.layer, n);     
        
        boolean check = false;
        if(checkExistedConnection(inN))
        {
            if(NEATAgent.debug)
                System.out.println("IN connection is already existed.");
        }
        else
        {
            //Adding connections
            check = addConnectionRandom(inN);
        }
        if(checkExistedConnection(outN))
        {
            if(NEATAgent.debug)
                System.out.println("OUT connection is already existed.");
        }
        else
        {
            //Adding connections
            check = addConnectionRandom(outN);
        }
        
        //Disable the old connection which is splitted by the new node
        if(check)
            return true;
        return false;
    }    
    
    //*******************************Propagation********************************
    public void setInput(int[] inputs)
    {
        for(int i = 0; i < inputs.length; ++i)
        {
            listInputNodes.get(i).a = inputs[i];
            //listInputNodes.get(i).z = Node.sigmoid(inputs[i]);
        }
    }
    
    public void setInput(float[] inputs)
    {
        for(int i = 0; i < inputs.length; ++i)
        {
            listInputNodes.get(i).a = inputs[i];
            //listInputNodes.get(i).z = Node.sigmoid(inputs[i]);
        }
    }
    
    public void setInput(double[] inputs)
    {
        for(int i = 0; i < inputs.length; ++i)
        {
            listInputNodes.get(i).a = inputs[i];
            listInputNodes.get(i).z = listInputNodes.get(i).a;
            //listInputNodes.get(i).z = Node.sigmoid(inputs[i]);
        }
    }
    
    public void feedForward() {
        int idx;
        double A;
        Layer lay = null;
        Node hiddenNode = null;
        Node outputNode = null;
        Connection c = null;
        //Hidden units' Z
        for(int i = 0; i < listHiddenNodes.size(); ++i)
        {
            hiddenNode = listHiddenNodes.get(i);
            lay = hiddenNode.layer;
            idx = hiddenNode.index;
            A = 0;
            for(int j = 0; j < listConnections.size(); ++j)
            {
                c = listConnections.get(j);
                if(c.toLayer.equals(lay) && c.idxToNode == idx && !c.isDisable)
                {
                    Layer from = c.fromLayer;
                    if(from.equals(Layer.INPUT))
                    {
                        for(int k = 0; k < listInputNodes.size(); ++k)
                        {
                            if(listInputNodes.get(k).index == c.idxFromNode)
                            {
                                A += listInputNodes.get(k).z*c.w;
                                break;
                            }
                        }
                    }
                    if(from.equals(Layer.HIDDEN))
                    {
                        for(int k = 0; k < listHiddenNodes.size(); ++k)
                        {
                            if(listHiddenNodes.get(k).index == c.idxFromNode)
                            {
                                A += listHiddenNodes.get(k).z*c.w;
                                break;
                            }
                        }
                    }
                }
            }
            //For bias unit
            for(int j = 0; j < listBiasConnections.size(); ++j)
            {
                c = listBiasConnections.get(j);
                if(c.toLayer == lay && c.idxFromNode == -1 && c.idxToNode == idx && !c.isDisable)
                {
                    Layer from = c.fromLayer;
                    if(from.equals(Layer.INPUT))
                    {
                        A += c.w;
                    }
                    if(from.equals(Layer.HIDDEN))
                    {    
                        A += c.w;
                    }
                }
            }
            hiddenNode.a = A;
            //hiddenNode.z = Node.tanh(A);
            hiddenNode.z = Node.sigmoid(A);
        }
        
        //Output units' Z
        for(int i = 0; i < listOutputNodes.size(); ++i)
        {
            outputNode = listOutputNodes.get(i);
            lay = outputNode.layer;
            idx = outputNode.index;
            A = 0;
            for(int j = 0; j < listConnections.size(); ++j)
            {
                c = listConnections.get(j);
                if(c.toLayer.equals(lay) && c.idxToNode == idx && !c.isDisable)
                {
                    Layer from = c.fromLayer;
                    if(from.equals(Layer.INPUT))
                    {
                        for(int k = 0; k < listInputNodes.size(); ++k)
                        {
                            if(listInputNodes.get(k).index == c.idxFromNode)
                            {
                                A += listInputNodes.get(k).z*c.w;
                                break;
                            }
                        }
                    }
                    if(from.equals(Layer.HIDDEN))
                    {
                        for(int k = 0; k < listHiddenNodes.size(); ++k)
                        {
                            if(listHiddenNodes.get(k).index == c.idxFromNode)
                            {
                                A += listHiddenNodes.get(k).z*c.w;
                                break;
                            }
                        }
                    }
                }
            }
            //For bias unit
            for(int j = 0; j < listBiasConnections.size(); ++j)
            {
                c = listBiasConnections.get(j);
                if(c.toLayer == lay && c.idxFromNode == -1 && c.idxToNode == idx && !c.isDisable)
                {
                    Layer from = c.fromLayer;
                    if(from == Layer.HIDDEN)
                    {    
                        A += c.w;
                    }
                }
            }
            outputNode.a = A;
            //outputNode.z = Node.tanh(A);
            outputNode.z = Node.sigmoid(A);
        }
    }
    
    public void forceOutput()
    {
        for(Node n : listOutputNodes)
        {
            if(n.z >= 0.5)
                n.z = 1;
            else
                n.z = 0;
        }
    }
    
    public void backPropagation(double[] output)
    {
        //Calculate error for output layer
        totalError = 0;
        for(int i = 0; i < listOutputNodes.size(); ++i)
        {
            listOutputNodes.get(i).error = output[i] - listOutputNodes.get(i).z;
            listOutputNodes.get(i).error *= Node.derivative_sigmoid(listOutputNodes.get(i).a);
            totalError += listOutputNodes.get(i).error*listOutputNodes.get(i).error*0.5;
        }
        totalError = totalError/listOutputNodes.size();
        
        //Calculate error for hidden layer
        for(int i = 0; i < listHiddenNodes.size(); ++i)
        {
            double sum = 0d;
            for(Connection c : listConnections)
            {
                if(c.fromLayer.equals(listHiddenNodes.get(i).layer) && c.idxFromNode == listHiddenNodes.get(i).index)
                {
                    for(Node aNode : listOutputNodes)
                    {
                        if(aNode.index == c.idxToNode)
                        {
                            sum += aNode.error * c.w;
                            break;
                        }
                    }
                }
            }
            sum *= Node.derivative_sigmoid(listHiddenNodes.get(i).a);
            listHiddenNodes.get(i).error = sum;
        }
        
        //Hidden bias
        double sumBias = 0d;
        for(Connection c : listBiasConnections)
        {
            if(c.fromLayer.equals(biasHidden.layer) && c.idxFromNode == -1)
            {
                sumBias += listOutputNodes.get(c.idxToNode).error * c.w;
            }
        }
        //sumBias *= Node.derivative_sigmoid(biasHidden.a);
        biasHidden.error = sumBias;
        
        //Calculate error for input layer
        for(int i = 0; i < listInputNodes.size(); ++i)
        {
            double sum = 0d;
            for(Connection c : listConnections)
            {
                if(c.fromLayer.equals(listInputNodes.get(i).layer) && c.idxFromNode == listInputNodes.get(i).index)
                {
                    for(Node aNode : listHiddenNodes)
                    {
                        if(aNode.index == c.idxToNode)
                        {
                            sum += aNode.error * c.w;
                            break;
                        }
                    }
                    
                }
            }
            sum *= Node.derivative_sigmoid(listInputNodes.get(i).a);
            listInputNodes.get(i).error = sum;
        }
        //Input bias
        sumBias = 0d;
        for(Connection c : listBiasConnections)
        {
            if(c.fromLayer.equals(biasInput.layer) && c.idxFromNode == -1)
            {
                sumBias += listHiddenNodes.get(c.idxToNode).error * c.w;
            }
        }
        //sumBias *= Node.derivative_sigmoid(biasInput.a);
        biasInput.error = sumBias;
    }
    
    public void updateWeightBackprobagation()
    {
        //Update input layer
        for(int i = 0; i < listInputNodes.size(); ++i)
        {
            for(Connection c : listConnections)
            {
                if(c.idxToNode == listInputNodes.get(i).index)
                {
                    c.w = c.w + eta*listInputNodes.get(i).error*listInputNodes.get(i).a*Node.derivative_sigmoid(listInputNodes.get(i).z);
                }
            }
        }
        
        //Input bias
        for(Connection c : listBiasConnections)
        {
            if(c.fromLayer.equals(Layer.INPUT) && c.idxFromNode == -1)
            {
                c.w = c.w + eta*biasInput.error;
            }
        }
        
        //Update hidden layer
        for(int i = 0; i < listHiddenNodes.size(); ++i)
        {
            for(Connection c : listConnections)
            {
                if(c.idxToNode == listHiddenNodes.get(i).index)
                {
                    //c.w = c.w + eta*listHiddenNodes.get(i).error*listHiddenNodes.get(i).z;
                    double z = 1;
                    if(c.fromLayer.equals(Layer.INPUT))
                    {
                        for(int j = 0; j < listInputNodes.size(); ++j)
                        {
                            if(listInputNodes.get(j).index == c.idxFromNode)
                                z = listInputNodes.get(j).z;
                        }
                    }
                    else if(c.fromLayer.equals(Layer.HIDDEN))
                    {
                        for(int j = 0; j < listHiddenNodes.size(); ++j)
                        {
                            if(listHiddenNodes.get(j).index == c.idxFromNode)
                                z = listHiddenNodes.get(j).z;
                        }
                    }
                    c.w = c.w + eta*listHiddenNodes.get(i).error*Node.derivative_sigmoid(listHiddenNodes.get(i).z)*z;
                }
            }
        }
        
        //Update hidden bias
        for(Connection c : listBiasConnections)
        {
            if(c.fromLayer.equals(Layer.HIDDEN) && c.idxFromNode == -1)
            {
                c.w = c.w + eta*biasHidden.error;
            }
        }
        
        //Update output layer
        for(int i = 0; i < listOutputNodes.size(); ++i)
        {
            for(Connection c : listConnections)
            {
                if(c.idxToNode == listOutputNodes.get(i).index)
                {
                    double z = 1;
                    for(int j = 0; j < listHiddenNodes.size(); ++j)
                    {
                        if(listHiddenNodes.get(j).index == c.idxFromNode)
                            z = listHiddenNodes.get(j).z;
                    }
                    c.w = c.w + eta*listOutputNodes.get(i).error*Node.derivative_sigmoid(listOutputNodes.get(i).z)*z;
                }
            }
        }
        
       
    }
    
    public double getTotalError()
    {
        return totalError;
    }
    
    public void obtainOutput(boolean[] outputs) {
        
            //if(listOutputNodes.get(i).z >= 0) //For tanh
        //KEY_LEFT = 0;
        //KEY_RIGHT = 1;
        //KEY_DOWN = 2;
        //KEY_JUMP = 3;
        //KEY_SPEED = 4;
        //KEY_UP = 5;
            if(listOutputNodes.get(0).z >= 0.5)
                outputs[0] = true;
            else
                outputs[0] = false;
            if(listOutputNodes.get(1).z >= 0.5)
                outputs[1] = true;
            else
                outputs[1] = false;
            if(listOutputNodes.get(2).z >= 0.5)
                outputs[2] = true;
            else
                outputs[2] = false;
            if(listOutputNodes.get(3).z >= 0.5)
                outputs[3] = true;
            else
                outputs[3] = false;
            if(listOutputNodes.get(4).z >= 0.5)
                outputs[4] = true;
            else
                outputs[4] = false;
            if(listOutputNodes.get(5).z >= 0.5)
                outputs[5] = true;
            else
                outputs[5] = false;
       
    }
    
    public void showGenInfo()
    {
//        System.out.println("Number of input nodes: " + listInputNodes.size());
//        for(int i = 0; i < listInputNodes.size(); ++i)
//        {
//            System.out.println("-" + listInputNodes.get(i).getInfo());
//        }
//        System.out.println("Number of output nodes: " + listOutputNodes.size());
//        for(int i = 0; i < listOutputNodes.size(); ++i)
//        {
//            System.out.println("-" + listOutputNodes.get(i).getInfo());
//        }
        System.out.println("Number of hidden nodes: " + listHiddenNodes.size());
        for(int i = 0; i < listHiddenNodes.size(); ++i)
        {
            System.out.println("-" + listHiddenNodes.get(i).getInfo());
        }
        System.out.println("Number of connections: " + listConnections.size());
        for(int i = 0; i < listConnections.size(); ++i)
        {
            System.out.println("-" + listConnections.get(i).getInfo());
        }
//        //For bias
//        System.out.println("Bias");
//        System.out.println("-Bias input:" + biasInput.getInfo());
//        System.out.println("-Bias hidden:" + biasHidden.getInfo());
        System.out.println("Number of bias connections: " + listBiasConnections.size());
        for(int i = 0; i < listBiasConnections.size(); ++i)
        {
            System.out.println("-" + listBiasConnections.get(i).getInfo());
        }
    }
    
    @Override
    public String toString()
    {
        return new Gson().toJson(this);
    }
}
