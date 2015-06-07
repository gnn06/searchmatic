/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gorsini.searcher;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author gorsini
 */
public class CanalplaySearcher {

    public void check() throws HTMLChangeException, IOException {
        String url = makeURL("intouchables");
        Document doc = Jsoup.connect(url).referrer("http://vod.canalplay.com/").get();
        Elements movies = doc.select("div.list_movie");
        String html = movies.html();
        String previousHTML = null;
        LOG.finest(html);
        File file = new File(CHECK_FILENAME);
        if (file.exists()) {
            previousHTML = FileUtils.readFileToString(new File(CHECK_FILENAME));
        } else {
            LOG.log(Level.INFO, "sauvegarde check");
            FileUtils.writeStringToFile(file, html);
        }
        if (previousHTML != null && !html.equals(previousHTML)) {
            // sauvegarde la nouvelle version pour pouvoir la comparer.
            FileUtils.writeStringToFile(new File(CHECK_FILENAME + ".new"), html);
            throw new HTMLChangeException();
        } else {
            LOG.log(Level.INFO, "no change detected into HTML response");
        }
    }

    /**
     *
     * @param movieToSearch
     * @return ArrayList<Movie> empty if no result
     */
    public ArrayList<Movie> searchMovie(Movie movieToSearch) {
        try {
            /*
             curl "http://vod.canalplay.com/pages/recherche/challengeexplorer.aspx?action=4&search=hercule" -H "Referer: http://vod.canalplay.com/"
             ramÃ¨ne que les films dispo. 
             */
            String titleToSearch = movieToSearch.getTitle();
            LOG.log(Level.FINER, "titre Ã  rechercher : {0}", titleToSearch);
            String url = makeURL(titleToSearch);
            Document doc = Jsoup.connect(url).referrer("http://vod.canalplay.com/").get();
            Elements movies = doc.select("div.list_movie");
            
            if (movies.isEmpty()) {
                LOG.log(Level.FINER, "no movie found with title {0}", titleToSearch);
                return null;
            } else {
                ArrayList<Movie> result = new ArrayList<Movie>();
                for (Element movie : movies) {
                	MovieSelector selector = new MovieSelector();
                	Movie movieFound = selector.selectMovie(movie, movieToSearch);
                    if (movieFound != null) {
                        LOG.log(Level.FINER, "film trouvÃ©:{0}", movieFound.toString());
                        result.add(movieFound);
                    }
                }
                return result;
            }
        } catch (Exception e) {
            System.out.println("problème HTTP");
            e.printStackTrace();
            return null;
        }
    }
    
    private String makeURL(String titleToSearch) {
        try {
            String searchTitre = URLEncoder.encode(titleToSearch, "UTF-8").replaceAll("\\+", "%20");
            LOG.log(Level.FINEST, "titre recherché : {0}", searchTitre);
            String result = CANALPLAY_URL + "search=" + searchTitre;
            return result;
        } catch (UnsupportedEncodingException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private static final String CHECK_FILENAME = "checkSearch.html";

    private static final String CANALPLAY_URL = "http://vod.canalplay.com/pages/recherche/challengeexplorer.aspx?action=4&";

    private static final String TAG = "MyActivity";

    private static final Logger LOG = Logger.getLogger(CanalplaySearcher.class.getName());
}
