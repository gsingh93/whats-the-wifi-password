package com.gulshansingh.whatsthewifipassword;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

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

	private boolean refresh() {
		boolean connected = NetworkInterface.updatePassword(this,
				new GetPasswordCompleteListener());
		if (connected) {
			Toast.makeText(this, "Refreshing", Toast.LENGTH_SHORT).show();
		}

		return connected;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		refresh();
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

			Resources res = getResources();
			String passwordTextHtml = String.format(
					res.getString(R.string.message_password_is), password);
			String lastSyncedText = String.format(
					res.getString(R.string.message_last_synced),
					dateFormatter.format(date), timeFormatter.format(date));

			passwordTextView.setText(Html.fromHtml(passwordTextHtml));
			lastSyncedTextView.setText(lastSyncedText);
		} else {
			passwordTextView.setText(R.string.message_no_password);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.menu_refresh:
			boolean success = refresh();
			if (!success) {
				Toast.makeText(this, R.string.error_message_refresh,
						Toast.LENGTH_LONG).show();
			}
			break;
		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}
}
