package com.wancy.nytsearch.adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wancy.nytsearch.R;
import com.wancy.nytsearch.activity.ArticleActivity;
import com.wancy.nytsearch.model.Article;

import java.util.ArrayList;
import java.util.List;


public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private List<Article> mArticles;
    private Context mContext;
    private Article mArticle;
    private OnItemClickListener listener;

    public ArticleAdapter(Context context, ArrayList<Article> articles) {
        this.mContext = context;
        this.mArticles = articles;
        this.listener = new OnItemClickListener() {
            @Override
            public void onItemClick(Article article) {
                Intent intent = new Intent(mContext, ArticleActivity.class);
                intent.putExtra("article", mArticle);
                mContext.startActivity(intent);
            }
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View articleView = inflater.inflate(R.layout.item_article_result, parent, false);

        // Return a new holder instance
        ArticleAdapter.ViewHolder viewHolder = new ArticleAdapter.ViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        this.mArticle = mArticles.get(position);
        viewHolder.tvTitle.setText(mArticle.getHeadline());
        viewHolder.ivImage.setImageResource(0);
        Glide.with(mContext)
                .load(Uri.parse(mArticle.getThumbNail()))
                .placeholder(R.drawable.ic_nocover)
                .into(viewHolder.ivImage);
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView ivImage;
        public TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(mArticle);
        }
    }



    public interface OnItemClickListener {
        void onItemClick(Article article);
    }
}
