package Java;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.*;

public class Functinos {

    public String[] noiseCases(int destNum, int total) {
        String[] result = new String[total];
        int index = 0;

        Queue<String> queue = new LinkedList<String>();
        queue.add("");
        while (!queue.isEmpty()) {
            String cur = queue.poll();

            // 다 채워짐
            if (cur.length() == destNum)
                result[index++] = cur;

                // 다 안채워짐
            else {
                queue.offer(cur + '0');
                queue.offer(cur + '1');
            }
        }

        return result;
    }


    public double[][] initTheta(int destNum) {
        double[][] result = new double[destNum][destNum];
        for (int i = 0; i < destNum; i++)
            Arrays.fill(result[i], 1.0 / Math.pow(destNum, 2));
        return result;
    }

    public Double caseProb(String location, String noise, double qValue, double pValue) {
        Random random = new Random();
        double result = 1.0;

        for (int i = 0; i < location.length(); i++) {
            double r = random.nextDouble();

            if (location.charAt(i) == '0') {
                if (noise.charAt(i) == '0') result *= (1 - qValue);
                else result *= qValue;
            } else {
                if (noise.charAt(i) == '0') result *= (1 - pValue);
                else result *= pValue;
            }
        }
        return result;
    }

    /**
     * DB data read
     **/
    public ArrayList<String> dbRead(String ip, int port, String dbName, String collectionName) {
        MongoClient mongo = new MongoClient(ip, port);
        MongoDatabase taxiDataDB = mongo.getDatabase(dbName);
        MongoCollection<Document> collection = taxiDataDB.getCollection(collectionName);

        // data object
        ArrayList<String> dbData = new ArrayList<String>();

        // select * from collection;
        FindIterable<Document> cursor = collection.find();

        // select query result
        for (Document document : cursor) {
            String rlp = (String) document.get("rlp");
            String rlc = (String) document.get("rlc");
            String newLine = rlp + ":" + rlc;
            dbData.add(newLine);
        }

        return dbData;
    }


    public double[][] probNoiseOrigin(int destNum, String[] noiseCases, double qValue, double pValue) {
        double[][] result = new double[noiseCases.length][destNum];

        for (String noise : noiseCases) {
            int x = Integer.parseInt(noise, 2);

            for (int originNum = 0; originNum < destNum; originNum++) {
                StringBuilder origin = new StringBuilder(String.format("%0" + destNum + "d", 0));
                origin.setCharAt(originNum, '1');

                int y = (int) Math.log(Integer.parseInt(origin.toString(), 2));

                result[x][y] = caseProb(origin.toString(), noise, qValue, pValue);
            }
        }

        return result;
    }

}
