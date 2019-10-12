import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

public class TestsDirect {
    @Test
    public void timeExecution() {
        int testCount = 10;
        long expected = 49999995000000L;
        long totalTime = 0L;
        for (int i = 0; i < testCount + 5; i++) {
            Instant start = Instant.now();
            Tests.TimingTest t = new Tests.TimingTest();
            t.addAll();
            Instant end = Instant.now();
            Assert.assertEquals(expected, t.local, 0);
            if (i > 5) {
                totalTime += Duration.between(start, end).toNanos();
            }
        }
        System.out.println("Time taken to test: " + totalTime/1000000f / testCount);
    }

    @Test
    public void timeThreadConstruction() throws InterruptedException {
        int testCount = 20;
        long totalTime = 0L;
        for (int i = 0; i < testCount + 5; i++) {
            Instant start = Instant.now();
            Thread t = new Thread(() -> {});
            t.start();
            Instant end = Instant.now();
            if (i > 5) {
                totalTime += Duration.between(start, end).toNanos();
            }
        }
        System.out.println("Time taken to construct: " + totalTime/1000000f / testCount);
    }

    @Test
    public void timeThreadExecution() throws InterruptedException {
        Thread th = new Thread(() -> {
            int testCount = 10;
            long expected = 49999995000000L;
            long totalTime = 0L;
            for (int i = 0; i < testCount + 5; i++) {
                Instant start = Instant.now();
                Tests.TimingTest t = new Tests.TimingTest();
                t.addAll();
                Instant end = Instant.now();
                Assert.assertEquals(expected, t.local, 0);
                if (i > 5) {
                    totalTime += Duration.between(start, end).toNanos();
                }
            }
            System.out.println("Time taken to test: " + totalTime/1000000f / testCount);
        });
        th.start();
        th.join();
    }
}
