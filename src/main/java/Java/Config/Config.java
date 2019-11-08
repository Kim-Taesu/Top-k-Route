package Java.Config;

public class Config {

    private double threshold = 0.001;
    private String ip = "localhost";
    private int port = 27017;
    private String dbName = "taxiDB";
    private String collectionTaxi = "taxiCollection";
    private String collectionProbNoise= "provNoise";
    private String collectionProbOrigin= "provOrigin";


    //    private String filePath = "C:\\Users\\Kim-Taesu\\Downloads\\TaxiMach_Link_Dataset_Full_201512\\TaxiMach_Link_Dataset_Full_201501.txt";
    private String filePath = "C:\\Users\\Kim-Taesu\\Desktop\\taxiData.csv";
    private int destNum = 6;
    private double epslion = 1.0;
    private double qValue = 1.0 / (Math.exp(epslion) + 1);
    private double pValue = 0.5;
    private String[] sigunguList = {"1111", "1114", "1117", "1120", "1121", "1123", "1126", "1129", "1130", "1132", "1135", "1138", "1141", "1144", "1147", "1150", "1153", "1154", "1156", "1159", "1162", "1165", "1168", "1171", "1174"};


    public String getCollectionProbNoise() {
        return collectionProbNoise;
    }

    public String getCollectionProbOrigin() {
        return collectionProbOrigin;
    }

    public String[] getSigunguList() {
        System.out.print("sigunguList : ");
        for(String sigungu : sigunguList) System.out.printf("%s ",sigungu);
        System.out.println();
        return sigunguList;
    }

    public double getpValue() {
        System.out.println("pValue : " + pValue);
        return pValue;
    }

    public double getqValue() {
        System.out.println("qValue : " + qValue);
        return qValue;
    }

    public String getFilePath() {
        System.out.println("filePath : " + filePath);
        return filePath;
    }

    // getter
    public String getIp() {
        System.out.println("ip : " + ip);
        return ip;
    }

    public int getPort() {
        System.out.println("port : " + port);
        return port;
    }

    public String getDbName() {
        System.out.println("dbName : " + dbName);
        return dbName;
    }

    public String getCollectionTaxi() {
        return collectionTaxi;
    }

    public int getDestNum() {
        System.out.println("destNum : " + destNum);
        return destNum;
    }


    public double getThreshold() {
        System.out.println("threshold : " + threshold);
        return threshold;
    }
}
