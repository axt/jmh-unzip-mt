package axt.benchmark;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Thread)
public class TranscodeBenchmark {
    byte[] src;

    @Setup
    public void setUp() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gos = new GZIPOutputStream(baos);
            ObjectOutputStream oos = new ObjectOutputStream(gos)) {
            oos.writeObject(new byte[65536]);
        }
        baos.close();
        src = baos.toByteArray();
    }

    private Object test() throws Exception {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(src);
            GZIPInputStream gis = new GZIPInputStream(bais);
            ObjectInputStream ois = new ObjectInputStream(gis)) {
            return ois.readObject();
        }
    }
    
    @Benchmark
    @Threads(1)
    public Object testSingleThread() throws Exception {
    	return test();
    }

    @Benchmark
    @Threads(Threads.MAX)
    public Object testAllThreads() throws Exception {
    	return test();
    }
}