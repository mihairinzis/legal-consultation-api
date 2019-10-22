package com.code4ro.legalconsultation.service.api;

import com.code4ro.legalconsultation.model.dto.DocumentConsolidatedDto;
import com.code4ro.legalconsultation.model.dto.DocumentViewDto;
import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import com.code4ro.legalconsultation.model.persistence.DocumentMetadata;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
public interface DocumentService {
    //return all metadata for documents
    List<DocumentMetadata> fetchAll();

    //return document metadata for a single document
    DocumentMetadata fetchOne(final UUID id);

    //return the entire consolidated form of the document,
    //that contains the metadata and the breakdown into units
    DocumentConsolidatedDto fetchOneConsolidated(final UUID id);

    //create a single document, including metadata and breakdown
    DocumentConsolidated create(final DocumentViewDto document,
                                final MultipartFile file);

    //update a single document
    DocumentConsolidated update(final UUID id,
                                final DocumentViewDto document,
                                final MultipartFile multipartFile);

    //delete a document based on id
    void deleteById(final UUID id) throws EntityNotFoundException;
}
