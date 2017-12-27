package ru.photogallery.romananchugov.photogallery;

/**
 * Created by romananchugov on 27.12.2017.
 */

public class GalleryItem {
    private String title;
    private String id;
    private String url_s;

    @Override
    public String toString() {
        return title;
    }

    public String getmCaption() {
        return title;
    }

    public void setmCaption(String mCaption) {
        this.title = mCaption;
    }

    public String getmId() {
        return id;
    }

    public void setmId(String mId) {
        this.id = mId;
    }

    public String getmUri() {
        return url_s;
    }

    public void setmUri(String mUri) {
        this.url_s = mUri;
    }
}
