package com.gorsini.searcher;


import java.util.ArrayList;
import java.util.Arrays;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class MovieSelector411Test {

    @Test
    public void testSelectMovie() {
        MovieSelectorT411 selector = new MovieSelectorT411();
        Document doc2 = Jsoup.parseBodyFragment("<table><tbody><tr><td valign=\"top\"> <a href=\"/torrents/search/?subcat=433\"> <i class=\"categories-icons category-spline-video-tv-series\"></i> </a> </td> <td> <a href=\"//www.t411.io/torrents/banished-s01e01-fastsub-vostfr-hdtv-xvid-addiction\" title=\"Banished.S01E01.FASTSUB.VOSTFR.HDTV.XviD-ADDiCTiON\">Banished.S01E01.FASTSUB.VOSTFR.HDTV.XviD-ADDiCTiON&nbsp;<span class=\"up\">(A)</span></a> <a href=\"#\" class=\"switcher alignright\"></a> <dl> <dt>Ajouté le:</dt> <dd>2015-03-22 07:42:34 (+00:00)</dd> <dt>Ajouté par:</dt> <dd><a href=\"/users/profile/guerrierdelanuit\" title=\"guerrierdelanuit\" class=\"profile\">guerrierdelanuit</a></dd> <dt>Status:</dt> <dd><strong class=\"up\">BON</strong> — Ce torrent est actif (<strong>103</strong> seeders et<strong>0</strong> leechers) et devrait &#x119;tre téléchargé rapidement</dd> </dl> </td> <td> <a href=\"/torrents/nfo/?id=5283429\" class=\"ajax nfo\"></a> </td> <td align=\"center\">2</td> <td align=\"center\">1 mois</td> <td align=\"center\">548.66 MB</td>  <td align=\"center\">593</td> <td align=\"center\" class=\"up\">103</td><td align=\"center\" class=\"down\">0</td></tr></tbody></table>",
                "http://http://www.t411.io//");
        Element element = doc2.body();
        Movie movie = new Movie("banished", null, 1, null);
        Movie result = selector.selectMovie(element, movie);
        assertNotNull(result);
        assertEquals(result.getTitle(), "Banished.S01E01.FASTSUB.VOSTFR.HDTV.XviD-ADDiCTiON (A)");
        assertEquals(result.getUrl(), "http://www.t411.io/torrents/banished-s01e01-fastsub-vostfr-hdtv-xvid-addiction");
    }
    
    public void testSelectMovieExclude() {
        MovieSelectorT411 selector = new MovieSelectorT411();
        Document doc2 = Jsoup.parseBodyFragment("<div class=\"list_movie\" xmlns:date=\"urn:cplay:date\" xmlns:format=\"urn:cplay:format\"><div class=\"col1\"><a href=\"/cinema/intouchables,297,303,20901.aspx\"><img width=\"70\" height=\"96\" src=\"http://canalplay-publishing.canal-plus.com/Movies/20901/Pictures/" +
                "fnac_1.gif\" alt=\"Intouchables\" title=\"Intouchables\"></a></div><div class=\"col2\"><ul><li class=\"price\"><strong style=\"color:black\"><span style=\"color:#000\">A partir de </span><span style=\"color:#000\">2.99&nbsp;â‚¬</span></strong></li><li class=\"displayHD\"><a href=\"/films/haute-definition.aspx?opf=-1\"><img src=\"/images/Pictos/hd.gif\" alt=\"Haute dÃ©finition\" title=\"Haute dÃ©finition\" border=\"0\"></a></li><li><strong><a href=\"/cinema/intouchables,297,303,20901.aspx\">Intouchables</a></strong></li><li><strong>ComÃ©die</strong></li><li>VF&nbsp;-&nbsp;1 h 47&nbsp;-&nbsp;2011</li><li><strong>De : </strong>Eric Toledano,&nbsp;Olivier Nakache<br></li><li><span><strong>Avec : </strong></span>FranÃ§ois Cluzet,&nbsp;Anne Le Ny</li><li>La rencontre improbable, touchante et drÃ´le entre un riche aristocrate tÃ©traplÃ©gique et un jeune de banlieue engagÃ© par hasard pour Ãªtre son aide Ã  domicile... </li></ul></div><div class=\"blank\"></div></div>",
                "http://vod.canalplay.com/");
        Element element = doc2.body();
        Movie movie = new Movie("intouchables", null, 1, new ArrayList(Arrays.asList("intouchable")));
        Movie result = selector.selectMovie(element, movie);
        assertNull(result);
    }
}

