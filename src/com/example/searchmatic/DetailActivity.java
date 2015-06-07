package com.example.searchmatic;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.searchmatic.R;
import com.gorsini.searcher.Movie;
import com.gorsini.searcher.SearchData;

public class DetailActivity extends Activity {

	// position of the current movie into searchData.movies
	private int movieID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_detail);

		Spinner spinner = (Spinner) findViewById(R.id.detail_search_type_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.search_type_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		final ListView listview = (ListView) this
				.findViewById(R.id.detail_list);
		ArrayAdapter<String> a = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		listview.setAdapter(a);
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Movie movie = Singleton.getInstance().getSearchData().movies.get(movieID);
				String url = movie.getResults().get(position).getUrl();
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		Intent intent = getIntent();
		movieID = intent.getIntExtra(MainActivity.MOVIE_ID, -1);
		if (movieID != -1) {
			fillDetail();
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_save) {
			saveMovie();
			return true;
		} else if (id == R.id.action_delete) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.dialog_delete_title)
					.setPositiveButton(R.string.dialog_OK,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									deleteMovie();
								}
							})
					.setNegativeButton(R.string.dialog_cancel, null);
			AlertDialog dialog = builder.create();
			dialog.show();
			
		}
		return super.onOptionsItemSelected(item);
	}

	public void HTTPsearchCommand(View view) {
		// requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// setProgressBarIndeterminateVisibility(true);
		EditText editText = (EditText) findViewById(R.id.detail_title);
		String title = editText.getText().toString();
		Spinner spinner = (Spinner) findViewById(R.id.detail_search_type_spinner);
		// getSelectedItem returns "canalplay"
		int searchType = spinner.getSelectedItemPosition();
		Movie movieWanted = new Movie(title, null/* url */, searchType, null/* exclude */);
		SearchData sd = new SearchData();
		sd.movies = new ArrayList<Movie>(1);
		sd.movies.add(movieWanted);
		new HTTPSearchTask(this).execute(movieWanted);
	}

	private void deleteMovie() {
		if (movieID == -1) {
			return;
		}
		Singleton.getInstance().getSearchData().movies.remove(movieID);
		Intent result = new Intent();
		setResult(Activity.RESULT_OK, result);
		finish();
	}

	private void saveMovie() {
		EditText editText = (EditText) findViewById(R.id.detail_title);
		String title = editText.getText().toString();
		Spinner spinner = (Spinner) findViewById(R.id.detail_search_type_spinner);
		int searchType = spinner.getSelectedItemPosition();
		Movie movieWanted = new Movie(title, null/* url */, searchType, null/* exclude */);
		if (Singleton.getInstance().getSearchData().movies == null) {
			Singleton.getInstance().getSearchData().movies = new ArrayList<Movie>();
		}
		if (movieID != -1) {
			Singleton.getInstance().getSearchData().movies.set(movieID, movieWanted);
		} else {
			Singleton.getInstance().getSearchData().movies.add(movieWanted);
		}
		Intent result = new Intent();
		setResult(Activity.RESULT_OK, result);
		finish();
	}
	
	private void fillDetail() {
		Movie m = Singleton.getInstance().getSearchData().movies.get(movieID);
		EditText editText = (EditText) findViewById(R.id.detail_title);
		String title = m.getTitle();
		editText.setText(title);
		Spinner spinner = (Spinner) findViewById(R.id.detail_search_type_spinner);
		int position = m.getSearchType();
		spinner.setSelection(position);
	}

	private static final String TAG = "MyActivity";

}

