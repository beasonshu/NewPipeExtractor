package org.schabi.newpipe.extractor.uc9muisc;

import org.schabi.newpipe.extractor.InfoItem;

import java.util.ArrayList;
import java.util.List;

public class U9MusicSectionItem extends InfoItem {
    private final List<U9MusicItem> itemList = new ArrayList<>();
    public U9MusicSectionItem(int serviceId, String url, String name) {
        super(InfoType.U9MUSIC, serviceId, url, name);
    }


}
