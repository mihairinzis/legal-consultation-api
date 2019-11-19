package com.code4ro.legalconsultation.service.impl.pdf.reader;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

@Component
public class PDFBoldTextStripperByArea extends PDFTextStripper {
    private final List<String> regions = new ArrayList<>();
    private final Map<String, Rectangle2D> regionArea = new HashMap<>();
    private final Map<String, ArrayList<List<TextPosition>>> regionCharacterList = new HashMap<>();
    private final Map<String, StringWriter> regionText = new HashMap<>();
    private final List<String> boldAreas = new ArrayList<>();
    private final BoldTextSelectionParser boldTextSelectionParser;

    private boolean wasBoldCharacter = false;
    private StringBuilder currentBoldAreaBuilder = new StringBuilder();

    @Autowired
    public PDFBoldTextStripperByArea(final BoldTextSelectionParser boldTextSelectionParser) throws IOException {
        this.boldTextSelectionParser = boldTextSelectionParser;
        super.setShouldSeparateByBeads(false);
    }

    @Override
    public final void setShouldSeparateByBeads(boolean aShouldSeparateByBeads) {
    }

    public void addRegion(String regionName, Rectangle2D rect) {
        regions.add(regionName);
        regionArea.put(regionName, rect);
    }

    public void removeRegion(String regionName) {
        regions.remove(regionName);
        regionArea.remove(regionName);
    }

    public List<String> getRegions() {
        return regions;
    }

    public String getTextForRegion(String regionName) {
        StringWriter text = regionText.get(regionName);
        return text.toString();
    }

    public void extractRegions(PDPage page) throws IOException {
        for (String region : regions) {
            setStartPage(getCurrentPageNo());
            setEndPage(getCurrentPageNo());
            //reset the stored text for the region so this class
            //can be reused.
            String regionName = region;
            ArrayList<List<TextPosition>> regionCharactersByArticle = new ArrayList<>();
            regionCharactersByArticle.add(new ArrayList<>());
            regionCharacterList.put(regionName, regionCharactersByArticle);
            regionText.put(regionName, new StringWriter());
        }

        if (page.hasContents()) {
            processPage(page);
        }
    }

    @Override
    protected void processTextPosition(TextPosition text) {
        for (String region : regionArea.keySet()) {
            Rectangle2D rect = regionArea.get(region);
            if (rect.contains(text.getX(), text.getY())) {
                charactersByArticle = regionCharacterList.get(region);
                processForBold(text);
                super.processTextPosition(text);
            }
        }
    }

    private void processForBold(final TextPosition text) {
        if (text.getFont().getFontDescriptor() != null) {
            if (text.getFont().getName().contains("Bold") && wasBoldCharacter) {
                currentBoldAreaBuilder.append(text.toString());
                wasBoldCharacter = true;
            } else if (text.getFont().getName().contains("Bold") && !wasBoldCharacter) {
                currentBoldAreaBuilder = new StringBuilder();
                currentBoldAreaBuilder.append(text.toString());
                wasBoldCharacter = true;
            } else {
                if (currentBoldAreaBuilder.length() != 0 && wasBoldCharacter) {
                    final Set<String> breakdown = boldTextSelectionParser.handle(currentBoldAreaBuilder.toString().trim());
                    boldAreas.addAll(breakdown);
                }
                wasBoldCharacter = false;
            }
        }
    }

    @Override
    protected void writePage() throws IOException {
        for (String region : regionArea.keySet()) {
            charactersByArticle = regionCharacterList.get(region);
            output = regionText.get(region);
            super.writePage();
        }
    }

    public List<String> getBoldAreas() {
        return boldAreas;
    }
}
