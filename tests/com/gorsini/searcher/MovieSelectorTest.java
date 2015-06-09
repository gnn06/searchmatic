package com.gorsini.searcher;


import java.util.ArrayList;
import java.util.Arrays;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class MovieSelectorTest {

    @Test
    public void testSelectMovie() {
        MovieSelector selector = new MovieSelector();
        Document doc2 = Jsoup.parseBodyFragment("<div class=\"list_movie\" xmlns:date=\"urn:cplay:date\" xmlns:format=\"urn:cplay:format\"><div class=\"col1\"><a href=\"/cinema/intouchables,297,303,20901.aspx\"><img width=\"70\" height=\"96\" src=\"http://canalplay-publishing.canal-plus.com/Movies/20901/Pictures/" +
                "fnac_1.gif\" alt=\"Intouchables\" title=\"Intouchables\"></a></div><div class=\"col2\"><ul><li class=\"price\"><strong style=\"color:black\"><span style=\"color:#000\">A partir de </span><span style=\"color:#000\">2.99&nbsp;€</span></strong></li><li class=\"displayHD\"><a href=\"/films/haute-definition.aspx?opf=-1\"><img src=\"/images/Pictos/hd.gif\" alt=\"Haute définition\" title=\"Haute définition\" border=\"0\"></a></li><li><strong><a href=\"/cinema/intouchables,297,303,20901.aspx\">Intouchables</a></strong></li><li><strong>Comédie</strong></li><li>VF&nbsp;-&nbsp;1 h 47&nbsp;-&nbsp;2011</li><li><strong>De : </strong>Eric Toledano,&nbsp;Olivier Nakache<br></li><li><span><strong>Avec : </strong></span>François Cluzet,&nbsp;Anne Le Ny</li><li>La rencontre improbable, touchante et drôle entre un riche aristocrate tétraplégique et un jeune de banlieue engagé par hasard pour être son aide à domicile... </li></ul></div><div class=\"blank\"></div></div>",
                "http://vod.canalplay.com/");
        Element element = doc2.body();
        Movie movie = new Movie("intouchables", null, 0, null);
        Movie result = selector.selectMovie(element, movie);
        assertNotNull(result);
        assertEquals(result.getTitle(), "Intouchables");
        assertEquals(result.getUrl(), "http://vod.canalplay.com/cinema/intouchables,297,303,20901.aspx");
    }
    
    public void testSelectMovieExclude() {
        MovieSelector selector = new MovieSelector();
        Document doc2 = Jsoup.parseBodyFragment("<div class=\"list_movie\" xmlns:date=\"urn:cplay:date\" xmlns:format=\"urn:cplay:format\"><div class=\"col1\"><a href=\"/cinema/intouchables,297,303,20901.aspx\"><img width=\"70\" height=\"96\" src=\"http://canalplay-publishing.canal-plus.com/Movies/20901/Pictures/" +
                "fnac_1.gif\" alt=\"Intouchables\" title=\"Intouchables\"></a></div><div class=\"col2\"><ul><li class=\"price\"><strong style=\"color:black\"><span style=\"color:#000\">A partir de </span><span style=\"color:#000\">2.99&nbsp;€</span></strong></li><li class=\"displayHD\"><a href=\"/films/haute-definition.aspx?opf=-1\"><img src=\"/images/Pictos/hd.gif\" alt=\"Haute définition\" title=\"Haute définition\" border=\"0\"></a></li><li><strong><a href=\"/cinema/intouchables,297,303,20901.aspx\">Intouchables</a></strong></li><li><strong>Comédie</strong></li><li>VF&nbsp;-&nbsp;1 h 47&nbsp;-&nbsp;2011</li><li><strong>De : </strong>Eric Toledano,&nbsp;Olivier Nakache<br></li><li><span><strong>Avec : </strong></span>François Cluzet,&nbsp;Anne Le Ny</li><li>La rencontre improbable, touchante et drôle entre un riche aristocrate tétraplégique et un jeune de banlieue engagé par hasard pour être son aide à domicile... </li></ul></div><div class=\"blank\"></div></div>",
                "http://vod.canalplay.com/");
        Element element = doc2.body();
        Movie movie = new Movie("intouchables", null, 0, new ArrayList(Arrays.asList("intouchable")));
        Movie result = selector.selectMovie(element, movie);
        assertNull(result);
    }
    
    @Test
    public void testSelectMovieGratuit() {
        MovieSelector selector = new MovieSelector();
        Document doc2 = Jsoup.parseBodyFragment("<div class=\"list_movie\" xmlns:date=\"urn:cplay:date\" xmlns:format=\"urn:cplay:format\"><div class=\"col1\"><a href=\"/cinema/imitation-game-minutes-gratuites,297,306,45623.aspx\"><img width=\"70\" height=\"96\" src=\"http://canalplay-publishing.canal-plus.com/Movies/45623/Pictures/"+
						"fnac_1.gif\" alt=\"Imitation Game, minutes gratuites\" title=\"Imitation Game, minutes gratuites\"></a></div><div class=\"col2\"><ul><li class=\"price\"><strong style=\"color:black\"><span style=\"color:#000\">Gratuit</span></strong></li><li class=\"displayHD\"><a href=\"/films/haute-definition.aspx?opf=-1\"><img src=\"/images/Pictos/hd.gif\" alt=\"Haute d�finition\" title=\"Haute d�finition\" border=\"0\"></a></li><li><strong><a href=\"/cinema/imitation-game-minutes-gratuites,297,306,45623.aspx\">Imitation Game, minutes gratuites</a></strong></li><li><strong>Passion</strong></li><li>VF&nbsp;-&nbsp;5 min&nbsp;-&nbsp;2014</li><li><strong>De : </strong>Mortem Tyldum<br></li><li><span><strong>Avec : </strong></span>Benedict Cumberbatch,&nbsp;Keira Knightley</li><li>D�couvrez le secret derri�re les minutes gratuites d'Imitation Game.</li></ul></div><div class=\"blank\"></div></div>");
        Element element = doc2.body();
        Movie movie = new Movie("imitation", null, 0, null);
        Movie result = selector.selectMovie(element, movie);
        assertNull(result);
    }
}

