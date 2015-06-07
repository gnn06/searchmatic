package com.example.searchmatic;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.searchmatic.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.gorsini.searcher.Movie;
import com.gorsini.searcher.SearchData;
import com.gorsini.searcher.Searcher;

public class MainActivity extends Activity implements
		OnConnectionFailedListener, ConnectionCallbacks {

	private class ListItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			// position commence à 0
			openExists(position);
		}
	}

	public GoogleApiClient mGoogleApiClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final ListView listview = (ListView) findViewById(R.id.listview);
		listview.setOnItemClickListener(new ListItemClickListener());
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillListview();
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addApi(Drive.API).addScope(Drive.SCOPE_FILE)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
		}
		mGoogleApiClient.connect();
	}

	@Override
	protected void onPause() {
		if (mGoogleApiClient != null) {
			mGoogleApiClient.disconnect();
		}
		super.onPause();
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.i(TAG, "connected");
		// TODO: Prévoir un chargement au lancement de l'appli
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Log.i(TAG, "onConnectionSuspended");
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(TAG, "onConnectionFailed" + result.toString());
		if (!result.hasResolution()) {
			// show the localized error dialog.
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}
		try {
			result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
		} catch (SendIntentException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult" + requestCode);
		super.onActivityResult(requestCode, resultCode, data);
		// TODO: Prévoir une sauvegarde à la volée.
		if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
			mGoogleApiClient.connect();
		} else if (requestCode == REQUEST_ADD_MOVIE) {
			if (resultCode == RESULT_OK) {
				findViewById(R.id.empty_list_movie).setVisibility(View.GONE);
				findViewById(R.id.listview).setVisibility(View.VISIBLE);
				fillListview();
			}
		} else if (requestCode == REQUEST_UPDATE_MOVIE) {
			if (resultCode == RESULT_OK) {
				fillListview();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_add) {
			openAdd();
			return true;
		} else if (id == R.id.action_refresh) {
			new HTTPSearchAllTask(this).execute(Singleton.getInstance()
					.getSearchData());
			return true;
		} else if (id == R.id.action_reset) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.dialog_reset_title)
					.setPositiveButton(R.string.dialog_OK,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									new DriveResetTask().execute((Void)null);
								}
							})
					.setNegativeButton(R.string.dialog_cancel, null);
			AlertDialog dialog = builder.create();
			dialog.show();
			return true;

		} else if (id == R.id.action_read) {
			new DriveReadTask().execute();
			return true;

		} else if (id == R.id.action_save) {
			new DriveWriteTask().execute();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class DriveResult {
		public DriveResult(String message) {
			this.message = message;
		}

		public DriveResult(SearchData sd) {
			this.sd = sd;
		}

		private String message = null;

		private SearchData sd = null;

		public String getMessage() {
			return this.message;
		}

		public SearchData getSearchData() {
			return this.sd;
		}

	}

	private class DriveReadTask extends AsyncTask<Void, Integer, DriveResult> {

		@Override
		protected DriveResult doInBackground(Void... paramVoid) {
			Query query = new Query.Builder()
					.addFilter(Filters.eq(SearchableField.TITLE, "search.json"))
					.addFilter(Filters.eq(SearchableField.TRASHED, false))
					.build();
			MetadataBufferResult result = Drive.DriveApi.query(
					mGoogleApiClient, query).await();

			if (!result.getStatus().isSuccess()) {
				return new DriveResult("Problem while retrieving results");
			}
			MetadataBuffer mdb = result.getMetadataBuffer();
			Log.i(TAG, "count" + mdb.getCount());
			if (mdb.getCount() > 1) {
				return new DriveResult("too much drive file");
			} else if (mdb.getCount() <= 0) {
				return new DriveResult("No drive file");
			}
			DriveId id1 = mdb.get(0).getDriveId();
			Log.i(TAG, id1.toString());

			DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, id1);
			DriveContentsResult driveContentsResult = file.open(
					mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();
			if (!driveContentsResult.getStatus().isSuccess()) {
				return new DriveResult("read error");
			}
			DriveContents driveContents = driveContentsResult
					.getDriveContents();
			Searcher s = new Searcher();
			SearchData psd = null;
			try {
				psd = s.streamGetWanted(new InputStreamReader(driveContents
						.getInputStream()));
			} catch (IOException e) {
				Log.e(TAG, "read error", e);
				return new DriveResult("IO exception");
			}
			return new DriveResult(psd);
		}

		@Override
		protected void onPreExecute() {
			findViewById(R.id.list_progressbar).setVisibility(View.VISIBLE);
			findViewById(R.id.listview).setVisibility(View.GONE);
			findViewById(R.id.empty_list_movie).setVisibility(View.GONE);
		}

		protected void onPostExecute(DriveResult result) {
			findViewById(R.id.list_progressbar).setVisibility(View.GONE);
			if (result.getMessage() != null) {
				findViewById(R.id.empty_list_movie).setVisibility(View.VISIBLE);
				showMessage(result.getMessage());
				Singleton.getInstance().setSearchData(new SearchData());
			} else {
				findViewById(R.id.listview).setVisibility(View.VISIBLE);
				Singleton.getInstance().setSearchData(result.getSearchData());
				fillListview();
			}
		}
	}

	private class DriveWriteTask extends AsyncTask<Void, Integer, DriveResult> {

		@Override
		protected DriveResult doInBackground(Void... paramVoid) {
			Query query = new Query.Builder()
					.addFilter(Filters.eq(SearchableField.TITLE, "search.json"))
					.addFilter(Filters.eq(SearchableField.TRASHED, false))
					.build();
			MetadataBufferResult resultQuery = Drive.DriveApi.query(
					mGoogleApiClient, query).await();
			if (!resultQuery.getStatus().isSuccess()) {
				return new DriveResult("Problem while retrieving results");
			}
			MetadataBuffer mdb = resultQuery.getMetadataBuffer();
			if (mdb.getCount() > 1) {
				return new DriveResult("too much drive file");
			} else if (mdb.getCount() >= 1) {
				DriveId id = mdb.get(0).getDriveId();
				DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, id);
				DriveContentsResult driveContentsResult = file.open(
						mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null)
						.await();
				if (!driveContentsResult.getStatus().isSuccess()) {
					return new DriveResult("read error");
				}
				DriveContents driveContents = driveContentsResult
						.getDriveContents();
				OutputStream outputStream = driveContents.getOutputStream();
				try {
					Searcher s = new Searcher();
					s.streamWrite(Singleton.getInstance().getSearchData(),
							outputStream);

				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
					return new DriveResult("IO exception");
				}
				com.google.android.gms.common.api.Status st = driveContents
						.commit(mGoogleApiClient, null).await();
				if (!st.isSuccess()) {
					return new DriveResult("commit error");
				}
			} else if (mdb.getCount() == 0) {
				DriveContentsResult contentResult = Drive.DriveApi
						.newDriveContents(mGoogleApiClient).await();
				if (!contentResult.getStatus().isSuccess()) {
					return new DriveResult("read error");
				}
				DriveContents driveContents = contentResult.getDriveContents();
				OutputStream outputStream = driveContents.getOutputStream();
				try {
					Searcher s = new Searcher();
					s.streamWrite(Singleton.getInstance().getSearchData(),
							outputStream);
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
					return new DriveResult("IO exception");
				}

				MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
						.setTitle("search.json").setMimeType("text/plain")
						.setStarred(true).build();

				// create a file on root folder
				DriveFileResult fileResult = Drive.DriveApi
						.getRootFolder(mGoogleApiClient)
						.createFile(mGoogleApiClient, changeSet, driveContents)
						.await();
				if (!fileResult.getStatus().isSuccess()) {
					return new DriveResult(
							"Error while trying to create the file");
				}
			}
			return new DriveResult("write done");
		}

		@Override
		protected void onPostExecute(DriveResult result) {
			super.onPostExecute(result);
			if (result != null) {
				showMessage(result.getMessage());
			}
		}
	}

	private class DriveResetTask extends AsyncTask<Void, Integer, DriveResult> {
		@Override
		protected DriveResult doInBackground(Void... paramVoid) {
			Query query = new Query.Builder()
					.addFilter(Filters.eq(SearchableField.TITLE, "search.json"))
					.addFilter(Filters.eq(SearchableField.TRASHED, false))
					.build();
			MetadataBufferResult resultQuery = Drive.DriveApi.query(
					mGoogleApiClient, query).await();
			if (!resultQuery.getStatus().isSuccess()) {
				return new DriveResult("Problem while retrieving results");
			}
			MetadataBuffer mdb = resultQuery.getMetadataBuffer();
			for (int i = 0; i < mdb.getCount(); i++) {
				DriveId id = mdb.get(i).getDriveId();
				DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, id);
				com.google.android.gms.common.api.Status st = file.trash(
						mGoogleApiClient).await();
				if (!st.isSuccess()) {
					return new DriveResult("delete error");
				}

			}

			return new DriveResult("reset done");
		}

		@Override
		protected void onPostExecute(DriveResult result) {
			super.onPostExecute(result);
			Singleton.getInstance().setSearchData(new SearchData());
			findViewById(R.id.listview).setVisibility(View.GONE);
			findViewById(R.id.empty_list_movie).setVisibility(View.VISIBLE);
			showMessage(result.getMessage());
		}
	}

	private void fillListview() {
		if (Singleton.getInstance().getSearchData() == null
				|| Singleton.getInstance().getSearchData().movies == null) {
			Log.i(TAG, "empty data");
			return;
		}
		Log.i(TAG, "number of movie to search : "
				+ Singleton.getInstance().getSearchData().movies.size());
		final ListView listview = (ListView) findViewById(R.id.listview);
		if (Singleton.getInstance().getSearchData().movies.isEmpty()) {
			listview.setEmptyView(findViewById(R.id.empty_list_movie));
			return;
		}
		String[] values = new String[Singleton.getInstance().getSearchData().movies
				.size()];
		for (int i = 0; i < Singleton.getInstance().getSearchData().movies
				.size(); i++) {
			values[i] = Singleton.getInstance().getSearchData().movies.get(i)
					.getTitle();
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, values);
		listview.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	private void showMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	private void openExists(int position) {
		Intent intent = new Intent(this, DetailActivity.class);
		intent.putExtra(MainActivity.MOVIE_ID, position);
		startActivityForResult(intent, REQUEST_UPDATE_MOVIE);
	}

	private void openAdd() {
		Intent intent = new Intent(this, DetailActivity.class);
		int id = -1;
		intent.putExtra(MainActivity.MOVIE_ID, id);
		startActivityForResult(intent, REQUEST_ADD_MOVIE);
	}

	public static final String TAG = "MyActivity";

	public final static String MOVIE_ID = "com.mycompany.searchmatic.MOVIEID";

	protected static final int REQUEST_CODE_RESOLUTION = 1;

	protected static final int REQUEST_ADD_MOVIE = 3;

	protected static final int REQUEST_UPDATE_MOVIE = 4;

}
