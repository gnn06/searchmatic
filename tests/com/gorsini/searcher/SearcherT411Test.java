package com.gorsini.searcher;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class SearcherT411Test {

	@Test
	public void testSearchMovieOne() {
		SearcherT411 s = new SearcherT411();
		ArrayList<Movie> result = s.searchMovie(new Movie("banished.s01e06", null, 1, null));
		assertTrue(result.size() == 1);
	}

	@Test
	public void testSearchMovieMany() {
		SearcherT411 s = new SearcherT411();
		ArrayList<Movie> result = s.searchMovie(new Movie("banished s01", null, 1, null));
		assertTrue(result.size() == 6);
	}

	@Test
	public void testSearchMovieNone() {
		SearcherT411 s = new SearcherT411();
		ArrayList<Movie> result = s.searchMovie(new Movie("banished.s01e07", null, 1, null));
		assertNull(result);
	}
}
