package com.code4ro.legalconsultation.service.impl.pdf.parser;

import com.code4ro.legalconsultation.model.persistence.DocumentNodeType;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SectionParser extends DocumentNodeParser {
    private final String tokenWithoutTitleRegex = "(SECŢIUNEA) ([^:]*?)$";

    @Override
    protected String getTitle(final Matcher matcher) {
        if (matcher.group(4) != null) {
            return matcher.group(4);
        }

        return null;
    }

    @Override
    protected String getIdentifier(final Matcher matcher) {
        return matcher.group(2);
    }

    @Override
    protected StartToken getCurrentToken() {
        return StartToken.SECTION;
    }

    @Override
    protected DocumentNodeType getNodeType() {
        return DocumentNodeType.SECTION;
    }

    @Override
    protected Pattern getPatternWithoutTitle() {
        return Pattern.compile(tokenWithoutTitleRegex);
    }
}
