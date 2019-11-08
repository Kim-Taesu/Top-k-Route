package Java.DB;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Random;

public class DbFunctions {

    public HashMap<String, Integer> initSigunguCode(int limit, String[] sigunguList) {
        HashMap<String, Integer> tmp = new HashMap<>();
        for (int i = 0; i < limit; i++) {
            tmp.put(sigunguList[i], i);
        }

        return tmp;
    }


    public void drop(MongoCollection<Document> collection) {
        collection.drop();
    }

    public String addNoise(String dest, Double qValue, Double pValue) {

        Random random = new Random();

        String noiseDest = "";

        for (int i = 0; i < dest.length(); i++) {
            double r = random.nextDouble();
            if (dest.charAt(i) == 0) {
                if (r < qValue) noiseDest += "1";
                else noiseDest += "0";
            } else {
                if (r < pValue) noiseDest += "1";
                else noiseDest += "0";
            }
        }

        return noiseDest;
    }


    public void read(MongoCollection<Document> collection) {
        FindIterable<Document> cursor = collection.find();
        for (Document document : cursor) {
            System.out.println(document);
        }
    }


    public void dataRead(int destNum, double qValue, double pValue, String filePath,
                         HashMap<String, Integer> sigunguCode,
                         HashMap<String, String> locationInfo, MongoCollection<Document> collection,
                         MongoCollection<Document> probCollection) {
        HashMap<String, Integer> noiseLocationInfo = new HashMap<>();
        double originProb[][] = new double[destNum][destNum];
        double originProbTotal = 0.0;

        BufferedReader br = null;
        try {
            br = Files.newBufferedReader(Paths.get(filePath));
            Charset.forName("UTF-8");
            String[] header = br.readLine().split(",");

            String line = "";

            int count = 0;
            while ((line = br.readLine()) != null) {
                count++;
                String[] lineTmp = line.split(",");
                String taxiId = lineTmp[0];
                Integer day = Integer.parseInt(lineTmp[1]);
                Integer time = Integer.parseInt(lineTmp[2]);
                String dest = lineTmp[3];


                // 특정 위치
                int destIndex;
                if (sigunguCode.containsKey(dest)) {
                    destIndex = sigunguCode.get(dest);
                } else continue;


                StringBuilder location = new StringBuilder(String.format("%0" + destNum + "d", 0));
                location.setCharAt(destIndex, '1');
                String noiseDest = addNoise(location.toString(), qValue, pValue);

                String pDest;

                // 이전 위치가 있을 때
                if (locationInfo.containsKey(taxiId)) {
                    pDest = locationInfo.get(taxiId);
                    originProb[noiseLocationInfo.get(taxiId)][destIndex]++;
                    originProbTotal++;
                }

                // 이전 위치가 없을 때
                else {
                    pDest = "null";
                }
                noiseLocationInfo.put(taxiId, destIndex);
                locationInfo.put(taxiId, noiseDest);


                // Insert
                Document document = new Document();
                document.put("taxiId", taxiId);
                document.put("day", day);
                document.put("time", time);
                document.put("rlp", pDest);
                document.put("rlc", noiseDest);
                insert(document, collection);

                if (count > 1000) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        for(int i=0;i<destNum;i++){
            for (int j=0;j<destNum;j++){
                Document document = new Document();
                document.put("a",i);
                document.put("b",j);
                document.put("prob",originProb[i][j]/originProbTotal);
                System.out.println("Prob " + i + " -> " + j + " : " + originProb[i][j]/originProbTotal);
                insert(document,probCollection);
            }
        }
    }

    public void insert(Document document, MongoCollection<Document> collection) {
        collection.insertOne(document);
    }

    public MongoCollection<Document> connect(String ip, int port, String dbName, String collectionName) {
        /** mongoDB setting **/
        MongoClient mongo = new MongoClient(ip, port);
        MongoDatabase taxiDataDB = mongo.getDatabase(dbName);
        MongoCollection<Document> collection = taxiDataDB.getCollection(collectionName);
        collection.drop();
        return collection;
    }
}
