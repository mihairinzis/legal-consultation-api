package com.code4ro.legalconsultation.service.api;

import com.code4ro.legalconsultation.model.persistence.DocumentNode;

import java.util.UUID;

public interface DocumentNodeService {
    DocumentNode getEntity(UUID id);
    DocumentNode parse(String pdfContent);
}
