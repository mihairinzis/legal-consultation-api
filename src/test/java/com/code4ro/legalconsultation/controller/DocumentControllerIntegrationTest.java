package com.code4ro.legalconsultation.controller;

import com.code4ro.legalconsultation.common.controller.AbstractControllerIntegrationTest;
import com.code4ro.legalconsultation.model.dto.DocumentViewDto;
import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import com.code4ro.legalconsultation.model.persistence.DocumentMetadata;
import com.code4ro.legalconsultation.model.persistence.DocumentNode;
import com.code4ro.legalconsultation.repository.DocumentConsolidatedRepository;
import com.code4ro.legalconsultation.repository.DocumentMetadataRepository;
import com.code4ro.legalconsultation.repository.DocumentNodeRepository;
import com.code4ro.legalconsultation.service.api.CommentService;
import com.code4ro.legalconsultation.util.CommentFactory;
import com.code4ro.legalconsultation.util.DocumentNodeFactory;
import com.code4ro.legalconsultation.util.PdfFileFactory;
import com.code4ro.legalconsultation.util.RandomObjectFiller;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DocumentControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Value("${storage.filesystem.directory}")
    private String customStoreDirPath;

    @Autowired
    private DocumentMetadataRepository documentMetadataRepository;
    @Autowired
    private DocumentConsolidatedRepository documentConsolidatedRepository;
    @Autowired
    private DocumentNodeRepository documentNodeRepository;
    @Autowired
    private DocumentNodeFactory documentNodeFactory;
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentFactory commentFactory;

    @Test
    @WithMockUser
    @Transactional
    public void saveDocument() throws Exception {
        final DocumentViewDto randomView = RandomObjectFiller.createAndFill(DocumentViewDto.class);

        final MockMultipartFile randomFile = PdfFileFactory.getAsMultipart(getClass().getClassLoader());
        mvc.perform(multipart("/api/document/")
                .file(randomFile)
                .param("title", randomView.getTitle())
                .param("number", randomView.getDocumentNumber().toString())
                .param("documentInitializer", randomView.getDocumentInitializer())
                .param("type", randomView.getDocumentType().toString())
                .param("creationDate", "09/09/2018")
                .param("receiveDate", "10/09/2018"))
                .andExpect(status().isCreated());

        assertThat(documentMetadataRepository.count()).isEqualTo(1);
        assertThat(documentConsolidatedRepository.count()).isEqualTo(1);
        assertThat(documentNodeRepository.count()).isEqualTo(14);

        final String soredFilePath = documentMetadataRepository.findAll().get(0).getFilePath();
        assertThatDocumentIsStored(soredFilePath);

        final List<DocumentConsolidated> documents = documentConsolidatedRepository.findAll();
        final DocumentConsolidated document = documents.iterator().next();
        assertThatDocumentNodeTreeIsGeneratedCorrectly(document.getDocumentNode());
    }

    private void assertThatDocumentNodeTreeIsGeneratedCorrectly(final DocumentNode document) {
        final DocumentNode expectedDocument = documentNodeFactory.createDocument(null, "Document title  on multiple lines", "Sample document introduction that can be on one line or on multiplelines");
        assertThat(document.getChildren()).hasSize(2);
        assertDocumentNodeContent(expectedDocument, document);

        final DocumentNode expectedChapter1 = documentNodeFactory.createChapter("I", "Chapter title on multiple lines  ", null);
        final DocumentNode chapter1 = document.getChildren().get(0);
        assertThat(chapter1.getChildren()).hasSize(2);
        assertDocumentNodeContent(expectedChapter1, chapter1);

        final DocumentNode expectedChapter2 = documentNodeFactory.createChapter("II", "Chapter title on single line", null);
        final DocumentNode chapter2 = document.getChildren().get(1);
        assertThat(chapter1.getChildren()).hasSize(2);
        assertDocumentNodeContent(expectedChapter2, chapter2);

        final DocumentNode expectedArticle4 = documentNodeFactory.createArticle("4", null, "Article without children");
        final DocumentNode article4 = chapter2.getChildren().get(0);
        assertThat(article4.getChildren()).isEmpty();
        assertDocumentNodeContent(expectedArticle4, article4);

        final DocumentNode expectedSection1 = documentNodeFactory.createSection("1", "Section with title", null);
        final DocumentNode section1 = chapter1.getChildren().get(0);
        assertThat(section1.getChildren()).hasSize(2);
        assertDocumentNodeContent(expectedSection1, section1);

        final DocumentNode expectedSection2 = documentNodeFactory.createSection("2", null, null);
        final DocumentNode section2 = chapter1.getChildren().get(1);
        assertThat(section1.getChildren()).hasSize(2);
        assertDocumentNodeContent(expectedSection2, section2);

        final DocumentNode expectedArticle1 = documentNodeFactory.createArticle("1", null, null);
        final DocumentNode article1 = section1.getChildren().get(0);
        assertThat(article1.getChildren()).hasSize(1);
        assertDocumentNodeContent(expectedArticle1, article1);

        final DocumentNode expectedArticle2 = documentNodeFactory.createArticle("2", "Article with title on one line", null);
        final DocumentNode article2 = section1.getChildren().get(1);
        assertThat(article2.getChildren()).hasSize(1);
        assertDocumentNodeContent(expectedArticle2, article2);

        final DocumentNode expectedArticle3 = documentNodeFactory.createArticle("3", "Article with title on multiple lines", null);
        final DocumentNode article3 = section2.getChildren().get(0);
        assertThat(article2.getChildren()).hasSize(1);
        assertDocumentNodeContent(expectedArticle3, article3);

        final DocumentNode expectedParagraph11 = documentNodeFactory.createParagraph("1", null, "Paragraph content on a single line");
        final DocumentNode paragraph11 = article1.getChildren().get(0);
        assertThat(paragraph11.getChildren()).isEmpty();
        assertDocumentNodeContent(expectedParagraph11, paragraph11);

        final DocumentNode expectedParagraph21 = documentNodeFactory.createParagraph("1", null, "Paragraph content on multiple lines");
        final DocumentNode paragraph21 = article2.getChildren().get(0);
        assertThat(paragraph21.getChildren()).isEmpty();
        assertDocumentNodeContent(expectedParagraph21, paragraph21);

        final DocumentNode expectedParagraph35 = documentNodeFactory.createParagraph("5", null, "Paragraph with multiple letters");
        final DocumentNode paragraph35 = article3.getChildren().get(0);
        assertThat(paragraph35.getChildren()).hasSize(2);
        assertDocumentNodeContent(expectedParagraph35, paragraph35);

        final DocumentNode expectedAlignmentA = documentNodeFactory.createAlignment("a", null, "Sample letter content 1");
        final DocumentNode alignmentA = paragraph35.getChildren().get(0);
        assertThat(alignmentA.getChildren()).isEmpty();
        assertDocumentNodeContent(expectedAlignmentA, alignmentA);

        final DocumentNode expectedAlignmentB = documentNodeFactory.createAlignment("b", null, "Sample letter content 2");
        final DocumentNode alignmentB = paragraph35.getChildren().get(1);
        assertThat(alignmentA.getChildren()).isEmpty();
        assertDocumentNodeContent(expectedAlignmentB, alignmentB);
    }

    private void assertDocumentNodeContent(final DocumentNode expected, final DocumentNode actual) {
        assertThat(expected.getIdentifier()).isEqualToIgnoringWhitespace(actual.getIdentifier());
        assertThat(expected.getTitle()).isEqualToIgnoringWhitespace(actual.getTitle());
        assertThat(expected.getContent()).isEqualToIgnoringWhitespace(actual.getContent());
        assertThat(expected.getDocumentNodeType()).isEqualByComparingTo(actual.getDocumentNodeType());
    }

    private void assertThatDocumentIsStored(String soredFilePath) {
        assertThat(soredFilePath).contains(customStoreDirPath);
        assertThat(Files.exists(Paths.get(soredFilePath))).isTrue();
        // remove the store directory
        final File storeDir = new File(customStoreDirPath);
        FileSystemUtils.deleteRecursively(storeDir);
        storeDir.deleteOnExit();
    }

    @Test
    @WithMockUser
    @Transactional
    public void getDocument() throws Exception {
        DocumentConsolidated consolidated = saveSingleConsolidated();

        mvc.perform(get(endpoint("/api/document/", consolidated.getId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(consolidated.getDocumentMetadata().getId().toString()))
                .andExpect(status().isOk());

        mvc.perform(get(endpoint("/api/document/", consolidated.getId(), "/consolidated"))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(consolidated.getId().toString()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(documentMetadataRepository.count()).isEqualTo(1);
        assertThat(documentConsolidatedRepository.count()).isEqualTo(1);
    }

    @Test
    @WithMockUser
    @Transactional
    public void getDocumentCommentsNumber() throws Exception {
        persistMockedUser();
        DocumentConsolidated consolidated = saveSingleConsolidated();
        commentFactory.save(consolidated.getDocumentNode().getId());
        commentFactory.save(consolidated.getDocumentNode().getId());

        mvc.perform(get(endpoint("/api/document/", consolidated.getId(), "/consolidated"))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.documentNode.numberOfComments").value("2"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithMockUser
    @Transactional
    public void listDocuments() throws Exception {
        DocumentConsolidated consolidated1 = saveSingleConsolidated();
        DocumentConsolidated consolidated2 = saveSingleConsolidated();

        mvc.perform(get("/api/document/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(consolidated1.getDocumentMetadata().getId().toString()))
                .andExpect(jsonPath("$[1].id").value(consolidated2.getDocumentMetadata().getId().toString()))
                .andExpect(status().isOk());

        assertThat(documentMetadataRepository.count()).isEqualTo(2);
        assertThat(documentConsolidatedRepository.count()).isEqualTo(2);
    }

    @Test
    @WithMockUser
    @Transactional
    public void deleteDocument() throws Exception {
        DocumentConsolidated consolidated = saveSingleConsolidated();

        mvc.perform(delete("/api/document/" + consolidated.getId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(documentMetadataRepository.count()).isEqualTo(0);
        assertThat(documentConsolidatedRepository.count()).isEqualTo(0);
    }

    @Test
    @WithMockUser
    public void testGetDocumentNotFound() throws Exception {
        UUID uuid = UUID.randomUUID();

        mvc.perform(get("/api/document/" + uuid.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testGetDocumentConsolidatedNotFound() throws Exception {
        UUID uuid = UUID.randomUUID();

        mvc.perform(get("/api/document/" + uuid.toString() + "/consolidated"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void deleteDocumentNotFound() throws Exception {
        UUID uuid = UUID.randomUUID();

        mvc.perform(delete("/api/document/" + uuid.toString()))
                .andExpect(status().isNotFound());
    }

    private DocumentConsolidated saveSingleConsolidated() {
        final DocumentNode documentNode = documentNodeFactory.save();
        final DocumentMetadata documentMetadata = RandomObjectFiller.createAndFill(DocumentMetadata.class);
        DocumentConsolidated consolidated = new DocumentConsolidated(documentMetadata, documentNode);
        return documentConsolidatedRepository.save(consolidated);
    }
}
