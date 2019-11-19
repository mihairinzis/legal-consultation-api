package com.code4ro.legalconsultation.service.impl.pdf;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BoldAreasRepository {
    private List<String> boldAreas = new ArrayList<>();

    public void setBoldAreas(final List<String> boldAreas) {
        this.boldAreas = boldAreas;
    }

    public String getMatchingBoldArea(final String text) {
        return this.boldAreas.stream()
                .filter(boldArea -> boldArea.equalsIgnoreCase(text.trim()))
                .findFirst()
                .orElse(getBoldAreaStartsWith(text));
    }

    private String getBoldAreaStartsWith(final String text) {
        return this.boldAreas.stream()
                .filter(boldArea -> boldArea.startsWith(text.trim()))
                .findFirst()
                .orElse(null);
    }
}
