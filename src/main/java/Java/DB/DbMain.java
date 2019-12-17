//package Java.DB;
//
//import Java.Config.Config;
//import com.mongodb.client.MongoCollection;
//import org.bson.Document;
//
//import java.util.HashMap;
//
//public class DbMain {
//
//    public static void main(String args[]) {
//
//        Config config = Config.getInstance();
//        DbService dbService = new DbService();
//
//
//        int destNum = config.getDestNum();
//        double qValue = config.getqValue();
//        double pValue = config.getpValue();
//
//        String[] destCodeList = config.getDestCodeList();
//        String filePath = config.getFilePath();
//
//        MongoCollection<Document> taxiColl = dbService.connect(
//                config.getIp(), config.getPort(), config.getDbName(), config.getTaxiColl()
//        );
//
//        MongoCollection<Document> probCollection = dbService.connect(
//                config.getIp(), config.getPort(), config.getDbName(), config.getCollectionProbOrigin()
//        );
//
//
//        HashMap<String, Integer> destCode = dbService.initDestCode(destNum, destCodeList);
//
//        dbService.drop(taxiColl);
//        dbService.dataRead(destNum, qValue, pValue, filePath, destCode,
//                new HashMap<String, String>(), taxiColl, probCollection);
//
//        dbService.read(taxiColl);
//    }
//}
