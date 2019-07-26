package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.common.exceptions.ResourceNotFoundException;
import com.code4ro.legalconsultation.model.dto.DocumentViewDto;
import com.code4ro.legalconsultation.model.persistence.DocumentBreakdown;
import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import com.code4ro.legalconsultation.model.persistence.DocumentMetadata;
import com.code4ro.legalconsultation.service.api.DocumentService;
import com.code4ro.legalconsultation.service.api.StorageApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentServiceImpl.class);

    private final DocumentConsolidatedService documentConsolidatedService;
    private final DocumentBreakdownService documentBreakdownService;
    private final DocumentMetadataService documentMetadataService;
    private final StorageApi storageApi;

    @Autowired
    public DocumentServiceImpl(final DocumentConsolidatedService documentConsolidatedService,
                               final DocumentBreakdownService documentBreakdownService,
                               final DocumentMetadataService documentMetadataService,
                               final StorageApi storageApi) {
        this.documentConsolidatedService = documentConsolidatedService;
        this.documentBreakdownService = documentBreakdownService;
        this.documentMetadataService = documentMetadataService;
        this.storageApi = storageApi;
    }

    @Override
    public List<DocumentMetadata> fetchAll() {
        return documentConsolidatedService.findAll()
                .stream()
                .map(DocumentConsolidated::getDocumentMetadata)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DocumentMetadata> fetchOne(final String id)
            throws ResourceNotFoundException{
        return Optional.ofNullable(documentConsolidatedService.findOne(id)
                .map(DocumentConsolidated::getDocumentMetadata)
                .orElse(null));
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<DocumentConsolidated> fetchOneConsolidated(final String id) {
        return documentConsolidatedService.findOne(id);
    }

    @Override
    public DocumentConsolidated create(final DocumentViewDto document, final MultipartFile file) {

        DocumentMetadata metadata = documentMetadataService.build(document);
        final String filePath  = storeFile(file);
        metadata.setFilePath(filePath);

        DocumentBreakdown breakdown = documentBreakdownService.build(file);

        return documentConsolidatedService.saveOne(new DocumentConsolidated(metadata, breakdown));
    }

    @Override
    public Optional<DocumentConsolidated> update(final String id, final DocumentViewDto document,
                                                 final MultipartFile file) {
        Optional<DocumentConsolidated> consolidated = documentConsolidatedService.findOne(id);

        if(!consolidated.isPresent())
            return Optional.empty();

        DocumentConsolidated documentConsolidated = consolidated.get();

        //update the metadata
        DocumentMetadata metadata = documentMetadataService.build(document);
        final String filePath  = storeFile(file);
        metadata.setFilePath(filePath);
        metadata.setId(documentConsolidated.getDocumentMetadata().getId());

        //update the breakdown
        DocumentBreakdown breakdown = documentBreakdownService.build(file);

        documentConsolidated.setDocumentMetadata(metadata);
        documentConsolidated.setDocumentBreakdown(breakdown);
        return Optional.of(documentConsolidatedService.saveOne(documentConsolidated));
    }

    @Override
    @Transactional
    public void deleteById(final String id) throws ResourceNotFoundException {
        DocumentConsolidated consolidated = documentConsolidatedService.findOne(id)
                .orElseThrow(ResourceNotFoundException::new);
        documentConsolidatedService.deleteById(id);
    }

    private String storeFile(MultipartFile file) {
        try {
            return storageApi.storeFile(file);
        } catch (Exception e) {
            LOG.error("Could not store document.", e);
            return null;
        }
    }
}
