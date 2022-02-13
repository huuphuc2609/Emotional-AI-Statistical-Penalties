/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.MyAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Phuc
 */
public class MultilayerNeuralNetwork 
{
    //Params
    int npInput;
    int npHidden;
    int npOutput;
    int numberHiddenLayer;
    double eta; //Learning Rate
    double threshold;
    double E = 0d;
    
    Random generator = new Random();
    
    Layer iLayer;
    Layer oLayer;
    List<Layer> hLayers;
    
    public MultilayerNeuralNetwork(int nInput, int nHidden, int nOutput, int nHiddenLayer)
    {
        npInput = nInput;
        npHidden = nHidden;
        npOutput = nOutput;
        numberHiddenLayer = nHiddenLayer;
                
        iLayer = new Layer(nInput,nInput);       //Input layer
        hLayers = new ArrayList<>();
        for(int i = 0; i < numberHiddenLayer; ++i) //Hidden layer
        {
            Layer temp;
            if(i == 0)
                temp = new Layer(nInput,nHidden);
            else
                temp = new Layer(nHidden, nHidden);
            hLayers.add(temp);
        }
        
        oLayer = new Layer(nHidden, nOutput);  //Output layer
    }
    
    public void setLearningRate(double value)
    {
        eta = value;
    }
    
    public void setThreshold(double value)
    {
        threshold = value;
    }
    
