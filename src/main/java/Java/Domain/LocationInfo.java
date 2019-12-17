package Java.Domain;

public class LocationInfo {

  private String taxiId;
  private String day;
  private String time;
  private String rlp;
  private String rlc;

  public LocationInfo(Object taxiId, Object day, Object time, Object rlp, Object rlc) {
    this.taxiId = (String) String.valueOf(taxiId);
    this.day = (String) day;
    this.time = (String) time;
    this.rlp = (String) rlp;
    this.rlc = (String) rlc;
  }

  public String getDay() {
    return day;
  }

  public String getRlc() {
    return rlc;
  }

  public String getRlp() {
    return rlp;
  }

  public String getTaxiId() {
    return taxiId;
  }

  public String getTime() {
    return time;
  }
}

