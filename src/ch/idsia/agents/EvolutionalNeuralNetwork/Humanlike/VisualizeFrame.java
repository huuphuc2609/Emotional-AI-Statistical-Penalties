/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Humanlike;

import ch.idsia.agents.EvolutionalNeuralNetwork.Connection;
import ch.idsia.agents.EvolutionalNeuralNetwork.Genome;
import ch.idsia.agents.EvolutionalNeuralNetwork.Layer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @author Phuc
 */
public class VisualizeFrame extends JFrame {
    
    Genome gen;
    NNPoint inputBias;
    NNPoint hiddenBias;
    List<NNPoint> listInputPos;
    List<NNPoint> listHiddenPos;
    List<NNPoint> listOutputPos;
    
    public VisualizeFrame()
    {
        this.setSize(640,480);//400 width and 500 height  
        this.setLayout(null);//using no layout managers  
        this.setVisible(true);//making the frame visible  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        listInputPos = new ArrayList<>();
        listHiddenPos = new ArrayList<>();
        listOutputPos = new ArrayList<>();
    }
    
    public VisualizeFrame(Genome inputGen)
    {
        gen = inputGen;
        this.setSize(1280,768);//400 width and 500 height  
        this.setLayout(null);//using no layout managers  
        this.setVisible(true);//making the frame visible  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        listInputPos = new ArrayList<>();
        listHiddenPos = new ArrayList<>();
        listOutputPos = new ArrayList<>();
    }
    
    public void setGenome(Genome inputGen)
    {
        gen = inputGen;
        this.setSize(1280,768);//400 width and 500 height  
        this.setLayout(null);//using no layout managers  
        this.setVisible(true);//making the frame visible  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        listInputPos = new ArrayList<>();
        listHiddenPos = new ArrayList<>();
        listOutputPos = new ArrayList<>();
    }
    
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        if(gen == null)
            return;
        //Input nodes
        int x,y,h,w;
        x = 100;
        y = 50;
        h = 10;
        w = 10;
        g.setColor(Color.green);
        for(int i = 0; i < gen.listInputNodes.size(); ++i)
        {
            listInputPos.add(new NNPoint(x,y,gen.listInputNodes.get(i).index));
            g.drawOval(x, y, w, h); //x,y,w,h
            g.fillOval(x, y, w, h);
            y+=30;
        }
        //Bias input to hidden
        inputBias = new NNPoint(x,y,-1);
        g.setColor(Color.white);
        g.drawOval(x,y,w,h);
        g.fillOval(x, y, w, h);
        //Hidden nodes
        x = x + 400;
        y = 50;
        g.setColor(Color.green);
        for(int i = 0; i < gen.listHiddenNodes.size(); ++i)
        {
            listHiddenPos.add(new NNPoint(x,y,gen.listHiddenNodes.get(i).index));
            g.drawOval(x, y, w, h); //x,y,w,h
            g.fillOval(x, y, w, h);
            y+=50;
        }
        //Bias hiddden to output
        hiddenBias = new NNPoint(x,y,-1);
        g.setColor(Color.white);
        g.drawOval(x,y,w,h);
        g.fillOval(x, y, w, h);
        //Output nodes
        x = x + 400;
        y = 50;
        g.setColor(Color.green);
        for(int i = 0; i < gen.listOutputNodes.size(); ++i)
        {
            listOutputPos.add(new NNPoint(x,y,gen.listOutputNodes.get(i).index));
            g.drawOval(x, y, w, h); //x,y,w,h
            g.fillOval(x, y, w, h);
            y+=50;
        }
        
