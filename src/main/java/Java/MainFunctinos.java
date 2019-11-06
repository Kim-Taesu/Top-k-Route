package Java;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;

public class MainFunctinos {

    SubFunction subFunction = new SubFunction();

    /** 특정 지역별로 모든 노이즈 경우의 수에 대한 확률 설정 **/
    public HashMap<String, HashMap<String, Double>> locationProb(int destNum, double qValue, double pValue) {

        // 지역 수에 맞는 모든 비트스트림 경우의 수
        ArrayList<String> cases = subFunction.totalCase(destNum);

        // 특정 지역에 대한 모든 노이즈 경우의 수의 확률 저장 객체
        HashMap<String, HashMap<String, Double>> caseProb = new HashMap<String, HashMap<String, Double>>();

        // 디바이스가 있을 수 있는 모든 위치에 대해
        for (String c : cases) {

            HashMap<String, Double> tmp = new HashMap<String, Double>();

            // 이동할 수 있는 모든 위치에 대해
            for (int i = 0; i < destNum; i++) {

                // 도착 위치 설정
                StringBuilder location = new StringBuilder(String.format("%0" + destNum + "d", 0));
                location.setCharAt(i, '1');

                // 특정 위치에서 특정 노이즈값으로 될 확률
                Double originToNoise = subFunction.caseProb(location.toString(), c, qValue, pValue);

                // 특정 위치 : 특정 노이즈 값으로 될 확률
                tmp.put(location.toString(), originToNoise);
            }

            // 특정 노이즈 값 : 모든 위치에서 특정 노이즈 값으로 될 확률
            caseProb.put(c, tmp);
        }
        return caseProb;
    }

    /** DB data read **/
    public ArrayList<String> dbRead(MongoCollection<Document> collection) {

        // data object
        ArrayList<String> dbData = new ArrayList<String>();

        // Db data total size
        double dbTotal = 0.0;


        // select * from collection;
        FindIterable<Document> cursor = collection.find();

        // select query result
        for (Document document : cursor) {
            String rlp = (String) document.get("rlp");
            String rlc = (String) document.get("rlc");
            String newLine = rlp + ":" + rlc;
            dbData.add(newLine);
            dbTotal++;
        }

        // data save
        dbData.add(0, String.valueOf(dbTotal));

        return dbData;
    }

    /** P(rh.lp, rh.lc | Lp=xa, Lc=xb) **/
    public ArrayList<Double> getDbProb(ArrayList<String> dbData,
                                       HashMap<String, HashMap<String, Double>> locationProb,
                                       String xA, String xB) {
        ArrayList<Double> result = new ArrayList<Double>();

        // 전체 DB 데이터에 대해
        for (String line : dbData) {

            // 각 로우를 : 로 split
            String[] tmp = line.split(":");

            // 이전에 존재했던 위치가 없을 때 (Lp 가 없다.)
            if (tmp[0].equals("null")) {
                result.add(1.0);
            }

            // 이전에 존재했던 위치가 있을 때 (Lp 가 있다.)
            else {
                // xa 일때 해당 db row의 lp가 될 확률
                double rlpProb = locationProb.get(tmp[0]).get(xA);

                // xb 일때 해당 db row의 lc가 될 확률
                double rlcProb = locationProb.get(tmp[1]).get(xB);

                // 확률 계산 후 저장
                result.add(rlpProb * rlcProb);
            }
        }
        return result;
    }

    /** EM Algorithm **/
    public Double emAlgoritm(double curTheta, double nextTheta, double totalThetaTmp,
                             ArrayList<Double> dbProb, double dbTotal, double threshold
    ) {

        // Iteration
        while (true) {
            double totalTheta = totalThetaTmp + curTheta;
            // M Step

            // E Step
            for (double rlProb : dbProb) {
                double EResult = (curTheta * rlProb) / (totalTheta * rlProb);
                nextTheta += EResult;
            }

            // M Step Result
            nextTheta /= dbTotal;

            // Iteration break 조건
            if (Math.abs(nextTheta - curTheta) < threshold) break;

            // Theta(t) 값 업데이트
            curTheta = nextTheta;

            // Theta(t+1) 값 초기화
            nextTheta = 0.0;

            // Normalize
        }

        return nextTheta;
    }
}
