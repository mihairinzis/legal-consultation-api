package com.code4ro.legalconsultation.controller;

import com.code4ro.legalconsultation.model.dto.DocumentViewDto;
import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import com.code4ro.legalconsultation.model.persistence.DocumentMetadata;
import com.code4ro.legalconsultation.model.persistence.DocumentType;
import com.code4ro.legalconsultation.service.api.DocumentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/document")
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @ApiOperation(value = "Return document metadata for all documents in the platform",
            response = List.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("")
    public ResponseEntity<List<DocumentMetadata>> getAllDocuments() {
        List<DocumentMetadata> documents = documentService.fetchAll();
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @ApiOperation(value = "Return document metadata for a single document in the platform based on id",
            response = DocumentMetadata.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/{id}")
    public ResponseEntity getDocumentById(@ApiParam("Id of the document object being requested") @PathVariable UUID id) {
        return ResponseEntity.ok(documentService.fetchOne(id));
    }

    @ApiOperation(value = "Return metadata and content for a single document in the platform based on id",
            response = DocumentConsolidated.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/{id}/consolidated")
    public ResponseEntity getDocumentConsolidatedById(@ApiParam("Id of the document object being requested") @PathVariable UUID id){
        return ResponseEntity.ok(documentService.fetchOneConsolidated(id));
    }

    @ApiOperation(value = "Delete metadata and contents for a single document in the platform based on id")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteDocument(@ApiParam("Id of the document object being deleted") @PathVariable UUID id) {
        documentService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Create a new document in the platform",
            response = UUID.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("")
    public ResponseEntity<UUID> createDocument(
            @ApiParam(value = "Title of the created document") @RequestParam("title") String documentTitle,
            @ApiParam(value = "The document number of the created document") @RequestParam("number") BigInteger documentNumber,
            @ApiParam(value = "The organization that initiated the document") @RequestParam("documentInitializer") String initiator,
            @ApiParam(value = "Type of the document") @RequestParam("type") DocumentType type,
            @ApiParam(value = "Date when the document was created") @DateTimeFormat(pattern = "dd/MM/yyyy") Date creationDate,
            @ApiParam(value = "Date when the document was received") @DateTimeFormat(pattern = "dd/MM/yyyy") Date receiveDate,
            @ApiParam(value = "The file containing the document content") @RequestParam("file") MultipartFile documentFile) {

        DocumentViewDto documentViewDto = new DocumentViewDto(documentTitle, documentNumber, initiator, type, creationDate, receiveDate);
        DocumentConsolidated consolidated = documentService.create(documentViewDto, documentFile);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(consolidated.getId());
    }

    @ApiOperation(value = "Modify a saved document in the platform",
            response = UUID.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping("/{id}")
    public ResponseEntity<UUID> modifyDocument(@ApiParam(value = "Id of the document being modified") @RequestParam UUID id,
                                               @ApiParam(value = "Title of the modified document") @RequestParam("title") String documentTitle,
                                               @ApiParam(value = "The document number of the modified document") @RequestParam("number") BigInteger documentNumber,
                                               @ApiParam(value = "The organization that initiated the document") @RequestParam("documentInitializer") String initiator,
                                               @ApiParam(value = "Type of the document") @RequestParam("type") DocumentType type,
                                               @ApiParam(value = "Date when the document was created") @DateTimeFormat(pattern = "dd/MM/yyyy") Date creationDate,
                                               @ApiParam(value = "Date when the document was received") @DateTimeFormat(pattern = "dd/MM/yyyy") Date receiveDate,
                                               @ApiParam(value = "The file containing the modified document content") @RequestParam("file") MultipartFile documentFile) {

        DocumentViewDto documentViewDto = new DocumentViewDto(documentTitle, documentNumber, initiator, type, creationDate, receiveDate);
        DocumentConsolidated consolidated = documentService.update(id, documentViewDto, documentFile);

        return ResponseEntity.ok(consolidated.getId());
    }
}
