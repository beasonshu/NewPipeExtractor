package org.schabi.newpipe.extractor.uc9muisc;

import org.schabi.newpipe.extractor.InfoItemsCollector;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;

import java.io.IOException;

public class U9MusicItemCollector extends InfoItemsCollector<U9MusicSectionItem, U9MusicExtractor> {
    public U9MusicItemCollector(int serviceId) {
        super(serviceId);
    }

    @Override
    public U9MusicSectionItem extract(U9MusicExtractor extractor) throws ExtractionException, IOException {
        U9MusicSectionItem u9MusicSectionItem = new U9MusicSectionItem(getServiceId(), extractor.getUrl(), extractor.getName());

        return u9MusicSectionItem;
    }



    @Override
    public void commit(final U9MusicExtractor extractor) {
        try {
            addItem(extract(extractor));
        } catch (final Exception e) {
            addError(e);
        }
    }
}
