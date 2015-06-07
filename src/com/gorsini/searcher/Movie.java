/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gorsini.searcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author gorsini
 */
public class Movie implements Serializable {

    public Movie(String title, String url, int searchType, ArrayList<String> excluded) {
        this.title = title;
        this.url = url;
        this.searchType = searchType;
        this.excludedTitles = excluded;
        this.results = null;
        this.resultDate = null;
    }
    public Date getResultDate() {
        return resultDate;
    }

    public ArrayList<String> getExcludedTitles() {
        return excludedTitles;
    }

    public void setExcludedTitles(ArrayList<String> excludedTitles) {
        this.excludedTitles = excludedTitles;
    }

    public ArrayList<Movie> getResults() {
        return results;
    }

    public void setResults(ArrayList<Movie> results) {
        
        
        if (results == null) {
            this.results = new ArrayList();
            this.resultDate = null;
        } else {
            this.results = results;
            this.resultDate = new Date();            
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSearchType() {
		return searchType;
	}
	public void setSearchType(int searchType) {
		this.searchType = searchType;
	}
	@Override
    public String toString() {
        return "Movie{" + "title=" + title + ", url=" + url + ", excludedTitles=" + excludedTitles + ", results=" + results + '}';
    }

	private String title;
	private String url;
	private int  searchType;
	private ArrayList<String> excludedTitles;
	private ArrayList<Movie> results;
	private Date resultDate = null;
}
