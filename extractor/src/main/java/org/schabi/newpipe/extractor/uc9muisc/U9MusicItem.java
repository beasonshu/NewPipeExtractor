package org.schabi.newpipe.extractor.uc9muisc;

import org.schabi.newpipe.extractor.InfoItem;

import java.io.Serializable;

public class U9MusicItem implements Serializable {
    private String description;
    private int videoCount = -1;
    private String title;
    private String thumbUrl;


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
