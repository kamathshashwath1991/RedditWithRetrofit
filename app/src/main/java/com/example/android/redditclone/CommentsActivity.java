package com.example.android.redditclone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

/**
 * Created by shash on 9/4/2017.
 */

public class CommentsActivity extends AppCompatActivity{

    private static String postURL;
    private static String postThumbnailURL;
    private static String postTitle;
    private static String postAuthor;
    private static String postUpdated;
    private int defaultImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        setupImageLoader();
        setInitPost();
    }
    private void setInitPost(){
        Intent incomingIntent= getIntent();
        postURL= incomingIntent.getStringExtra("@string/post_url");
        postThumbnailURL= incomingIntent.getStringExtra("@string/post_thumbnail");
        postTitle= incomingIntent.getStringExtra("@string/post_title");
        postAuthor= incomingIntent.getStringExtra("@string/post_author");
        postUpdated= incomingIntent.getStringExtra("@string/post_updated");

        TextView title= (TextView) findViewById(R.id.post_title);
        TextView author= (TextView) findViewById(R.id.post_author);
        TextView updated= (TextView) findViewById(R.id.post_updated);
        ImageView thumbnail= (ImageView) findViewById(R.id.post_thumbNail);
        Button btnReply= (Button) findViewById(R.id.button_reply);
        ProgressBar progressBar= (ProgressBar) findViewById(R.id.postLoadingProgressBar);

        title.setText(postTitle);
        author.setText(postAuthor);
        updated.setText(postUpdated);

        displayImage(postThumbnailURL,thumbnail,progressBar);
    }

    private void displayImage(String imageURL, ImageView imageView, final ProgressBar progressBar){
        ImageLoader imageLoader = ImageLoader.getInstance();



        //create display options
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage).build();

        //download and display image from url
        imageLoader.displayImage(imageURL,imageView, options , new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progressBar.setVisibility(View.GONE);
            }

        });
    }

    private void setupImageLoader(){
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                CommentsActivity.this)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP
        defaultImage = CommentsActivity.this.getResources().getIdentifier("@drawable/reddit_alien",null,CommentsActivity.this.getPackageName());
    }
}
