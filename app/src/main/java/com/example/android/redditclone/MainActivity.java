package com.example.android.redditclone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

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
    private static final String BASE_URL="https://www.reddit.com/r/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        ReddtiFeed reddtiFeed= retrofit.create(ReddtiFeed.class);
        Call<Feed> call= reddtiFeed.getFeed();

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
               // Log.d(TAG, "onResponse: "+ response.body().getEntries());
                Log.d(TAG, "onResponse: response" + response.toString());

                List<Entry> entries= response.body().getEntries();
                Log.d(TAG, "onResponse: get entries"+ entries);

                ArrayList<Post> posts= new ArrayList<Post>();

                for (int i=0; i< entries.size();i++){
                    ExtractXML  extractXML1= new ExtractXML(entries.get(0).getContent(),"<a href=");
                    List<String> postContent= extractXML1.start();


                    ExtractXML  extractXML2= new ExtractXML(entries.get(0).getContent(),"<img src=");
                    try {
                        postContent.add(extractXML2.start().get(0));
                    }catch (NullPointerException e){
                        Log.e(TAG, "onResponse: Null Pointer exception" + e.getMessage() );
                        postContent.add(null);
                    }catch (IndexOutOfBoundsException e){
                        Log.e(TAG, "onResponse: Index Out of bounds exception "+ e.getMessage() );
                        postContent.add(null);
                    }

                    posts.add(new Post(
                       entries.get(0).getTitle(),
                            entries.get(0).getAuthor().getName(),
                            entries.get(0).getUpdated(),
                            postContent.get(0),
                            postContent.get(postContent.size()-1)
                    ));
                }
                for (int j=0; j <posts.size(); j++){
                    Log.d(TAG, "onResponse:  \n"+
                                    "Post Url"+ posts.get(j).getPostUrl() + "\n" +
                                    "ThumbNail: " + posts.get(j).getThumbnailURL() + "\n" +
                                    "Titlte" + posts.get(j).getTitle() +"\n" +
                                            "Author" + posts.get(j).getAuthor()+ "\n" +
                                            "updated" +posts.get(j).getDate_updated()
                    );
                }

            }



            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: unable to retrieve RSS"+ t.getMessage() );
                Toast.makeText(MainActivity.this, "an error occured", Toast.LENGTH_LONG).show();
            }
        });

    }
}
