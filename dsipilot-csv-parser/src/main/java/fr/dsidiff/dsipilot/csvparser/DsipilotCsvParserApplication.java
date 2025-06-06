package fr.dsidiff.dsipilot.csvparser;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableBatchProcessing
@SpringBootApplication
public class DsipilotCsvParserApplication {

    public static void main(String[] args) {
        SpringApplication.run(DsipilotCsvParserApplication.class, args);
    }

}
