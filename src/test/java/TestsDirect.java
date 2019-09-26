import compiler.JavaCompiler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import slave.RemoteObject;
import slave.exceptions.SlaveException;
import slave.exceptions.UnknownObjectException;
import slave.types.DockerSlave;
import slave.types.ProcessSlave;
import slave.types.SlaveType;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
