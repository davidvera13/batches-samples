package fr.dsidiff.dsipilot.csvparser.batch.watcher;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CsvFileWatcher {
    // folder à scanner
    // private static final String DIRECTORY_TO_WATCH = "D:\\DSIPILOT_V9\\TESTWRITER\\IN";
    @Value("${watch.directory}")
    private String directoryToWatch;

    @Value("${output.directory}")
    private String outputDir;

    private volatile boolean running = true;


    private final JobLauncher jobLauncher;
    private final Job csvJob;

    @EventListener(ApplicationReadyEvent.class)
    public void watchDirectory() throws
            IOException,
            InterruptedException{
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(directoryToWatch);
        path.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        System.out.println("Watching directory: " + path);

        while (true) {
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                String fileName = event.context().toString();
                if (fileName.endsWith(".csv") || fileName.endsWith(".CSV")) {
                    String fullPath = path.resolve(fileName).toString();
                    System.out.println("Detected new CSV: " + fullPath);

                    // Start stopwatch
                    StopWatch stopWatch = StopWatch.createStarted(); // Or use System.nanoTime()

                    JobParameters params = new JobParametersBuilder()
                            .addString("filePath", fullPath)
                            .addString("outputFile", outputDir + "\\" + fileName.replace(".csv", "_out.csv"))
                            .addDate("timestamp", new Date())
                            .toJobParameters();

                    try {
                        JobExecution execution = jobLauncher.run(csvJob, params);
                        while (execution.isRunning()) {
                            Thread.sleep(500); // Wait until the job finishes
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    stopWatch.stop();
                    System.out.println("✅ Completed processing " + fileName + " in " + stopWatch.getTime(TimeUnit.MILLISECONDS) + " ms");
                }
            }
            key.reset();
        }
    }

    public void stop() {
        this.running = false;
    }
}