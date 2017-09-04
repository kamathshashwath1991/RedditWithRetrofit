package com.example.android.redditclone.Accounts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shash on 9/4/2017.
 */

public class CheckLogin {

    @SerializedName("json")
    @Expose
    private Json json;

    public Json getJson() {
        return json;
    }

    public void setJson(Json json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return "CheckLogin{" +
                "json=" + json +
                '}';
    }
}