    public void train(List<DataRow> patterns)
    {
        System.out.println("Start training...");
        int loop = 0;
        do
        {
            loop++;
            for(int i = 0; i < patterns.size(); ++i)
            {
                //***************Feed forward***************
                //Input layer
                for(int j = 0; j < npInput; ++j)
                {
                    iLayer.unit[j].a = 0d;
                    //Sum
                    for(int k = 0; k < npInput; ++k)
                    {
                        iLayer.unit[j].a += iLayer.unit[j].w[k]*patterns.get(i).data[k];   
                    }
                    //Plus bias
                    iLayer.unit[j].a += iLayer.bias*iLayer.unit[j].w[npInput];
                    //Activate
                    iLayer.unit[j].z = sigmoid(iLayer.unit[j].a);
                }
                
                //Hidden layers
                for(int l = 0; l < hLayers.size(); ++l)
                {
                    //If the first hidden layer
                    if(l == 0)
                    {
                        for(int k = 0; k < npHidden; ++k)
                        {
                            hLayers.get(l).unit[k].a = 0d;
                            //Sum
                            for(int m = 0; m < npInput; ++m)
                            {
                                hLayers.get(l).unit[k].a += hLayers.get(l).unit[k].w[m]*iLayer.unit[m].z;
                            }
                            //Plus bias
                            hLayers.get(l).unit[k].a += hLayers.get(l).bias*hLayers.get(l).unit[k].w[npInput];

                            //Activate
                            hLayers.get(l).unit[k].z = sigmoid(hLayers.get(l).unit[k].a);
                        }
                    }
                    //The rest of hidden layers
                    else
                    {
                        for(int k = 0; k < npHidden; ++k)
                        {
                            hLayers.get(l).unit[k].a = 0d;
                            //Sum
                            for(int m = 0; m < npHidden; ++m)
                            {
                                hLayers.get(l).unit[k].a += hLayers.get(l).unit[k].w[m]*hLayers.get(l-1).unit[m].z;
                            }
                            //Plus bias
                            hLayers.get(l).unit[k].a += hLayers.get(l).bias*hLayers.get(l).unit[k].w[npHidden];

                            //Activate
                            hLayers.get(l).unit[k].z = sigmoid(hLayers.get(l).unit[k].a);
                        }
                    }
                }
                
                //Output layer
                for(int j = 0; j < npOutput; ++j)
                {
                    oLayer.unit[j].a = 0d;
                    //Sum
                    for(int k = 0; k < npHidden; ++k)
                    {
                        oLayer.unit[j].a += oLayer.unit[j].w[k]*hLayers.get(numberHiddenLayer-1).unit[k].z;   
                    }
                    //Plus bias
                    oLayer.unit[j].a += oLayer.bias*oLayer.unit[j].w[npHidden];
                    //Activate
                    oLayer.unit[j].z = sigmoid(oLayer.unit[j].a);
                }

                //***************Back propagation***************
                //Total error calculation
                E = 0d; 
                //Output layer
                for(int j = 0; j < oLayer.unit.length; ++j)
                {
                    oLayer.unit[j].error = patterns.get(i).lables[j] - oLayer.unit[j].z;
                    E += oLayer.unit[j].error*oLayer.unit[j].error*0.5;
                    oLayer.unit[j].error *= derivative_sigmoid(oLayer.unit[j].a);
                }
                //Hidden layers
                for(int j = numberHiddenLayer-1 ; j > 0; --j)
                {
                    if(j == numberHiddenLayer-1)
                    {
                        for(int k = 0; k < hLayers.get(j).unit.length; ++k)
                        {
                            double sum = 0d;
                            for(int m = 0; m < oLayer.unit.length; ++m)
                            {
                                sum += oLayer.unit[m].w[k]*oLayer.unit[m].error;
                            }
                            sum *= derivative_sigmoid(hLayers.get(j).unit[k].a);
                            hLayers.get(j).unit[k].error = sum;
                        }
                    }
                    else
                    {
                        for(int k = 0; k < hLayers.get(j).unit.length; ++k)
                        {
                            double sum = 0d;
                            for(int m = 0; m < hLayers.get(j+1).unit.length; ++m)
                            {
                                sum += hLayers.get(j+1).unit[m].w[k]*hLayers.get(j+1).unit[m].error;
                            }
                            sum *= derivative_sigmoid(hLayers.get(j).unit[k].a);
                            hLayers.get(j).unit[k].error = sum;
                        }
                    }
                }
                //Input layer
                for(int j = 0; j < npInput; ++j)
                {
                    double sum = 0d;
                    for(int m = 0; m < hLayers.get(0).unit.length; ++m)
                    {
                        sum += hLayers.get(0).unit[m].w[j]*hLayers.get(0).unit[m].error;
                    }
                    sum *= derivative_sigmoid(iLayer.unit[j].a);
                    iLayer.unit[j].error = sum;
                }
                E = E/npOutput;
                //E = Math.sqrt(2*E/npOutput);
                //***************Update weight***************
                //Input layer
                for(int j = 0; j < npInput; ++j)
                {
                    //Update weight
                    for(int k = 0; k < npInput; ++k)
                    {
                        iLayer.unit[j].w[k] = iLayer.unit[j].w[k] + eta*iLayer.unit[j].error * patterns.get(i).data[k];
                    }
                    //Update bias
                    iLayer.unit[j].w[npInput] = iLayer.unit[j].w[npInput] + eta*iLayer.unit[j].error;
                }
                //Hidden layer
                for(int l = 0; l < hLayers.size(); ++l)
                {
                    //Update weight
                    if(l == 0)
                    {
                        for(int j = 0; j < hLayers.get(l).unit.length; ++j)
                        {
                            //Update weight
                            for(int k = 0; k < iLayer.unit.length; ++k)
                            {
                                hLayers.get(l).unit[j].w[k] = hLayers.get(l).unit[j].w[k] + eta*hLayers.get(l).unit[j].error * iLayer.unit[k].z;
                            }
                            //Update bias
                            hLayers.get(l).unit[j].w[npInput] = hLayers.get(l).unit[j].w[npInput] + eta*hLayers.get(l).unit[j].error;
                        }
                    }
                    else
                    {
                        for(int j = 0; j < hLayers.get(l).unit.length; ++j)
                        {
                            //Update weight
                            for(int k = 0; k < hLayers.get(l-1).unit.length; ++k)
                            {
                                hLayers.get(l).unit[j].w[k] = hLayers.get(l).unit[j].w[k] + eta*hLayers.get(l).unit[j].error * hLayers.get(l-1).unit[k].z;
                            }
                            //Update bias
                            hLayers.get(l).unit[j].w[hLayers.get(l-1).unit.length] = hLayers.get(l).unit[j].w[hLayers.get(l-1).unit.length] + eta*hLayers.get(l).unit[j].error;
                        }
                    }
                }
                //Output layer
                for(int j = 0; j < oLayer.unit.length; ++j)
                {
                    //Update weight
                    for(int k = 0; k < npHidden; ++k)
                    {
                        oLayer.unit[j].w[k] = oLayer.unit[j].w[k] + eta*oLayer.unit[j].error * hLayers.get(numberHiddenLayer-1).unit[k].z;
                    }
                    //Update bias
                    oLayer.unit[j].w[npHidden] = oLayer.unit[j].w[npHidden] + eta*oLayer.unit[j].error;
                }
            }
            System.out.println(String.valueOf(E));   
        }while(E > threshold || loop < 30);
        //}while(E > threshold);
        System.out.println("Training finished.");
    }
    
