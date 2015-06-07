/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gorsini.searcher;

import java.util.logging.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author gorsini
 */
public class MovieSelectorT411 {

    // :eq commence à 0
	public Movie selectMovie(Element movieElem, Movie movieToSearch) {
        LOG.finest(movieElem.html());
        Elements title = movieElem.select("td:eq(1) a");
        String foundTitre = title.get(0).text();
        String movieURL = title.get(0).attr("abs:href");
        if (movieToSearch.getExcludedTitles() != null
                && movieToSearch.getExcludedTitles().contains(foundTitre)) {
            LOG.finer("excluded title.");
            return null;
        }
        Movie result = new Movie(foundTitre, movieURL, 1, null);
        return result;
    }
    
    private static final Logger LOG = Logger.getLogger(MovieSelector.class.getName());
}
