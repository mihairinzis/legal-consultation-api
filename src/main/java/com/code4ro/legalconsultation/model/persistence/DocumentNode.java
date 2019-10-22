package com.code4ro.legalconsultation.model.persistence;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "document_nodes")
@Getter
@Setter
public class DocumentNode extends BaseEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent")
    private DocumentNode parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DocumentNode> children;

    @Column(name = "document_node_type")
    private DocumentNodeType documentNodeType;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;
}
