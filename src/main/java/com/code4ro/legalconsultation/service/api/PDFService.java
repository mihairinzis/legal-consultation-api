package com.code4ro.legalconsultation.service.api;

import org.springframework.web.multipart.MultipartFile;

public interface PDFService {
    String readAsString(MultipartFile file);
}
