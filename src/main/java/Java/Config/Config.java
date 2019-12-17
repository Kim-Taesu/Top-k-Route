package Java.Config;

public class Config {

  private static Config config = new Config();

  private Config() {
    /** init variables **/
    ip = "localhost";
    port = 27017;
    dbName = "Taxi_DB";
    taxiColl = "Noise_Taxi_Data";
    testColl = "Test_Noise_Taxi_Data";
    noiseDataProbColl = "Noise_Taxi_Data_Prob";
    collectionProbOrigin = "provOrigin";

    filePath = "C:\\Users\\Kim-Taesu\\Downloads\\TaxiMach_Link_Dataset_Full_201512\\TaxiMach_Link_Dataset_Full_201501.txt";
//        filePath = "C:\\Users\\Kim-Taesu\\Desktop\\taxiData.csv";
    destNum = 4;
    epsilon = 1;
    qValue = 1.0 / (Math.exp(epsilon) + 1);
    pValue = 0.5;
    destCodeList = new String[]{
        "1111", "1114", "1117", "1120", "1121",
        "1123", "1126", "1129", "1130", "1132",
        "1135", "1138", "1141", "1144", "1147",
        "1150", "1153", "1154", "1156", "1159",
        "1162", "1165", "1168", "1171", "1174"
    };
    samplingNum = 100000;
    threshold = 0.000001;
  }

  /**
   * get instance
   **/

  public static Config getInstance() {
    return config;
  }


  /**
   * Variables
   **/
  private double threshold;
  private String ip;
  private int port;
  private String dbName;
  private String taxiColl;
  private String testColl;
  private String noiseDataProbColl;
  private String collectionProbOrigin;
  private String filePath;
  private int destNum;
  private double epsilon;
  private double qValue;
  private double pValue;
  private String[] destCodeList;
  private int samplingNum;


  /**
   * Getter
   **/

  public double getEpsilon() {
    return epsilon;
  }

  public int getSamplingNum() {
    return samplingNum;
  }

  public String getTestColl() {
    return testColl;
  }

  public String getNoiseDataProbColl() {
    return noiseDataProbColl;
  }

  public double getpValue() {
    return pValue;
  }

  public double getqValue() {
    return qValue;
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

  public int getDestNum() {
    return destNum;
  }

  public double getThreshold() {
    return threshold;
  }
}
