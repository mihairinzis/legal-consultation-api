package com.code4ro.legalconsultation.model.persistence;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="comments")
@Getter
@Setter
public class Comment extends BaseEntity {
    @Column(name = "text")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_node_id")
    private DocumentNode documentNode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private ApplicationUser owner;

    @Column(name = "last_edit_date", nullable=false)
    @Temporal(TemporalType.DATE)
    @LastModifiedDate
    private Date lastEditDateTime;
}
