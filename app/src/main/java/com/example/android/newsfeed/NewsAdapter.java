package com.example.android.newsfeed;

import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Younes on 24/06/2017.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private List<Article> articlesList = new ArrayList<>();
    private int rawLayout;
    private Context mContext;
    private ItemClickListener clickListener;

    public NewsAdapter(List<Article> articlesList, int rawLayout, Context context) {
        this.articlesList = articlesList;
        this.rawLayout = rawLayout;
        this.mContext = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView thumbnailImageView;
        private TextView sectionTextView, titleTextView, dateTextView, authorsTextView;
        private MyViewHolder(View view) {
            super(view);
            thumbnailImageView = (ImageView) view.findViewById(R.id.article_thumbnail);
            sectionTextView = (TextView) view.findViewById(R.id.article_section);
            titleTextView = (TextView) view.findViewById(R.id.article_title);
            authorsTextView = (TextView) view.findViewById(R.id.article_authors);
            dateTextView = (TextView) view.findViewById(R.id.article_date);
            // Attach a click listener to the entire row view
            view.setOnClickListener(this);
        }
        // Handles the row being being clicked
        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Article currentArticle = articlesList.get(position);

        if (!currentArticle.getThumbnail().isEmpty()) {
            Picasso.with(mContext).load(currentArticle.getThumbnail()).into(holder.thumbnailImageView);
        }
        else { holder.thumbnailImageView.setImageResource(R.drawable.img_default_thumbnail); }
        holder.sectionTextView.setText(currentArticle.getSection());
        holder.titleTextView.setText(currentArticle.getTitle());

        if (currentArticle.getAuthors().size() > 0) {
            StringBuilder authorsBuilder = new StringBuilder();
            boolean first = true;
            for (int i = 0; i < currentArticle.getAuthors().size(); i++) {
                if (first) {
                    first = false;
                } else {
                    authorsBuilder.append(System.getProperty("line.separator"));
                }
                authorsBuilder.append(currentArticle.getAuthors().get(i));
            }
            holder.authorsTextView.setText(authorsBuilder.toString());
        }
        else {
            holder.authorsTextView.setVisibility(View.GONE);
        }

        Date dateTimeObject = new Date (currentArticle.getDateTime());
        String formattedDateTime = formatDateTime(dateTimeObject);
        holder.dateTextView.setText(formattedDateTime);
    }

    /**
     * Helper method to return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDateTime(Date dateObject) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("LLL dd, yyyy, h:mm a");
        return dateTimeFormat.format(dateObject);
    }

    @Override
    public int getItemCount() {
        if (articlesList == null) {
            return 0;
        } else { return articlesList.size(); }
    }

    // Helper method to set the actual article list into the recyclerview on the activity
    public void setArticleInfoList(List<Article> articlesList) {
        this.articlesList = articlesList;
    }
}
