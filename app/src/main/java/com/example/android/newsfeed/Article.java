package com.example.android.newsfeed;

import java.util.ArrayList;

/**
 * Created by Younes on 24/06/2017.
 */

public class Article {

    /** Section to which the article belongs */
    private String mSection;

    /** Title of the article */
    private String mTitle;

    /** Author(s) of the article */
    private ArrayList<String> mAuthors;

    /** Publication date of the article */
    private long mDateTime;

    /** Link for the article */
    private String mLink;

    /** Thumbnail URL for the article */
    private String mThumbnail;

    /**
     * Create a new article object with the following parameters
     *
     * @param Section is the section to which the article belongs
     * @param Title is the title of the article
     * @param Authors is the author of the article
     * @param DateTime is the date in which the article has been published
     * @param Link is the link to the web page of the article
     * @param Thumbnail is the link to the web page of the article
     */
    public Article(String Section, String Title, ArrayList<String> Authors, long DateTime, String Link, String Thumbnail) {
        mSection = Section;
        mTitle = Title;
        mAuthors = Authors;
        mDateTime = DateTime;
        mLink = Link;
        mThumbnail = Thumbnail;
    }

    /**
     * Get the section to which the article belongs
     */
    public String getSection() { return mSection; }

    /**
     * Get the title of the article
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Get the list of authors of the article
     */
    public ArrayList<String> getAuthors() {
        return mAuthors;
    }

    /**
     * Get the published date of the article
     */
    public long getDateTime() {
        return mDateTime;
    }

    /**
     * Get the URL of the article
     */
    public String getLink() {
        return mLink;
    }

    /**
     * Get the thumbnail URL of the article
     */
    public String getThumbnail() {
        return mThumbnail;
    }
}
