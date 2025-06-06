package fr.dsidiff.dsipilot.csvparser.data;

import fr.dsidiff.dsipilot.csvparser.models.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository
        extends JpaRepository<DocumentEntity, Long> {
}
