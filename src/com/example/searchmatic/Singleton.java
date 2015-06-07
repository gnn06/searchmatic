package com.example.searchmatic;

import com.gorsini.searcher.SearchData;

public class Singleton {
    private static Singleton mInstance = null;
 
    private SearchData mSearchData;
 
    private Singleton(){
        mSearchData = new SearchData();
    }
 
    public static Singleton getInstance(){
        if(mInstance == null)
        {
            mInstance = new Singleton();
        }
        return mInstance;
    }
 
    public SearchData getSearchData(){
        return this.mSearchData;
    }
 
    public void setSearchData(SearchData value){
        mSearchData = value;
    }
}