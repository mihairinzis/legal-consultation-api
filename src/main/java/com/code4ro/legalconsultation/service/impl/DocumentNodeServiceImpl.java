package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.model.persistence.DocumentNode;
import com.code4ro.legalconsultation.repository.DocumentNodeRepository;
import com.code4ro.legalconsultation.service.api.DocumentNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
public class DocumentNodeServiceImpl implements DocumentNodeService {

    private final DocumentNodeRepository documentNodeRepository;

    @Autowired
    public DocumentNodeServiceImpl(final DocumentNodeRepository documentNodeRepository) {
        this.documentNodeRepository = documentNodeRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public DocumentNode getEntity(final UUID id) {
        return documentNodeRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    @Override
    public DocumentNode parse(String pdfContent) {
        // TODO parse the given string in the node tree structure
        final DocumentNode documentNode = new DocumentNode();
        return documentNodeRepository.save(documentNode);
    }
}
