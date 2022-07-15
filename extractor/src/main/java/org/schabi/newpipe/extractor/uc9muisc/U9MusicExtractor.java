package org.schabi.newpipe.extractor.uc9muisc;

import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.getJsonPostResponse;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.prepareDesktopJsonBuilder;
import static org.schabi.newpipe.extractor.utils.Utils.EMPTY_STRING;
import static org.schabi.newpipe.extractor.utils.Utils.isNullOrEmpty;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonWriter;

import org.schabi.newpipe.extractor.InfoItemExtractor;
import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ContentNotAvailableException;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeChannelLinkHandlerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

public  class U9MusicExtractor extends ListExtractor<U9MusicSectionItem> implements InfoItemExtractor {
    private JsonObject initialData;

    /**
     * Some channels have response redirects and the only way to reliably get the id is by saving it
     * <p>
     * "Movies & Shows":
     * <pre>
     * UCuJcl0Ju-gPDoksRjK1ya-w ┐
     * UChBfWrfBXL9wS6tQtgjt_OQ ├ UClgRkhTL3_hImCAmdLfDE4g
     * UCok7UTQQEP1Rsctxiv3gwSQ ┘
     * </pre>
     */
    private String redirectedChannelId;

    public U9MusicExtractor(final StreamingService service,
                                        final ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader) throws IOException,
            ExtractionException {
        final String channelPath = super.getId();
        final String[] channelId = channelPath.split("/");
        String id = "";
        // If the url is an URL which is not a /channel URL, we need to use the
        // navigation/resolve_url endpoint of the InnerTube API to get the channel id. Otherwise,
        // we couldn't get information about the channel associated with this URL, if there is one.
        if (!channelId[0].equals("channel")) {
            final byte[] body = JsonWriter.string(prepareDesktopJsonBuilder(
                            getExtractorLocalization(), getExtractorContentCountry())
                            .value("url", "https://www.youtube.com/" + channelPath)
                            .done())
                    .getBytes(StandardCharsets.UTF_8);

            final JsonObject jsonResponse = getJsonPostResponse("navigation/resolve_url",
                    body, getExtractorLocalization());

            if (!isNullOrEmpty(jsonResponse.getObject("error"))) {
                final JsonObject errorJsonObject = jsonResponse.getObject("error");
                final int errorCode = errorJsonObject.getInt("code");
                if (errorCode == 404) {
                    throw new ContentNotAvailableException("This channel doesn't exist.");
                } else {
                    throw new ContentNotAvailableException("Got error:\""
                            + errorJsonObject.getString("status") + "\": "
                            + errorJsonObject.getString("message"));
                }
            }

            final JsonObject endpoint = jsonResponse.getObject("endpoint");

            final String webPageType = endpoint.getObject("commandMetadata")
                    .getObject("webCommandMetadata")
                    .getString("webPageType", EMPTY_STRING);

            final JsonObject browseEndpoint = endpoint.getObject("browseEndpoint");
            final String browseId = browseEndpoint.getString("browseId", EMPTY_STRING);

            if (webPageType.equalsIgnoreCase("WEB_PAGE_TYPE_BROWSE")
                    || webPageType.equalsIgnoreCase("WEB_PAGE_TYPE_CHANNEL")
                    && !browseId.isEmpty()) {
                if (!browseId.startsWith("UC")) {
                    throw new ExtractionException("Redirected id is not pointing to a channel");
                }

                id = browseId;
                redirectedChannelId = browseId;
            }
        } else {
            id = channelId[1];
        }
        JsonObject ajaxJson = null;

        int level = 0;
        while (level < 3) {
            final byte[] body = JsonWriter.string(prepareDesktopJsonBuilder(
                            getExtractorLocalization(), getExtractorContentCountry())
                            .value("browseId", id)
                            .value("params", "EgZ2aWRlb3M%3D") // Equal to videos
                            .done())
                    .getBytes(StandardCharsets.UTF_8);

            final JsonObject jsonResponse = getJsonPostResponse("browse", body,
                    getExtractorLocalization());

            if (!isNullOrEmpty(jsonResponse.getObject("error"))) {
                final JsonObject errorJsonObject = jsonResponse.getObject("error");
                final int errorCode = errorJsonObject.getInt("code");
                if (errorCode == 404) {
                    throw new ContentNotAvailableException("This channel doesn't exist.");
                } else {
                    throw new ContentNotAvailableException("Got error:\""
                            + errorJsonObject.getString("status") + "\": "
                            + errorJsonObject.getString("message"));
                }
            }

            final JsonObject endpoint = jsonResponse.getArray("onResponseReceivedActions")
                    .getObject(0)
                    .getObject("navigateAction")
                    .getObject("endpoint");

            final String webPageType = endpoint.getObject("commandMetadata")
                    .getObject("webCommandMetadata")
                    .getString("webPageType", EMPTY_STRING);

            final String browseId = endpoint.getObject("browseEndpoint").getString("browseId",
                    EMPTY_STRING);

            if (webPageType.equalsIgnoreCase("WEB_PAGE_TYPE_BROWSE")
                    || webPageType.equalsIgnoreCase("WEB_PAGE_TYPE_CHANNEL")
                    && !browseId.isEmpty()) {
                if (!browseId.startsWith("UC")) {
                    throw new ExtractionException("Redirected id is not pointing to a channel");
                }

                id = browseId;
                redirectedChannelId = browseId;
                level++;
            } else {
                ajaxJson = jsonResponse;
                break;
            }
        }

        if (ajaxJson == null) {
            throw new ExtractionException("Could not fetch initial JSON data");
        }

        initialData = ajaxJson;
        YoutubeParsingHelper.defaultAlertsCheck(initialData);
    }

