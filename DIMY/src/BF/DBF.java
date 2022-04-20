package BF;

import TCP.TCPObjSend;
import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.io.IOException;

public class DBF extends Thread{

    BloomFilter[] DBFs;
    int DBFsIndex;
    String DBFcache = "";

    public DBF() {
        DBFs = new BloomFilter[]{null, null, null, null, null, null};
        DBFsIndex = 0;
    }

    private void newDBF() {
//        System.out.println("****** new DBF generate ******");
//        System.out.println("****** current DBF is [DBF: " + DBFsIndex + "] ******");
        System.out.println("\n*********************************");
        System.out.println("90 seconds have passed");
        System.out.println("Generate new DBF");
        System.out.println("Current DBF: DBF[" + DBFsIndex + "]");
        System.out.println("*********************************");
        DBFcache = "";
        BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 100, 0.001);
        DBFs[DBFsIndex] = bloomFilter;
    }

    public void insert(String str) {
//        System.out.println("****** insert into [DBF: " + DBFsIndex + "] ******");
        System.out.println("Updating DBF ...");
        System.out.println("    [new encID]: " + str);
        System.out.println("    [current DBF]: DBF[" + DBFsIndex + "]");
        System.out.println("    [notice]: EncID has already been deleted");
        if (DBFs[DBFsIndex] != null) {
            DBFs[DBFsIndex].put(str);
        }
        DBFcache += str + "\n";
        System.out.println("EncID in DBF[" + DBFsIndex + "]:");
        System.out.println(DBFcache);

    }

    public BloomFilter newQBF() {
        BloomFilter<String> QBF = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 100, 0.001);
        for (int i = 0; i < 5; i++) {
            QBF.putAll(DBFs[i]);
        }
        return QBF;
    }

    public BloomFilter newCBF() {
        BloomFilter<String> CBF = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 100, 0.001);
        for (int i = 0; i <= DBFsIndex; i++) {
//            if (DBFs[i] != null) {
//                CBF.putAll(DBFs[i]);
//                System.out.println("Combine DBF[" + i + "] into CBF.");
//            }
            CBF.putAll(DBFs[i]);
            System.out.println("Combine DBF[" + i + "] into CBF.");
        }
        return CBF;
    }

    public void run() {
        while(true) {
            if (DBFsIndex < 6) {
                newDBF();
                try {
                    sleep(90000);
//                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DBFsIndex++;
            } else {
                try {
                    TCPObjSend.sendQBF(newQBF());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DBFsIndex = 0;
            }
        }
    }

}
