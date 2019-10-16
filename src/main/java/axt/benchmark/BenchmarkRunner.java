package axt.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkRunner {
	public static void main(String[] args) {
		Options opt = new OptionsBuilder()
		        .include(".*")
		        .jvm("/fast/shenandoah/build/linux-x86_64-server-fastdebug/images/jdk/bin/java")
		        .jvmArgs("-Xms1024m", "-Xmx1024m")
		        //.jvmArgsAppend("-XX:+UseConcMarkSweepGC", "-XX:+CMSConcurrentMTEnabled")
		        //.jvmArgsAppend("-XX:+UseG1GC")
		        //.jvmArgsAppend("-XX:+UnlockExperimentalVMOptions", "-XX:+UseZGC")
		        .jvmArgsAppend("-XX:+UnlockExperimentalVMOptions", "-XX:+UseShenandoahGC")

		        //.addProfiler(XProfiler.class)
		        //.addProfiler(LinuxPerfAsmProfiler.class)
		        //.addProfiler(LinuxPerfProfiler.class)
		        //.addProfiler(LinuxPerfNormProfiler.class)
		        //.addProfiler(SafepointsProfiler.class)
		        //.addProfiler(HotspotClassloadingProfiler.class)
		        //.addProfiler(HotspotCompilationProfiler.class)
		        //.addProfiler(HotspotMemoryProfiler.class)
		        //.addProfiler(HotspotRuntimeProfiler.class)
		        //.addProfiler(HotspotThreadProfiler.class)
		        //.addProfiler(ClassloaderProfiler.class)
		        //.addProfiler(CompilerProfiler.class)
		        //.addProfiler(GCProfiler.class)
		        //.addProfiler(PausesProfiler.class)
		        //.addProfiler(StackProfiler.class)
		        //.addProfiler(JFRProfiler.class)
		        
		        //.jvmArgs("-Xmx256m", "-Xms256m", "-XX:+UnlockCommercialFeatures",
		        //        "-Djmh.stack.profiles=" + destinationFolder,
		        //        "-Djmh.executor=FJP",
		        //        "-Djmh.fr.options=defaultrecording=true,settings=" + profile)
		        //.result("/tmp/benchmarkResults.csv")
		        //.resultFormat(ResultFormatType.CSV)
		        .forks(1)
		        .build();
		 try {
			new Runner(opt).run();
		} catch (RunnerException e) {
			e.printStackTrace();
		}		
	}
}