    @Nonnull
    @Override
    public String getUrl() throws ParsingException {
        try {
            return YoutubeChannelLinkHandlerFactory.getInstance().getUrl("channel/" + getId());
        } catch (final ParsingException e) {
            return super.getUrl();
        }
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        return null;
    }

    @Override
    public String getBigCoverUrl() throws ParsingException {
        return null;
    }

    @Nonnull
    @Override
    public String getId() throws ParsingException {
        final String channelId = initialData.getObject("header")
                .getObject("carouselHeaderRenderer")
                .getArray("contents").getObject(1)
                .getObject("topicChannelDetailsRenderer")
                .getObject("subscribeButton")
                .getObject("subscribeButtonRenderer")
                .getString("channelId", EMPTY_STRING);

        if (!channelId.isEmpty()) {
            return channelId;
        } else if (!isNullOrEmpty(redirectedChannelId)) {
            return redirectedChannelId;
        } else {
            return "";
        }
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException{
        try {
            return initialData.getObject("header")
                    .getObject("carouselHeaderRenderer")
                    .getArray("contents").getObject(1)
                    .getObject("topicChannelDetailsRenderer")
                    .getString("title");
        } catch (final Exception e) {
//            throw new ParsingException("Could not get channel name", e);
            return "music";
        }

    }




    @Nonnull
    @Override
    public InfoItemsPage<U9MusicSectionItem> getInitialPage() throws IOException, ExtractionException {
        U9MusicItemCollector collector = new U9MusicItemCollector(getServiceId());
        final JsonArray itemSectionRenderers = initialData.getObject("contents")
                .getObject("twoColumnBrowseResultsRenderer").getArray("tabs").getObject(0)
                .getObject("tabRenderer").getObject("content").getObject("sectionListRenderer")
                .getArray("contents");
        for (Object itemSection:itemSectionRenderers) {
//            collector.commit();
        }

        return InfoItemsPage.emptyPage();
    }

    @Override
    public InfoItemsPage<U9MusicSectionItem> getPage(final Page page) throws IOException,
            ExtractionException {
        return InfoItemsPage.emptyPage();
    }




}
