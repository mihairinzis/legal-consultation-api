package com.code4ro.legalconsultation.model.dto;

import com.code4ro.legalconsultation.model.persistence.DocumentNodeType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class DocumentNodeDto extends BaseEntityDto {
    private List<DocumentNodeDto> children;
    private DocumentNodeType documentNodeType;
    private String title;
    private String content;
    private BigInteger numberOfComments;
}
