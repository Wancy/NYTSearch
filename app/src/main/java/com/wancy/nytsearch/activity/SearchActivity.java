package com.wancy.nytsearch.activity;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wancy.nytsearch.EndlessRecyclerViewScrollListener;
import com.wancy.nytsearch.R;
import com.wancy.nytsearch.adapter.ArticleAdapter;
import com.wancy.nytsearch.fragment.EditDialogFragment;
import com.wancy.nytsearch.model.Article;
import com.wancy.nytsearch.net.ArticleClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;


public class SearchActivity extends AppCompatActivity implements EditDialogFragment.EditDialogListener{

    private RecyclerView rvArticles;
    private ArticleAdapter articleAdapter;
    private ArrayList<Article> articles;
    private EditText etQuery;
    private Button btnSearch;
    private HashMap<String, String> params;
    private ArticleClient client;
    private int page;
    private EndlessRecyclerViewScrollListener scrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        client = new ArticleClient();
        params = new HashMap<>();
        //params.put("api-key", "308a1e2428f64bbb97abb8c214ab9d1a");
        page = 0;
        params.put("page", String.valueOf(page));

        rvArticles = (RecyclerView) findViewById(R.id.rvArticles);
/*        etQuery = (EditText) findViewById(R.id.etQuery);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onArticleSearch(true);
            }
        });*/

        articles = new ArrayList<>();
        articleAdapter = new ArticleAdapter(this, articles);
        rvArticles.setAdapter(articleAdapter);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4, 1);
        rvArticles.setLayoutManager(staggeredGridLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi();
            }
        };
        rvArticles.addOnScrollListener(scrollListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onArticleSearch(true);
                params.put("q", query);
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfstatement
        if (id == R.id.action_settings) {
            showEditDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showEditDialog() {
        EditDialogFragment editDialogFragment = EditDialogFragment.newInstance(params);
        editDialogFragment.show(getSupportFragmentManager(), "edit_dialog");
    }

    @Override
    public void onFinishEditDialog(HashMap<String, String> params) {
        this.params = params;
        onArticleSearch(true);

    }

    public void onArticleSearch(final Boolean isNewSearch) {
        String url = "";
        for (String key : params.keySet()) {
            String paramString = params.get(key).trim();
            if (!paramString.equals("") && !paramString.equals("?")) {
                url += "&" + key;
                url += "=" + paramString;
            }
        }

        client.getArticles(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;

                try{
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    if (isNewSearch) {
                        articles.clear();
                        scrollListener.resetState();
                    }
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    articleAdapter.notifyDataSetChanged();
                    // record this value before making any changes to the existing list
                    int curSize = articleAdapter.getItemCount();
                    articleAdapter.notifyItemRangeInserted(curSize, articles.size());

                    Log.d("DEBUG", articles.toString());
                    rvArticles.invalidate();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }

    public void loadNextDataFromApi() {
        page++;
        params.put("page", String.valueOf(page));
        onArticleSearch(false);
    }
}
