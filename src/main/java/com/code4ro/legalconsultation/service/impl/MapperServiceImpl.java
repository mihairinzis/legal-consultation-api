package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.model.dto.BaseEntityDto;
import com.code4ro.legalconsultation.model.dto.DocumentNodeDto;
import com.code4ro.legalconsultation.model.dto.DocumentViewDto;
import com.code4ro.legalconsultation.model.dto.UserDto;
import com.code4ro.legalconsultation.model.persistence.BaseEntity;
import com.code4ro.legalconsultation.model.persistence.DocumentMetadata;
import com.code4ro.legalconsultation.model.persistence.DocumentNode;
import com.code4ro.legalconsultation.model.persistence.User;
import com.code4ro.legalconsultation.service.api.MapperService;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapperServiceImpl implements MapperService, ApplicationListener<ContextRefreshedEvent> {
    private final ModelMapper modelMapper;

    public MapperServiceImpl() {
        this.modelMapper = new ModelMapper();

        addCustomMappings();
        addCustomTypeMaps();
    }

    private void addCustomMappings() {
        modelMapper.createTypeMap(BaseEntity.class, BaseEntityDto.class)
                .addMapping(BaseEntity::getId, BaseEntityDto::setId);
        modelMapper.createTypeMap(BaseEntityDto.class, BaseEntity.class)
                .addMapping(BaseEntityDto::getId, BaseEntity::setId);
    }

    private void addCustomTypeMaps() {
        modelMapper.createTypeMap(User.class, UserDto.class)
                .includeBase(BaseEntity.class, BaseEntityDto.class);
        modelMapper.createTypeMap(UserDto.class, User.class)
                .includeBase(BaseEntityDto.class, BaseEntity.class);
        modelMapper.createTypeMap(DocumentMetadata.class, DocumentViewDto.class);
        modelMapper.createTypeMap(DocumentViewDto.class, DocumentMetadata.class);
        modelMapper.createTypeMap(DocumentNode.class, DocumentNodeDto.class);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        final Collection<AbstractConverter> converters =
                event.getApplicationContext().getBeansOfType(AbstractConverter.class).values();
        converters.forEach(modelMapper::addConverter);
    }

    @Override
    public <T> T map(Object source, Class<T> targetType) {
        return source != null ? modelMapper.map(source, targetType) : null;
    }

    @Override
    public <T> List<T> mapList(List<?> sourceList, Class<? extends T> targetClass) {
        if (sourceList == null) {
            return Collections.emptyList();
        }

        return sourceList.stream()
                .map(listElement -> modelMapper.map(listElement, targetClass))
                .collect(Collectors.toList());
    }

    @Override
    public <T> Page<T> mapPage(Page<?> sourcePage, Class<? extends T> targetClass) {
        if (sourcePage == null) {
            return Page.empty();
        }

        return sourcePage.map(pageElement -> modelMapper.map(pageElement, targetClass));
    }
}
