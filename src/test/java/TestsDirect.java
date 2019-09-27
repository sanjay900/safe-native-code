import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

public class TestsDirect {


    public static class TimingTest implements Serializable {
        long local = 0;

        void addAll() {
            for (long i = 0; i < 10000000; i++) {
                addSingle(i);
            }
        }

        void addSingle(long i) {
            local += i;
        }
        @Test
        public void timeConstruction() {
            int testCount = 20;
            long totalTime = 0L;
            for (int i = 0; i < testCount + 5; i++) {
                Instant start = Instant.now();
                new TimingTest();
                Instant end = Instant.now();
                if (i > 5) {
                    totalTime += Duration.between(start, end).toMillis();
                }
            }
            System.out.println("Time taken to construct: " + totalTime / testCount);
        }

    }

    @Test
    public void time() {
        int testCount = 10;
        long expected = 49999995000000L;
        long totalTime = 0L;
        for (int i = 0; i < testCount + 5; i++) {
            Instant start = Instant.now();
            TimingTest t = new TimingTest();
            t.addAll();
            Instant end = Instant.now();
            Assert.assertEquals(expected, t.local, 0);
            if (i > 5) {
                totalTime += Duration.between(start, end).toMillis();
            }
        }
        System.out.println("Time taken to test: " + totalTime / testCount);
    }
}
