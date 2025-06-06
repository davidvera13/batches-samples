package fr.dsidiff.dsipilot.csvparser.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "columns")
@Getter
@Setter
public class CsvColumnProperties {
    private String names;
    private String fileName;

    public List<String> getColumnsAsList() {
        return Arrays.asList(names.split(","));
    }
}
