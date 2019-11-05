package Java;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class SubFunction {


    public ArrayList<String> totalCase(int destNum) {
        ArrayList<String> result = new ArrayList<String>();

        Queue<String> queue = new LinkedList<String>();
        queue.add("");

        while (!queue.isEmpty()) {
            String cur = queue.poll();

            // 다 채워짐
            if (cur.length() == destNum) {
                result.add(cur);
            }

            // 다 안채워짐
            else {
                queue.offer(cur + '0');
                queue.offer(cur + '1');
            }
        }

        return result;
    }


    public Double caseProb(String location, String noise, double qValue, double pValue) {
        Random random = new Random();
        double result = 1.0;

        for (int i = 0; i < location.length(); i++) {
            double r = random.nextDouble();

            if (location.charAt(i) == '0') {
                if (noise.charAt(i) == '0') result *= (1 - qValue);
                else result *= qValue;
            } else {
                if (noise.charAt(i) == '0') result *= (1 - pValue);
                else result *= pValue;
            }
        }
        return result;
    }
}

