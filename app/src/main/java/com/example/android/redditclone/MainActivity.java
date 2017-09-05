package com.example.android.redditclone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.redditclone.Accounts.LoginActivity;
import com.example.android.redditclone.Comments.CommentsActivity;
import com.example.android.redditclone.model.Feed;
import com.example.android.redditclone.model.entry.Entry;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    URLS urls= new URLS();

    private Button btnRefresh;
    private EditText mFeedName;
    private String currentFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRefresh= (Button) findViewById(R.id.feed_refresh);
        mFeedName= (EditText) findViewById(R.id.edit_feeds);
        setupToolBar();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedName= mFeedName.getText().toString();
                if (!mFeedName.equals(" ")){
                    currentFeed= feedName;
                    init();
                }
                else {
                    init();
                }
            }
        });

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
                        Intent intent= new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                }
                return false;
            }
        });

    }


    private void init() {
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(urls.BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        ReddtiFeed reddtiFeed= retrofit.create(ReddtiFeed.class);

        Call<Feed> call= reddtiFeed.getFeed(currentFeed);

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                Log.d(TAG, "onResponse: response body" + response.body().getEntries());
                Log.d(TAG, "onResponse: Server Response"+ response.toString());

                List<Entry> entries= response.body().getEntries();
                Log.d(TAG, "onResponse: entrys" + entries.get(0).getAuthor());
                Log.d(TAG, "onResponse: updated" + entries.get(0).getUpdated());
                Log.d(TAG, "onResponse: title" + entries.get(0).getTitle());

                final ArrayList<Post> posts= new ArrayList<Post>();

                for (int i=0; i<entries.size();i++){
                    ExtractXML extractXML1= new ExtractXML(entries.get(0).getContent(),"<a href=");
                    List<String> postContent= extractXML1.start();

                    ExtractXML extractXML2= new ExtractXML(entries.get(0).getContent(),"<img src=");
                    try{
                        postContent.add(extractXML2.start().get(0));
                    }catch (NullPointerException e){
                        postContent.add(null);
                    }catch (IndexOutOfBoundsException e){
                        postContent.add(null);
                    }
                    int lastPosition= postContent.size()-1;
                    try{
                        posts.add(new Post(
                                entries.get(i).getTitle(),
                                entries.get(i).getAuthor().getName(),
                                entries.get(i).getUpdated(),
                                postContent.get(0),
                                postContent.get(lastPosition),
                                entries.get(i).getId()
                        ));

                    }catch (NullPointerException e){
                        posts.add(new Post(
                                entries.get(i).getTitle(),
                                "None",
                                entries.get(i).getUpdated(),
                                postContent.get(0),
                                postContent.get(lastPosition),
                                entries.get(i).getId()
                        ));
                        Log.e(TAG, "onResponse: Null Pointer Exception "+ e.getMessage() );
                    }

                }
                for (int j=0; j<posts.size(); j++){
                    Log.d(TAG, "onResponse: \n" + "Post Url: " + posts.get(j).getPostURL()
                            + "\n" + "ThumbNail Url: " + posts.get(j).getThumbnailURL() + "\n"
                            + "Title: " + posts.get(j).getTitle()+ "\n" +
                            "Author: " + posts.get(j).getAuthor()+"\n"
                            + "Title: " + posts.get(j).getTitle()+ "\n"
                            + "Updated: " + posts.get(j).getDate_updated());
                }

                ListView listView= (ListView) findViewById(R.id.ListView);
                CustomListAdapter customListAdapter= new CustomListAdapter(MainActivity.this,R.layout.card_layout_main,posts);
                listView.setAdapter(customListAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(TAG, "onItemClick: Clicked: " + posts.get(position).toString());
                        Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
                        intent.putExtra("@string/post_url", posts.get(position).getPostURL());
                        intent.putExtra("@string/post_thumbnail", posts.get(position).getThumbnailURL());
                        intent.putExtra("@string/post_title", posts.get(position).getTitle());
                        intent.putExtra("@string/post_author", posts.get(position).getAuthor());
                        intent.putExtra("@string/post_updated", posts.get(position).getDate_updated());
                        intent.putExtra("@string/post_id",posts.get(position).getId());
                        startActivity(intent);
                    }
                });

            }
            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS " + t.getMessage());
                Toast.makeText(MainActivity.this,"Please enter SubReddit Section",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu,menu);
        return true;
    }


}
