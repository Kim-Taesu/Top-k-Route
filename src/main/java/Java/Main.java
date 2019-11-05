package Java;

import java.util.ArrayList;
import java.util.HashMap;


public class Main {
    public static void main(String args[]) {
        MainFunctinos mainFunctinos = new MainFunctinos();
        Parameters parameters = new Parameters();
        int destNum = parameters.getDestNum();
        double threshold = parameters.getThreshold();
        double totalThetaTmp = parameters.getTotalThetaTmp();


        HashMap<String, HashMap<String, Double>> locationProb = mainFunctinos.locationProb(destNum, parameters.getqValue(), parameters.getpValue());
        HashMap<String, HashMap<String, Double>> result = mainFunctinos.locationProb(destNum, parameters.getqValue(), parameters.getpValue());


        // All DB Data Read
        ArrayList<String> dbData = mainFunctinos.dbRead();

        // DB Total
        double dbTotal = Double.parseDouble(dbData.remove(0));


        // 출발 지역
        for (int a = 0; a < destNum; a++) {
            StringBuilder xA = new StringBuilder(String.format("%0" + destNum + "d", 0));
            xA.setCharAt(a, '1');

            // 도착 지역
            for (int b = 0; b < destNum; b++) {
                StringBuilder xB = new StringBuilder(String.format("%0" + destNum + "d", 0));
                xB.setCharAt(a, '1');

                // DB Data 확률 계산
                ArrayList<Double> dbProb = mainFunctinos.getDbProb(dbData, locationProb, xA.toString(), xB.toString());

                // EM Algorithm
                double nextTheta = mainFunctinos.emAlgoritm(
                        1 / Math.pow(destNum, 2),
                        0.0,
                        totalThetaTmp,
                        dbProb, dbTotal, threshold);

                HashMap<String, Double> tmp = new HashMap<String, Double>();
                tmp.put(xB.toString(), nextTheta);
                result.put(xA.toString(), tmp);
            }
        }

    }
}
