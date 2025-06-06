package fr.dsidiff.dsipilot.csvparser.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Getter @Setter
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 128, unique = true, nullable = false)
    private String documentId;

    private String filePath;
    private String fileName;
    private String fileType;
}
