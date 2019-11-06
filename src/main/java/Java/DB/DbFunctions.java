package Java.DB;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DbFunctions {
    Config config = new Config();
    DbSubFunction dbSubFunction = new DbSubFunction();
    MongoCollection<Document> collection;

    public DbFunctions() {
        collection = config.getCollection();
    }

    public void drop() {
        collection.drop();
    }


    public void insert(String taxiId, int day, int time, String dest, String pDest) {
//        System.out.println("insert ("+taxiId+", "+day+", "+time+", "+dest+", "+pDest+")");
        Document document = new Document();
        document.put("taxiId", taxiId);
        document.put("day", day);
        document.put("time", time);
        document.put("rlp", pDest);
        document.put("rlc", dest);
        collection.insertOne(document);
    }

    public void read() {
        FindIterable<Document> cursor = collection.find();
        for (Document document : cursor) {
            System.out.println(document);
        }
    }

    public void dataRead() {

        int destNum = config.getDestNum();
        double qValue = config.getqValue();
        double pValue = config.getpValue();

        BufferedReader br = null;
        try {
            br = Files.newBufferedReader(Paths.get(config.getFilePath()));
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
                int destIndex = config.getSigunguCode(dest);

                // 서울 지역이 아님
                if (destIndex == -1) continue;

                StringBuilder location = new StringBuilder(String.format("%0" + destNum + "d", 0));
                location.setCharAt(destIndex, '1');
//                System.out.println("origin : "+dest+" => " +location.toString());
                String noiseDest = dbSubFunction.addNoise(location.toString(), qValue, pValue);

                String pDest = config.getLocationInfo(taxiId);
                config.setLocationInfo(taxiId, noiseDest);

                insert(taxiId, day, time, noiseDest, pDest);

                if (count > 1000) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
