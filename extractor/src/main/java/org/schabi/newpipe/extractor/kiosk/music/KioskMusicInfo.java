package org.schabi.newpipe.extractor.kiosk.music;

/*
 * Created by Christian Schabesberger on 12.08.17.
 *
 * Copyright (C) Christian Schabesberger 2017 <chris.schabesberger@mailbox.org>
 * KioskInfo.java is part of NewPipe.
 *
 * NewPipe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPipe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.ListInfo;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.kiosk.KioskExtractor;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.utils.ExtractorHelper;

import java.io.IOException;

public final class KioskMusicInfo extends ListInfo<StreamInfoItem> {
    private KioskMusicInfo(final int serviceId, final ListLinkHandler linkHandler, final String name) {
        super(serviceId, linkHandler, name);
    }

    public static ListExtractor.InfoItemsPage<StreamInfoItem> getMoreItems(
            final StreamingService service, final String url, final Page page)
            throws IOException, ExtractionException {
        return service.getKioskMusicList(false).getExtractorByUrl(url, page).getPage(page);
    }

    public static KioskMusicInfo getInfo(final String url) throws IOException, ExtractionException {
        return getInfo(NewPipe.getServiceByUrl(url), url);
    }

    public static KioskMusicInfo getInfo(final StreamingService service, final String url)
            throws IOException, ExtractionException {
        final KioskExtractor extractor = service.getKioskMusicList(false).getExtractorByUrl(url, null);
        extractor.fetchPage();
        KioskMusicInfo info = getInfo(extractor);
        if (info.getRelatedItems().isEmpty()){
            final KioskExtractor extractor1 = service.getKioskMusicList(true).getExtractorByUrl(url, null);
            extractor1.fetchPage();
            return getInfo(extractor1);
        }else {
            return info;
        }
    }

    /**
     * Get KioskInfo from KioskExtractor
     *
     * @param extractor an extractor where fetchPage() was already got called on.
     */
    public static KioskMusicInfo getInfo(final KioskExtractor extractor) throws ExtractionException {

        final KioskMusicInfo info = new KioskMusicInfo(extractor.getServiceId(),
                extractor.getLinkHandler(),
                extractor.getName());

        final ListExtractor.InfoItemsPage<StreamInfoItem> itemsPage
                = ExtractorHelper.getItemsPageOrLogError(info, extractor);
        info.setRelatedItems(itemsPage.getItems());
        info.setNextPage(itemsPage.getNextPage());

        return info;
    }
}
