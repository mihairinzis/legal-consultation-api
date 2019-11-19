package com.code4ro.legalconsultation.service;

import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import com.code4ro.legalconsultation.model.persistence.DocumentMetadata;
import com.code4ro.legalconsultation.repository.DocumentConsolidatedRepository;
import com.code4ro.legalconsultation.service.api.MapperService;
import com.code4ro.legalconsultation.service.impl.DocumentConsolidatedService;
import com.code4ro.legalconsultation.util.DocumentNodeFactory;
import com.code4ro.legalconsultation.util.RandomObjectFiller;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentConsolidatedServiceTest {

    @Mock
    private DocumentConsolidatedRepository documentConsolidatedRepository;
    @Mock
    private MapperService mapperService;

    @InjectMocks
    private DocumentConsolidatedService documentConsolidatedService;

    private final DocumentNodeFactory documentNodeFactory = new DocumentNodeFactory();

    @Test
    public void getDocument(){
        final UUID uuid = UUID.randomUUID();
        when(documentConsolidatedRepository.findById(any(UUID.class))).thenReturn(Optional.of(new DocumentConsolidated()));

        documentConsolidatedService.getOne(uuid);
        verify(documentConsolidatedRepository).findById(uuid);
    }

    @Test
    public void getAllDocuments(){
        documentConsolidatedService.findAll();
        verify(documentConsolidatedRepository).findAll();
    }

    @Test
    public void saveDocument() {
        final DocumentMetadata documentMetadata = RandomObjectFiller.createAndFill(DocumentMetadata.class);
        DocumentConsolidated documentConsolidated =
                new DocumentConsolidated(documentMetadata, documentNodeFactory.create());
        documentConsolidatedService.saveOne(documentConsolidated);

        verify(documentConsolidatedRepository).save(documentConsolidated);
    }

    @Test
    public void deleteDocument(){
        final UUID id = UUID.randomUUID();
        documentConsolidatedService.deleteById(id);

        verify(documentConsolidatedRepository).deleteById(id);
    }
}
