package com.code4ro.legalconsultation.util;

import com.code4ro.legalconsultation.model.persistence.DocumentNode;
import com.code4ro.legalconsultation.model.persistence.DocumentNodeType;
import com.code4ro.legalconsultation.repository.DocumentNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class DocumentNodeFactory {

    @Autowired
    private DocumentNodeRepository documentNodeRepository;

    public DocumentNode save() {
        return documentNodeRepository.save(create());
    }

    public DocumentNode create() {
        final DocumentNode root = new DocumentNode();
        root.setDocumentNodeType(DocumentNodeType.DOCUMENT);
        final DocumentNode child = new DocumentNode();
        child.setDocumentNodeType(DocumentNodeType.ARTICLE);
        root.setChildren(Collections.singletonList(child));
        child.setParent(root);
        return root;
    }
}
