package Java;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.HashMap;

public class Parameters {

    // values
    private double epslion = 1.0;
    private double qValue = 1.0 / (Math.exp(epslion) + 1);
    private double pValue = 0.5;

    private int destNum = 5;

    private double threshold = 1.0;
    private HashMap<Integer, HashMap<Integer, Double>> initTheta;
    private double totalThetaTmp = 0.0;

    private String ip = "localhost";
    private int port = 27017;
    private String dbName = "taxiDB";
    private String collectionName = "taxiCollection";
    private MongoClient mongo;
    private MongoDatabase taxiDataDB;
    private MongoCollection<Document> collection;


    public Parameters() {
        /** mongoDB setting **/
        mongo = new MongoClient(ip, port);
        taxiDataDB = mongo.getDatabase(dbName);
        collection = taxiDataDB.getCollection(collectionName);
        System.out.println("=================================");
        System.out.println("connect mongodb");
        System.out.println("=================================");
        System.out.println("ip : "+ip);
        System.out.println("port : "+port);
        System.out.println("db : "+dbName);
        System.out.println("collection : "+collectionName);
        System.out.println("=================================\n");


        System.out.println("=================================");
        // init theta
        initTheta = new HashMap<Integer, HashMap<Integer, Double>>();
        for (int i = 0; i < destNum; i++) {
            HashMap<Integer, Double> tmp = new HashMap<Integer, Double>();
            for (int j = 0; j < destNum; j++) {
                tmp.put(j, 1 / Math.pow(destNum, 2));
            }
            initTheta.put(i, tmp);
        }
        System.out.println("theta init");
        System.out.println("=================================");
        for(int i : initTheta.keySet()){
            System.out.println(i);
            System.out.println(initTheta.get(i)+"\n");
        }
        System.out.println("=================================\n");



        System.out.println("=================================");
        // init totalThetaTmp
        for (int q = 0; q < destNum; q++) {
            for (int w = 0; w < destNum; w++) {
                totalThetaTmp += 1 / Math.pow(destNum, 2);
            }
        }
        totalThetaTmp -= 1 / Math.pow(destNum, 2);
        System.out.println("=================================");
        System.out.println("total theta tmp init : " +totalThetaTmp);
        System.out.println("=================================\n");

    }

    // getter
    public int getDestNum() {
        return destNum;
    }

    public double getqValue() {
        return qValue;
    }

    public double getpValue() {
        return pValue;
    }

    public double getThreshold() {
        return threshold;
    }

    public HashMap<Integer, HashMap<Integer, Double>> getInitTheta() {
        return (HashMap<Integer, HashMap<Integer, Double>>) initTheta.clone();
    }

    public double getTotalThetaTmp() {
        return totalThetaTmp;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }
}
