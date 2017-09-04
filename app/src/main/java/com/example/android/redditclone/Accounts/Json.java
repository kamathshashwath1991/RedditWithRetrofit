package com.example.android.redditclone.Accounts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shash on 9/4/2017.
 */

public class Json {

    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Json{" +
                "data=" + data +
                '}';
    }
}
