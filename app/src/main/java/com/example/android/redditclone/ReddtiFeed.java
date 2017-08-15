package com.example.android.redditclone;

import com.example.android.redditclone.model.Feed;

import org.simpleframework.xml.Path;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by shash on 8/11/2017.
 */

public interface ReddtiFeed  {
     String BASE_URL="https://www.reddit.com/r/";

    @GET("{feed_name}/.rss")
    Call<Feed> getFeed(@Path("feed_name") String feed_name);


}
