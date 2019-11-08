package Java.Main;

import Java.Config.Config;
import Java.DB.DbFunctions;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;


public class Main {


    public static void main(String args[]) {

        /** 필요 객체 생성 **/
        Functinos functinos = new Functinos();
        Config config = new Config();
        DbFunctions dbFunctions = new DbFunctions();

        /** 필요 변수 **/
        System.out.println("\n==================\nParameters Info\n==================\n");
        int destNum = config.getDestNum();
        double threshold = config.getThreshold();
        double qValue = config.getqValue();
        double pValue = config.getpValue();
        double[][] theta = functinos.initTheta(destNum);
        String[] noiseCases = functinos.noiseCases(destNum, (int) Math.pow(2, destNum));
        double[][] probNoiseOrigin = functinos.probNoiseOrigin(destNum, noiseCases, qValue, pValue);
        double[][] result = new double[destNum][destNum];
        int iterationCount = 0;


        /** DB Read **/
        System.out.println("\n==================\nmongoDB setting\n==================\n");
        ArrayList<String> dbData = functinos.dbRead(config.getIp(), config.getPort(),
                config.getDbName(), config.getCollectionTaxi());
        // DB data total count
        double dbTotal = dbData.size();
        System.out.println("dbTotal : " + dbTotal);
        MongoCollection<Document> probCollection = dbFunctions.connect(config.getIp(), config.getPort(), config.getDbName(), config.getCollectionProbNoise());


        System.out.println("\n==================\nIteration Start\n==================\n");
        /** Iteration **/
        while (true) {
            iterationCount++;
            System.out.println("Iteration Count : "+iterationCount);
            double[][] preTheta = new double[theta.length][theta[0].length];
            for (int i = 0; i < destNum; i++) {
                for (int j = 0; j < destNum; j++) {
                    preTheta[i][j] = theta[i][j];
                }
            }

            double thetaSum = 0.0;
            for (int a = 0; a < destNum; a++) {
                StringBuilder xA = new StringBuilder(String.format("%0" + destNum + "d", 0));
                xA.setCharAt(a, '1');

                for (int b = 0; b < destNum; b++) {
                    StringBuilder xB = new StringBuilder(String.format("%0" + destNum + "d", 0));
                    xB.setCharAt(b, '1');
//                    System.out.println("a: " + xA.toString() + " -> " + "b: " + xB.toString());

                    /** M Step ==  {(1<u<dbTotal) P( Xa, Xb | rlp, rlc ; theta[a][b] ) } / dbTotal **/
                    double eStepSum = 0.0;
                    double nullCount = 0;

                    for (int u = 0; u < dbTotal; u++) {
                        String[] noiseData = dbData.get(u).split(":");
                        String rlp = noiseData[0];
                        String rlc = noiseData[1];

                        if (rlp.equals("null")) {
                            nullCount++;
                            continue;
                        }


                        /** E Step == P( Xa, Xb | rlp, rlc ; theta[a][b] ) **/
                        // theta[a][b] * P( rlp, rlc | Xa, Xb )
                        double eStep1 = preTheta[a][b]
                                * probNoiseOrigin[Integer.parseInt(rlp, 2)][(int) Math.log(Integer.parseInt(xA.toString(), 2))]
                                    * probNoiseOrigin[Integer.parseInt(rlc, 2)][(int) Math.log(Integer.parseInt(xB.toString(), 2))];

                        // (1<v<n, 1<w<n) theta[v][w] * P( rlp, rlc | Xv, Xw)
                        double eStep2 = 0.0;
                        for (int v = 0; v < destNum; v++) {
                            StringBuilder xQ = new StringBuilder(String.format("%0" + destNum + "d", 0));
                            xQ.setCharAt(v, '1');

                            for (int w = 0; w < destNum; w++) {
                                StringBuilder xW = new StringBuilder(String.format("%0" + destNum + "d", 0));
                                xW.setCharAt(w, '1');
                                eStep2 += preTheta[v][w]
                                        * probNoiseOrigin[Integer.parseInt(rlp, 2)][(int) Math.log(Integer.parseInt(xQ.toString(), 2))]
                                        * probNoiseOrigin[Integer.parseInt(rlc, 2)][(int) Math.log(Integer.parseInt(xW.toString(), 2))];
                            }
                        }

                        // theta[a][b] * P( rlp, rlc | Xa, Xb )
                        // ----------------------------------------------------
                        // (1<v<n, 1<w<n) theta[v][w] * P( rlp, rlc | Xv, Xw)
                        double eStepResult = eStep1 / eStep2;
                        /** E Step end **/

                        // (1<u<dbTotal) P( Xa, Xb | rlp, rlc ; theta[a][b] )
                        eStepSum += eStepResult;
                    }


                    double mStepResult = eStepSum / (dbTotal - nullCount);

//                    System.out.println("\t\tE Step result total : " + eStepSum);
//                    System.out.println("\t\tM Step result : " + mStepResult);

                    theta[a][b] = mStepResult;
                    thetaSum+=theta[a][b];
                }
            }
            /** M Step end **/

            /** Normalize **/
            double normalizeCheck = 0.0;
            for (int a = 0; a < destNum; a++) {
                for (int b = 0; b < destNum; b++) {
                    theta[a][b] /= thetaSum;
                    normalizeCheck += theta[a][b];
                }
            }
//            System.out.println("nomalized values sum : " + normalizeCheck/thetaSum);

            /** max( theta(t+1)[a][b] - theta(t)[a][b] ) **/
//            System.out.println("\n\nTheta Value");
            double max = 0.0;
            for (int a = 0; a < destNum; a++) {
                for (int b = 0; b < destNum; b++) {
//                    System.out.println("a: " + a + ", b: " + b + ", theta: " + theta[a][b]);
//                    System.out.println(theta[a][b] + " || " + preTheta[a][b]);

                    double compareValue = Math.abs(theta[a][b] - preTheta[a][b]);
                    max = Math.max(max, compareValue);
                }

                /** P( a -> b) update **/
                for (int b = 0; b < destNum; b++)
                    result[a][b] = theta[a][b];
            }


            /** Iteration Check **/
            System.out.println("\tcompare : max theta("+max+") and threshold(" +threshold+")");
            if (max < threshold) break;
        }


        double resultSum = 0.0;
        System.out.println("\n\nResult");
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < destNum; j++) {

                System.out.println("Prob " + i + " -> " + j + " : " + result[i][j]);
                resultSum += result[i][j];

                Document document = new Document();
                document.put("a",i);
                document.put("b",j);
                document.put("prob",result[i][j]);
                dbFunctions.insert(document,probCollection);
            }
        }
        System.out.println("total : " + resultSum);
    }
}
