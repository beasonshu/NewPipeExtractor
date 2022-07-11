package org.schabi.newpipe.extractor.uc9muisc;

import org.schabi.newpipe.extractor.InfoItemsCollector;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;

import java.io.IOException;

public class U9MusicItemCollector extends InfoItemsCollector<U9MusicItem, U9MusicExtractor> {
    public U9MusicItemCollector(int serviceId) {
        super(serviceId);
    }

    @Override
    public U9MusicItem extract(U9MusicExtractor extractor) throws ExtractionException, IOException {
        extractor.getInitialPage();
        return null;
    }
}
