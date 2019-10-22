package com.code4ro.legalconsultation.repository;

import com.code4ro.legalconsultation.model.persistence.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findByDocumentNodeId(final UUID nodeId,
                                       final Pageable pageable);
    BigInteger countByDocumentNodeId(final UUID nodeId);
}
