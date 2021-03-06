package Helper;

import com.google.common.hash.BloomFilter;

import java.lang.reflect.Field;
import java.util.BitSet;
import java.util.Random;

public class Helper {

    public static boolean msgDrop() {
        Random rand = new Random();
        int value = rand.nextInt(10);
        if (value < 5) {
            return true;
        } else {
            return false;
        }
    }

    public static <T> boolean intersectCheck(BloomFilter<T> bloomFilter, BloomFilter<T> that) {
        BitSet thisBitSet = getBitSet(bloomFilter);
        BitSet thatBitSet = getBitSet(that);
        BitSet intersectionBitSet = new BitSet(thisBitSet.size());
        intersectionBitSet.or(thisBitSet);
        intersectionBitSet.and(thatBitSet);
        return intersectionBitSet.length() != 0;
    }

    private static <T> BitSet getBitSet(BloomFilter<T> bloomFilter) {
        try {
            Field bitsField = BloomFilter.class.getDeclaredField("bits");
            bitsField.setAccessible(true);
            Object bitArray = bitsField.get(bloomFilter);
            Field dataField = bitArray.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            return BitSet.valueOf((long[]) dataField.get(bitArray));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
