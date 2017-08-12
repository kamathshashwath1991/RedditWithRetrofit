package com.example.android.redditclone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.redditclone.model.Feed;
import com.example.android.redditclone.model.entry.Entry;

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
                Log.d(TAG, "onResponse: author" + entries.get(0).getAuthor());
                Log.d(TAG, "onResponse: updated"+ entries.get(0).getUpdated());
                Log.d(TAG, "onResponse: title"+ entries.get(0).getTitle());
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: unable to retrieve RSS"+ t.getMessage() );
                Toast.makeText(MainActivity.this, "an error occured", Toast.LENGTH_LONG).show();
            }
        });

    }
}
