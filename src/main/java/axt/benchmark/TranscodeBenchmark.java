package axt.benchmark;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

public class TranscodeBenchmark {

	public static byte[] objectToByteArray(Serializable o) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try(ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(baos))) {
				oos.writeObject(o);
			}
			baos.close();
			return baos.toByteArray();
		} catch(IOException ioe) {
			throw new RuntimeException("Cannot convert object to bytearray: '" + o.getClass().getName() + "'", ioe);
		} 
	}

	public Object byteArrayToObject(byte[] bs) {
		Serializable obj;
		try (ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(bs)))) {
			obj = Serializable.class.cast(ois.readObject());
		} catch (IOException | ClassNotFoundException ex) {
			throw new RuntimeException("Exception happened while deserializing from a byte array", ex);
		}
		return obj;
	}
	
	
	public static class Model implements Serializable {
		private static final long serialVersionUID = 1L;
		private int[] ids;
		public Model(int n) {
			Random rd = new Random(1);
			ids = new int[n];
			for (int i=0; i<n; i++) {
				for (int j=0; j < ids.length; j++) { 
					if (j%2==0) {
						ids[j] = rd.nextInt();
					}
				}
			}
		}
	}
	
	@State(Scope.Benchmark)
	public static class ExecutionPlan {		
		public final int N = 500;
		
		public int size = 1000;
		
		Map<String, byte[]> data = new HashMap<>();

		@Setup(Level.Trial)
		public void setUp() {
			for (int i=0; i<N; i++) {
				data.put(String.valueOf(i), objectToByteArray(new Model(N)));
			}
		}
	}
	
	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.SECONDS)
	@Warmup(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
	@Measurement(iterations = 10, time = 10, timeUnit = TimeUnit.SECONDS)
	@Timeout(time=60, timeUnit=TimeUnit.MINUTES)
	public void benchmark(final ExecutionPlan e, final Blackhole b) {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 16, 10000L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(e.N));

		for (int i=0; i < e.N; i++) {
			final Map<String, byte[]> data = e.data;
			executor.submit(new Runnable() {
				public void run() {
					for (byte[] ba : data.values()) {
						b.consume(byteArrayToObject(ba));
					}
				}
			});
		}
		
		executor.shutdown();
		try { 
			executor.awaitTermination(10, TimeUnit.SECONDS); 
		} catch (InterruptedException ex) {}
	}
}
