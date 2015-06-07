/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gorsini.searcher;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author gorsini
 */
public class SearchData {

    public ArrayList<Movie> movies;

    private Date lastSearchDate;

    public Date getLastSearchDate() {
        return lastSearchDate;
    }

    public void setLastSearchDate(Date lastSearchDate) {
        this.lastSearchDate = lastSearchDate;
    }

    public SearchData() {
        this.movies = null;
        this.lastSearchDate = null;
    }
}
