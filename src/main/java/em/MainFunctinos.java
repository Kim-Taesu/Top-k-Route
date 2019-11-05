package em;

import java.util.ArrayList;
import java.util.HashMap;

public class MainFunctinos {

    SubFunction subFunction = new SubFunction();


    public HashMap<String, HashMap<String, Double>> locationProb(int destNum, double qValue, double pValue) {

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

                tmp.put(location.toString(), subFunction.caseProb(location.toString(), c, qValue, pValue));
            }

            caseProb.put(c, tmp);
        }
        return caseProb;
    }

    public ArrayList<String> dbRead() {
        // DB Total
        double dbTotal = 0.0;

        ArrayList<String> dbData = new ArrayList<String>();
        for (int d = 0; d < dbTotal; d++) {
            // DB data
            String rlp = null;
            String rlc = null;
            String newLine = rlp + ":" + rlc;
            dbData.add(newLine);
        }
        return dbData;
    }

    public ArrayList<Double> getDbProb(ArrayList<String> dbData,
                                       HashMap<String, HashMap<String, Double>> locationProb,
                                       String xA, String xB) {
        ArrayList<Double> result = new ArrayList<Double>();
        for (String line : dbData) {
            String[] tmp = line.split(":");
            // DB data Prob
            double rlpProb = locationProb.get(tmp[0]).get(xA);
            double rlcProb = locationProb.get(tmp[1]).get(xB);
            result.add(rlpProb * rlcProb);
        }
        return result;
    }

    public Double getTotalThetaTmp(int destNum, int a, int b,
                                   HashMap<Integer, HashMap<Integer, Double>> theta) {

        double totalThetaTmp = 0.0;
        for (int q = 0; q < destNum; q++) {
            for (int w = 0; w < destNum; w++) {

                // 현재 출발지역과 이전지역이 같은 경우는 넘어간다. (Theta 값이 바뀌기 때문)
                if (q == a && w == b) continue;
                totalThetaTmp += theta.get(q).get(w);
            }
        }
        return totalThetaTmp;
    }


    public Double emAlgoritm(double curTheta, double nextTheta, double totalThetaTmp,
                             ArrayList<Double> dbProb, double dbTotal, double threshold
    ) {

        while (true) {
            double totalTheta = totalThetaTmp + curTheta;
            // M-step
            for (double rlProb : dbProb) {
                // E-Step
                double EResult = (curTheta * rlProb) / (totalTheta * rlProb);
                nextTheta += EResult;
            }
            // MResult
            nextTheta /= dbTotal;

            // break 조건
            if (Math.abs(nextTheta - curTheta) < threshold) break;

            curTheta = nextTheta;
            nextTheta = 0.0;

            // Normalize
        }

        return nextTheta;
    }

}
