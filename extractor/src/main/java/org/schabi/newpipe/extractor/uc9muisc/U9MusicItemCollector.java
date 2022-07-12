package org.schabi.newpipe.extractor.uc9muisc;

import org.schabi.newpipe.extractor.InfoItemsCollector;
import org.schabi.newpipe.extractor.comments.CommentsInfoItem;
import org.schabi.newpipe.extractor.comments.CommentsInfoItemExtractor;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class U9MusicItemCollector extends InfoItemsCollector<U9MusicItem, U9MusicExtractor> {
    public U9MusicItemCollector(int serviceId) {
        super(serviceId);
    }

    @Override
    public U9MusicItem extract(U9MusicExtractor extractor) throws ExtractionException, IOException {
        U9MusicItem u9MusicItem = new U9MusicItem(getServiceId(), extractor.getUrl(), extractor.getName());

        return u9MusicItem;
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
