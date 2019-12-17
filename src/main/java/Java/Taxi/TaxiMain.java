//package Java.Taxi;
//
//import Java.Config.Config;
//import Java.DB.DbService;
//import com.mongodb.client.MongoCollection;
//import org.bson.Document;
//
//import java.util.ArrayList;
//
//
//public class TaxiMain {
//
//
//    public static void main(String args[]) {
//
//
//        /** 필요 객체 생성 **/
//        TaxiService taxiService = new TaxiService();
//        Config config = new Config();
//        DbService dbService = new DbService();
//
//        /** 필요 변수 **/
//        System.out.println("\n==================\nParameters Info\n==================\n");
//        int destNum = config.getDestNum();
//        double threshold = config.getThreshold();
//        double qValue = config.getqValue();
//        double pValue = config.getpValue();
//
//        double[][] theta = taxiService.initTheta(destNum);
//        String[] noiseCases = taxiService.computeNoiseCases(destNum);
//        double[][] probNoiseOrigin = taxiService.computeProbNoiseOrigin(destNum, noiseCases, qValue, pValue);
//
//        double[][] result = new double[destNum][destNum];
//        int iterationCount = 0;
//
//
//        /** DB Read **/
//        System.out.println("\n==================\nmongoDB Data Read\n==================\n");
//        ArrayList<String> dbData = taxiService.dbRead(config.getIp(), config.getPort(),
//                config.getDbName(), config.getTaxiColl());
//        // DB data total count
//        double dbTotal = dbData.size();
//        System.out.println("dbTotal : " + dbTotal + " (null value data : " + destNum + ")");
//
//
//        System.out.println("\n==================\nmongoDB setting\n==================\n");
//        MongoCollection<Document> probCollection = dbService.connect(
//                config.getIp(), config.getPort(), config.getDbName(), config.getProbNoiseColl()
//        );
//
//
//        System.out.println("\n==================\nIteration Start\n==================\n");
//        /** Iteration **/
//        while (true) {
//            iterationCount++;
//            System.out.println("Iteration Count : " + iterationCount);
//            double[][] preTheta = new double[theta.length][theta[0].length];
//            for (int i = 0; i < destNum; i++) {
//                for (int j = 0; j < destNum; j++) {
//                    preTheta[i][j] = theta[i][j];
//                }
//            }
//
//            double thetaSum = 0.0;
//            for (int a = 0; a < destNum; a++) {
//                for (int b = 0; b < destNum; b++) {
//
//                    /** M Step ==  {(1<u<dbTotal) P( Xa, Xb | rlp, rlc ; theta[a][b] ) } / dbTotal **/
//                    double eStepSum = 0.0;
//                    double nullCount = 0;
//
//
//                    for (int u = 0; u < dbTotal; u++) {
//                        String[] noiseData = dbData.get(u).split(":");
//                        String rlp = noiseData[0];
//                        String rlc = noiseData[1];
//
//                        if (rlp.equals("null")) {
//                            nullCount++;
//                            continue;
//                        }
//
//
//                        int rlpIndex = Integer.parseInt(rlp, 2);
//                        int rlcIndex = Integer.parseInt(rlc, 2);
//
//
//                        /** E Step == P( Xa, Xb | rlp, rlc ; theta[a][b] ) **/
//                        // theta[a][b] * P( rlp, rlc | Xa, Xb )
//                        double eStep1 = preTheta[a][b] * probNoiseOrigin[rlpIndex][a] * probNoiseOrigin[rlcIndex][b];
//
//                        // (1<v<n, 1<w<n) theta[v][w] * P( rlp, rlc | Xv, Xw)
//                        double eStep2 = 0.0;
//                        for (int v = 0; v < destNum; v++)
//                            for (int w = 0; w < destNum; w++)
//                                eStep2 += preTheta[v][w] * probNoiseOrigin[rlpIndex][v] * probNoiseOrigin[rlcIndex][w];
//
//                        // theta[a][b] * P( rlp, rlc | Xa, Xb )
//                        // ----------------------------------------------------
//                        // (1<v<n, 1<w<n) theta[v][w] * P( rlp, rlc | Xv, Xw)
//                        double eStepResult = eStep1 / eStep2;
//                        /** E Step end **/
//
//                        // (1<u<dbTotal) P( Xa, Xb | rlp, rlc ; theta[a][b] )
//                        eStepSum += eStepResult;
//                    }
//
//
//                    double mStepResult = eStepSum / (dbTotal - nullCount);
//
//
//                    theta[a][b] = mStepResult;
//                    thetaSum += theta[a][b];
//                }
//            }
//            /** M Step end **/
//
//            /** Normalize **/
//            for (int a = 0; a < destNum; a++)
//                for (int b = 0; b < destNum; b++)
//                    theta[a][b] /= thetaSum;
//
//            /** max( | theta(t+1)[a][b] - theta(t)[a][b] | ) **/
//            double max = 0.0;
//            double compareValue = 0.0;
//            for (int a = 0; a < destNum; a++)
//                for (int b = 0; b < destNum; b++)
//                    compareValue = Math.abs(theta[a][b] - preTheta[a][b]);
//            max = Math.max(max, compareValue);
//
//
//            /** Iteration Check **/
//            System.out.println("\tcompare : max theta(" + max + ") and threshold(" + threshold + ")");
//            if (max < threshold) break;
//        }
//
//        /** P( a -> b) update **/
//        for (int a = 0; a < destNum; a++)
//            for (int b = 0; b < destNum; b++)
//                result[a][b] = theta[a][b];
//
//
//        double resultSum = 0.0;
//        System.out.println("\n\nResult");
//        for (int i = 0; i < result.length; i++) {
//            for (int j = 0; j < destNum; j++) {
//
////                System.out.println("Prob " + i + " -> " + j + " : " + result[i][j]);
//                System.out.println(result[i][j]);
//                resultSum += result[i][j];
//
//                Document document = new Document();
//                document.put("a", i);
//                document.put("b", j);
//                document.put("prob", result[i][j]);
//                dbService.insert(document, probCollection);
//            }
//        }
//        System.out.println("total : " + resultSum);
//    }
//}
