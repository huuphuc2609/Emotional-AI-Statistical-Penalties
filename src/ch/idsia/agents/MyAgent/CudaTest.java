/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.MyAgent;

/**
 *
 * @author Phuc
 */

import jcuda.*;
import jcuda.runtime.JCuda;


public class CudaTest {
    public static void main(String args[])
    {
        Pointer pointer = new Pointer();
        JCuda.cudaMalloc(pointer, 4);
        System.out.println("Pointer: "+pointer);
        JCuda.cudaFree(pointer);
    }
}
