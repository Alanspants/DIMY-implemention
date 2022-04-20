package BF;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.lang.reflect.Field;
import java.util.BitSet;
import java.util.function.IntPredicate;

public class containTest {

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

    public static void main(String[] args) {
        BloomFilter<String> BF1 = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 100, 0.001);
        BloomFilter<String> BF2 = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 100, 0.001);

//        BF1.put("apple");
        BF1.put("orange");
        BF1.put("blue");

        BF2.put("apple");
        BF2.put("banana");
        System.out.println(intersectCheck(BF2, BF1));
    }
}
