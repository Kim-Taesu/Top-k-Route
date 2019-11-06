package Java.DB;

public class DbMain {

    public static void main(String args[]){
        DbFunctions dbFunctions = new DbFunctions();

        dbFunctions.drop();
        dbFunctions.dataRead();
        dbFunctions.read();

    }
}
