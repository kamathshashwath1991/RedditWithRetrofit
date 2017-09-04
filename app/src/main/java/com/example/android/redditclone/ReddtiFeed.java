package com.example.android.redditclone;

import com.example.android.redditclone.Accounts.CheckLogin;
import com.example.android.redditclone.Comments.CheckComment;
import com.example.android.redditclone.model.Feed;

import org.simpleframework.xml.Path;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by shash on 8/11/2017.
 */

public interface ReddtiFeed  {

    String BASE_URL = "https://www.reddit.com/r/";

    //Non-static feed name
    @GET("{feed_name}/.rss")
    Call<Feed> getFeed(@retrofit2.http.Path("feed_name") String feed_name);

    @POST("{user}")
    Call<CheckLogin> signIn(
            @HeaderMap Map<String, String> headers,
            @retrofit2.http.Path("user") String username,
            @Query("user") String user,
            @Query("passwd") String password,
            @Query("api_type") String type
    );
    @POST("{comment}")
    Call<CheckComment> submitComment(
            @HeaderMap Map<String, String> headers,
            @retrofit2.http.Path("comment") String comment,
            @Query("parent") String parent,
            @Query("amp;text") String text
    );
}
