package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.model.dto.DocumentConsolidatedDto;
import com.code4ro.legalconsultation.model.dto.DocumentViewDto;
import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import com.code4ro.legalconsultation.model.persistence.DocumentMetadata;
import com.code4ro.legalconsultation.model.persistence.DocumentNode;
import com.code4ro.legalconsultation.service.api.DocumentNodeService;
import com.code4ro.legalconsultation.service.api.DocumentService;
import com.code4ro.legalconsultation.service.api.PDFService;
import com.code4ro.legalconsultation.service.api.StorageApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentServiceImpl.class);

    private final DocumentConsolidatedService documentConsolidatedService;
    private final DocumentMetadataService documentMetadataService;
    private final PDFService pdfService;
    private final DocumentNodeService documentNodeService;
    private final StorageApi storageApi;

    @Autowired
    public DocumentServiceImpl(final DocumentConsolidatedService documentConsolidatedService,
                               final DocumentMetadataService documentMetadataService,
                               final PDFService pdfService,
                               final DocumentNodeService documentNodeService,
                               final StorageApi storageApi) {
        this.documentConsolidatedService = documentConsolidatedService;
        this.documentMetadataService = documentMetadataService;
        this.pdfService = pdfService;
        this.documentNodeService = documentNodeService;
        this.storageApi = storageApi;
    }

    @Transactional(readOnly = true)
    @Override
    public List<DocumentMetadata> fetchAll() {
        return documentConsolidatedService.findAll()
                .stream()
                .map(DocumentConsolidated::getDocumentMetadata)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public DocumentMetadata fetchOne(final UUID id) {
        return documentConsolidatedService.getEntity(id).getDocumentMetadata();
    }

    @Transactional(readOnly = true)
    @Override
    public DocumentConsolidatedDto fetchOneConsolidated(final UUID id) {
        return documentConsolidatedService.getOne(id);
    }

    @Transactional
    @Override
    public DocumentConsolidated create(final DocumentViewDto document, final MultipartFile file) {

        DocumentMetadata metadata = documentMetadataService.build(document);
        final String filePath  = storeFile(file);
        metadata.setFilePath(filePath);

        final String pdfContent = pdfService.readAsString(file);
        final DocumentNode documentNode = documentNodeService.parse(pdfContent);

        return documentConsolidatedService.saveOne(new DocumentConsolidated(metadata, documentNode));
    }

    @Transactional
    @Override
    public DocumentConsolidated update(final UUID id,
                                                 final DocumentViewDto document,
                                                 final MultipartFile file) {
        final DocumentConsolidated consolidated = documentConsolidatedService.getEntity(id);

        // TODO delete current file from storage and the document node

        //update the metadata
        DocumentMetadata metadata = documentMetadataService.build(document);
        final String filePath  = storeFile(file);
        metadata.setFilePath(filePath);
        metadata.setId(consolidated.getDocumentMetadata().getId());

        final String pdfContent = pdfService.readAsString(file);
        final DocumentNode documentNode = documentNodeService.parse(pdfContent);

        consolidated.setDocumentMetadata(metadata);
        consolidated.setDocumentNode(documentNode);
        return documentConsolidatedService.saveOne(consolidated);
    }

    @Transactional
    @Override
    public void deleteById(final UUID id) throws EntityNotFoundException {
        documentConsolidatedService.getEntity(id);
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
