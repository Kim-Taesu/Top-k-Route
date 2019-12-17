package Java.Taxi;

import Java.Domain.LocationInfo;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class TaxiService {

  private int destNum;
  private double qValue;
  private double pValue;
  private double threshold;
  private double dataTotal;
  private BigInteger noiseCase;

  public TaxiService(int destNum, double qValue, double pValue, double threshold,
      double dataTotal) {

    this.destNum = destNum;
    this.qValue = qValue;
    this.pValue = pValue;
    this.threshold = threshold;
    this.dataTotal = dataTotal;

    System.out.println("\nCreate TaxiService Class");
    System.out.println("\tq : " + qValue);
    System.out.println("\tp : " + pValue);
    System.out.println("\tthreshold : " + threshold);
    System.out.println("\tdest num : " + destNum);
    System.out.println("\tdata num : " + dataTotal);
  }

  public String[] computeNoiseCases() {
    System.out.println("\ncompute Noise Cases");
    BigInteger last = new BigInteger(String.valueOf(1 << destNum));
    String[] result = new String[last.intValue()];
    noiseCase = BigInteger.ZERO;
    for (; noiseCase.compareTo(last) != 1 & noiseCase.compareTo(last) != 0;
        noiseCase = noiseCase.add(BigInteger.ONE)) {
      String a = noiseCase.toString(2);
      String tmp = "";
      if (a.length() < destNum) {
        for (int i = a.length(); i < destNum; i++) {
          tmp += "0";
        }
      }
      result[noiseCase.intValue()] = tmp + a;
    }

    /** print **/
    System.out.print('\t');
    for (String r : result) {
      System.out.print(String.format("%5s,", r));
    }
    System.out.println();
    return result;
  }


  public double[][] initTheta() {
    System.out.println("\ninitTheta");
    double[][] result = new double[destNum][destNum];
    for (int i = 0; i < destNum; i++) {
      Arrays.fill(result[i], 1.0 / (destNum * destNum));
    }
    for (double[] re : result) {
      for (double r : re) {
        System.out.print(String.format("%10f", r));
      }
      System.out.println();
    }
    return result;
  }


  /**
   * DB data read
   *
   * @return
   */


  public double[][] computeProbNoiseCasesFromOrigin(String[] noiseCases) {
    System.out.println("\ncompute Prob NoiseCases From Origin");
    double[][] probNoiseOrigin = new double[noiseCases.length][destNum];
    for (String noiseDest : noiseCases) {
      int noiseDestIndex = Integer.parseInt(noiseDest, 2);
      for (int originIndex = 0; originIndex < destNum; originIndex++) {
        StringBuilder originDest = new StringBuilder(String.format("%0" + destNum + "d", 0));
        originDest.setCharAt(originIndex, '1');

        // noise Dest 가 origin Dest 에서 왔을 확률 계산
        probNoiseOrigin[noiseDestIndex][originIndex] = computeProbNoiseFromOrigin(
            noiseDest, originDest.toString());
      }
    }

    /** print prob**/
    int index = 0;
    for (double[] re : probNoiseOrigin) {
      System.out.print("\t(noise_" + noiseCases[index++] + ") : ");
      for (double r : re) {
        System.out.print("\t" + String.format("%f", r));
      }
      System.out.println();
    }

    return probNoiseOrigin;
  }

  public Double computeProbNoiseFromOrigin(String noise, String origin) {
    double result = 1.0;
    // P ( NOISE | ORIGIN )
    for (int i = 0; i < origin.length(); i++) {
      // origin(i) == 0
      if (origin.charAt(i) == '0') {
        // origin(0) -> noise(0)
        if (noise.charAt(i) == '0') {
          result *= (1 - qValue);
        }
        // origin(0) -> noise(1)
        else {
          result *= qValue;
        }
      }
      // origin(i) == 1
      else {
        // origin(1) -> noise(0)
        if (noise.charAt(i) == '0') {
          result *= (1 - pValue);
        }
        // origin(1) -> noise(1)
        else {
          result *= pValue;
        }
      }
    }

//    System.out.println(origin + " -> " + noise + " : " + result);
    return result;
  }

  public double[][][] computeEStepData(ArrayList<LocationInfo> data,
      double[][] probNoiseFromOrigin) {

    double[][][] eStepData = new double[(int) dataTotal][destNum][destNum];
    double[][] dataStatus = new double[noiseCase.intValue()][noiseCase.intValue()];

    // 모든 DB 데이터에 대해
    for (int u = 0; u < dataTotal; u++) {

      // data get
      LocationInfo doc = data.get(u);

      // 이전 위치 정보가 없으면 continue
      if (doc.getRlp().equals("null")) {
        continue;
      }

      // 이전 위치(rlp)와 다음 위치(rlc)의 index 계산
      int rlpIndex = Integer.parseInt(doc.getRlp(), 2);
      int rlcIndex = Integer.parseInt(doc.getRlc(), 2);

      dataStatus[rlpIndex][rlcIndex]++;

//      System.out.println(rlpIndex + " : " + rlcIndex);

      // 모든 위치에서 rlp, rlc 로 될 확률을 구하고 곱한다.
      // 이동 : rlp -> rlp
      // prob( a -> rlp ) : 어느 특정 위치(a)가 노이즈 추가 후 rlp 가 될 확률
      // prob( b -> rlc ) : 어느 특정 위치(b)가 노이즈 추가 후 rlc 가 될 확률
      for (int a = 0; a < destNum; a++) {
        for (int b = 0; b < destNum; b++) {
          // 이동 확률 : prob(a->rlp) * prob(b->rlc)
          // P ( rh*lp , rh*lc  |  Lp=xa, Lc=xb )
          eStepData[u][a][b] = probNoiseFromOrigin[rlpIndex][a] * probNoiseFromOrigin[rlcIndex][b];
//          System.out.println("\t" + a + ", " + b + " = " + result[u][a][b]);
        }
      }
    }


    for (int a = 0; a < noiseCase.intValue(); a++) {
      for (int b = 0; b < noiseCase.intValue(); b++) {
        System.out.print(dataStatus[a][b]+"\t");
      }
      System.out.println();
    }
    return eStepData;
  }

  public double[][] computeEMAlgorithm(double[][] theta, double[][][] eStepData) {


    while (true) {
      /** theta copy **/
      double[][] prevTheta = new double[theta.length][theta[0].length];
      for (int a = 0; a < destNum; a++) {
        for (int b = 0; b < destNum; b++) {
          prevTheta[a][b] = theta[a][b];
          theta[a][b] = 0.0;
        }
      }

      /** E-Step 1 **/
      double[] eStepSum = new double[(int) dataTotal];
      for (int u = 0; u < dataTotal; u++) {
        for (int a = 0; a < destNum; a++) {
          for (int b = 0; b < destNum; b++) {
            eStepSum[u] += (prevTheta[a][b] * eStepData[u][a][b]);
          }
        }
      }

      /** E-Step 2 **/
      double[][][] eStepResult = new double[destNum][destNum][(int) dataTotal];
      for (int a = 0; a < destNum; a++) {
        for (int b = 0; b < destNum; b++) {
          for (int u = 0; u < dataTotal; u++) {
            eStepResult[a][b][u] =
                (prevTheta[a][b] * eStepData[u][a][b]) / eStepSum[u];
          }
        }
      }

      /** M-Step **/
      for (int a = 0; a < destNum; a++) {
        for (int b = 0; b < destNum; b++) {
          for (int u = 0; u < dataTotal; u++) {
            theta[a][b] += eStepResult[a][b][u];
          }
          theta[a][b] /= dataTotal;
        }
      }

      /** get maxValue **/
      double maxValue = getMaxValue(prevTheta, theta);

      /** Check Max Value**/
      System.out
          .println("\tmax_theta(" + maxValue + "), threshold(" + threshold + ")");
      if (maxValue < threshold) {
        break;
      }
    }
    return theta;
  }

  private double getMaxValue(double[][] prevTheta, double[][] theta) {
    double maxValue = Double.MIN_VALUE;
    for (int a = 0; a < destNum; a++) {
      for (int b = 0; b < destNum; b++) {
        maxValue = Math.max(maxValue, Math.abs(theta[a][b] - prevTheta[a][b]));
      }
    }
    return maxValue;
  }

  public void normalize(double[][] theta, double[] thetaSum) {
    for (int a = 0; a < destNum; a++) {
      for (int b = 0; b < destNum; b++) {
        theta[a][b] /= thetaSum[a];
      }
    }
  }

  public void printTheta(double[][] theta) {
    System.out.println("\nTheta Result");
    double result = 0.0;
    for (int i = 0; i < theta.length; i++) {
      for (int j = 0; j < destNum; j++) {
        System.out.println(theta[i][j]);
        result += theta[i][j];
      }
    }
    System.out.println("theta total " + result);
  }

  public void getProb(double[][] theta) {
    for (int i = 0; i < theta.length; i++) {
      double thetaSum = 0.0;
      for (int j = 0; j < destNum; j++) {
        thetaSum += theta[i][j];
      }
      for (int j = 0; j < destNum; j++) {
        theta[i][j] /= thetaSum;
      }
    }

    printTheta(theta);
  }
}
