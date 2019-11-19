package com.code4ro.legalconsultation.service.impl.pdf.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class DocumentNodeParserFactory {
    private final ChapterParser chapterParser;
    private final ArticleParser articleParser;
    private final SectionParser sectionParser;
    private final ParagraphParser paragraphParser;
    private final AlignmentParser alignmentParser;

    private Map<StartToken, Pattern> patterns;

    @Autowired
    public DocumentNodeParserFactory(final ChapterParser chapterParser,
                                     final ArticleParser articleParser,
                                     final SectionParser sectionParser,
                                     final ParagraphParser paragraphParser,
                                     final AlignmentParser alignmentParser) {
        this.chapterParser = chapterParser;
        this.articleParser = articleParser;
        this.sectionParser = sectionParser;
        this.paragraphParser = paragraphParser;
        this.alignmentParser = alignmentParser;
        this.patterns = Arrays.stream(StartToken.values())
                .collect(Collectors.toMap(Function.identity(), token -> Pattern.compile(token.regex)));
    }

    public DocumentNodeParser getParser(final String line) {
        final Map<StartToken, Matcher> matchers = getMatchers(line);
        if (matchers.get(StartToken.CHAPTER).matches()) {
            return chapterParser;
        }
        if (matchers.get(StartToken.SECTION).matches()) {
            return sectionParser;
        }
        if (matchers.get(StartToken.ARTICLE).matches()) {
            return articleParser;
        }
        if (matchers.get(StartToken.PARAGRAPH).matches()) {
            return paragraphParser;
        }
        if (matchers.get(StartToken.ALIGNMENT).matches()) {
            return alignmentParser;
        }

        return null;
    }

    private Map<StartToken, Matcher> getMatchers(final String line) {
        final Map<StartToken, Matcher> matchers = new EnumMap<>(StartToken.class);
        patterns.forEach((key, pattern) -> matchers.put(key, pattern.matcher(line)));
        return matchers;
    }
}
