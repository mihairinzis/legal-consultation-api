package com.code4ro.legalconsultation.util;

import com.code4ro.legalconsultation.model.dto.CommentDto;
import com.code4ro.legalconsultation.model.persistence.ApplicationUser;
import com.code4ro.legalconsultation.model.persistence.Comment;
import com.code4ro.legalconsultation.service.api.CommentService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public final class CommentFactory {

    @Autowired
    private CommentService commentService;

    public CommentDto create() {
        final CommentDto commentDto = RandomObjectFiller.createAndFill(CommentDto.class);
        commentDto.setId(null);
        commentDto.setLastEditDateTime(new Date());
        return commentDto;
    }

    public CommentDto save(final UUID nodeId) {
        return commentService.create(nodeId, create());
    }

    public Comment createEntity() {
        final Comment comment = new Comment();
        comment.setOwner(RandomObjectFiller.createAndFillWithBaseEntity(ApplicationUser.class));
        comment.setText(RandomStringUtils.randomAlphanumeric(10));
        return comment;
    }
}
