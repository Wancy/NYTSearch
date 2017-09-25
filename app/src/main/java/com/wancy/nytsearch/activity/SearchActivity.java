package com.wancy.nytsearch.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.wancy.nytsearch.R;
import com.wancy.nytsearch.adapter.ArticleAdapter;
import com.wancy.nytsearch.fragment.EditDialogFragment;
import com.wancy.nytsearch.model.Article;
import com.wancy.nytsearch.net.ArticleClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class SearchActivity extends AppCompatActivity implements EditDialogFragment.EditDialogListener{

    private RecyclerView rvArticles;
    private ArticleAdapter articleAdapter;
    private ArrayList<Article> articles;
    private EditText etQuery;
    private Button btnSearch;
    private RequestParams params;
    private ArticleClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        client = new ArticleClient();
        params = new RequestParams();
        params.put("api-key", "308a1e2428f64bbb97abb8c214ab9d1a");
        params.put("page", 0);

        rvArticles = (RecyclerView) findViewById(R.id.rvArticles);
        etQuery = (EditText) findViewById(R.id.etQuery);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onArticleSearch();
            }
        });

        articles = new ArrayList<>();
        articleAdapter = new ArticleAdapter(this, articles);
        rvArticles.setAdapter(articleAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        rvArticles.setLayoutManager(gridLayoutManager);
/*        rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore() {
                loadNextDataFromApi(page);
            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
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
    public void onFinishEditDialog(RequestParams params) {
        this.params = params;
        onArticleSearch();

    }

    public void onArticleSearch() {
        String query = etQuery.getText().toString();
        //if (query == null || query.length() == 0) return;
        Toast.makeText(this, "Searching for " + query, Toast.LENGTH_LONG).show();

        params.put("q", query);

        client.getArticles(params, new JsonHttpResponseHandler() {
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

    public void loadNextDataFromApi(int offset) {

    }
}
