package com.code4ro.legalconsultation.service.impl.pdf.parser;

public enum StartToken {
    DOCUMENT("(?!.*)."),
    CHAPTER("(CAPITOLUL) (.*?): (.*)"),
    SECTION("(SECŢIUNEA) ([0-9]*)(:? (.*)?)?"),
    ARTICLE("(Art[.]) ([0-9]*)(:? (.*)?)?"),
    PARAGRAPH("[(]([0-9]*?)[)]((?! ).*)"),
    ALIGNMENT("(^[a-z]*?)[)] ?((?! ).*)");

    public final String regex;

    StartToken(String regex) {
        this.regex = regex;
    }
}
