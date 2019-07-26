package com.code4ro.legalconsultation.controller;

import com.code4ro.legalconsultation.common.exceptions.ResourceNotFoundException;
import com.code4ro.legalconsultation.model.dto.DocumentViewDto;
import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import com.code4ro.legalconsultation.model.persistence.DocumentMetadata;
import com.code4ro.legalconsultation.model.persistence.DocumentType;
import com.code4ro.legalconsultation.service.api.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/document")
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("")
    public ResponseEntity<List<DocumentMetadata>> getAllDocuments() {
        List<DocumentMetadata> documents = documentService.fetchAll();
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity getDocumentById(@PathVariable String id) {
        Optional<DocumentMetadata> optDocument = documentService.fetchOne(id);

        if(!optDocument.isPresent())
            throw new ResourceNotFoundException();

        return ResponseEntity.ok(optDocument.get());
    }

    @GetMapping("/{id}/consolidated")
    public ResponseEntity getDocumentConsolidatedById(@PathVariable String id){
        Optional<DocumentConsolidated> consolidatedDocumentOpt = documentService.fetchOneConsolidated(id);

        if(!consolidatedDocumentOpt.isPresent())
            throw new ResourceNotFoundException();

        return ResponseEntity.ok(consolidatedDocumentOpt.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteDocument(@PathVariable String id) {
        documentService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("")
    public ResponseEntity<UUID> createDocument(@RequestParam("title") String documentTitle,
            @RequestParam("number") BigInteger documentNumber,
            @RequestParam("documentInitializer") String initiator,
            @RequestParam("type") DocumentType type,
            @DateTimeFormat(pattern = "dd/MM/yyyy") Date creationDate,
            @DateTimeFormat(pattern = "dd/MM/yyyy") Date receiveDate,
            @RequestParam("file") MultipartFile documentFile) {

        DocumentViewDto documentViewDto = new DocumentViewDto(documentTitle, documentNumber, initiator, type, creationDate, receiveDate);
        DocumentConsolidated consolidated = documentService.create(documentViewDto, documentFile);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(consolidated.getId());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UUID> modifyDocument(@PathVariable String id, @RequestParam("file") MultipartFile documentFile, @RequestBody DocumentViewDto doc) {
        Optional<DocumentConsolidated> consolidated = documentService.update(id, doc, documentFile);
        if(!consolidated.isPresent())
            throw new ResourceNotFoundException();

        return ResponseEntity.ok(consolidated.get().getId());
    }
}
