package com.example.android.popularmoviesstageone.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by akshayshahane on 01/07/17.
 */

public class Reviews {

    @SerializedName("id")
    String id;

    @SerializedName("author")
    String author;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @SerializedName("content")
    String content;
}
