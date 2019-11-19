package com.code4ro.legalconsultation.service.impl.pdf.reader;

import com.code4ro.legalconsultation.service.impl.pdf.parser.StartToken;
import com.code4ro.legalconsultation.service.impl.pdf.parser.StartTokenMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class BoldTextSelectionParser {
    private final StartTokenMatcher startTokenMatcher;

    @Autowired
    public BoldTextSelectionParser(final StartTokenMatcher startTokenMatcher) {
        this.startTokenMatcher = startTokenMatcher;
    }

    public Set<String> handle(final String boldArea) {
        final Set<String> result = new HashSet<>();
        final StartToken startToken = startTokenMatcher.getStartToken(boldArea);
        if (startTokenMatcher.matchesOtherThan(startToken, boldArea)) {
            final String firstMatch = startTokenMatcher.getMatchedString(startToken, boldArea);
            final String restOfTheString = boldArea.split(firstMatch)[1];
            result.addAll(handle(firstMatch));
            result.addAll(handle(restOfTheString));
        } else {
            result.add(boldArea);
        }

        return result;
    }
}
