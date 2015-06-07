package com.example.searchmatic;

import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.searchmatic.R;
import com.gorsini.searcher.Movie;
import com.gorsini.searcher.Searcher;

public class HTTPSearchTask extends AsyncTask<Movie, Integer, Movie> {

	private Activity mContext;
	
	public HTTPSearchTask(Activity context) {
		mContext = context;
	}

	@Override
	protected Movie doInBackground(Movie... movieWanted) {
		
		Searcher cs = new Searcher();
		ArrayList<Movie> result = cs.search(movieWanted[0]);
		movieWanted[0].setResults(result);
		return movieWanted[0];
	}
	
	@Override
	protected void onPreExecute() {
		mContext.findViewById(R.id.detail_progressbar).setVisibility(View.VISIBLE);
		mContext.findViewById(R.id.detail_list).setVisibility(View.GONE);
		mContext.findViewById(R.id.detail_empty).setVisibility(View.GONE);
	}

	protected void onPostExecute(Movie result) {
		final ListView listview = (ListView) mContext
				.findViewById(R.id.detail_list);
		
		mContext.findViewById(R.id.detail_progressbar).setVisibility(View.GONE);

		ArrayAdapter<String> a = (ArrayAdapter<String>)listview.getAdapter();
		
		a.clear();
		if (result.getResults() != null) {
			for (int i = 0; i < result.getResults().size(); i++) {
				a.add(result.getResults().get(i).getTitle());
				
			}
		}
		// Unnecessary to call notifyDataSetChanged
		// because setEmptyView will call it. 

		// wait to fill list to manage empty view to avoid
		// that when no search was launch, 'empty' was shown.
		View emptyView = mContext.findViewById(R.id.detail_empty);
		listview.setEmptyView(emptyView);
		
	}
}