package ru.photogallery.romananchugov.photogallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by romananchugov on 27.12.2017.
 */

public class PhotoGalleryFragment extends Fragment{

    private RecyclerView mPhotoRecyclerView;
    public static final String TAG = "PhotoGalleryFragment";

    private List<GalleryItem> mItems = new ArrayList<>();

    private ThumbnailDownloader<PhotoHolder> mThumbnailDownLoader;

    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();

        Handler responseHandler = new Handler();
        mThumbnailDownLoader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownLoader.setThumbnailDownloaderListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
                Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                target.bindDrawable(drawable);
            }
        });
        mThumbnailDownLoader.start();
        mThumbnailDownLoader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mPhotoRecyclerView.addOnScrollListener(new RecyclerEndListener());
        setupAdapter();
        return v;
    }

    private void setupAdapter(){
        if(isAdded()){
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownLoader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>>{

        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
            return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems.addAll(galleryItems);

            if(FlickrFetchr.PAGE == 1) {
                setupAdapter();
            }else{
                mPhotoRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder{
        private ImageView mItemImageView;

        public PhotoHolder(View itemView){
            super(itemView);
            mItemImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
        }

        public void bindDrawable(Drawable drawable){
            mItemImageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> items){
            mGalleryItems = items;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.gallery_item, parent, false);
            return new PhotoHolder(v);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            Drawable placeHolder = getResources().getDrawable(R.drawable.bill_up_close);
            holder.bindDrawable(placeHolder);
            mThumbnailDownLoader.queueThumbnail(holder, galleryItem.getmUri());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class RecyclerEndListener extends RecyclerView.OnScrollListener{
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager()  ;
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int pastVisibleItems = layoutManager.findFirstCompletelyVisibleItemPosition();

            if(pastVisibleItems+visibleItemCount >= totalItemCount){
                FlickrFetchr.PAGE++;
                new FetchItemsTask().execute();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownLoader.clearQueue();
    }
}
