package em;


public class Parameters {

    // values
    private double epslion = 1.0;
    private double qValue = 1.0 / (Math.exp(epslion) + 1);
    private double pValue = 0.5;
    private int destNum = 5;
    private double threshold = 0.0;


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
}
