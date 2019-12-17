package Java.DB;

import Java.Config.Config;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.List;
import org.bson.Document;

public class DbTest {

  public static void main(String args[]) {

    /** 필요 싱글톤 객체 참조 **/
    Config config = Config.getInstance();

    DbService dbService = DbService.getInstance();

    /** DB connect **/
    MongoClient mongoClient = dbService.connectDatabase(config.getIp(), config.getPort());

    /** Database DB get **/
    MongoDatabase testDatabase = dbService.getDB(mongoClient, config.getDbName());

    /** DB collection get **/
    MongoCollection<Document> testCollection = dbService.getCollection(
        testDatabase, config.getTestColl());
    testCollection.drop();

    /** sample 데이터 생성 **/
    List<Document> sampleData = dbService.makeSample(
        config.getDestNum(), config.getSamplingNum(), config.getqValue(), config.getpValue());

    /** 데이터 저장 **/
    testCollection.insertMany(sampleData);
  }
}
