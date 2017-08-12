package com.example.android.redditclone;

import com.example.android.redditclone.model.Feed;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by shash on 8/11/2017.
 */

public interface ReddtiFeed  {
     String BASE_URL="https://www.reddit.com/r/";

    @GET("earthporn/.rss")
    Call<Feed> getFeed();


}
