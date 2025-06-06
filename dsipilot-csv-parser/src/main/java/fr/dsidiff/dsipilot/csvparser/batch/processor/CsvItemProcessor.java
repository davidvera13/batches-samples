package fr.dsidiff.dsipilot.csvparser.batch.processor;

import fr.dsidiff.dsipilot.csvparser.configs.CsvColumnProperties;
import fr.dsidiff.dsipilot.csvparser.models.DocumentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CsvItemProcessor implements ItemProcessor<Map<String, String>, DocumentEntity> {
    @Value("${watch.directory}")
    private String directoryToWatch;
    private final CsvColumnProperties columnProperties;


    @Override
    public DocumentEntity process(@NonNull Map<String, String> item) throws IOException {
        String documentId = UUID.randomUUID().toString();
        String fileName = item.get(columnProperties.getFileName());
        //String entFile = item.getOrDefault("NOM_DOCUMENT", fileName);

        DocumentEntity entity = new DocumentEntity();
        entity.setDocumentId(documentId);
        entity.setFileName(fileName);
        entity.setFilePath(directoryToWatch + "\\" + fileName);
        entity.setFileType(Files.probeContentType(Path.of(fileName)));
        return entity;
    }

}
