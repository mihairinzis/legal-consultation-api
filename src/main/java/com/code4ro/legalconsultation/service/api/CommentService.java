package com.code4ro.legalconsultation.service.api;

import com.code4ro.legalconsultation.model.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.UUID;

public interface CommentService {
    CommentDto update(UUID nodeId, UUID id, CommentDto commentDto);
    CommentDto create(UUID nodeId, CommentDto commentDto);
    void delete(UUID id);
    Page<CommentDto> findAll(UUID nodeId, Pageable pageable);
    BigInteger count(UUID nodeId);
}
