package com.wancy.nytsearch.net;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ArticleClient {
    private AsyncHttpClient client;
    private static final String API_BASE_URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

    public ArticleClient() {
        this.client = new AsyncHttpClient();
    }

    public void getArticles(RequestParams params, JsonHttpResponseHandler handler) {
        client.get(API_BASE_URL, params, handler);

    }

}
