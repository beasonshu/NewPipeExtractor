package org.schabi.newpipe.extractor.uc9muisc;

import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.ListInfo;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelInfo;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.utils.ExtractorHelper;

import java.io.IOException;

public class U9MusicInfo extends ListInfo<U9MusicItem> {
    public U9MusicInfo(int serviceId, ListLinkHandler listUrlIdHandler, String name) {
        super(serviceId, listUrlIdHandler, name);
    }

    public static U9MusicInfo getInfo(final StreamingService service, final String url)
            throws IOException, ExtractionException {
        final U9MusicExtractor extractor = service.getU9MusicExtractor(url);
        extractor.fetchPage();
        return getInfo(extractor);
    }

    public static U9MusicInfo getInfo(final String url) throws IOException, ExtractionException {
        return getInfo(NewPipe.getServiceByUrl(url), url);
    }


    public static U9MusicInfo getInfo(final U9MusicExtractor extractor)
            throws IOException, ExtractionException {

        final int serviceId = extractor.getServiceId();
        final String name = extractor.getName();

        final U9MusicInfo info =
                new U9MusicInfo(serviceId, extractor.getLinkHandler(), name);
        info.setRelatedItems(extractor.getInitialPage().getItems());
        return info;
    }
}
