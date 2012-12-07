package com.gulshansingh.whatsthewifipassword;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public interface AsyncTaskCompleteListener<T> {
		public void onComplete(T result);
	}

	public class GetPasswordCompleteListener implements
			AsyncTaskCompleteListener<String> {
		public void onComplete(String result) {
			displayText();
		}
	}

	private void refresh(boolean messageOnError) {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info.isConnected()) {
			NetworkInterface.updatePassword(this,
					new GetPasswordCompleteListener());
			Toast.makeText(this, "Refreshing", Toast.LENGTH_SHORT);
		} else {
			if (messageOnError) {
				Toast.makeText(
						this,
						"Unable to retrieve password. Please make sure you have an internet connection",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		refresh(false);
		displayText();
	}

	private void displayText() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String password = preferences.getString("password", null);

		TextView passwordTextView = (TextView) findViewById(R.id.password);
		if (password != null) {
			TextView lastSyncedTextView = (TextView) findViewById(R.id.last_synced);
			long timestamp = preferences.getLong("timestamp", 0);
			Date date = new Date(timestamp);
			SimpleDateFormat dateFormatter = new SimpleDateFormat(
					"MMMMM d, yyyy", Locale.US);
			SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm aa",
					Locale.US);

			String passwordTextHtml = "The password is <font color='green'>"
					+ password + "</font>";
			String lastSyncedText = "Last Synced " + dateFormatter.format(date)
					+ " at " + timeFormatter.format(date);

			passwordTextView.setText(Html.fromHtml(passwordTextHtml));
			lastSyncedTextView.setText(lastSyncedText);
		} else {
			passwordTextView
					.setText("The password will be downloaded if you have a WiFi connection. This is the only time you will need a connection.");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.menu_refresh:
			refresh(true);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}
}
