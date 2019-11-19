package com.code4ro.legalconsultation.service.impl.pdf;

import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.service.api.PDFService;
import com.code4ro.legalconsultation.service.impl.pdf.reader.BasicOARPdfReader;
import com.code4ro.legalconsultation.service.impl.pdf.reader.PDFReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PDFServiceImpl implements PDFService {
    private static final Logger LOG = LoggerFactory.getLogger(PDFServiceImpl.class);

    private final BasicOARPdfReader basicOARPdfReader;

    @Autowired
    public PDFServiceImpl(final BasicOARPdfReader basicOARPdfReader) {
        this.basicOARPdfReader = basicOARPdfReader;
    }

    @Override
    public String readAsString(final MultipartFile file) {

        try {
            final PDDocument doc = PDDocument.load(file.getInputStream());
            // TODO: add a more general way for getting the right parser based on document template once we have more document types
            final PDFReader pdfReader = basicOARPdfReader;
            return pdfReader.getContent(doc);
        } catch (IOException e) {
            LOG.warn("Exception while parsing PDF file", e);
            throw new LegalValidationException("document.parse.pdf.failed", HttpStatus.BAD_REQUEST);
        }
    }
}
