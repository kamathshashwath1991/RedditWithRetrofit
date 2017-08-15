package com.example.android.redditclone;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by shash on 8/14/2017.
 */

public class CustomAdapter extends ArrayAdapter<Post> {

    private static final String TAG = "CustomAdapter";
    private Context mContext;
    private int mResource;
    private int lastPosition= -1;

    private static class ViewHolder{
        TextView title;
        TextView author;
        TextView date_updated;
        ProgressBar mProgressBar;
        ImageView thumbnailURL;
    }

    public CustomAdapter(Context applicationContext, int card_layout_main, ArrayList<Post> posts) {
        super(applicationContext,card_layout_main,posts);
        mContext= applicationContext;
        mResource= card_layout_main;

        setupImageLoader();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String title= getItem(position).getTitle();
        String imgUrl= getItem(position).getThumbnailURL();
        String author= getItem(position).getAuthor();
        String date_updated= getItem(position).getDate_updated();


        try{
            final View result;

            final ViewHolder holder;

            if (convertView==null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(mResource, parent, false);
                holder= new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.cardTitle);
                holder.thumbnailURL = (ImageView) convertView.findViewById(R.id.cardImage);
                holder.author= (TextView) convertView.findViewById(R.id.cardAuthor);
                holder.date_updated= (TextView) convertView.findViewById(R.id.cardUpdated);
                holder.mProgressBar= (ProgressBar) convertView.findViewById(R.id.ProgressDialog);


                result = convertView;

                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
                result = convertView;
            }


            lastPosition=position;

            holder.title.setText(title);
            holder.author.setText(author);
            holder.date_updated.setText(date_updated);


            ImageLoader imageLoader = ImageLoader.getInstance();

            int defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed",null,mContext.getPackageName());

            //create display options
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage)
                    .showImageOnLoading(defaultImage).build();

            //download and display image from url
            imageLoader.displayImage(imgUrl, holder.thumbnailURL, options,new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    holder.mProgressBar.setVisibility(View.VISIBLE);
                }
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    holder.mProgressBar.setVisibility(View.GONE);
                }
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.mProgressBar.setVisibility(View.GONE);
                }
                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    holder.mProgressBar.setVisibility(View.GONE);
                }

            });

            return convertView;
        }
        catch (IllegalArgumentException e){
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage() );
            return convertView;
        }
    }

    private void setupImageLoader(){
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP
    }
}
