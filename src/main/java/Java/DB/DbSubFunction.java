package Java.DB;

import java.util.Random;

public class DbSubFunction {
    public String addNoise(String dest, Double qValue, Double pValue) {

        Random random = new Random();

        String noiseDest = "";

        for (int i = 0; i < dest.length(); i++) {
            double r = random.nextDouble();
            if (dest.charAt(i) == 0) {
                if (r < qValue) noiseDest += "1";
                else noiseDest += "0";
            }
            else {
                if (r < pValue) noiseDest += "1";
                else noiseDest += "0";
            }
        }

        return noiseDest;
    }
}
