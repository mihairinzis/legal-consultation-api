package com.code4ro.legalconsultation.converters;

import com.code4ro.legalconsultation.model.dto.DocumentNodeDto;
import com.code4ro.legalconsultation.model.persistence.DocumentNode;
import com.code4ro.legalconsultation.service.api.CommentService;
import com.code4ro.legalconsultation.service.api.MapperService;
import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocumentNodeDtoConverter extends AbstractConverter<DocumentNode, DocumentNodeDto> {

    private final CommentService commentService;
    private final MapperService mapperService;

    @Autowired
    public DocumentNodeDtoConverter(CommentService commentService, MapperService mapperService) {
        this.commentService = commentService;
        this.mapperService = mapperService;
    }

    @Override
    protected DocumentNodeDto convert(DocumentNode documentNode) {
        final DocumentNodeDto dto = new DocumentNodeDto();
        dto.setId(documentNode.getId());
        dto.setChildren(mapperService.mapList(documentNode.getChildren(), DocumentNodeDto.class));
        dto.setDocumentNodeType(documentNode.getDocumentNodeType());
        dto.setTitle(documentNode.getTitle());
        dto.setContent(documentNode.getContent());
        dto.setNumberOfComments(commentService.count(documentNode.getId()));
        return dto;
    }
}
