package BF;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

public class DBF extends Thread{

    BloomFilter[] DBFs;
    int DBFsIndex;

    public DBF() {
        DBFs = new BloomFilter[]{null, null, null, null, null, null};
        DBFsIndex = 0;
    }

    private void newDBF() {
        System.out.println("****** new DBF generate ******");
        System.out.println("****** current DBF is [DBF: " + DBFsIndex + "] ******");
        BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 1000, 0.001);
        DBFs[DBFsIndex] = bloomFilter;
    }

    public void insert(String str) {
        System.out.println("****** insert into [DBF: " + DBFsIndex + "] ******");
        if (DBFs[DBFsIndex] != null) {
            DBFs[DBFsIndex].put(str);
        }
    }

    public void newQBF() {
        BloomFilter<String> QBF = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 1000, 0.001);
        for (int i = 0; i < 5; i++) {
            QBF.putAll(DBFs[i]);
        }
        System.out.println(QBF);
    }

    public void run() {
        while(true) {
            if (DBFsIndex < 6) {
                newDBF();
                try {
                    sleep(90000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DBFsIndex++;
            } else {
                DBFsIndex = 0;
            }
        }
    }

//    public void run() {
//        while(true) {
//            if (DBFsIndex < 6) {
//                newDBF();
//                insert("hello_" + DBFsIndex);
//                try {
////                    sleep(90000);
//                    sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                DBFsIndex++;
//            } else {
//                newQBF();
//                DBFsIndex = 0;
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        DBF dbf = new DBF();
//        dbf.start();
//    }
}
