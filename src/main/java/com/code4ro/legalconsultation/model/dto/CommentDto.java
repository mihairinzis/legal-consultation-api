package com.code4ro.legalconsultation.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CommentDto extends BaseEntityDto {
    private String text;
    private Date lastEditDateTime;
}
