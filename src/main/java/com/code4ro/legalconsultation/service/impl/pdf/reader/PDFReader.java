package com.code4ro.legalconsultation.service.impl.pdf.reader;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;

public interface PDFReader {
    String getContent(PDDocument document) throws IOException;
}
