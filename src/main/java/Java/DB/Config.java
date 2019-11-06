package Java.DB;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.HashMap;

public class Config {

    private String ip = "localhost";
    private int port = 27017;
    private String dbName = "taxiDB";
    private String collectionName = "taxiCollection";

    private String filePath = "C:\\Users\\Kim-Taesu\\Desktop\\taxiData.csv";

    private MongoClient mongo;
    private MongoDatabase taxiDataDB;
    private MongoCollection<Document> collection;

    private HashMap<String, String> locationInfo;


    private HashMap<String, Integer> sigunguCode;
    private int destNum;


    private double epslion = 1.0;
    private double qValue = 1.0 / (Math.exp(epslion) + 1);
    private double pValue = 0.5;


    public Config() {
        /** mongoDB setting **/
        mongo = new MongoClient(ip, port);
        taxiDataDB = mongo.getDatabase(dbName);
        collection = taxiDataDB.getCollection(collectionName);

        locationInfo = new HashMap<>();

        sigunguCode = new HashMap<>();
        sigunguCode.put("1111", 0);
        sigunguCode.put("1114", 1);
        sigunguCode.put("1117", 2);
        sigunguCode.put("1120", 3);
        sigunguCode.put("1121", 4);
//        sigunguCode.put("1123", 5);
//        sigunguCode.put("1126", 6);
//        sigunguCode.put("1129", 7);
//        sigunguCode.put("1130", 8);
//        sigunguCode.put("1132", 9);
//        sigunguCode.put("1135", 10);
//        sigunguCode.put("1138", 11);
//        sigunguCode.put("1141", 12);
//        sigunguCode.put("1144", 13);
//        sigunguCode.put("1147", 14);
//        sigunguCode.put("1150", 15);
//        sigunguCode.put("1153", 16);
//        sigunguCode.put("1154", 17);
//        sigunguCode.put("1156", 18);
//        sigunguCode.put("1159", 19);
//        sigunguCode.put("1162", 20);
//        sigunguCode.put("1165", 21);
//        sigunguCode.put("1168", 22);
//        sigunguCode.put("1171", 23);
//        sigunguCode.put("1174", 24);

        destNum = sigunguCode.size();
    }

    public double getpValue() {
        return pValue;
    }

    public double getqValue() {
        return qValue;
    }

    public int getDestNum() {
        return destNum;
    }

    public Integer getSigunguCode(String dest) {
        if(sigunguCode.containsKey(dest)) return sigunguCode.get(dest);
        else return -1;
    }

    public void setLocationInfo(String key, String value) {
        locationInfo.put(key, value);
    }

    public String getLocationInfo(String key) {
        if (locationInfo.containsKey(key)) return locationInfo.get(key);
        else return "null";
    }

    public String getFilePath() {
        return filePath;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getDbName() {
        return dbName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }
}