    public boolean[] test(double[] testSample)
    {
        boolean[] result = new boolean[npOutput];
        double[] outputValues = new double[npOutput];
        
        //Input layer
        for(int j = 0; j < npInput; ++j)
        {
            iLayer.unit[j].a = 0d;
            //Sum
            for(int k = 0; k < npInput; ++k)
            {
                iLayer.unit[j].a += iLayer.unit[j].w[k]*testSample[k];   
            }
            //Plus bias
            iLayer.unit[j].a += iLayer.bias*iLayer.unit[j].w[npInput];
            //Activate
            iLayer.unit[j].z = sigmoid(iLayer.unit[j].a);
        }
        for(int l = 0; l < hLayers.size(); ++l)
        {
            //Input layer
            if(l == 0)
            {
                for(int j = 0; j < hLayers.get(l).unit.length; ++j)
                {
                    hLayers.get(l).unit[j].a = 0d;
                    //Sum
                    for(int k = 0; k < iLayer.unit.length; ++k)
                    {
                        hLayers.get(l).unit[j].a += hLayers.get(l).unit[j].w[k]*iLayer.unit[k].z;
                    }
                    //Plus bias
                    hLayers.get(l).unit[j].a += hLayers.get(l).bias*hLayers.get(l).unit[j].w[iLayer.unit.length];
                    //Activate
                    hLayers.get(l).unit[j].z = sigmoid(hLayers.get(l).unit[j].a);
                }
            }
            //Hidden layers
            else
            {
                for(int j = 0; j < hLayers.get(l).unit.length; ++j)
                {
                    hLayers.get(l).unit[j].a = 0d;
                    //Sum
                    for(int k = 0; k < hLayers.get(l-1).unit.length; ++k)
                    {
                        hLayers.get(l).unit[j].a += hLayers.get(l).unit[j].w[k]*hLayers.get(l-1).unit[k].z;
                    }
                    //Plus bias
                    hLayers.get(l).unit[j].a += hLayers.get(l).bias*hLayers.get(l).unit[j].w[hLayers.get(l-1).unit.length];
                    //Activate
                    hLayers.get(l).unit[j].z = sigmoid(hLayers.get(l).unit[j].a);
                }
            }
        }

        //Output layer
        for(int i = 0; i < oLayer.unit.length; ++i)
        {
            oLayer.unit[i].a = 0d;
            //Sum
            for(int j = 0; j < hLayers.get(numberHiddenLayer-1).unit.length; ++j)
            {
                oLayer.unit[i].a += oLayer.unit[i].w[j]*hLayers.get(numberHiddenLayer-1).unit[j].z;
            }
            //Plus bias
            oLayer.unit[i].a += oLayer.bias*oLayer.unit[i].w[hLayers.get(numberHiddenLayer-1).unit.length];
            //Activate
            oLayer.unit[i].z = sigmoid(oLayer.unit[i].a);
            outputValues[i] = oLayer.unit[i].z;
        }
        
        for(int i = 0; i < npOutput; ++i)
        {
            if(outputValues[i] > 0.5)
            {
                result[i] = true;
            }
            else
            {
                result[i] = false;
            }
        }
        return result;
    }
    
    public void testAccuracy(List<double[]> testSample, List<Integer> target)
    {
//        int T = 0;
//        for(int i = 0; i < testSample.size(); ++i)
//        {
//            double[] outputValues = new double[npOutput];
//            for(int l = 0; l < nn.length; ++l)
//            {
//                //Input layer
//                if(l == 0)
//                {
//                    for(int j = 0; j < npInput; ++j)
//                    {
//                        nn[l].unit[j].a = 0d;
//                        //Sum
//                        for(int k = 0; k < npInput; ++k)
//                        {
//                            nn[l].unit[j].a += nn[l].unit[j].w[k]*testSample.get(i)[k];   
//                        }
//                        //Plus bias
//                        nn[l].unit[j].a += nn[l].bias*nn[l].unit[j].w[npInput];
//                        //Activate
//                        nn[l].unit[j].z = sigmoid(nn[l].unit[j].a);
//                    }
//                }
//                //Hidden layer + Output layer
//                else
//                {
//                    for(int j = 0; j < nn[l].unit.length; ++j)
//                    {
//                        nn[l].unit[j].a = 0d;
//                        //Sum
//                        for(int k = 0; k < nn[l-1].unit.length; ++k)
//                        {
//                            nn[l].unit[j].a += nn[l].unit[j].w[k]*nn[l-1].unit[k].z;
//                        }
//                        //Plus bias
//                        nn[l].unit[j].a += nn[l].bias*nn[l].unit[j].w[nn[l-1].unit.length];
//                        //Activate
//                        nn[l].unit[j].z = sigmoid(nn[l].unit[j].a);
//                        if(l == nn.length - 1)
//                        {
//                            outputValues[j] = nn[l].unit[j].z;
//                        }
//                    }
//                }
//            }
//
//            double max = outputValues[0];
//            int idx = 0;
//            for(int j = 0; j < npOutput; ++j)
//            {
//                if(max < outputValues[j])
//                {
//                    max = outputValues[j];
//                    idx = j;
//                }
//            }
//            if(idx == target.get(i))
//                T++;
//        }
//        double acc = 1.0*T/testSample.size();
//        System.out.println("Accuracy = " + String.valueOf(acc));
    }
    
    public static double sigmoid(double x) 
    {
        return (1.0 / ( 1.0 + Math.exp(-1.0*x)));
    }
    
    public static double derivative_sigmoid(double x)
    {
        return sigmoid(x)*(1.0-sigmoid(x));
    }
    
    public static double derivative_tanh(double x)
    {
        return 1-(Math.tanh(x)*Math.tanh(x));
    }
    
    public static double euclidean_distance(double[] a, double[] b)
    {
        double result = 0;
        for(int i = 0; i < a.length; ++i)
        {
            result += (a[i] - b[i])*(a[i] - b[i]);
        }
        return Math.sqrt(result);
    }
}
