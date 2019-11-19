package com.code4ro.legalconsultation.service.impl.pdf.parser;

import com.code4ro.legalconsultation.model.persistence.DocumentNode;
import com.code4ro.legalconsultation.model.persistence.DocumentNodeType;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DocumentParser extends DocumentNodeParser {
    private final String tokenWithoutTitleRegex = "(?!.*).";

    @Override
    protected String getTitle(final Matcher matcher) {
        return null;
    }

    @Override
    protected String getIdentifier(final Matcher matcher) {
        return null;
    }

    @Override
    protected StartToken getCurrentToken() {
        return StartToken.DOCUMENT;
    }

    @Override
    protected DocumentNodeType getNodeType() {
        return DocumentNodeType.DOCUMENT;
    }

    @Override
    protected Pattern getPatternWithoutTitle() {
        return Pattern.compile(tokenWithoutTitleRegex);
    }

    @Override
    protected void setTitle(final DocumentNode documentNode, final String title, final Matcher titleMatcher) {
        documentNode.setTitle(title);
    }
}
