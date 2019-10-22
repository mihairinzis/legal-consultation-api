package com.code4ro.legalconsultation.service;

import com.code4ro.legalconsultation.model.dto.DocumentConsolidatedDto;
import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import com.code4ro.legalconsultation.service.impl.DocumentConsolidatedService;
import com.code4ro.legalconsultation.service.impl.DocumentServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceTest {

    @Mock
    private DocumentConsolidatedService documentConsolidatedService;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Test
    public void getDocument(){
        final UUID uuid = UUID.randomUUID();
        when(documentConsolidatedService.getEntity(any(UUID.class))).thenReturn((new DocumentConsolidated()));

        documentService.fetchOne(uuid);

        verify(documentConsolidatedService).getEntity(uuid);
    }

    @Test
    public void getDocumentConsolidated(){
        final UUID uuid = UUID.randomUUID();
        when(documentConsolidatedService.getOne(any(UUID.class))).thenReturn(new DocumentConsolidatedDto());

        documentService.fetchOneConsolidated(uuid);

        verify(documentConsolidatedService).getOne(uuid);
    }

    @Test
    public void getAllDocuments(){
        documentService.fetchAll();

        verify(documentConsolidatedService).findAll();
    }

    @Test
    public void deleteDocument(){
        final UUID uuid = UUID.randomUUID();

        documentService.deleteById(uuid);

        verify(documentConsolidatedService).deleteById(uuid);
    }
}
