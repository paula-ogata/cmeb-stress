package org.caipivinhos.appproject;

import java.util.ArrayList;

public class HRVMethods {

    //Melhor para 10s
    static double rmssdCalculation(ArrayList<Integer> myRR){
        double[] dif = new double[myRR.size()-1];

        for (int i = 0; i < myRR.size()-1; i++) {
            dif[i] = Math.pow((myRR.get(i)-myRR.get(i+1)),2);
        }
        double sum = 0;
        for (int i = 0; i < dif.length-1; i++) {
            sum += dif[i];
        }

        double average = sum / dif.length;

        return Math.sqrt(average);
    }

    static double getStressPercentage(ArrayList<Integer> myRR, double median_level){
        double rmssd = rmssdCalculation(myRR);
        double stressPc = -1;

        stressPc = 50 + ((median_level-rmssd)/median_level)*50;

        return stressPc;
    }

    static int percLabeling(double stressPc){
        int stressLevel = -1;

        if (stressPc > 70){
            stressLevel = 2; //Severe
        } else if (stressPc > 40){
            stressLevel = 1; //High
        } else if (stressPc <= 40){
            stressLevel = 0; //Moderate
        }

        return stressLevel;
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
