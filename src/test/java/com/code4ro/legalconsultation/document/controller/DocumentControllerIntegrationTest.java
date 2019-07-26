package com.code4ro.legalconsultation.document.controller;

import com.code4ro.legalconsultation.common.controller.AbstractControllerIntegrationTest;
import com.code4ro.legalconsultation.model.dto.DocumentViewDto;
import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import com.code4ro.legalconsultation.repository.DocumentBreakdownRepository;
import com.code4ro.legalconsultation.repository.DocumentConsolidatedRepository;
import com.code4ro.legalconsultation.repository.DocumentMetadataRepository;
import com.code4ro.legalconsultation.util.RandomObjectFiller;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DocumentControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Value("${storage.filesystem.directory}")
    private String customStoreDirPath;

    @Autowired
    private DocumentMetadataRepository documentMetadataRepository;
    @Autowired
    private DocumentBreakdownRepository documentBreakdownRepository;
    @Autowired
    private DocumentConsolidatedRepository documentConsolidatedRepository;

    @Test
    @WithMockUser
    public void saveDocument() throws Exception{
        final String randomDocumentContent = RandomStringUtils.randomAscii(10);
        final DocumentViewDto randomView = RandomObjectFiller.createAndFill(DocumentViewDto.class);
        final MockMultipartFile randomFile = new MockMultipartFile("file", "file.doc", "text/plain",
                randomDocumentContent.getBytes());

        mvc.perform(MockMvcRequestBuilders.multipart("/api/document")
                .file(randomFile)
                .param("title", randomView.getTitle())
                .param("number", randomView.getDocumentNumber().toString())
                .param("documentInitializer", randomView.getDocumentInitializer())
                .param("type", randomView.getDocumentType().toString())
                .param("creationDate", "09/09/2018")
                .param("receiveDate", "10/09/2018"))
                .andExpect(status().isCreated());

        assertThat(documentMetadataRepository.count()).isEqualTo(1);
        assertThat(documentBreakdownRepository.count()).isEqualTo(1);
        assertThat(documentConsolidatedRepository.count()).isEqualTo(1);

        final String soredFilePath = documentMetadataRepository.findAll().get(0).getFilePath();
        assertThatDocumentIsStored(randomDocumentContent, soredFilePath);
    }

    private void assertThatDocumentIsStored(String expectedDocumentContent, String soredFilePath) throws IOException {
        assertThat(soredFilePath).contains(customStoreDirPath);
        final String fileContent = Files.readString(Paths.get(soredFilePath), StandardCharsets.US_ASCII);
        assertThat(fileContent).isEqualTo(expectedDocumentContent);
        // remove the store directory
        final File storeDir = new File(customStoreDirPath);
        FileSystemUtils.deleteRecursively(storeDir);
        storeDir.deleteOnExit();
    }

    @Test
    @WithMockUser
    public void getDocument() throws Exception {

        DocumentConsolidated consolidated = saveSingleConsolidated();

        mvc.perform(MockMvcRequestBuilders.get("/api/document/" + consolidated.getId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(consolidated.getDocumentMetadata().getId().toString()))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders.get("/api/document/" + consolidated.getId().toString() + "/consolidated")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(consolidated.getId().toString()))
                .andExpect(status().isOk());

        assertThat(documentMetadataRepository.count()).isEqualTo(1);
        assertThat(documentBreakdownRepository.count()).isEqualTo(1);
        assertThat(documentConsolidatedRepository.count()).isEqualTo(1);
    }

    @Test
    @WithMockUser
    public void listDocuments() throws Exception{
        DocumentConsolidated consolidated1 = saveSingleConsolidated();
        DocumentConsolidated consolidated2 = saveSingleConsolidated();

        mvc.perform(MockMvcRequestBuilders.get("/api/document")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(consolidated1.getDocumentMetadata().getId().toString()))
                .andExpect(jsonPath("$[1].id").value(consolidated2.getDocumentMetadata().getId().toString()))
                .andExpect(status().isOk());

        assertThat(documentMetadataRepository.count()).isEqualTo(2);
        assertThat(documentBreakdownRepository.count()).isEqualTo(2);
        assertThat(documentConsolidatedRepository.count()).isEqualTo(2);
    }

    @Test
    @WithMockUser
    public void deleteDocument() throws Exception{
        DocumentConsolidated consolidated = saveSingleConsolidated();

        mvc.perform(MockMvcRequestBuilders.delete("/api/document/" + consolidated.getId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(documentMetadataRepository.count()).isEqualTo(0);
        assertThat(documentBreakdownRepository.count()).isEqualTo(0);
        assertThat(documentConsolidatedRepository.count()).isEqualTo(0);
    }

    @Test
    @WithMockUser
    public void testGetDocumentNotFound() throws Exception{
        UUID uuid = UUID.randomUUID();

        mvc.perform(MockMvcRequestBuilders.get("/api/document/" + uuid.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testGetDocumentConsolidatedNotFound() throws Exception{
        UUID uuid = UUID.randomUUID();

        mvc.perform(MockMvcRequestBuilders.get("/api/document/" + uuid.toString() + "/consolidated"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void deleteDocumentNotFound() throws Exception{
        UUID uuid = UUID.randomUUID();

        mvc.perform(MockMvcRequestBuilders.delete("/api/document" + uuid.toString()))
                .andExpect(status().isNotFound());
    }

    private DocumentConsolidated saveSingleConsolidated(){
        DocumentConsolidated consolidated = RandomObjectFiller.buildRandomDocumentConsolidated();
        return documentConsolidatedRepository.save(consolidated);
    }
}
