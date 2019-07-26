package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.model.dto.DocumentViewDto;
import com.code4ro.legalconsultation.model.persistence.DocumentBreakdown;
import com.code4ro.legalconsultation.repository.DocumentBreakdownRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentBreakdownService {

    private final DocumentBreakdownRepository documentBreakdownRepository;

    @Autowired
    public DocumentBreakdownService(DocumentBreakdownRepository documentBreakdownRepository) {
        this.documentBreakdownRepository = documentBreakdownRepository;
    }

    public List<DocumentBreakdown> fetchAll(){
        return documentBreakdownRepository.findAll();
    }

    public Optional<DocumentBreakdown> fetchOne(final String id){
        return documentBreakdownRepository.findById(UUID.fromString(id));
    }

    public DocumentBreakdown create(final Path documentPath){
        // (1)
        //TODO - decide what is the input for creating the document breakdown
        //TODO - implement the parsing logic for the document
        return documentBreakdownRepository.save(new DocumentBreakdown());
    }

    public DocumentBreakdown build(final MultipartFile documentPath){
        return new DocumentBreakdown();
    }

    public DocumentBreakdown update(final String id, final DocumentViewDto documentViewDto){
        //TODO - same as (1)
        DocumentBreakdown documentBreakdown = new DocumentBreakdown();
        documentBreakdown.setId(UUID.fromString(id));
        return documentBreakdownRepository.save(documentBreakdown);
    }

    public void deleteById(final String id){
        documentBreakdownRepository.deleteById(UUID.fromString(id));
    }
}
