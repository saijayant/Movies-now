package com.example.android.popularmoviesstageone.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by akshayshahane on 30/06/17.
 */

public class Trailer {
    @SerializedName("id")
    String id ;

    @SerializedName("key")
    String key ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
