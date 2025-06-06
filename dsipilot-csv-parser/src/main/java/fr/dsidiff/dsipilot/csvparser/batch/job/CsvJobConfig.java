package fr.dsidiff.dsipilot.csvparser.batch.job;

import fr.dsidiff.dsipilot.csvparser.batch.processor.CsvItemProcessor;
import fr.dsidiff.dsipilot.csvparser.configs.CsvColumnProperties;
import fr.dsidiff.dsipilot.csvparser.data.DocumentRepository;
import fr.dsidiff.dsipilot.csvparser.models.DocumentEntity;
import fr.dsidiff.dsipilot.csvparser.batch.writer.CsvAndDbWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CsvJobConfig  {
    private final CsvColumnProperties columnProperties;

    @Bean
    @StepScope
    public FlatFileItemReader<Map<String, String>> reader(
            @Value("#{jobParameters['filePath']}") String filePath,
            CsvColumnProperties columnProperties) {
        FlatFileItemReader<Map<String, String>> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(filePath));
        reader.setLinesToSkip(1);

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(";");
        tokenizer.setNames(columnProperties.getColumnsAsList().toArray(new String[0]));

        DefaultLineMapper<Map<String, String>> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSet -> {
            Map<String, String> row = new HashMap<>();
            for (String name : columnProperties.getColumnsAsList()) {
                row.put(name, fieldSet.readString(name));
            }
            return row;
        });

        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    @StepScope
    public CsvAndDbWriter writer(DocumentRepository repository) {
        return new CsvAndDbWriter(repository);
    }


    @Bean
    public CsvItemProcessor processor(CsvColumnProperties columnProperties) {
        return new CsvItemProcessor(columnProperties);
    }

    @Bean
    public Job csvJob(
            JobRepository jobRepository,
            Step csvStep) {
        return new JobBuilder("csvJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(csvStep)
                .build();
    }

    @Bean
    public Step csvStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<Map<String, String>> reader,
            CsvItemProcessor processor,
            CsvAndDbWriter writer) {
        return new StepBuilder("csvStep", jobRepository)
                .<Map<String, String>, DocumentEntity>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
