package Java;


import java.util.HashMap;

public class Parameters {

    // values
    private double epslion = 1.0;
    private double qValue = 1.0 / (Math.exp(epslion) + 1);
    private double pValue = 0.5;
    private int destNum = 5;
    private double threshold = 0.0;
    private HashMap<Integer, HashMap<Integer, Double>> initTheta;
    private double totalThetaTmp=0.0;


    // getter
    public int getDestNum() {
        return destNum;
    }

    public double getqValue() {
        return qValue;
    }

    public double getpValue() {
        return pValue;
    }

    public double getThreshold() {
        return threshold;
    }

    public HashMap<Integer, HashMap<Integer, Double>> getInitTheta() {
        return (HashMap<Integer, HashMap<Integer, Double>>) initTheta.clone();
    }

    public double getTotalThetaTmp() {
        return totalThetaTmp;
    }

    public Parameters(){

        // init theta
        initTheta = new HashMap<Integer, HashMap<Integer, Double>>();
        for (int i = 0; i < destNum; i++) {
            HashMap<Integer, Double> tmp = new HashMap<Integer, Double>();
            for (int j = 0; j < destNum; j++) {
                tmp.put(j, 1 / Math.pow(destNum, 2));
            }
            initTheta.put(i, tmp);
        }

        // init totalThetaTmp
        for (int q = 0; q < destNum; q++) {
            for (int w = 0; w < destNum; w++) {
                totalThetaTmp+=1/Math.pow(destNum,2);
            }
        }
        totalThetaTmp-=1/Math.pow(destNum,2);

    }


}
