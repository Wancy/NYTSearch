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

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wancy.nytsearch.R;
import com.wancy.nytsearch.adapter.ArticleAdapter;
import com.wancy.nytsearch.fragment.EditDialogFragment;
import com.wancy.nytsearch.model.Article;
import com.wancy.nytsearch.net.ArticleClient;
import com.wancy.nytsearch.net.MyJsonHandler;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity implements EditDialogFragment.EditDialogListener{

    private RecyclerView rvArticles;
    private ArticleAdapter articleAdapter;
    private ArrayList<Article> articles;
    private EditText etQuery;
    private Button btnSearch;
    private RequestParams params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

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
        rvArticles.setLayoutManager(new GridLayoutManager(this, 4));
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
        ArticleClient client = new ArticleClient();
        this.params = params;
        client.getArticles(this.params, new MyJsonHandler(articles, articleAdapter));
    }

    public void onArticleSearch() {
        String query = etQuery.getText().toString();
        //if (query == null || query.length() == 0) return;
        Toast.makeText(this, "Searching for " + query, Toast.LENGTH_LONG).show();

        ArticleClient client = new ArticleClient();
        this.params.put("q", query);

        client.getArticles(this.params, new MyJsonHandler(articles, articleAdapter));
    }
}
