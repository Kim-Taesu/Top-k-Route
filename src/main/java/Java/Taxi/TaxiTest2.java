package Java.Taxi;

import Java.Config.Config;
import Java.DB.DbService;
import Java.Domain.LocationInfo;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import org.bson.Document;

public class TaxiTest2 {

  public static void main(String argv[]) {
    /** Config 인스턴스 생성 **/

    // Get Config Class instance
    Config config = Config.getInstance();

    //------------------------------------------------------------------------------------------------------------//

    /** DB Service **/
    DbService dbService = DbService.getInstance();

    // Get mongoDB client
    MongoClient mongoClient = dbService.connectDatabase(config.getIp(), config.getPort());

    // Get Database
    MongoDatabase database = dbService.getDB(mongoClient, config.getDbName());

    // Get Collection
    MongoCollection<Document> collection = dbService
        .getCollection(database, config.getTestColl());

    // Get Data from Database
    ArrayList<LocationInfo> data = dbService.readAll(collection);
    double dataTotal = data.size();

    //------------------------------------------------------------------------------------------------------------//

    /** Taxi Service **/
    TaxiService taxiService = new TaxiService(config.getDestNum(), config.getqValue(),
        config.getpValue(), config.getThreshold(), dataTotal);

    // 지역 수의 모든 noise dest 경우의수 계산
    String[] noiseCases = taxiService.computeNoiseCases();

    // noise dest가 원래 dest에서(1~destNum) 왔을 경우의 수 계산
    double[][] probNoiseFromOrigin = taxiService.computeProbNoiseCasesFromOrigin(noiseCases);

    // Initialize Theta
    double[][] theta = taxiService.initTheta();

    // Compute E Step Init Data
    double[][][] eStepData = taxiService.computeEStepData(data, probNoiseFromOrigin);

    // EM Algorithm
    double[][] emResult = taxiService.computeEMAlgorithm(theta, eStepData);

    //------------------------------------------------------------------------------------------------------------//

    /** Compute Prob **/
    taxiService.getProb(emResult);


  }
}
