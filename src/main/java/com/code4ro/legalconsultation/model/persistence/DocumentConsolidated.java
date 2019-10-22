package com.code4ro.legalconsultation.model.persistence;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "consolidated_document")
@Getter
@Setter
public class DocumentConsolidated extends BaseEntity {

    @OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "metadata_id")
    private DocumentMetadata documentMetadata;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "document_node_id")
    private DocumentNode documentNode;

    public DocumentConsolidated(DocumentMetadata documentMetadata, DocumentNode documentNode) {
        this.documentMetadata = documentMetadata;
        this.documentNode = documentNode;
    }

    public DocumentConsolidated() {
    }
}
