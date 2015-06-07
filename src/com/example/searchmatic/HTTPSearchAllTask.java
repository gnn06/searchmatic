package com.example.searchmatic;

import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.searchmatic.R;
import com.gorsini.searcher.Movie;
import com.gorsini.searcher.SearchData;
import com.gorsini.searcher.Searcher;

public class HTTPSearchAllTask extends AsyncTask<SearchData, Integer, SearchData> {
	
	private Activity mContext;
	
	public HTTPSearchAllTask (Activity context){
         mContext = context;
    }

	@Override
	protected SearchData doInBackground(SearchData... sd) {
		if (sd[0].movies == null) {
			return null;
		}
		Searcher cs = new Searcher();
		for (int i = 0; i < sd[0].movies.size(); i++) {
			ArrayList<Movie> result = cs.search(sd[0].movies.get(i));
			sd[0].movies.get(i).setResults(result);
		}		
		return sd[0];
	}
	
	protected void onPostExecute(SearchData result) {
		if (result == null || result.movies == null)
			return;
					
		String[] values = new String[result.movies.size()];
		for (int i = 0; i < result.movies.size(); i++) {
			String label = result.movies.get(i).getResults().isEmpty() ? " not found" : " found";
			values[i] = result.movies.get(i).getTitle() + label;
		}
		final ListView listview = (ListView) mContext.findViewById(R.id.listview);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
				android.R.layout.simple_list_item_1, values);
		listview.setAdapter(adapter);
	}
}