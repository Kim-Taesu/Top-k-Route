package Java.DB;

import Java.Domain.LocationInfo;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.bson.Document;

public class DbService {

  private static DbService dbService = new DbService();
  private Random random = new Random();

  private DbService() {
    System.out.println("\nCreate DbService Class");
  }

  public static DbService getInstance() {
    return dbService;
  }

  public List<Document> makeSample(int destNum, int samplingNum, double qValue, double pValue) {
    System.out.println("dest num\t" + destNum);
    System.out.println("sampling num\t" + samplingNum);
    System.out.println("q\t" + qValue);
    System.out.println("p\t" + pValue);

    List<Document> sampleData = new ArrayList<>();
    /** 각 택시별 하차 정보 저장 배열 선언 및 초기화(-1)**/
    int[] taxiDestIndex = new int[destNum];
    Arrays.fill(taxiDestIndex, -1);

    /** 각 택시별 하차 정보 저장 배열 선언 및 초기화(-1)**/
    double[][] destMoveStatus = new double[destNum][destNum];

    double[][] dataStatus = new double[16][16];

    for (int i = 0; i < samplingNum; i++) {
      /** taxi Id와 다음 하차 지역은 random으로 생성 **/
      int taxiId = random.nextInt(destNum);
      int nextDestIndex = random.nextInt(destNum);

      /** 데이터가 균형적으로 분포되지 않게 하기 위해 설정 **/
      if (i % 3 == 0) {
        nextDestIndex /= 3;
      }

      /** 이전에 하차했던 지역의 인덱스 get **/
      int prevDestIndex = taxiDestIndex[taxiId];

      /** 현재 하차할 지역의 인덱스를 set **/
      taxiDestIndex[taxiId] = nextDestIndex;

      /** 이전에 하차한 지역의 인덱스가 -1 일때 (처읍으로 하차지역 정보가 생성될 때) **/
      if (prevDestIndex == -1) {
        continue;
      }

      /** 이전의 하차한 지역의 정보가 있다면 이전 하차지역에서 다음 하차할 값을 + 1 **/
      else {
        destMoveStatus[prevDestIndex][nextDestIndex]++;
      }

      /** 해당 차량의 하차지역을 비트시퀀스로 변환 후 노이즈 추가 **/
      StringBuilder dest = new StringBuilder(String.format("%0" + destNum + "d", 0));
      dest.setCharAt(nextDestIndex, '1');
      String noiseDest = addNoise(dest.toString(), qValue, pValue);

      /** 해당 차량의 이전 하차지역을 비트시퀀스로 변환 후 노이즈 추가 **/
      StringBuilder prevDest = new StringBuilder(String.format("%0" + destNum + "d", 0));
      prevDest.setCharAt(prevDestIndex, '1');
      String noisePrevDest = addNoise(prevDest.toString(), qValue, pValue);

      int rlpIndex = Integer.parseInt(noisePrevDest, 2);
      int rlcIndex = Integer.parseInt(noiseDest, 2);
      dataStatus[rlpIndex][rlcIndex]++;

      /** DB Insert **/
      Document document = new Document();
      document.put("taxiId", taxiId);
      document.put("rlp", noisePrevDest);
      document.put("rlc", noiseDest);
      sampleData.add(document);
//            insert(document, testCollection);
    }

    /** 지역간 이동 상황 출력 **/
    double moveTotal[] = new double[destNum];
    int index = 0;
    for (double[] startDest : destMoveStatus) {
      for (double endDest : startDest) {
        moveTotal[index] += endDest;
        System.out.print(endDest + "\t");
      }
      index++;
      System.out.println();
    }

    /** Get Prob i -> j **/
    for (int i = 0; i < destNum; i++) {
      for (int j = 0; j < destNum; j++) {
        System.out.println(destMoveStatus[i][j] / moveTotal[i]);
      }
    }

    for (int a = 0; a < 16; a++) {
      for (int b = 0; b < 16; b++) {
        System.out.print(dataStatus[a][b] + "\t");
      }
      System.out.println();
    }
    return sampleData;
  }


  public String addNoise(String dest, Double qValue, Double pValue) {
    String noiseDest = "";
    for (int i = 0; i < dest.length(); i++) {
      double r = random.nextDouble();
      if (dest.charAt(i) == '0') {
        if (r < qValue) {
          noiseDest += "1";
        } else {
          noiseDest += "0";
        }
      }

      else {
        if (r > pValue) {
          noiseDest += "1";
        } else {
          noiseDest += "0";
        }
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
        } else {
          continue;
        }

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
//                insert(document, collection);

        if (count > 1000) {
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    for (int i = 0; i < destNum; i++) {
      for (int j = 0; j < destNum; j++) {
        Document document = new Document();
        document.put("a", i);
        document.put("b", j);
        document.put("prob", originProb[i][j] / originProbTotal);
        System.out.println("Prob " + i + " -> " + j + " : " + originProb[i][j] / originProbTotal);
//                insert(document, probCollection);
      }
    }
  }

  public MongoClient connectDatabase(String ip, int port) {
    System.out.println("\tip : " + ip);
    System.out.println("\tport : " + port);
    return new MongoClient(ip, port);
  }

  public MongoDatabase getDB(MongoClient mongoClient, String dbName) {
    return mongoClient.getDatabase(dbName);
  }

  public MongoCollection<Document> getCollection(MongoDatabase mongoDatabase,
      String collectionName) {
    return mongoDatabase.getCollection(collectionName);
  }

  public ArrayList<LocationInfo> readAll(MongoCollection<Document> collection) {
    ArrayList<LocationInfo> result = new ArrayList<>();
    MongoCursor<Document> data = collection.find().iterator();
    while (data.hasNext()) {
      Document document = data.next();
      result.add(new LocationInfo(document.get("taxiId"), document.get("day"), document.get("time"),
          document.get("rlp"), document.get("rlc")));
    }
    return result;
  }


}
