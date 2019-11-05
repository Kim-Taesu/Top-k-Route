package em;

import java.util.ArrayList;
import java.util.HashMap;


public class Main {
    public static void main(String args[]) {
        MainFunctinos mainFunctinos = new MainFunctinos();
        Parameters parameters = new Parameters();

        int destNum = parameters.getDestNum();
        double threshold = parameters.getThreshold();
        HashMap<String, HashMap<String, Double>> locationProb = mainFunctinos.locationProb(destNum);
        HashMap<Integer, HashMap<Integer, Double>> theta;
        HashMap<String, HashMap<String, Double>> result = mainFunctinos.locationProb(destNum);


        // DB Total
        double dbTotal = 0.0;

        // All DB Data Read
        ArrayList<String> dbData = new ArrayList<String>();
        for (int d = 0; d < dbTotal; d++) {
            // DB data
            String rlp = null;
            String rlc = null;
            String newLine = rlp + ":" + rlc;
            dbData.add(newLine);
        }

        // 출발 지역
        for (int a = 0; a < destNum; a++) {
            StringBuilder xA = new StringBuilder(String.format("%0" + destNum + "d", 0));
            xA.setCharAt(a, '1');

            // 도착 지역
            for (int b = 0; b < destNum; b++) {
                StringBuilder xB = new StringBuilder(String.format("%0" + destNum + "d", 0));
                xB.setCharAt(a, '1');

                // DB Data 확률 계산
                ArrayList<Double> dbProb = new ArrayList<Double>();
                for (String line : dbData) {
                    String[] tmp = line.split(":");
                    // DB data Prob
                    double rlpProb = locationProb.get(tmp[0]).get(xA);
                    double rlcProb = locationProb.get(tmp[1]).get(xB);
                    dbProb.add(rlpProb * rlcProb);
                }

                // EM Algorithm
                // Init Theta 생성 및 초기화
                theta = mainFunctinos.initTheta(destNum);


                // 총합 Theta
                double totalThetaTmp = 0.0;
                for (int q = 0; q < destNum; q++) {
                    for (int w = 0; w < destNum; w++) {

                        // 현재 출발지역과 이전지역이 같은 경우는 넘어간다. (Theta 값이 바뀌기 때문)
                        if (q == a && w == b) continue;
                        totalThetaTmp += theta.get(q).get(w);
                    }
                }


                // 현재 Theta
                double curTheta = theta.get(a).get(b);
                double nextTheta = 0.0;
                
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

                HashMap<String, Double> tmp = new HashMap<String, Double>();
                tmp.put(xB.toString(), nextTheta);
                result.put(xA.toString(), tmp);
                theta.clear();
            }
        }

    }
}
