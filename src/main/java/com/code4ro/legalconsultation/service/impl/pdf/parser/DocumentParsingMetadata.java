package com.code4ro.legalconsultation.service.impl.pdf.parser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentParsingMetadata {
    public Integer currentLineIndex;
    private Integer linesCount;

    public DocumentParsingMetadata(final Integer linesCount) {
        this.linesCount = linesCount;
        currentLineIndex = 0;
    }

    public void next() {
        currentLineIndex++;
    }

    public boolean isLastLine() {
        return currentLineIndex == linesCount - 1;
    }
}
