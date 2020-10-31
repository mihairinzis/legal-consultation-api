package com.code4ro.legalconsultation.comment.authorization;

import com.code4ro.legalconsultation.comment.model.persistence.Comment;
import com.code4ro.legalconsultation.comment.service.CommentService;
import com.code4ro.legalconsultation.core.authorization.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class CommentAuthorization extends Authorization {

    private final CommentService commentService;

    @Autowired
    public CommentAuthorization(final CommentService commentService) {
        this.commentService = commentService;
    }

    public boolean canChangeComment(final UUID commentId) {
        if (isSuperUser()) {
            return true;
        }
        final Comment comment = commentService.findById(commentId);
        return Objects.equals(getCurrentUserId(), comment.getOwner().getId());
    }
}
