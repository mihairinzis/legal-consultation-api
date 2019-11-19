package com.code4ro.legalconsultation.service.impl.pdf.parser;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class StartTokenMatcher {
    private Map<StartToken, Pattern> patterns;

    public StartTokenMatcher() {
        this.patterns = Arrays.stream(StartToken.values())
                .collect(Collectors.toMap(Function.identity(), value -> Pattern.compile(value.regex)));
    }

    public boolean isRegularLine(final String line) {
        return this.patterns.keySet().stream()
                .noneMatch(regex -> {
                    final Pattern pattern = patterns.get(regex);
                    final Matcher regexMatcher = pattern.matcher(line);
                    return regexMatcher.matches();
                });
    }

    public boolean startsWith(final StartToken startToken, final String line) {
        final Pattern pattern = patterns.get(startToken);
        final Matcher regexMatcher = pattern.matcher(line);
        return regexMatcher.matches();
    }

    public StartToken getStartToken(final String line) {
        return this.patterns.keySet().stream()
                .filter(regex -> {
                    final Pattern pattern = patterns.get(regex);
                    final Matcher regexMatcher = pattern.matcher(line);
                    return regexMatcher.matches();
                })
                .findFirst()
                .orElse(null);
    }

    public boolean matchesOtherThan(final StartToken startToken, final String line) {
        return patterns.keySet().stream()
                .filter(token -> token != startToken)
                .anyMatch(regex -> {
                    final Pattern pattern = patterns.get(regex);
                    final Matcher regexMatcher = pattern.matcher(line);
                    return regexMatcher.find();
                });
    }

    public String getMatchedString(final StartToken startToken, final String line) {
        return patterns.keySet().stream()
                .filter(token -> token != startToken)
                .filter(regex -> {
                    final Pattern pattern = patterns.get(regex);
                    final Matcher regexMatcher = pattern.matcher(line);
                    return regexMatcher.find();
                })
                .map(regex -> line.split(regex.regex)[0])
                .findFirst()
                .orElse(null);
    }

}
