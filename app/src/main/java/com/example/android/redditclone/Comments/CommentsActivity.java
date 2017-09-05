package com.example.android.redditclone.Comments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.redditclone.Accounts.CheckLogin;
import com.example.android.redditclone.Accounts.LoginActivity;
import com.example.android.redditclone.ExtractXML;
import com.example.android.redditclone.MainActivity;
import com.example.android.redditclone.R;
import com.example.android.redditclone.ReddtiFeed;
import com.example.android.redditclone.URLS;
import com.example.android.redditclone.model.Feed;
import com.example.android.redditclone.model.entry.Entry;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by shash on 9/4/2017.
 */

public class CommentsActivity extends AppCompatActivity{
    private static final String TAG = "CommentsActivity";
    URLS urls = new URLS();

    private static String postURL;
    private static String postThumbnailURL;
    private static String postTitle;
    private static String postAuthor;
    private static String postUpdated;
    private static String postId;
    private String modhash;
    private String username;
    private String cookie;

    private int defaultImage;
    private String currentFeed;
    private ListView mListView;
    private ArrayList<Comment> mComments;
    private ProgressBar mProgressBar;
    private TextView progressText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        mProgressBar = (ProgressBar) findViewById(R.id.commentsLoadingProgressBar);
        Log.d(TAG, "onCreate: Started.");
        setupToolBar();
        getSessionParams();
        mProgressBar.setVisibility(View.VISIBLE);
        progressText= (TextView) findViewById(R.id.ProgressText);
        setupImageLoader();
        initPost();
        init();
    }

    private void setupToolBar(){
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "onMenuItemClick: clicked menu item" + item);

                switch (item.getItemId()){
                    case R.id.navLogin:
                        Intent intent= new Intent(CommentsActivity.this, LoginActivity.class);
                        startActivity(intent);
                }
                return false;
            }
        });

    }

    private void init(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urls.BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        ReddtiFeed feedAPI = retrofit.create(ReddtiFeed.class);

        Call<Feed> call = feedAPI.getFeed(currentFeed);

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                //Log.d(TAG, "onResponse: feed: " + response.body().toString());
                Log.d(TAG, "onResponse: Server Response: " + response.toString());

                mComments = new ArrayList<Comment>();
                List<Entry> entrys = response.body().getEntries();
                for ( int i = 0; i < entrys.size(); i++){
                    ExtractXML extract = new ExtractXML(entrys.get(i).getContent(), "<div class=\"md\"><p>","</p>");
                    List<String> commentDetails = extract.start();
                    try{
                        mComments.add(new Comment(
                                commentDetails.get(0),
                                entrys.get(i).getAuthor().getName(),
                                entrys.get(i).getUpdated(),
                                entrys.get(i).getId()

                        ));
                    }catch (IndexOutOfBoundsException e){
                        mComments.add(new Comment(
                                "Error reading comment",
                                "None",
                                "None",
                                "None"
                        ));
                        Log.e(TAG, "onResponse: IndexOutOfBoundsException: " + e.getMessage() );
                    }
                    catch (NullPointerException e){
                        mComments.add(new Comment(
                                commentDetails.get(0),
                                "None",
                                entrys.get(i).getUpdated(),
                                entrys.get(i).getId()

                        ));
                        Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage() );
                    }
                }
                mListView = (ListView) findViewById(R.id.comments_list_view);
                CommentsListAdapter adapter = new CommentsListAdapter(CommentsActivity.this, R.layout.comments_layout, mComments);
                mListView.setAdapter(adapter);

                mProgressBar.setVisibility(View.GONE);
                progressText.setText("");

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        getUserComment(postId);
                    }
                });
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.getMessage() );
                Toast.makeText(CommentsActivity.this, "An Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initPost(){
        Intent incomingIntent = getIntent();
        postURL = incomingIntent.getStringExtra("@string/post_url");
        postThumbnailURL = incomingIntent.getStringExtra("@string/post_thumbnail");
        postTitle = incomingIntent.getStringExtra("@string/post_title");
        postAuthor = incomingIntent.getStringExtra("@string/post_author");
        postUpdated = incomingIntent.getStringExtra("@string/post_updated");
        postId= incomingIntent.getStringExtra("@string/post_id");

        TextView title = (TextView) findViewById(R.id.post_title);
        TextView author = (TextView) findViewById(R.id.post_author);
        TextView updated = (TextView) findViewById(R.id.post_updated);
        ImageView thumbnail = (ImageView) findViewById(R.id.post_thumbNail);
        Button btnReply = (Button) findViewById(R.id.button_reply);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.postLoadingProgressBar);

        title.setText(postTitle);
        author.setText(postAuthor);
        updated.setText(postUpdated);
        displayImage(postThumbnailURL, thumbnail, progressBar);

        //NOTE: NSFW posts will cause an error. We can catch it with ArrayIndexOutOfBoundsException
        try{
            String[] splitURL = postURL.split(urls.BASE_URL);
            currentFeed = splitURL[1];
            Log.d(TAG, "initPost: current feed: " + currentFeed);
        }catch (ArrayIndexOutOfBoundsException e){
            Log.e(TAG, "initPost: ArrayIndexOutOfBoundsException: " + e.getMessage() );
        }

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: reply ");
                getUserComment(postId);
            }
        });

        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: WebIntent here: " + postURL);
                Intent intent= new Intent(CommentsActivity.this,WebViewActivity.class);
                intent.putExtra("url", postURL);
                startActivity(intent);
            }
        });

    }

    private void getUserComment(final String post_id){
        final Dialog dialog= new Dialog(CommentsActivity.this);
        dialog.setTitle("Post Your Comment");
        dialog.setContentView(R.layout.comment_input_dialog);

        int width= (int)(getResources().getDisplayMetrics().widthPixels*0.95);
        int height= (int)(getResources().getDisplayMetrics().heightPixels*0.60);
        dialog.getWindow().setLayout(width,height);
        dialog.show();

        Button btnPostComment= (Button) dialog.findViewById(R.id.btn_post_comment);
        final EditText comment= (EditText) dialog.findViewById(R.id.dialogComment);

        btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Post Comment Attemp");
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(urls.COMMENT_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ReddtiFeed feedAPI = retrofit.create(ReddtiFeed.class);

                HashMap<String, String> headerMap = new HashMap<>();
                headerMap.put("User-Agent", username);
                headerMap.put("X-Modhash", modhash);
                headerMap.put("cookie", "reddit_session=" + cookie);

                Log.d(TAG, "btnPostComment:  \n" +
                        "username: " + username + "\n" +
                        "modhash: " + modhash + "\n" +
                        "cookie: " + cookie + "\n"
                );


                String myComment= comment.getText().toString();
                Call<CheckComment> call = feedAPI.submitComment(headerMap,"comment",post_id, myComment);
                call.enqueue(new Callback<CheckComment>() {
                    @Override
                    public void onResponse(Call<CheckComment> call, Response<CheckComment> response) {
                        try{
                            //Log.d(TAG, "onResponse: feed: " + response.body().toString());
                            Log.d(TAG, "onResponse: Server Response: " + response.toString());

                            String postSuccess= response.body().getSuccess();
                            if (postSuccess.equals("true")){
                                dialog.dismiss();
                                Toast.makeText(CommentsActivity.this,"Comment Submitted Successfully",Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(CommentsActivity.this, "An Error Occured! Did you sign IN?", Toast.LENGTH_SHORT).show();
                            }

                        }catch (NullPointerException e){
                            Log.e(TAG, "onResponse: Null Pointer Exveption "+e.getMessage() );
                        }
                    }
                    @Override
                    public void onFailure(Call<CheckComment> call, Throwable t) {
                        Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.getMessage() );
                        Toast.makeText(CommentsActivity.this, "An Error Occured", Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });
    }

    private void getSessionParams(){
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(CommentsActivity.this);
        username= sharedPreferences.getString("@string/SessionUsername","");
        modhash= sharedPreferences.getString("@string/SessionModhash","");
        cookie= sharedPreferences.getString("@string/SessionCookie","");

        Log.d(TAG, "getSessionParams: Stored Session Params: \n" +
                "username: " + username+ "\n"+
                "modhash: " +  modhash+ "\n" +
                "cookie " +  cookie+ "\n");
    }

    private void displayImage(String imageURL, ImageView imageView, final ProgressBar progressBar){

        //create the imageloader object
        ImageLoader imageLoader = ImageLoader.getInstance();

        //create display options
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage).build();

        //download and display image from url
        imageLoader.displayImage(imageURL, imageView, options , new ImageLoadingListener() {
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

    /**
     * Required for setting up the Universal Image loader Library
     */
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu,menu);
        return true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "onPostResume: Session Param Called");
        getSessionParams();
    }


}
