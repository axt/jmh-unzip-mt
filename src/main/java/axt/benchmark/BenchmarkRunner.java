package axt.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkRunner {
	public static void main(String[] args) {
		String javaHome = System.getenv("JAVA_HOME");
		String javaOpts = System.getenv("JAVA_OPTS");
		String javaGC = System.getenv("JAVA_GC");
		
		ChainedOptionsBuilder optionsBuilder = new OptionsBuilder()
				.include(".*")
				.jvmArgs("-Xms1024m", "-Xmx1024m")
				.forks(1);
		
		if (javaHome != null)
			optionsBuilder = optionsBuilder.jvm(javaHome+"/bin/java");

		if (javaOpts != null)
			optionsBuilder = optionsBuilder.jvm(javaOpts);

		if (javaGC != null) {
			switch (javaGC) {
				case "CMS":
					optionsBuilder = optionsBuilder.jvmArgsAppend("-XX:+UseConcMarkSweepGC", "-XX:+CMSConcurrentMTEnabled");
					break;
				case "G1":
					optionsBuilder = optionsBuilder.jvmArgsAppend("-XX:+UseG1GC");
					break;
				case "Z":
					optionsBuilder = optionsBuilder.jvmArgsAppend("-XX:+UnlockExperimentalVMOptions", "-XX:+UseZGC");
					break;
				case "Shenandoah":
					optionsBuilder = optionsBuilder.jvmArgsAppend("-XX:+UnlockExperimentalVMOptions", "-XX:+UseShenandoahGC");
					break;
				default:
					System.err.println("Unknown GC specified, possible choices: CMS, G1, Z, Shenandoah");
					System.exit(1);
					break;
			}
		}
		try {
			new Runner(optionsBuilder.build()).run();
		} catch (RunnerException e) {
			e.printStackTrace();
		}		
	}
}

