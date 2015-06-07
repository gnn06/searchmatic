package com.gorsini.searcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.util.Log;

import com.example.searchmatic.MainActivity;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class Searcher {

	public static final String FILENAME = "movielist.json";
	private static final Logger LOG = Logger.getLogger(Main.class.getName());

	public ArrayList<Movie> search(Movie wantedMovie) {
		ArrayList<Movie> result = null;
		switch (wantedMovie.getSearchType()) {
		case 0: // canalplay
			CanalplaySearcher s = new CanalplaySearcher();
			result = s.searchMovie(wantedMovie);
			break;

		case 1: // t411
			SearcherT411 sT411 = new SearcherT411();
			result = sT411.searchMovie(wantedMovie);
			break;

		default:

			break;
		}
		return result;
	}

	public void search() {
		SearchData searchData = null;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			searchData = getWanted();
			if (searchData == null) {
				LOG.info("empty file");
				return;
			}
			LOG.info("number of movie to search : " + searchData.movies.size());

			CanalplaySearcher canalplaySearcher = new CanalplaySearcher();

			// check that no changes occured
			canalplaySearcher.check();

			ArrayList<Movie> test = canalplaySearcher.searchMovie(new Movie(
					"test", null, 0, null));

			int foundedCount = 0;
			for (Movie movie : searchData.movies) {
				// Si on a déjà des résultat pour cette recherche, on ne fait
				// rien
				if (movie.getResultDate() != null) {
					continue;
				}
				ArrayList<Movie> foundedMovies = canalplaySearcher
						.searchMovie(movie);
				movie.setResults(foundedMovies);
				foundedCount += foundedMovies == null ? 0 : foundedMovies
						.size();
			}
			LOG.log(Level.INFO, "number of founded movie : {0}", foundedCount);
			LOG.info("sauvegarde des résultats dans " + FILENAME);

			searchData.setLastSearchDate(new Date());

			write(searchData);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public SearchData getWanted() {
		SearchData searchData = null;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			LOG.info("lecture fichier " + FILENAME);
			FileReader reader = new FileReader(FILENAME);
			searchData = gson.fromJson(reader, SearchData.class);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return searchData;
	}

	public SearchData getWanted(Activity mContext) throws FileNotFoundException {
		SearchData searchData = null;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		LOG.info("lecture fichier " + FILENAME);
		FileReader reader = new FileReader(new File(mContext.getFilesDir(),
				FILENAME));
		searchData = gson.fromJson(reader, SearchData.class);
		return searchData;
	}
	
	ResultCallback<DriveContentsResult> contentsOpenedCallback =
	        new ResultCallback<DriveContentsResult>() {
	    @Override
	    public void onResult(DriveContentsResult result) {
	        if (!result.getStatus().isSuccess()) {
	            // display an error saying file can't be opened
	            return;
	        }
	        // DriveContents object contains pointers
	        // to the actual byte stream
	        DriveContents contents = result.getDriveContents();
	    }
	};
	
	public SearchData getWantedNew(Activity mContext) throws FileNotFoundException {
		LOG.info("lecture fichier " + FILENAME);
		
		SearchData searchData = null;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		FileReader reader = new FileReader(new File(mContext.getFilesDir(),
				FILENAME));
		searchData = gson.fromJson(reader, SearchData.class);
		
		return searchData;
	}

	public SearchData streamGetWanted(InputStreamReader fis) throws IOException {
		Log.i("myactivity", "lecture stream ");
		SearchData searchData = null;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonReader reader = new JsonReader(fis);
		reader.setLenient(true);
		searchData = gson.fromJson(reader, SearchData.class);
//		char buffer[] = new char[100];
//		StringBuffer sb = new StringBuffer();
//		while (fis.read(buffer) != -1) {
//			sb.append(buffer);
//		}
//		String json = sb.toString();
//		searchData = gson.fromJson(json, SearchData.class);
		return searchData;
	}

	public void streamWrite(SearchData searchData, OutputStream fos)
			throws IOException {
		Log.i("myactivity", "ecriture " + FILENAME);
		Gson gson = new GsonBuilder()/*.setPrettyPrinting()*/.create();
		String json = gson.toJson(searchData);
		fos.write(json.getBytes());
		fos.close();
	}

	public void write(SearchData searchData) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		FileWriter writer = new FileWriter(FILENAME);
		writer.write(gson.toJson(searchData));
		writer.close();
	}

	public void write(SearchData searchData, Activity mContext)
			throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		File f = new File(mContext.getFilesDir(),
				FILENAME);
		FileWriter writer = new FileWriter(f);
		Log.i(MainActivity.TAG, "write into : " + f.getAbsolutePath());
		writer.write(gson.toJson(searchData));
		writer.close();
	}
}
