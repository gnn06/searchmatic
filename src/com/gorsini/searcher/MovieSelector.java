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
public class MovieSelector {

    public Movie selectMovie(Element movieElem, Movie movieToSearch) {
        LOG.finest(movieElem.html());
        Elements title = movieElem.select("div.col2 li strong a");
        String foundTitre = title.get(0).text();
        String movieURL = title.get(0).attr("abs:href");
        if (movieToSearch.getExcludedTitles() != null
                && movieToSearch.getExcludedTitles().contains(foundTitre)) {
            LOG.finer("excluded title.");
            return null;
        }
        // prix égale 'à partir du ', '2,99 €', 'gratuit'
        Elements priceElem = movieElem.select("li.price");
        String price = priceElem.get(0).text();
        boolean disponible = !price.equals("Indisponible actuellement") &&
        		             !price.equals("Gratuit");
        boolean HD = !movieElem.select("li.displayHD").isEmpty();
        if (!disponible) {
            return null;
        } else {
            Movie result = new Movie(foundTitre, movieURL, 0, null);
            return result;
        }
    }
    
    private static final Logger LOG = Logger.getLogger(MovieSelector.class.getName());
}
