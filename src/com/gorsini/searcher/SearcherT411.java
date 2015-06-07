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
public class SearcherT411 {

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
            Document doc = Jsoup.connect(url).get();
            Elements movies = doc.select("table.results tbody tr");
            
            if (movies.isEmpty()) {
                LOG.log(Level.FINER, "no movie found with title {0}", titleToSearch);
                return null;
            } else {
                ArrayList<Movie> result = new ArrayList<Movie>();
                for (Element movie : movies) {
                	MovieSelectorT411 selector = new MovieSelectorT411();
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
//            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // http://www.t411.io/torrents/search/?name=banis%E9d+s01e06&description=&file=&user=&cat=&search=%40name+banis%E9d+s01e06+&submit=Recherche
    private String makeURL(String titleToSearch) {
        try {
            String searchTitre = URLEncoder.encode(titleToSearch, "UTF-8");
            LOG.log(Level.FINEST, "titre recherché : {0}", searchTitre);
            String result = CANALPLAY_URL.replaceAll("TOTO", searchTitre);
            return result;
        } catch (UnsupportedEncodingException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    
    	
    private static final String CHECK_FILENAME = "checkSearch.html";

    private static final String CANALPLAY_URL = "http://www.t411.io/torrents/search/?name=TOTO&description=&file=&user=&cat=210&subcat=433&search=%40name+TOTO+&submit=Recherche";
    	
    private static final String TAG = "MyActivity";

    private static final Logger LOG = Logger.getLogger(SearcherT411.class.getName());
    
}
