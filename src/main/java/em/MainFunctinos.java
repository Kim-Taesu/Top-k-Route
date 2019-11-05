package em;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MainFunctinos {

    SubFunction subFunction = new SubFunction();


    public HashMap<String, HashMap<String, Double>> locationProb(int destNum) {

        System.out.println("\n==============================");
        System.out.println("locationProb start");
        System.out.println("==============================\nResult");

        // 모든 경우의 변수
        ArrayList<String> cases = subFunction.totalCase(destNum);

        // 경우의 수 확률 변수
        HashMap<String, HashMap<String, Double>> caseProb = new HashMap<String, HashMap<String, Double>>();


        for (String c : cases) {
            HashMap<String, Double> tmp = new HashMap<String, Double>();
            for (int i = 0; i < destNum; i++) {
                // 특정 위치
                StringBuilder location = new StringBuilder(String.format("%0" + destNum + "d", 0));
                location.setCharAt(i, '1');

                tmp.put(location.toString(), subFunction.caseProb(location.toString(), c));
            }

            caseProb.put(c, tmp);
        }


        // print
        Iterator<String> key = caseProb.keySet().iterator();
        while (key.hasNext()) {
            String k = key.next();
            System.out.println("key is " + k);
            System.out.println(caseProb.get(k));
        }

        System.out.println("\n==============================");
        System.out.println("locationProb end");
        System.out.println("==============================\n");

        return caseProb;
    }

    public HashMap<Integer, HashMap<Integer, Double>> initTheta(int destNum) {
        HashMap<Integer, HashMap<Integer, Double>> initTheta = new HashMap<Integer, HashMap<Integer, Double>>();

        System.out.println("\n==============================");
        System.out.println("initTheta start");
        System.out.println("==============================\nResult");

        for (int i = 0; i < destNum; i++) {

            HashMap<Integer, Double> tmp = new HashMap<Integer, Double>();
            for (int j = 0; j < destNum; j++) {
                tmp.put(j, 1 / Math.pow(destNum, 2));
            }
            initTheta.put(i, tmp);

            // print
            System.out.println(i);
            System.out.println(tmp);
        }

        System.out.println("\n==============================");
        System.out.println("initTheta end");
        System.out.println("==============================\n");

        return initTheta;
    }



}
