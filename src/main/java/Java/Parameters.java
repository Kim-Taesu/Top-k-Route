package Java;


public class Parameters {

    // values
    private double epslion = 1.0;
    private double qValue = 1.0 / (Math.exp(epslion) + 1);
    private double pValue = 0.5;

    private int destNum = 5;

    private double threshold = 0.00001;

    private String ip = "localhost";
    private int port = 27017;
    private String dbName = "taxiDB";
    private String collectionName = "taxiCollection";


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

    public String getCollectionName() {
        System.out.println("collectionName : " + collectionName);
        return collectionName;
    }

    public int getDestNum() {

        System.out.println("destNum : " + destNum);
        return destNum;
    }

    public double getqValue() {
        System.out.println("epslion : " + epslion);
        System.out.println("qValue : " + qValue);
        return qValue;
    }

    public double getpValue() {
        System.out.println("pValue : " + pValue);
        return pValue;
    }

    public double getThreshold() {

        System.out.println("threshold : " + threshold);
        return threshold;
    }

}
