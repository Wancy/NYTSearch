package com.wancy.nytsearch.net;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.wancy.nytsearch.adapter.ArticleAdapter;
import com.wancy.nytsearch.model.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class MyJsonHandler extends JsonHttpResponseHandler {
    private ArticleAdapter articleAdapter;
    private ArrayList<Article> articles;
    public MyJsonHandler(ArrayList<Article> articles, ArticleAdapter articleAdapter) {
        this.articleAdapter = articleAdapter;
        this.articles = articles;
    }
    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.d("DEBUG", response.toString());
        JSONArray articleJsonResults = null;

        try{
            articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
            articles.clear();
            articles.addAll(Article.fromJSONArray(articleJsonResults));
            // record this value before making any changes to the existing list
            int curSize = articleAdapter.getItemCount();
            articleAdapter.notifyItemRangeInserted(curSize, articles.size());
            Log.d("DEBUG", articles.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        super.onFailure(statusCode, headers, responseString, throwable);
    }
}
