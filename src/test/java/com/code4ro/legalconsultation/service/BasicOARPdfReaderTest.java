package com.code4ro.legalconsultation.service;

import com.code4ro.legalconsultation.service.impl.pdf.reader.BasicOARPdfReader;
import com.code4ro.legalconsultation.util.PdfFileFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BasicOARPdfReaderTest {
    private final BasicOARPdfReader pdfReader = new BasicOARPdfReader();
    private final ClassLoader classLoader = getClass().getClassLoader();

    @Test
    public void testOARPdfParsing() {

        PdfFileFactory.getAsFiles(getClass().getClassLoader()).forEach(file -> {
            try {
                final PDDocument document = PDDocument.load(file);
                final String content = pdfReader.getContent(document);
                final String expectedContent = getExpectedContent(file.getName());
                assertThat(expectedContent).isEqualToIgnoringWhitespace(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private String getExpectedContent(final String filename) throws IOException {
        final String newFilename = filename.replace(".pdf", ".txt");
        final String filePath = new File(classLoader.getResource(String.format("pdf/%s", newFilename)).getFile()).getAbsolutePath();
        return Files.readString(Paths.get(filePath));
    }
}
