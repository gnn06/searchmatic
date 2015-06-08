package com.gorsini.searcher;

import java.io.*;
import java.util.*;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class CanalplaySearcherTest {

    @Mocked     File file;
    @Mocked     FileUtils fileutils;

    @Mocked     Jsoup jsoup;
    @Injectable Element elemMovie1;
    @Injectable Element elemMovie2;
    @Mocked     MovieSelector selector;

    @Before
    public void setup() {
    }

    @Test
    public void testSearchMovieNone(@Mocked final Document document) {
        CanalplaySearcher instance = new CanalplaySearcher();
        final Elements elemMovie = new Elements();
        new Expectations() {
            {
                document.select(anyString);
                result = elemMovie;
            }
        };
        ArrayList<Movie> result = instance.searchMovie(new Movie("griffe", null, 0, null));
        assertEquals(null, result);
    }

    @Test
    public void testSearchMovieOne(@Mocked final Document document) {
        CanalplaySearcher instance = new CanalplaySearcher();
        final Elements elemsMovie = new Elements(elemMovie1);
        new Expectations() {
            {
                document.select(anyString);
                result = elemsMovie;
                //Movie movieFound = selectMovie(movie, movieToSearch)
                selector.selectMovie((Element)any, (Movie)any);
                result = new Movie("titre1","url1",0, null);
            }
        };
        String titleToSearch = "searchtitle";
        ArrayList<Movie> result = instance.searchMovie(new Movie(titleToSearch, null, 0, null));
        assertEquals(1, result.size());
        assertEquals("titre1", result.get(0).getTitle());
        assertEquals("url1", result.get(0).getUrl());
    }

    @Test
    public void testSearchMovieMany(@Mocked final Document document) {
        CanalplaySearcher instance = new CanalplaySearcher();
        final Elements elemsMovie = new Elements(elemMovie1, elemMovie2);

        new Expectations() {
            {
                document.select(anyString);
                result = elemsMovie;

                //Movie movieFound = selectMovie(movie, movieToSearch)
                selector.selectMovie((Element)any, (Movie)any);
                result = new Movie("titre1","url1",0, null);
                result = new Movie("titre2","url2",0, null);
            }
        };
        ArrayList<Movie> result = instance.searchMovie(new Movie("griffes", null, 0, null));
        assertEquals(2, result.size());
        assertEquals("titre1", result.get(0).getTitle());
        assertEquals("url1", result.get(0).getUrl());
        assertEquals("titre2", result.get(1).getTitle());
        assertEquals("url2", result.get(1).getUrl());
    }

    @Test
    public void testcheckNoPrevious(@Mocked final Elements elements) throws HTMLChangeException, IOException {
        new Expectations() {
            {
                file.exists();
                result = false;
                elements.html();
                result = "html";
            }
        };
        CanalplaySearcher instance = new CanalplaySearcher();
        instance.check();
        new Verifications() {
            {
                FileUtils.writeStringToFile((File) any, "html");
            }
        };
    }

    @Test
    public void testcheckWithPreviousEquals(@Mocked final Elements elements) throws HTMLChangeException, IOException {
        new Expectations() {
            {
                file.exists();
                result = true;
                elements.html();
                result = "html";
                FileUtils.readFileToString((File) any);
                result = "html";
                FileUtils.writeStringToFile((File) any, null);
                times = 0;
            }
        };
        CanalplaySearcher instance = new CanalplaySearcher();
        instance.check();
    }

    @Test(expected = HTMLChangeException.class)
    public void testcheckWithPreviousDifferent(@Mocked final Elements elements) throws HTMLChangeException, IOException {
        new Expectations() {
            {
                file.exists();
                result = true;
                elements.html();
                result = "html";
                FileUtils.readFileToString((File) any);
                result = "html2";
                FileUtils.writeStringToFile((File) any, "html");
            }
        };
        CanalplaySearcher instance = new CanalplaySearcher();
        instance.check();
    }
}
