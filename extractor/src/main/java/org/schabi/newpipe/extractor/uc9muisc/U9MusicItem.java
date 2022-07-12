package org.schabi.newpipe.extractor.uc9muisc;

import org.schabi.newpipe.extractor.InfoItem;

public class U9MusicItem extends InfoItem {
    private String description;
    private int videoCount = -1;
    public U9MusicItem(int serviceId, String url, String name) {
        super(InfoType.U9MUSIC, serviceId, url, name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(int videoCount) {
        this.videoCount = videoCount;
    }
}
