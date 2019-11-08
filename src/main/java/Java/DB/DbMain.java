package Java.DB;

import Java.Config.Config;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.HashMap;

public class DbMain {

    public static void main(String args[]) {

        Config config = new Config();
        DbFunctions dbFunctions = new DbFunctions();


        int destNum = config.getDestNum();
        double qValue = config.getqValue();
        double pValue = config.getpValue();
        String[] sigunguList = config.getSigunguList();
        String filePath = config.getFilePath();

        MongoCollection<Document> collection = dbFunctions.connect(config.getIp(), config.getPort(), config.getDbName(), config.getCollectionTaxi());
        MongoCollection<Document> probCollection = dbFunctions.connect(config.getIp(), config.getPort(), config.getDbName(), config.getCollectionProbOrigin());
        HashMap<String, Integer> sigunguCode = dbFunctions.initSigunguCode(destNum, sigunguList);

        dbFunctions.drop(collection);
        dbFunctions.dataRead(destNum, qValue, pValue, filePath, sigunguCode, new HashMap<String, String>(), collection,probCollection);

        dbFunctions.read(collection);
    }
}
