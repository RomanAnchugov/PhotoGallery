package ru.photogallery.romananchugov.photogallery;

/**
 * Created by romananchugov on 27.12.2017.
 */

public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mUri;

    @Override
    public String toString() {
        return mCaption;
    }

    public String getmCaption() {
        return mCaption;
    }

    public void setmCaption(String mCaption) {
        this.mCaption = mCaption;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmUri() {
        return mUri;
    }

    public void setmUri(String mUri) {
        this.mUri = mUri;
    }
}
