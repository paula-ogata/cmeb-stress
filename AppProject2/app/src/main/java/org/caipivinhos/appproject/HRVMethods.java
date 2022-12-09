package org.caipivinhos.appproject;

import java.util.ArrayList;

public class HRVMethods {

    //Melhor para 10s
    static double rmssdCalculation(ArrayList<Integer> myRR){
        double[] dif = new double[myRR.size()-1];

        for (int i = 0; i < myRR.size()-1; i++) {
            dif[i] = Math.pow((myRR.get(i)-myRR.get(i+1)),2);

            //System.out.println(dif[i]);
        }
        double sum = 0;
        for (int i = 0; i < dif.length-1; i++) {
            sum += dif[i];

            //System.out.println(dif[i]);
        }

        double average = sum / dif.length;

        return Math.sqrt(average);
    }


    static double avgCalculation(int[] myRR) {
        double average;
        float sum = 0;

        //compute sum
        for (int rr : myRR) {
            sum += rr;
        }

        average = (sum / myRR.length);
        return average;
    }

    public static double stdCalculation(double[] avgRR) {

        // get the sum of array
        double sum = 0.0;
        for (double rravg : avgRR) {
            sum += rravg;
        }

        // get the mean of array
        int length = avgRR.length;
        double mean = sum / length;

        // calculate the standard deviation
        double standardDeviation = 0.0;
        for (double rravg : avgRR) {
            standardDeviation += Math.pow(rravg - mean, 2);
        }

        return Math.sqrt(standardDeviation / length);
    }
}
