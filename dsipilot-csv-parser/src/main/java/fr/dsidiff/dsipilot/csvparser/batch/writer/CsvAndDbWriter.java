package fr.dsidiff.dsipilot.csvparser.batch.writer;

import fr.dsidiff.dsipilot.csvparser.data.DocumentRepository;
import fr.dsidiff.dsipilot.csvparser.models.DocumentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@RequiredArgsConstructor
public class CsvAndDbWriter implements ItemWriter<DocumentEntity> {
    private final DocumentRepository documentRepository;

    @Value("#{jobParameters['filePath']}")
    private String filePath;

    @Value("${output.directory}")
    private String outputDir;


    @Override
    public void write(Chunk<? extends DocumentEntity> items) throws Exception {
        // Save to DB
        documentRepository.saveAll(items);
        // Append to CSV
        String fileName = new File(filePath).getName().replace(".csv", ".csv.txt");
        Path outputFile = Paths.get(outputDir, fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(
                outputFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            for (DocumentEntity doc : items) {
                writer.write(doc.getFileName()); // you can write full filePath if preferred
                writer.newLine();
            }
        }
    }
}