        //Draw connections
        for(int i = 0; i < gen.listConnections.size(); ++i)
        {
            Connection tmp = gen.listConnections.get(i);
            int fromId = tmp.idxFromNode;
            int toId = tmp.idxToNode;
            
            int fromX = 0;
            int fromY = 0;
            int toX = 0;
            int toY = 0;
            if(tmp.fromLayer == Layer.INPUT)
            {
                for(int j = 0; j < listInputPos.size(); ++j)
                {
                    NNPoint p = listInputPos.get(j);
                    if(p.NodeID == fromId)
                    {
                        fromX = p.x;
                        fromY = p.y;
                        break;
                    }
                }
            }
            else if(tmp.fromLayer == Layer.HIDDEN)
            {
                for(int j = 0; j < listHiddenPos.size(); ++j)
                {
                    NNPoint p = listHiddenPos.get(j);
                    if(p.NodeID == fromId)
                    {
                        fromX = p.x;
                        fromY = p.y;
                        break;
                    }
                }
            }
            
            if(tmp.toLayer == Layer.HIDDEN)
            {
                for(int j = 0; j < listHiddenPos.size(); ++j)
                {
                    NNPoint p = listHiddenPos.get(j);
                    if(p.NodeID == toId)
                    {
                        toX = p.x;
                        toY = p.y;
                        break;
                    }
                }
            }
            else if(tmp.toLayer == Layer.OUTPUT)
            {
                for(int j = 0; j < listOutputPos.size(); ++j)
                {
                    NNPoint p = listOutputPos.get(j);
                    if(p.NodeID == toId)
                    {
                        toX = p.x;
                        toY = p.y;
                        break;
                    }
                }
            }
            Graphics2D g2 = (Graphics2D) g;
            float lineWidth = 0;
            if(tmp.w >= 0)
            {
                g2.setColor(Color.black);
                lineWidth = (float)tmp.w;
            }
            else
            {
                g2.setColor(Color.red);
                lineWidth = (float)(-tmp.w);
            }
           
            
            g2.setStroke(new BasicStroke((int)lineWidth*6));
            if(tmp.fromLayer == tmp.toLayer)
            {
                QuadCurve2D q = new QuadCurve2D.Float();
                q.setCurve(fromX+w/2, fromY+h/2, toX+w/2-fromX+w/2, (toY-fromY)/2+fromY, toX+w/2, toY+h/2);
                g2.draw(q);
            }
            else
            {
                g2.drawLine(fromX + w/2, fromY + h/2, toX + w/2, toY + + h/2);
            }
        }
        //Bias connections
        for(int i = 0; i < gen.listBiasConnections.size(); ++i)
        {
            Connection tmp = gen.listBiasConnections.get(i);
            int fromId = tmp.idxFromNode;
            int toId = tmp.idxToNode;
            
            int fromX = 0;
            int fromY = 0;
            int toX = 0;
            int toY = 0;
            if(tmp.fromLayer == Layer.INPUT)
            {
                fromX = inputBias.x;
                fromY = inputBias.y;
            }
            else if(tmp.fromLayer == Layer.HIDDEN)
            {
                fromX = hiddenBias.x;
                fromY = hiddenBias.y;
            }
            
            if(tmp.toLayer == Layer.HIDDEN)
            {
                for(int j = 0; j < listHiddenPos.size(); ++j)
                {
                    NNPoint p = listHiddenPos.get(j);
                    if(p.NodeID == toId)
                    {
                        toX = p.x;
                        toY = p.y;
                        break;
                    }
                }
            }
            else if(tmp.toLayer == Layer.OUTPUT)
            {
                for(int j = 0; j < listOutputPos.size(); ++j)
                {
                    NNPoint p = listOutputPos.get(j);
                    if(p.NodeID == toId)
                    {
                        toX = p.x;
                        toY = p.y;
                        break;
                    }
                }
            }
                
            Graphics2D g2 = (Graphics2D) g;
            float lineWidth = 0;
            if(tmp.w >= 0)
            {
                g2.setColor(Color.black);
                lineWidth = (float)tmp.w;
            }
            else
            {
                g2.setColor(Color.red);
                lineWidth = (float)(-tmp.w);
            }
            g2.drawLine(fromX + w/2, fromY + h/2, toX + w/2, toY + + h/2);
        }
        
    }
}
