package Java;

import java.util.ArrayList;
import java.util.HashMap;


public class Main {


    public static void main(String args[]) {


        /** 필요 객체 생성 **/
        MainFunctinos mainFunctinos = new MainFunctinos();
        Parameters parameters = new Parameters();

        /** 필요 변수 get **/
        int destNum = parameters.getDestNum();
        double threshold = parameters.getThreshold();
        double totalThetaTmp = parameters.getTotalThetaTmp();

        /** DB Read **/
        ArrayList<String> dbData = mainFunctinos.dbRead(parameters.getCollection());

        /** DB data total count **/
        double dbTotal = Double.parseDouble(dbData.remove(0));
        System.out.println("dbTotal : " + dbTotal);

        /** 위치 경우의 수**/
        HashMap<String, HashMap<String, Double>> locationProb = mainFunctinos.locationProb(destNum, parameters.getqValue(), parameters.getpValue());
        HashMap<String, HashMap<String, Double>> result = (HashMap<String, HashMap<String, Double>>) locationProb.clone();


        /** 출발 지역 **/
        for (int a = 0; a < destNum; a++) {
            StringBuilder xA = new StringBuilder(String.format("%0" + destNum + "d", 0));
            xA.setCharAt(a, '1');

            /** 도착 지역 **/
            for (int b = 0; b < destNum; b++) {
                System.out.println("a: " + a + " || " + "b: " + b);
                StringBuilder xB = new StringBuilder(String.format("%0" + destNum + "d", 0));
                xB.setCharAt(a, '1');

                /** P(rh.lp, rh.lc | Lp=xa, Lc=xb) **/
                ArrayList<Double> dbProb = mainFunctinos.getDbProb(dbData, locationProb, xA.toString(), xB.toString());

                /** EM Algorithm **/
                double nextTheta = mainFunctinos.emAlgoritm(
                        1 / Math.pow(destNum, 2),
                        0.0,
                        totalThetaTmp,
                        dbProb, dbTotal, threshold);
                HashMap<String, Double> tmp = new HashMap<>();
                tmp.put(xB.toString(), nextTheta);
                result.put(xA.toString(), tmp);
            }
        }
        System.out.println(result);
    }
}
